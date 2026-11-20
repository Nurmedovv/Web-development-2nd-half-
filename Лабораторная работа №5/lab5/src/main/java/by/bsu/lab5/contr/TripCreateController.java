package by.bsu.lab5.contr;

import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;

import by.bsu.lab5.dao.DAOTrip;
import by.bsu.lab5.exept.DAOException;

public class TripCreateController extends BaseController {

    private final DAOTrip tripDao = new DAOTrip();

    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        WebContext ctx = createContext(webExchange);
        IWebRequest request = webExchange.getRequest();
        String tripIdParam = request.getParameterValue("tripId");
        String requestIdParam = request.getParameterValue("requestId");
        String carIdParam = request.getParameterValue("carId");
        String departureDateParam = request.getParameterValue("departureDate");

        if (tripIdParam != null) {
            ctx.setVariable("tripId", tripIdParam);
            ctx.setVariable("requestId", requestIdParam);
            ctx.setVariable("carId", carIdParam);
            ctx.setVariable("departureDate", departureDateParam);
            if (requestIdParam == null || requestIdParam.isBlank()
                    || departureDateParam == null || departureDateParam.isBlank()) {
                ctx.setVariable("createError", "Некорректно заполненная форма: проверьте ID и дату");
            } else {
                try {
                    int tripId = Integer.parseInt(tripIdParam.trim());
                    int requestId = Integer.parseInt(requestIdParam.trim());
                    Integer carId = (carIdParam == null || carIdParam.isBlank())
                            ? null : Integer.parseInt(carIdParam.trim());
                    LocalDate departureDate = LocalDate.parse(departureDateParam.trim());
                    tripDao.create(tripId, requestId, carId, departureDate);
                    ctx.setVariable("createSuccess", "Рейс № " + tripId + " успешно создан");
                    ctx.removeVariable("tripId");
                    ctx.removeVariable("requestId");
                    ctx.removeVariable("carId");
                    ctx.removeVariable("departureDate");
                } catch (NumberFormatException | DateTimeParseException e) {
                    ctx.setVariable("createError", "Некорректно заполненная форма: проверьте ID и дату");
                } catch (DAOException e) {
                    if (e.getCause() == null) {
                        ctx.setVariable("createError", e.getMessage());
                    } else {
                        throw e;
                    }
                }
            }
        }

        templateEngine.process("trips/create", ctx, writer);
    }
}
