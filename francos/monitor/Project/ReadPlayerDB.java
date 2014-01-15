import java.io.*;
import java.util.Enumeration;

class ReadPlayerDB {
   PlayerDB playerDB;
      
   ReadPlayerDB(String filename) {
      playerDB = null;
      try {
         FileInputStream fis = new FileInputStream(filename);
         ObjectInputStream ois = new ObjectInputStream(fis);
         playerDB = (PlayerDB) ois.readObject();
      }
      catch (Exception e) {
         System.out.println("CANNOT READ DB: " + e);
      }
   }
      
   void processPlayers() {
      SillyPlayerWrapper sp = 
        new SillyPlayerWrapper("FOO", playerDB.getEconomy());
      for (Enumeration e = playerDB.getPlayers() ; e.hasMoreElements() ; ) {
         Player curPlayer = (Player) e.nextElement();
         
         System.out.println("\nPlayer: "+curPlayer.getIdentity());
         String paswd = curPlayer.playerPassword;
         System.out.print("Password: ");
         if (paswd != null) {
            System.out.println(paswd);
            System.out.print("Byte-by-byte: ");
            byte[] bytes = paswd.getBytes();
            for (int i=0 ; i < bytes.length ; i++)
               System.out.print(bytes[i]+"+");
            System.out.println();
            System.out.print("Password: ");
            for (int i=0 ; i < bytes.length ; i++)
              if (bytes[i] < 30) System.out.print("@");
              else System.out.print((char)bytes[i]);            
            System.out.println();
         } else {
            System.out.println("(null)");
         }
         
         System.out.println("Cookie: "+curPlayer.getMonitorPassword());
      }
//          PrintStream file = null;
//          try
//          {
//             FileOutputStream f = new FileOutputStream(
//                           GameParameters.STATISTICS_FILE);

//             file = new PrintStream(f);

//          }
//          catch (IOException ioe)
//          {
//             System.out.println("UNABLE TO OPEN STATISTICS LOG!");
//          }
         
      }
      
      public static void main(String argv[]) {
         ReadPlayerDB rpd = new ReadPlayerDB(argv[0]);
         rpd.processPlayers();
      }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
