package simpleParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import simpleParser.parser.BundeslaenderMap;

class BundeslaenderMapTest {

  BundeslaenderMap bm = new BundeslaenderMap();

  /* ************************************ GOOD TESTS ************************************
   * These tests cover situations where everything is fine - the data is complete and method calls are executed correctly.
   */

  @Test
  void getterReturnsListOfAllBundeslaender() {
    int size = bm.getBundeslandMap().size();
    assertEquals(16, size);
  }

  @Test
  void enteringRightCodeReturnsString() {
    String code = BundeslaenderMap.getBundeslandName("01");
    assertEquals("DE-SH", code);
  }

  /* ************************************ BAD TESTS ************************************
   * These tests cover situations where something is wrong.
   */
  @Test
  void enteringWrongCodeReturnsDE() {
    String code = BundeslaenderMap.getBundeslandName("42");
    assertEquals("DE", code);
  }

  @Test
  void enteringEmptyCodeReturnsDE() {
    String code = BundeslaenderMap.getBundeslandName(null);
    assertEquals("DE", code);
  }
}
