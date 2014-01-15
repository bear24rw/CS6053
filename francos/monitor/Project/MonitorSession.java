// MonitorSession.java                                           -*- Java -*-
//    A class to encapsulate a session with a monitor
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
// $Id: MonitorSession.java,v 1.1 2009/05/13 17:46:09 franco Exp $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: MonitorSession.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.17  2003/12/04 21:30:12  cokane
// Make sendDirective allow any directive and not just default to WAR_DECLARE
//
// Revision 0.16  2003/12/04 21:16:18  cokane
// Add some readability assisting spaces.
//
// Revision 0.15  2003/11/26 00:47:19  cokane
// Add the slash chars to the path in CA Cert loading
//
// Revision 0.14  2003/11/25 21:45:41  cokane
// Add the components for Certification Authority handling.
//
// Revision 0.13  2003/11/25 20:49:58  cokane
//   -- Pulled from bkuhns tree.
//
// Revision 0.12  1998/12/15 05:56:01  bkuhn
//   -- put files under the GPL
//
// Revision 0.11  1998/11/30 07:56:06  bkuhn
//    -- added methods to support encryption
//    -- made println and readLine so that I can hide the encryption
//       inside the MonitorSession
//
// Revision 0.10  1998/11/29 12:01:55  bkuhn
//   -- rewrote aliveCheck() to make it a bit more readable,
//      and fix the problem with checkConnectingHost() not sending an
//      error message properly.  I subsequently commented out the whole
//      check for hosts matching because it was unfair to those who
//      didn't have accounts on machines that held servers they sought
//      to impersonate
//
// Revision 0.9  1998/11/25 06:49:33  bkuhn
//   -- added the ability to get access to the current command from
//      other classes; I needed this for WarRunner
//   -- added the sendDirective() method, since I had send.* methods
//      for all the other things that got sent.  I am not using
//      the sendDirective() method everywhere, yet, though.
//
// Revision 0.8  1998/11/17 21:19:13  bkuhn
//   # moved constasnts to GameParameters
//
// Revision 0.7  1998/11/16 08:07:15  bkuhn
//   # increased version number to 0.9
//
// Revision 0.6  1998/11/15 17:45:24  bkuhn
//    # updated version number
//   -- fixed bug where both COMMAND_ERROR: and RESULT: was sent in Message Group
//    responses after some commands has been fixed.
//
// Revision 0.5  1998/11/13 08:37:53  bkuhn
//   -- added a RandomRecurringEvent for CheckForLiving
//
// Revision 0.4  1998/11/09 07:18:55  bkuhn
//   -- reorganized things so that the guts of command handling
//      is actually methods in here.  This allows the sub
//      classes to very easily require() things and process
//      commands.
//
// Revision 0.2  1998/11/03 06:15:53  bkuhn
//   # initial version
//
import java.math.*;
import java.util.*;
import java.io.*;
import java.net.*;

/*****************************************************************************/
class MonitorSession {
   static final String rcsid = "$Revision: 1.1 $";

   BufferedReader  input;
   PrintStream     output;
   // Check it out!  PrintStream is deprecated, and one is supposed
   // to replace it with PrintWriter----PrintWriter works great 
   // in kaffe, but Sun's JVM can't do anything with it.
   // What do you think of that?
	// 
   PubRSA          pubKey;
   RSA             privateKey;   
   PlayerCertificate monitorCert;

   Player          player, auxiliary;
   PlayerDB        playerDB;

   Command         curCommand;
      
   DHEngine        dhEngine;
   Cipher          cipher;
      
   boolean         inTransfer;
   boolean		   direction;
   String          transTo, transFrom;
   
   /* Transfer command data holders. XXX do this better later */
   PubRSA transferKey;
   int transferRounds;
   String transferAuthorizeSet;
   String transferSubsetA;
   String transferSubsetK;
   String transferSubsetJ;
   boolean transferAuthorized;
   int subsetAsize;
   OutgoingConnectionHandler transferConnection;

   /*********************************************************************/
   MonitorSession(PlayerDB players) {
      playerDB = players;

      dhEngine = null;
      cipher = null;
	  InitializeCertAuthority();
	  endTransfer();
   }
   /*********************************************************************/
   boolean setDHExchangeKey(DHKey dhKey, String key) {
      dhEngine = new DHEngine(playerDB.getDHKey());
      return dhEngine.setExchangeKey(key);
   }
   /*********************************************************************/
   String getDHExchangeKey() {  return dhEngine.getExchangeKey();  }
   /*********************************************************************/
   void enableCipherMode() { cipher = new Cipher(dhEngine.getSharedKey()); }
   /*********************************************************************/
   void println(String s) {
      if (cipher == null) {
         System.out.println("outgoing>>>"+s);
         output.println(s);
      } else {
         s = cipher.Encrypt(s);
         System.out.println("outgoing [encrypted] >>>"+s);
         output.println(s);
      }
   }
   /*********************************************************************/
   String readLine() throws IOException {
      String s = input.readLine();

		if (s == null) {
           System.out.println("incoming>>> broken connection");
			return "QUIT";
		} else if (cipher == null) {
           System.out.println("incoming>>>"+s);
           return s;
      } else {
         String d = cipher.Decrypt(s);
         System.out.println("incoming [decrypted] >>>"+d);
         return d;
      }
   }
   /*********************************************************************/
   private PrintStream getOutput() {  return output;  }
   /*********************************************************************/
   void greet() {
      sendComment("Monitor Version " + GameParameters.MONITOR_VERSION);
   }
   /*********************************************************************/
   boolean checkConnectingHost(String hostname) {
      if (player == null || player.getHostName() == null)
         return false;
      else
         return player.getHostName().equalsIgnoreCase(hostname);
   }         

   /* BEGIN Cert. Authority stuffs */
   /** Invalidate, or turn off the certificate authority feature. If
	 any of the members are set to null, the authority is disabled and
	 a warning is sent. **/
   private void InvalidateCertAuthority() {
	   pubKey = null;
	   privateKey = null;
	   monitorCert = null;
   }

   /** Init the server Keys and Certificate, to start CA handling **/
   private void InitializeCertAuthority() {
	   try {
		   /* Throws IOExceptions, NullPointerException,
			  ClassNotFoundException */
		   /* Make sure the configuration is set */
		   if(GameParameters.MONITOR_RSAPRIV == "")
			   throw new NullPointerException("MONITOR_RSAPRIV not set.");
		   if(GameParameters.MONITOR_RSAPUB == "")
			   throw new NullPointerException("MONITOR_RSAPUB not set.");
		   if(GameParameters.MONITOR_CERT == "")
			   throw new NullPointerException("MONITOR_CERT not set.");

		   /* Read the monitor certificate, stored on disk. */
		   ObjectInputStream ois = new ObjectInputStream(
				   new FileInputStream(GameParameters.GAME_DIRECTORY + "/" +
					   GameParameters.MONITOR_CERT));
		   monitorCert = (PlayerCertificate)ois.readObject();
		   ois.close();

		   /* Read the monitor private key, stored on disk. */
		   ois = new ObjectInputStream(new FileInputStream(
					   GameParameters.GAME_DIRECTORY + "/" +
					   GameParameters.MONITOR_RSAPRIV));
		   privateKey = (RSA)ois.readObject();
		   ois.close();

		   /* Read the monitor public key, stored on disk */
		   ois = new ObjectInputStream(new FileInputStream(
					   GameParameters.GAME_DIRECTORY + "/" +
					   GameParameters.MONITOR_RSAPUB));
		   pubKey = (PubRSA)ois.readObject();
		   ois.close();

	   } catch(Exception iox) {
		   System.out.println("Warning, Cert Authority disabled: " +
				   iox.getMessage());
		   InvalidateCertAuthority();
	   }
   }
   
   /** Get the Monitor's Public Certificate. **/
   public PlayerCertificate getMonitorCertificate() {
	   return monitorCert;
   }

   /** Get the Monitor's Private Signing Key. **/
   public PubRSA getMonitorPublicKey() {
	   return pubKey;
   }

   /** Get the Monitor's Public Key (Verification). **/
   public RSA getMonitorPrivateKey() {
	   return privateKey;
   }
	   
   /*********************************************************************/
   void sendComplete() {  println(Directive.WAITING_DIRECTIVE);  }         
   /*********************************************************************/
   public boolean requireVerifyAndExecute(String commandRequried)
      throws IOException, MonitorSessionException  {
      int count = 0;
      boolean notYetVerified = true;
         
      while (notYetVerified)  {
         if (++count > GameParameters.MAX_COMMAND_RETRY_ON_REQUIRE) {
            throw new MonitorSessionException(
                      "Excessive command failures...Disconnecting...");
         }
            
         require(commandRequried);
                     
         if ( curCommand != null && curCommand.verify(this) ) {
            curCommand.execute(this);
            notYetVerified = false;
         } else {
            Date date = new Date();
            String s = date.toString();
            System.out.println(s + ":");
            System.out.println("requireVerifyAndExecute: [" + commandRequried +
                               "]: Can't execute command");
         }
		 if(curCommand != null)
         	curCommand = curCommand.getNext();
      }            
      return true;
   }
   /*********************************************************************/
   void require(String commandRequired)
      throws IOException, MonitorSessionException {
      completeOutputAndGetCommand(commandRequired,
                                  GameParameters.MAX_COMMAND_RETRY_ON_REQUIRE);
   }                
   /*********************************************************************/
   void completeOutputAndGetCommand(int maxRetry)
      throws IOException, MonitorSessionException {
      int count = 0;
	  Command localCurCommand = null;

      while (localCurCommand == null) {
         if (++count > maxRetry) {
            throw new MonitorSessionException(
                       "Excessive command failures...Disconnecting...");
         }
         sendComplete();

         try {  localCurCommand = Command.getInstance(readLine(), curCommand); }
         catch (CommandException ce) {
            ce.report(this);
			if(localCurCommand != null) 
            	localCurCommand = localCurCommand.getNext();
         }
      }
	  curCommand = localCurCommand;
   }
   /*********************************************************************/
   void completeOutputAndGetCommand(String commandRequired, int maxRetry)
      throws IOException, MonitorSessionException {
      int count = 0;
	  Command localCurCommand = null;

      while (localCurCommand == null) {
         if (++count > maxRetry)
            throw new MonitorSessionException(
                    "Excessive command failures...Disconnecting...");

         println(Directive.REQUIRE_DIRECTIVE + commandRequired);
         sendComplete();

         try { localCurCommand = Command.getInstance(readLine(), 
				 commandRequired, curCommand); }
         catch (CommandException ce) {
            ce.report(this);
			if(localCurCommand != null)
            	localCurCommand = localCurCommand.getNext();
         }
      }
	  curCommand = localCurCommand;
   }
   /*********************************************************************/
   Player requestIdent() throws IOException, MonitorSessionException {
      Player p = null;

      require(IdentCommand.COMMAND_STRING);
      if (curCommand != null && curCommand.verify(this)) {
         curCommand.execute(this);
         p = playerDB.lookupOrCreate((IdentCommand)curCommand);
      }
      curCommand = null;

      if (p == null)
         sendError(" Unable to find or create player with given identity");
      return p;
   }
   /*********************************************************************/
   public PlayerDB getPlayerDB() { return playerDB; }
   /*********************************************************************/
   public Player getPlayer() { return player; }
   /*********************************************************************/
   public Player getAuxillaryPlayer() { return auxiliary; }
   /*******************************************************************/
   public boolean aliveCheck(InetAddress address, int port, boolean
                             reportProblemsAsCommandError) {
      AliveConnection aliveConnection = null;
      boolean returnValue = true;
      
      if (player == null || playerDB == null) return false;

      try {
         aliveConnection = 
            new AliveConnection(address, port, playerDB, player);
      } catch (MonitorSessionException mse) {
         sendErrorOrComment(reportProblemsAsCommandError,
                            mse.getMessage());
         returnValue =  false;
      }
      if (returnValue) {
         aliveConnection.start();
         
         try { aliveConnection.join(); }
         catch(InterruptedException ie) { }
         
         if (! aliveConnection.completedNormally())
            sendErrorOrComment(reportProblemsAsCommandError,
                               "unable to verify server alive at "
                               + address.getHostName() + " on port "
                               + port);
         returnValue = aliveConnection.completedNormally();

// Put this code back in if you want to force that all active client
//  connects come from the same machine that the server comes from
//             if (! checkConnectingHost(address.getHostName()))
//             {
//                sendErrorOrComment(reportProblemsAsCommandError,
//                                   "Connection mismatch - " +
//                                " your active connection is from a different"
//                                   + " machine than your server lives on.");
//                returnValue = false;
//             }
      }
      return returnValue;
   }
   /*******************************************************************/
   /* initiate() returns TRUE iff. we are able to initate a session
   ** with the player.  player will be set to a valid player if
   ** the session was initiated.
   */
   boolean initiate() throws MonitorSessionException {

      try { player = requestIdent(); }
      catch (IOException ioe) {
         throw new MonitorSessionCreationException(
                            "unable to identify player");
      }
      return (player != null);
   }
   /*********************************************************************/
   boolean getNewCommand() throws MonitorSessionException {
      if (curCommand == null) {
         try {
            completeOutputAndGetCommand(GameParameters.MAX_COMMAND_RETRY);
         } catch (IOException ie) {
            curCommand = null;
            endSession();
            throw new MonitorSessionException("Session complete");
         }
      }
      return (curCommand != null);
   }            
   /*********************************************************************/
   Command getCurCommand() {  return curCommand;  }
   /*********************************************************************/
   void clearCurCommand() { curCommand = null;  }
   /*********************************************************************/
   void handleCurrentCommand() {
      if (curCommand == null) return;
      
      if (curCommand.verify(this)) 
		  curCommand.execute(this);
      
      curCommand = curCommand.getNext();
   }
   /*********************************************************************/
   public void endSession() {
      try { input.close(); output.close(); } catch (IOException ie) { }
   }
   /*********************************************************************/
   public void sendResult(String result) {
      println(Directive.RESULT_DIRECTIVE + result);
   }
   /*********************************************************************/
   public void sendDirective(String directive, String data) {
      println(directive + data);
   }
   /*********************************************************************/
   void sendComment(String s) {
      println(Directive.COMMENT_DIRECTIVE + s);
   }         
   /*********************************************************************/
   public void sendError(String error) {
      Date date = new Date();
      String ss = date.toString();
      System.out.println(ss+": Send Error:"+error);
      CommandException.report(this, error);
   }
   /*********************************************************************/
   public void sendErrorOrComment(boolean reportProblemsAsCommandError, 
                                  String message) {
      if (reportProblemsAsCommandError)
         sendError(message);
      else
         sendComment(message);
   }
	/* Authentication code for Transfer requests */
   /** Returns that we are in transfer mode right now. **/
   public boolean transferring() {
	   return inTransfer;
   }

   /** Turns off transfer mode, unconditionally. **/
   public void endTransfer() {
	   inTransfer = false;
	   transTo = transFrom = null;
   }

   /** Sets the variables used in transfer mode . **/
   public void setTransfer() {
	   inTransfer = true;
	/*   transTo = in;
	   transFrom = out;*/
   }

   public void setTransferPubKey(BigInteger v, BigInteger n) {
	   transferKey = new PubRSA(v, n);
   }

   public PubRSA getTransferPubKey() {
	   return transferKey;
   }

   /** Get the stored transfer rounds. **/
   public int getTransferRounds() {
	   return transferRounds;
   }

   public void setTransferRounds(int i) {
      if (i > GameParameters.MAX_ROUNDS) {
         sendComment("Number of rounds reduced to "+GameParameters.MAX_ROUNDS);
         transferRounds = GameParameters.MAX_ROUNDS;
      } else {
         transferRounds = i;
      }
   }

   public int getSubsetASize() {
      return subsetAsize;
   }

   /** Get the 'Authorize' set L. **/
   public String getAuthorizeSet() {
	   return transferAuthorizeSet;
   }

   public void setAuthorizeSet(String a) {
	   transferAuthorizeSet = a;
   }

   public void setSubSetA(String a) {
	   transferSubsetA = a;
       StringTokenizer t = new StringTokenizer(a," ");
       subsetAsize = t.countTokens();
   }

   public String getSubSetA() {
	   return transferSubsetA;
   }

   public void setSubSetK(String k) {
	   transferSubsetK = k;
   }

   public String getSubSetK() {
	   return transferSubsetK;
   }

   public void setSubSetJ(String j) {
	   transferSubsetJ = j;
   }

   public String getSubSetJ() {
	   return transferSubsetJ;
   }

   public boolean transferDirection() {
	   return direction;
   }

   public void setTransferDirection() {
	   direction = true;
   }

   public void resetTransferDirection() {
	   direction = false;
   }

   public void setTransferAuthorized() {
	   transferAuthorized = true;
   }

   public void notTransferAuthorized() {
	   transferAuthorized = false;
   }

   public boolean isTransferAuthorized() {
	   return transferAuthorized;
   }
}

/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
