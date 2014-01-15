// Economy.java                                                -*- Java -*-
//     The economy of the entire system
//
// COPYRIGHT (C) 1998, Bradley M. Kuhn
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of
// the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
//
// Written   :   Bradley M. Kuhn         University of Cincinnati
//   By          
//
// Written   :   John Franco
//   For         Special Topics: Java Programming
//               15-625-595-001, Fall 1998
// RCS       :
//
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/Economy.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: Economy.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.11  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.10  1998/12/02 07:37:52  bkuhn
//   -- commenting changes
//
// Revision 0.8  1998/11/30 09:13:17  bkuhn
//   -- added holders to make sure we always have enough folk holding
//      a given resource
//   -- moved stuff that updatedStatistics() was printing to printStatistics()
//
// Revision 0.7  1998/11/29 23:24:21  bkuhn
//   -- decreased Economy max out value from 1000000 to 10000
//
// Revision 0.6  1998/11/29 12:01:49  bkuhn
//   # changed a few things from int to long
//   -- changed updateStatistics so it now writes a file and has
//      a reasonable algorithm
//   -- write trade() so that Players can now trade with the Monitor
//
// Revision 0.5  1998/11/25 04:36:01  bkuhn
//   -- made it so that someone is never given more than half of thier
//      allotment to a single resource
//
// Revision 0.4  1998/11/18 07:51:45  bkuhn
//   # moved ALLOTMENT_AMOUNT_TO_GIVE constant to GameParameters
//
// Revision 0.3  1998/11/16 07:47:24  bkuhn
//   -- got getNewDistributionVector() working
//   -- fixed typo in hash table building in constructor
//
// Revision 0.2  1998/11/15 17:12:02  bkuhn
//   # began working on minor changes that aren't doing much yet
//
// Revision 0.1  1998/11/13 10:57:19  bkuhn
//   # initial version
//

import java.util.*;
import java.security.*;
import java.io.*;

/*****************************************************************************/
class Economy implements Serializable {
   Hashtable market;
   Hashtable holders;

   static SecureRandom random = new SecureRandom();

   /*********************************************************************/
   Economy() {
      market = new Hashtable(10);
      holders = new Hashtable(10);

      market.put("rupyulars", new ResourceValue("rupyulars"));

      /***
      market.put("oil", new ResourceValue("oil"));
      market.put("steel", new ResourceValue("steel"));
      market.put("plastic", new ResourceValue("plastic"));
      market.put("copper", new ResourceValue("copper"));
      market.put("glass", new ResourceValue("glass"));
      market.put("rubber", new ResourceValue("rubber"));
      ***/

      for (Enumeration e = market.keys() ; e.hasMoreElements() ;)
         holders.put(e.nextElement(), new Integer(0));

      /***
      double newValue = GameParameters.VALUE_OF_NON_EXISTENT_RESOURCE;
      market.put("weapons", new ResourceValue("weapons",newValue));
      market.put("computers", new ResourceValue("computers",newValue));
      market.put("vehicles", new ResourceValue("vechicles",newValue));
      ***/
   }
   /*********************************************************************/
   public Enumeration getResourceNames() {  return market.keys();  }
   /*********************************************************************/
   public ResourceValue getResourceValueByName(String name) {
      return (ResourceValue) market.get(name);
   }
   /*********************************************************************/
   void increaseHolder(String key, int amount) {
      holders.put(key, new Integer(((Integer)holders.get(key)).intValue() + 
                                   amount));
   }
   /*********************************************************************/
   boolean reasonableHoldings() {
      for (Enumeration e = holders.keys() ; e.hasMoreElements() ;) {
         String currentHolding = (String) e.nextElement();

         if ( ((Integer) holders.get(currentHolding)).intValue() < 
              GameParameters.MINIMUM_PLAYERS_THAT_MUST_HOLD_EACH_RESOURCE)
            return false;
      }
      return true;
   }
   /*********************************************************************/
   public Hashtable getNewDistributionHash() {
      Hashtable distribution = new Hashtable(holders.size());
      String[] names =  new String[holders.size()];
      Enumeration e = holders.keys();

      String firstOne = null;

      for (int ii = 0; e.hasMoreElements() ; ii++) {
         names[ii] = (String) e.nextElement();
         distribution.put(names[ii], new Long(0));
         
         int holderCount =  ((Integer)holders.get(names[ii])).intValue();

         if (holderCount < 
             GameParameters.MINIMUM_PLAYERS_THAT_MUST_HOLD_EACH_RESOURCE) {
            if (firstOne == null)
               firstOne = names[ii];
            else {
               if (random.nextDouble() <= 0.50) firstOne = names[ii];
            }
         }
      }

      long remainingAllotment = GameParameters.ALLOTMENT_AMOUNT_TO_GIVE;

      /*** JVF
      while (remainingAllotment > 0) {
      ***/ 
         String currentHolding;
         /*** JVF
         do {
         ***/
            if (firstOne != null) {
               currentHolding = firstOne;
               firstOne = null;
            } else {
               /*** JVF
               int ii = random.nextInt();
               if (ii < 0) ii = - ii;
                  
               ii %= names.length;
               currentHolding = names[ii];
               ***/
               currentHolding = names[0];
            }
            /***
         } while(((Long) distribution.get(currentHolding)).longValue()
                 >= GameParameters.ALLOTMENT_AMOUNT_TO_GIVE / 2);
            ***/

         // Now, pick a random amount to give them.
         long oldValue  = ((Long) distribution.get(currentHolding))
                                .longValue();

         long amount = GameParameters.ALLOTMENT_AMOUNT_TO_GIVE;
         /*** JVF
         do {
            amount = random.nextLong();
            if (amount < 0) amount = -amount;
            amount %= remainingAllotment + 1;
            System.out.println("Amount:"+amount+" oldval:"+oldValue);
         } while (amount + oldValue
                  >= GameParameters.ALLOTMENT_AMOUNT_TO_GIVE / 2);
         // never give more than half total in one thing
         remainingAllotment -= amount;
         ***/

         distribution.put(currentHolding, new Long(amount + oldValue));
         /*** JVF
      }          
         ***/  
      return distribution;
   }
   /**********************************************************************/
   private double getRandomDoubleBetween(double min, double max) {
      double rand = random.nextDouble();
      if (rand < 0) rand = - rand;
      return ( (rand * (max - min) ) + min );
   }
   /*********************************************************************/
   public synchronized void updateStatistics() {
      long rupyulars =  
         ((ResourceValue) market.get("rupyulars")).getAmount();
      
      if (rupyulars == 0) rupyulars = 1;
      
      /*** JVF
      for (Enumeration e = market.keys() ; e.hasMoreElements() ;) {
         String key = (String) e.nextElement();
         
         if (key.equalsIgnoreCase("rupyulars")) continue;
         
         ResourceValue rv = (ResourceValue) market.get(key);

         long amount = rv.getAmount();

         double newValue;

         boolean commod = false;
         //System.out.println("Economomy: ["+key+"]");
         if (key.equalsIgnoreCase("computers")) commod = true;
         if (key.equalsIgnoreCase("vehicles")) commod = true;
         if (key.equalsIgnoreCase("weapons")) commod = true;
				
         if (commod)
            newValue =  GameParameters.VALUE_OF_NON_EXISTENT_RESOURCE;
         else
            newValue = 1.0;
				
         if (amount > 0) {
            double change = ( ((double)rupyulars) /  amount)
               - rv.getMarketValue();

            double delta = getRandomDoubleBetween(0.1 * change, change);

            newValue = rv.getMarketValue() + delta;
         }
         rv.setMarketValue(newValue);
      }
      ***/ 
   }
   /*********************************************************************/
   public synchronized void printStatistics(PrintStream file) {
      if (file == null) return;
      
      file.println("ECONOMY STATISTICS");

      long rupyulars = ((ResourceValue) market.get("rupyulars")).getAmount();

      file.println("     rupyulars: " + " count = " + rupyulars +
                   ", value = 1.00000");

      for (Enumeration e = market.keys() ; e.hasMoreElements() ;) {
         String key = (String) e.nextElement();

         if (key.equalsIgnoreCase("rupyulars")) continue;

         ResourceValue rv = (ResourceValue) market.get(key);

         file.println("     " + key + ": " + " count = " + rv.getAmount() +
                      ", value = " + rv.getMarketValue() +
                      ", monitorHolds = " + rv.getMonitorAmount());
      }
   }         
   /*********************************************************************/
   // This methods decreases 
   // names of resources
   synchronized boolean trade(Player player, long amountFrom)
      throws UnknownResourceException {
      String resourceTo = "rupyulars";
      
      ResourceValue rvDesired = (ResourceValue) market.get(resourceTo);

      if (rvDesired == null)
         throw new UnknownResourceException(resourceTo +
                                            " is not a known resource");

      if (rvDesired.getMonitorAmount() < amountFrom)
         return false;

      double valueOfDesired = amountFrom * rvDesired.getMarketValue();
 
      try {
         player.getWealth().changeHolding("rupyulars", 0 - amountFrom);
         rvDesired.changeMonitorAmount(amountFrom);
      } catch (InsufficientResourceException ire) {
         Date date = new Date();
         String ss = date.toString();
         System.out.println(ss+": FATAL MONTIOR TRADE ERROR:  (" + 
                            ire.getMessage() + ")");
      }
      return true;
   }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
