package by.bsu.lab5.contr;

import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.web.IWebExchange;

public interface IController {

    void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
            throws Exception;

}
