import java.math.*;

class RoundsCommand extends Command {
   private static String rcsid = "$Revision: 1.1 $";
   public static String COMMAND_STRING = "ROUNDS";
   private int rounds;

   public String getCommandMessage() {
      return new String(this.COMMAND_STRING);
   }

   public void initialize(String args[]) throws CommandException {
      super.initialize(args);
      try {
         if (args.length > 2)
            throw new ArrayIndexOutOfBoundsException();
         rounds = Integer.parseInt(args[1], 10);
         if (rounds < 1)
            throw new NumberFormatException();
      } catch(ArrayIndexOutOfBoundsException ax) {
         throw new CommandException("ROUNDS: Usage ROUNDS ARG1" +
                    " ARG1 is the number of chosen rounds.");
      } catch(NumberFormatException nfx) {
         throw new CommandException("ROUNDS: Usage ROUNDS ARG1" +
                    " ARG1 must be a positive non-zero decimal number.");
      }
   }
   
   public boolean verify(MonitorSession session) {
      if (!session.transferring()) {
         session.sendError("ROUNDS not available, must use " +
                           "TRANSFER_REQUEST");
         return false;
      }
      if (session.getTransferPubKey() == null) {
         session.sendError("PUBLIC_KEY has not been sent");
         return false;
      }
      return true;
   }
   
   public void execute(MonitorSession session) {
      session.setTransferRounds(rounds);
   }
}
