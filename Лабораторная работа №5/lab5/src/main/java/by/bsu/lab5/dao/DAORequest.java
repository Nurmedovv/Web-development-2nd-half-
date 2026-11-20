package by.bsu.lab5.dao;

import by.bsu.lab5.entity.Request;
import by.bsu.lab5.entity.Request_;
import by.bsu.lab5.exept.DAOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DAORequest {

    private static final Logger log = LoggerFactory.getLogger(DAORequest.class);

    public List<Request> findAll() throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Request> cq = cb.createQuery(Request.class);
            Root<Request> root = cq.from(Request.class);
            root.fetch(Request_.customer);
            root.fetch(Request_.route);
            root.fetch(Request_.requiredBodyType);
            cq.select(root).orderBy(cb.asc(root.get(Request_.id)));
            List<Request> list = em.createQuery(cq).getResultList();
            log.debug("Loaded {} requests", list.size());
            return list;
        } catch (Exception e) {
            log.error("Can't read requests", e);
            throw new DAOException("Can't read requests: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Request findById(int id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Request> cq = cb.createQuery(Request.class);
            Root<Request> root = cq.from(Request.class);
            root.fetch(Request_.customer);
            root.fetch(Request_.route);
            root.fetch(Request_.requiredBodyType);
            cq.select(root).where(cb.equal(root.get(Request_.id), id));
            List<Request> list = em.createQuery(cq).getResultList();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            log.error("Can't read request with id = {}", id, e);
            throw new DAOException("Can't read request with id = " + id + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public int insert(Request request) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            request.setCustomer(em.find(by.bsu.lab5.entity.Customer.class, request.getCustomer().getId()));
            request.setRoute(em.find(by.bsu.lab5.entity.Route.class, request.getRoute().getId()));
            request.setRequiredBodyType(em.find(by.bsu.lab5.entity.BodyType.class,
                    request.getRequiredBodyType().getId()));
            em.persist(request);
            em.getTransaction().commit();
            log.info("Request created: {}", request);
            return request.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't insert request of customer {}", request.getCustomer(), e);
            throw new DAOException("Can't insert request: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
