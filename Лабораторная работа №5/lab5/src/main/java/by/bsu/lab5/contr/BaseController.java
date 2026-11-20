package by.bsu.lab5.contr;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.IServletWebExchange;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public abstract class BaseController implements IController {

    protected WebContext createContext(IWebExchange webExchange) {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        HttpServletRequest request = (HttpServletRequest) ((IServletWebExchange) webExchange).getNativeRequestObject();
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userSession = session.getAttribute("userSession");
            if (userSession != null) {
                ctx.setVariable("userSession", userSession);
            }
            Object errorMessage = session.getAttribute("errorMessage");
            if (errorMessage != null) {
                ctx.setVariable("errorMessage", errorMessage);
                session.removeAttribute("errorMessage");
            }
        }
        ctx.setVariable("visits", request.getAttribute("visits"));
        ctx.setVariable("lastVisit", request.getAttribute("lastVisit"));
        ctx.setVariable("contextPath", request.getContextPath());
        return ctx;
    }
}
