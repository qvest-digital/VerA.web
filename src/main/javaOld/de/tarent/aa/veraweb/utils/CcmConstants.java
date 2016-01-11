/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.utils;

/**
 * Diese Schnittstelle stellt Konstanten für die Kommunikation mittels des
 * zentralen Kommunikationsmoduls ZKM (aka central communication module CCM)
 * bereit.
 *
 * @author mikel
 */
public interface CcmConstants {
    /** VerA.web-Namensraum-URI */
    public final static String CCM_NAMESPACE_URI = "http://www.auswaertiges-amt.de/CCM";
    /** xmlns:ccm-Namensraum-Attribut */
    public final static String CCM_NAMESPACE_ATTRIBUTE = "xmlns:ccm";
    /** Mitteilungselement-Name mit Namensraumm-Præfix */
    public final static String MESSAGE_ELEMENT_CCM = "ccm:message";
    /** lokaler Mitteilungselement-Name */
    public final static String MESSAGE_ELEMENT = MESSAGE_ELEMENT_CCM.substring(4);
    /** Versionselement-Name mit Namensraumm-Præfix */
    public final static String VERSION_ELEMENT_CCM = "ccm:version";
    /** lokaler Versionselement-Name */
    public final static String VERSION_ELEMENT = VERSION_ELEMENT_CCM.substring(4);
    /** ID-Element-Name mit Namensraumm-Præfix */
    public final static String ID_ELEMENT_CCM = "ccm:id";
    /** lokaler ID-Element-Name */
    public final static String ID_ELEMENT = ID_ELEMENT_CCM.substring(4);
    /** Text des Versionselements */
    public final static String VERSION_TEXT = "1.0";
    /** Kopfelement-Name mit Namensraumm-Præfix */
    public final static String HEADER_ELEMENT_CCM = "ccm:header";
    /** lokaler Kopfelement-Name */
    public final static String HEADER_ELEMENT = HEADER_ELEMENT_CCM.substring(4);
    /** Körperelement-Name mit Namensraumm-Præfix */
    public final static String BODY_ELEMENT_CCM = "ccm:body";
    /** lokaler Körperelement-Name */
    public final static String BODY_ELEMENT = BODY_ELEMENT_CCM.substring(4);
    /** Prioritätselement-Name mit Namensraumm-Præfix */
    public final static String PRIORITY_ELEMENT_CCM = "ccm:priority";
    /** lokaler Prioritätselement-Name */
    public final static String PRIORITY_ELEMENT = PRIORITY_ELEMENT_CCM.substring(4);
    /** Text des Prioritätselements: hohe Priorität */
    public final static String PRIOTITY_TEXT_HIGH = "hoch";
    /** Text des Prioritätselements: normale Priorität */
    public final static String PRIOTITY_TEXT_NORMAL = "normal";
    /** Zeitlimitelement-Name mit Namensraumm-Præfix */
    public final static String TIMELIMIT_ELEMENT_CCM = "ccm:timelimit";
    /** lokaler Zeitlimitelement-Name */
    public final static String TIMELIMIT_ELEMENT = TIMELIMIT_ELEMENT_CCM.substring(4);
    /** Absenderelement-Name mit Namensraumm-Præfix */
    public final static String SENDER_ELEMENT_CCM = "ccm:sender";
    /** lokaler Absenderelement-Name */
    public final static String SENDER_ELEMENT = SENDER_ELEMENT_CCM.substring(4);
    /** Empfängerelement-Name mit Namensraumm-Præfix */
    public final static String RECEIVER_ELEMENT_CCM = "ccm:receiver";
    /** lokaler Empfängerelement-Name */
    public final static String RECEIVER_ELEMENT = RECEIVER_ELEMENT_CCM.substring(4);
    /** Ortelement-Name mit Namensraumm-Præfix */
    public final static String PLACE_ELEMENT_CCM = "ccm:place";
    /** lokaler Ortelement-Name */
    public final static String PLACE_ELEMENT = PLACE_ELEMENT_CCM.substring(4);
    /** ZKM-Kürzel-Element-Name mit Namensraumm-Præfix */
    public final static String ENDPOINT_ELEMENT_CCM = "ccm:endpoint";
    /** lokaler ZKM-Kürzel-Element-Name */
    public final static String ENDPOINT_ELEMENT = ENDPOINT_ELEMENT_CCM.substring(4);
    /** Anwendungselement-Name mit Namensraumm-Præfix */
    public final static String APPLICATION_ELEMENT_CCM = "ccm:application";
    /** lokaler Anwendungselement-Name */
    public final static String APPLICATION_ELEMENT = APPLICATION_ELEMENT_CCM.substring(4);
}
