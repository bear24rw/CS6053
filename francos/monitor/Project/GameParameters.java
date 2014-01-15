// GameParameters.java                                           -*- Java -*-
//   All the constants for Game Parameters
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
// $Source: /home/C653_Project_12/8150/Project/GameParameters.java $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: GameParameters.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.12  2003/12/01 20:08:44  cokane
// Add contact email address.
//
// Revision 0.11  2003/11/25 21:32:07  cokane
// Added Certification authority preferences.
//
// Revision 0.10  2003/11/25 21:00:44  cokane
// Import from bkuhn's sources.
//
// Revision 0.9  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.8  1998/12/08 23:34:22  bkuhn
//   -- minor fixes
//
// Revision 0.6  1998/12/02 04:16:52  bkuhn
//    # increased version number
//    -- added INIMUM_PLAYERS_THAT_MUST_HOLD_EACH_RESOURCE
//
// Revision 0.5  1998/11/30 11:16:18  bkuhn
//   # changed to version 1.4
//   -- added GAME_DIRECTORY string
//   -- Added PARTICIPANT_DATABASE_IS_STATIC variable
//   -- added PARTICIPANT_DB_SERIALIZE_TIME
//   -- REMOVED STATISTICS_FILE, don't need it since I serialize
//      PlayerDB and all sub-objects into PARTICIPANT_DB_FILE
//   -- added DH_KEY_FILE
//
// Revision 0.4  1998/11/29 23:11:58  bkuhn
//    # increased version number
//   -- added Cracking parameters
//
// Revision 0.3  1998/11/29 12:07:41  bkuhn
//   # changed version to 1.2
//   -- changed TRADE_CONFIRM_CONNECTION to TRANSACTION_CONFIRM_CONNECTION
//   -- added WAR_TRUCE_WINNER
//   -- added MONITOR_MARKUP_PERCENT and STATISTICS_FILE
//
// Revision 0.2  1998/11/25 09:30:28  bkuhn
//   # incremented VERSION number
//   -- added SECONDS_BETWEEN_RANDOM_PLAYER
//   -- added WAR_RUNNER times
//   -- added all the other WAR related variables
//   -- added PARTICIPANT_HOST_PORT_COMPUTER_COST and MONITOR_IDENTITY
//
// Revision 0.1  1998/11/18 07:41:51  bkuhn
//   # initial version
//

/*****************************************************************************/
class GameParameters {
   static final String rcsid = "$Revision: 1.1 $";

   /**********************************************************************/
   /* MONITOR  -- these are values used by the Monitor for serving  */
   
   // MONITOR_VERSION
   //   The version of the Monitor
   static final String MONITOR_VERSION = "2.2.1";

   public static String GAME_DIRECTORY = "";
   
   // MONITOR_SERVER_PORT 
   //  The port the monitor listens on
   public static int MONITOR_SERVER_PORT = 8150;

   // PARTICIPANT_DATABASE_IS_STATIC
   //  Variable that is true iff. one cannot add to the Player Database
   
   public static boolean PARTICIPANT_DATABASE_IS_STATIC = false;

   // MONITOR_MAX_CONNECTIONS
   //   Maximum number of connections the monitor permits before it starts
   //   refusing new connections
   public static final int MONITOR_MAX_CONNECTIONS = 100;

   /**********************************************************************/
   /* TIMEOUTS  --- these are values for timeouts on various connections */
   
   // ALIVE_CONNECTION_TIMEOUT
   // The amount of time (in seconds) to wait when Monitor makes a
   //  conneciton to check to see if someone is alive
   public static final double ALIVE_CONNECTION_TIMEOUT = 25.0;

   // TRANSACTION_CONFIRM_CONNECTION_TIMEOUT
   // The amount of time (in seconds) to wait when Monitor makes a
   //  conneciton to confirm a trade
   public static final double TRANSACTION_CONFIRM_CONNECTION_TIMEOUT = 180.0;
   
   // HOST_PORT_LOOKUP_TIMEOUT
   // The amount of time (in seconds) to wait while trying to verify
   //  that someone is on a particular host and port given by the HOST_PORT
   public static final double HOST_PORT_LOOKUP_TIMEOUT = 20.0;
      
   // INCOMING_CONNECTION_TIMEOUT
   // The amount of time (in seconds) to allow a Player to send commands
   //  on an incoming connection before kicking them off
   public static final double INCOMING_CONNECTION_TIMEOUT = 30 * 60.0;
   
   // MAX_COMMAND_RETRY_ON_REQUIRE and MAX_COMMAND_RETRY
   //   The number of times to allow a Player to retry a given command
   //     before forcing them to disconnect
   public static final int MAX_COMMAND_RETRY            = 10;
   public static final int MAX_COMMAND_RETRY_ON_REQUIRE = MAX_COMMAND_RETRY/2;
   
   // SECONDS_ALIVE_FOR_RESOURCES
   //  This is the number of seconds that a player must be alive
   //  to receive resources
   //  note that this should be less than or equal to the 
   //   AWARD_RESOURCES_TIME below.  Otherwise, the Players will
   //   never get any resources, because the AwardResources class
   //   resets the Player's seconds alive
   static final double SECONDS_ALIVE_FOR_RESOURCES = 60.0 * 15.0;

   // SECONDS_BETWEEN_RANDOM_PLAYER
   //  This is the number of seconds that a player must wait to 
   //  receive a random player's information
   static final double SECONDS_BETWEEN_RANDOM_PLAYER = 60.0 * 3.0;
   
   public static final int MAX_CONNECTIONS_PER_PERIOD = 150;
   /**********************************************************************/
   /* Recurring event timers         */
   
   public static final double CHECK_FOR_LIVING_MINIMUM_TIME = 10.0 * 60.0;
   public static final double CHECK_FOR_LIVING_MAXIMUM_TIME = 20.0 * 60.0;
   
   public static final double WAR_RUNNER_MINIMUM_TIME = 5.0 * 60.0;
   public static final double WAR_RUNNER_MAXIMUM_TIME = 5.0 * 60.0;
   
   public static final double AWARD_RESOURCES_TIME = 60.0 * 15.0;
   
   public static final double PARTICIPANT_DB_SERIALIZE_TIME = 1.0 * 60.0;
   /**********************************************************************/
   /* Cracking         */
   // CRACK_PASSWORD_FACTOR
   //   The Factor to use to determine if a password is cracked
   
   public static final double CRACK_PASSWORD_FACTOR = 100.0;
   
   // CRACK_PARTICIPANT_STATUS_FACTOR
   //   The Factor to use to determine if a Player_status is cracked
   
   public static final double CRACK_PARTICIPANT_STATUS_FACTOR = 50.0;
   
   /**********************************************************************/
   /* War         */
   
   // BAD_WAR_PENALTY
   //   Percent penalty on a Player who declares war on wrong place 
   static final double BAD_WAR_PENALTY = .1;
   
   // NO_WAR_WINNER
   //   String to use when there is no winner yet to a war
   static final String NO_WAR_WINNER = "NONE";
   
   // WAR_TRUCE_WINNER
   //   String to use when there is a truce in a war
   static final String WAR_TRUCE_WINNER = "TRUCE";
   
   // PILLAGE_PERCENT_MIN
   //   Minimum percentage to allow of pillage after a war
   static final double PILLAGE_PERCENT_MIN = .1;
   
   // PILLAGE_PERCENT_MAX
   //   Maximum percentage to allow of pillage after a war
   static final double PILLAGE_PERCENT_MAX = .5;
   
   // WINNER_BATTLE_PERCENT_LOST_MIN
   //   minimum percentage of resource that a winner of a battle will lose
   
   static final double WINNER_BATTLE_PERCENT_LOST_MIN = 0.0;
   
   // WINNER_BATTLE_PERCENT_LOST_MAX
   //   maximum percentage of resource that a winner of a battle will lose
   static final double WINNER_BATTLE_PERCENT_LOST_MAX = 0.15;
   
   // LOSER_BATTLE_PERCENT_LOST_MIN
   //   minimum percentage of resource that a loser in a battle will lose
   
   static final double LOSER_BATTLE_PERCENT_LOST_MIN = 0.25;
   
   // LOSER_BATTLE_PERCENT_LOST_MAX
   //   maximum percentage of resource that a loser in a battle will lose
   
   static final double LOSER_BATTLE_PERCENT_LOST_MAX = 0.60;
   
   /**********************************************************************/
   /* Misc         */
   // ALLOTMENT_AMOUNT_TO_GIVE
   // This is the amount of items to give at each allotment time
   public static final int ALLOTMENT_AMOUNT_TO_GIVE = 100;
   
   // MONITOR_MARKUP_PERCENT
   // This is the amount that the monitor marks up on trades with it
   public static final double MONITOR_MARKUP_PERCENT = .10;
   
   // PASSWORD_LENGTH 
   //  The length, in bits, of the passwords that are chosen by the system
   static final int PASSWORD_LENGTH = 98;
   
   
   // CHANGE_AMOUNT_FOR_SYNTHESIZE
   //   Amount to change a resource by to synthesize another resource
   static final long CHANGE_AMOUNT_FOR_SYNTESIZE = 2;
   
   // PARTICIPANT_HOST_PORT_COMPUTER_COST
   //   Cost of Computer resources needed to run the PARTICIPANT_HOST_PORT
   //   command
   static final int PARTICIPANT_HOST_PORT_COMPUTER_COST = 100;
   
   // MONITOR_IDENTITY
   //   The Identity to use for Monitor
   static final String MONITOR_IDENTITY = "MONITOR";
   
   // PARTICIPANT_DB_FILE
   //   File where statistics are stored
   static final String PARTICIPANT_DB_FILE = "players.db";
   
   // MINIMUM_PLAYERS_THAT_MUST_HOLD_EACH_RESOURCE
   // The miminum players that must hold each resource...to make
   //  sure that there is enough of each resource in the game.
   
   public static int MINIMUM_PLAYERS_THAT_MUST_HOLD_EACH_RESOURCE = 0;
   
   /**********************************************************************/
   /* Encrytption         */
   
   static final String DH_KEY_FILE = "DHKey";
   
   //VALUE_OF_NON_EXISTENT_RESOURCE 
   //  The inital value of resource when there are none yet in
   //  the Economy
   static final double VALUE_OF_NON_EXISTENT_RESOURCE = 50.0;
   
   /* Certification Authority Key Signing, Verification */
   /** Server "Signer's" Certificate. **/
   static final String MONITOR_CERT = "CACert";
   
   /** Server's Private RSA Key, for signing. **/
   static final String MONITOR_RSAPRIV = "RSAPriv";
   
   /** Server's Public RSA Key, for verification. **/
   static final String MONITOR_RSAPUB = "RSAPub";
   
   /** Contact printed in unexpected error context **/
   static final String SERVICE_CONTACT = "franco@helios.ececs.uc.edu";
   
   /* Zero-Knowledge Authentication */
   static final int MAX_ROUNDS = 20;
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
