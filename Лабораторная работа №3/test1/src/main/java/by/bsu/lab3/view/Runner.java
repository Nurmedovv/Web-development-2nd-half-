package by.bsu.lab3.view;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import by.bsu.lab3.connector.ConnectionPool;
import by.bsu.lab3.dao.DAOCar;
import by.bsu.lab3.dao.DAOBodyType;
import by.bsu.lab3.dao.DAOCustomer;
import by.bsu.lab3.dao.DAODriver;
import by.bsu.lab3.dao.DAORequest;
import by.bsu.lab3.dao.DAORoute;
import by.bsu.lab3.dao.DAOTrip;
import by.bsu.lab3.entity.BodyType;
import by.bsu.lab3.entity.Car;
import by.bsu.lab3.entity.Customer;
import by.bsu.lab3.entity.Driver;
import by.bsu.lab3.entity.Request;
import by.bsu.lab3.entity.Route;
import by.bsu.lab3.entity.Trip;
import by.bsu.lab3.exept.DAOException;
import by.bsu.lab3.exept.JDBCConnectionException;

public class Runner {
    private static final Logger LOGGER = Logger.getLogger(Runner.class.getName());

    private static final String MENU = """
            ------------------------------------------------------------
            1  - Список рейсов
            2  - Рейсы заданного водителя
            3  - Автомобили в ремонте
            4  - Назначить водителя на рейс
            5  - Назначить автомобиль на рейс
            6  - Отметить рейс выполненным
            7  - Поставить автомобиль на ремонт
            8  - Добавить водителя
            9  - Добавить автомобиль
            10 - Добавить заявку
            11 - Создать рейс
            12 - Удалить водителя
            13 - Удалить автомобиль
            14 - Удалить рейс
            15 - Добавить заказчика
            16 - Добавить маршрут
            17 - Добавить тип кузова
            0  - Выход
            ------------------------------------------------------------""";

    private final Scanner scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    private final DAODriver driverDao = new DAODriver();
    private final DAOCar carDao = new DAOCar();
    private final DAORequest requestDao = new DAORequest();
    private final DAOTrip tripDao = new DAOTrip();
    private final DAOCustomer customerDao = new DAOCustomer();
    private final DAORoute routeDao = new DAORoute();
    private final DAOBodyType bodyTypeDao = new DAOBodyType();

    public static void main(String[] args) {
        configureLogging();

        ConnectionPool pool = ConnectionPool.getInstance();
        try {
            pool.init();
        } catch (JDBCConnectionException e) {
            System.err.println("Не удалось подключиться к базе данных: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Connection pool initialization failed", e);
            return;
        }

        try {
            new Runner().run();
        } finally {
            try {
                pool.closePool();
            } catch (JDBCConnectionException e) {
                System.err.println("Не удалось закрыть пул соединений: " + e.getMessage());
                LOGGER.log(Level.WARNING, "Connection pool closing failed", e);
            }
        }
    }

    // INFO уходят в lab3.log, в консоль — только warning+
    private static void configureLogging() {
        Logger appLogger = Logger.getLogger("by.bsu.lab3");
        appLogger.setLevel(Level.INFO);
        try {
            FileHandler fileHandler = new FileHandler("lab3.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            appLogger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Лог-файл lab3.log не открыт: " + e.getMessage());
        }
        for (Handler handler : Logger.getLogger("").getHandlers()) {
            handler.setLevel(Level.WARNING);
        }
    }

    private void run() {
        System.out.println("Диспетчерская автобазы, вариант 8");
        while (true) {
            System.out.println(MENU);
            System.out.println("(0 — выход; при вводе данных: 0 или «отмена» — вернуться в меню)");
            System.out.print("Ваш выбор: ");
            String choice = readLine("");
            try {
                switch (choice) {
                    case "1" -> showAllTrips();
                    case "2" -> showTripsOfDriver();
                    case "3" -> showCarsInRepair();
                    case "4" -> assignDriverToTrip();
                    case "5" -> assignCarToTrip();
                    case "6" -> completeTrip();
                    case "7" -> sendCarToRepair();
                    case "8" -> addDriver();
                    case "9" -> addCar();
                    case "10" -> addRequest();
                    case "11" -> createTrip();
                    case "12" -> deleteDriver();
                    case "13" -> deleteCar();
                    case "14" -> deleteTrip();
                    case "15" -> addCustomer();
                    case "16" -> addRoute();
                    case "17" -> addBodyType();
                    case "0" -> {
                        System.out.println("Работа завершена.");
                        return;
                    }
                    default -> System.out.println("Неизвестный пункт меню: " + choice);
                }
            } catch (CancelledException e) {
                System.out.println("Действие отменено, возврат в меню.");
            } catch (DAOException e) {
                System.err.println("Ошибка: " + e.getMessage());
                LOGGER.log(Level.INFO, "Menu action failed", e);
            } catch (IllegalStateException e) {
                System.out.println();
                System.out.println("Ввод завершён, выход.");
                return;
            }
        }
    }

    // запросы
    private void showAllTrips() throws DAOException {
        printTrips(tripDao.findAll());
    }

    private void showTripsOfDriver() throws DAOException {
        printDrivers(driverDao.findAll());
        int driverId = readInt("Введите id водителя: ");
        printTrips(tripDao.findByDriver(driverId));
    }

    private void showCarsInRepair() throws DAOException {
        printCars(carDao.findInRepair());
    }

    private void assignDriverToTrip() throws DAOException {
        printTrips(tripDao.findAll());
        int tripId = readInt("Введите id рейса: ");
        printDrivers(driverDao.findAll());
        int driverId = readInt("Введите id водителя: ");
        tripDao.assignDriver(tripId, driverId);
        System.out.println("Водитель " + driverId + " назначен на рейс " + tripId + ".");
    }

    private void assignCarToTrip() throws DAOException {
        printTrips(tripDao.findAll());
        int tripId = readInt("Введите id рейса: ");
        printCars(carDao.findAll());
        int carId = readInt("Введите id автомобиля: ");
        tripDao.assignCar(tripId, carId);
        System.out.println("Автомобиль " + carId + " назначен на рейс " + tripId + ".");
    }

    private void completeTrip() throws DAOException {
        printTrips(tripDao.findAll());
        int tripId = readInt("Введите id рейса: ");
        System.out.println("1 - автомобиль исправен");
        System.out.println("2 - автомобиль требует ремонта");
        int condition = readInt("Состояние автомобиля после рейса: ");
        tripDao.complete(tripId, condition == 2 ? Car.STATUS_REPAIR : Car.STATUS_SERVICEABLE);
        System.out.println("Рейс " + tripId + " отмечен выполненным.");
    }

    private void sendCarToRepair() throws DAOException {
        printCars(carDao.findAll());
        int carId = readInt("Введите id автомобиля: ");
        carDao.updateStatus(carId, Car.STATUS_REPAIR);
        System.out.println("Автомобиль " + carId + " поставлен в ремонт.");
    }

    // добавление
    private void addDriver() throws DAOException {
        Driver driver = new Driver();
        driver.setFullName(readValue("ФИО водителя: "));
        driver.setLicenseCategory(readValue("Категория прав (B, C, ...): "));
        driver.setPhone(readValue("Телефон: "));
        int id = driverDao.insert(driver);
        System.out.println("Водитель добавлен, id = " + id + ".");
    }

    private void addCar() throws DAOException {
        Car car = new Car();
        car.setPlateNumber(readValue("Государственный номер: "));
        car.setModel(readValue("Модель: "));
        printBodyTypes(bodyTypeDao.findAll());
        car.setBodyTypeId(readInt("id типа кузова: "));
        car.setLoadKg(readInt("Грузоподъёмность, кг: "));
        car.setStatus(Car.STATUS_SERVICEABLE);
        int id = carDao.insert(car);
        System.out.println("Автомобиль добавлен, id = " + id + ".");
    }

    private void addRequest() throws DAOException {
        Request request = new Request();
        printCustomers(customerDao.findAll());
        request.setCustomerId(readInt("id заказчика: "));
        printRoutes(routeDao.findAll());
        request.setRouteId(readInt("id маршрута: "));
        printBodyTypes(bodyTypeDao.findAll());
        request.setRequiredBodyTypeId(readInt("id требуемого типа кузова: "));
        request.setRequiredLoadKg(readInt("Требуемая грузоподъёмность, кг: "));
        request.setDepartureDate(readDate("Дата выезда (ГГГГ-ММ-ДД): "));
        int id = requestDao.insert(request);
        System.out.println("Заявка добавлена, id = " + id + ".");
    }

    private void createTrip() throws DAOException {
        printRequests(requestDao.findAll());
        int requestId = readInt("Введите id заявки: ");
        printCars(carDao.findAll());
        Integer carId = readOptionalInt("id автомобиля (Enter - без автомобиля, 0 - отмена): ");
        LocalDate departureDate = readDate("Дата выезда (ГГГГ-ММ-ДД): ");
        int tripId = tripDao.create(requestId, carId, departureDate);
        System.out.println("Рейс создан, id = " + tripId + ".");
    }

    // удаление
    private void deleteDriver() throws DAOException {
        printDrivers(driverDao.findAll());
        int driverId = readInt("Введите id удаляемого водителя: ");
        driverDao.delete(driverId);
        System.out.println("Водитель " + driverId + " удалён.");
    }

    private void deleteCar() throws DAOException {
        printCars(carDao.findAll());
        int carId = readInt("Введите id удаляемого автомобиля: ");
        carDao.delete(carId);
        System.out.println("Автомобиль " + carId + " удалён.");
    }

    private void deleteTrip() throws DAOException {
        printTrips(tripDao.findAll());
        int tripId = readInt("Введите id удаляемого рейса: ");
        tripDao.delete(tripId);
        System.out.println("Рейс " + tripId + " удалён.");
    }

    // справочники
    private void addCustomer() throws DAOException {
        printCustomers(customerDao.findAll());
        Customer customer = new Customer();
        customer.setName(readValue("Название заказчика: "));
        int id = customerDao.insert(customer);
        System.out.println("Заказчик добавлен, id = " + id + ".");
    }

    private void addRoute() throws DAOException {
        printRoutes(routeDao.findAll());
        Route route = new Route();
        route.setCityFrom(readValue("Город отправления: "));
        route.setCityTo(readValue("Город назначения: "));
        int id = routeDao.insert(route);
        System.out.println("Маршрут добавлен, id = " + id + ".");
    }

    private void addBodyType() throws DAOException {
        printBodyTypes(bodyTypeDao.findAll());
        BodyType bodyType = new BodyType();
        bodyType.setName(readValue("Название типа кузова (например Седан): "));
        int id = bodyTypeDao.insert(bodyType);
        System.out.println("Тип кузова добавлен, id = " + id + ".");
    }

    // вывод
    private static void printTrips(List<Trip> trips) {
        if (trips.isEmpty()) {
            System.out.println("Рейсы не найдены.");
            return;
        }
        System.out.printf("%-4s %-8s %-26s %-11s %-24s %-11s %-11s %-12s%n",
                "ID", "Заявка", "Маршрут", "Авто", "Водитель", "Выезд", "Выполнен", "Статус");
        for (Trip trip : trips) {
            String route = trip.getRouteFrom() == null ? "-"
                    : trip.getRouteFrom() + " -> " + trip.getRouteTo();
            String car = trip.getCarPlate() == null ? "-" : trip.getCarPlate();
            String driver = trip.getDriverName() == null ? "не назначен" : trip.getDriverName();
            String completion = trip.getCompletionDate() == null ? "-" : trip.getCompletionDate().toString();
            System.out.printf("%-4d %-8d %-26s %-11s %-24s %-11s %-11s %-12s%n",
                    trip.getId(), trip.getRequestId(), route, car, driver,
                    trip.getDepartureDate(), completion, tripStatus(trip.getStatus()));
        }
        System.out.println("Всего рейсов: " + trips.size());
    }

    private static void printDrivers(List<Driver> drivers) {
        if (drivers.isEmpty()) {
            System.out.println("Водители не найдены.");
            return;
        }
        System.out.printf("%-4s %-30s %-12s %-15s%n", "ID", "ФИО", "Категория", "Телефон");
        for (Driver driver : drivers) {
            System.out.printf("%-4d %-30s %-12s %-15s%n",
                    driver.getId(), driver.getFullName(), driver.getLicenseCategory(), driver.getPhone());
        }
    }

    private static void printCars(List<Car> cars) {
        if (cars.isEmpty()) {
            System.out.println("Автомобили не найдены.");
            return;
        }
        System.out.printf("%-4s %-13s %-16s %-16s %-12s %-10s%n",
                "ID", "Гос. номер", "Модель", "Тип кузова", "Груз., кг", "Состояние");
        for (Car car : cars) {
            System.out.printf("%-4d %-13s %-16s %-16s %-12d %-10s%n",
                    car.getId(), car.getPlateNumber(), car.getModel(), car.getBodyTypeName(),
                    car.getLoadKg(), carStatus(car.getStatus()));
        }
    }

    private static void printRequests(List<Request> requests) {
        if (requests.isEmpty()) {
            System.out.println("Заявки не найдены.");
            return;
        }
        System.out.printf("%-4s %-20s %-26s %-16s %-12s %-11s%n",
                "ID", "Заказчик", "Маршрут", "Тип кузова", "Груз., кг", "Выезд");
        for (Request request : requests) {
            System.out.printf("%-4d %-20s %-26s %-16s %-12d %-11s%n",
                    request.getId(), request.getCustomerName(),
                    request.getRouteFrom() + " -> " + request.getRouteTo(),
                    request.getRequiredBodyTypeName(), request.getRequiredLoadKg(),
                    request.getDepartureDate());
        }
    }

    private static void printCustomers(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("Заказчики не найдены.");
            return;
        }
        System.out.printf("%-4s %-30s%n", "ID", "Заказчик");
        for (Customer customer : customers) {
            System.out.printf("%-4d %-30s%n", customer.getId(), customer.getName());
        }
    }

    private static void printRoutes(List<Route> routes) {
        if (routes.isEmpty()) {
            System.out.println("Маршруты не найдены.");
            return;
        }
        System.out.printf("%-4s %-20s %-20s%n", "ID", "Откуда", "Куда");
        for (Route route : routes) {
            System.out.printf("%-4d %-20s %-20s%n",
                    route.getId(), route.getCityFrom(), route.getCityTo());
        }
    }

    private static void printBodyTypes(List<BodyType> bodyTypes) {
        if (bodyTypes.isEmpty()) {
            System.out.println("Типы кузова не найдены.");
            return;
        }
        System.out.printf("%-4s %-20s%n", "ID", "Тип кузова");
        for (BodyType bodyType : bodyTypes) {
            System.out.printf("%-4d %-20s%n", bodyType.getId(), bodyType.getName());
        }
    }

    private static String tripStatus(String status) {
        return Trip.STATUS_COMPLETED.equals(status) ? "выполнен" : "запланирован";
    }

    private static String carStatus(String status) {
        return Car.STATUS_REPAIR.equals(status) ? "в ремонте" : "исправен";
    }

    private static class CancelledException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    // ввод
    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            throw new IllegalStateException("Input is closed");
        }
        return scanner.nextLine().trim();
    }

    // как readLine, но поддерживает отмену (0 / отмена / назад)
    private String readValue(String prompt) {
        String line = readLine(prompt);
        String normalized = line.toLowerCase();
        if (normalized.equals("0") || normalized.equals("-")
                || normalized.equals("отмена") || normalized.equals("назад")) {
            throw new CancelledException();
        }
        return line;
    }

    private int readInt(String prompt) {
        while (true) {
            String line = readValue(prompt);
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Ожидалось целое число, повторите ввод.");
            }
        }
    }

    private Integer readOptionalInt(String prompt) {
        while (true) {
            String line = readValue(prompt);
            if (line.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Ожидалось целое число или пустая строка, повторите ввод.");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String line = readValue(prompt);
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("Ожидалась дата в формате ГГГГ-ММ-ДД, повторите ввод.");
            }
        }
    }
}
