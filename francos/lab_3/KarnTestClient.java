import java.io.*;
import java.net.*;

public class KarnTestClient {
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
            // Connect to the server
            Socket connect = new Socket("localhost", 8280);
            in = new BufferedReader(
                    new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream(), true);

            // Build a Karn encryptor from the shared secret
            Karn karn = new Karn(dhe.getSecret(in, out));

            // Encrypt plaintext from the command line and send it to Server
            String plaintext = arg[0];
            System.out.println("Client: plaintext:"+plaintext+"\n");
            String ciphertext = karn.encrypt(plaintext);
            System.out.println("Client: ciphertext:"+ciphertext+"\n");
            out.println(ciphertext);

            // Leave
        } catch (Exception e) {
            System.out.println("Yikes!");
        }
    }
}
