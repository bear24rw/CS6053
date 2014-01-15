import java.math.*;
import java.security.*;
import java.io.*;

class SignThis {
	public static void main(String args[]) {
		BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			System.out.print("V=");
			BigInteger v = new BigInteger(in.readLine(), 32);
			System.out.print("N=");
			BigInteger n = new BigInteger(in.readLine(), 32);

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
						"../../Final/RSAPriv"));
			RSA secret = (RSA)ois.readObject();
			ois.close();
			md.update(v.toByteArray());
			md.update(n.toByteArray());
			BigInteger fp = new BigInteger(1, md.digest());
			System.out.println(secret.signNum(fp).toString(32));
		} catch(Exception x) {
			System.err.println("There was an error");
		}
	}
}
