package by.bsu.lab5.contr;

import java.io.Writer;
import java.util.List;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import by.bsu.lab5.dao.DAODriver;
import by.bsu.lab5.entity.Driver;

public class DriverListController extends BaseController {

    private final DAODriver driverDao = new DAODriver();

    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        List<Driver> drivers = driverDao.findAll();
        WebContext ctx = createContext(webExchange);
        ctx.setVariable("drivers", drivers);
        templateEngine.process("drivers/list", ctx, writer);
    }
}
