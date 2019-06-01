/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesystem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author panos
 */
public class User implements Serializable{
    private String name;                //όνομα
    private String login;               //login name
    private byte[] salt;                //το μοναδικό τυχαίως δημιουργημένο salt
    private String encrypted_password;  //ο κρυπτογραφημένος κωδικός
    private KeyGen k;                   //η κλάση με τα κλειδιά του χρηστη
    
    public User(String name, String login, String password, KeyGen k) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        this.name=name;             //ο construtor
        this.login=login;
        this.k=k;
        
        salt = new byte[16];    //αρχικοποίηση salt
        SecureRandom random = new SecureRandom();       //παραγωγή ενός ισχυρού κρυπτογραφικά αντικειμένου random
        random.nextBytes(salt);             //δημιουργία του τυχαίου salt
        
        MessageDigest md = MessageDigest.getInstance("SHA3-512");       //παραγωγή αντικειμενου MessageDigest που περιεχει ετοιμους αλγοριθμους σύνοψης (επιλέγουμε SHA3-512)
        md.update(salt);                //προσθέτουμε το salt στη σύνοψη
        
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));       //με τη μέθοδο digest και το αντικείμενο md από πριν hashάρουμε τον κωδικό μας
        
        // Δημιουργία key και αλγοριθμου για symmetric
        Key aesKey = new SecretKeySpec(k.getPubicKey(login).getBytes(), "AES-256");
        Cipher cipher = Cipher.getInstance("AES-256");      //o αλγόριθμος
        
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);       //κρυπτογραφεί
        encrypted_password = Base64.getEncoder().encodeToString(cipher.doFinal(hashedPassword));        //βάζει τον κρυπτογραφημένο κωδικό μετα απο συνοψη
        
        /*
        // decrypt the text
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        String decrypted = new String(cipher.doFinal(cipher.doFinal(hashedPassword)));
        System.err.println(decrypted);
                */
        
        File directory = new File("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\" + login);       //δημιουργώ το directory για κάθε χρήστη
        if (!directory.exists()) {      //αν δεν υπαρχει ήδη
            if (directory.mkdir()) {        //αν έγινε επιστρέφει μήνυμα επιτυχίας
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");      //αλλιώς αποτυχίας
            }
        }
        
    }
    //getters
    public String getName(){
        return this.name;
    }
    
    public String getLogin(){
        return this.login;
    }
    
    public byte[] getSalt(){
        return this.salt;
    }
    
    public String getEncrypted_password(){
        return this.encrypted_password;
    }
    
    public KeyGen getKeyGen(){
        return this.k;
    }
}
