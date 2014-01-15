// Wealth.java                                           -*- Java -*-
//    A class to encaspulate all the types of Wealth
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/Wealth.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: Wealth.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.12  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.11  1998/12/02 07:32:03  bkuhn
//   -- moved so that Economy didn't need to be static in Wealth
//
// Revision 0.9  1998/11/30 08:12:45  bkuhn
//   -- made it serializable
//   # FIX ME:  That increaseHOlder() call probably shouldn't be there!
//
// Revision 0.8  1998/11/29 12:02:15  bkuhn
//   # changed some types from int to long
//
// Revision 0.7  1998/11/25 04:44:38  bkuhn
//   -- added getResourceNames() method so one can iterate over the
//      resource names if needed
//
// Revision 0.6  1998/11/18 07:08:50  bkuhn
//   -- fixed the functions that deal with holdings
//   -- filled in the Resource implementations
//   -- creatd synthesize
//
// Revision 0.5  1998/11/16 07:41:01  bkuhn
//   -- set things up to use the new distribution stuff from Economy
//   -- added getEconomy()
//
// Revision 0.4  1998/11/15 17:04:12  bkuhn
//    # working with getting Economy working
//
// Revision 0.3  1998/11/13 10:55:42  bkuhn
//   -- changed the way holdings is initialized
//   -- began work on Economy and daily allotment
//
// Revision 0.2  1998/11/03 06:15:58  bkuhn
//   # initial version
//

import java.util.*;
import java.security.*;
import java.io.Serializable;
/****************************************************************************/
class Wealth implements Serializable {
   Hashtable holdings;
   Economy  economy;
   static SecureRandom random = new SecureRandom();

   /*********************************************************************/
   Wealth(Economy econ) {
      economy = econ;
      holdings = new Hashtable(10);
      Hashtable distribution = economy.getNewDistributionHash();

      // Currency and RawMaterial

      for (Enumeration e = distribution.keys() ; e.hasMoreElements() ; ) {
         String curHolding = (String) e.nextElement();
         long amount = ((Long) distribution.get(curHolding)).longValue();
         if (curHolding.equalsIgnoreCase("rupyulars"))
            holdings.put(curHolding,
                      new Currency(curHolding,
                                   economy.getResourceValueByName(curHolding),
                                   amount));
         else
            holdings.put(curHolding,
                         new RawMaterial(curHolding,
                                   economy.getResourceValueByName(curHolding),
                                   amount));
         if (amount > 0) economy.increaseHolder(curHolding, 1);
      }

      // Products
      /*** JVF
      holdings.put("weapons", new Weapons("weapons", 
                              economy.getResourceValueByName("weapons")));
      
      holdings.put("computers", new Computers("computers", 
                              economy.getResourceValueByName("computers")));
         
      holdings.put("vehicles", new Vehicles("vehicles", 
                              economy.getResourceValueByName("vehicles")));
      ***/
   }
   /*********************************************************************/
   Enumeration getResourceNames() {  return holdings.keys();  }
   /*********************************************************************/
   public Economy getEconomy() {  return economy; }
   /*********************************************************************/
   public synchronized long changeHolding(String holdingType, long amount)
      throws UnknownResourceException, InsufficientResourceException {
      holdingType = holdingType.toLowerCase();
      Resource current = (Resource) holdings.get(holdingType);

      if (current == null)
         throw new UnknownResourceException("Unknown resource, " +
                                            holdingType.toLowerCase());
      return current.changeAmount(amount);
   }
   /*********************************************************************/
   public synchronized long getHolding(String holdingType)
      throws UnknownResourceException {
      holdingType = holdingType.toLowerCase();
      Resource current = (Resource) holdings.get(holdingType);
      
      if (current == null)
         throw new UnknownResourceException("Unknown resource, " +
                                            holdingType);
      return current.getAmount();
   }
   /*********************************************************************/
   public Resource getResourceByName(String holdingType)
      throws UnknownResourceException {
      holdingType = holdingType.toLowerCase();

      Resource current = (Resource) holdings.get(holdingType);
      
      if (current == null)
         throw new UnknownResourceException("Unknown resource, " +
                                            holdingType);
      return current;
   }
   /*********************************************************************/
   public String getHoldingReport() {
      String report = "";
      
      for (Enumeration e = holdings.keys() ; e.hasMoreElements() ;) {
         String key = (String) e.nextElement();
         Resource resource = (Resource) holdings.get(key);
         report = new String(report + key +
                             " " + resource.getAmount() + " ");
      }
      return report;
   }
   /*********************************************************************/
   public void allocateResourcesToOwner() {
      for (Enumeration e = holdings.keys() ; e.hasMoreElements() ;) {
         String key = (String) e.nextElement();
         Resource current = (Resource) holdings.get(key);
         current.allocate();
      }
   }
   /*********************************************************************/
   public void allocateResourcesToMonitor(Date lastAliveCheck) {
      for (Enumeration e = holdings.keys() ; e.hasMoreElements() ;) {
         String key = (String) e.nextElement();
         Resource current = (Resource) holdings.get(key);
         if (current.getResourceValue() == null)
            System.out.println("RESOURCEVALUEs IS  NULL for " + key);
         
         current.getResourceValue()
            .changeMonitorAmount(current.getAwardedAmount());
         if (lastAliveCheck == null) current.decrementAmount();
      }
   }         
   /*********************************************************************/
   /* Synthesize takes a product, and trys to build it.  true is return
   ** iff. the product can be built.  false is returned if the resources
   ** are not available to build the product
   ** NOTE: not very elegant to hard code the cost for everything here.
   */
   public boolean synthesize(String product)
      throws UnknownResourceException, InsufficientResourceException {
      Resource current = (Resource) holdings.get(product);
      
      if (current == null)
         throw new UnknownResourceException("unknown resource, " + 
                                            product);
      return current.synthesize(this);
   }
}

/*****************************************************************************/
abstract class Resource implements Serializable {
   long amount;
   String name;
   ResourceValue resourceValue;
   long awardedPerAllocation;
   /**********************************************************************/
   Resource(String n, ResourceValue rv, long award)
      throws NumberFormatException {
      if (award < 0)
         throw new NumberFormatException(
                       "parameter, award, must be 0 or greater");

      name = new String(n);
      resourceValue = rv;
      amount = 0;
      awardedPerAllocation = award;
   }
   /**********************************************************************/
   Resource(String n, ResourceValue rv, long award, long start)
      throws NumberFormatException {
      this(n, rv, award);
      
      if (start < 0)
         throw new NumberFormatException(
                        "parameter, start, must be 0 or greater");

      try { changeAmount(start);  }
      catch(InsufficientResourceException ire) {}
   }
   /**********************************************************************/
   public String getName() {  return new String(name);  }
   /**********************************************************************/
   protected synchronized void allocate() {
      try { changeAmount(awardedPerAllocation);  }
      catch(InsufficientResourceException ire) {}
   }
   /**********************************************************************/
   protected synchronized ResourceValue getResourceValue() {
      return resourceValue;
   }
   /**********************************************************************/
   public synchronized long getAmount() { return amount;  }
   /***********************************************************************/
   /* changeAmount changes the amount by delta and returns the new amount */
   public synchronized long changeAmount(long delta)
         throws InsufficientResourceException {
      if (amount + delta < 0)
         throw new InsufficientResourceException(name + " holdings have" +
                                                 " been depeleted");

      amount += delta;
      resourceValue.changeAmount(delta);
      return amount;
   }

   public synchronized long decrementAmount() {
      long t = (long)(0.99*amount);
      resourceValue.changeAmount(t);
      amount = t;
      return amount;
   }

   /**********************************************************************/
   protected long getAwardedAmount() {  return awardedPerAllocation; }
   /**********************************************************************/
   public synchronized boolean synthesize(Wealth wealth)
      throws UnknownResourceException, InsufficientResourceException {
      return false;
   }
}

/*****************************************************************************/
class Currency extends Resource {
   /**********************************************************************/
   Currency(String n, ResourceValue rv, long award) {  super(n, rv, award);  }
}

/*****************************************************************************/
class RawMaterial extends Resource {
   /**********************************************************************/
   RawMaterial(String n, ResourceValue rv, long award) { super(n, rv, award); }
}

/*****************************************************************************/
class Weapons extends Resource implements Serializable {
   /**********************************************************************/
   Weapons(String n, ResourceValue rv) { super(n, rv, 0);  }
   /**********************************************************************/
   public boolean synthesize(Wealth wealth)
      throws UnknownResourceException, InsufficientResourceException {
      RawMaterial steel, plastic, oil;
         
      steel = (RawMaterial) wealth.getResourceByName("steel");
      plastic = (RawMaterial) wealth.getResourceByName("plastic");
      oil = (RawMaterial) wealth.getResourceByName("oil");

      long value = GameParameters.CHANGE_AMOUNT_FOR_SYNTESIZE;

      if (steel.getAmount() >= value && plastic.getAmount() >= value &&
          oil.getAmount() >= value) {
         steel.changeAmount(0 - value);
         plastic.changeAmount(0 - value);
         oil.changeAmount(0 - value);
         
         changeAmount(1);
         return true;
      } else 
         throw new InsufficientResourceException("Insufficient resources " +
                                                 "for this operation");
   }
}

/*****************************************************************************/
class Computers extends Resource implements Serializable {
   /**********************************************************************/
   Computers(String n, ResourceValue rv) { super(n, rv, 0); }
   /**********************************************************************/
   public boolean synthesize(Wealth wealth)
      throws UnknownResourceException, InsufficientResourceException {
      RawMaterial copper, plastic, glass;

      copper = (RawMaterial) wealth.getResourceByName("copper");
      plastic = (RawMaterial) wealth.getResourceByName("plastic");
      glass = (RawMaterial) wealth.getResourceByName("glass");

      long value = GameParameters.CHANGE_AMOUNT_FOR_SYNTESIZE;

      if (copper.getAmount() >= value && plastic.getAmount() >= value &&
          glass.getAmount() >= value) {
         copper.changeAmount(0 - value);
         plastic.changeAmount(0 - value);
         glass.changeAmount(0 - value);
         
         changeAmount(1);

         return true;
      } else {
         throw new InsufficientResourceException("Insufficient resources " +
                                                 "for this operation");
      }
   }
}

/*****************************************************************************/
class Vehicles extends Resource implements Serializable {
   /**********************************************************************/
   Vehicles(String n, ResourceValue rv)  {  super(n, rv, 0);  }
   /**********************************************************************/
   public boolean synthesize(Wealth wealth)
      throws UnknownResourceException, InsufficientResourceException {
      RawMaterial steel, rubber, oil, glass;

      steel = (RawMaterial) wealth.getResourceByName("steel");
      rubber = (RawMaterial) wealth.getResourceByName("rubber");
      glass = (RawMaterial) wealth.getResourceByName("glass");
      oil = (RawMaterial) wealth.getResourceByName("oil");

      long value = GameParameters.CHANGE_AMOUNT_FOR_SYNTESIZE;

      if (steel.getAmount() >= value && rubber.getAmount() >= value &&
          glass.getAmount() >= value && oil.getAmount() >= value) {
         steel.changeAmount(0 - value);
         rubber.changeAmount(0 - value);
         glass.changeAmount(0 - value);
         
         changeAmount(1);
         return true;
      } else {
         throw new InsufficientResourceException("Insufficient resources " +
                                                 "for this operation");
      }
   }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
