// TradeRequestCommand.java                                     -*- Java -*-
//   Command to request trades
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/TradeRequestCommand.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: TradeRequestCommand.java,v $
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
class TradeRequestCommand extends Command {
   public static final String COMMAND_STRING = "TRANSFER_REQUEST";

   Player playerTo;
   String identityTo;
   String identityFrom;
   long amountFrom;
   String tradeData;
   TransactionConfirmConnection tradeConnection;
   boolean direction = true;

   /**********************************************************************/
   // verify() checks to see if this command is permitted
   public Player getPlayerToTradeWith() {  return playerTo; }
   /**********************************************************************/
   String getCommandMessage() {  return new String(COMMAND_STRING);  }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);
      String forString;

      try {
         identityFrom = arguments[1];
         amountFrom   = Long.parseLong(arguments[2]);
         forString    = arguments[3];
         identityTo = arguments[4];
         
         tradeData = "";
         for (int ii = 1 ; ii < 5 ; ii ++) 
            tradeData = tradeData.concat(arguments[ii] + " ");
      } catch (ArrayIndexOutOfBoundsException abe) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": command, " + arguments[0] + 
            ", requires 4 arguments, <IDENTITY1> <AMOUNT1> [for|from]"+
                                    " <IDENTITY2>");
      } catch(NumberFormatException ne) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": Amounts must be integers");
      }

      if (forString.equalsIgnoreCase("for")) direction = true;
      else if (forString.equalsIgnoreCase("from")) direction = false;
      else throw new CommandException("ARG3 must be \"for\" or \"from\"");

      if (amountFrom < 0)
         throw new CommandException("Only positive amounts may be transfered");

      if (amountFrom == 0)
         throw new CommandException("One of the parties must be giving "+
                                    "something");
   }
   /**********************************************************************/
   public void execute(MonitorSession session) {
      Player thisPlayer = session.getPlayer();

      try {
         if (identityTo.equalsIgnoreCase(GameParameters.MONITOR_IDENTITY)) {
            // This is a MONITOR trade...see if we can do it
            if (thisPlayer.getEconomy().trade(thisPlayer, amountFrom))
               session.sendResult(COMMAND_STRING + " ACCEPTED");
            else
               session.sendResult(COMMAND_STRING + " REJECTED");
         } else if (!direction && 
                    playerTo.getWealth().getHolding("rupyulars") < amountFrom){
            session.sendResult(COMMAND_STRING+" 1REJECTED-Lack of points1");
         } else if (direction &&
                    thisPlayer.getWealth().getHolding("rupyulars")<amountFrom){
            session.sendResult(COMMAND_STRING+" 2REJECTED-Lack of points2");
         } else {
            String results = tradeConnection.getMessage();
            if (results.equalsIgnoreCase("ACCEPTED")) {
               thisPlayer.wonWar();  // 
               playerTo.lostWar();   //

               if (direction) {
                  thisPlayer.getWealth().changeHolding("rupyulars",0-amountFrom);
                  playerTo.getWealth().changeHolding("rupyulars",amountFrom);
                  playerTo.foughtWar(amountFrom);
               } else {
                  long amt = (long)(amountFrom * 1.01);
                  thisPlayer.getWealth().changeHolding("rupyulars",amt);
                  playerTo.getWealth().changeHolding("rupyulars",0-amountFrom);
                  playerTo.trucedWar(amountFrom);
               }
            } else if (results.equalsIgnoreCase("REJECTED")) {
               playerTo.declaredWar();  //
            }
            session.sendResult(COMMAND_STRING + " " +
                               tradeConnection.getMessage());
         }
      } catch (UnknownResourceException ure) {
         session.sendError(
            "FATAL TRANSFER ERROR: post to java-project@helios.ececs.uc.edu ("+
            ure.getMessage() + ")");
      } catch (InsufficientResourceException ire) {
         session.sendError(
            "FATAL TRANSFER ERROR: post to java-project@helios.ececs.uc.edu ("+
            ire.getMessage() + ")");
      }
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Seeing if transfer can be executed...");
   }
   /**********************************************************************/
   public boolean verify(MonitorSession session) {
      /***
      if (! identityFrom.equalsIgnoreCase(session.getPlayer().getIdentity())) {
         session.sendError(session.getPlayer().getIdentity() +
                           " is not permitted to transfer as " +
                           identityFrom);
         return false;
      }
      ***/
      if (identityFrom.equalsIgnoreCase(identityTo)) {
         session.sendError("trading with yourself is counter-productive");
         return false;
      }
            
      playerTo = session.getPlayerDB().lookup(identityTo);
      
      if (playerTo == null && (! identityTo.equalsIgnoreCase(
                                        GameParameters.MONITOR_IDENTITY))) {
         session.sendError("unable to find player with identity, "
                           + identityTo);
         return false;
      }
      try {
         if (session.getPlayer().getWealth().getHolding("rupyulars")
                               < amountFrom) {
            session.sendError(identityFrom +
                              " holds less than " + amountFrom +
                              " units of rupyulars");
            return false;
         }
         if (playerTo == null) {
            // Note: this is just a dummy check to see if we get an
            //       exception thrown on resource value
            if (session.getPlayer().getWealth().getHolding("rupyulars") >= 0)
               return true;
         }
         /***
         else {
            if (playerTo.getWealth().getHolding("rupyulars") < amountFrom)
               // just pretend and return true---check again in execute()
               return true;
         }
         ***/
      } catch (UnknownResourceException ure) {
         Date date = new Date();
         String ss = date.toString();
         System.out.println(ss+": Transfer Request: unknown resource");
         session.sendError(ure.getMessage());
         return false;
      }

      if (identityTo.equalsIgnoreCase(GameParameters.MONITOR_IDENTITY))
         return true;
      else {
         // A transfer with a regular player, not the MONITOR

         tradeConnection = null;
            
         try {
            tradeConnection = 
               new TransactionConfirmConnection(session.getPlayerDB(), 
                              playerTo,Directive.TRANSFER_DIRECTIVE+tradeData,
                              TradeResponseCommand.COMMAND_STRING);
         } catch (MonitorSessionException mse) {
            Date date = new Date();
            String ss = date.toString();
            System.out.println(ss+": Transfer Request: no connection");
            session.sendError("unable to make connection to verify transfer");
            return false;
         }
         tradeConnection.start();
            
         try { tradeConnection.join(); }
         catch(InterruptedException ie) { }

         return true;
      }
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

