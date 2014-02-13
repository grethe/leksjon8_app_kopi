/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leksjon8_app;

/**
 *
 * @author Grethe
 */
public class Person {

    private String fornavn;
    private String etternavn;
    private int personNr;

    public Person(int persNr, String fornavn, String etternavn) {
        this.personNr = persNr;
        this.fornavn = fornavn;
        this.etternavn = etternavn;
    }

    public int getPersonNr() {
        return personNr;
    }

    public String getFornavn() {
        return fornavn;
    }

    public String getEtternavn() {
        return etternavn;
    }
    
    public void setFornavn(String newValue){ fornavn = newValue; }
    public void setEtternavn(String newValue){ etternavn = newValue; }
    public void setPersonNr(int newValue){ personNr = newValue;}

    @Override
    public String toString() {
        return fornavn + " " + etternavn;
    }
}      
       