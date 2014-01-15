// War.java                                                        -*- Java -*-
//    War class
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/War.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: War.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.5  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.4  1998/11/30 08:59:07  bkuhn
//   -- made it Serializable and added startWarThread() so when we
//      come back off disk, I can restart wars
//
// Revision 0.3  1998/11/29 19:58:55  bkuhn
//   # added a comment
//
// Revision 0.2  1998/11/29 12:02:11  bkuhn
//   -- added truce() method and endWarThread() method for truce()
//      and other methods that end the war to use
//
// Revision 0.1  1998/11/25 10:45:28  bkuhn
//   # initial version
//
//

import java.security.SecureRandom;
import java.util.*;
import java.io.Serializable;

/*****************************************************************************/
class War implements Serializable
{
      static final String rcsid = "$Revision: 1.1 $";

      PlayerDB playerDB;
      Player attacker, victim;
      int attackerWeapons, attackerVehicles;
      int victimWeapons, victimVehicles;
      boolean warOver;
      String winnerIdent;
      int battleCount;
      RecurringEvent recurringEvent;

      static SecureRandom random = new SecureRandom();

      /**********************************************************************/
      War(PlayerDB pdb, Player initiator, int weap, int veh, Player vict)
      {
         playerDB = pdb;
         attacker = initiator;
         victim = vict;
         attackerWeapons = weap;
         attackerVehicles = veh;

         warOver = false;
         battleCount = 0;

         winnerIdent = GameParameters.NO_WAR_WINNER;

         recurringEvent = null;

         findDefense();
      }
      /**********************************************************************/
      PlayerDB getPlayerDB()
      {
         return playerDB;
      }
      /**********************************************************************/
      Player getAttacker()
      {
         return attacker;
      }
      /**********************************************************************/
      Player getVictim()
      {
         return victim;
      }
      /**********************************************************************/
      boolean isOver()
      {
         return warOver;
      }
      /**********************************************************************/
      String getWarStatus(Player p)
      {
         String message = "aggressor " + attacker.getIdentity() +
                          " defender " + victim.getIdentity() +
                          " winner " + winnerIdent + " battles " +
                          battleCount + " status ";

         if (warOver)
            message = message + " COMPLETED ";
         else
            message = message + "ACTIVE ";
         
         if (p == attacker)
            return (message + getAttackerStatus());
         else if (p == victim)
            return (message + getVictimStatus());
         else
            return new String("FATAL WAR ERROR: "  +
                            " post to java-project@ebb.org (unknown player:" +
                              p.getIdentity()  + ")");
      }
      /**********************************************************************/
      private String getAttackerStatus()
      {
         return("weapons " + attackerWeapons + " vehicles " +
                attackerVehicles);
      }
      /**********************************************************************/
      private String getVictimStatus()
      {
         return("weapons " + victimWeapons + " vehicles " +
                victimVehicles);
      }
      /**********************************************************************/
      private void findDefense()
      {
         WarDefendConnection defendConnection = null;
         try
         {
            defendConnection = new WarDefendConnection(playerDB, victim,
                                                       " " + 
                                                       attacker.getIdentity());
            defendConnection.start();

            try { defendConnection.join(); }
            catch(InterruptedException ie) {}
         }
         catch (MonitorSessionException mse)
         {
            if (defendConnection != null &&
                defendConnection.isAlive())
               defendConnection.stop();
         }
         if (defendConnection != null)
         {
            victimWeapons = defendConnection.getWeapons();
            victimVehicles  = defendConnection.getVehicles();
         }
      }
      /**********************************************************************/
      synchronized String getWinner()
      {
         return new String(winnerIdent);
      }
      /**********************************************************************/
      private synchronized void processAttack()
      {
         int victimIndex = (victimWeapons > victimVehicles) ? 
                              victimWeapons : victimVehicles;
         int attackerIndex = (attackerWeapons > attackerVehicles) ? 
                              attackerWeapons : attackerVehicles;

         double percentChanceVictimWins = (double) victimIndex /
                                       (double) (victimIndex + attackerIndex);

         double victimMin, attackerMin, victimMax, attackerMax;

         double rand = random.nextDouble();
         if (rand < 0) rand = - rand;

         if (rand  <= percentChanceVictimWins)
         {
            victimMin = GameParameters.WINNER_BATTLE_PERCENT_LOST_MIN;
            victimMax = GameParameters.WINNER_BATTLE_PERCENT_LOST_MAX;
            attackerMin = GameParameters.LOSER_BATTLE_PERCENT_LOST_MIN;
            attackerMax = GameParameters.LOSER_BATTLE_PERCENT_LOST_MAX;
         }
         else 
         {
            victimMin = GameParameters.LOSER_BATTLE_PERCENT_LOST_MIN;
            victimMax = GameParameters.LOSER_BATTLE_PERCENT_LOST_MAX;
            attackerMin = GameParameters.WINNER_BATTLE_PERCENT_LOST_MIN;
            attackerMax = GameParameters.WINNER_BATTLE_PERCENT_LOST_MAX;
         }

         victimWeapons -= victimWeapons *
            getRandomDoubleBetween(victimMin, victimMax);
         if (victimWeapons < 0) victimWeapons = 0;

         victimVehicles -= victimVehicles *
            getRandomDoubleBetween(victimMin, victimMax);
         if (victimVehicles < 0) victimVehicles = 0;

         attackerWeapons -= attackerWeapons *
            getRandomDoubleBetween(attackerMin, attackerMax);
         if (attackerWeapons < 0) attackerWeapons = 0;

         attackerVehicles -= attackerVehicles *
            getRandomDoubleBetween(attackerMin, attackerMax);
         if (attackerVehicles < 0) attackerVehicles = 0;
      }
      /**********************************************************************/
      synchronized boolean executeBattle()
      {
         if (warOver)
            return true;

         battleCount++;

         processAttack();

         boolean victimWins = false, 
                 attackerWins = false;

         if (victimVehicles == 0 || victimWeapons == 0)
            attackerWins = true;

         if (attackerVehicles == 0 || attackerWeapons == 0)
            victimWins = true;

         if (attackerWins && victimWins)
         {
            if (attackerVehicles >= victimVehicles &&
                attackerWeapons >= victimWeapons)
               victimWins = false;
            else if (victimVehicles >= attackerVehicles &&
                     victimWeapons >= attackerWeapons)
               attackerWins = false;
            else 
            {
               // Well, the are so evenly matched....We just have to flip
               // a coin.

               double rand = random.nextDouble();
               if (rand < 0) rand = - rand;

               if (rand < 0.5)
                  attackerWins = false;
               else
                  victimWins = false;
            }
         }
         // Note that whoBeatWhom must be called LAST, as we stop the thread!
         if (attackerWins)
            whoBeatWhom(attacker, victim);
         else if (victimWins)
            whoBeatWhom(victim, attacker);

         return warOver;
      }
      /**********************************************************************/
      private double getRandomDoubleBetween(double min, double max)
      {
         double rand = random.nextDouble();
         if (rand < 0) rand = - rand;
         return ( (rand * (max - min) ) + min );
      }
      /**********************************************************************/
      void setRecurringEvent(RecurringEvent re)
      {
         recurringEvent = re;
      }
      /**********************************************************************/
      synchronized void whoBeatWhom(Player winner, Player loser)
      {
         winnerIdent = winner.getIdentity();
         winner.wonWar();
         loser.lostWar();

			Date date = new Date();
			String s = date.toString();
			System.out.println(s+":");
         System.out.println("WAR_OUTCOME:     " + winnerIdent + " has " +
                            "defeated " + loser.getIdentity());
         try
         {
            winner.getWealth().
               changeHolding("weapons", victimWeapons + attackerWeapons);
            winner.getWealth().
               changeHolding("vehicles", victimVehicles + attackerVehicles);

            victimWeapons = attackerWeapons =
               victimVehicles = attackerVehicles = 0;
         }
         catch (UnknownResourceException ure)
         {
			   date = new Date();
			   s = date.toString();
			   System.out.println(s+":");
            System.out.println("FATAL DECLAR WAR ERROR:  (" + 
                               ure.getMessage() + ")");
         }
         catch (InsufficientResourceException ire)
         {
			date = new Date();
			s = date.toString();
			System.out.println(s+":");
				
            System.out.println("FATAL DECLAR WAR ERROR:  (" + 
                               ire.getMessage() + ")");
         }
         double percent = getRandomDoubleBetween(
                              GameParameters.PILLAGE_PERCENT_MIN,
                              GameParameters.PILLAGE_PERCENT_MIN);

         for (Enumeration e = loser.getWealth().getResourceNames() ;
              e.hasMoreElements() ;)
         {
            String key = (String) e.nextElement();

            try 
            {
               int amountToTransfer = (int)
                                 (loser.getWealth().getHolding(key) * percent);
               loser.getWealth().changeHolding(key, - amountToTransfer);
               winner.getWealth().changeHolding(key, amountToTransfer);
            }
            catch (UnknownResourceException ure)
            {
			      date = new Date();
			      s = date.toString();
			      System.out.println(s+":");
               System.out.println("FATAL DECLAR WAR ERROR:  (" + 
                                  ure.getMessage() + ")");
            }
            catch (InsufficientResourceException ire)
            {
			      date = new Date();
			      s = date.toString();
			      System.out.println(s+":");
               System.out.println("FATAL DECLAR WAR ERROR:  (" + 
                                  ire.getMessage() + ")");
            }
         }
         warOver = true;
         endWarThread();
      }
      /**********************************************************************/
      synchronized boolean truce(Hashtable resources, Player receiver,
                                 Player giver)
         throws InsufficientResourceException, UnknownResourceException
      {
         if (warOver)
            return false;

         for (Enumeration e = resources.keys() ; e.hasMoreElements() ;)
         {
            String curResource = (String) e.nextElement();
            int    value       = ( (Integer) resources.get(curResource) )
                                           .intValue();

            giver.getWealth().changeHolding(curResource, 
                                                 0 - value);
            receiver.getWealth().changeHolding(curResource, value);
         }
         try
         {
            victim.getWealth().
               changeHolding("weapons", victimWeapons);
            victim.getWealth().
               changeHolding("vehicles", victimVehicles);

            attacker.getWealth().
               changeHolding("weapons", attackerWeapons);
            attacker.getWealth().
               changeHolding("vehicles", attackerVehicles);

            victimWeapons = attackerWeapons =
               victimVehicles = attackerVehicles = 0;
         }
         catch (UnknownResourceException ure)
         {
			Date date = new Date();
			String s = date.toString();
			System.out.println(s+":");
				
            System.out.println("FATAL DECLAR WAR ERROR:  (" + 
                               ure.getMessage() + ")");
         }
         catch (InsufficientResourceException ire)
         {
			Date date = new Date();
			String s = date.toString();
			System.out.println(s+":");
				
            System.out.println("FATAL DECLAR WAR ERROR:  (" + 
                               ire.getMessage() + ")");
         }


         receiver.truceWar();
         giver.truceWar();

         winnerIdent = GameParameters.WAR_TRUCE_WINNER;
         warOver = true;

			Date date = new Date();
			String s = date.toString();
			System.out.println(s+":");
			
         System.out.println("WAR_OUTCOME:     " + giver.getIdentity() +
                            " has negotiated a truce with " +
                            receiver.getIdentity());
         endWarThread();

         return true;
      }
      /**********************************************************************/
      void startWarThread()
      {
         // This includes stoping our own thread
         if (recurringEvent != null && (! recurringEvent.isAlive()) )
            recurringEvent.start();
      }
      /**********************************************************************/
      void endWarThread()
      {
         // This includes stoping our own thread
         if (recurringEvent != null && recurringEvent.isAlive())
            recurringEvent.stop();
      }
      /**********************************************************************/
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
