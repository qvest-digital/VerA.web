package de.tarent.dblayer.persistence;
import de.tarent.commons.datahandling.entity.EntityFactory;
import de.tarent.commons.datahandling.entity.DefaultEntityFactory;

public class FirmaEntityFactory extends DefaultEntityFactory {

    private static FirmaEntityFactory instance = null;

    protected FirmaEntityFactory() {
        super(Firma.class);
    }

    public static synchronized FirmaEntityFactory getInstance() {
        if (instance == null) {
            instance = new FirmaEntityFactory();
        }
        return instance;
    }

    protected EntityFactory getFactoryFor(String attribute) {
        if ("firma".equals(attribute)) {
            return PersonEntityFactory.getInstance();
        }
        return null;
    }
}
