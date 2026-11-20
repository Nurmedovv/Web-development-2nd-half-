package by.bsu.lab5.dao;

import by.bsu.lab5.entity.*;
import by.bsu.lab5.exept.DAOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class DAOTrip {

    private static final Logger log = LoggerFactory.getLogger(DAOTrip.class);

    private final DAODriver driverDao = new DAODriver();
    private final DAOCar carDao = new DAOCar();
    private final DAORequest requestDao = new DAORequest();


    public List<Trip> findAll() throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
            Root<Trip> root = cq.from(Trip.class);
            root.fetch(Trip_.request).fetch(Request_.route);
            root.fetch(Trip_.driver, jakarta.persistence.criteria.JoinType.LEFT);
            root.fetch(Trip_.car, jakarta.persistence.criteria.JoinType.LEFT);
            cq.select(root).orderBy(cb.asc(root.get(Trip_.id)));
            List<Trip> list = em.createQuery(cq).getResultList();
            log.debug("Loaded {} trips", list.size());
            return list;
        } catch (Exception e) {
            log.error("Can't read trips", e);
            throw new DAOException("Can't read trips: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Trip> findByDriver(int driverId) throws DAOException {
        Driver driver = driverDao.findById(driverId);
        if (driver == null) {
            throw new DAOException("Driver with id = " + driverId + " is not found");
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
            Root<Trip> root = cq.from(Trip.class);
            root.fetch(Trip_.request).fetch(Request_.route);
            root.fetch(Trip_.driver, jakarta.persistence.criteria.JoinType.LEFT);
            root.fetch(Trip_.car, jakarta.persistence.criteria.JoinType.LEFT);
            cq.select(root)
              .where(cb.equal(root.get(Trip_.driver).get(Driver_.id), driverId))
              .orderBy(cb.asc(root.get(Trip_.id)));
            List<Trip> list = em.createQuery(cq).getResultList();
            log.info("Trips of driver {}: {}", driver.getFullName(), list.size());
            return list;
        } catch (Exception e) {
            log.error("Can't read trips of driver with id = {}", driverId, e);
            throw new DAOException("Can't read trips of driver with id = " + driverId
                    + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Trip findById(int id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
            Root<Trip> root = cq.from(Trip.class);
            root.fetch(Trip_.request).fetch(Request_.route);
            root.fetch(Trip_.driver, jakarta.persistence.criteria.JoinType.LEFT);
            root.fetch(Trip_.car, jakarta.persistence.criteria.JoinType.LEFT);
            cq.select(root).where(cb.equal(root.get(Trip_.id), id));
            List<Trip> list = em.createQuery(cq).getResultList();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            log.error("Can't read trip with id = {}", id, e);
            throw new DAOException("Can't read trip with id = " + id + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    public int create(int tripId, int requestId, Integer carId, LocalDate departureDate) throws DAOException {
        if (tripId <= 0) {
            throw new DAOException("ID рейса не может быть " + tripId);
        }

        Trip existing = findById(tripId);
        if (existing != null) {
            throw new DAOException("Рейс с id = " + tripId + " уже существует");
        }

        Request request = requestDao.findById(requestId);
        if (request == null) {
            throw new DAOException("Request with id = " + requestId + " is not found");
        }
        if (carId != null) {
            Car car = carDao.findById(carId);
            checkCarFitsRequest(request, car, carId);
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Trip trip = new Trip();
            trip.setId(tripId);
            trip.setRequest(em.find(Request.class, requestId));
            if (carId != null) {
                trip.setCar(em.find(Car.class, carId));
            }
            trip.setStatus(Trip.STATUS_PLANNED);
            trip.setDepartureDate(departureDate);
            em.persist(trip);
            em.getTransaction().commit();
            log.info("Trip created: id = {}, request = {}, car = {}", trip.getId(), requestId, carId);
            return trip.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't create trip for request with id = {}", requestId, e);
            throw new DAOException("Can't create trip for request with id = " + requestId
                    + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void assignDriver(int tripId, int driverId) throws DAOException {
        Trip trip = requireTrip(tripId);
        requireNotCompleted(trip);

        Driver driver = driverDao.findById(driverId);
        if (driver == null) {
            throw new DAOException("Driver with id = " + driverId + " is not found");
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Trip managed = em.find(Trip.class, tripId);
            managed.setDriver(em.find(Driver.class, driverId));
            em.getTransaction().commit();
            log.info("Driver {} assigned to trip {}", driver.getFullName(), tripId);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't assign driver to trip with id = {}", tripId, e);
            throw new DAOException("Can't assign driver to trip with id = " + tripId
                    + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void assignCar(int tripId, int carId) throws DAOException {
        Trip trip = requireTrip(tripId);
        requireNotCompleted(trip);

        Request request = requestDao.findById(trip.getRequest().getId());
        if (request == null) {
            throw new DAOException("Request with id = " + trip.getRequest().getId() + " is not found");
        }
        Car car = carDao.findById(carId);
        checkCarFitsRequest(request, car, carId);

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Trip managed = em.find(Trip.class, tripId);
            managed.setCar(em.find(Car.class, carId));
            em.getTransaction().commit();
            log.info("Car {} assigned to trip {}", car.getPlateNumber(), tripId);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't assign car to trip with id = {}", tripId, e);
            throw new DAOException("Can't assign car to trip with id = " + tripId
                    + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void complete(int tripId, String carCondition) throws DAOException {
        Trip trip = requireTrip(tripId);
        requireNotCompleted(trip);
        if (trip.getDriver() == null) {
            throw new DAOException("Driver is not assigned to trip with id = " + tripId);
        }
        if (trip.getCar() == null) {
            throw new DAOException("Car is not assigned to trip with id = " + tripId);
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Trip managed = em.find(Trip.class, tripId);
            managed.setStatus(Trip.STATUS_COMPLETED);
            managed.setCompletionDate(LocalDate.now());
            managed.setCarConditionAfter(carCondition);
            Car car = em.find(Car.class, trip.getCar().getId());
            car.setStatus(carCondition);
            tx.commit();
            log.info("Trip {} completed, car {} condition -> {}", tripId, trip.getCar().getId(), carCondition);
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Can't complete trip with id = {}", tripId, e);
            throw new DAOException("Can't complete trip with id = " + tripId + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void delete(int tripId) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, tripId);
            if (trip == null) {
                em.getTransaction().rollback();
                throw new DAOException("Trip with id = " + tripId + " is not found");
            }
            em.remove(trip);
            em.getTransaction().commit();
            log.info("Trip {} deleted", tripId);
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't delete trip with id = {}", tripId, e);
            throw new DAOException("Can't delete trip with id = " + tripId + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    private Trip requireTrip(int tripId) throws DAOException {
        Trip trip = findById(tripId);
        if (trip == null) {
            throw new DAOException("Trip with id = " + tripId + " is not found");
        }
        return trip;
    }

    private void requireNotCompleted(Trip trip) throws DAOException {
        if (Trip.STATUS_COMPLETED.equals(trip.getStatus())) {
            throw new DAOException("Trip with id = " + trip.getId() + " is already completed");
        }
    }

    private void checkCarFitsRequest(Request request, Car car, int carId) throws DAOException {
        if (car == null) {
            throw new DAOException("Car with id = " + carId + " is not found");
        }
        if (!car.isServiceable()) {
            throw new DAOException("Car " + car.getPlateNumber()
                    + " is in repair and can't be assigned to a trip");
        }
        if (car.getBodyType().getId() != request.getRequiredBodyType().getId()) {
            throw new DAOException("Car body type '" + car.getBodyType().getName()
                    + "' does not match the request (required '"
                    + request.getRequiredBodyType().getName() + "')");
        }
        if (car.getLoadKg() < request.getRequiredLoadKg()) {
            throw new DAOException("Car load capacity " + car.getLoadKg()
                    + " kg is less than required " + request.getRequiredLoadKg() + " kg");
        }
    }
}
