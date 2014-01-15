/**********************************************************************************
  * GetCertificate - Get the public Certificate (Identity) of a player ************
  * used for CA based authentication on transfers *********************************/

/*
  RCS ID:
  $Id: GetCertificateCommand.java,v 1.1 2009/05/13 17:46:09 franco Exp $

  $Revision: 1.1 $

  ChangeLog:
  $Log: GetCertificateCommand.java,v $
  Revision 1.1  2009/05/13 17:46:09  franco
  Initial revision

  Revision 1.1.1.1  2008/04/17 00:44:24  franco


  Revision 1.2  2003/11/26 01:44:27  cokane
  Changed the banner to be representative of the class.

  Revision 1.1  2003/11/26 01:38:43  cokane
  Initial revision

*/
import java.util.*;

class GetCertificateCommand extends Command {
	/** RCS Revision Id. **/
	static final String rcsis = "$Revision: 1.1 $";
	public static final String COMMAND_STRING = "GET_CERTIFICATE";
	String targetPlayer;

	/** Returns the name of this command. **/
	String getCommandMessage() {
		return new String(COMMAND_STRING);
	}

	/** Initialize the command parameters. **/
	void initialize(String args[]) throws CommandException {
		super.initialize(args);
		targetPlayer = null;

		try {
			targetPlayer = args[1];
		} catch(ArrayIndexOutOfBoundsException aoobx) {
			throw new CommandException((new Date()).toString() + ": command " +
					getCommandMessage() + " requires one argument <PLAYERNAME> to " +
					"be sent.");
		}
	}
	/** Execute the command. **/
	public void execute(MonitorSession session) {
		if(targetPlayer.equalsIgnoreCase("MONITOR")) {
			try {
				session.sendResult("CERTIFICATE " + GameParameters.MONITOR_IDENTITY + " " +
						session.getMonitorCertificate().getCertificate());
			} catch(NullPointerException npx) {
				session.sendError("No monitor certificate loaded.");
			}
			return;
		}
		Player dest = session.getPlayerDB().lookup(targetPlayer);
		if(dest == null) {
			session.sendError("No Player " + targetPlayer + ".");
			return;
		}

		String cert = dest.getCertIdentity();
		if(cert == null) {
			session.sendError(targetPlayer.toUpperCase() + " has not Certificate registered.");
			return;
		}

		session.sendResult("CERTIFICATE " + targetPlayer.toUpperCase() + " " + cert);
	}
}
