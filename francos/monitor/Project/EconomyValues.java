import java.util.*;
import java.security.*;
import java.io.*;

public class EconomyValues {
   PlayerDB getPlayerDB(String filename) {
      PlayerDB playerDB = null;

      try {
         FileInputStream fis = new FileInputStream(filename);
         ObjectInputStream ois = new ObjectInputStream(fis);
         playerDB = (PlayerDB) ois.readObject();
         ois.close();
      } catch (Exception e) {
         System.out.println("FATAL ERROR GETTING PLAYER DB: " + e);
      }
		
      return playerDB;
   }

   public String getResult (String filename) {
      try { Thread.sleep(1000); } catch (Exception e) { }
		
      PlayerDB pdb = getPlayerDB(filename);
      Economy economy = pdb.getEconomy();
      double rupyulars 
	 = economy.getResourceValueByName("rupyulars").getMarketValue();
      /***
      double weapons = 
	 economy.getResourceValueByName("weapons").getMarketValue();
      double computers = 
	 economy.getResourceValueByName("computers").getMarketValue();
      double vehicles = 
	 economy.getResourceValueByName("vehicles").getMarketValue();
      double copper = 
	 economy.getResourceValueByName("copper").getMarketValue();
      double oil = 
	 economy.getResourceValueByName("oil").getMarketValue();
      double steel = 
	 economy.getResourceValueByName("steel").getMarketValue();
      double plastic = 
	 economy.getResourceValueByName("plastic").getMarketValue();
      double glass = 
	 economy.getResourceValueByName("glass").getMarketValue();
      double rubber = 
	 economy.getResourceValueByName("rubber").getMarketValue();
      ***/

      return new String ("Conversions: Rupyulars: "+rupyulars/*+*/);
			 /***
			 " Weapons: "+weapons+
			 " Computers: "+computers+
			 " Vehicles: "+vehicles+
			 " Copper: "+copper+
			 " Oil: "+oil+
			 " Steel: "+steel+
			 " Plastic: "+plastic+
			 " Glass: "+glass+
			 " Rubber: "+rubber);
			 ***/
   }
}
