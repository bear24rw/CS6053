// ChangePasswordCommand.java                                      -*- Java -*-
//   Command for changing player and monitor passwords password
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/ChangePasswordCommand.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: ChangePasswordCommand.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.3  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.2  1998/11/30 03:18:25  bkuhn
//   -- changed things to use new println() method in MonitorSession
//
// Revision 0.1  1998/11/16 08:05:33  bkuhn
//   # initial version
//
import java.util.*;
import java.io.*;
/*****************************************************************************/
class ChangePasswordCommand extends Command {
   public static final String COMMAND_STRING = "CHANGE_PASSWORD";

   String oldPassword, newPassword;

   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);

      oldPassword  = "";
      newPassword = null;
      try {
         oldPassword = arguments[1];
         newPassword = arguments[2];
      }
      catch (ArrayIndexOutOfBoundsException abe) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": command, " + arguments[0] + 
               ", requires two arguments, <OLD_PARTICIPANT_PASSWORD> and " +
                                    "<NEW_PARTICIPANT_PASSWORD>");
      } 
   }
   /**********************************************************************/
   String getCommandMessage() {
      return new String(COMMAND_STRING);
   }
   /**********************************************************************/
   public void execute(MonitorSession session) {
      session.getPlayer().changePlayerPassword(newPassword);
      
      session.sendResult(COMMAND_STRING + " " + 
                         session.getPlayer().getMonitorPassword());
      session.getPlayer().gaveMonitorPassword();
   }
   /**********************************************************************/
   public boolean verify(MonitorSession session) {
      if (session.getPlayer().playerPasswordCheck(oldPassword)) {
         return true;
      } else {
         session.sendError("Old player password is invalid.");
         return false;
      }        
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + "Changing Password...");
   }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/

