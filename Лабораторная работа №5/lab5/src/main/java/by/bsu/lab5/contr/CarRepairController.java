package by.bsu.lab5.contr;

import java.io.Writer;
import java.util.List;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import by.bsu.lab5.dao.DAOCar;
import by.bsu.lab5.entity.Car;

public class CarRepairController extends BaseController {

    private final DAOCar carDao = new DAOCar();

    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        List<Car> cars = carDao.findInRepair();
        WebContext ctx = createContext(webExchange);
        ctx.setVariable("cars", cars);
        templateEngine.process("cars/repair", ctx, writer);
    }
}
