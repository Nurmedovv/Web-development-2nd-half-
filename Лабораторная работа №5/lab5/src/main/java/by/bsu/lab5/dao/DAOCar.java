package by.bsu.lab5.dao;

import by.bsu.lab5.entity.Car;
import by.bsu.lab5.entity.Car_;
import by.bsu.lab5.exept.DAOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DAOCar {

    private static final Logger log = LoggerFactory.getLogger(DAOCar.class);

    public List<Car> findAll() throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Car> cq = cb.createQuery(Car.class);
            Root<Car> root = cq.from(Car.class);
            root.fetch(Car_.bodyType);
            cq.select(root).orderBy(cb.asc(root.get(Car_.id)));
            List<Car> list = em.createQuery(cq).getResultList();
            log.debug("Loaded {} cars", list.size());
            return list;
        } catch (Exception e) {
            log.error("Can't read cars", e);
            throw new DAOException("Can't read cars: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Car> findInRepair() throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Car> cq = cb.createQuery(Car.class);
            Root<Car> root = cq.from(Car.class);
            root.fetch(Car_.bodyType);
            cq.select(root)
              .where(cb.equal(root.get(Car_.status), Car.STATUS_REPAIR))
              .orderBy(cb.asc(root.get(Car_.id)));
            List<Car> list = em.createQuery(cq).getResultList();
            log.info("Cars in repair: {}", list.size());
            return list;
        } catch (Exception e) {
            log.error("Can't read cars in repair", e);
            throw new DAOException("Can't read cars in repair: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Car findById(int id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Car> cq = cb.createQuery(Car.class);
            Root<Car> root = cq.from(Car.class);
            root.fetch(Car_.bodyType);
            cq.select(root).where(cb.equal(root.get(Car_.id), id));
            List<Car> list = em.createQuery(cq).getResultList();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            log.error("Can't read car with id = {}", id, e);
            throw new DAOException("Can't read car with id = " + id + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public int insert(Car car) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            car.setBodyType(em.find(by.bsu.lab5.entity.BodyType.class, car.getBodyType().getId()));
            em.persist(car);
            em.getTransaction().commit();
            log.info("Car created: {}", car);
            return car.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't insert car {}", car.getPlateNumber(), e);
            throw new DAOException("Can't insert car '" + car.getPlateNumber() + "': " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void updateStatus(int id, String status) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Car car = em.find(Car.class, id);
            if (car == null) {
                em.getTransaction().rollback();
                throw new DAOException("Car with id = " + id + " is not found");
            }
            car.setStatus(status);
            em.getTransaction().commit();
            log.info("Car {} status -> {}", id, status);
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't update status of car with id = {}", id, e);
            throw new DAOException("Can't update status of car with id = " + id + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void delete(int id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Car car = em.find(Car.class, id);
            if (car == null) {
                em.getTransaction().rollback();
                throw new DAOException("Car with id = " + id + " is not found");
            }
            em.remove(car);
            em.getTransaction().commit();
            log.info("Car {} deleted", id);
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't delete car with id = {}", id, e);
            throw new DAOException("Can't delete car with id = " + id
                    + " (возможно, он назначен на рейсы): " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
