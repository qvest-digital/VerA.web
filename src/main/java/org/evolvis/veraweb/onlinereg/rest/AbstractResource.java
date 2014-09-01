package org.evolvis.veraweb.onlinereg.rest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

/**
 * Created by mley on 01.09.14.
 */
public class AbstractResource {

    @Context
    protected ServletContext context;

    protected SessionFactory getSessionFactory() {
        return (SessionFactory) context.getAttribute("SessionFactory");
    }

    protected Session openSession() {
        return getSessionFactory().openSession();
    }
}
