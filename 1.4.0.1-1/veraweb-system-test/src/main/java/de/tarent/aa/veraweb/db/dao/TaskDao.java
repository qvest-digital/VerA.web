package de.tarent.aa.veraweb.db.dao;

import de.tarent.aa.veraweb.db.entity.Task;

public interface TaskDao extends GenericDao<Task, Long> {

    Task getTask(String title) throws Exception;

}
