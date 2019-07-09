package de.tarent.aa.veraweb.beans.facade;
/**
 * Diese Schnittstelle stellt Konstanten für {@link de.tarent.aa.veraweb.beans.Person}-
 * und {@link de.tarent.aa.veraweb.beans.Person}-Instanzen zur Verfügung.
 *
 * @author christoph
 */
public interface PersonConstants {
    /**
     * Person ist noch nicht als gelöscht markiert worden.
     */
    String DELETED_FALSE = "f";

    /**
     * Person wurde als gelöscht markiert.
     */
    String DELETED_TRUE = "t";

    /**
     * Person ist eine Privatperson.
     */
    String ISCOMPANY_FALSE = "f";

    /**
     * Person ist eine Firma / Institution.
     */
    String ISCOMPANY_TRUE = "t";

    /**
     * Member ID für die Hauptperson
     */
    int MEMBER_MAIN = 1;

    /**
     * Member ID für den Partner
     */
    int MEMBER_PARTNER = 2;

    /**
     * Adresstype ID für die geschäftliche Anschrift
     */
    int ADDRESSTYPE_BUSINESS = 1;

    /**
     * Adresstype ID für die private Anschrift
     */
    int ADDRESSTYPE_PRIVATE = 2;

    /**
     * Adresstype ID für die weitere Anschrift
     */
    int ADDRESSTYPE_OTHER = 3;

    /**
     * Locale ID für den lateinischen Zeichensatz
     */
    int LOCALE_LATIN = 1;

    /**
     * Locale ID für den lateinischen Zeichensatz
     */
    int LOCALE_EXTRA1 = 2;

    /**
     * Locale ID für den lateinischen Zeichensatz
     */
    int LOCALE_EXTRA2 = 3;

    /**
     * Gibt an ob eine Person aus dem Inland kommt.
     */
    String DOMESTIC_INLAND = "t";

    /**
     * Gibt an ob eine Person aus dem Ausland kommt.
     */
    String DOMESTIC_AUSLAND = "f";

    /**
     * Gibt an ob eine Person männlich ist.
     */
    String SEX_MALE = "m";

    /**
     * Gibt an ob eine Person weiblich ist.
     */
    String SEX_FEMALE = "f";
}
