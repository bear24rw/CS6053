// WarTruceOfferCommand.java                                      -*- Java -*-
//   Command for offering a truce during a war
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/WarTruceOfferCommand.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: WarTruceOfferCommand.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.3  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.2  1998/11/30 03:18:23  bkuhn
// -- changed things to use new println() method in MonitorSession
//
// Revision 0.1  1998/11/29 12:02:13  bkuhn
//   # initial version
//
//

import java.util.*;
import java.io.*;
import java.net.*;

/*****************************************************************************/
class WarTruceOfferCommand extends Command
{
      public static final String COMMAND_STRING = "WAR_TRUCE_OFFER";

      String identityTo;
      String identityFrom;
      String offerData;
      Hashtable resources;
      TransactionConfirmConnection truceConnection;
      Player playerTo;

      /**********************************************************************/
      String getCommandMessage()
      {
         return new String(COMMAND_STRING);
      }
      /**********************************************************************/
      void initialize(String args[]) throws CommandException
      {
         super.initialize(args);

         String toString;
         offerData = " ";
         resources = new Hashtable();
         try
         {
            identityFrom = arguments[1];
            toString = arguments[2];
            identityTo = arguments[3];

            offerData = identityFrom + " " + toString + " " + identityTo;

            for(int ii = 4; ii < arguments.length - 1; ii += 2)
            {
               System.out.println("setting : " + ii + "--" + arguments[ii] + 
                                  "--" + ii + " ===" + arguments[ii+1]);
               int value = Integer.parseInt(arguments[ii + 1]);
               resources.put(arguments[ii], new Integer(value));
               
               if (value < 0)
                  throw new CommandException(
                     "Only positive amounts may be used for truce");
                             
               offerData += " " + arguments[ii] + " " + arguments[ii+1];
            }
         }
         catch (ArrayIndexOutOfBoundsException abe)
         {
            throw new CommandException("command, " + arguments[0] + 
                ", requires an odd number of  arguments, " +
                "<IDENTITY1> to <IDENTITY2> <RESOURCE1> <AMOUNT1>" +
                "<RESOURCE1> <AMOUNT1> <RESOURCE2> <AMOUNT2>" +
                "<RESOURCEN> <AMOUNTN>");
         } 
         catch(NumberFormatException ne)
         {
            throw new CommandException("Amounts must be integers");
         }
         if (! toString.equalsIgnoreCase("to") )
            throw new CommandException("ARG2 must be \"to\"");
      }
      /**********************************************************************/
      public void execute(MonitorSession session)
      {
         String message = truceConnection.getMessage();

         try 
         {
             Player thisPlayer = session.getPlayer();

            if (truceConnection.getMessage().equalsIgnoreCase("ACCEPTED"))
                if (! 
                    thisPlayer.lookupWar(playerTo).truce(resources, playerTo,
                                                         thisPlayer))
                   message = "WAR_OVER";
         }
         catch (UnknownResourceException ure)
         {
            session.sendError(
               "FATAL TRUCE ERROR: post to java-project@helios.ececs.uc.edu (" +
               ure.getMessage() + ")");
         }
         catch (InsufficientResourceException ire)
         {
            session.sendError(
               "FATAL TRUCE ERROR: post to java-project@helios.ececs.uc.edu (" +
               ire.getMessage() + ")");
         }
         session.sendResult(COMMAND_STRING + " " + message);
      }
      /**********************************************************************/
      public void echo(MonitorSession session) {
         session.println(Directive.COMMENT_DIRECTIVE + 
                     "Seeing if truce can be negotiated...");
      }
      /**********************************************************************/
      // verify() checks to see if this command is permitted
      public boolean verify(MonitorSession session)
      {
         if (! identityFrom.equalsIgnoreCase(session.getPlayer().
                                             getIdentity())  )
         {
            session.sendError(session.getPlayer().getIdentity() +
                              " is not permitated to declare truces for " +
                              identityFrom);
            return false;
         }

         playerTo = session.getPlayerDB().lookup(identityTo);

         if (playerTo == null) 
         {
            session.sendError("unable to find player with identity, "
                             + identityTo);
            return false;
         }
         if (! session.getPlayer().atWarWith(playerTo))
         {
            session.sendError(identityFrom + " is not at war with " + 
                              identityTo);
            return false;
         }
         try {
            for (Enumeration e = resources.keys() ; e.hasMoreElements() ;)
            {
               String curResource = (String) e.nextElement();
               int    value       = ((Integer) resources.get(curResource))
                                         .intValue();

               if (session.getPlayer().getWealth().getHolding(curResource)
                               < value)
               {
                  session.sendError(identityFrom +
                                    " holds less than " + value +
                                    " units of " + curResource);
                  return false;
               }
            }
         }
         catch (UnknownResourceException ure)
         {
				Date date = new Date();
				String ss = date.toString();
				System.out.println(ss+": War truce Offer: unknown resource");
            session.sendError(ure.getMessage());
            return false;
         }

         truceConnection = null;

         try
         {
            truceConnection = 
               new TransactionConfirmConnection(session.getPlayerDB(), 
                    playerTo, Directive.WAR_TRUCE_OFFER_DIRECTIVE + offerData,
                     WarTruceResponseCommand.COMMAND_STRING);
         }
         catch (MonitorSessionException mse)
         {
				Date date = new Date();
				String ss = date.toString();	
            session.sendError(ss+": unable to make connection to verify truce");
            return false;
         }

         truceConnection.start();

         try { truceConnection.join(); }
         catch(InterruptedException ie) { }

         return true;
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

