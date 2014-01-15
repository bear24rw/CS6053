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
// $Id: Command.java,v 1.1 2009/05/13 17:46:09 franco Exp $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: Command.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.16  2003/12/02 20:38:42  cokane
// Moved Trade*Command to Transfer*Command, to fit with style and
//   set up for the newer transfer proxied authentication.
//
// Revision 0.15  2003/12/02 17:57:49  cokane
//   -- Added the GetCertificateCommand and MakeCertificateCommand to the Command db.
//
// Revision 0.14  2003/11/26 00:20:06  cokane
//   -- Added the GetMonitorKeyCommand to the hash, removed wolfwood's code injection.
//
// Revision 0.13  2003/11/26 00:07:31  cokane
//   -- Imported from bkuhn's source tree
//
// Revision 0.12  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.11  1998/11/30 03:18:25  bkuhn
//   -- changed things to use new println() method in MonitorSession
//
// Revision 0.10  1998/11/29 23:10:16  bkuhn
//   -- added PlayerMonitorPasswordCrackCommand and PlayerStatusCrackCommand
//   # fixed minor typo in an error message
//
// Revision 0.9  1998/11/29 12:01:45  bkuhn
//   -- added WarTruceResponseCommand, WarTruceOfferCommand
//
// Revision 0.8  1998/11/25 10:03:51  bkuhn
//   -- added Commands: WarStatusCommand, WarCommand, DefendCommand,
//                      RandomPlayerHostPortCommand, and
//                      PlayerHostPortCommand
//
// Revision 0.7  1998/11/18 07:51:14  bkuhn
//   -- added creations of new commands
//   -- added GetGameIdents into this file (a new command)
//   -- made an echo() method in the base class
//
// Revision 0.6  1998/11/16 07:52:00  bkuhn
//   -- added ChangePassword command
//
// Revision 0.5  1998/11/13 08:35:07  bkuhn
//   -- changed execute() so that it now prints empty result message
//      by default
//
// Revision 0.4  1998/11/09 06:12:17  bkuhn
//   -- added PlayerPasswordCommand
//   -- removed CommandException from verify()'s throw list
//
// Revision 0.3  1998/11/07 22:24:54  bkuhn
//   -- added HelpCommand
//   -- moved QuitCommand to its own file
//
// Revision 0.2  1998/11/03 06:15:53  bkuhn
//   -- changed instantiate() to getInstance()
//   -- added PlayerStatusCommand class
//

import java.util.*;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
/*****************************************************************************/
class ValidCommandHash extends Hashtable {
   ValidCommandHash() {
      super();

      // IdentCommand.getCommandMessage() won't work
      // probably because chicken-or-egg issue
      
      this.put(ValuesCommand.COMMAND_STRING, "ValuesCommand");
      this.put(GetMonitorKeyCommand.COMMAND_STRING, "GetMonitorKeyCommand");
      this.put(GetCertificateCommand.COMMAND_STRING, "GetCertificateCommand");
      this.put(MakeCertificateCommand.COMMAND_STRING, "MakeCertificateCommand");
      this.put(IdentCommand.COMMAND_STRING, "IdentCommand");
      this.put(QuitCommand.COMMAND_STRING, "QuitCommand");
      //         this.put("MODE", "ModeCommand");
      this.put(HostPortCommand.COMMAND_STRING, "HostPortCommand");
      this.put(PlayerStatusCommand.COMMAND_STRING, "PlayerStatusCommand");
      this.put(WarStatusCommand.COMMAND_STRING, "WarStatusCommand");
      this.put(HelpCommand.COMMAND_STRING, "HelpCommand");
      this.put(AliveCommand.COMMAND_STRING, "AliveCommand");
      this.put(WarCommand.COMMAND_STRING, "WarCommand");
      this.put(DefendCommand.COMMAND_STRING, "DefendCommand");
      this.put(WarTruceResponseCommand.COMMAND_STRING,
               "WarTruceResponseCommand");
      this.put(WarTruceOfferCommand.COMMAND_STRING,
               "WarTruceOfferCommand");
      
      this.put(PlayerPasswordCommand.COMMAND_STRING,
               "PlayerPasswordCommand");
      this.put(PlayerMonitorPasswordCrackCommand.COMMAND_STRING,
               "PlayerMonitorPasswordCrackCommand");
      this.put(PlayerStatusCrackCommand.COMMAND_STRING,
               "PlayerStatusCrackCommand");
      
      this.put(ChangePasswordCommand.COMMAND_STRING, 
               "ChangePasswordCommand");
      
      this.put(SignOffCommand.COMMAND_STRING, "SignOffCommand");
      this.put(SynthesizeCommand.COMMAND_STRING, "SynthesizeCommand");
      this.put(TransferResponseCommand.COMMAND_STRING, 
	       "TransferResponseCommand");
      this.put(TransferRequestCommand.COMMAND_STRING, 
	       "TransferRequestCommand");
      this.put(GetGameIdents.COMMAND_STRING, "GetGameIdents");
      this.put(RandomPlayerHostPortCommandCommand.COMMAND_STRING,
               "RandomPlayerHostPortCommandCommand");
      this.put(PlayerHostPortCommand.COMMAND_STRING,
               "PlayerHostPortCommand");
      this.put(PublicKeyCommand.COMMAND_STRING,
	       "PublicKeyCommand");
      this.put(RoundsCommand.COMMAND_STRING, "RoundsCommand");
      this.put(AuthorizeSetCommand.COMMAND_STRING, 
	       "AuthorizeSetCommand");
      this.put(SubSetACommand.COMMAND_STRING,
	       "SubSetACommand");
      this.put(SubSetKCommand.COMMAND_STRING,
	       "SubSetKCommand");
      this.put(SubSetJCommand.COMMAND_STRING,
	       "SubSetJCommand");
   }
}
/*****************************************************************************/
abstract class Command {
   static final String rcsid = "$Revision: 1.1 $";

   static ValidCommandHash validCommands = new ValidCommandHash();

   // validCommands is a hash table key'ed by the command
   // that we see from the player, and valued by the subclass 
   // of Command that should be instantiated when we receive 
   //  that command

   // This seems like an awfully sloppy way to do this....
   //  Is there a way to make sure that the hash table is initalized?

   static boolean   mustFillValidCommands = true;

   // the arguments given by the Player for this command 
   String arguments[];
   private Command nextCommand;

   /**********************************************************************/
   static final public Command getInstance(String commandLine)
      throws CommandException, IOException {
      return getInstance(commandLine, null, null);
   }

   static final public Command getInstance(String commandLine, Command cur)
      throws CommandException, IOException {
      return getInstance(commandLine, null, cur);
   }

   /**********************************************************************/
   static final public Command getInstance(String commandLine,
                                           String expectedCommand)
      throws CommandException, IOException {
      String args[];

      if (commandLine == null || commandLine.equals("code002"))
	 throw new CommandException("Got (null) from you - watch it!!");
      if (commandLine.equals("code001"))
	 throw new CommandException("Geez - you set encryption but are not "+
				    "encrypting!!");

      args = Command.tokenizeIncoming(commandLine);

      if (args.length == 0) throw new CommandException("command is required");

      if ( expectedCommand != null && 
           (! args[0].equals(expectedCommand)) )
         throw new CommandException("Command, " + args[0] + ", " +
                          "was not expected in this context, please send " + 
                                    expectedCommand);

      String classToCreate = (String) validCommands.get(args[0]);

      if (classToCreate == null)
         throw new CommandException("Command, " + args[0] + ", not found.");

      Class commandClass;
      Command command;

      try {
         command = (Command) Class.forName(classToCreate).newInstance();
      } catch (Exception e) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": "+args[0] + " not yet implemented ("+
                                    classToCreate +")");
      }
      command.initialize(args);

      return command;
   }

   static final public Command getInstance(String commandLine,
                                           String expectedCommand,
					   Command cur)
      throws CommandException, IOException {

      if (commandLine == null || commandLine.equals("code002"))
	 throw new CommandException("Got (null) from you - watch it!!");
      if (commandLine.equals("code001"))
	 throw new CommandException("Geez - you set encryption but are not "+
				    "encrypting!!");
      
      Command newCurCommand 
	 = Command.getInstance(commandLine, expectedCommand);
      newCurCommand.setNext(cur);
      return newCurCommand;
   }
   /***********************************************************************/
   /* tokenizeIncoming() takes a String, that is assumed to be
   **   a command string from a player, and tokenizes it into 
   **   arguments
   */
   private static String[] tokenizeIncoming(String commandLine) {
      Vector argv = new Vector();
         
      StringTokenizer st = new StringTokenizer(commandLine);

      while (st.hasMoreTokens()) argv.addElement(st.nextToken().toUpperCase());

      String args[] = new String[argv.size()];
      argv.copyInto(args);

      //         args = args.toUpperCase();
         
      return args;
   }
   /**********************************************************************/
   String getCommandMessage() {  return new String("UNDEFINED");  }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      arguments = args;

      if (!arguments[0].equals(getCommandMessage()))
         throw new CommandException("command, " + arguments[0] + 
                      ", is valid, but not expected in this context, " +
                                    getCommandMessage() + "was expected.");
   }
   /**********************************************************************/
   // verify() checks to see if this command is permitted
   public boolean verify(MonitorSession monitorSession) { return true; }
   /**********************************************************************/
   // execute() actually carries out the behavior for the command
   public void execute(MonitorSession session) {
      session.sendResult(new String(getCommandMessage()));
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + "Your command was: " +
                      arguments[0]);
			
      Date date = new Date();
      String s = date.toString();
      System.out.println(s+":");
			
      System.out.println("Received command that was: " + arguments[0]);

      for(int ii = 1; ii < arguments.length; ii++) {
         session.println("The " + ii + "th argument was " + arguments[ii]);
         System.out.println("The " + ii +
                            "th argument was " + arguments[ii]);
      }
   }

   public void setNext(Command n) {
	   nextCommand = n;
   }

   public Command getNext() {
	   return nextCommand;
   }
}

/*****************************************************************************/
class PlayerStatusCommand extends Command {
   public static final String COMMAND_STRING = "PARTICIPANT_STATUS";
   /**********************************************************************/
   String getCommandMessage() {  return new String(COMMAND_STRING); }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);
   }
   /**********************************************************************/
   public void execute(MonitorSession session) {
      PlayerDB playerDB = session.getPlayerDB();
      Economy economy = playerDB.getEconomy();
      double rupyulars = 
         economy.getResourceValueByName("rupyulars").getMarketValue();
      String values = new String("Conversion: Rupyulars: "+rupyulars);
      session.sendResult(new String(COMMAND_STRING + " " +
                   session.getPlayer().getWealth().getHoldingReport()) +
                         " || "+ values);
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Sending your results...");
   }
}

/*****************************************************************************/
class WarStatusCommand extends Command {
   String enemyIdent;
   War    war;

   public static final String COMMAND_STRING = "WAR_STATUS";
   /**********************************************************************/
   String getCommandMessage() {
      return new String(COMMAND_STRING);
   }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);
      
      enemyIdent = "";
      war = null;

      try {  enemyIdent = arguments[1];  }
      catch (ArrayIndexOutOfBoundsException abe) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": command, " + arguments[0] + 
                           ", requires one argument, <PARTICIPANT_IDENTITY>");
      } 
   }
   /**********************************************************************/
   public boolean verify(MonitorSession monitorSession) {
      Player enemy = monitorSession.getPlayerDB().lookup(enemyIdent);

      if (enemy == null) {
         monitorSession.sendError("Player, " + enemyIdent +
                                  ", is not known.");
         return false;
      } else {
         war = monitorSession.getPlayer().lookupWar(enemy);

         if (war == null) {
            monitorSession.sendError("You have never been at war with "
                                     + enemyIdent);
            return false;
         } else
            return true;
      }
   }
   /**********************************************************************/
   public void execute(MonitorSession session) {
      session.sendResult(COMMAND_STRING + " " +
                         war.getWarStatus(session.getPlayer()));
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Sending War status...");
   }
}

/*****************************************************************************/
class GetGameIdents extends Command {
   public static final String COMMAND_STRING = "GET_GAME_IDENTS";
   /**********************************************************************/
   String getCommandMessage() {  return new String(COMMAND_STRING);  }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);
   }
   /**********************************************************************/
   public void execute(MonitorSession session) {
      String result = "";
   
	for (Enumeration e = session.getPlayerDB().getPlayers() ;
           e.hasMoreElements() ; ) {
         Player curPlayer = (Player) e.nextElement();

	 result  += curPlayer.getIdentity() + " ";
         
      }
      session.sendResult(COMMAND_STRING + " " + result);
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Sending your results...");
   }
}

/*****************************************************************************/
class RandomPlayerHostPortCommandCommand extends Command {
   public static final String COMMAND_STRING = "RANDOM_PARTICIPANT_HOST_PORT";
   /**********************************************************************/
   String getCommandMessage() {
      return new String(COMMAND_STRING);
   }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);
   }
   /**********************************************************************/
   public boolean verify(MonitorSession monitorSession) {
      if (monitorSession.getPlayer().randomPlayerHostPortPermitted()) {
         return true;
      } else {
         monitorSession.sendError("Too soon to receive a host and port "
                                  + " of a random Player.  Try again later.");
         return false;
      }
   }
   /**********************************************************************/
   public void execute(MonitorSession session) {
      Player randomPlayer = session.getPlayerDB().
         getRandomPlayerWithHostPort(session.getPlayer());

      if (randomPlayer == null) 
         session.sendError("No Players with valid host-ports currently" 
                           + " in database.");
      else
         session.sendResult(COMMAND_STRING + " " +
                            randomPlayer.getIdentity() + " " + 
                            randomPlayer.getHostName() + " " +
                            randomPlayer.getPort());

   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Sending a Player's host and port...");
   }
}

/*****************************************************************************/
class PlayerHostPortCommand extends Command {
   public static final String COMMAND_STRING = "PARTICIPANT_HOST_PORT";

   String identSought;
   /**********************************************************************/
   String getCommandMessage() {
      return new String(COMMAND_STRING);
   }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);

      try { identSought = arguments[1]; }
      catch (ArrayIndexOutOfBoundsException abe) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": command, " + arguments[0] + 
                      ", requires one argument, <PARTICIPANT_IDENTITY>");
      } 
   }
   /**********************************************************************/
   public boolean verify(MonitorSession monitorSession) {
      try {
         PlayerDB playerDB = monitorSession.getPlayerDB();
         Economy economy = playerDB.getEconomy();
         double rupyulars = 
            economy.getResourceValueByName("rupyulars").getMarketValue();
         Wealth wealth = monitorSession.getPlayer().getWealth();
         long rup = wealth.getHolding("rupyulars");
         double amount = rup*rupyulars;
	    
         if (amount < GameParameters.PARTICIPANT_HOST_PORT_COMPUTER_COST) {
            monitorSession.sendError("Not enough points to " +
                                     "perform this funciton.");
            return false;
         }
         return true;
      } catch (UnknownResourceException ure) {
         monitorSession.sendError("FATAL PLAYER HOST PORT ERROR: " + 
                      " post to java-project@helios.ececs.uc.edu (" + 
                                  ure.getMessage() + ")");
         return false;
      }
   }
   /**********************************************************************/
   public void execute(MonitorSession monitorSession) {
      Player player = monitorSession.getPlayerDB().lookup(identSought);
      
      Wealth wealth = monitorSession.getPlayer().getWealth();

      /***/ //Temporary JVF
      try {
         long rup = wealth.changeHolding("rupyulars", -GameParameters.PARTICIPANT_HOST_PORT_COMPUTER_COST);
      } catch (Exception e) {}
      /***/
      
      if (player == null)
         monitorSession.sendError("Player, " + identSought +
                                  ", is not known.");
      else if (player.getHostName() == null || player.getPort() <= 0)
         monitorSession.sendError("Player, " + identSought +
                                  ", is not currently registered on a port.");
      else
         monitorSession.sendResult(COMMAND_STRING + " " + identSought +
                                   " " + player.getHostName() + " "
                                   + player.getPort());
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Sending a Player's host and port...");
   }
}

/*****************************************************************************/
class WarCommand extends Command {
   public static final String COMMAND_STRING = "WAR_DECLARE";

   String identToWarWith;
   int vehicles, weapons;
   InetAddress address;
   int port;

   /**********************************************************************/
   String getCommandMessage() { return new String(COMMAND_STRING); }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);

      String machineName = "";
      try {
         identToWarWith = arguments[1];
         machineName = arguments[2];
         port = Integer.parseInt(arguments[3]);
            
         InetAddress addressList[] = InetAddress.getAllByName(machineName);
         address = addressList[0];     // Just take the first one...have

         weapons = Integer.parseInt(arguments[4]);
         vehicles = Integer.parseInt(arguments[5]);
            
      } catch (ArrayIndexOutOfBoundsException abe) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": command, " + arguments[0] + 
                ", requires five arguments, " +
                " <PARTICIPANT_IDENTITY> <HOST> <PORT> <WEAPONS> <VEHICLES>");
      } catch(NumberFormatException ne) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+
                ": Weapons and vehicles amounts must be integers");
      } catch(UnknownHostException uhe) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": Host, " + machineName +
                ", is not known.");
      }
      if (port <= 1024 || port >= 65536)
         throw new CommandException(
                "The port must be between 1024 and 65536, inclusive");
      if (weapons < 0 || vehicles < 0)
         throw new CommandException(
               "Weapons and vehicles amounts must be positive integers");
   }
   /**********************************************************************/
   public boolean verify(MonitorSession monitorSession) {
      try {
         if (identToWarWith.equalsIgnoreCase(
                monitorSession.getPlayer().getIdentity())) {
            monitorSession.sendError("You cannot declare war on yourself");
            return false;
         } else if (monitorSession.getPlayer().getWealth().
                     getHolding("weapons") < weapons) {
            monitorSession.sendError("You do not have the weapons " +
                                     " resources you requested to commit.");
            return false;
         } else if (monitorSession.getPlayer().getWealth().
                     getHolding("vehicles") < vehicles) {
            monitorSession.sendError("You do not have the vehicles " +
                                     " resources you requested to commit.");
            return false;
         } else {
            monitorSession.getPlayer().getWealth().
               changeHolding("weapons", - weapons);
            monitorSession.getPlayer().getWealth().
                  changeHolding("vehicles", - vehicles);
               return true;
         }
      } catch (UnknownResourceException ure) {
         monitorSession.sendError(
               "FATAL WAR COMMAND ERROR: " + 
               " post to java-project@helios.ececs.uc.edu (" + 
               ure.getMessage() + ")");
         return false;
      } catch (InsufficientResourceException ire) {
         monitorSession.sendError(
               "FATAL WAR COMMAND ERROR: " + 
               " post to java-project@helios.ececs.uc.edu (" + 
               ire.getMessage() + ")");
         return false;
      }
   }
   /**********************************************************************/
   public void execute(MonitorSession monitorSession) {
      int weaponsPercent = (int)(weapons * GameParameters.BAD_WAR_PENALTY);
      int vehiclesPercent = (int)(vehicles * GameParameters.BAD_WAR_PENALTY);
      boolean startWar = false;

      PlayerDB playerDB = monitorSession.getPlayerDB();
      Player enemy = monitorSession.getPlayerDB().lookup(identToWarWith);
      Player thisPlayer = monitorSession.getPlayer();

      if (enemy == null)
         monitorSession.sendError("Player, " + identToWarWith +
                                  ", is not known.");
      else {
         boolean isAlive = false;
         AliveConnection aliveConnection = null;
         try {
            aliveConnection = new AliveConnection(address, port, playerDB,
                                                  enemy);
            aliveConnection.start();
            
            try { aliveConnection.join(); }
            catch(InterruptedException ie) { }
               
            isAlive = aliveConnection.completedNormally();
         } catch (MonitorSessionException mse) {
            if (aliveConnection != null &&
                aliveConnection.isAlive())
               aliveConnection.stop();
            
            isAlive =  false;
         }
            
         if (! isAlive)
            monitorSession.sendError("unable to find " + identToWarWith +
                                     "  at " + address.getHostName() +
                                     " on port " + port);
         else {
            // This Player may have found his enemy on a place we didn't
            // know about.  Set the enemy's address and port in case.
            enemy.setInetAddressAndPort(address, port);

            if (thisPlayer.atWarWith(enemy))
               monitorSession.sendError(thisPlayer.getIdentity() +
                                        " is already at war " +
                                        " with " + enemy.getIdentity());
            else {
               startWar = true;
               War war = new War(playerDB, thisPlayer, weapons,
                                 vehicles, enemy);
//                  playerDB.addWar(war);
               
               thisPlayer.addWar(war, enemy);
               enemy.addWar(war, thisPlayer);

               RandomRecurringEvent rre = null;

               try {
                  rre = 
                     new RandomRecurringEvent(Class.forName("WarRunner"),
                                war, GameParameters.WAR_RUNNER_MINIMUM_TIME,
                                GameParameters.WAR_RUNNER_MAXIMUM_TIME);
               } catch (ClassNotFoundException cnfe) {
                  System.out.println(
                       "MONITOR: Unable to find one of the EVENT classes; " +
                       "Problems will occur!");
               }
               if (rre != null) {
                  war.setRecurringEvent(rre);
                  rre.start();
               }
            }
         }
      }
         
      if (startWar) {
         monitorSession.sendResult(COMMAND_STRING + " War begun with "
                                   + identToWarWith);
         thisPlayer.declaredWar();
      } else {
         try {
            thisPlayer.getWealth().changeHolding("weapons", 
                                                 weapons
                                                 - weaponsPercent);

            thisPlayer.getWealth(). changeHolding("vehicles",
                                                  vehicles -
                                                  vehiclesPercent);
         } catch (UnknownResourceException ure) {
            monitorSession.sendError(
                   "FATAL DECLAR WAR ERROR: " + 
                   " post to java-project@helios.ececs.uc.edu (" + 
                   ure.getMessage() + ")");
         } catch (InsufficientResourceException ire) {
            monitorSession.sendError(
                   "FATAL DECLARE WAR ERROR: " + 
                   " post to java-project@helios.ececs.uc.edu (" + 
                   ire.getMessage() + ")");
         }
      }
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Declaring war on " + identToWarWith + "...");
   }
}

/*****************************************************************************/
class HelpCommand extends Command {
   public static final String COMMAND_STRING = "HELP";
   /**********************************************************************/
   String getCommandMessage() {
      return new String(COMMAND_STRING);
   }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);
   }
   /**********************************************************************/
   public void execute(MonitorSession session) {
      String help = "";

      for (Enumeration e = validCommands.keys() ; e.hasMoreElements() ;) {
         String key = (String) e.nextElement();
         help = new String(help + key + " ");
      }
      session.sendResult(new String(COMMAND_STRING + " " +
                                    help));
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Sending help...");
   }
}

/*****************************************************************************/
class  ValuesCommand extends Command {
   public static final String COMMAND_STRING = "VALUES";
   
   String getCommandMessage() {  return new String(COMMAND_STRING);  }
	
   void initialize(String args[]) throws CommandException {
      super.initialize(args);
   }
	
   public boolean verify(MonitorSession session) {  return true;  }
	
   public void execute(MonitorSession session) {
      session.sendResult(getCommandMessage());
   }
	
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE +
                      "You are an anonymous user to me");
   }
	
   String getIdent() {  return new String("Anonymous"); }
}

/*****************************************************************************/
class PlayerMonitorPasswordCrackCommand extends Command {
   public static final String COMMAND_STRING =
      "PARTICIPANT_MONITOR_PASSWORD_CRACK";

   static SecureRandom random = new SecureRandom();

   String identSought;
   long computersToUse;
   Player playerSought;

   /**********************************************************************/
   String getCommandMessage() {  return new String(COMMAND_STRING); }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);

      try {
         identSought = arguments[1];
         computersToUse = Long.parseLong(arguments[2]);
      } catch (ArrayIndexOutOfBoundsException abe) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": command, " + arguments[0] + 
                ", requires two arguments, <PARTICIPANT_IDENTITY> " +
                "<COMPUTERS_TO_USER>");
      } catch(NumberFormatException ne) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": Computer amount must be an integer");
      }
      if (computersToUse < 0)
         throw new CommandException("Computer amount must be positive int");
   }
   /**********************************************************************/
   public boolean verify(MonitorSession monitorSession) {
      playerSought = monitorSession.getPlayerDB().lookup(identSought);

      if (playerSought == null) {
         monitorSession.sendError("Player, " + identSought +
                                  ", is not known.");
         return false;
      }
      try {
         if (monitorSession.getPlayer().getWealth().getHolding("computers")
             < computersToUse) {
            monitorSession.sendError("Not enough computer resources to " +
                                     "perform this funciton.");
            return false;
         } else {
            monitorSession.getPlayer().getWealth().
               changeHolding("computers", - computersToUse);
            return true;
         }
      } catch (UnknownResourceException ure) {
         monitorSession.sendError(
               "FATAL PLAYER CRACK ERROR: " + 
               " post to java-project@helios.ececs.uc.edu (" + 
               ure.getMessage() + ")");
         return false;
      } catch (InsufficientResourceException ire) {
         monitorSession.sendError(
               "FATAL PLAYER CRACK ERROR: " + 
               " post to java-project@helios.ececs.uc.edu (" + 
               ire.getMessage() + ")");
         return false;
      }
   }
   /**********************************************************************/
   public void execute(MonitorSession monitorSession) {
      double rand = random.nextDouble();

      if (rand < 0) rand = - rand;

      if ( (computersToUse  / 
		   (computersToUse+GameParameters.CRACK_PASSWORD_FACTOR)) >= rand)
         monitorSession.sendResult(COMMAND_STRING + " " + identSought +
                                   " SUCCEEDED " + 
                                   playerSought.getMonitorPassword());
      else 
         monitorSession.sendResult(COMMAND_STRING + " " + identSought +
                                   " FAILED ");
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Trying to crack Player's  monitor password...");
   }
}

/*****************************************************************************/
class PlayerStatusCrackCommand extends Command {
   public static final String COMMAND_STRING = "PARTICIPANT_STATUS_CRACK";

   static SecureRandom random = new SecureRandom();

   String identSought;
   long computersToUse;
   Player playerSought;

   /**********************************************************************/
   String getCommandMessage() {
      return new String(COMMAND_STRING);
   }
   /**********************************************************************/
   void initialize(String args[]) throws CommandException {
      super.initialize(args);

      try {
         identSought = arguments[1];
         computersToUse = Long.parseLong(arguments[2]);
      } catch (ArrayIndexOutOfBoundsException abe) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": command, " + arguments[0] + 
                   ", requires two arguments, <PARTICIPANT_IDENTITY> " +
                   "<COMPUTERS_TO_USER>");
      } catch(NumberFormatException ne) {
         Date date = new Date();
         String ss = date.toString();
         throw new CommandException(ss+": Computer amount must be an integer");
      }
      if (computersToUse < 0)
         throw new CommandException("Computer amount must be positive int");
   }
   /**********************************************************************/
   public boolean verify(MonitorSession monitorSession) {
      playerSought = monitorSession.getPlayerDB().lookup(identSought);

      if (playerSought == null) {
         monitorSession.sendError("Player, " + identSought +
                                  ", is not known.");
         return false;
      }
      try {
         if (monitorSession.getPlayer().getWealth().getHolding("computers")
             < computersToUse) {
            monitorSession.sendError("Not enough computer resources to " +
                                     "perform this funciton.");
            return false;
         } else {
            monitorSession.getPlayer().getWealth().
               changeHolding("computers", - computersToUse);
            return true;
         }
      } catch (UnknownResourceException ure) {
         monitorSession.sendError(
               "FATAL PLAYER CRACK ERROR: " + 
               " post to java-project@helios.ececs.uc.edu (" + 
               ure.getMessage() + ")");
         return false;
      } catch (InsufficientResourceException ire) {
         monitorSession.sendError(
               "FATAL PLAYER CRACK ERROR: " + 
               " post to java-project@helios.ececs.uc.edu (" + 
               ire.getMessage() + ")");
         return false;
      }
   }
   /**********************************************************************/
   public void execute(MonitorSession monitorSession) {
      double rand = random.nextDouble();

      if (rand < 0) rand = - rand;

      if ( (computersToUse  / 
           GameParameters.CRACK_PARTICIPANT_STATUS_FACTOR) >= rand)
         monitorSession.sendResult(COMMAND_STRING + " " + identSought +
                                   " SUCCEEDED " + 
                             playerSought.getWealth().getHoldingReport());
      else 
         monitorSession.sendResult(COMMAND_STRING + " " + identSought +
                                   " FAILED ");
   }
   /**********************************************************************/
   public void echo(MonitorSession session) {
      session.println(Directive.COMMENT_DIRECTIVE + 
                      "Trying to crack Player's  monitor password...");
   }
}

/*****************************************************************************/
