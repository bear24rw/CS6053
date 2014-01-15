import java.math.*;

class SubSetACommand extends Command {
   private static String rcsid = "$Revision: 1.1 $";
   public static String COMMAND_STRING = "SUBSET_A";
   private int argnum;
   String subset;
   boolean error_flag = false;
   int last = -1;
   
   public String getCommandMessage() {
      return new String(this.COMMAND_STRING);
   }
   
   public void initialize(String args[]) throws CommandException {
      super.initialize(args);
      try {
         last = -1;
         subset = "";
         argnum = args.length - 1;
         for (int i=1; i < args.length; i++) {
            int h = Integer.parseInt(args[i].trim());
            if (h <= last) error_flag = true;
            last = h;
            subset += args[i].trim() + " ";
         }
         subset = subset.trim(); // There will be a trailing space
      } catch(ArrayIndexOutOfBoundsException ax) {
         throw new CommandException("SUBSET_A Usage: SUBSET_A " +
                            "ARG1 ARG2 ... ARGn, n < number of ROUNDS.");
      }
   }
   
   public boolean verify(MonitorSession session) {
      if (!session.transferring()) {
         session.sendError("SUBSET_A not available, must use " +
                           "TRANSFER_REQUEST");
         return false;
      }
      if (argnum > session.getTransferRounds()) {
         session.sendError("SUBSET_A cannot have more elements " +
                           "than "+session.getTransferRounds()+".");
         return false;
      }
      if (argnum < 1) {
         session.sendError("SUBSET_A must have at least one element.");
         return false;
      }
      if (error_flag) {
         session.sendError("SUBSET_A must be an increasing sequence.");
         return false;
      }
      if (last >= session.getTransferRounds()) {
         session.sendError("SUBSET_A indices must all be less than "+
                           session.getTransferRounds()+".");
         return false;
      }
      return true;
   }
   
   public void execute(MonitorSession session) {
      session.setSubSetA(subset);
   }
}
