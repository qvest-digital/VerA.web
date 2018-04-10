package de.tarent.dblayer.persistence;

import java.util.*;

public class Firma {

    int turnover;
    String name;
    int id;

    List mitarbeiter;

    public int getId() {
        return id;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public int getTurnover() {
        return turnover;
    }

    public void setTurnover(int newTurnover) {
        this.turnover = newTurnover;
    }

    public List getMitarbeiter() {
        return mitarbeiter;
    }

    public void setMitarbeiter(List newMitarbeiter) {
        this.mitarbeiter = newMitarbeiter;
    }

    public void addMitarbeiter(Person person) {
        if (mitarbeiter == null)
            mitarbeiter = new ArrayList();
        mitarbeiter.add(person);
    }
}
