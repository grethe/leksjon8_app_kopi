package leksjon8_app;

import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author gs-dell
 */
public class Database {

    private final String dbURL;
    private Connection forbindelse;
    private final String sqlSelectPerson = "select * from person where persnr = ?";
    private final String sqlSelectAllePersoner = "select * from person order by persnr";
    private final String sqlInsertPerson = "insert into person values(?, ?, ?)";
    private final String sqlDeletePerson = "delete from person where persnr = ?";
    private final String sqlUpdatePerson = "update person set fornavn = ?, etternavn = ? where persnr = ?";
    private final String sqlDropTable = "drop table person";
    private final String sqlCreateTable = "CREATE TABLE person(persnr INTEGER not null primary key,fornavn VARCHAR(40) NOT NULL,etternavn VARCHAR(40) NOT NULL)";

    public Database(String dbURL) {
        this.dbURL = dbURL;
    }

    private void åpneForbindelse() {
        // Applications no longer need to explictly load JDBC drivers using Class.forName(). 
        try {
            forbindelse = DriverManager.getConnection(dbURL);
            System.out.println("Databaseforbindelse opprettet");
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Konstruktøren");
            Opprydder.lukkForbindelse(forbindelse);
        }
    }

    private void lukkForbindelse() {
        System.out.println("Lukker databaseforbindelsen");
        Opprydder.lukkForbindelse(forbindelse);
    }

    /*
     * Metode som henter en liste med alle personer som er registrert i tabellen
     * Metoden returnerer null dersom tabellen er tom
     */
    public ArrayList<Person> getAllePersoner() {
        System.out.println("getAllePersoner()");
        PreparedStatement psSelectAlle = null;
        ResultSet res = null;
        ArrayList<Person> personListe = null;
        åpneForbindelse();
        try {
            psSelectAlle = forbindelse.prepareStatement(sqlSelectAllePersoner);
            res = psSelectAlle.executeQuery();
            while (res.next()) {
                Person p = new Person(res.getInt("persnr"), res.getString("fornavn"), res.getString("etternavn"));
                if (personListe == null) {
                    personListe = new ArrayList<Person>();
                }
                personListe.add(p);
            }
        } catch (SQLException e) {
            Opprydder.rullTilbake(forbindelse);
            Opprydder.skrivMelding(e, "getAllePersoner()");

        } finally {
            Opprydder.settAutoCommit(forbindelse);
            Opprydder.lukkSetning(psSelectAlle);
        }
        lukkForbindelse();
        return personListe;
    }

    /*
     * Metode som søker etter person gitt personnr
     * Metoden returnerer null dersom person ikke finnes
     */
    public Person getPerson(int personnr) {
        System.out.println("getPerson()");
        PreparedStatement psSelectEn = null;
        ResultSet res = null;
        Person p = null;
        åpneForbindelse();
        try {
            psSelectEn = forbindelse.prepareStatement(sqlSelectPerson);
            psSelectEn.setInt(1, personnr);
            res = psSelectEn.executeQuery();
            while (res.next()) {
                p = new Person(res.getInt("persnr"), res.getString("fornavn"), res.getString("etternavn"));
            }
        } catch (SQLException e) {
            Opprydder.rullTilbake(forbindelse);
            Opprydder.skrivMelding(e, "getPerson()");

        } finally {
            Opprydder.settAutoCommit(forbindelse);
            Opprydder.lukkSetning(psSelectEn);
        }
        lukkForbindelse();
        return p;
    }
    /*
     * Metode som registrerer en ny person. Metoden returnerer false dersom
     * person med samme personnr registrert fra før.
     */

    public boolean regPerson(Person p) {
        System.out.println("regPerson(): " + p);
        PreparedStatement psRegPerson = null;

        åpneForbindelse();
        boolean ok = false;
        try {
            /*
             * Transaksjon starter, en tabell skal oppdateres, bruker låsing (eg. unødvendig her
             */
            forbindelse.setAutoCommit(false);

            psRegPerson = forbindelse.prepareStatement(sqlInsertPerson);

            /*
             * Oppdaterer persontabellen
             */
            if (p.getPersonNr() < 0) {
                p.setPersonNr(getNyttNr());
            }
            psRegPerson.setInt(1, p.getPersonNr());
            psRegPerson.setString(2, p.getFornavn());
            psRegPerson.setString(3, p.getEtternavn());
            psRegPerson.executeUpdate();
            forbindelse.commit();
            /*
             * Transaksjon slutt
             */
            ok = true;

        } catch (SQLException e) {
            Opprydder.rullTilbake(forbindelse);
            String sqlStatus = e.getSQLState().trim();
            String statusklasse = sqlStatus.substring(0, 2);
            if (statusklasse.equals("23")) { // standard kode for "integrity constraint violation"
                ok = false;  // person med samme personnr er registrert fra før
            } else {
                Opprydder.skrivMelding(e, "regPerson()");
            }
        } finally {
            Opprydder.settAutoCommit(forbindelse);
            Opprydder.lukkSetning(psRegPerson);
        }
        lukkForbindelse();
        return ok;
    }
    /*
     * Metode som registrerer en ny person. Metoden returnerer false dersom
     * person med samme personnr registrert fra før.
     */

    public boolean updatePerson(Person p) {
        System.out.println("updatePerson(): " + p);
        PreparedStatement psUpdatePerson = null;

        åpneForbindelse();
        boolean ok = false;
        try {
            /*
             * Transaksjon starter, en tabell skal oppdateres, bruker låsing (eg. unødvendig her
             */
            forbindelse.setAutoCommit(false);

            psUpdatePerson = forbindelse.prepareStatement(sqlUpdatePerson);

            /*
             * Oppdaterer persontabellen, det er kun mulig å endre fornavn og etternavn
             */
            psUpdatePerson.setInt(3, p.getPersonNr());
            psUpdatePerson.setString(1, p.getFornavn());
            psUpdatePerson.setString(2, p.getEtternavn());
            psUpdatePerson.executeUpdate();
            forbindelse.commit();
            /*
             * Transaksjon slutt
             */
            ok = true;

        } catch (SQLException e) {
            Opprydder.rullTilbake(forbindelse);
            String sqlStatus = e.getSQLState().trim();
            String statusklasse = sqlStatus.substring(0, 2);
            if (statusklasse.equals("23")) { // standard kode for "integrity constraint violation"
                ok = false;  // person med samme personnr er registrert fra før
            } else {
                Opprydder.skrivMelding(e, "updatePerson()");
            }
        } finally {
            Opprydder.settAutoCommit(forbindelse);
            Opprydder.lukkSetning(psUpdatePerson);
        }
        lukkForbindelse();
        return ok;
    }

    private int getNyttNr() {
        int nyttNr = 1; // dersom det ikke er data i databasen
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

        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "registrerNyPerson()");
            return -1;   // RETUR, feil oppstått (finally-blokken blir utført)
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(setning);
        }
        return nyttNr;
    }
    /*
     * Metode som sletter en person. Metoden returnerer false dersom noe feiler
     */

    public boolean slettPerson(Person p) {
        System.out.println("slettPerson(): " + p);
        PreparedStatement psSlettPerson = null;
        åpneForbindelse();
        boolean ok = false;
        try {
            /* Transaksjon starter, en tabell skal oppdateres, bruker låsing (eg. unødvendig her  */
            forbindelse.setAutoCommit(false);
            psSlettPerson = forbindelse.prepareStatement(sqlDeletePerson);
            psSlettPerson.setInt(1, p.getPersonNr());
            psSlettPerson.executeUpdate();
            forbindelse.commit();
            /* Transaksjon slutt */
            ok = true;
        } catch (SQLException e) {
            Opprydder.rullTilbake(forbindelse);
            Opprydder.skrivMelding(e, "slettPerson()");
        } finally {
            Opprydder.settAutoCommit(forbindelse);
            Opprydder.lukkSetning(psSlettPerson);
        }
        lukkForbindelse();
        return ok;
    }

    /*
     * Metodene under er det ikke laget tester for - de brukes for å initere, klargjøre for testing og rydde opp etter
     */
    /* 
     * Metode som sletter tabellen person
     */
    public boolean slettPersonTabell() {
        System.out.println("slettTabell(): ");
        PreparedStatement psDropTable = null;
        åpneForbindelse();
        boolean ok = false;
        try {
            /* Transaksjon starter, en tabell skal oppdateres, bruker låsing (eg. unødvendig her  */
            forbindelse.setAutoCommit(false);
            psDropTable = forbindelse.prepareStatement(sqlDropTable);
            psDropTable.executeUpdate();
            forbindelse.commit();
            /* Transaksjon slutt */
            ok = true;
        } catch (SQLException e) {
            Opprydder.rullTilbake(forbindelse);
            Opprydder.skrivMelding(e, "slettTabell()");
        } finally {
            Opprydder.settAutoCommit(forbindelse);
            Opprydder.lukkSetning(psDropTable);
        }
        lukkForbindelse();
        return ok;
    }
    /* 
     * Metode som oppretter tabellen person
     */

    public boolean opprettPersonTabell() {
        System.out.println("opprettPersonTabell(): ");
        PreparedStatement psCreateTable = null;
        åpneForbindelse();
        boolean ok = false;
        try {
            /* Transaksjon starter, en tabell skal oppdateres, bruker låsing (eg. unødvendig her  */
            forbindelse.setAutoCommit(false);
            psCreateTable = forbindelse.prepareStatement(sqlCreateTable);
            psCreateTable.executeUpdate();
            forbindelse.commit();
            /* Transaksjon slutt */
            ok = true;
        } catch (SQLException e) {
            Opprydder.rullTilbake(forbindelse);
            Opprydder.skrivMelding(e, "opprettPersonTabell()");
        } finally {
            Opprydder.settAutoCommit(forbindelse);
            Opprydder.lukkSetning(psCreateTable);
        }
        lukkForbindelse();
        return ok;
    }
    /* 
     * Metode som sletter tabellen person
     */

    public boolean initPersonTabell(ArrayList<Person> liste) {
        System.out.println("initPersonTabell(): " + liste);
        PreparedStatement psInitTable = null;
        åpneForbindelse();
        boolean ok = false;
        try {
            /* Transaksjon starter, en tabell skal oppdateres, bruker låsing  */
            forbindelse.setAutoCommit(false);
            psInitTable = forbindelse.prepareStatement(sqlInsertPerson);
            for (int i = 0; i < liste.size(); i++) {
                psInitTable.setInt(1, liste.get(i).getPersonNr());
                psInitTable.setString(2, liste.get(i).getFornavn());
                psInitTable.setString(3, liste.get(i).getEtternavn());
                psInitTable.executeUpdate();
            }
            forbindelse.commit();
            /* Transaksjon slutt */
            ok = true;
        } catch (SQLException e) {
            Opprydder.rullTilbake(forbindelse);
            Opprydder.skrivMelding(e, "slettTabell()");
        } finally {
            Opprydder.settAutoCommit(forbindelse);
            Opprydder.lukkSetning(psInitTable);
        }
        lukkForbindelse();
        return ok;
    }
}
