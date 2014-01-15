import java.util.Date;
import java.security.*;
import java.math.*;
import java.io.*;

/*
 * This object is used for Public Key Exchange. 
 * The Crypto routines require it.  I haven't put the heavy
 * duty methods in here because I want it to stay small
 */ 

class DHKey implements Serializable {
   BigInteger p, g;    /* These two make up the public Key */

   String Description;
   Date created;
   
   DHKey (BigInteger P, BigInteger G, String what) {
      p = P;
      g = G;
      
      Description = what;
      created = new Date();
   }

   /* You may wish to customize the following */
   public String toString() {
      StringBuffer scratch = new StringBuffer();
      scratch.append("Public Key(p): " + p.toString(32) + "\n" );
      scratch.append("Public Key(g): " + g.toString(32) + "\n" );
      scratch.append("Description: "   + Description  + "\n" );
      scratch.append("Created: "       + created );
      return scratch.toString();
   }
}

public class PlantDHKey {
   public static void main (String arg[]) {
      try {
			BigInteger p = new BigInteger("7897383601534681724700886135766287333879367007236994792380151951185032550914983506148400098806010880449684316518296830583436041101740143835597057941064647");
			BigInteger g = new BigInteger("2333938645766150615511255943169694097469294538730577330470365230748185729160097289200390738424346682521059501689463393405180773510126708477896062227281603");
			DHKey key = new DHKey(p,g,"C653 DH key");
			FileOutputStream fos = new FileOutputStream("DHKey");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(key);
      } catch (Exception e) {
			System.out.println("Whoops!");
      }
   }
}
