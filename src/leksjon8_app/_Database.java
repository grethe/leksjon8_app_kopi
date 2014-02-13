/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package leksjon8_app;

/**
 *
 * @author Grethe

 * Database.java - "Programmering i Java", 4.utgave - 2009-07-01
 *
 * Dette er en "database-wrapper" for vedlikehold av tabellen Person.
 * Konstruktøren åpner en databaseforbindelse som holdes inntil klienten
 * kaller metoden kobleNedforbindelse().
 *
 * Feilhåndtering: Konstruktøren kaster unntaksobjekt dersom
 * JDBC-klassen ikke kan lastes, eller databaseforbindelsen ikke kan åpnes.
 * For øvrig håndteres SQLException ved en utskrift i kommandovinduet.
 * Slike unntak skyldes programmeringsfeil og  sendes ikke til klienten.
 */

import java.sql.*;
import java.util.ArrayList;

public class _Database {
  private Connection forbindelse;

  /**
   * Laster JDBC-klassene og åpner databaseforbindelsen.
   */
  public _Database(String dbDriver, String dbNavn) throws Exception {
    try {
      Class.forName(dbDriver);
      forbindelse = DriverManager.getConnection(dbNavn);
    } catch (Exception e) {
      Opprydder.skrivMelding(e, "konstruktør");
      throw e;
    }
  }

  /**
   * Metode for å lukke forbindelse. Må kalles av klienten før programmet avsluttes.
   */
  public void kobleNedForbindelse() {
    Opprydder.lukkForbindelse(forbindelse);
  }

  /**
   * Returnerer en ArrayList som inneholder alle dataene i tabellen person.
   * ArrayListen inneholder personobjekter. Listen er sortert etter etternavn.
   * Returnerer null dersom SQLException er kastet.
   */
  public ArrayList<Person> finnAlle() {
    ArrayList<Person>  alle = new ArrayList<Person>();
    String sqlsetning  = "select persnr, fornavn, etternavn from person order by etternavn, fornavn";
    System.out.println(sqlsetning);

    ResultSet res = null;
    Statement setning = null;
    try {
      setning = forbindelse.createStatement();
      res = setning.executeQuery(sqlsetning);
      while (res.next()) {
        int nr = res.getInt("persnr");
        String fornavn = res.getString("fornavn");
        String etternavn = res.getString("etternavn");
        Person navnet = new Person(nr, fornavn, etternavn);
        alle.add(navnet);
      }
    } catch (SQLException e) {
      Opprydder.skrivMelding(e, "finnAlle()");
      alle = null;
    } finally {  // setningene nedenfor utføres alltid
      Opprydder.lukkResSet(res);
      Opprydder.lukkSetning(setning);
    }
    return alle;
  }

  /**
   * Endrer navn.
   * Det nye navnet samt personens nummer sendes inn som argument.
   * Returnerer false dersom ingen person med dette nummeret eksisterer,
   * eller SQLException er inntruffet.
   */
  public boolean endreNavn(Person personen) {
    int nr = personen.getPersonNr();
    String nyttFornavn = personen.getFornavn();
    String nyttEtternavn = personen.getEtternavn();
    String sqlsetning = "update person set fornavn = '" + nyttFornavn
          + "', etternavn = '" + nyttEtternavn + "' where persnr = " + nr;
    System.out.println(sqlsetning);

    Statement setning = null;
    try {
      setning = forbindelse.createStatement();
      return (setning.executeUpdate(sqlsetning) != 0);
    } catch (SQLException e) {
      Opprydder.skrivMelding(e, "endreNavn()");
    } finally {
      Opprydder.lukkSetning(setning);
    }
    return false;
  }

  /**
   * Registrerer ny person.
   * Nummer tildeles av systemet, og et fullstendig personobjekt returneres til
   * klienten. Dersom databasetabellen er tom, settes personnr lik 1, ellers
   * settes det lik hittil største nummer + 1.(På grunn av at data kan slettes,
   * kan denne metoden føre til hull i nummersekvensen.)
   * Metoden returnerer null dersom feil oppstått.
   */
  public Person registrerNyPerson(String fornavn, String etternavn) {
    int nyttNr = 1; // dersom det ikke er data i databasen
    boolean ok = false;
    int antForsøk = 0;
    do {
      ResultSet res = null;
      Statement setning = null;
      try {
        String sqlsetning = "select max(persnr) as maks from person";
        setning = forbindelse.createStatement();
        res = setning.executeQuery(sqlsetning);
         /*
          * Hvis det ikke er data i tabellen, vil maks() returnere SQL NULL.
          * getInt() vil omforme denne til 0, og nyttNr blir dermed 1 dersom
          * det ikke er data i databasen. Det er ok.
          */
        res.next();
        nyttNr = res.getInt("maks") + 1;
        sqlsetning = "insert into person values("
                           + nyttNr + ", '"  + fornavn + "', '" + etternavn + "')";
        System.out.println(sqlsetning);
        setning.executeUpdate(sqlsetning);
        ok = true;
      } catch (SQLException e) {
        /*
         * Dersom feilen skyldes at person med samme nummer eksisterer fra før,
         *  må det bety at en annen klient har vært inne i databasen mellom
         *  våre to sql-setninger. Vi kjører derfor gjennom opptil tre ganger til.
         *  (Dette er noe som vil inntreffe svært sjelden.)
         */
        if (antForsøk < 4) {
          antForsøk++;
        } else {
          Opprydder.skrivMelding(e, "registrerNyPerson()");
          return null;   // RETUR, feil oppstått (finally-blokken blir utført)
        }
      } finally {
        Opprydder.lukkResSet(res);
        Opprydder.lukkSetning(setning);
      }
    } while (!ok);
    return new Person(nyttNr, fornavn, etternavn);
  }

  /**
   * Sletter person med gitt nummer.
   * Returnerer false dersom personen ikke finnes, eller SQLException inntruffet.
   */
  public boolean slettPerson(int nr) {
    String sqlsetning = "delete from person where persnr = " + nr;
    System.out.println(sqlsetning);

    Statement setning = null;
    try {
      setning = forbindelse.createStatement();
      return (setning.executeUpdate(sqlsetning) != 0);
    } catch (SQLException e) {
      Opprydder.skrivMelding(e, "slettPerson()");
    } finally {
      Opprydder.lukkSetning(setning);
    }
    return false;
  }

  /**
   * En ufullstendig testklient følger.
   * I utskriften vil sql-setningene fra metodene bli skrevet ut.
   * Se oppgave etter dette delkapitlet for mer fullstendig testing.
   */
 
}
