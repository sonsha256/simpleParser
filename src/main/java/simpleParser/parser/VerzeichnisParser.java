package simpleParser.parser;

import java.io.File;
import java.lang.invoke.MethodHandles;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerzeichnisParser {

  // First, we need to know what is needed:
  // 1. We want to read in the verzeichnis-xml-file.
  // 2. We want to extract all the information on the standorte. -> that happens in the handler
  // 3. These extracted information should be mapped onto FHIR-Location-data-types. -> that happens
  // in the handler
  // 4. The FHIR-Location-data-types will be put into an ArrayList, at least that's what has
  // happened in the code before. -> that happens in the handler

  public VerzeichnisParser() {

    // That's just the logger, almost every class has one.
    final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Our parser needs to know where to find the file, that's why we give it a filePath:
    String filePath = "src/main/resources/20220722_Verzeichnisabruf_aktuell.xml";

    // We make a file from the filePath:
    File inputFile = new File(filePath);

    // Also, we add what we want to handle - the Krankenhaus …
    KrankenhausHandler krankenhausHandler = new KrankenhausHandler();

    // Then we want to handle the Standorte …
    StandortHandler standortHandler = new StandortHandler(krankenhausHandler);

    try {

      // We get a SAXParserFactory instance …
      SAXParserFactory factory = SAXParserFactory.newInstance();

      // … that we use to build a SAXParser:
      SAXParser saxParser = factory.newSAXParser();

      // … and that's what we're also parsing:
      saxParser.parse(inputFile, krankenhausHandler);

      // … and parse them, of course.
      saxParser.parse(inputFile, standortHandler);

    } catch (Exception e) {
      LOGGER.warn("Error while parsing input file ('" + filePath + "'): " + e.getMessage());
      LOGGER.warn("The directory will be empty and therefore completely useless.");
    }
  }
}
