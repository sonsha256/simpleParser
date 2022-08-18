package simpleParser.parser;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import helper.UUIDType5;

/** @author sal */
public class StandortHandler extends DefaultHandler {

  public StandortHandler(KrankenhausHandler kh) {
    krankenhausHandler = kh;
  }
  /*
   * So, what happens in the handler?
   * 1. Information is extracted.
   * 2. From that information we create a FHIR-Location.
   * 3. We put that FHIR-Location into an output-type, atm it's an arrayList.
   */

  // ***************** variables *****************
  // These are the XML-tag-names we're working with. Using variables makes life easier and protects
  // the code from typos.
  private static final String KRANKENHAUS_ELEMENT = "Krankenhaus";
  private static final String STANDORT_ELEMENT = "Standort";
  private static final String GUELTIG_VON_ELEMENT = "GültigVon";
  private static final String GUELTIG_BIS_ELEMENT = "GültigBis";
  private static final String LETZTE_AENDERUNG_ELEMENT = "LetzteÄnderung";

  private static final String REF_KH_ELEMENT = "ReferenzKrankenhaus";
  // We're using one sub-element from ReferenzKrankenhaus:
  private static final String REF_KH_IK_ELEMENT = "IK";

  private static final String STANDORT_ID_ELEMENT = "StandortId";
  private static final String BEZEICHNUNG_ELEMENT = "Bezeichnung";

  private static final String GEO_ADRESSE_ELEMENT = "GeoAdresse";
  // We're using these six sub-elements from GeoAdresse:
  private static final String LAENGENGRAD_ELEMENT = "Längengrad";
  private static final String BREITENGRAD_ELEMENT = "Breitengrad";
  private static final String PLZ_ELEMENT = "PLZ";
  private static final String ORT_ELEMENT = "Ort";
  private static final String STRASSE_ELEMENT = "Straße";
  private static final String HAUSNUMMER_ELEMENT = "Hausnummer";

  private static final String BUNDESLAND_ELEMENT = "Bundesland";
  private static final String EINRICHTUNG_ELEMENT = "Einrichtung";

  // If the parser enters a field, the value changes to true. So these booleans basically tell us
  // where we are in the xml-file.
  private boolean isKrankenhaus = false;
  private boolean isStandort = false;
  private boolean isGueltigBis = false;
  private boolean isGueltigVon = false;
  private boolean isLetzteAenderung = false;
  private boolean isReferenzKrankenhaus = false;
  private boolean isRefKHIK = false;
  private boolean isStandortId = false;
  private boolean isBezeichnung = false;
  private boolean isGeoAdresse = false;
  private boolean isLaengengrad = false;
  private boolean isBreitengrad = false;
  private boolean isPLZ = false;
  private boolean isOrt = false;
  private boolean isStrasse = false;
  private boolean isHausnummer = false;
  private boolean isBundesland = false;
  private boolean isEinrichtung = false;

  // The values are temporarily stored and overwritten as soon as a new element of this
  // type is encountered.
  private static Location location;
  private static Address geoAdresse;
  private static LocalDate gueltigBis;
  private static LocalDate gueltigVon;
  private static LocalDate letzteAenderung;

  private static String refKHIK;
  private static String standortId;
  private static String standortIdAsUUID;
  private static String bezeichnung;
  private static String laengengrad;
  private static String breitengrad;
  private static String plz;
  private static String ort;
  private static String strasse;
  private static String hausnummer;
  private static String bundesland;
  private static String alias;

  // ***************** special stuff *****************

  private static KrankenhausHandler krankenhausHandler;

  /** Here is where we put all our locations :) */
  private ArrayList<Location> standortLocationArrayList = new ArrayList<Location>();

  /**
   * If there is a valid location, it will be added to the list.
   *
   * @param location
   */
  private void addLocation(Location location) {
    if (location != null) {
      standortLocationArrayList.add(location);
    }
  }

  /** @return the List of all Standorte that can be used for other purposes */
  public ArrayList<Location> getStandorte() {
    return standortLocationArrayList;
  }

  /** Our data set contains dates that need to be formatted properly. */
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * The date is provided as a String but we want it to be a REAL date.
   *
   * @param date
   * @return proper date format
   */
  private LocalDate dateFormatter(String date) {
    return LocalDate.parse(date, formatter);
  }

  /** We need the current date for checking whether the data is currently active */
  static LocalDate today = LocalDate.now();

  /**
   * Checking whether the current Standort is currently active. Most of the Standort-Elements don't
   * contain a GueltigBis-Element since we're working with the Verzeichnisbruf_aktuell, which is the
   * current data set. Nevertheless some of the Standort-Elements contain this field, so we need to
   * check them. The gueltigVon-Element is included, just in case some of the Elements might not be
   * valid yet.
   *
   * @return true if today is after gueltigVon and before gueltigBis (can also be null)
   */
  private static boolean isCurrentlyActive() {
    return ((gueltigVon.isBefore(today) || gueltigVon.isEqual(today))
        && ((gueltigBis == null) || gueltigBis.isAfter(today) || gueltigBis.isEqual(today)));
  }

  /**
   * The StandortId is also used as an id in the FHIR-Location-type. It needs to be different from
   * the identifier, that's why a name-based UUID is used.This code-part is taken from another
   * project, I'm not sure about the TODO-Part.
   *
   * @param name is the StandortId from the input-data
   * @return a unique identifier
   */
  private static String getNameBasedUUID(String name) {
    // TODO make configurable but still somehow static
    UUID namespaceId = UUID.fromString("7f0e4b29-c12b-49fe-bb9c-8d3e77f25a0e");
    UUID gid = UUIDType5.nameUUIDFromNamespaceAndString(namespaceId, name);
    return gid.toString();
  }

  /**
   * Overwrites data. To be used after putting a new location into the list. In case one of the
   * elements is not given in the input file, it will prevent it from having the same value as the
   * Location before.
   */
  private void resetData() {
    gueltigBis = null;
    gueltigVon = null;
    letzteAenderung = null;

    refKHIK = "";
    standortId = "";
    standortIdAsUUID = "";
    bezeichnung = "keine Angabe";
    laengengrad = "0";
    breitengrad = "0";
    plz = "keine Angabe";
    ort = "keine Angabe";
    strasse = "keine Angabe";
    hausnummer = "keine Angabe";
    bundesland = "DE";
    alias = "keine Angabe";
  }

  /**
   * If you want to test your code the wrong way, you can do it by using this method. It prints the
   * values of the read-in elements as well as the current status, the alias and the generated UUID.
   */
  private static void printStandortInformation() {
    System.out.println("Ist aktiv? " + isCurrentlyActive());
    System.out.println(GUELTIG_BIS_ELEMENT + ": " + gueltigBis);
    System.out.println(GUELTIG_VON_ELEMENT + ": " + gueltigVon);
    System.out.println(LETZTE_AENDERUNG_ELEMENT + ": " + letzteAenderung);
    System.out.println(REF_KH_IK_ELEMENT + ": " + refKHIK);
    System.out.println(STANDORT_ID_ELEMENT + ": " + standortId);
    System.out.println("StandortIdAsUUID: " + standortIdAsUUID);
    System.out.println(BEZEICHNUNG_ELEMENT + ": " + bezeichnung);
    System.out.println("Alias: " + alias);
    System.out.println(LAENGENGRAD_ELEMENT + ": " + laengengrad);
    System.out.println(BREITENGRAD_ELEMENT + ": " + breitengrad);
    System.out.println(PLZ_ELEMENT + ": " + plz);
    System.out.println(ORT_ELEMENT + ": " + ort);
    System.out.println(STRASSE_ELEMENT + ": " + strasse);
    System.out.println(HAUSNUMMER_ELEMENT + ": " + hausnummer);
    System.out.println(BUNDESLAND_ELEMENT + ": " + bundesland);
  }

  /**
   * Processing the information that was read in and using it to generate a new
   * FHIR-Location-Element
   */
  private void setLocationInformation() {

    if (strasse.equals("keine Angabe")) return;
    if (refKHIK.isBlank()) return;
    if (alias.equals("IK nicht vorhanden")) return;
    if (gueltigVon == null) return;

    location = new Location();
    geoAdresse = new Address();

    if (isCurrentlyActive()) {
      location.setStatus(Location.LocationStatus.ACTIVE);
    } else {
      location.setStatus(Location.LocationStatus.INACTIVE);
    }

    Date lastUpdate = Date.valueOf(letzteAenderung);
    location.getMeta().setLastUpdated(lastUpdate);

    location.setManagingOrganization(
        new Reference()
            .setIdentifier(
                new Identifier().setSystem("http://fhir.de/sid/arge-ik/iknr").setValue(refKHIK)));

    location.addIdentifier(
        new Identifier().setSystem("http://fhir.de/sid/dkgev/standortnummer").setValue(standortId));
    location.setId(getNameBasedUUID(standortIdAsUUID));

    location.addAlias(krankenhausHandler.getHospitalName(refKHIK));
    location.setName(bezeichnung);

    Location.LocationPositionComponent position =
        new Location.LocationPositionComponent()
            .setLatitude(Double.parseDouble(breitengrad))
            .setLongitude(Double.parseDouble(laengengrad));
    location.setPosition(position);

    geoAdresse.setPostalCode(plz);
    geoAdresse.setCity(ort);
    geoAdresse.setState(bundesland);
    StringType lineGa = geoAdresse.addLineElement();
    lineGa.setValue(strasse + " " + hausnummer);
    lineGa.addExtension(
        new Extension()
            .setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-streetName")
            .setValue(new StringType().setValue(strasse)));
    lineGa.addExtension(
        new Extension()
            .setUrl("http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-houseNumber")
            .setValue(new StringType().setValue(hausnummer)));
    location.setAddress(geoAdresse);

    location.addType(
        new CodeableConcept(
            new Coding()
                .setSystem("https://demis.rki.de/fhir/CodeSystem/locationType")
                .setCode("hospitalLocation")));
    location.addType(
        new CodeableConcept(
            new Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                .setCode("HOSP")));
    location.setPhysicalType(
        new CodeableConcept(
            new Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/location-physical-type")
                .setCode("si")));
    location.setMode(Location.LocationMode.INSTANCE);

    addLocation(location);
    resetData();
  }

  /** On entering the field, the boolean becomes true, which marks the field as 'active'. */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    switch (qName) {
      case KRANKENHAUS_ELEMENT:
        isKrankenhaus = true;
        break;
      case STANDORT_ELEMENT:
        isStandort = true;
        break;
      case GUELTIG_BIS_ELEMENT:
        isGueltigBis = true;
        break;
      case GUELTIG_VON_ELEMENT:
        isGueltigVon = true;
        break;
      case LETZTE_AENDERUNG_ELEMENT:
        isLetzteAenderung = true;
        break;
      case REF_KH_ELEMENT:
        isReferenzKrankenhaus = true;
        break;
      case REF_KH_IK_ELEMENT:
        isRefKHIK = true;
        break;
      case STANDORT_ID_ELEMENT:
        isStandortId = true;
        break;
      case BEZEICHNUNG_ELEMENT:
        isBezeichnung = true;
        break;
      case GEO_ADRESSE_ELEMENT:
        isGeoAdresse = true;
        break;
      case LAENGENGRAD_ELEMENT:
        isLaengengrad = true;
        break;
      case BREITENGRAD_ELEMENT:
        isBreitengrad = true;
        break;
      case PLZ_ELEMENT:
        isPLZ = true;
        break;
      case ORT_ELEMENT:
        isOrt = true;
        break;
      case STRASSE_ELEMENT:
        isStrasse = true;
        break;
      case HAUSNUMMER_ELEMENT:
        isHausnummer = true;
        break;
      case BUNDESLAND_ELEMENT:
        isBundesland = true;
        break;
      case EINRICHTUNG_ELEMENT:
        isEinrichtung = true;
        break;
    }
  }

  /**
   * The data is read in and stored. Also, for alias, standortIdAsUUID and bundesland some of the
   * stored data is used to generate their values.
   */
  @Override
  public void characters(char ch[], int start, int length) throws SAXException {

    boolean isOnlyStandort = (isStandort && !isKrankenhaus && !isEinrichtung);

    // There is a GeoAdresse and a PostAdresse. We're only using GeoAdresse, but since the tags for
    // PLZ, Ort and Hausnummer are the same in both, we need to exclude the PostAdresse
    // sub-elements.
    boolean isOnlyStandortAndGeoAdresse =
        (isStandort && !isKrankenhaus && !isEinrichtung && isGeoAdresse);

    if (isOnlyStandort && isGueltigVon) {
      gueltigVon = dateFormatter(new String(ch, start, length));

    } else if (isOnlyStandort && isGueltigBis) {
      gueltigBis = dateFormatter(new String(ch, start, length));

    } else if (isOnlyStandort && isLetzteAenderung) {
      letzteAenderung = dateFormatter(new String(ch, start, length));

    } else if (isOnlyStandort && isRefKHIK) {
      refKHIK = new String(ch, start, length);
      alias = krankenhausHandler.getHospitalName(refKHIK);

    } else if (isOnlyStandort && isStandortId) {
      standortId = new String(ch, start, length);
      standortIdAsUUID = getNameBasedUUID(standortId);

    } else if (isOnlyStandort && isBezeichnung) {
      bezeichnung = new String(ch, start, length);

      // Although Straße is called StraßePostfach in the PostAdresse-Version, so there wouldn't be a
      // duplication, we'll use the isOnlyStandortAndGeoAdresse-check, because it fits into the
      // block thematically.
    } else if (isOnlyStandortAndGeoAdresse && isLaengengrad) {
      laengengrad = new String(ch, start, length);

    } else if (isOnlyStandortAndGeoAdresse && isBreitengrad) {
      breitengrad = new String(ch, start, length);

    } else if (isOnlyStandortAndGeoAdresse && isPLZ) {
      plz = new String(ch, start, length);

    } else if (isOnlyStandortAndGeoAdresse && isOrt) {
      ort = new String(ch, start, length);

    } else if (isOnlyStandortAndGeoAdresse && isStrasse) {
      strasse = new String(ch, start, length);

    } else if (isOnlyStandortAndGeoAdresse && isHausnummer) {
      hausnummer = new String(ch, start, length);

      // The given data set uses a numeric code for the Bundeslaender, while HL7 Germany prescribes
      // ISO 2166-2, which consists of a five-character String. That's why we need to translate
      // these codes.
    } else if (isOnlyStandort && isBundesland) {
      bundesland = BundeslaenderMap.getBundeslandName(new String(ch, start, length));
    }
  }

  /**
   * On leaving the field, it is marked as inactive. When leaving Standort, the stored data will be
   * processed before changing the activity status.
   */
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    switch (qName) {
      case KRANKENHAUS_ELEMENT:
        isKrankenhaus = false;
        break;

      case STANDORT_ELEMENT:
        setLocationInformation();
        // activate for low-effort-testing
        // printStandortInformation();
        isStandort = false;
        break;

      case GUELTIG_VON_ELEMENT:
        isGueltigVon = false;
        break;
      case GUELTIG_BIS_ELEMENT:
        isGueltigBis = false;
        break;
      case LETZTE_AENDERUNG_ELEMENT:
        isLetzteAenderung = false;
        break;
      case REF_KH_ELEMENT:
        isReferenzKrankenhaus = false;
        break;
      case REF_KH_IK_ELEMENT:
        isRefKHIK = false;
        break;
      case STANDORT_ID_ELEMENT:
        isStandortId = false;
        break;
      case BEZEICHNUNG_ELEMENT:
        isBezeichnung = false;
        break;
      case GEO_ADRESSE_ELEMENT:
        isGeoAdresse = false;
        break;
      case LAENGENGRAD_ELEMENT:
        isLaengengrad = false;
        break;
      case BREITENGRAD_ELEMENT:
        isBreitengrad = false;
        break;
      case PLZ_ELEMENT:
        isPLZ = false;
        break;
      case ORT_ELEMENT:
        isOrt = false;
        break;
      case STRASSE_ELEMENT:
        isStrasse = false;
        break;
      case HAUSNUMMER_ELEMENT:
        isHausnummer = false;
        break;
      case BUNDESLAND_ELEMENT:
        isBundesland = false;
        break;
      case EINRICHTUNG_ELEMENT:
        isEinrichtung = false;
        break;
    }
  }
}
