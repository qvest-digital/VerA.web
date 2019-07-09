package org.evolvis.veraweb.onlinereg.rest;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

/**
 * Created by mley on 01.09.14.
 */
public abstract class AbstractResource {
    @Context
    @Setter // for testing
    protected ServletContext context;

    protected SessionFactory getSessionFactory() {
        return (SessionFactory) context.getAttribute("SessionFactory");
    }

    protected Session openSession() {
        return getSessionFactory().openSession();
    }
}
