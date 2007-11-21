/*
 * $Id: OrgUnitDependent.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 02.08.2005
 */
package de.tarent.aa.veraweb.beans;

/**
 * Diese Schnittstelle dient als Marker für Beans, die mandantenspezifisch behandelt werden
 * sollen.<br>
 * 
 * Dies wird über eine Markerschnittstelle statt einer Zwischenklasse in der Hierarchie
 * umgesetzt, da der konkrete Filter auf den Mandanten Factory-abhängig ist. 
 * 
 * @author mikel
 */
public interface OrgUnitDependent {

}
