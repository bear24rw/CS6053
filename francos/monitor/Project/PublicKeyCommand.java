import java.math.*;

class PublicKeyCommand extends Command {
	private static String rcsid = "$Revision: 1.1 $";
	public static String COMMAND_STRING = "PUBLIC_KEY";
	private BigInteger v, n;

	public String getCommandMessage() {
		return new String(COMMAND_STRING);
	}

	public void initialize(String args[]) throws CommandException {
		super.initialize(args);
		try {
			v = new BigInteger(args[1], 32);
			n = new BigInteger(args[2], 32);
		} catch(ArrayIndexOutOfBoundsException ax) {
			throw new CommandException("PUBLIC_KEY Usage: PUBLIC_KEY " +
					"ARG1 ARG2");
		}
	}

	public boolean verify(MonitorSession session) {
		if(!session.transferring()) {
			session.sendError("PUBLIC_KEY not available, must use " +
					"TRANSFER_REQUEST");
			return false;
		}
		if((v == null) || (n == null))
			return false;
		return true;
	}
	
	public void execute(MonitorSession session) {
		session.setTransferPubKey(v, n);
	}
}
