// AwardResources.java                                            -*- Java -*-
//    Give players their resources
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/AwardResources.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: AwardResources.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.4  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.3  1998/11/30 08:59:45  bkuhn
//   -- stopped caculating statistics only when Awarding resources.
//      It is now done when the PlayerDB is serialized
//
// Revision 0.2  1998/11/18 03:30:33  bkuhn
//   # cosemetic changes for formatting of output to stdout
//
// Revision 0.1  1998/11/16 08:28:14  bkuhn
//   # initial version
//
//

import java.util.*;
import java.util.Enumeration;

/*****************************************************************************/
class AwardResources extends Thread implements RecurredEvent {
   PlayerDB playerDB;
   static final String rcsid = "$Revision: 1.1 $";

   AwardResources() {   }
   /**********************************************************************/
   public void initialize(Object o) {
      playerDB = (PlayerDB) o;
   }
   /**********************************************************************/
   public void run() {
      Player curPlayer= null;

      Date date = new Date();
      String s = date.toString();
      System.out.println(s+":");

      System.out.println("AWARD_RESOURCES: Beginning...");

      for (Enumeration e = playerDB.getPlayers() ; e.hasMoreElements() ; ) {
         AliveConnection aliveConnection = null;

         curPlayer = (Player) e.nextElement();

         if (curPlayer.awardResources())
            System.out.println("AWARD_RESOURCES:   " +
                               curPlayer.getIdentity()
                               + " was awarded resources.");
         else
            System.out.println("AWARD_RESOURCES:   " +
                               curPlayer.getIdentity()
                               + "\'s resources were given to the Monitor.");
      }
      date = new Date();
      s = date.toString();
      System.out.println(s+":");
      
      System.out.println("AWARD_RESOURCES: Ending...");
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
