// Player.java                                           -*- Java -*-
//    A class to encapsulate Players
//
// COPYRIGHT (C) 1998 Bradley M. Kuhn, All Rights Reserved.
//
// Written   :   Bradley M. Kuhn         University of Cincinnati
//   By          
//
// Written   :   John Franco
//   For         Special Topics: Java Programming
//               15-625-595-001, Fall 1998
// RCS       :
//
// $Source: /home/franco/CVS/home/C653_Final_09.8150/Project/Player.java,v $
// $Revision: 1.1 $
// $Date: 2009/05/13 17:46:09 $
//
// $Log: Player.java,v $
// Revision 1.1  2009/05/13 17:46:09  franco
// Initial revision
//
// Revision 1.1.1.1  2008/04/17 00:44:24  franco
//
//
// Revision 0.12  2003/11/26 01:52:25  cokane
// Catch NullPointerException in getCertIdentity
//
// Revision 0.11  2003/11/25 07:26:02  cokane
//  -- Changed the password HASH function. The password hash is now taken as the
//     magnitude of an always-positive BigInteger.
//  -- Added the new feature of acting as a certificate authority. Each Player
//     has a certificate attached to them, which is signed by the monitor upon
//     import, to allow users to do peer<-->peer authentication, using the
//     Monitor as their Certification Authority.
//
//
// Revision 0.10  1998/12/02 07:30:32  bkuhn
//   -- moved so that Economy didn't need to be static in Wealth
//
// Revision 0.9  1998/11/30 07:38:28  bkuhn
//    -- made it serializable and added getWars()
//
// Revision 0.8  1998/11/25 09:51:56  bkuhn
//   -- added variables and methods to handle wars
//   -- added variables and methods to keep track when a player
//      can receive a RANDOM_PARTICIPANT_HOST_PORT
//
// Revision 0.7  1998/11/17 21:20:56  bkuhn
//   # move constants to GameParameters
//   -- added resetMonitorPassword() method
//
// Revision 0.6  1998/11/16 08:39:52  bkuhn
//   -- added changePlayerPassword()
//   -- set things up so resources could be awarded
//
// Revision 0.5  1998/11/15 17:04:04  bkuhn
//   -- added stuff to keep track of if Player has seen his/her
//      Monitor Password.
//
// Revision 0.4  1998/11/13 08:53:41  bkuhn
//   -- added code to handle lastAlive data
//   -- changed password generation method
//
// Revision 0.3  1998/11/09 05:15:09  bkuhn
//   -- added MONITOR and PLAYER password support, including
//      checksum for PLAYER passwords
//
// Revision 0.2  1998/11/03 06:15:58  bkuhn
//   # initial version
//

import java.net.*;
import java.util.*;
import java.math.*;
import java.security.*;
import java.io.Serializable;
/*****************************************************************************/
class Player implements Serializable {
   static SecureRandom random = new SecureRandom();

   String identity;
   String playerPassword, shaOfPlayerPassword;
   String monitorPassword;
   InetAddress addressLastSeenOn;
   int portLastSeenOn;
   Wealth wealth;
   PlayerCertificate certIdentity;
      
   Hashtable wars;
   long warsFought, warsTruce;
   int warsDeclared, warsLost, warsWon;
   long amt_committed;                     // 11/12/10
   
   Date lastAliveCheck;
   long millisecondsAliveSinceLastResourceAllocation;
   Date canReceiveRandomPlayer;
   
   boolean hasMonitorPassword;
   boolean usingEncryption;
   
   /*********************************************************************/
   Player(String who, Economy economy) {
      identity = who;
      wealth = new Wealth(economy);
      portLastSeenOn = -1;
      addressLastSeenOn = null;
      monitorPassword = choosePassword(who);
      playerPassword  = null;
      shaOfPlayerPassword = null;
      lastAliveCheck = null;
      millisecondsAliveSinceLastResourceAllocation = 0;
      canReceiveRandomPlayer = new Date();
      hasMonitorPassword = false;
      wars = new Hashtable();
      warsFought = warsTruce = 0;
      warsDeclared = warsLost = warsWon = 0;
      amt_committed = 0;  // 11/12/10
      usingEncryption = false;
      certIdentity = null; /* Init this to NULL */
   }
   /*********************************************************************/
   public void setEncrypted (boolean e) {  usingEncryption = e;  }
   public boolean getEncrypted () {  return usingEncryption;  }
   /*********************************************************************/
   private void setPlayerPassword(String newPassword) {
      playerPassword = newPassword;
      shaOfPlayerPassword = null;
   }
   /*********************************************************************/
   public boolean hasSeenMonitorPassword() {  return hasMonitorPassword;  }
   /*********************************************************************/
   public synchronized void gaveMonitorPassword() { hasMonitorPassword=true; }
   /*********************************************************************/
   public synchronized void resetMonitorPassword() {
      monitorPassword = choosePassword(identity);
      hasMonitorPassword = false;
   }
   /*********************************************************************/
   private String choosePassword(String who) {
      return (new BigInteger(GameParameters.PASSWORD_LENGTH, random))
         .toString(36).toUpperCase();
   }
   /*********************************************************************/
   String performSHA(String input) {
      /* This method is:
       * Copyright(C) 1998 Robert Sexton.
       * Use it any way you wish.  Just leave my name on.
       */
      
      MessageDigest md;
      byte target[]; 

      try {
         md = MessageDigest.getInstance("SHA");
      } catch (NoSuchAlgorithmException e) {
         Date date = new Date();
         String ss = date.toString();
         System.out.println(ss+": No Such Algorithm Exception");
         return new String("");
      }

      target = input.toUpperCase().getBytes(); 
      md.update(target);
	
      /* return (new BigInteger(md.digest()).abs().toString(16)); */
	  return (new BigInteger(1, md.digest())).toString(16);
   }
   /*********************************************************************/
   public String playerPasswordSHA() {
      if (shaOfPlayerPassword == null)
         shaOfPlayerPassword = performSHA(playerPassword);
      return new String(shaOfPlayerPassword);
   }
   /*********************************************************************/
   public boolean playerPasswordCheck(String checkAgainst) {
      if (playerPassword == null)
         setPlayerPassword(new String(checkAgainst.toUpperCase()));
      
      return playerPassword.equalsIgnoreCase(checkAgainst);
   }
   /*********************************************************************/
   public boolean monitorPasswordCheck(String checkAgainst) {
      return monitorPassword.equalsIgnoreCase(checkAgainst);
   }
   /*********************************************************************/
   String getIdentity() {  return new String(identity);  }
   /*********************************************************************/
   String getMonitorPassword() {
      return new String(monitorPassword);
   }
   /*********************************************************************/
   synchronized void changePlayerPassword(String newPassword) {
      setPlayerPassword(newPassword);
      monitorPassword = choosePassword(identity);
      hasMonitorPassword = false;
   }         
   /*********************************************************************/
   String getHostName() {
      return (addressLastSeenOn == null ?  null :
              new String(addressLastSeenOn.getHostName()) );
   }
   /*********************************************************************/
   Wealth getWealth() {  return wealth;  }
   /*********************************************************************/
   long getAmountCommitted () { return amt_committed; }   // 11/12/10
   /*********************************************************************/	
   Economy getEconomy() { return wealth.getEconomy(); }
   /*********************************************************************/
   synchronized void setInetAddressAndPort(InetAddress address, int port) {
      addressLastSeenOn = address;
      portLastSeenOn    = port;
   }
   /*********************************************************************/
   InetAddress getInetAddress() {
      return addressLastSeenOn;
   }
   /*********************************************************************/
   int getPort() { return portLastSeenOn;  }
   /*********************************************************************/
   public synchronized void checkedForLiving(boolean isAlive) {
      if (!isAlive)
         lastAliveCheck = null;
      else {
         Date now = new Date();
         
         if (lastAliveCheck != null)
            millisecondsAliveSinceLastResourceAllocation += 
               now.getTime() - lastAliveCheck.getTime();
         
         lastAliveCheck = now;
      }
   }
   /*********************************************************************/
   public synchronized boolean randomPlayerHostPortPermitted() {
      Date now = new Date();
            
      if (now.before(canReceiveRandomPlayer))
         return false;
      else {
         canReceiveRandomPlayer.setTime(now.getTime() + 
               (long) (GameParameters.SECONDS_BETWEEN_RANDOM_PLAYER * 1000.0));
         return true;
      }
   }
   /*********************************************************************/
   public synchronized long getSecondsAliveSinceLastResourceAllocation() {
      return millisecondsAliveSinceLastResourceAllocation / 1000;
   }
   /*********************************************************************/
   void addWar(War war, Player enemy) {
      warsFought++;
      wars.put(enemy.getIdentity(), war);
   }
   void foughtWar(long chg) { warsFought += chg; }
   /*********************************************************************/
   void amountCommitted (long amt) {       // 11/12/10
      amt_committed += amt;
      if (amt_committed < 0) {
         amt_committed = 0;
         System.out.println("Amount committed went negative");
      }
   }
   /*********************************************************************/	
   void wonWar() { warsWon++;  }
   /*********************************************************************/
   void lostWar() { warsLost++; }
   /*********************************************************************/
   void truceWar () { warsTruce++; }
   /*********************************************************************/	
   void trucedWar(long chg) { warsTruce += chg; }
   /*********************************************************************/
   void declaredWar() { warsDeclared++; }
   /*********************************************************************/
   War lookupWar(Player enemy) {
      return (War) wars.get(enemy.getIdentity());
   }
   /*********************************************************************/
   Enumeration getWars() { return wars.elements(); }
   /*********************************************************************/
   boolean atWarWith(Player enemy) {
      War war = (War) wars.get(enemy.getIdentity());
      
      return (war != null && (! war.isOver()));
   }
   /*********************************************************************/
   public synchronized boolean awardResources() {
      boolean give  = (getSecondsAliveSinceLastResourceAllocation() >=
                       GameParameters.SECONDS_ALIVE_FOR_RESOURCES);

      if (give)
         wealth.allocateResourcesToOwner();
      else 
         wealth.allocateResourcesToMonitor(lastAliveCheck);

      millisecondsAliveSinceLastResourceAllocation  = 0;
      return give;
   }

   /** Create a new identity for this player, by using the supplied,
	 server-signed certificate.
	**/
   public synchronized void createIdentity(BigInteger signedIdentity) {
	   certIdentity = new PlayerCertificate(signedIdentity, getIdentity());
   }

   /** Return the base32 String representation of this Certificate. **/
   public synchronized String getCertIdentity() {
	   try {
		   return certIdentity.getCertificate(); /* Return base32 cert. str. */
	   } catch(NullPointerException npx) {
		   return null;
	   }
   }
}
