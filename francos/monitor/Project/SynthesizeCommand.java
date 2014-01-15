// SynthesizeCommand.java                                         -*- Java -*-
//   Command to synthesize more complex resources
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/SynthesizeCommand.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: SynthesizeCommand.java,v $
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
// -- changed things to use new println() method in MonitorSession
//
// Revision 0.1  1998/11/18 03:35:17  bkuhn
//   # initial version
//
import java.util.*;
import java.io.*;

/*****************************************************************************/
class SynthesizeCommand extends Command
{
      public static final String COMMAND_STRING = "SYNTHESIZE";

      String resource;

      /**********************************************************************/
      void initialize(String args[]) throws CommandException
      {
         super.initialize(args);

         resource  = "";
         try
         {
            resource = arguments[1];
         }
         catch (ArrayIndexOutOfBoundsException abe)
         {
				Date date = new Date();
				String ss = date.toString();
            throw new CommandException(ss+": command, " + arguments[0] + 
                            ", requires one argument, <RESOURCE>");
         } 
         resource = resource.toLowerCase();
      }
      /**********************************************************************/
      String getCommandMessage()
      {
         return new String(COMMAND_STRING);
      }
      /**********************************************************************/
      public void execute(MonitorSession session)
      {
         try
         {
           if (session.getPlayer().getWealth().synthesize(resource))
              session.sendResult(COMMAND_STRING + " " + resource + " " +
                                 "holdings increased by one.");
           else
              session.sendError("unable to synthesize this resource");
         }
         catch(UnknownResourceException ure)
         {
				Date date = new Date();
				String ss = date.toString();
				System.out.println(ss+": Synthesize Resource: unknown resource");
            session.sendError(ure.getMessage());
         }
         catch(InsufficientResourceException ure)
         {
				Date date = new Date();
				String ss = date.toString();
            System.out.println(ss+": Synthesize Resource: Insufficient");
            session.sendError(ure.getMessage());
         }
      }
      /**********************************************************************/
      public boolean verify(MonitorSession session)
      {
         try
         {
            if ( session.getPlayer() != null &&
                 session.getPlayer().getWealth() != null &&
                 session.getPlayer().getWealth().getHolding(resource) >= 0)
               return true;
            else 
               session.sendError("Player is not known");
         }
         catch(UnknownResourceException ure)
         {
				Date date = new Date();
				String ss = date.toString();
            System.out.println(ss+": Synthesize Resource: unknown resource");
            session.sendError(ure.getMessage());
         }
         return false;
      }
      /**********************************************************************/
      public void echo(MonitorSession session) {
         session.println(Directive.COMMENT_DIRECTIVE +
                    "Attempting to synthesize resource...");
      }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/

