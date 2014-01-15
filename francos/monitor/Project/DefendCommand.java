// DefendCommand.java                                              -*- Java -*-
//   Command to defend oneself
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/DefendCommand.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: DefendCommand.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.3  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.2  1998/11/30 03:18:25  bkuhn
//  -- changed things to use new println() method in MonitorSession
//
// Revision 0.1  1998/11/25 09:31:35  bkuhn
//   # initial version
//

import java.util.*;
import java.io.*;
import java.net.*;
/*****************************************************************************/
class DefendCommand extends Command
{
      public static final String COMMAND_STRING = "WAR_DEFEND";

      int weapons, vehicles;
      /**********************************************************************/
      String getCommandMessage()
      {
         return new String(COMMAND_STRING);
      }
      /**********************************************************************/
      int getWeapons()
      {
         return weapons;
      }
      /**********************************************************************/
      int getVehicles()
      {
         return vehicles;
      }
      /**********************************************************************/
      void initialize(String args[]) throws CommandException
      {
         super.initialize(args);

         try
         {
            weapons = Integer.parseInt(arguments[1]);
            vehicles = Integer.parseInt(arguments[2]);

         }
         catch (ArrayIndexOutOfBoundsException abe)
         {
				Date date = new Date();
				String ss = date.toString();
            throw new CommandException(ss+": command, " + arguments[0] + 
             ", requires two arguments, <WEAPON_AMOUNT> and <VEHICLE_AMOUNT>");
         } 
         catch(NumberFormatException ne)
         {
				Date date = new Date();
				String ss = date.toString();
            throw new CommandException(ss+
               ": Weapons and vehicles must be integers.");
         }
         if (weapons < 0 || vehicles < 0)
            throw new CommandException(
               "Computers and vehicles amounts must be positive integers");
      }
      /**********************************************************************/
      public boolean verify(MonitorSession monitorSession)
      {
         try 
         {
            if (monitorSession.getPlayer().getWealth().getHolding("vehicles")
                < vehicles)
            {
               monitorSession.sendError("You have requested to defend " +
                                        "with more vehicles resources " +
                                        "than currently held.");
               return false;
            }
            else if (monitorSession.getPlayer().getWealth().
                     getHolding("weapons") < weapons)
            {
               monitorSession.sendError("You have requested to defend " +
                                        "with more weapons resources " +
                                        "than currently held.");
               return false;
            }
            else
            {
               monitorSession.getPlayer().getWealth().
                  changeHolding("vehicles", - vehicles);
               monitorSession.getPlayer().getWealth().
                  changeHolding("weapons", - weapons);
               return true;
            }
         }
         catch (UnknownResourceException ure)
         {
            monitorSession.sendError(
               "FATAL DEFEND ERROR: " + 
               " post to java-project@helios.ececs.uc.edu (" +
               ure.getMessage() + ")");
            return false;
         }
         catch (InsufficientResourceException ire)
         {
            monitorSession.sendError(
               "FATAL DEFEND ERROR: " + 
               " post to java-project@helios.ececs.uc.edu (" +
               ire.getMessage() + ")");
            return false;
         }
      }
      /**********************************************************************/
      public void echo(MonitorSession session) {
         session.println(Directive.COMMENT_DIRECTIVE + 
                     "Defending with " + weapons + " weapons and " + 
                     vehicles + " vehicles");
      }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/

