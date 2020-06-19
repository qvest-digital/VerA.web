package de.tarent.commons.datahandling.entity;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import de.tarent.commons.utils.Pojo;
import de.tarent.commons.utils.StringTools;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract class for object creation and object filling over reflection.
 * It is intended for subclassing, as well as usage out of the box.
 */
@Log4j2
public class DefaultEntityFactory implements EntityFactory {
    public static final String PROPERTY_SEPARATOR = ".";

    Class instantiationClass;
    private static Object[] emptyObjectArray = new Object[] {};
    private static Class[] emptyClassArray = new Class[] {};
    private String className;

    /**
     * The name of the PrimaryKey with which to look up entities in the
     * LookupContext for duplicate-identification. The default is null.
     */
    private String keyName;

    /**
     * Contructor for initialisation with the class, the factory should serve
     * for. Uses the default LookupContext-keyName "id".
     */
    public DefaultEntityFactory(Class instantiationClass) {
        this(instantiationClass, null);
    }

    /**
     * Contructor for initialisation with the class, the factory should serve
     * for.
     *
     * @param keyName The id with which to look up entities in the
     *                LookupContext.
     */
    public DefaultEntityFactory(Class instantiationClass, String keyName) {
        setInstantiationClass(instantiationClass);
        this.keyName = keyName;
    }

    /**
     * Returns a factory responsible for creation of instances for supplied attributeName
     * This method should be customized by subclasses.
     */
    protected EntityFactory getFactoryFor(String attributeName) {
        return null;
    }

    /**
     * Returns a factory responsible for creation of instances for supplied attributeName
     * This method should be customized by subclasses.
     */
    protected EntityFactory getFactoryFor(Class attributeType, String attributeName) {
        return EntityFactoryRegistry.getEntityFactory(attributeType);
    }

    protected EntityFactory getFactoryFor(Object entity, String attributeName) {
        return null;
    }

    /**
     * Get the entity from the LookupContext using the keyName as ID.
     */
    public Object getEntityFromLookupContext(AttributeSource as, LookupContext lc) {
        if (keyName == null) {
            return null;
        }
        Object id = as.getAttribute(keyName);
        return lc.getEntity(id, instantiationClass.getName());
    }

    /**
     * Store the entity in the LookupContext using the keyName as ID.
     */
    public void storeEntityInLookupContext(Object entity, LookupContext lc) {
        if (keyName == null) {
            return;
        }
        Object id = Pojo.get(entity, keyName);
        lc.registerEntity(id, instantiationClass.getName(), entity);
    }

    public Object getEntity() {
        try {
            return instantiationClass.getConstructor(emptyClassArray).newInstance(emptyObjectArray);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fills the entity with the AttributeSet
     * TODO: At the moment this implementation covers creation of linked entities only. The filing of referenced entities is
     * not supported for now.
     */
    public void fillEntity(Object entity, AttributeSource as, LookupContext lc) {
        fillEntity(entity, as, lc, false);
    }

    /**
     * Fills the entity with the AttributeSet
     * TODO: At the moment this implementation covers creation of linked entities only. The filing of referenced entities is
     * not supported for now.
     */
    public void fillEntity(Object entity, AttributeSource as, LookupContext lc, boolean fillLinkedEntitiesOnly) {
        assert (as.getAttributeNames() != null) : "AttributeSource.getAttributeNames() should not be null";

        if (logger.isTraceEnabled()) {
            logger.trace("filling entity '" + entity + "' (class '" + className + "')");
        }

        // a list to remember, which complex properties we had already set
        List addedReferredProperties = new ArrayList(0);

        for (Iterator iter = as.getAttributeNames().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();

            int pos = name.indexOf(PROPERTY_SEPARATOR);
            // property to another entity, supplied from another factory
            if (pos != -1) {
                String propertyName = name.substring(0, pos);

                if (addedReferredProperties.contains(propertyName))
                // do not add the same assotiation twice
                {
                    continue;
                }
                addedReferredProperties.add(propertyName);

                // create or fill the entity if any field for the entity exist
                EntityFactory ef = getFactoryFor(propertyName);
                if (ef == null) {
                    ef = getFactoryFor(entity, propertyName);
                }
                if (ef == null) {
                    ef = getFactoryFor(as.getAttributeType(propertyName), propertyName);
                }
                if (ef == null) {
                    throw new RuntimeException(
                      "No factory configured for property '" + propertyName + "' (in factory " + getClass().getName() +
                        ").");
                }
                PrefixedAttributeSource pas = new PrefixedAttributeSource(propertyName + PROPERTY_SEPARATOR, as);
                if (!pas.hasNotNullFields()) {
                    logger.debug("skip entity creation with only null fields");
                    continue;
                }

                Object referredEntity = null;

                // try to lookup an existing referred entity
                Method getMethod = Pojo.getGetMethod(entity, propertyName);
                if (getMethod != null) {
                    referredEntity = Pojo.get(entity, getMethod);
                }

                if (referredEntity != null && !(referredEntity instanceof Collection)) {
                    ef.fillEntity(referredEntity, pas, lc);
                } else {
                    // create a new on, if none exists already
                    referredEntity = ef.getEntity(pas, lc);
                }

                // assign it
                Method setMethod = Pojo.getSetMethod(entity, propertyName);
                Class[] setType = null;
                if (setMethod != null) {
                    setType = setMethod.getParameterTypes();
                }

                // set directly (X:1)
                assert setType != null;
                if (setMethod != null && setType[0].isAssignableFrom(referredEntity.getClass())) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("set entity property '" + propertyName + "' to '" + referredEntity + "'");
                    }
                    Pojo.set(entity, propertyName, referredEntity);

                    // or append to over an addXXX method (X:N)
                } else {
                    // TODO: speed up with caching of the method lookup
                    try {
                        Method addMethod = instantiationClass.getMethod("add" + StringTools.capitalizeFirstLetter(propertyName),
                          new Class[] { referredEntity.getClass() });
                        // TODO: all assignable datatypes should be accepted and not just the one specified by referredEntity
                        // .getClass()
                        if (logger.isTraceEnabled()) {
                            logger.trace(
                              "add value to entity property list '" + propertyName + "' value: '" + referredEntity + "'");
                        }

                        Pojo.set(entity, addMethod, referredEntity);
                    } catch (NoSuchMethodException nsme) {
                        throw new RuntimeException(
                          "No suitable set or add method in class " + instantiationClass + " for property '" +
                            propertyName + "' with argument type " + referredEntity.getClass() + " found.");
                    }
                }

                // easy settable property
            } else {
                if (fillLinkedEntitiesOnly) {
                    continue;
                }

                if (logger.isTraceEnabled()) {
                    logger.trace("set entity property '" + name + "' to '" + as.getAttribute(name) + "'");
                }
                Pojo.set(entity, name, as.getAttribute(name));
            }
        }
    }

    /**
     * This default implementation fills the entity over reflection
     */
    public Object getEntity(AttributeSource as, LookupContext lc) {
        assert as != null;
        Object entity = getEntityFromLookupContext(as, lc);
        if (entity == null) {
            entity = getEntity();
            fillEntity(entity, as, lc, false);
            storeEntityInLookupContext(entity, lc);
        } else {
            fillEntity(entity, as, lc, false);
        }
        return entity;
    }

    /**
     * Returns an AttributeSource wrapper over the supplied entity
     */
    public void writeTo(ParamSet target, Object entity) {
        for (Iterator iter = target.getAttributeNames().iterator(); iter.hasNext(); ) {
            String attributeName = (String) iter.next();

            int pos = attributeName.indexOf(PROPERTY_SEPARATOR);
            if (pos != -1) {
                String propertyName = attributeName.substring(0, pos);
                String referingEntityPropertyName = attributeName.substring(pos + 1);
                Object referingEntity = Pojo.get(entity, propertyName);
                if (referingEntity != null) {
                    target.setAttribute(attributeName, Pojo.get(referingEntity, referingEntityPropertyName));
                } else {
                    target.setAttribute(attributeName, null);
                    if (logger.isDebugEnabled()) {
                        logger.debug(
                          "No instance for property " + propertyName + " found in entity " + entity.getClass().getName() +
                            ".");
                    }
                }
            } else {
                target.setAttribute(attributeName, Pojo.get(entity, attributeName));
            }
        }
    }

    public Class getInstantiationClass() {
        return instantiationClass;
    }

    public void setInstantiationClass(Class newInstantiationClass) {
        this.instantiationClass = newInstantiationClass;
        className = this.instantiationClass.getName();
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
