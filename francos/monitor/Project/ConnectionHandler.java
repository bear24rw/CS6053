// ConnectionHandler.java                                         -*- Java -*-
//    A class to handle connections
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/ConnectionHandler.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: ConnectionHandler.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.4  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.3  1998/11/09 06:30:13  bkuhn
//   -- made handleTimeout() unabstract so that it can be
//      called when I only have a ConnectionHandler object
//
// Revision 0.2  1998/11/08 17:37:28  bkuhn
//   -- added completedNormally() methods for testing if the connection
//      completed normally
//   -- added timer support
//   -- made it an abstract class
//   -- modifed constructor to include timer support
//
// Revision 0.1  1998/11/03 06:15:53  bkuhn
//   # initial version
//

import java.util.*;
import java.net.*;
import java.io.IOException;
/*****************************************************************************/
abstract class ConnectionHandler extends Thread implements TimedExistance {
   static final String rcsid = "$Revision: 1.1 $";
      
   Socket connection;
   Timer timer; 
   
   boolean completedNormally;

   ConnectionHandler(Socket incoming, double timeToLive) {
      this(timeToLive);
      connection = incoming;
   }
   /**********************************************************************/
   protected ConnectionHandler(double timeToLive) {
      unsetCompletedNormally();
      timer = new Timer(timeToLive, this);
      timer.start();
   }
   /**********************************************************************/
   protected void setCompletedNormally() {
      completedNormally = true;
   }
   /**********************************************************************/
   protected void unsetCompletedNormally() {
      completedNormally = false;
   }
   /**********************************************************************/
   public  boolean completedNormally() {
      return completedNormally;
   }
   /**********************************************************************/
   protected void terminateConnection() {
      try {
         if (connection != null) {
            connection.close();
            connection = null;
         }
      } catch (IOException ie) {}
   }
   /**********************************************************************/
   /* outOfTime() comes from TimedExistance interface.
   **  The timer calls this when it goes off.
   */
   public void outOfTime() {
      handleTimeout();
      terminateConnection();
      unsetCompletedNormally();
      stop();
   }
   /**********************************************************************/
   public void handleTimeout() { }         
   /*********************************************************************/
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
