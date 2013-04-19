package de.tarent.aa.veraweb.db.daoimpl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import de.tarent.aa.veraweb.db.dao.GenericDao;

/**
 * Abstract implementation of the {@link GenericDao} interface.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * 
 * @param <T>
 *            type of the entity
 * @param <PK>
 *            type of the primary key
 */
@Transactional
public abstract class GenericDaoImpl<T extends Serializable, PK extends Serializable> implements GenericDao<T, PK> {

    /**
     * Logger used wihtin this class.
     */
    private static final Logger LOG = Logger.getLogger(GenericDaoImpl.class);

    /**
     * {@link EntityManager}.
     */
    @PersistenceContext
    protected EntityManager em;

    /**
     * Type of the entity class. Will be retrieved through instantiation.
     */
    private Class<?> entityType;

    /**
     * Type of the primary key. Will be retrieved through instantiation.
     */
    @SuppressWarnings("unused")
    private Class<?> pkType;

    /**
     * The name of the table represented by the entity. If the table name is not the same as the simple name of the
     * class representing the entity, which is the default behavior.
     */
    private String tableName;

    /**
     * Std. constructor.
     */
    public GenericDaoImpl() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        entityType = (Class<?>) pt.getActualTypeArguments()[0];
        pkType = (Class<?>) pt.getActualTypeArguments()[1];

    }

    @Override
    public void persist(final T newInstance) throws Exception {
        if (newInstance == null) {
            LOG.error("trying to persist null entity");
            throw new NullPointerException();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing persist for entity type: " + entityType.getSimpleName() + " with values: "
                    + newInstance);
        }
        em.persist(newInstance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T find(final PK id) throws Exception {
        if (id == null) {
            LOG.error("primary key is null");
            throw new NullPointerException();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing find for entity type: " + entityType.getSimpleName() + " with id: " + id.toString());
        }
        return (T) em.find(entityType, id);
    }

    @Override
    public T update(final T changedInstance) throws Exception {
        if (changedInstance == null) {
            LOG.error("trying to update null entity");
            throw new NullPointerException();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing update for entity type:" + entityType.getSimpleName() + " with entity: "
                    + changedInstance.toString());
        }
        return (T) em.merge(changedInstance);
    }

    @Override
    public void delete(final T instanceToDelete) throws Exception {
        if (instanceToDelete == null) {
            LOG.error("trying to delete null entity");
            throw new NullPointerException();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing delete for entity type:" + entityType.getSimpleName() + " with entity: "
                    + instanceToDelete.toString());
        }

        em.remove(em.merge(instanceToDelete));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing findAll for entity " + getTableName());
        }

        Query q = em.createNativeQuery("SELECT * FROM " + getTableName(), entityType);
        return q.getResultList();
    }

    @Override
    public long count() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing count for entity " + getTableName());
        }
        Query q = em.createQuery("SELECT COUNT (x) FROM " + getTableName() + " x", Long.class);
        return (Long) q.getSingleResult();
    }

    @Override
    public int deleteAll() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing deleteAll for entity " + getTableName());
        }
        Query q = em.createQuery("DELETE FROM " + getTableName());
        return q.executeUpdate();
    }

    /**
     * Getter for table name, which the given entity represents.
     * 
     * @return either the simpleName of the class this entity represents, or, tablename, if it was set.
     */
    private String getTableName() {
        if (tableName == null) {
            return entityType.getSimpleName();
        }
        return tableName;
    }
}
