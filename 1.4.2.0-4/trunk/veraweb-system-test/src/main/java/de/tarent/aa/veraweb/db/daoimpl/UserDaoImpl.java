package de.tarent.aa.veraweb.db.daoimpl;

import org.springframework.stereotype.Component;

import de.tarent.aa.veraweb.db.dao.UserDao;
import de.tarent.aa.veraweb.db.entity.User;

@Component(value = "UserDao")
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {

}
