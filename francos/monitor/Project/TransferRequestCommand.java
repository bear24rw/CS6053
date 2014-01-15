// TransferRequestCommand.java                                     -*- Java -*-
//   Command to request trades
//
// COPYRIGHT (C) 2003, Coleman Kane
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
//               15-653-595-001, Fall 2003
// RCS       :
//
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/TransferRequestCommand.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: TransferRequestCommand.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.5  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.4  1998/12/01 21:51:37  bkuhn
//    -- Fixed bug with trade
//
// Revision 0.3  1998/11/30 03:18:24  bkuhn
// -- changed things to use new println() method in MonitorSession
//
// Revision 0.2  1998/11/29 12:02:07  bkuhn
//    # changed some types from int to long
//   -- added code to handle trades with MONITOR
//   -- added check to see if Player was trying to trade with themselves
//   -- changed use of TradeConfirmConnection to TransactionConfirmConnection
//
// Revision 0.1  1998/11/18 07:32:04  bkuhn
//   # initial version
//

import java.util.*;
import java.io.*;
import java.net.*;
/*****************************************************************************/
class TransferRequestCommand extends Command {
   public static final String COMMAND_STRING = "TRANSFER_REQUEST";

   Player playerTo, playerFrom;
   String identityTo;
   String identityFrom;
   String results = "";
   long amountFrom;
   String tradeData;
   boolean committed;                                       // 11/12/10

   /**********************************************************************/
   public Player getPlayerToTradeWith() {  return playerTo; }
   /**********************************************************************/
   String getCommandMessage() {  return new String(COMMAND_STRING);  }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);
      String forString;
      committed = false;                                     // 11/12/10

      try {
         identityTo = arguments[1];
         amountFrom   = Long.parseLong(arguments[2]);
         if (amountFrom <= 0)    // 11/12/10
            throw new CommandException("Positive amounts only are transfered");
         forString    = arguments[3];
         identityFrom = arguments[4];
         
         tradeData = "";
         for (int ii = 1 ; ii < 5 ; ii ++) 
           tradeData = tradeData.concat(arguments[ii] + " ");
      } catch (ArrayIndexOutOfBoundsException abe) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": command, " + arguments[0] + 
               ", requires 4 arguments, <IDENTITY1> <AMOUNT1> from"+
               " <IDENTITY2>");
      } catch(NumberFormatException ne) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": Amounts must be integers");
      }
      
      if (!forString.equalsIgnoreCase("from"))
        throw new CommandException("ARG3 must be \"from\"");

      if (amountFrom <= 0)
         throw new CommandException("Only positive amounts are transfered");
   }
   /**********************************************************************/
   public void execute(MonitorSession session) {
      /** If we got here, then this is a good transfer, so just do it. **/
      synchronized (playerTo) {
         try {
            long amt = amountFrom;
            playerTo.wonWar();
            playerFrom.lostWar();
            
            amt = (long)(amt * 1.01);
            playerFrom.trucedWar(amountFrom);
            playerTo.foughtWar(amt);
            playerTo.getWealth().changeHolding("rupyulars",amt);
            playerFrom.getWealth().changeHolding("rupyulars",0-amountFrom);
         }  catch (UnknownResourceException ure) {
            session.sendError(
               "FATAL TRANSFER ERROR: post to project@helios.ececs.uc.edu ("+
               ure.getMessage() + ")");
         } catch (InsufficientResourceException ire) {
            session.sendError(
               "FATAL TRANSFER ERROR: post to project@helios.ececs.uc.edu ("+
               ire.getMessage() + ")");
         }
         if (committed) playerFrom.amountCommitted(-amountFrom);  // 11/12/10
         committed = false;
      }
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Seeing if transfer can be executed...");
   }
   /**********************************************************************/
   public boolean verify(MonitorSession session) {
      /** First, check that all input parameters are valid. **/
      if (identityFrom.equalsIgnoreCase(identityTo)) {
         session.sendError("Receiver and sender with same identity not legal");
         return false;
      }
      
      if (identityTo.equalsIgnoreCase("MONITOR") ||
          identityFrom.equalsIgnoreCase("MONITOR")) {
         session.sendError("Monitor cannot be a sender or recipient");
         return false;
      }
      
      if (identityFrom.equalsIgnoreCase(session.getPlayer().getIdentity())) {
         session.sendError("Sender cannot be the initiator");
         return false;
      }
      
      playerTo = session.getPlayerDB().lookup(identityTo);
      playerFrom = session.getPlayerDB().lookup(identityFrom);
      
      try {
         if (playerFrom.getWealth().getHolding("rupyulars") < amountFrom) {
            session.sendError(COMMAND_STRING+" REJECTED-Lack of points");
            return false;
         }
         long acom = playerFrom.getAmountCommitted();  // 11/12/10  V
         if (playerFrom.getWealth().getHolding("rupyulars") < 
             amountFrom + acom) {
            session.sendError(COMMAND_STRING+" REJECTED-doubling points?");
            return false;
         }                                             // 11/12/10  ^
      } catch (UnknownResourceException ure) {
         session.sendError(
            "FATAL TRANSFER ERROR: post to project@helios.ececs.uc.edu ("+
            ure.getMessage() + ")");
      } 
      
      if (playerTo == null || playerFrom == null) { 
         session.sendError("Either "+identityTo+" or "+identityFrom+
                           " is not a participant");
         return false;
      }
      
      /** Now actually run the verification socket. **/
      // If it is desire to allow transfers FROM the initiator you might
      // experiment with ReceiverAuthConnection - SenderAuthConnection
      // seems to have a one track mind which causes the initiator to
      // be the prover no matter what.
      try {
         playerFrom.amountCommitted(amountFrom);   // 11/12/10
         committed = true;
         SenderAuthConnection outConnection = 
           new SenderAuthConnection(session.getPlayerDB(), 
                                    playerFrom,  // verifier
                                    //remotePlayer,
                                    //playerTo,     // prover
                                    //session.getPlayer(),
                                    session.getPlayerDB().lookup(identityTo),
                                    tradeData);
         outConnection.beginSession();
         session.setTransfer();
         session.resetTransferDirection();
         session.requireVerifyAndExecute(PublicKeyCommand.COMMAND_STRING);
         PubRSA pKey = session.getTransferPubKey();
         outConnection.sendPublicKey(pKey);
         session.setTransferRounds(outConnection.getRounds());
         session.sendResult(RoundsCommand.COMMAND_STRING + " " +
                            session.getTransferRounds());
         session.requireVerifyAndExecute(AuthorizeSetCommand.COMMAND_STRING);
         outConnection.sendAuthorizeSet(session.getAuthorizeSet());
         session.setSubSetA(outConnection.getSubSetA());
         session.sendResult(SubSetACommand.COMMAND_STRING + " " +
                            session.getSubSetA());
         session.requireVerifyAndExecute(SubSetKCommand.COMMAND_STRING);
         outConnection.sendSubSetK(session.getSubSetK());
         session.requireVerifyAndExecute(SubSetJCommand.COMMAND_STRING);
         outConnection.sendSubSetJ(session.getSubSetJ());
         
         if (!outConnection.isAuthorized()) {
            session.sendResult(TransferResponseCommand.COMMAND_STRING + 
                               " DECLINED");
            outConnection.shutdownConnection();
            playerFrom.declaredWar();
            if (committed) playerFrom.amountCommitted(-amountFrom); // 11/12/10
            committed = false;
            return false;
         }
         
         session.sendResult(TransferResponseCommand.COMMAND_STRING + 
                            " ACCEPTED");
         //playerFrom = outConnection.getConnectedIdentity();
         outConnection.shutdownConnection();
         outConnection = null;
         return true;
      } catch(MonitorSessionCreationException mscex) {
         session.endTransfer();
         session.sendError("NOT ALIVE; " + mscex.getMessage());
         if (committed) playerFrom.amountCommitted(-amountFrom); // 11/12/10
         committed = false;
         return false;
      } catch(MonitorSessionException msex) {
         session.endTransfer();
         session.sendError("Session Ended Prematurely, dying");
         if (committed) playerFrom.amountCommitted(-amountFrom); // 11/12/10
         committed = false;
         return false;
      } catch(IOException iox) {
         session.endTransfer();
         System.out.println("There was a local I/O Error: " +
                            iox.getMessage());
         if (committed) playerFrom.amountCommitted(-amountFrom); // 11/12/10
         committed = false;
         return false;
      } catch (Exception lst) {
         if (committed) playerFrom.amountCommitted(-amountFrom); // 11/12/10
         committed = false;
         System.out.println(lst.toString());
         return false;
      }
   }
   /**********************************************************************/
}
