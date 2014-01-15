import java.util.*;
import java.net.*;

class ReportTime extends Thread {
   ReportTimeStarter timer;
   DatagramSocket socket;

   public ReportTime (ReportTimeStarter timer) {
      super("Contest Timer");
      this.timer = timer;
      
      try { socket = new DatagramSocket(8444); }
      catch (java.net.SocketException e) {
         System.out.println("Could not create datagram socket.");
      }
   }

   public void run () {
      if (socket == null) return;

      while (true) {
         try {
            byte[] buf = new byte[256];
            DatagramPacket packet;
            InetAddress address;
            int port;

            // receive request
            packet = new DatagramPacket(buf, 256);
            socket.receive(packet);
            address = packet.getAddress();
            port = packet.getPort();

            // send response
	    Date date = new Date();
	    long time = (timer.target - date.getTime())/1000;

	    buf = (new Long(time)).toString().getBytes();
	    packet = new DatagramPacket(buf, buf.length, address, port);
	    socket.send(packet);
         }
         catch (Exception e) {
	    System.out.println("Exception:  " + String.valueOf(e));
         }
      }
   }
}

class ReportTimeStarter {
   Calendar enddate;
   long target;
   String args[];

   public ReportTimeStarter (String args[]) { this.args = args; }

   public void start () {
      try {
	 //enddate = Calendar.getInstance(TimeZone.getTimeZone("EDT"));
	 enddate = Calendar.getInstance();
	 int hour = Integer.parseInt(args[0]);
	 int minute = Integer.parseInt(args[1]);
	 int day = Integer.parseInt(args[2]);
	 int month = Integer.parseInt(args[3]);
	 int year = Integer.parseInt(args[4]);
	 enddate.set(year, month-1, day, hour, minute, 0);
	 target = enddate.getTimeInMillis();
	 (new ReportTime(this)).start();
      } catch (Exception e) {
	 System.out.println("Usage: java ContestTimer <hour> <minute> "+
			    "<day> <month> <year>");
      }
   }
}

public class ContestTimer {
   // usage: java ContestTimer <hour> <minute> <day> <month> <year>
   public static void main (String args[]) {
      (new ReportTimeStarter(args)).start();
   }
}