package by.bsu.lab5.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import by.bsu.lab5.contr.IController;
import by.bsu.lab5.session.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    private static final DateTimeFormatter COOKIE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final int COOKIE_MAX_AGE = 30 * 24 * 60 * 60;

    private JakartaServletWebApplication application;
    private ITemplateEngine templateEngine;

    @Override
    public void init() {
        this.application = JakartaServletWebApplication.buildApplication(getServletContext());
        this.templateEngine = buildTemplateEngine(this.application);
        log.info("Application started");
        System.out.println(startUrl());
    }

    private String startUrl() {
        String port = getInitParameter("httpPort");
        if (port == null || port.isBlank()) {
            port = "8080";
        }
        String contextPath = getServletContext().getContextPath();
        String suffix = (contextPath == null || contextPath.isEmpty()) ? "/" : contextPath + "/";
        return "http://localhost:" + port + suffix;
    }

    private ITemplateEngine buildTemplateEngine(final IWebApplication application) {
        final WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(application);
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setCacheTTLMs(3600000L);
        resolver.setCacheable(true);
        final TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    private void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = normalizePath(request);
        log.info("{} {}", request.getMethod(), path);

        if (isStatic(path)) {
            if (path.endsWith(".html")) {
                updateSessionAndCookies(request, response);
            }
            serveStatic(path, response);
            return;
        }

        updateSessionAndCookies(request, response);

        try {
            final IServletWebExchange webExchange = this.application.buildExchange(request, response);
            final IWebRequest webRequest = webExchange.getRequest();
            IController controller = ControllerMappings.resolveControllerForRequest(webRequest);
            if (controller == null) {
                fail(request, response, "Страница не найдена");
                return;
            }
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            final Writer writer = response.getWriter();
            controller.process(webExchange, templateEngine, writer);
        } catch (Exception e) {
            log.error("Error handling request {}", path, e);
            fail(request, response, isDbFailure(e)
                    ? "Не удалось подключиться к базе данных. Попробуйте позже."
                    : "Внутренняя ошибка сервера. Попробуйте позже.");
        }
    }

    private String normalizePath(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (path.isEmpty() || "/".equals(path)) {
            return "/index.html";
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private boolean isStatic(String path) {
        return path.contains(".");
    }

    private void serveStatic(String path, HttpServletResponse response) throws IOException {
        try (InputStream in = getServletContext().getResourceAsStream(path)) {
            if (in == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String mimeType = getServletContext().getMimeType(path);
            if (path.endsWith(".html")) {
                response.setContentType("text/html;charset=UTF-8");
            } else if (mimeType != null) {
                response.setContentType(mimeType);
            }
            OutputStream out = response.getOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
    }

    private void updateSessionAndCookies(HttpServletRequest request, HttpServletResponse response) {
        LocalDateTime now = LocalDateTime.now();
        HttpSession session = request.getSession(true);
        UserSession userSession = (UserSession) session.getAttribute("userSession");
        if (userSession == null) {
            userSession = new UserSession();
            int visits = 1;
            String previousVisit = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("visits".equals(cookie.getName())) {
                        try {
                            visits = Integer.parseInt(cookie.getValue()) + 1;
                        } catch (NumberFormatException ignored) {
                            visits = 1;
                        }
                    } else if ("lastVisit".equals(cookie.getName())) {
                        previousVisit = cookie.getValue();
                    }
                }
            }
            userSession.setVisitCount(visits);
            userSession.setPreviousVisit(previousVisit);
            userSession.setLastVisit(now);
            session.setAttribute("userSession", userSession);

            String path = request.getContextPath().isEmpty() ? "/" : request.getContextPath();
            Cookie visitsCookie = new Cookie("visits", String.valueOf(visits));
            visitsCookie.setMaxAge(COOKIE_MAX_AGE);
            visitsCookie.setPath(path);
            Cookie lastVisitCookie = new Cookie("lastVisit", now.format(COOKIE_TIME));
            lastVisitCookie.setMaxAge(COOKIE_MAX_AGE);
            lastVisitCookie.setPath(path);
            response.addCookie(visitsCookie);
            response.addCookie(lastVisitCookie);
            log.info("New user session {}, visit #{}", userSession.getId(), visits);
        }

        request.setAttribute("visits", userSession.getVisitCount());
        String previous = userSession.getPreviousVisit();
        request.setAttribute("lastVisit", previous == null ? "—" : previous.replace('T', ' '));
        request.setAttribute("sessionId", session.getId());
    }

    private void fail(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        if (response.isCommitted()) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        request.getSession(true).setAttribute("errorMessage", message);
        response.sendRedirect(request.getContextPath() + "/welcome/error");
    }

    private boolean isDbFailure(Throwable e) {
        Throwable current = e;
        while (current != null) {
            String name = current.getClass().getName();
            if (current instanceof java.sql.SQLException
                    || name.contains("Persistence")
                    || name.contains("Database")
                    || name.contains("Communication")) {
                return true;
            }
            current = current.getCause() == current ? null : current.getCause();
        }
        return false;
    }
}
