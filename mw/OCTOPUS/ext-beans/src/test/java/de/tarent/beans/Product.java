/*
 * $Id: Product.java,v 1.2 2007/06/11 13:24:36 christoph Exp $
 *
 * Created on 12.05.2006
 */
package de.tarent.beans;

import de.tarent.octopus.beans.MapBean;

/**
 * This bean class represents an entry of the table <code>produkt</code> of the
 * test database schema.
 *
 * @author mikel
 */
public class Product extends MapBean {
    //
    // bean attributes
    //
    /** pk_produkt serial not null, CONSTRAINT produkt_pkey PRIMARY KEY (pk_produkt) */
    public Integer id;
    /** fk_firma integer */
    public Integer firmId;
    /** name varchar(50) */
    public String name;

    //
    // sample bean
    //
    /** sample {@link Product} bean for easier use of the bean framework */
    public final static Product SAMPLE = new Product();
}
