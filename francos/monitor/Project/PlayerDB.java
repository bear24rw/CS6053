// PlayerDB.java                                           -*- Java -*-
//    A class for Player Database
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/PlayerDB.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: PlayerDB.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.8  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.7  1998/12/02 07:35:57  bkuhn
//   -- moved so that Economy didn't need to be static in Wealth
//
// Revision 0.6  1998/11/30 10:39:06  bkuhn
//   -- added DHKey for encryption
//    -- made it serializable
//
// Revision 0.5  1998/11/23 03:32:06  bkuhn
//   -- added a method, getRandomPlayerWithHostPort(), for the
//      RANDOM_PARTICIPANT_HOST_PORT command to use
//
// Revision 0.4  1998/11/15 17:51:29  bkuhn
//   -- minor change on what methods are synchronized
//
// Revision 0.3  1998/11/13 07:15:25  bkuhn
//   -- added method getPlayers()
//
// Revision 0.2  1998/11/03 06:15:58  bkuhn
//   # initial version
//

import java.util.*;
import java.security.*;
import java.io.*;
/*****************************************************************************/
class PlayerDB implements Serializable { 
   static SecureRandom random = new SecureRandom();

   Hashtable players;
   DHKey dhKey;
   Economy economy;

   /*********************************************************************/
   PlayerDB() {
      economy = new Economy();
      players = new Hashtable();

      try {
         FileInputStream fis = new FileInputStream(
               GameParameters.GAME_DIRECTORY + "/" + 
               GameParameters.DH_KEY_FILE);
         ObjectInputStream ois = new ObjectInputStream(fis);

         dhKey = (DHKey) ois.readObject();
      } catch (Exception e) {
         Date date = new Date();
         String ss = date.toString();
         System.out.println(ss+": FATAL: Attempting to read DH FIle: " +
                            GameParameters.GAME_DIRECTORY + "/" + 
                            GameParameters.DH_KEY_FILE + " : " + e);
      }
   }
   /*********************************************************************/
   Economy getEconomy() {  return economy;  }
   /*********************************************************************/
   synchronized void add(Player p) {
      players.put(p.getIdentity(), p);
   }
   /*********************************************************************/
   /* lookup looks up the Player with identity, identity.  Returns
   ** that player, or null if no player is found
   */
   Player lookup(String identity) { return ((Player)players.get(identity)); }
   /*********************************************************************/
   DHKey getDHKey() {  return dhKey;  }
   /*********************************************************************/
   Enumeration getPlayers() {  return players.elements();  }
   /*********************************************************************/
   Player getRandomPlayerWithHostPort(Player exceptThisOne) {
      int count = players.size();

      Player list[] = new Player[count];
      int ii = 0;
         
      for (Enumeration e = players.elements() ; e.hasMoreElements() ; ii++)
         list[ii] =  (Player) e.nextElement();

      int tries;
      int loc = 0;
      for (tries = 0; tries < 10 ; tries++) {
         loc = random.nextInt();
         
         if (loc < 1) loc = - loc;
         loc %= count;
         
         if (list[loc] != exceptThisOne && list[loc].getHostName() != null
             && list[loc].getPort() > 0)
            break;
      }

      if (tries < 10)
         return list[loc];
      else
         return null;
   }
   /*********************************************************************/
   /* lookupOrCreate processes the IdentCommand given, to find the
   ** identity of the player.  It then looks up the player, creating
   **  that player if they don't already exist.
   **  The player will be returned.  In case of error, null is returned.
   */
   Player lookupOrCreate(IdentCommand identCommand) {
      Player player   = null;
      String identity = identCommand.getIdent();

      if (identity != null) {
         player = (Player) players.get(identity);
         if (player == null &&
             (! GameParameters.PARTICIPANT_DATABASE_IS_STATIC) ) {
            add(player = new Player(identity, economy));
         }
      }
      return player;
   }
}
