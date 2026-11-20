package by.bsu.lab5.contr;

import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

public class ErrorController extends BaseController {

    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        WebContext ctx = createContext(webExchange);
        if (ctx.getVariable("errorMessage") == null) {
            ctx.setVariable("errorMessage", "Произошла непредвиденная ошибка. Попробуйте позже.");
        }
        templateEngine.process("error", ctx, writer);
    }
}
