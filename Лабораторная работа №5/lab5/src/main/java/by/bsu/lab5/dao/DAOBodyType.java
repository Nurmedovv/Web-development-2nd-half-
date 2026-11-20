package by.bsu.lab5.dao;

import by.bsu.lab5.entity.BodyType;
import by.bsu.lab5.entity.BodyType_;
import by.bsu.lab5.exept.DAOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DAOBodyType {

    private static final Logger log = LoggerFactory.getLogger(DAOBodyType.class);

    public List<BodyType> findAll() throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<BodyType> cq = cb.createQuery(BodyType.class);
            Root<BodyType> root = cq.from(BodyType.class);
            cq.select(root).orderBy(cb.asc(root.get(BodyType_.id)));
            List<BodyType> list = em.createQuery(cq).getResultList();
            log.debug("Loaded {} body types", list.size());
            return list;
        } catch (Exception e) {
            log.error("Can't read body types", e);
            throw new DAOException("Can't read body types: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public BodyType findById(int id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<BodyType> cq = cb.createQuery(BodyType.class);
            Root<BodyType> root = cq.from(BodyType.class);
            cq.select(root).where(cb.equal(root.get(BodyType_.id), id));
            List<BodyType> list = em.createQuery(cq).getResultList();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            log.error("Can't read body type with id = {}", id, e);
            throw new DAOException("Can't read body type with id = " + id + ": " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public int insert(BodyType bodyType) throws DAOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(bodyType);
            em.getTransaction().commit();
            log.info("Body type created: {}", bodyType);
            return bodyType.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Can't insert body type {}", bodyType.getName(), e);
            throw new DAOException("Can't insert body type '" + bodyType.getName() + "': " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
