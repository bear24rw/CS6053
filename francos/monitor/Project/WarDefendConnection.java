// WarDefendConnection.java                                        -*- Java -*-
//    A class for making connections to check for war defense.
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/WarDefendConnection.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: WarDefendConnection.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.3  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.2  1998/11/29 12:02:12  bkuhn
//   -- set this up so that it uses TRANSACTION_CONFIRM_CONNECTION_TIMEOUT
//   --  SHOULD this be replaces with a TransactionConfirmConnection!?!?
//
// Revision 0.1  1998/11/25 07:40:45  bkuhn
//   # initial version
//
//

import java.io.*;
import java.net.*;
/*****************************************************************************/
class WarDefendConnection extends OutgoingConnectionHandler
{
      static final String rcsid = "$Revision: 1.1 $";

      Player victimPlayer;
      String defendData;
      int vehicles, weapons;
      
      WarDefendConnection(Socket outgoing, double timeToLive)
      {
         super(outgoing, timeToLive);
         setPriority(NORM_PRIORITY + 2);
      }
      /**********************************************************************/
      WarDefendConnection(InetAddress address, int port, PlayerDB players, 
                             double timeToLive)
         throws MonitorSessionCreationException
      {
         super(address, port, timeToLive, players);

         setPriority(NORM_PRIORITY + 2);
      }
      /**********************************************************************/
      WarDefendConnection(PlayerDB players, Player player, String defend)
         throws MonitorSessionCreationException
      {
         this(player.getInetAddress(), player.getPort(), players,
              GameParameters.TRANSACTION_CONFIRM_CONNECTION_TIMEOUT);

         victimPlayer = player;
         defendData = defend;
         weapons = vehicles = 0;
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
      public void run()
      {
         try
         {
            Command curCommand = null;

            if (session.initiate(victimPlayer))
            {
               session.sendDirective(Directive.WAR_DECLARATION_DIRECTIVE,
                                     defendData);
               int count = 0;
               boolean notYetVerified = true;
               
               while (notYetVerified)
               {
                  if (++count > GameParameters.MAX_COMMAND_RETRY_ON_REQUIRE)
                     break;

                  session.require(DefendCommand.COMMAND_STRING);
                     
                  if ( session.getCurCommand() != null && 
                       session.getCurCommand().verify(session) ) 
                  {
                     session.getCurCommand().execute(session);
                     DefendCommand dc = (DefendCommand)
                        session.getCurCommand();

                     weapons = dc.getWeapons();
                     vehicles = dc.getVehicles();

                     notYetVerified = false;
                  }
                  session.clearCurCommand();
               }            
               session.endSession();
            }
         }
         catch (IOException ioe) { }
         catch (MonitorSessionException mse) { }

         setCompletedNormally();
      }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
