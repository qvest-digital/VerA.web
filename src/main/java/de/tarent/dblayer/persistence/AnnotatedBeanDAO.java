package de.tarent.dblayer.persistence;

/** data access object for annotated beans. simplifies the
 * handling of DAOs a bit (but just a little bit...)
 * 
 * @author Martin Pelzer, tarent GmbH
 *
 */
public class AnnotatedBeanDAO extends AbstractDAO {

	public AnnotatedBeanDAO(Class bean) {
		super(bean);
		this.setEntityFactory(new DefaultAnnotatedBeanEntityFactory(bean));
	}
	
}
