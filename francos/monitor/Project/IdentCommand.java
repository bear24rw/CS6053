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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/IdentCommand.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: IdentCommand.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.8  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.7  1998/11/30 06:34:34  bkuhn
//   -- added support for an exchangeKey optional parameter to do
//      DiffieHellman and subsequent encryption
//
// Revision 0.6  1998/11/29 12:01:51  bkuhn
//   # added a test for WAR_TRUCE_WINNER as well as others
//
// Revision 0.5  1998/11/25 04:19:46  bkuhn
//   -- made sure that certain identities are never given to players
//      because they have special meaning (i.e., MONITOR and NONE)
//
// Revision 0.4  1998/11/13 08:35:13  bkuhn
//   # cosmetic changes
//
// Revision 0.3  1998/11/09 04:04:46  bkuhn
//   # cosmetic changes
//
// Revision 0.2  1998/11/03 06:15:53  bkuhn
//    # cosmetic changes
//

import java.util.*;
import java.io.*;

/**********************************************************************/
class IdentCommand  extends Command {
   public final static String COMMAND_STRING = "IDENT";

   String identity;
   String exchangeKey;

   /**********************************************************************/
   String getCommandMessage() {
      return new String(COMMAND_STRING);
   }
   /**********************************************************************/
   IdentCommand() { }
   /**********************************************************************/
   IdentCommand(String args[])  throws CommandException {
      initialize(args);
   }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);
      
      exchangeKey = null;

      try {
         identity = arguments[1];  // Actually look this person up!
      }
      catch (ArrayIndexOutOfBoundsException abe) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": command, " + arguments[0] + 
            ", requires one or two arguments, <IDENTITY> (<DH_EXCHANGE_KEY>)");
      }

      if (arguments.length >= 3) exchangeKey = arguments[2];

      if (identity.equalsIgnoreCase(GameParameters.MONITOR_IDENTITY) || 
          identity.equalsIgnoreCase(GameParameters.NO_WAR_WINNER) ||
          identity.equalsIgnoreCase(GameParameters.WAR_TRUCE_WINNER) )
         throw new CommandException(
            "Invalid IDENTITY given, please give another.");
   }
   /**********************************************************************/
   // verify() checks to see if this command is permitted
   public boolean verify(MonitorSession session) {
      if (exchangeKey == null ||
          session.setDHExchangeKey(session.getPlayerDB().getDHKey(),exchangeKey))
         return true;
      else {
         session.sendError("Invalid DH key");
         return false;
      }
   }
   /**********************************************************************/
   // execute() actually carries out the behavior for the command
   public void execute(MonitorSession session) {
      if (exchangeKey != null) {
         session.sendResult(getCommandMessage() + " " +
                            session.getDHExchangeKey());
         session.enableCipherMode();
         Player player=(Player)session.getPlayerDB().players.get(identity);
         if (player != null) player.setEncrypted(true);
         System.out.println("Player "+identity+" enc: true ("+player+")");
      } else {
         Player player=(Player)session.getPlayerDB().players.get(identity);
         if (player != null) player.setEncrypted(false);
         System.out.println("Player "+identity+" enc: false ("+player+")");
      }
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE +
                      "Your identity appears to be: " + identity);
   }
   /**********************************************************************/
   String getIdent() {
      return new String(identity);
   }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
