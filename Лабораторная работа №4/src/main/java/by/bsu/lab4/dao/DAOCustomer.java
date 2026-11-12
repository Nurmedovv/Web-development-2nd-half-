package by.bsu.lab4.dao;

import by.bsu.lab4.entity.Customer;
import by.bsu.lab4.entity.Customer_;
import by.bsu.lab4.exept.DAOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DAOCustomer {

    private static final Logger log = LoggerFactory.getLogger(DAOCustomer.class);

    public List<Customer> findAll() throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
            Root<Customer> root = cq.from(Customer.class);
            cq.select(root).orderBy(cb.asc(root.get(Customer_.id)));
            List<Customer> list = em.createQuery(cq).getResultList();
            log.debug("Loaded {} customers", list.size());
            return list;
        } catch (Exception e) {
            log.error("Can't read customers", e);
            throw new DAOException("Can't read customers: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Customer findById(int id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
            Root<Customer> root = cq.from(Customer.class);
            cq.select(root).where(cb.equal(root.get(Customer_.id), id));
            List<Customer> list = em.createQuery(cq).getResultList();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            log.error("Can't read customer with id = {}", id, e);
            throw new DAOException("Can't read customer with id = " + id + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public int insert(Customer customer) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(customer);
            em.getTransaction().commit();
            log.info("Customer created: {}", customer);
            return customer.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't insert customer {}", customer.getName(), e);
            throw new DAOException("Can't insert customer '" + customer.getName() + "': " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
