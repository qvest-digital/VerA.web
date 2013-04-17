package de.tarent.aa.veraweb.db.daoimpl;

import org.springframework.stereotype.Component;

import de.tarent.aa.veraweb.db.dao.TaskDao;
import de.tarent.aa.veraweb.db.entity.Task;

@Component(value = "TaskDao")
public class TaskDaoImpl extends GenericDaoImpl<Task, Long> implements TaskDao {


}
