import java.math.*;

class SubSetKCommand extends Command {
   private static String rcsid = "$Revision: 1.1 $";
   public static String COMMAND_STRING = "SUBSET_K";
   private int argnum;
   String subset;
   int size = 0;
   
   public String getCommandMessage() {
      return new String(this.COMMAND_STRING);
   }
   
   public void initialize(String args[]) throws CommandException {
      super.initialize(args);
      size = 0;
      try {
         subset = "";
         argnum = args.length - 1;
         for (int i = 1; i < args.length; i++) {
            subset += args[i].trim() + " ";
            size++;
         }
         subset = subset.trim(); // There will be a trailing space
      } catch (ArrayIndexOutOfBoundsException ax) {
         throw new CommandException("SUBSET_K Usage: SUBSET_K " +
                    "ARG1 ARG2 ... ARGn, n is the number of chosen " +
                    "components from SUBSET A.");
      }
   }
   
   public boolean verify(MonitorSession session) {
      if (!session.transferring()) {
         session.sendError("SUBSET_K not available, must use " +
                           "TRANSFER_REQUEST");
         return false;
      }
      if (argnum > session.getTransferRounds()) {
         session.sendError("SUBSET_K cannot have more elements " +
                           "than ROUNDS specified.");
         return false;
      }
      if (session.getSubsetASize() != size) {
         session.sendError("SUBSET_K must have "+session.getSubsetASize()+
                           " integers.");
         return false;
      }
      return true;
   }
   
   public void execute(MonitorSession session) {
      session.setSubSetK(subset);
   }
}
