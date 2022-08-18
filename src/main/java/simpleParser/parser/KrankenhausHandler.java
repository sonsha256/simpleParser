package simpleParser.parser;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KrankenhausHandler extends DefaultHandler {

  /* Some Standorte are simply called Hauptstandort. That doesn't tell much.
   * So, we're also using an alias, which we get from the Krankenhaus,
   * that is referenced via an IK in the Standort-Element. The IK isn't really
   * helpful for humans, either, so we're using the IK to find the name of the Krankenhaus
   * within the Krankenhaus-Element.
   *
   * So what exactly happens:
   * 1. We extract each IK and the according Krankenhaus-name
   * 2. This information is put into a map that can be used by the StandortHandler to get the alias.
   */

  // ***************** variables *****************

  // These are the XML-tag-names we're working with.
  private static final String KRANKENHAUS_ELEMENT = "Krankenhaus";
  private static final String HAUPT_IK_ELEMENT = "HauptIK";
  private static final String IK_ELEMENT = "IK";
  private static final String BEZEICHNUNG_ELEMENT = "Bezeichnung";
  private static final String STANDORT_ELEMENT = "Standort";
  private static final String EINRICHTUNG_ELEMENT = "Einrichtung";

  // If the parser enters a field, the value changes to true. So these booleans basically tell us
  // where we  are in the xml-file.
  private boolean isKrankenhaus = false;
  private boolean isHauptIK = false;
  private boolean isIK = false;
  private boolean isBezeichnung = false;
  private boolean isStandort = false;
  private boolean isEinrichtung = false;

  // This is where the values are temporarily stored.
  private String ik;
  private String bezeichnung;

  /** That's the HashMap where everything comes together and can be further used. */
  private HashMap<String, String> hospitalMap = new HashMap<String, String>();

  /** On entering an element it is marked as active by setting the boolean value to true. */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    switch (qName) {
      case KRANKENHAUS_ELEMENT:
        isKrankenhaus = true;
        break;
      case HAUPT_IK_ELEMENT:
        isHauptIK = true;
        break;
      case IK_ELEMENT:
        isIK = true;
        break;
      case BEZEICHNUNG_ELEMENT:
        isBezeichnung = true;
        break;
      case STANDORT_ELEMENT:
        isStandort = true;
        break;
      case EINRICHTUNG_ELEMENT:
        isEinrichtung = true;
        break;
    }
  }

  /** This is where the fun part happens: That data is read in and processed. */
  @Override
  public void characters(char ch[], int start, int length) throws SAXException {

    boolean isOnlyKrankenhaus = (isKrankenhaus && !isStandort && !isEinrichtung);

    // Storing the information
    if (isOnlyKrankenhaus && isHauptIK && isIK) {
      ik = new String(ch, start, length);
    } else if (isOnlyKrankenhaus && isBezeichnung) {
      bezeichnung = new String(ch, start, length);
    }
  }

  /**
   * On leaving the field, the obtained data is stored in the map and the field is marked as
   * inactive.
   */
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    switch (qName) {
      case KRANKENHAUS_ELEMENT:
        addHospital(ik, bezeichnung);
        bezeichnung = "keine Bezeichnung vorhanden";
        ik = null;
        isKrankenhaus = false;
        break;
      case HAUPT_IK_ELEMENT:
        isHauptIK = false;
        break;
      case IK_ELEMENT:
        isIK = false;
        break;
      case BEZEICHNUNG_ELEMENT:
        isBezeichnung = false;
        break;
      case STANDORT_ELEMENT:
        isStandort = false;
        break;
      case EINRICHTUNG_ELEMENT:
        isEinrichtung = false;
        break;
    }
  }

  /**
   * This is why we need this class in the first place: StandortHandler can now access the real name
   * of the Krankenhaus by the IK
   */
  public String getHospitalName(String ik) {

    if (hospitalMap.containsKey(ik)) {
      return hospitalMap.get(ik);
    } else {
      return "IK nicht vorhanden";
    }
  }

  /**
   * Returns the whole map of each IK and its Krankenhaus, so far it's only needed for testing
   * purposes.
   */
  public HashMap<String, String> getHospitalMap() {
    return hospitalMap;
  }

  /**
   * If there is a non-empty IK, the data will be put into the map.
   *
   * @param ik cannot be null, is an identifier of the Krankenhaus
   * @param bezeichnung is the name of the Krankenhaus
   */
  private void addHospital(String ik, String bezeichnung) {
    if (ik != null) {
      hospitalMap.put(ik, bezeichnung);
    }
  }
}
