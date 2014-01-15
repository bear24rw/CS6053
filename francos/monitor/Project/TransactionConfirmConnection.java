// TransactionConfirmConnection.java                               -*- Java -*-
//    A class for confirming transactions
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/TransactionConfirmConnection.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: TransactionConfirmConnection.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.4  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.3  1998/12/01 21:42:09  bkuhn
//    -- Fixed bug for trading and other side didn't response properly
//
// Revision 0.2  1998/11/29 12:02:08  bkuhn
//   -- cahnged name from TradeConfirmConnection to TransactionConfirmConnection
//       to make things more general
//
// Revision 0.1  1998/11/18 06:47:24  bkuhn
//   # initial version
//
//

import java.io.*;
import java.net.*;
/*****************************************************************************/
class TransactionConfirmConnection extends OutgoingConnectionHandler {
   static final String rcsid = "$Revision: 1.1 $";

   Player playerToTransactionWith;
   String transactionData;
   String message;
   String commandRequired;
   /**********************************************************************/
   TransactionConfirmConnection(Socket outgoing, double timeToLive) {
      super(outgoing, timeToLive);
      setPriority(NORM_PRIORITY + 2);
   }
   /**********************************************************************/
   TransactionConfirmConnection(InetAddress address, int port,
                                PlayerDB players, double timeToLive)
      throws MonitorSessionCreationException {
      super(address, port, timeToLive, players);

      setPriority(NORM_PRIORITY + 2);
   }
   /**********************************************************************/
   TransactionConfirmConnection(PlayerDB players, Player player,
                                String transaction, String command)
      throws MonitorSessionCreationException {
      this(player.getInetAddress(), player.getPort(), players,
           GameParameters.TRANSACTION_CONFIRM_CONNECTION_TIMEOUT);

      playerToTransactionWith = player;
      transactionData = transaction;
      message = "NOT_ALIVE";
      commandRequired = command;
   }
   /**********************************************************************/
   String getMessage() {  return new String(message);  }
   /**********************************************************************/
   public void run() {
      try {
         if (session.verifyTransaction(playerToTransactionWith,
                                       transactionData, commandRequired))
            message = "ACCEPTED";
         else
            message = "REJECTED";

         setCompletedNormally();
      } catch(MonitorSessionException mse) {
         session = null;
         message = "NOT_ALIVE";
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
