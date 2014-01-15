import java.math.*;

class AuthorizeSetCommand extends Command {
   private static String rcsid = "$Revision: 1.1 $";
   public static String COMMAND_STRING = "AUTHORIZE_SET";
   private int argnum;
   String subset;
   
   public String getCommandMessage() {
      return new String(this.COMMAND_STRING);
   }
   
   public void initialize(String args[]) throws CommandException {
      super.initialize(args);
      try {
         subset = "";
         argnum = args.length - 1;
         for(int i = 1; i < args.length; i++)
            subset += args[i].trim() + " ";
         subset = subset.trim(); // There will be a trailing space
      } catch(ArrayIndexOutOfBoundsException ax) {
         throw new CommandException("AUTHORIZE_SET Usage: AUTHORIZE_SET " +
             "ARG1 ARG2 ... ARGn, n is the number of chosen rounds.");
      }
   }
   
   public boolean verify(MonitorSession session) {
      if(!session.transferring()) {
         session.sendError("AUTHORIZE_SET not available, must use " +
                           "TRANSFER_REQUEST");
         return false;
      }
      if (argnum > session.getTransferRounds()) {
         session.sendError("AUTHORIZE_SET cannot have more elements " +
                           "than "+session.getTransferRounds()+".");
         return false;
      }
      if (argnum < session.getTransferRounds()) {
         session.sendError("AUTHORIZE_SET cannot have fewer elements " +
                           "than "+session.getTransferRounds()+".");
         return false;
      }
      return true;
   }
   
   public void execute(MonitorSession session) {
      session.setAuthorizeSet(subset);
   }
}
