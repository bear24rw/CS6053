/*****************************************************************************
 ** SenderAuthConnection - Server-controlled connection to the client ********
 **  for authentication of transfer connection sending points. ***************
 *****************************************************************************/

/*
	RCS Info:

	$Id: SenderAuthConnection.java,v 1.1 2009/05/13 17:46:09 franco Exp $

	$Date: 2009/05/13 17:46:09 $

	$Revision: 1.1 $

	$Log: SenderAuthConnection.java,v $
	Revision 1.1  2009/05/13 17:46:09  franco
	Initial revision
	
	Revision 1.1.1.1  2008/04/17 00:44:24  franco
	
	

*/
import java.io.*;
import java.net.*;

class SenderAuthConnection extends OutgoingConnectionHandler {
   public final static int PUBLICKEY_STATE = 1;
   public final static int ROUNDS_STATE = 2;
   public final static int AUTHSET_STATE = 3;
   public final static int SUBSETA_STATE = 4;
   public final static int SUBSETJ_STATE = 5;
   public final static int SUBSETK_STATE = 6;
   public final static int AUTHORIZE_STATE = 7;

   static final String rcsid = "$Revision: 1.1 $";
   Player wantedPlayer, recvPlayer;
   int curState;
   String curMessage;
   String transferData;
   
   public SenderAuthConnection(PlayerDB players, Player send, Player recv,
	  String transferData) throws MonitorSessionCreationException {
      super(send.getInetAddress(), send.getPort(), 
	    GameParameters.TRANSACTION_CONFIRM_CONNECTION_TIMEOUT, players);
      setPriority(NORM_PRIORITY + 2);
      wantedPlayer = send;
      recvPlayer = recv;
      this.transferData = transferData;
   }
   
   public void beginSession() throws MonitorSessionException {
      session.initiate(wantedPlayer);
      try {
	 session.requireVerifyAndExecute(AliveCommand.COMMAND_STRING);
      } catch(IOException iox) {
	 throw new MonitorSessionException("Identity not verified");
      }
      
      session.sendDirective(Directive.TRANSFER_DIRECTIVE,
			    transferData); 
   }

   public void sendPublicKey(PubRSA key) {
      session.setTransferPubKey(key.getExponent(), key.getModulus());
      session.sendResult(PublicKeyCommand.COMMAND_STRING + " " +
			 key.getExponent().toString(32) + " " +
			 key.getModulus().toString(32));
      session.setTransfer();
   }
   
   public int getRounds() throws MonitorSessionException {
      try {
	 session.requireVerifyAndExecute(RoundsCommand.COMMAND_STRING);
	 return session.getTransferRounds();
      } catch(IOException iox) {
	 throw new MonitorSessionException("Connection Error");
      }
   }
   
   public void sendAuthorizeSet(String authorizeSet) {
      session.setAuthorizeSet(authorizeSet);
      session.sendResult(AuthorizeSetCommand.COMMAND_STRING + " " +
			 authorizeSet);
   }

   public String getSubSetA() throws MonitorSessionException {
      try {
	 session.requireVerifyAndExecute(SubSetACommand.COMMAND_STRING);
	 return session.getSubSetA();
      } catch(IOException iox) {
	 throw new MonitorSessionException("Connection Error");
      }
   }
   
   public void sendSubSetK(String k) {
      session.sendResult(SubSetKCommand.COMMAND_STRING + " " + k);
   }
   
   public void sendSubSetJ(String j) {
      session.sendResult(SubSetJCommand.COMMAND_STRING + " " + j);
   }
   
   public boolean isAuthorized() throws MonitorSessionException {
      try {
	 session.requireVerifyAndExecute(
			 TransferResponseCommand.COMMAND_STRING);
	 setCompletedNormally();
	 return session.isTransferAuthorized();
      } catch(IOException iox) {
	 throw new MonitorSessionException("Connection Error");
      }
   }

   public void shutdownConnection() throws MonitorSessionException {
      try {
	 session.requireVerifyAndExecute(QuitCommand.COMMAND_STRING);
	 terminateConnection();
	 session = null;
      } catch(IOException iox) {
	 throw new MonitorSessionException("Connection error");
      }
   }
   
   public Player getConnectedIdentity() {
      return session.getPlayer();
   }
}
