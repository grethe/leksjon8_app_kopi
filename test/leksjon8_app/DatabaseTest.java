/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leksjon8_app;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Grethe
 */
public class DatabaseTest {

    private static String databasenavn = "jdbc:derby://localhost:1527/persondata;user=waplj;password=waplj";
    private static Database instance;

    public DatabaseTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        instance = new Database(databasenavn);
        // initiering databasen 
        instance.slettPersonTabell();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        // klargjøre for neste testkjøring.. - oppretter tabellen slik at prosjektet kan kjøres med data i tabellen (index.xhtml)
      /*  instance.opprettPersonTabell();
         ArrayList<Person> personListe = new ArrayList<Person>();
         personListe.add(new Person(100, "OLE","HANSEN"));
         personListe.add(new Person(101, "ANNE GRETHE","ÅS"));
         personListe.add(new Person(102, "JONNY","HANSEN"));  
         instance.initPersonTabell(personListe);  */
    }

    @Before
    public void setUp() {
        //legge inn kjent data i tabellene
        instance.opprettPersonTabell();
        ArrayList<Person> personListe = new ArrayList<Person>();
        personListe.add(new Person(100, "OLE", "HANSEN"));
        personListe.add(new Person(101, "ANNE GRETHE", "ÅS"));
        personListe.add(new Person(102, "JONNY", "HANSEN"));
        instance.initPersonTabell(personListe);
    }

    @After
    public void tearDown() {
        // tilbakestill 
        //instance.slettPersonTabell(); // resulterer i at tabellen slettes hver gang..
    }
 /**
     * Test of getAllePersoner method, of class Database.
     */
    @Test //(timeout = 250)
    public void testGetAllePersoner() {
        System.out.println("Database: getAllePersoner");
        instance.getAllePersoner();
        ArrayList<Person> expResult = new ArrayList<Person>();
        expResult.add(new Person(100, "OLE","HANSEN"));
        expResult.add(new Person(101, "ANNE GRETHE","ÅS"));
        expResult.add(new Person(102, "JONNY","HANSEN"));               
        ArrayList<Person> result = instance.getAllePersoner();
        assertEquals(expResult, result);  // Husk equals-metode i klassen Person
    }

    /**
     * Test of getPerson method, of class Database.
     */
    @Test
    public void testGetPerson() {
        System.out.println("Database: getPerson");
        int personnr = 100;
        //Database instance = null;
        Person expResult = new Person (100, "OLE", "HANSEN");
        Person result = instance.getPerson(personnr);
        assertEquals(expResult, result);
    }

    /**
     * Test of regPerson method, of class Database.
     */
    @Test
    public void testRegPerson() {
        System.out.println("Database: regPerson");
        Person p1 = new Person(100, "OLA", "HANSEN");
        Person p2 = new Person(103, "Test", "Testus");

        // Prøver å registrere en som er registrert fra før
        boolean expResult1 = false;
        boolean result1 = instance.regPerson(p1);
        assertEquals(expResult1, result1);
        
        // prøv å registrere en ny person som ikke er registrert før
        boolean expResult2 = true;
        boolean result2 = instance.regPerson(p2);
        assertEquals(expResult2, result2);
  //      instance.slettPerson(p2); // tilbakestill i databasen
    }
    
 
//    /**
//     * Test of getAllePersoner method, of class Database.
//     */
//   @Test
//    public void testGetAllePersoner() {
//        System.out.println("getAllePersoner");
//        Database instance = null;
//        ArrayList<Person> expResult = null;
//        ArrayList<Person> result = instance.getAllePersoner();
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPerson method, of class Database.
//     */
//    @Test
//    public void testGetPerson() {
//        System.out.println("getPerson");
//        int personnr = 0;
//        Database instance = null;
//        Person expResult = null;
//        Person result = instance.getPerson(personnr);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of regPerson method, of class Database.
//     */
//    @Test
//    public void testRegPerson() {
//        System.out.println("regPerson");
//        Person p = null;
//        Database instance = null;
//        boolean expResult = false;
//        boolean result = instance.regPerson(p);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of updatePerson method, of class Database.
//     */
//    @Test
//    public void testUpdatePerson() {
//        System.out.println("updatePerson");
//        Person p = null;
//        Database instance = null;
//        boolean expResult = false;
//        boolean result = instance.updatePerson(p);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of slettPerson method, of class Database.
//     */
//    @Test
//    public void testSlettPerson() {
//        System.out.println("slettPerson");
//        Person p = null;
//        Database instance = null;
//        boolean expResult = false;
//        boolean result = instance.slettPerson(p);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of slettPersonTabell method, of class Database.
//     */
//    @Test
//    public void testSlettPersonTabell() {
//        System.out.println("slettPersonTabell");
//        Database instance = null;
//        boolean expResult = false;
//        boolean result = instance.slettPersonTabell();
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of opprettPersonTabell method, of class Database.
//     */
//    @Test
//    public void testOpprettPersonTabell() {
//        System.out.println("opprettPersonTabell");
//        Database instance = null;
//        boolean expResult = false;
//        boolean result = instance.opprettPersonTabell();
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of initPersonTabell method, of class Database.
//     */
//    @Test
//    public void testInitPersonTabell() {
//        System.out.println("initPersonTabell");
//        ArrayList<Person> liste = null;
//        Database instance = null;
//        boolean expResult = false;
//        boolean result = instance.initPersonTabell(liste);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }

}
