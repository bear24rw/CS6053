import java.util.*;
import java.io.*;
import java.net.*;

public class Gauss {
   public static void main (String args[]) {
      DataInputStream in;
      DataOutputStream out;
      try {
			in = new DataInputStream(System.in);
			Socket s = new Socket("helios.ececs.uc.edu",8149);
			out = new DataOutputStream(s.getOutputStream());
			while (true) {
				out.writeByte(in.readByte());
				out.flush();
			} 
      } catch (Exception e) {
			System.out.println(e.toString());
      }
   }
}
