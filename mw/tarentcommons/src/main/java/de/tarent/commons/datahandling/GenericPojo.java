package de.tarent.commons.datahandling;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
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

import de.tarent.commons.datahandling.entity.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This is an generic implementation for pojos using the {@see java.lang.reflect.Proxy}.
 * With this implementation it is possible to provide an implementation for a pojo-Object by runtime,
 * using the interface of the pojo.
 *
 * <p>GenericPojos delegeate calls to the pojo to two different members: All property access is
 * controled by a GenericPojoStorage and all other calls are controled by a GenericPojoManager.
 * The last one is optional.</p>
 *
 * TODO: Implement default values depending on the type of the propertie
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class GenericPojo implements InvocationHandler {

    GenericPojoManager pojoManager = null;
    GenericPojoStorage pojoStorage = null;

    /**
     * Contructe a new GenericPojo with the supplied storage and manager
     */
    public GenericPojo(GenericPojoStorage storage, GenericPojoManager manager) {
        pojoStorage = storage;
        pojoManager = manager;
    }

    /**
     * Contructe a new GenericPojo with the supplied storage and no manager
     */
    public GenericPojo(GenericPojoStorage storage) {
        pojoStorage = storage;
    }

    /**
     * Contructe a new GenericPojo with the default storage and no manager
     */
    public GenericPojo() {
        pojoStorage = new HashMapPojoStorage();
    }

    /**
     * Create a GenericPojo implementation for the supplied pojoInterface with the default storage and no pojoManager
     *
     * @param pojoInterface the interface for the pojo
     * @return the new Pojo object implementing the supplied interface
     */
    public static Object implementPojo(Class pojoInterface) {
        return Proxy.newProxyInstance(pojoInterface.getClassLoader(), new Class[] { pojoInterface }, new GenericPojo());
    }

    /**
     * Create a GenericPojo implementation for the supplied pojoInterface with the supplied storage and no pojoManager
     *
     * @param pojoInterface the interface for the pojo
     * @return the new Pojo object implementing the supplied interface
     */
    public static Object implementPojo(Class pojoInterface, GenericPojoStorage storage) {
        return Proxy.newProxyInstance(pojoInterface.getClassLoader(), new Class[] { pojoInterface }, new GenericPojo(storage));
    }

    /**
     * Create a GenericPojo implementation for the supplied pojoInterface with the supplied storage and pojoManager
     *
     * @param pojoInterface the interface for the pojo
     * @param storage       the storage for the properties
     * @param manager       the manager for handling of arbitary calls to the pojo
     * @return the new Pojo object implementing the supplied interface
     */
    public static Object implementPojo(Class pojoInterface, GenericPojoStorage storage, GenericPojoManager manager) {
        return Proxy.newProxyInstance(pojoInterface.getClassLoader(), new Class[] { pojoInterface },
                new GenericPojo(storage, manager));
    }

    /**
     * The invocation of the proxy. Here we decide,
     * what action we want to perform: get or set an attribute.
     *
     * @param proxy  The proxy instance
     * @param method The called Method
     * @param args   the call arguments
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();

        if (proxy instanceof Entity && name.equals("getAttribute") && args.length == 1) {
            return pojoStorage.get(args[0]);
        }

        if (proxy instanceof WritableEntity && name.equals("setAttribute") && args.length == 2) {
            return pojoStorage.put(args[0], args[1]);
        }

        // get
        if (name.length() > 3 && (args == null || args.length == 0) && name.startsWith("get")) {
            return pojoStorage.get(getKeyByMethodName(name, 3));
        }

        // set
        if (name.length() > 3 && args != null && args.length == 1 && name.startsWith("set")) {
            return pojoStorage.put(getKeyByMethodName(name, 3), args[0]);
        }

        // is
        if (name.length() > 2 && (args == null || args.length == 0) && name.startsWith("is")) {
            return pojoStorage.get(getKeyByMethodName(name, 2));
        }

        // if no property is accessed, we delegate the call to a PojoManager
        if (pojoManager != null) {
            return pojoManager.methodCalled(proxy, this, method, args);
        }

        if (name.equals("toString")) {
            return toString();
        }

        return null;
    }

    protected static String getKeyByMethodName(String methodName, int offset) {
        String name = methodName.substring(offset++, offset).toLowerCase();
        if (methodName.length() > offset) {
            name += methodName.substring(offset);
        }
        return name;
    }
}
