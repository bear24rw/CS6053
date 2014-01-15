// IncomingConnectionHandler.java                                 -*- Java -*-
//    A class to handle connections
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/IncomingConnectionHandler.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: IncomingConnectionHandler.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.7  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.6  1998/12/08 23:33:49  bkuhn
//   -- added counter
//
// Revision 0.5  1998/11/17 00:45:22  bkuhn
//   # moved INCOMING_CONNECTION_TIMEOUT constat to GameParameters
//
// Revision 0.4  1998/11/09 08:43:07  bkuhn
//   -- added yield for in-between commands
//   -- took out unnecessary yield at end
//
// Revision 0.3  1998/11/09 06:49:18  bkuhn
//   # cosmetic changes
//
// Revision 0.2  1998/11/08 17:46:37  bkuhn
//   -- changed constructor
//   -- added Timer support
//   -- made session a member of the class instead of local variable in run()
//
// Revision 0.1  1998/11/03 06:15:53  bkuhn
//   # initial versin
//

import java.util.*;
import java.io.*;
import java.net.*;
/*****************************************************************************/
class IncomingConnectionHandler extends ConnectionHandler {
   static final String rcsid = "$Revision: 1.1 $";

   PassiveMonitorSession session = null;
   MonitorServer server;
   int connectionNumber;

   IncomingConnectionHandler(Socket incoming, int num, MonitorServer svr) {
      super(incoming, GameParameters.INCOMING_CONNECTION_TIMEOUT);
      
      connectionNumber = num;
      server = svr;
      setPriority(NORM_PRIORITY + 1);
   }
   /**********************************************************************/
   protected void terminateConnection() {
      super.terminateConnection();
      server.closeConnection(connectionNumber);
   }
   /**********************************************************************/
   public void handleTimeout() {
      if (session != null)
         session.sendComment("Timeout occurred at " + timer.getTime() +
                             " seconds.  Disconnecting...");
   }         
   /**********************************************************************/
   public void run() {
      try {
         session = new PassiveMonitorSession(server.getPlayerDB(), connection);

         if (session.initiate(connection.getInetAddress())) {
            while (session.checkPlayer() <
                   GameParameters.MAX_CONNECTIONS_PER_PERIOD)
               if (session.getNewCommand()) {
      
                  session.incrementPlayer();
                  session.handleCurrentCommand();
                  yield();  // yield() in case our command took a while
               }
            session.sendComment("Commands per hour" +
                                " limit has been exceeded.");
         }
      } catch (MonitorSessionException e) {
         Date date = new Date();
         String ss = date.toString();
         session.sendComment(ss+": IncomingConnectionHandler "+e.getMessage());
         session = null;
      }

      terminateConnection();
      timer.outOfTime();
   }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
