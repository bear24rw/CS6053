import java.io.*;
import java.util.*;

public class MakeStandings {
   PlayerDB playerDB;
   Economy economy;
   double rupyulars;

   MakeStandings(String filename) {
      playerDB = null;
      try {
         FileInputStream fis = new FileInputStream(filename);
         ObjectInputStream ois = new ObjectInputStream(fis);
         playerDB = (PlayerDB) ois.readObject();
         ois.close();
         economy = playerDB.getEconomy();
         rupyulars 
            = economy.getResourceValueByName("rupyulars").getMarketValue();
      }
      catch (Exception e) {
         System.out.println("CANNOT READ DB: " + e);
      }
   }
	
   void processPlayers() {
      Player p[] = new Player[1500];
      double w[] = new double[1500];
      FileWriter os = null;
      
      try {
         FileOutputStream fos = new FileOutputStream("standings8150.html");
         os = new FileWriter(fos.getFD());
	 
         Date date = new Date();
         String ds = date.toString();
	 
         // Print the file header
         os.write(
           "<html>\n"+
           "<head>\n"+
           "<META http-equiv=\"Refresh\" CONTENT=\"120;url=http://gauss.ececs.uc.edu/test.html\">\n"+
           "<title>Test Results</title>\n"+
           "</head>\n"+
           "<body bgcolor=\"#ffffdf\">\n"+
           "<center>\n"+
           "<table cellspacing=10>\n"+
           "<tr>\n"+
           "<td align=\"CENTER\" width=300><font size=-1>20-CS-653-001</font></td>\n"+
           "<th><b><font size=+2 color=\"#BB0000\">Network Security</font></b></th>\n"+
           "<td align=\"CENTER\" width=300><font size=-1>Fall 2010</font></td>\n"+
           "</tr>\n"+
           "</table><p>\n"+
           "<font size=+2 color=\"#0000BB\"><b><nobr>Official Test Results</nobr></b></font><p>\n"+
           "<p><font size=-1 color=\"#cc0000\">"+ds+"</font><p>\n"+
           //"<p><font size=-1 color=\"#cc0000\">Mon Dec 06 00:08:26 EST 2010</font><p>\n"+
           //"<font color=\"#bb0000\"></b>Tentative Winners:</b></font> Brett Toothman, James Orr, Ryan Anderson, Sridharan Gopalakrishnan, Vivek Goyal<p>\n"+
           //"<font size=-1 color=\"#770077\">Disputes may be emailed to franco@gauss.ececs.uc.edu, Dec 6</font><br>\n"+
           //"<font size=-1 color=\"#007777\">See <a href=\"http://gauss.ececs.uc.edu/Courses/c653/extra/cottier.txt\">DC2's explanation</a> of the most exciting game end ever</font><p>\n"+
           "</center>\n"+
           "<p>\n"+
           "<table>\n"+
           "<tr><td></td><td></td><td></td><td></td><td></td><th><u>Transfers</u></th><td></td></tr>\n"+
           "<th><u>Participant</u></th>"+
           "<th><u>Rank</u></th>"+
           "<th><u>Points</u></th>"+
           "<th>&nbsp;&nbsp;&nbsp;</th>"+
           "<th><u>Initiate</u></th>"+
           "<th><u>Accept</u></th>"+
           "<th><u>Decline</u></th>"+
           "<th>&nbsp;&nbsp;&nbsp;</th>"+
           "<th><u>Amount Out</u></th>"+
           "<th><u>Amount In</u></th></tr>\n");

         int index = 0;
         for (Enumeration e = playerDB.getPlayers() ; e.hasMoreElements() ; ) {
            Player curPlayer = (Player) e.nextElement();
            Wealth wealth = curPlayer.getWealth();
            long rup = wealth.getHolding("rupyulars");
            double amount = rup*rupyulars;
            
            int i = index;
            for ( ; i > 0 && amount > w[i-1] ; i--) {
               w[i] = w[i-1];
               p[i] = p[i-1];
            }
            w[i] = amount;
            p[i] = curPlayer;
            index++;
         }
				
         for (int i=0 ; i < index ; i++) {
            SillyPlayerWrapper sp = 
               new SillyPlayerWrapper("Foo",playerDB.getEconomy());
            
            int leng = p[i].getIdentity().length();
            if (leng > 20) leng = 20;
            String player_name = p[i].getIdentity().substring(0,leng);
            if (p[i].getIdentity().equals("PPL")) player_name += "*";
            os.write(
              "<tr><td>"+player_name+"</td>"+
              "<td align=\"CENTER\">"+(i+1)+"</td>"+
              "<td align=\"CENTER\">"+(long)w[i]+"</td><td>  </td>"+
              "<td align=\"CENTER\">"+sp.getWarsWon(p[i])+"</td>"+
              "<td align=\"CENTER\">"+sp.getWarsLost(p[i])+"</td>"+
              "<td align=\"CENTER\">"+sp.getWarsDeclared(p[i])+"</td>"+
              "<td></td>"+
              "<td align=\"CENTER\">"+sp.getWarsTruce(p[i])+"</td>"+
              "<td align=\"CENTER\">"+sp.getWarsFought(p[i])+"</td></tr>\n");
         }
         os.write("</table><p>\n");
			/***
			os.write("<font color=#0000bb size=+1><b>Teams:</b></font>\n");
			os.write("<table>\n");
			os.write("<tr><td>&nbsp;&nbsp;&nbsp;</td>\n");
			os.write("<td colspan=2 align=left><b>Klafter's Marauders</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Matthew Jackson, Richard Klafter, Hasso Pape</td></tr>\n");	
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>LuckyDay, DustyBottoms, Nederlander</td></tr>\n");		
			os.write("<tr><td></td><td colspan=2 align=left><b>Banzhaf's Rangers</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Kelly Erickson, Rob Meyer, Brett Kizer, Matt Banzhaf</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Lone_Rangers_I, _II, _III</td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>Team C Bandits</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Nick Foltz, Ben Kossenjans, Edward Kimball</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>TeamC_I, _II, _III</td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>O'Briens's Monsters</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Nick LaRoche, Jeremy O'Brien, Jake Ledbetter</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>TheCookieMonster_I, _II, _III</td></tr>\n");
			os.write("<tr><td></td><td colspan=2 align=left><b>Shelton's Stooges</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Mike Henderson, Ryan Smith, Jim Munafo, Mike Shelton</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Moe, Larry, Curly</td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>Code Warriors</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Tim Rapp, Ryan Davis, Derek Baker</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>IPv6_I, _II, _III</td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>The Phalanx</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>David Haynes, Gregg Trueb</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Phalanx_I, _II, _III</td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>The Lethal Weapons</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Aaron Hoffman, Abhishek Pandey, William Brown</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Lethal_Weapons_I, _II, _III</td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>DC2</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Derek Carson, Devin Cottier</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Twilight_Sparkle, Applejack, Rarity</td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>Wyatt's Attackers</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Neal Wyatt, Gary Sigrist</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Attacker_1, _2, _3</td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>The Tels</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Anusha Darmaraj, Jagadeesh Patchala, Sahithi Chintapalli</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Tel1, Tel2, Tel3<td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>The Testers</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Brett Toothman, James Orr, Ryan Anderson, Sridharan Gopalakrishnan, Vivek Goyal</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Test1, Test2, Test3<td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>The Boilermakers</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Joseph Boeckman, Jeff Weber</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Boilermakers_I, _II, _III<td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>The Disney Ducks</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>John Townsend, Joshua Burbrink</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Huey, Louie, Dewey<td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>Creighton's Hot Rods</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Creighton Long, Evan Chiu, Matthew White</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Car, Ram, Rod<td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>TJ's Eclectics</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>TJ Ellis, Arjun Bakshi, Opeyemi Oyediran</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>TJ, The_Runt, SuperGeekNinja<td></tr>\n");			
			os.write("<tr><td></td><td colspan=2 align=left><b>Barber's Portsmouths</b></td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Kristin Barber, Saibal Ghosh, Suryadip Chakraborty</td></tr>\n");
			os.write("<tr><td></td><td>&nbsp;&nbsp;&nbsp;<td>Shoelace_red, Shoelace_blue, Shoelace_green<td></tr>\n");			
			os.write("</table><p>\n");
         ***/
         os.write("</body>\n</html>\n");
         os.close();
      }
      catch (Exception e) { System.out.println(e); }
   }
   
   public static void main(String argv[]) {
      MakeStandings ms = new MakeStandings(argv[0]);
      ms.processPlayers();
   }
}
/*
  Local Variables:
  tab-width: 4
  indent-tabs-mode: nil
  eval: (c-set-style "ellemtel")
  End:
*/
