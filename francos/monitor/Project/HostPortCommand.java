// Command.java                                                 -*- Java -*-
//   Commands avaiable to the monitor
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/HostPortCommand.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: HostPortCommand.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.9  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.8  1998/11/30 03:18:25  bkuhn
//  -- changed things to use new println() method in MonitorSession
//
// Revision 0.7  1998/11/17 15:09:47  bkuhn
//   # moved HOST_PORT_LOOKUP_TIMEOUT constant to GameParameters
//
// Revision 0.6  1998/11/15 18:12:10  bkuhn
//   -- made valid port range 1024 to 65536
//
// Revision 0.5  1998/11/13 08:37:37  bkuhn
//   -- added check for port range to be valid
//   -- added sendResult() to execute(); all commands send a result now
//
// Revision 0.4  1998/11/09 06:57:52  bkuhn
//   -- fixed verify() so that it now uses the aliveCheck()
//      method in MonitorSession
//
// Revision 0.3  1998/11/08 17:46:44  bkuhn
//   -- wrote verify method
//
// Revision 0.2  1998/11/03 06:15:53  bkuhn
//    # cosmetic changes
//

import java.util.*;
import java.io.*;
import java.net.*;

/*****************************************************************************/
class HostPortCommand extends Command
{
      InetAddress address;
      int port;

      public static final String COMMAND_STRING = "HOST_PORT";
      /**********************************************************************/
      String getCommandMessage()
      {
         return new String(COMMAND_STRING);
      }
      /**********************************************************************/
      void initialize(String args[]) throws CommandException
      {
         super.initialize(args);

         String machineName = "";
         try
         {
            machineName = arguments[1];
            port = Integer.parseInt(arguments[2]);
            
            InetAddress addressList[] = InetAddress.getAllByName(machineName);
            address = addressList[0];     // Just take the first one...have
         }                                // to pick one....
         catch (ArrayIndexOutOfBoundsException abe)
         {
				Date date = new Date();
				String ss = date.toString();
            throw new CommandException(ss+": command, " + arguments[0] + 
                            ", requires two arguments, <HOSTNAME> and <PORT>");
         } 
         catch(NumberFormatException ne)
         {
				Date date = new Date();
				String ss = date.toString();
            throw new CommandException(ss+": Port number must be an integer.");
         }
         catch(UnknownHostException uhe)
         {
				Date date = new Date();
				String ss = date.toString();
            throw new CommandException(ss+": Host, " + machineName +
                                       ", is not known.");
         }

         if (port <= 1024 || port >= 65536)
            throw new CommandException(
               "The port must be between 1024 and 65536, inclusive");
            
      }
      /**********************************************************************/
      public void execute(MonitorSession session)
      {
         session.getPlayer().setInetAddressAndPort(address, port);
         session.sendResult(COMMAND_STRING + " " + address.getHostName() +
                            " " + port);
      }
      /**********************************************************************/
      public void echo(MonitorSession session) {
         session.println(Directive.COMMENT_DIRECTIVE + 
                     "Set your host to " + address.getHostName() +
                     " and port to " + port);
      }
      /**********************************************************************/
      // verify() checks to see if this command is permitted
      public boolean verify(MonitorSession session)
      {
         return session.aliveCheck(address, port, true);
      }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/

