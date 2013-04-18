package de.tarent.aa.veraweb.db.daoimpl;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import de.tarent.aa.veraweb.db.dao.TaskDao;
import de.tarent.aa.veraweb.db.entity.Task;

@Component(value = "TaskDao")
public class TaskDaoImpl extends GenericDaoImpl<Task, Long> implements TaskDao {

    @Override
    public int deleteAll() {
        int count = super.deleteAll();
        
        Query q = em.createNativeQuery("ALTER SEQUENCE veraweb.ttask_pk_seq MINVALUE 1 START 1 RESTART 1");
        q.executeUpdate();
        
        return count;
        
    }
}
