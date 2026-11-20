package by.bsu.lab5.contr;

import java.io.Writer;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;

import by.bsu.lab5.dao.DAOTrip;
import by.bsu.lab5.entity.Trip;
import by.bsu.lab5.exept.DAOException;

public class TripsByDriverController extends BaseController {

    private final DAOTrip tripDao = new DAOTrip();

    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        WebContext ctx = createContext(webExchange);
        IWebRequest request = webExchange.getRequest();
        String driverIdParam = request.getParameterValue("driverId");
        List<Trip> trips = Collections.emptyList();
        boolean searched = false;

        if (driverIdParam != null && !driverIdParam.isBlank()) {
            searched = true;
            try {
                int driverId = Integer.parseInt(driverIdParam.trim());
                ctx.setVariable("driverId", driverId);
                trips = tripDao.findByDriver(driverId);
            } catch (NumberFormatException e) {
                ctx.setVariable("searchError", "ID водителя должен быть целым числом");
            } catch (DAOException e) {
                if (e.getCause() == null) {
                    ctx.setVariable("searchError", e.getMessage());
                } else {
                    throw e;
                }
            }
        }

        ctx.setVariable("trips", trips);
        ctx.setVariable("searched", searched);
        templateEngine.process("trips/by-driver", ctx, writer);
    }
}
