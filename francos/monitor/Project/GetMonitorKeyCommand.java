/**********************************************************************************
  * GetMonitorKeyCommand - Get the Public Key of the monitor **********************
  * Allow a participant to query the public key of the monitor, to be used later **
  * in verification. **************************************************************/

/*
  RCS ID:
  $Id: GetMonitorKeyCommand.java,v 1.1 2009/05/13 17:46:09 franco Exp $

  $Revision: 1.1 $

  ChangeLog:
  $Log: GetMonitorKeyCommand.java,v $
  Revision 1.1  2009/05/13 17:46:09  franco
  Initial revision

  Revision 1.1.1.1  2008/04/17 00:44:24  franco


  Revision 1.1  2003/11/26 00:06:34  cokane
  Initial revision

*/

class GetMonitorKeyCommand extends Command {
	/** RCS Revision ID. **/
	static final String rcsid = "$Revision: 1.1 $";

	public static final String COMMAND_STRING = "GET_MONITOR_KEY";

	/** Returns the name of this command. **/
	String getCommandMessage() {
		return new String(COMMAND_STRING);
	}

	/** Execute this commad. **/
	public void execute(MonitorSession session) {
		session.sendResult("MONITOR_KEY " + 
				session.getMonitorPublicKey().getModulus().toString(32));
	}
}
