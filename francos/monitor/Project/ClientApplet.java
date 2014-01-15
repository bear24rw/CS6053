import java.awt.*;
import java.io.*;
import java.net.*;

class Clients implements Runnable
{
   Thread runner = null;
   Socket socket = null;
   TextField sendout, status;
   TextArea takein;   
   DataInputStream in = null;
   DataOutputStream out = null;
   boolean connected = false;
   ClientApplet clientapplet;
   String name;
   
   public Clients(TextField st, TextField so, TextArea ti, ClientApplet ca, String nm)
   {
      status = st;
      sendout = so;
      takein = ti;
      clientapplet = ca;
      name = nm;
   }
   
   public void go ()
   {
      status.setText("Ready to send");
      runner = new Thread(this);
      runner.start();
   }

   public void disconnect ()
   {
      try
      {
         socket.close();
      }
      catch (Exception e) { status.setText(String.valueOf(e));  }
      status.setText("Connection Closed");
      connected = false;
      clientapplet.setClientNull();
   }
   
   public void connectit ()
   {
      try
      {
			socket = new Socket("helios", 8150);
         in = new DataInputStream(socket.getInputStream());
         out = new DataOutputStream(socket.getOutputStream());

         out.writeBytes(name+"\n");  // Send ID of this process
         status.setText("ID sent");
         connected = true;
         go();
      }
      catch (Exception e)
      {
         status.setText("Connection Refused - Server Running? ("+e+")");
         clientapplet.setClientNull();	 
      }
   }
	 
   public void transmitit()
   {
      try
      {
         out.writeBytes(sendout.getText()+"\n");
         status.setText("Sending to server");
      }
      catch (Exception e)
      {
         status.setText("Cannot Send to Server - Server Down?");
         clientapplet.setClientNull();	 
      }
   }
	 
   public void run ()
   {
      try
      {
         while (true)
         {
            String str = in.readLine();
            takein.appendText(str+"\n");
            if (str.equals("") || str == null) break;
	 		}
      }
      catch (Exception e) { }
      disconnect();
   }
}

public class ClientApplet extends java.applet.Applet
{
   Button sendit, leave, openit;
   TextField sendout, status;
   TextArea takein;
   Clients client = null;
   
   public void init ()
   {
      setLayout(new BorderLayout());
      Panel p1 = new Panel();
      p1.setLayout(new BorderLayout());
      p1.add("North", new Label("Status"));
      p1.add("Center", status = new TextField("No connection yet"));
      add("North", p1);
      Panel p2 = new Panel();
      p2.setLayout(new BorderLayout());
      p2.add("North", new Label("Received From Server"));
      p2.add("Center", takein = new TextArea(10,30));
      p2.add("South", sendout = new TextField(20));		
      add("Center", p2);
      Panel p3 = new Panel();
      p3.setLayout(new GridLayout(1,3,10,10));
      p3.add(sendit = new Button("Send It"));
      p3.add(openit = new Button("Connect"));
      p3.add(leave = new Button("Close"));
      add("South", p3);
   }

   public boolean action (Event evt, Object obj)
   {
      if (evt.target.equals(sendit) && client != null)
         client.transmitit();
      else
      if (evt.target.equals(openit) && client == null)
      {
         client = new Clients(status, sendout, takein, this, getParameter("name"));
         client.connectit();
      }
      else
      if (evt.target.equals(leave) && client != null)
      {
         client.disconnect();
	 client = null;
      }

      return super.action(evt, obj);
   }
   
   public void setClientNull () { client = null; }
}
