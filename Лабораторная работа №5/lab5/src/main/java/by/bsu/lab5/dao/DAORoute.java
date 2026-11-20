package by.bsu.lab5.dao;

import by.bsu.lab5.entity.Route;
import by.bsu.lab5.entity.Route_;
import by.bsu.lab5.exept.DAOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DAORoute {

    private static final Logger log = LoggerFactory.getLogger(DAORoute.class);

    public List<Route> findAll() throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Route> cq = cb.createQuery(Route.class);
            Root<Route> root = cq.from(Route.class);
            cq.select(root).orderBy(cb.asc(root.get(Route_.id)));
            List<Route> list = em.createQuery(cq).getResultList();
            log.debug("Loaded {} routes", list.size());
            return list;
        } catch (Exception e) {
            log.error("Can't read routes", e);
            throw new DAOException("Can't read routes: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Route findById(int id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Route> cq = cb.createQuery(Route.class);
            Root<Route> root = cq.from(Route.class);
            cq.select(root).where(cb.equal(root.get(Route_.id), id));
            List<Route> list = em.createQuery(cq).getResultList();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            log.error("Can't read route with id = {}", id, e);
            throw new DAOException("Can't read route with id = " + id + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public int insert(Route route) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(route);
            em.getTransaction().commit();
            log.info("Route created: {}", route);
            return route.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't insert route {} - {}", route.getCityFrom(), route.getCityTo(), e);
            throw new DAOException("Can't insert route " + route.getCityFrom() + " - " + route.getCityTo()
                    + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
