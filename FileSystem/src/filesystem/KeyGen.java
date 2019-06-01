/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesystem;

/**
 *
 * @author panos
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class KeyGen {
    private PrivateKey privateKey;          //το Private Key αποθηκεύεται plane text καταχρηστικά για λόγους της εργασίας

    public KeyGen(String login) throws IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
        //παίρνω ως βάση το μοναδικό login
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");       //keygenerator με βαση Digital Signature Algorithm και SUN πάροχο

            // Αρχικοποίηση KeyPairGenerator.
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);

            // Δημιουργια των private key & public key.
            KeyPair keyPair = keyGen.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
             /*
            encoder = Base64.getEncoder();
            System.out.println("privateKey: " + encoder.encodeToString(privateKey.getEncoded()));
            System.out.println("publicKey: " + encoder.encodeToString(publicKey.getEncoded()));
            System.out.println(this.privateKey);
            System.out.println(this.publicKey);
            */
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\public.txt", true))) {
                writer.append(login + " " + publicKey + "\n");      //γραφω τα public keys στο αντιστοιχο αρχείο
                writer.close();
            }
            
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES-256");        //για τη δημιουργία του symmetric παρόμοια διαδικασία
            keyGenerator.init(168);
            SecretKey secretKey = keyGenerator.generateKey();
            Cipher cipher = Cipher.getInstance("AES-256");
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            String encryptedSymmetric = Base64.getEncoder().encodeToString(cipher.doFinal(secretKey.getEncoded()));     //το κρυπτογραφώ
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\" + login + "Symmetric.txt"))) {
                writer.write(encryptedSymmetric);       //το βάζω στο αντίστοιχο αρχείο που είναι στο repository καθε χρηστη
                writer.close();
            }
            
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
        }
    }

    public PrivateKey getPrivateKey(){      //getter για το private key
        return this.privateKey;
    }

    public String getPubicKey(String loginname) throws FileNotFoundException, IOException{      //getter για τον public key από το αρχείο
        String login;
        String privateEnc=null;
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\public.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                login = parts[0];           // login
                if(loginname == null ? login == null : loginname.equals(login)){
                    privateEnc = parts[1];      // public key
                }

            }
        }
        return privateEnc;
    }
    
    public String getSymmetricKey(String login) throws FileNotFoundException, IOException{      //getter για το symmetric key από το αντίστοιχο αρχείο
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\" + login + "Symmetric.txt"))) {
            line = br.readLine();

        }
        return line;
    }
    
}