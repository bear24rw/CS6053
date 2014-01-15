// AliveConnection.java                                 -*- Java -*-
//    A class to handling alive connections
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/AliveConnection.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: AliveConnection.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.5  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.4  1998/11/17 16:56:08  bkuhn
//   # moved ALIVE_CONNECTION_TIMEOUT constant to GameParameters
//
// Revision 0.3  1998/11/13 08:57:07  bkuhn
//   -- added checkedForLiving call to update time stamp
//
// Revision 0.2  1998/11/09 07:00:54  bkuhn
//   -- add try block to catch MonitorSessionException()
//   -- fixed constructor
//
// Revision 0.1  1998/11/08 17:20:32  bkuhn
//   # initial version
//
//

import java.io.*;
import java.net.*;
/*****************************************************************************/
class AliveConnection extends OutgoingConnectionHandler
{
      static final String rcsid = "$Revision: 1.1 $";

      Player playerWeThoughtThisWas;

      AliveConnection(Socket outgoing, double timeToLive)
      {
         super(outgoing, timeToLive);
         setPriority(NORM_PRIORITY + 2);
      }
      /**********************************************************************/
      AliveConnection(InetAddress address, int port, PlayerDB players, 
                      Player player, double timeToLive)
         throws MonitorSessionCreationException
      {
         super(address, port, timeToLive, players);

         playerWeThoughtThisWas = player;
      }
      /**********************************************************************/
      AliveConnection(InetAddress address, int port, PlayerDB players, 
                      Player player)
         throws MonitorSessionCreationException
      {
         this(address, port, players, player,
              GameParameters.ALIVE_CONNECTION_TIMEOUT);
      }
      /**********************************************************************/
      public void run()
      {
         try
         {
            if (session.verifyAlive(playerWeThoughtThisWas))
            {
               setCompletedNormally();
               playerWeThoughtThisWas.checkedForLiving(true);
            }
         }
         catch(MonitorSessionException mse)
         {
            session = null;
         }
         terminateConnection();
         timer.outOfTime();
         timer.stop();
      }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
