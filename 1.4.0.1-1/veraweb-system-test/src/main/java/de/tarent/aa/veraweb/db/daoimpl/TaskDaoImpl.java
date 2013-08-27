package de.tarent.aa.veraweb.db.daoimpl;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import de.tarent.aa.veraweb.db.dao.TaskDao;
import de.tarent.aa.veraweb.db.entity.Task;

@Component(value = "TaskDao")
public class TaskDaoImpl extends GenericDaoImpl<Task, Long> implements TaskDao {

    /**
     * Logger used wihtin this class.
     */
    private static final Logger LOG = Logger.getLogger(TaskDaoImpl.class);
    
    @Override
    public int deleteAll() {
        int count = super.deleteAll();
        
        Query q = em.createNativeQuery("ALTER SEQUENCE veraweb.ttask_pk_seq MINVALUE 1 START 1 RESTART 1");
        q.executeUpdate();
        
        return count;
        
    }

    @Override
    public Task getTask(String title) throws Exception {
        if (title == null) {
            LOG.error("title is null");
            throw new NullPointerException();
        }
        Query q = em.createNamedQuery(Task.GET_TASK_BY_TITLE);
        q.setParameter("title", title);
        return (Task) q.getSingleResult();
    }
}
