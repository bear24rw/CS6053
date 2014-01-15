
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/CommandException.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: CommandException.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.6  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.5  1998/11/30 03:18:25  bkuhn
//   -- changed things to use new println() method in MonitorSession
//
// Revision 0.4  1998/11/09 04:04:46  bkuhn
//   -- added static method for reporting error without
//      a CommandException object
//
// Revision 0.3  1998/11/07 20:01:55  bkuhn
//   -- made report() function public
//
// Revision 0.2  1998/11/03 06:15:53  bkuhn
//   # cosmetic changes
//

import java.util.*;
import java.io.*;

/*****************************************************************************/
class CommandException extends Exception {
      String message;

      static final String COMMAND_ERROR   = Directive.COMMAND_ERROR_DIRECTIVE;

      CommandException(String newMessage) {  message = newMessage;  }
      
      public String getMessage() {  return new String(message);  }
       
      public void report(MonitorSession session) {
         session.println(COMMAND_ERROR + message);
      }

      static public void report(MonitorSession session, String message) {
         session.println(COMMAND_ERROR + message);
      }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
