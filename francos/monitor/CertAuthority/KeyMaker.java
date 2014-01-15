import java.math.*;
import java.io.*;
import java.lang.*;
import java.security.*;

public class KeyMaker {
	static RSA r = new RSA();

	static public void main(String args[]) {
		if (args.length > 2) {
			try {
				/* Write the PrivateKey */
				ObjectOutputStream oos = 
				  new ObjectOutputStream(new FileOutputStream(args[0]));
				oos.writeObject(r);
				oos.close();

				oos = new ObjectOutputStream(new FileOutputStream(args[1]));
				oos.writeObject(r.publicKey());
				oos.close();
				
				/* Build the hash */
				MessageDigest md = MessageDigest.getInstance("SHA-1");
				md.update(r.publicKey().getExponent().toByteArray());
				md.update(r.publicKey().getModulus().toByteArray());
				BigInteger h = r.signNum(new BigInteger(1, md.digest()));
				PlayerCertificate c = new PlayerCertificate(h, "MONITOR");
				
				oos = new ObjectOutputStream(new FileOutputStream(args[2]));
				oos.writeObject(c);
				oos.close();
			} catch(Exception x) {
				System.out.println("There was an error building the keyfile");
			}
		} else {
			System.out.println("Usage: KeyMaker <private-key-file> <public-key-file> <certificate-file>");
		}
	}
}
