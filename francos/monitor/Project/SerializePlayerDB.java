// SerializePlayerDB.java                                         -*- Java -*-
//    Serialize the Player database periodically
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/SerializePlayerDB.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: SerializePlayerDB.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.3  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.2  1998/12/02 03:03:47  bkuhn
//    -- increased this thread's priority
//
// Revision 0.1  1998/11/30 11:22:29  bkuhn
//   # initial version
//


import java.util.*;
import java.io.*;
/*****************************************************************************/
class SerializePlayerDB extends Thread implements RecurredEvent
{
      PlayerDB playerDB;
      static final String rcsid = "$Revision: 1.1 $";

      SerializePlayerDB()
      {
      }
      /**********************************************************************/
      public void initialize(Object o)
      {
         playerDB = (PlayerDB) o;
      }
      /**********************************************************************/
      public void run()
      {
         Player curPlayer;

         setPriority(MAX_PRIORITY - 1);
			Date date = new Date();
			String s = date.toString();
			System.out.println(s+":");
			
         System.out.println("SERIALIZE_PARTCIPANT_DB: Beginning...");

         // First, if we have any players, update the economy
         for(Enumeration e = playerDB.getPlayers(); e.hasMoreElements() ; )
         {
            ((Player) e.nextElement()).getEconomy().updateStatistics();
            break;
         }

         try
         {
            FileOutputStream fos = new FileOutputStream(
               GameParameters.GAME_DIRECTORY + "/" + 
               GameParameters.PARTICIPANT_DB_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject( (Object) playerDB);
            oos.close();
            fos = new FileOutputStream(
               GameParameters.GAME_DIRECTORY + "/" + 
               GameParameters.PARTICIPANT_DB_FILE + ".bak");
            oos = new ObjectOutputStream(fos);
            oos.writeObject( (Object) playerDB);
            oos.close();
         }
         catch (Exception e)
         {
				date = new Date();
				s = date.toString();
            System.out.println(s+": SERIALIZE_PARTICIPANT_DB: FATAL ERROR---" + e);
         }
			date = new Date();
			s = date.toString();
			System.out.println(s+":");
			
         System.out.println("SERIALIZE_PARTICIPANT_DB: Ending+++");
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
