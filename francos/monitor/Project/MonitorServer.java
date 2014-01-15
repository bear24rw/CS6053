// MontiorServer.java                                           -*- Java -*-
//    The server that listens on the main port and spins off worker threads
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
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/MonitorServer.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: MonitorServer.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.14  2003/11/25 07:47:22  cokane
// Import from franco's tree
//
// Revision 0.13  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.12  1998/12/08 23:34:31  bkuhn
//   -- added counter
//
// Revision 0.11  1998/12/02 07:02:13  bkuhn
//   -- changed things so I have different error message for no players
//
// Revision 0.10  1998/12/02 06:01:01  bkuhn
//   -- minor change to startup output
//
// Revision 0.9  1998/12/02 04:59:50  bkuhn
//   -- added extra print statement for starting wars
//
// Revision 0.8  1998/11/30 10:51:28  bkuhn
//    -- changed things so that we have both STATIC and DYNAMIC modes
//       and are able to restore a PlayerDB from a serialized file.
//       Note that we need to restart all the Wars when we come back
//    -- added game directory as a command line argument
//
// Revision 0.7  1998/11/17 00:51:21  bkuhn
//   # moved constasnts to GameParameters
//
// Revision 0.6  1998/11/16 08:39:18  bkuhn
//   -- added RecurringEvent for AwardResources
//
// Revision 0.5  1998/11/15 18:12:37  bkuhn
//   # moved where server.start() happens
//
// Revision 0.4  1998/11/13 12:12:22  bkuhn
//   -- added a RandomRecurringEvent for CheckForLiving
//
// Revision 0.3  1998/11/09 06:18:11  bkuhn
//   -- made MonitorServer extend Thread instead of implement
//      Runnable
//   -- changed priority
//   -- made it so the main joins() on the Thread
//      (This is most probably unecessary)
//
// Revision 0.2  1998/11/08 01:09:37  bkuhn
//   # removed debugging output
//   # cosmetic changes
//
// Revision 0.1  1998/11/03 06:15:53  bkuhn
//   # initial version
//

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

/*****************************************************************************/
class MonitorServer extends Thread {
   int connectionCount;
   PlayerDB playerDB;

   /**********************************************************************/
   public MonitorServer() { this(new PlayerDB());  }
   /**********************************************************************/
   public MonitorServer(PlayerDB pdb) {
      playerDB = pdb;
      connectionCount = 0;
      setPriority(NORM_PRIORITY + 2);
   }
   /**********************************************************************/
   protected synchronized int incrementCount() { return ++connectionCount; }
   /**********************************************************************/
   protected synchronized int decrementCount() { return --connectionCount; }
   /**********************************************************************/
   protected synchronized int getCount() {  return connectionCount;  }
   /**********************************************************************/
   public void closeConnection(int connectionNumber) {  decrementCount();  }
   /**********************************************************************/
   protected PlayerDB getPlayerDB()  {  return playerDB;  }
   /**********************************************************************/
   public void run() {
         
      try {
         ServerSocket s = 
            new ServerSocket(GameParameters.MONITOR_SERVER_PORT);
         System.out.println("MONITOR: Monitor listening on port " + 
                            GameParameters.MONITOR_SERVER_PORT);
            
         while (true) {
            Socket incoming = s.accept();
      
            if (getCount() < GameParameters.MONITOR_MAX_CONNECTIONS)
               new IncomingConnectionHandler(incoming, incrementCount(),
                                             this).start();
            else
               System.out.println("MONITOR: ignored incoming request " +
                                  "due to excessive connections");
         }
      } catch (Exception f) {
         Date date = new Date();
         String ss = date.toString();
         System.out.println(ss+": MONITOR: Unable to bind to port, " +
                            GameParameters.MONITOR_SERVER_PORT);
		 System.out.println(f);
      }
   }
   /**********************************************************************/
   public static PlayerDB recoverPlayerDB() {
      PlayerDB playerDB = null;

      try {
         FileInputStream fis = new FileInputStream(
                                 GameParameters.GAME_DIRECTORY + "/" + 
                                 GameParameters.PARTICIPANT_DB_FILE);
         ObjectInputStream ois = new ObjectInputStream(fis);
         playerDB = (PlayerDB) ois.readObject();
         ois.close();
      } catch (Exception e) {
         Date date = new Date();
         String ss = date.toString();
         System.out.println(ss+": MONITOR_SERVER: " + 
                            "FATAL ERROR RECOVERING PLAYER DB: " + e);
      }
      Player curPlayer = null;

      // We must go through and restart any wars!

      for (Enumeration pe = playerDB.getPlayers() ; pe.hasMoreElements() ; ) {
         curPlayer = (Player) pe.nextElement();

         for (Enumeration we = curPlayer.getWars(); we.hasMoreElements() ; ) {
            War curWar = (War) we.nextElement();

            // Restart the War going if it wasn't over
            
            if (! curWar.isOver()) {
               Date date = new Date();
               String s = date.toString();
               System.out.println(s+":");

               System.out.println("MONITOR: WAR_FINISH: " +
                                  curWar.getAttacker().getIdentity() +
                                  " vs. " +
                                  curWar.getVictim().getIdentity()); 
               curWar.startWarThread();
//                  RandomRecurringEvent rre = null;
//                   try
//                   {
//                      rre = 
//                         new RandomRecurringEvent(Class.forName("WarRunner"),
//                                curWar, GameParameters.WAR_RUNNER_MINIMUM_TIME,
//                                     GameParameters.WAR_RUNNER_MAXIMUM_TIME);
//                   }
//                   catch (ClassNotFoundException cnfe)
//                   {
//                      System.out.println(
//                         "MONITOR: Unable to find one of the EVENT classes; " +
//                         "Problems will occur!");
//                   }
//                   if (rre != null)
//                   {
//                      curWar.setRecurringEvent(rre);
//                      rre.start();
//                   }
            }
         }
      }
      if ( curPlayer == null)
         System.out.println("MONITOR: Enable to see if game has reasonable"
                            + " holdings");
      else if (! curPlayer.getWealth().getEconomy().reasonableHoldings())
         System.out.println("MONITOR: FATAL ERROR - "
                            + "This game does not have reasonable holdings!");
      
      return playerDB;
   }         
   /**********************************************************************/
   public static void main(String arg[]) throws Exception {
      PlayerDB playerDB = null;
      try {
         GameParameters.MONITOR_SERVER_PORT = Integer.parseInt(arg[0]);

         GameParameters.GAME_DIRECTORY = arg[3];
            
         if (arg[1].equalsIgnoreCase("RECOVER"))
            playerDB = MonitorServer.recoverPlayerDB();
         else if (! arg[1].equalsIgnoreCase("NEW"))
            throw new Exception("NOT NEW");

         GameParameters.PARTICIPANT_DATABASE_IS_STATIC = 
            arg[2].equalsIgnoreCase("STATIC");

         if (! (arg[2].equalsIgnoreCase("STATIC") || 
                arg[2].equalsIgnoreCase("DYNAMIC") ) )
            throw new Exception("NOT RIGHT");
      } catch (OutOfMemoryError e) {
         Date date = new Date();
         String ss = date.toString();
         System.out.println("Out of Memory <1>");
         System.exit(0);
      } catch (Exception aoe) {
         throw new Exception( "usage: MonitorServer " +
                 "<PORT> <RECOVER|NEW> <STATIC|DYNAMIC> <GAME_DIR>");
      }
      
      MonitorServer server;

      if (playerDB == null)
         server = new MonitorServer();
      else
         server = new MonitorServer(playerDB);

      try {
         new RandomRecurringEvent(Class.forName("CheckForLiving"),
                                  server.getPlayerDB(),
                                  GameParameters.CHECK_FOR_LIVING_MINIMUM_TIME,
                                  GameParameters.CHECK_FOR_LIVING_MAXIMUM_TIME
                                  ).start();

         new RecurringEvent(Class.forName("AwardResources"),
                            server.getPlayerDB(),
                            GameParameters.AWARD_RESOURCES_TIME
                            ).start();

         new RecurringEvent(Class.forName("ClearCommandCounter"),
                            PassiveMonitorSession.getPlayerCommandCounter(),
                            60.0 * 60.0
                            ).start();

         new RecurringEvent(Class.forName("SerializePlayerDB"),
                            server.getPlayerDB(),
                            GameParameters.PARTICIPANT_DB_SERIALIZE_TIME
                            ).start();
      } catch (OutOfMemoryError e) {
				
         Date date = new Date();
         String ss = date.toString();
         System.out.println(ss+": Out of Memory");
         System.exit(0);
      } catch (ClassNotFoundException cnfe) {
         System.out.println(
               "MONITOR: Unable to find one of the EVENT classes; " +
               "Problems will occur!");
      }
      try {
         server.start();
         
         try { server.join(); } catch(InterruptedException ie) { }
         System.out.println("MONITOR: Server Exiting...");
      } catch (OutOfMemoryError e) {
         Date date = new Date();
         String ss = date.toString();
         System.out.println(ss+": Out of Memory");
         System.exit(0);
      }
   }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
