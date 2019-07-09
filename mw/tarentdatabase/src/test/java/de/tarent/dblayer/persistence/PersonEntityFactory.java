package de.tarent.dblayer.persistence;
import de.tarent.commons.datahandling.entity.EntityFactory;
import de.tarent.commons.datahandling.entity.DefaultEntityFactory;

public class PersonEntityFactory extends DefaultEntityFactory {

    private static PersonEntityFactory instance = null;

    protected PersonEntityFactory() {
        super(Person.class);
    }

    public static synchronized PersonEntityFactory getInstance() {
        if (instance == null) {
            instance = new PersonEntityFactory();
        }
        return instance;
    }

    protected EntityFactory getFactoryFor(String attribute) {
        if ("firma".equals(attribute)) {
            return FirmaEntityFactory.getInstance();
        }
        return null;
    }
}
