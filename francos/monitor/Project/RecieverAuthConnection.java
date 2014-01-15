/*****************************************************************************
 ** RecieverAuthConnection - Server-controlled connection to the client ******
 **  for authentication of transfer connection, recieving points. ************
 *****************************************************************************/

/*
	RCS Info:
	$Id: RecieverAuthConnection.java,v 1.1 2009/05/13 17:46:09 franco Exp $
	$Date: 2009/05/13 17:46:09 $
	$Revision: 1.1 $
	$Log: RecieverAuthConnection.java,v $
	Revision 1.1  2009/05/13 17:46:09  franco
	Initial revision
	
	Revision 1.1.1.1  2008/04/17 00:44:24  franco
	
	
	In order to steal, you must IDENT with your own name, but mimic the
	other parties' verify-prove transaction.
*/
import java.io.*;
import java.net.*;

class RecieverAuthConnection extends OutgoingConnectionHandler {
   static final String rcsid = "$Revision: 1.1 $";
   Player wantedPlayer, isPlayer, senderPlayer;
   String transferData;
   
   public RecieverAuthConnection(PlayerDB players, Player recv, Player send,
				 String transferData) 
      throws MonitorSessionCreationException {
      super(recv.getInetAddress(), recv.getPort(),
	    GameParameters.TRANSACTION_CONFIRM_CONNECTION_TIMEOUT, players);
      wantedPlayer = recv;
      senderPlayer = send;
      this.transferData = transferData;
   }
	
   public void beginSession() throws MonitorSessionException {
      session.initiate(wantedPlayer);
      try {
	 session.requireVerifyAndExecute(AliveCommand.COMMAND_STRING);
      } catch(IOException iox) {
	 throw new MonitorSessionException("Identity not verified");
      }
      session.setTransfer();
      session.println(Directive.TRANSFER_DIRECTIVE + transferData); 
   }
   
   public PubRSA requestPubKey() throws MonitorSessionException {
      try {
	 session.requireVerifyAndExecute(PublicKeyCommand.COMMAND_STRING);
      } catch(IOException iox) {
	 throw new MonitorSessionException("I/O Error requesting public key");
      }
      return session.getTransferPubKey();
   }
   
   public void sendRounds(int r) {
      session.setTransferRounds(r);
      session.sendResult(RoundsCommand.COMMAND_STRING + " " + r);
   }
   
   public String requestAuthorizeSet() throws MonitorSessionException {
      try {
	 session.requireVerifyAndExecute(AuthorizeSetCommand.COMMAND_STRING);
      } catch(IOException iox) {
	 throw new MonitorSessionException(iox.getMessage());
      }
      return session.getAuthorizeSet();
   }
   
   public void sendSubsetA(String subSetA) {
      session.setSubSetA(subSetA);
      session.sendResult(SubSetACommand.COMMAND_STRING + " " +
			 subSetA);
   }
   
   public String getSubSetK() throws MonitorSessionException {
      try {
	 session.requireVerifyAndExecute(SubSetKCommand.COMMAND_STRING);
      } catch(IOException iox) {
	 throw new MonitorSessionException(iox.getMessage());
      }
      return session.getSubSetK();
   }
   
   public String getSubSetJ() throws MonitorSessionException {
      try {
	 session.requireVerifyAndExecute(SubSetJCommand.COMMAND_STRING);
      } catch(IOException iox) {
	 throw new MonitorSessionException(iox.getMessage());
      }
      return session.getSubSetJ();
   }
   
   public void transferDeclined() throws MonitorSessionException {
      try {
	 session.sendResult(TransferResponseCommand.COMMAND_STRING +
			    " DECLINED");
	 setCompletedNormally();
	 session.requireVerifyAndExecute(QuitCommand.COMMAND_STRING);
	 terminateConnection();
	 session = null;
      } catch(IOException iox) {
	 throw new MonitorSessionException("Connection Error");
      }
   }

   public void transferAuthorized() throws MonitorSessionException {
      try {
	 session.sendResult(TransferResponseCommand.COMMAND_STRING +
			    " ACCEPTED");
	 setCompletedNormally();
	 session.requireVerifyAndExecute(QuitCommand.COMMAND_STRING);
	 terminateConnection();
	 session = null;
      } catch(IOException iox) {
	 throw new MonitorSessionException("Connection Error");
      }
   }
   
   public Player getConnectedIdentity() {
      return session.getPlayer();
   }
}
