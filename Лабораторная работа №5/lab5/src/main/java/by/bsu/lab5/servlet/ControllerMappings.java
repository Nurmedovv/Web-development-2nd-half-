package by.bsu.lab5.servlet;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.web.IWebRequest;

import by.bsu.lab5.contr.CarRepairController;
import by.bsu.lab5.contr.DriverListController;
import by.bsu.lab5.contr.ErrorController;
import by.bsu.lab5.contr.IController;
import by.bsu.lab5.contr.TripCreateController;
import by.bsu.lab5.contr.TripListController;
import by.bsu.lab5.contr.TripsByDriverController;

public final class ControllerMappings {

    private static final Map<String, IController> CONTROLLERS = new HashMap<>();

    static {
        CONTROLLERS.put("/welcome/trips/list", new TripListController());
        CONTROLLERS.put("/welcome/trips/by-driver", new TripsByDriverController());
        CONTROLLERS.put("/welcome/trips/create", new TripCreateController());
        CONTROLLERS.put("/welcome/cars/repair", new CarRepairController());
        CONTROLLERS.put("/welcome/drivers/list", new DriverListController());
        CONTROLLERS.put("/welcome/error", new ErrorController());
    }

    public static IController resolveControllerForRequest(final IWebRequest request) {
        final String path = request.getPathWithinApplication();
        return CONTROLLERS.get(path);
    }

    private ControllerMappings() {
    }
}
