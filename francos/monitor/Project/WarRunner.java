// WarRunner.java                                            -*- Java -*-
//    Run a war
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/WarRunner.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: WarRunner.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.3  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.2  1998/11/30 10:13:52  bkuhn
//   -- made it serializaable so wars could be too
//
// Revision 0.1  1998/11/25 10:34:53  bkuhn
//   # initial version
//
//

import java.util.*;
import java.io.Serializable;

/*****************************************************************************/
// Note that we Serialize this class....This is  for convience only!
// Make sure you make a new one of these instead when you bring this back in
/*****************************************************************************/
class WarRunner extends Thread implements RecurredEvent, Serializable
{
      War war;
      static final String rcsid = "$Revision: 1.1 $";

      WarRunner()
      {
      }
      /**********************************************************************/
      public void initialize(Object o)
      {
         war = (War) o;
      }
      /**********************************************************************/
      boolean checkAlive(Player player)
      {
         AliveConnection aliveConnection = null;
         try
         {
            aliveConnection = 
               new AliveConnection(player.getInetAddress(),
                                   player.getPort(), war.getPlayerDB(),
                                   player);
            aliveConnection.start();

            try { aliveConnection.join(); }
            catch(InterruptedException ie) { }
            
         }
         catch (MonitorSessionException mse)
         {
            if (aliveConnection != null &&
                aliveConnection.isAlive())
               aliveConnection.stop();
         }

         boolean wasAlive = aliveConnection != null && 
                            aliveConnection.completedNormally();

         player.checkedForLiving(wasAlive);
         return wasAlive;
      }
      /**********************************************************************/
      // Keep in mind:  call to executeBattle() or whoBeatWhom(), if the
      // war is over WILL stop the thread we are currently running in!
      public void run()
      {
         Player victim = war.getVictim();
         Player attacker = war.getAttacker();
			
			Date date = new Date();
			String s = date.toString();
			System.out.println(s+":");

         System.out.println("WAR_RUNNER: Beginning Battle Between..." +
                            attacker.getIdentity() + " against " + 
                            victim.getIdentity());

         if (! checkAlive(victim)) 
         {
				date = new Date();
				s = date.toString();
				System.out.println(s+":");
            System.out.println("WAR_RUNNER:            " +
                               attacker.getIdentity() + " won due to timeout");
            war.whoBeatWhom(attacker, victim);
         }
         else if (! checkAlive(attacker))
         {
				date = new Date();
				s = date.toString();
				System.out.println(s+":");
            System.out.println("WAR_RUNNER:            " +
                               victim.getIdentity() + " won due to timeout");
            war.whoBeatWhom(victim, attacker);
         }
         else 
            war.executeBattle();
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
