// ActiveMonitorSession.java                                     -*- Java -*-
//    A class to encapsulate a session with a monitor, active
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/ActiveMonitorSession.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: ActiveMonitorSession.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.8  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.7  1998/12/04 17:48:19  bkuhn
//   -- fixed security bug with PARTICIPANT_PASSWORD_CHECKSUM.
//      It should *not* be sent on PassiveMonitorSessions!
//
// Revision 0.6  1998/11/30 03:10:20  bkuhn
//   -- changed things to use new println() method in MonitorSession
//
// Revision 0.5  1998/11/29 12:01:38  bkuhn
//   -- changed verifyTrade() to verifyTransaction() and had it
//      use the new TransactionResponseCommand instead of TradeResponseCommand
//
// Revision 0.4  1998/11/18 06:47:32  bkuhn
//   -- added verifyTrade() method
//
// Revision 0.3  1998/11/09 07:24:52  bkuhn
//   -- added initiate()
//   -- made verifyAlive() work with new requireVerifyAndExecute()
//      method
//
// Revision 0.2  1998/11/08 02:04:47  bkuhn
//   -- added verifyALive() method
//   -- modified constructor so this is useful
//
// Revision 0.1  1998/11/03 06:15:53  bkuhn
//   # initial version
//

import java.util.*;
import java.io.*;
import java.net.*;

/*****************************************************************************/
class ActiveMonitorSession extends MonitorSession {
   ActiveMonitorSession(PlayerDB players, Socket connection)
      throws MonitorSessionCreationException {
      super(players);

      try {
         input  = new BufferedReader(new InputStreamReader(
                                      connection.getInputStream())); 

         output = new PrintStream(connection.getOutputStream());
         //             output = new PrintWriter(connection.getOutputStream());
      } catch (IOException ie) {
         Date date = new Date();
         String ss = date.toString();
         throw new MonitorSessionCreationException(ss+": "+
                        "Created connection to " + player.getIdentity() +
                        ",  but was unable to maintain connection.");
      }
      greet();
   }
   /**********************************************************************/
   boolean initiate(Player playerWeThoughtThisWas)
      throws MonitorSessionException {
      println(Directive.PARTICIPANT_PASSWORD_CHECKSUM_DIRECTIVE + " " +
              playerWeThoughtThisWas.playerPasswordSHA());
      
      return super.initiate();
   }
   /**********************************************************************/
   public boolean verifyAlive(Player playerWeThoughtThisWas)
      throws MonitorSessionException {
      // Hold on to the player we thought that we were connnecting to.
      // If this turns out to be another player, then we know that
      // the player lied to us
      
      player = null;
      
      try {
         if (initiate(playerWeThoughtThisWas)) {
            requireVerifyAndExecute(AliveCommand.COMMAND_STRING);
            requireVerifyAndExecute(QuitCommand.COMMAND_STRING);
         }
      } catch (IOException ioe) {
         Date date = new Date();
         String ss = date.toString();
         throw new MonitorSessionException(ss+": "+
                                   "error making connection to player, " +
                                   playerWeThoughtThisWas.getIdentity());
      }
      return (player == playerWeThoughtThisWas);
   }
   /**********************************************************************/
   public boolean verifyTransaction(Player playerForTransaction,
                                    String dataToSend,
                                    String commandRequired)
      throws MonitorSessionException {
      String response = "";
      
      try {
         if (initiate(playerForTransaction)) {
            println(dataToSend);

            int count = 0;
            boolean notYetVerified = true;
               
            while (notYetVerified) {
               if (++count > GameParameters.MAX_COMMAND_RETRY_ON_REQUIRE)
                  throw new MonitorSessionException(
                          "Excessive command failures...Disconnecting...");
            
               require(commandRequired);
                     
               if ( curCommand != null && curCommand.verify(this) ) {
                  curCommand.execute(this);
                  TransactionResponseCommand trc =
                     (TransactionResponseCommand) curCommand;
                  response = trc.getResponse();
                  notYetVerified = false;
               }
               curCommand = null;
            }            

            if (response == null)
               throw new MonitorSessionException(
                              "error making connection to player, " +
                              playerForTransaction.getIdentity());
            endSession();
         }
      } catch (IOException ioe) {
         Date date = new Date();
         String ss = date.toString();
         throw new MonitorSessionException(ss+": "+
                              "error making connection to player, " +
                              playerForTransaction.getIdentity());
      }
      
      return (response.equalsIgnoreCase("ACCEPT"));
   }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
