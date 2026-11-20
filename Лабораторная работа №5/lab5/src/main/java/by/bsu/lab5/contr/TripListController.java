package by.bsu.lab5.contr;

import java.io.Writer;
import java.util.List;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import by.bsu.lab5.dao.DAOTrip;
import by.bsu.lab5.entity.Trip;

public class TripListController extends BaseController {

    private final DAOTrip tripDao = new DAOTrip();

    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        List<Trip> trips = tripDao.findAll();
        WebContext ctx = createContext(webExchange);
        ctx.setVariable("trips", trips);
        templateEngine.process("trips/list", ctx, writer);
    }
}
