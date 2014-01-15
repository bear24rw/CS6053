// OutgoingConnectionHandler.java                                 -*- Java -*-
//    A class to handling outgoing connections from the monitor
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/OutgoingConnectionHandler.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: OutgoingConnectionHandler.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.5  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.4  1998/11/15 18:10:36  bkuhn
//   -- made valid port range 1024 to 65536
//
// Revision 0.3  1998/11/13 06:16:38  bkuhn
//   -- added checks for port bounds
//
// Revision 0.2  1998/11/09 06:30:39  bkuhn
//   -- removed handleTimeout() because it seemed to be causing
//      problems.  Probably should be tried again later.
//      It seemed to be some issue with writing to output
//      when it was closed.
//
// Revision 0.1  1998/11/08 17:34:25  bkuhn
//   # initial version
//
//

import java.util.*;
import java.io.*;
import java.net.*;
/*****************************************************************************/
class OutgoingConnectionHandler extends ConnectionHandler {
   static final String rcsid = "$Revision: 1.1 $";

   ActiveMonitorSession session = null;

   OutgoingConnectionHandler(Socket outgoing, double timeToLive) {
      super(outgoing, timeToLive);
   }
   /**********************************************************************/
   OutgoingConnectionHandler(InetAddress address, int port,
                             double timeToLive, PlayerDB players)
      throws MonitorSessionCreationException {
      super(timeToLive);
      
      if (address == null || port <= 1024 || port >= 65536)
         throw new MonitorSessionCreationException(
            "unable to connect because host or port given was invalid.");
      try { connection = new Socket(address, port); }
      catch(IOException ie) {
         Date date = new Date();
         String ss = date.toString();
         throw new MonitorSessionCreationException(ss+
              ": unable to connect to host " +
              address.getHostName() + " on port " +
              port + "|" + ie.getMessage());
      }
      session = new ActiveMonitorSession(players, connection);
   }
   /**********************************************************************/
   protected void terminateConnection() {
      super.terminateConnection();
      stop();
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
