package de.tarent.aa.veraweb.db.daoimpl;

import org.springframework.stereotype.Component;

import de.tarent.aa.veraweb.db.dao.OrgunitDao;
import de.tarent.aa.veraweb.db.entity.Orgunit;

@Component(value = "OrgunitDao")
public class OrgunitDaoImpl extends GenericDaoImpl<Orgunit, Long> implements OrgunitDao {

}
