package de.tarent.aa.veraweb.db.daoimpl;

import org.springframework.stereotype.Component;

import de.tarent.aa.veraweb.db.dao.EventDao;
import de.tarent.aa.veraweb.db.entity.Event;

@Component(value = "EventDao")
public class EventDaoImpl extends GenericDaoImpl<Event, Long> implements EventDao {

}
