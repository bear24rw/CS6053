/*************************************************************
 ** MakeCertificateCommand - Create a new certficate ID for **
 ** the player. **********************************************/

/* 
 RCS info:
 
 $Id: MakeCertificateCommand.java,v 1.1 2009/05/13 17:46:09 franco Exp $
 
 $Revision: 1.1 $

 $Date: 2009/05/13 17:46:09 $

 $Log: MakeCertificateCommand.java,v $
 Revision 1.1  2009/05/13 17:46:09  franco
 Initial revision

 Revision 1.1.1.1  2008/04/17 00:44:24  franco


 Revision 1.1  2003/12/02 17:52:59  cokane
 Initial revision


*/
import java.security.*;
import java.math.*;
import java.util.*;
import java.io.*;
class MakeCertificateCommand extends Command {
	private static String rcsid = "$Revision: 1.1 $";
	public static String COMMAND_STRING = "MAKE_CERTIFICATE";

	/** Use this for building the certificate. **/
	private MessageDigest md;
	
	/** Initialize this Command and its MessageDigest helper. **/
	public MakeCertificateCommand() {
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch(NoSuchAlgorithmException nsax) {
			md = null;
		}
	}

	/** Initialize the command with the supplied public key from the client. **/
	void initialize(String args[]) throws CommandException {
		super.initialize(args);
		try {
			BigInteger exp = new BigInteger(args[1], 32);
			BigInteger mod = new BigInteger(args[2], 32);
			
			md.update(exp.toByteArray()); /* First, hash the exp. */
			md.update(mod.toByteArray()); /* Next, hash the mod. */
		} catch(NullPointerException npx) {
			throw new CommandException((new Date()).toString() + 
				": SHA-1 Digest initialization failed, email: " +
				GameParameters.SERVICE_CONTACT + ".");
		} catch(ArrayIndexOutOfBoundsException aioobx) {
			throw new CommandException((new Date()).toString() +
				"Proper Usage: MAKE_CERTIFICATE ARG1 ARG2, ARG1 is the " +
				"exponent portion of the public key, ARG2 is the modulus.");
		}
	}

	/** Execute this Command context. Stores the player's certificate. **/
	public void execute(MonitorSession session) {
		try {
			BigInteger c = new BigInteger(1, md.digest());
			c = session.getMonitorPrivateKey().signNum(c);
			session.getPlayer().createIdentity(c);
			session.sendResult("CERTIFICATE " + session.getPlayer().getIdentity() +
				" " + c.toString(32));
		} catch(NullPointerException npx) {
			session.sendError("Monitor CA Certificate not loaded.");
		}
	}

	/** Returns the name of this command, necessary for Command. **/
	public String getCommandMessage() {
		return this.COMMAND_STRING;
	}
}
