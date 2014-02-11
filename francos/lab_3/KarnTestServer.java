import java.io.*;
import java.math.*;
import java.security.*;
import java.net.*;

public class KarnTestServer {
    public static void main (String arg[]) {
        BufferedReader in;
        PrintWriter out;

        // Use Diffie Hellman to create a shared secret
        DiffieHellmanExchange dhe = new DiffieHellmanExchange();
        try {
            dhe.getDHParmMakePublicKey("DHKey");
        } catch (Exception e) {
            System.out.println("Error in getting DHKey from file.");
            System.exit(1);
        }

        try {
            // Wait for a connection from a client then connect
            ServerSocket socket = new ServerSocket(8280);
            Socket connect = socket.accept();
            in = new BufferedReader(
                    new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter (connect.getOutputStream(), true);

            // Build a Karn encryptor from the shared secret
            Karn karn = new Karn(dhe.getSecret(in, out));

            // Receive encrypted message from client and decrypt
            String ciphertext = in.readLine();
            System.out.println("Server: ciphertext:"+ciphertext+"\n");
            String plaintext = karn.decrypt(ciphertext);
            System.out.println("Server: plaintext:"+plaintext);

            // Leave
        } catch (Exception e) {
            System.out.println("Whoops! - no network");
        }
    }
}
