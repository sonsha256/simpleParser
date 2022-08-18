package simpleParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import simpleParser.parser.KrankenhausHandler;

class KrankenhausHandlerTest {

  String goodTestPath = "src/test/resources/good/testfile_good.xml";
  String badTestNoBezeichnungPath = "src/test/resources/bad/emptyBezeichnungKrankenhaus.xml";
  String badTestNoIKPath = "src/test/resources/bad/emptyIKKrankenhaus.xml";

  static SAXParser saxParser;
  static SAXParserFactory factory;

  @BeforeAll
  public static void init() throws IOException, SAXException, ParserConfigurationException {
    System.out.println("BeforeAll init() method called");
    factory = SAXParserFactory.newInstance();
    saxParser = factory.newSAXParser();
  }

  /* ************************************ GOOD TESTS ************************************
   * These tests cover situations where everything is fine - the data is complete and method calls are executed correctly.
   */

  @Test
  void mapOfIKAndKrankenhausContainsAllElements()
      throws SAXException, IOException, ParserConfigurationException {
    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(goodTestPath, krankenhausHandler);
    HashMap<String, String> result = krankenhausHandler.getHospitalMap();
    assertEquals(2, result.size());
  }

  @Test
  void enteringIKReturnsNameOfHospital() throws SAXException, IOException {
    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(goodTestPath, krankenhausHandler);

    String result = krankenhausHandler.getHospitalName("123456789");
    assertEquals("Erstes Testhospital", result);

    result = krankenhausHandler.getHospitalName("987654321");
    assertEquals("Zweites Testhospital", result);
  }

  /* ************************************ BAD TESTS ************************************
   * These tests cover situations where something is wrong.
   */

  @Test
  void enteringWrongIKReturnsIKNichtVorhanden() throws SAXException, IOException {
    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(goodTestPath, krankenhausHandler);

    String result = krankenhausHandler.getHospitalName("424242");
    assertEquals("IK nicht vorhanden", result);
  }

  @Test
  void krankenhausWithNoNameReturnsKeineBezeichnungVorhanden() throws SAXException, IOException {
    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(badTestNoBezeichnungPath, krankenhausHandler);

    String result = krankenhausHandler.getHospitalName("987654321");
    assertEquals("keine Bezeichnung vorhanden", result);
  }

  @Test
  void krankenhausWithoutIKIsNotIncluded() throws SAXException, IOException {
    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();
    saxParser.parse(badTestNoIKPath, krankenhausHandler);

    int result = krankenhausHandler.getHospitalMap().size();
    assertEquals(1, result);
  }
}
