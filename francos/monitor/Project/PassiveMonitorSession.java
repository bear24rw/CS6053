// PassiveMonitorSession.java                                     -*- Java -*-
//    A class to encapsulate a session with a monitor, passive
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/PassiveMonitorSession.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: PassiveMonitorSession.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.11  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.10  1998/12/08 23:33:58  bkuhn
//   -- added counter
//
// Revision 0.8  1998/11/30 03:18:25  bkuhn
// -- changed things to use new println() method in MonitorSession
//
// Revision 0.7  1998/11/16 01:41:54  bkuhn
//   -- made sure that the PARTICIPANT_PASSWORD_CHECKSUM gets sent
//      when ALIVE is REQUIRE:'d
//
// Revision 0.6  1998/11/15 16:52:40  bkuhn
//   -- made it so that PASSWORD is only required if Player has
//      never seen his/her Monitor Password
//
// Revision 0.5  1998/11/13 08:18:05  bkuhn
//   # added check for plyaer being null in initiate()
//
// Revision 0.4  1998/11/09 07:15:30  bkuhn
//   -- initiate now handles HOST_PORT checks properly
//
// Revision 0.3  1998/11/07 21:18:34  bkuhn
//   -- modified constructor to take PlayerDB
//
// Revision 0.2  1998/11/03 06:15:58  bkuhn
//   # initial version
//

import java.util.*;
import java.io.*;
import java.net.*;

/*****************************************************************************/
class PassiveMonitorSession extends MonitorSession {
   static PlayerCommandCounter playerCommandCounter =
      new PlayerCommandCounter();
   InetAddress addressConnectedTo;

   PassiveMonitorSession(PlayerDB players, Socket incoming)
      throws MonitorSessionCreationException {
      super(players);

      try {
         input  = new BufferedReader(new InputStreamReader(
                         incoming.getInputStream())); 

         output = new PrintStream(incoming.getOutputStream());
      } catch (IOException ie) {
         Date date = new Date();
         String ss = date.toString();
         throw new MonitorSessionCreationException(ss+
                                 ": unable to create I/O streams");
      }
      greet();
   }
   /*********************************************************************/
   int incrementPlayer() {
      return playerCommandCounter.incrementPlayer(player.getIdentity());
   }
   /*********************************************************************/
   int checkPlayer() {
      return playerCommandCounter.checkPlayer(player.getIdentity());
   }
   /*********************************************************************/
   static PlayerCommandCounter getPlayerCommandCounter() {
      return playerCommandCounter;
   }
   /*********************************************************************/
   boolean checkConnectingHost(String hostname) {
      return addressConnectedTo.getHostName().equalsIgnoreCase(hostname);
   }         
   /*******************************************************************/
   /* initiate() returns TRUE iff. we are able to initate a session
   ** with the player.  player will be set to a valid player if
   ** the session was initiated.
   */
   boolean initiate(InetAddress whereFrom) throws MonitorSessionException  {
      addressConnectedTo = whereFrom;

      try {
         if (super.initiate()) {
            if (player != null) {
               if (playerCommandCounter.checkPlayer(player.getIdentity())
                   > GameParameters.MAX_CONNECTIONS_PER_PERIOD)
                  throw new 
                     MonitorSessionCreationException("Commands per hour" +
                                                 " limit has been exceeded.");
               if (player.hasSeenMonitorPassword())
                  requireVerifyAndExecute(AliveCommand.COMMAND_STRING);
               else
                  requireVerifyAndExecute(
                                 PlayerPasswordCommand.COMMAND_STRING);
               if (player.getHostName() == null ||
                   (! checkConnectingHost(whereFrom.getHostName())) ||
                   (! aliveCheck(player.getInetAddress(),
                                 player.getPort(), false)))
                  requireVerifyAndExecute(HostPortCommand.COMMAND_STRING);
            }
         }
      } catch (IOException ie) {
         player = null;
         endSession();
         Date date = new Date();
         String ss = date.toString();
         throw new MonitorSessionCreationException(ss+
                                 ": unable to verify player's identity");
      }
      return (player != null);
   }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
