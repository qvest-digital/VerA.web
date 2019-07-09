package de.tarent.dblayer.persistence;
import de.tarent.dblayer.engine.DBAccess;

/**
 * data access object for annotated beans. simplifies the
 * handling of DAOs a bit (but just a little bit...)
 *
 * @author Martin Pelzer, tarent GmbH
 */
public class AnnotatedBeanDAO extends AbstractDAO {

    public AnnotatedBeanDAO(Class bean) {
        super(bean);
        this.setEntityFactory(new DefaultAnnotatedBeanEntityFactory(bean));
    }

    public AnnotatedBeanDAO(Class bean, AbstractDBMapping dbMapping) {
        super(bean);
        this.setEntityFactory(new DefaultAnnotatedBeanEntityFactory(bean));

        // initialize DB mapping and set to this DAO
        dbMapping.setDBContext(DBAccess.getContextWithoutConnection());
        dbMapping.setBeanName(bean);
        dbMapping.configureMapping();
        dbMapping.init();
        setDbMapping(dbMapping);
    }
}
