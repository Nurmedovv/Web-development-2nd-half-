package by.bsu.lab5.dao;

import by.bsu.lab5.entity.Driver;
import by.bsu.lab5.entity.Driver_;
import by.bsu.lab5.exept.DAOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DAODriver {

    private static final Logger log = LoggerFactory.getLogger(DAODriver.class);

    public List<Driver> findAll() throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Driver> cq = cb.createQuery(Driver.class);
            Root<Driver> root = cq.from(Driver.class);
            cq.select(root).orderBy(cb.asc(root.get(Driver_.id)));
            List<Driver> list = em.createQuery(cq).getResultList();
            log.debug("Loaded {} drivers", list.size());
            return list;
        } catch (Exception e) {
            log.error("Can't read drivers", e);
            throw new DAOException("Can't read drivers: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Driver findById(int id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Driver> cq = cb.createQuery(Driver.class);
            Root<Driver> root = cq.from(Driver.class);
            cq.select(root).where(cb.equal(root.get(Driver_.id), id));
            List<Driver> list = em.createQuery(cq).getResultList();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            log.error("Can't read driver with id = {}", id, e);
            throw new DAOException("Can't read driver with id = " + id + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public int insert(Driver driver) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(driver);
            em.getTransaction().commit();
            log.info("Driver created: {}", driver);
            return driver.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't insert driver {}", driver.getFullName(), e);
            throw new DAOException("Can't insert driver '" + driver.getFullName() + "': " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void delete(int id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Driver driver = em.find(Driver.class, id);
            if (driver == null) {
                em.getTransaction().rollback();
                throw new DAOException("Driver with id = " + id + " is not found");
            }
            em.remove(driver);
            em.getTransaction().commit();
            log.info("Driver {} deleted", id);
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't delete driver with id = {}", id, e);
            throw new DAOException("Can't delete driver with id = " + id
                    + " (возможно, он назначен на рейсы): " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
