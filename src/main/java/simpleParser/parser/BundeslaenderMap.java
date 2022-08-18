package simpleParser.parser;

import java.util.HashMap;

public class BundeslaenderMap {

  public BundeslaenderMap() {}

  /*
   * in accordance with iso 3166-2:de
   */
  private static HashMap<String, String> bundeslandMap =
      new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
          put("01", "DE-SH");
          put("02", "DE-HH");
          put("03", "DE-NI");
          put("04", "DE-HB");
          put("05", "DE-NW");
          put("06", "DE-HE");
          put("07", "DE-RP");
          put("08", "DE-BW");
          put("09", "DE-BY");
          put("10", "DE-SL");
          put("11", "DE-BE");
          put("12", "DE-BB");
          put("13", "DE-MV");
          put("14", "DE-SN");
          put("15", "DE-ST");
          put("16", "DE-TH");
        }
      };

  public HashMap<String, String> getBundeslandMap() {
    return bundeslandMap;
  }

  public static String getBundeslandName(String code) {
    if (bundeslandMap.get(code) != null) return bundeslandMap.get(code);
    return "DE";
  }
}
