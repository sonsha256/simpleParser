package simpleParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Location;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import simpleParser.parser.KrankenhausHandler;
import simpleParser.parser.StandortHandler;

class StandortHandlerTest {

  static SAXParser saxParser;
  static SAXParserFactory factory;

  String goodTestPath = "src/test/resources/good/testfile_good.xml";

  String rangeEmptyBezeichnungPath = "src/test/resources/range/emptyBezeichnungStandort.xml";
  String rangeEmptyBundeslandPath = "src/test/resources/range/emptyBundesland.xml";
  String rangeNoEinrichtungPath = "src/test/resources/range/noEinrichtung.xml";
  String rangeNotActiveAnymorePath = "src/test/resources/range/notActiveAnymore.xml";
  String rangeNotActiveYetPath = "src/test/resources/range/notActiveYet.xml";
  String rangeWrongBundeslandPath = "src/test/resources/range/wrongBundesland.xml";

  String badEmptyGeoadressePath = "src/test/resources/bad/emptyGeoadresse.xml";
  String badEmptyGueltigVonPath = "src/test/resources/bad/emptyGueltigVon.xml";
  String badWrongRefKHIKPath = "src/test/resources/bad/wrongRefKHIK.xml";
  String badNoRefKHIKPath = "src/test/resources/bad/emptyRefKHIK.xml";

  @BeforeAll
  public static void init() throws IOException, SAXException, ParserConfigurationException {
    factory = SAXParserFactory.newInstance();
    saxParser = factory.newSAXParser();
  }

  /* ************************************ GOOD TESTS ************************************
   * These tests cover situations where everything is fine - the data is complete and method calls are executed correctly.
   */

  @Test
  void listContainsAllStandorte() throws IOException, SAXException, ParserConfigurationException {
    String testPath = goodTestPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    int result = standortHandler.getStandorte().size();
    assertEquals(3, result);
  }

  @Test
  void listContainsAllStandorteAsLocations()
      throws IOException, SAXException, ParserConfigurationException {
    String testPath = goodTestPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    ArrayList<Location> result = standortHandler.getStandorte();

    for (int i = 0; i < result.size(); i++) {
      assertEquals(result.get(i).getClass(), Location.class);
    }
  }

  @Test
  void firstLocationContainsAllData()
      throws IOException, SAXException, ParserConfigurationException {
    String testPath = goodTestPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    Location location = standortHandler.getStandorte().get(0);

    String name = location.getName();
    String alias = location.getAlias().get(0).toString();
    String ls = location.getStatus().toString();
    String city = location.getAddress().getCity();
    String postalCode = location.getAddress().getPostalCode();
    String state = location.getAddress().getState();
    String streetAndNumber = location.getAddress().getLine().get(0).toString();
    String longitude = location.getPosition().getLongitude().toString();
    String latitude = location.getPosition().getLatitude().toString();
    String identifier = location.getIdentifier().get(0).toString();
    String id = location.getId();
    CodeableConcept physicalType = location.getPhysicalType();
    CodeableConcept type1 = location.getType().get(0);
    CodeableConcept type2 = location.getType().get(1);
    String mode = location.getMode().toString();
    String lastUpdate = location.getMeta().getLastUpdated().toString();

    assertNotNull(identifier);
    assertNotNull(id);
    assertNotNull(physicalType);
    assertNotNull(type1);
    assertNotNull(type2);
    assertEquals("Teststandort Eins", name);
    assertEquals("Erstes Testhospital", alias);
    assertEquals("ACTIVE", ls);
    assertEquals("Teststadt", city);
    assertEquals("12345", postalCode);
    assertEquals("DE-BE", state);
    assertEquals("Erste Teststraße 42", streetAndNumber);
    assertEquals("13.457398903429", longitude);
    assertEquals("52.434620577035", latitude);
    assertEquals("INSTANCE", mode);
    assertEquals("2022-08-05", lastUpdate);
  }

  @Test
  void secondLocationContainsAllData()
      throws IOException, SAXException, ParserConfigurationException {
    String testPath = goodTestPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    Location location = standortHandler.getStandorte().get(1);

    String name = location.getName();
    String alias = location.getAlias().get(0).toString();
    String ls = location.getStatus().toString();
    String city = location.getAddress().getCity();
    String postalCode = location.getAddress().getPostalCode();
    String state = location.getAddress().getState();
    String streetAndNumber = location.getAddress().getLine().get(0).toString();
    String longitude = location.getPosition().getLongitude().toString();
    String latitude = location.getPosition().getLatitude().toString();
    String identifier = location.getIdentifier().get(0).toString();
    String id = location.getId();
    CodeableConcept physicalType = location.getPhysicalType();
    CodeableConcept type1 = location.getType().get(0);
    CodeableConcept type2 = location.getType().get(1);
    String mode = location.getMode().toString();
    String lastUpdate = location.getMeta().getLastUpdated().toString();

    assertNotNull(identifier);
    assertNotNull(id);
    assertNotNull(physicalType);
    assertNotNull(type1);
    assertNotNull(type2);
    assertEquals("Hauptstandort", name);
    assertEquals("Zweites Testhospital", alias);
    assertEquals("ACTIVE", ls);
    assertEquals("Teststadt Zwei", city);
    assertEquals("54321", postalCode);
    assertEquals("DE-NW", state);
    assertEquals("Zweite Teststraße 24", streetAndNumber);
    assertEquals("7.793447166673", longitude);
    assertEquals("51.761843160121", latitude);
    assertEquals("INSTANCE", mode);
    assertEquals("2020-09-18", lastUpdate);
  }

  @Test
  void thirdLocationContainsAllData()
      throws IOException, SAXException, ParserConfigurationException {
    String testPath = goodTestPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    Location location = standortHandler.getStandorte().get(2);

    String name = location.getName();
    String alias = location.getAlias().get(0).toString();
    String ls = location.getStatus().toString();
    String city = location.getAddress().getCity();
    String postalCode = location.getAddress().getPostalCode();
    String state = location.getAddress().getState();
    String streetAndNumber = location.getAddress().getLine().get(0).toString();
    String longitude = location.getPosition().getLongitude().toString();
    String latitude = location.getPosition().getLatitude().toString();
    String identifier = location.getIdentifier().get(0).toString();
    String id = location.getId();
    CodeableConcept physicalType = location.getPhysicalType();
    CodeableConcept type1 = location.getType().get(0);
    CodeableConcept type2 = location.getType().get(1);
    String mode = location.getMode().toString();
    String lastUpdate = location.getMeta().getLastUpdated().toString();

    assertNotNull(identifier);
    assertNotNull(id);
    assertNotNull(physicalType);
    assertNotNull(type1);
    assertNotNull(type2);
    assertEquals("Teststandort Drei", name);
    assertEquals("Erstes Testhospital", alias);
    assertEquals("INACTIVE", ls);
    assertEquals("Teststadt", city);
    assertEquals("12345", postalCode);
    assertEquals("DE-BE", state);
    assertEquals("Erste Teststraße 44", streetAndNumber);
    assertEquals("Teststadt", city);
    assertEquals("13.457398903429", longitude);
    assertEquals("52.434620577035", latitude);
    assertEquals("INSTANCE", mode);
    assertEquals("2020-08-05", lastUpdate);
  }

  /* ************************************ RANGE TESTS ************************************
   * These tests cover situations where something is not exactly wrong but it's critical
   */

  @Test
  void gueltigVonInFutureReturnsInactive()
      throws IOException, SAXException, ParserConfigurationException {
    String testPath = rangeNotActiveYetPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    ArrayList<Location> result = standortHandler.getStandorte();
    Location.LocationStatus ls = result.get(1).getStatus();
    assertEquals(Location.LocationStatus.INACTIVE, ls);
  }

  @Test
  void gueltigBisInPastReturnsInactive()
      throws IOException, SAXException, ParserConfigurationException {
    String testPath = rangeNotActiveAnymorePath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    ArrayList<Location> result = standortHandler.getStandorte();
    Location.LocationStatus ls = result.get(1).getStatus();
    assertEquals(Location.LocationStatus.INACTIVE, ls);
  }

  @Test
  void emptyBezeichnungInStandortHasMessageButStillHasAlias()
      throws IOException, SAXException, ParserConfigurationException {
    String testPath = rangeEmptyBezeichnungPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    ArrayList<Location> result = standortHandler.getStandorte();

    String bezeichnung = result.get(1).getName();
    assertEquals("keine Angabe", bezeichnung);

    String alias = result.get(1).getAlias().get(0).toString();
    assertEquals("Zweites Testhospital", alias);
  }

  @Test
  void emptyBundeslandJustHasDE() throws IOException, SAXException, ParserConfigurationException {
    String testPath = rangeEmptyBundeslandPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    ArrayList<Location> result = standortHandler.getStandorte();

    String bundesland = result.get(1).getAddress().getState();
    assertEquals("DE", bundesland);
  }

  @Test
  void wrongBundeslandHasOnlyDE() throws IOException, SAXException, ParserConfigurationException {
    String testPath = rangeWrongBundeslandPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    ArrayList<Location> result = standortHandler.getStandorte();

    String bundesland = result.get(1).getAddress().getState();
    assertEquals("DE", bundesland);
  }

  @Test
  void hasNoEinrichtungButIsStillInList()
      throws IOException, SAXException, ParserConfigurationException {
    String testPath = rangeNoEinrichtungPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    int result = standortHandler.getStandorte().size();
    assertEquals(2, result);
  }

  /* ************************************ BAD TESTS ************************************
   * These tests cover situations where something is wrong.
   */

  @Test
  void noGeoadresseIsNotIncluded() throws IOException, SAXException, ParserConfigurationException {
    String testPath = badEmptyGeoadressePath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    int result = standortHandler.getStandorte().size();
    assertEquals(1, result);
  }

  void wrongRefKHIKIsNotIncluded() throws IOException, SAXException, ParserConfigurationException {
    String testPath = badWrongRefKHIKPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    int result = standortHandler.getStandorte().size();
    assertEquals(1, result);
  }

  @Test
  void noGueltigVonNotIncluded() throws IOException, SAXException, ParserConfigurationException {
    String testPath = badEmptyGueltigVonPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    int result = standortHandler.getStandorte().size();
    assertEquals(1, result);
  }

  @Test
  void noRefKHIKIsNotIncluded() throws IOException, SAXException, ParserConfigurationException {
    String testPath = badNoRefKHIKPath;

    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(testPath, krankenhausHandler);
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);
    saxParser.parse(testPath, standortHandler);

    int result = standortHandler.getStandorte().size();
    assertEquals(1, result);
  }
}
