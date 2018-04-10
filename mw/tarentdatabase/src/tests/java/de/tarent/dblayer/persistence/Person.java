package de.tarent.dblayer.persistence;

import java.util.Date;

public class Person {

    String givenName;
    String lastName;
    Date birthday;
    int firmaFk;
    Firma firma;

    int id;

    public int getId() {
        return id;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public int getFirmaFk() {
        return firmaFk;
    }

    public void setFirmaFk(int newFirmaFk) {
        this.firmaFk = newFirmaFk;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date newBirthday) {
        this.birthday = newBirthday;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String newGivenName) {
        this.givenName = newGivenName;

   }

    public Firma getFirma() {
        return firma;
    }

    public void setFirma(Firma newFirma) {
        this.firma = newFirma;
    }
}
