/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesystem;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
public class FileSystem {

    /**
     * @param args the command line arguments
     */

    public boolean checkAvailability(String loginname) throws FileNotFoundException, IOException{
        //συνάρτηση που παίρνει login name και ελέγχει κατά την εγγραφή αν μπορεί να δεσμευτεί το login name ή είναι ήδη δεσμευμένο
        String login;
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\public.txt"))) {     //μπαίνει στο αρχείο όπου κρατώνται τα public keyes ανά user
            String line;
            while ((line = br.readLine()) != null) {        //τα διαβάζει όλα
                String[] parts = line.split(" ");
                login = parts[0];           // login
                if(loginname == null ? login == null : loginname.equals(login)){
                    return false;       //επιστρέφει false αν βρει κάποιον με αυτό το login
                }

            }
        }
        return true;        //αλλιώς επιστρέφει true
        //η συνάρτηση θα μπορούσε να ψάχνει και στο users.txt
    }
    
    public User verifyPassword(String login, String password, KeyGen k) throws NoSuchAlgorithmException, InvalidKeyException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, FileNotFoundException, ClassNotFoundException{
        //συνάρτηση που παίρνει τα στοιχεία και επιστρέφει user
        byte[] salt = new byte[16];    //αρχικοποίηση salt
        SecureRandom random = new SecureRandom();       //παραγωγή ενός ισχυρού κρυπτογραφικά αντικειμένου random
        random.nextBytes(salt);             //δημιουργία του τυχαίου salt
        
        MessageDigest md = MessageDigest.getInstance("SHA3-512");       //παραγωγή αντικειμενου MessageDigest που περιεχει ετοιμους αλγοριθμους σύνοψης (επιλέγουμε SHA3-512)
        md.update(salt);                //προσθέτουμε το salt στη σύνοψη
        
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));       //με τη μέθοδο digest και το αντικείμενο md από πριν hashάρουμε τον κωδικό μας
        
        // Δημιουργία key και αλγορίθμου
        Key aesKey = new SecretKeySpec(k.getPubicKey(login).getBytes(), "AES-256");
        Cipher cipher = Cipher.getInstance("AES-256");
        
        // encrypt 
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        String encrypted_password = Base64.getEncoder().encodeToString(cipher.doFinal(hashedPassword));
        
        if(encrypted_password.equals(returnUserofPassword(login).getEncrypted_password())){
            return (returnUserofPassword(login));       //αν οντως οι συνόψεις κρυπρογραφημένες ισούνται είναι όντως ο χρήστης
        }
        return null;
    }

    public User returnUserofPassword(String login) throws FileNotFoundException, IOException, ClassNotFoundException{
        //συνάρτηση που ψάχνει ανάλογα με το login τον αντίστοιχο χρήστη
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users.txt"));
        //διαβαζει απ το users.txt
        Object obj = null;

        while ((obj = inputStream.readObject()) != null) {
          if (obj instanceof User) {
              if( ((User) obj).getLogin().equals(login) ){
                  return ((User) obj);      //αν βρει εναν χρηστη που να ταυτίζεται με το login τον επιστρέφει
              }
          }
        }
        inputStream.close();
        return null;
    }
    
    public void addUser(User user) throws IOException{
        
        //προσθέτει χρήστη
        FileOutputStream fileOut = new FileOutputStream("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users.txt");
        //στο αρχείο χρηστών
        try (ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(user);
            objectOut.close();
        }
        
    }
    
    public File findFile(String name,File file){
        //συνάρτηση που ψάχνει το file βάση του ονόματος και του αρχείου που βρίσκεται
        File[] list = file.listFiles();     //λίστα αρχείων μέσα στο directory file
        if(list!=null){     //αν δεν ειναι κενή
            for (File fil : list){      //την διατρέχει
                if (fil.isDirectory()){     //αν ο φάκελος είναι κι αυτός directory
                    findFile(name,fil);     //ξανακαλεί τη συνάρτηση αναδρομικά
                }
                else if (name.equalsIgnoreCase(fil.getName())){ //αλλιως αν το βρει
                    return fil;     //το επιστρέφει
                }
            }
        }
        return null;
    }
    
    public void moveFile(File myFile) throws IOException{
        //συνάρτηση που μετακινεί έναν δωθέντα φάκελο στο safedirectory
        String filename = myFile.getName();
        myFile.renameTo(new File("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\safedirectory\\" + filename));
    }
    
    public void encrypt(String key, File inputFile, File outputFile) {
        //κρυπτογράφηση
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
 
    public void decrypt(String key, File inputFile, File outputFile) {
        //αποκρυπτογράφηση
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }
 
    public void doCrypto(int cipherMode, String key, File inputFile, File outputFile) {
        //η ακριβής συνάρητηση κρυπτογράφησης
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), "AES");       //γίνεται ένα secret Key
            Cipher cipher = Cipher.getInstance("AES");      //με αλγόριθμο AES
            cipher.init(cipherMode, secretKey);         //αρχικοποιεί τον αλγόριθμο με το κλειδί
             
            FileOutputStream outputStream;
            try (FileInputStream inputStream = new FileInputStream(inputFile)) {        //ανοίγει το αρχείο
                byte[] inputBytes = new byte[(int) inputFile.length()];     //το κάνει bytes
                inputStream.read(inputBytes);                           //το διαβάζει
                byte[] outputBytes = cipher.doFinal(inputBytes);        //το αποθηκεύει κρυπτογραφημένο
                outputStream = new FileOutputStream(outputFile);        //το γράφω στο αντίστοιχο αρχείο προορισμού (που μπορεί να είναι το ίδιο)
                outputStream.write(outputBytes);
                outputStream.flush();
            }
            outputStream.close();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
        }
    }
    
    public void openFile(File file) throws IOException{
        //αν υπάρχει Desktop διαθέσιμο
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
        }
        
        Desktop desktop = Desktop.getDesktop();
        if(file.exists()) desktop.open(file);       //αν υπαρχει εμφανίζω το αρχείο
    }
    
    public boolean violations(String login){
        //ανοίγω το αρχείο αρχείο παραβάσεων σε αρχεία του χρήστη
        return new File("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users\\" + login + "\\violations.txt")!=null;
    }
    
    public String createFile(String name, User user) throws IOException{
        //δημιουργώ τον φάκελο
        File file = new File("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users\\" + user.getLogin() + "\\name");
  
        //αν έγινε
        if (file.createNewFile()){
            //το κρυπτογραφεί και επιστρέφει μήνυμα επιτυχίας
            encrypt(user.getKeyGen().getSymmetricKey(user.getLogin()), file, file);
            return("File is created!");
        } else {
           return("File already exists.");      //αλλιώς αποτυχίας
        }
    }
    
}
