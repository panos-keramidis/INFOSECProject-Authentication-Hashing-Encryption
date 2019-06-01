/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesystem;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author panos
 */
public class FileSystemGUI extends Application {
    
    private Stage primaryStage;
    private static FileSystem filesystem;        //ο controler 
    
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;   //το παραθυρο
               
        this.primaryStage.setMinHeight(350);    //σετάρω το παράθυρο να έχει συγκεκριμένο μέγεθος
        this.primaryStage.setMinWidth(500);
        this.primaryStage.setMaxHeight(350);
        this.primaryStage.setMaxWidth(500);
        
        primaryStage.setTitle("Σύστημα Διαχείρισης αρχείων");   //ο τίτλος
        set_primary_stage();
    } //Η συνάρτηση που αρχικοποιει το GUI 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        filesystem = new FileSystem();
        launch(args);
                
    }  //Launch
    
    public void set_primary_stage() {      //η αρχική σελίδα
        Pane pane = new Pane();     //ο πίνακας με τα περιεγχόμενα στο GUI
        Button btn = new Button();  //το πρώτο κουμπί (της συνδεσης)
        Button btn2 = new Button();  //το δευτερο κουμπί κουμπί (της εγγραφης)
        pane.setPrefSize(500, 350);

        btn.setText("Σύνδεση");        //ο τίτλος του
        btn.setOnAction(e -> {login();});  //όταν το πατάει ο χρήστης τι να ενεργοποιείται
        
        btn2.setText("Εγγραφή");        //ο τίτλος του
        btn2.setOnAction(e -> {signup();});  //όταν το πατάει ο χρήστης τι να ενεργοποιείται
        
        Text text1 = new Text();        //δυο κείμενα καλωσορίσματος
        Text text2 = new Text();
        text1.setText("Καλωσήλθατε στο σύστημα διαχείρισης αρχείων!");
        text2.setText("Επιλέξτε λειτουργία χρήστη");

        text1.setX(90);     //η θέση τους στον πίνακα του GUI
        text1.setY(50);

        text2.setX(150);
        text2.setY(70);

        StackPane root = new StackPane();   //ο κόμβος root
        pane.getChildren().add(btn);        //προσθέτω τα πάντα στον πίνακα του GUI
        pane.getChildren().add(btn2);        //προσθέτω τα πάντα στον πίνακα του GUI
        
        pane.getChildren().add(text1);
        pane.getChildren().add(text2);

        btn.setLayoutX(210);  //η τοποθεσία των κουμπιών
        btn.setLayoutY(102.5);  
        
        btn2.setLayoutX(207);  //η τοποθεσία των κουμπιών
        btn2.setLayoutY(142.5);
        
        root.getChildren().add(pane);

        primaryStage.setScene(new Scene(root, 500, 350));
        primaryStage.show();
    }
    
        
    public void login() {
        Pane pane = new Pane(); //ο πίνακας
        pane.setPrefSize(500, 350);

        Text text1 = new Text();    //εισαγωγικό κείμενο
        text1.setText("Συμπληρώστε τα στοιχεία σας");

        text1.setX(115);    //η διαρύθμισή του
        text1.setY(20);

        TextField login = new TextField();     //τα πεδία συμπλήρωσης
        login.setPromptText("Login name");    //έχουν προσυμπληρωμένο το τι θέλουμε να συμπληρώσει ο χρήστης
        login.setFocusTraversable(false);  

        login.setLayoutX(10);      //η διαρύθμισή τους
        login.setLayoutY(30);

        TextField password = new TextField();
        password.setPromptText("Password");
        password.setFocusTraversable(false);

        password.setLayoutX(300);
        password.setLayoutY(30);

        Button btn2 = new Button(); //το κουμπί κράτησης
        btn2.setText("Login");
        
        
        btn2.setOnAction(e -> {     //πατώντας το κουμπί login
            try {                   
                if(filesystem.verifyPassword(login.getText(), password.getText(), new KeyGen(login.getText()))!=null){      //ελέγχει την εγκυρότητα των στοιχείων
                    start(filesystem.verifyPassword(login.getText(), password.getText(), new KeyGen(login.getText())));     //ξεκιναει το πρόγραμμα με τον χρήστη που αντιστοιχεί στα στοιχεία του login
                }
                else{       //αλλιως βγαζει alert
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("ERROR");
                    alert.setHeaderText(null);
                    alert.setContentText("Κάποιο από τα στοιχεία σας είναι λάθος");
                    alert.showAndWait();
                }
            } catch (NoSuchAlgorithmException | InvalidKeyException | IOException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | InterruptedException ex) {
                Logger.getLogger(FileSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        });
        
        btn2.setLayoutX(210);  //η διαρύθμισή του
        btn2.setLayoutY(257.5);  

        StackPane root = new StackPane();   //ο root
        pane.getChildren().add(login);     //προσθέτω τα πάντα σε αυτόν
        pane.getChildren().add(password);
        pane.getChildren().add(btn2);
        pane.getChildren().add(text1);

        root.getChildren().add(pane);

        primaryStage.setScene(new Scene(root, 500, 350));
        primaryStage.show();
    }
    
    public void signup() {  
        Pane pane = new Pane(); //ο πίνακας
        pane.setPrefSize(500, 350);

        Text text1 = new Text();    //εισαγωγικό κείμενο
        text1.setText("Συμπληρώστε τα στοιχεία σας");

        text1.setX(115);    //η διαρύθμισή του
        text1.setY(20);

        TextField login = new TextField();     //τα πεδία συμπλήρωσης
        login.setPromptText("Login name");    //έχουν προσυμπληρωμένο το τι θέλουμε να συμπληρώσει ο χρήστης
        login.setFocusTraversable(false);  

        login.setLayoutX(10);      //η διαρύθμισή τους
        login.setLayoutY(30);

        TextField password = new TextField();
        password.setPromptText("Password");
        password.setFocusTraversable(false);

        password.setLayoutX(300);
        password.setLayoutY(30);
        
        TextField name = new TextField();
        name.setPromptText("Name");
        name.setFocusTraversable(false);

        name.setLayoutX(10);
        name.setLayoutY(80);

        Button btn2 = new Button(); //το κουμπί κράτησης
        btn2.setText("Sign up");
        
        btn2.setOnAction((ActionEvent e) -> {       //αν πατήσει το κουμπί για εγγραφή
            try {
                if(filesystem.checkAvailability(login.getText())){      //αν το όνομα είναι διαθέσιμο
                    KeyGen k = new KeyGen(login.getText());         //δημιουργώ κλειδια
                    User user = new User(name.getText(), login.getText(), password.getText(), k);       //χρήστη
                    filesystem.addUser(user);       //τον προσθέτω στο σύστημα
                    start(user);        //ξεκινάει με τον νεο αυτο χρήστη
                }
                else{       //αλλιώς alert
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("ERROR");
                    alert.setHeaderText(null);
                    alert.setContentText("Το login name είναι δεσμευμένο");
                    alert.showAndWait();
                }
            } catch (IOException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InterruptedException ex) {
                Logger.getLogger(FileSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        btn2.setLayoutX(210);  //η διαρύθμισή του
        btn2.setLayoutY(257.5);  

        StackPane root = new StackPane();   //ο root
        pane.getChildren().add(login);     //προσθέτω τα πάντα σε αυτόν
        pane.getChildren().add(password);
        pane.getChildren().add(name);
        pane.getChildren().add(btn2);
        pane.getChildren().add(text1);

        root.getChildren().add(pane);

        primaryStage.setScene(new Scene(root, 500, 350));
        primaryStage.show();
    }
    
    public void start(User user) throws IOException, InterruptedException {
        Pane pane = new Pane();     //ο πίνακας περιεγχομένων
        pane.setPrefSize(500, 350);
        Button btn = new Button();      //δυο ανάλογα με την επιλογή του χρήστη
        Button btn2 = new Button();
        Button btn3 = new Button();
        Button btn4 = new Button();
        Button btn5 = new Button();
        
        btn.setText("Μετακινήστε αρχείο στο safedirectory");    //μετακίνηση στο safedirectory
        btn.setOnAction(e -> move(user));

        btn2.setText("Εύρεση συγκεκριμένου φακέλου");    //εύρεση συγκεκριμένου φακέλου βάση ονόματος
        btn2.setOnAction(e -> findCertain(user));
        
        btn3.setText("Άνοιγμα φακέλων");    //άνοιγμα των φακέλων
        btn3.setOnAction(e -> open(user));
        
        btn4.setText("Προβολή λίστας");    //προβολή λίστας των φακέλων στο directory του χρήστη
        btn4.setOnAction(e -> list(user));
        
        btn5.setText("Προσθήκη αρχείου");    //προσθήκη αρχείου
        btn5.setOnAction(e -> add(user));

        Text text1 = new Text();    //δυο απλά κείμενα
        Text text2 = new Text();
        text1.setText("Καλώς ήρθατε " + user.getName()); //ok
        text2.setText("Επιλέξτε μία από τις λειτουργίες!");

        text1.setX(160);    //η διαρύθμισή τους
        text1.setY(50);

        text2.setX(180);
        text2.setY(90);
        
        if(filesystem.violations(user.getLogin())){     //αν ο χρήστης έχει στο αρχείο του κάποιες παραβάσεις των προσωπικών του αρχείων πρέπει να ενημερώνεται με την είσοδό του με alert
            Alert alert = new Alert(Alert.AlertType.WARNING);       
            alert.setTitle("Warning");
            alert.setHeaderText("Κάποιος παραβίασε τα αρχεία στο προσωπικό σας directory!");
            alert.setResizable(false);
            alert.setContentText("Θέλετε να ανοίξετε το ιστορικό καταγραφής των παραβιάσεων;");

            Optional<ButtonType> result = alert.showAndWait();
            ButtonType button = result.orElse(ButtonType.CANCEL);

            if (button == ButtonType.OK) {      //αν επιλέξει να το δει τώρα
                filesystem.openFile(new File("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users\\" + user.getLogin() + "\\violations.txt"));         //ανοίγει το violations file του
                TimeUnit.SECONDS.sleep(1);      //περιμένει ένα δευτερόλεπτο (συμβολικά για να μπορεί να τα διαγράψει αμέσως αφότου κλείσει το file όχι νωρίτερα)
                new FileWriter("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users\\" + user.getLogin() + "\\violations.txt", false).close();     //και τα διαγράφει εφόσον πλέον είναι αναγνωσμένα
            }
        }

        
        StackPane root = new StackPane();   //ο κόμβος root
        pane.getChildren().add(btn);    //τα προσθέτω στον πίνακα
        pane.getChildren().add(btn2);
        pane.getChildren().add(btn3);
        pane.getChildren().add(btn4);
        pane.getChildren().add(btn5);
        pane.getChildren().add(text1);
        pane.getChildren().add(text2);

        btn.setLayoutX(140);  //η διαρύθμιση των κουμπιών
        btn.setLayoutY(102.5);  

        btn2.setLayoutX(140);  
        btn2.setLayoutY(145);
        
        btn3.setLayoutX(190);  
        btn3.setLayoutY(187.5);
        
        btn4.setLayoutX(190);  
        btn4.setLayoutY(230);
        
        btn5.setLayoutX(180);  
        btn5.setLayoutY(270);

        root.getChildren().add(pane);

        primaryStage.setScene(new Scene(root, 500, 350));
        primaryStage.show();
    }
    
    public void move(User user){
        Pane pane = new Pane();     //ο πίνακας περιεγχομένων
        pane.setPrefSize(500, 350);
               
        FileChooser fileChooser = new FileChooser();        //ένας filechooser
        fileChooser.getExtensionFilters().addAll(           //τι τύπους αρχείων δέχεται
         new FileChooser.ExtensionFilter("Text Files", "*.txt"),
         new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
         new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
        
        fileChooser.setInitialDirectory(new File("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users\\" + user.getLogin()));      //μετακινώ μόνο από το αρχείο του χρήστη
        
        Button button = new Button("Επιλέξτε φάκελο για να μεταφέρετε");
        button.setOnAction(e -> {       //όταν επιλέξει
            File selectedFile = fileChooser.showOpenDialog(primaryStage);       //βάζω σε ενα file το αρχείο που επιλέγει
            try {   
                filesystem.moveFile(selectedFile);          //το μετακινώ
            } catch (IOException ex) {
                Logger.getLogger(FileSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        button.setLayoutX(140);  //η διαρύθμιση των κουμπιών
        button.setLayoutY(102.5);  
        
        StackPane root = new StackPane();
        pane.getChildren().add(button);
        root.getChildren().add(pane);

        primaryStage.setScene(new Scene(root, 500, 350));
        primaryStage.show();
    }
    
    public void findCertain(User user){
        Pane pane = new Pane(); //ο πίνακας
        pane.setPrefSize(500, 350);

        Text text1 = new Text();    //εισαγωγικό κείμενο
        text1.setText("Συμπληρώστε τα στοιχεία σας");

        text1.setX(115);    //η διαρύθμισή του
        text1.setY(20);

        TextField search = new TextField();     //τα πεδία συμπλήρωσης
        search.setPromptText("Όνομα αρχείου");    //έχουν προσυμπληρωμένο το τι θέλουμε να συμπληρώσει ο χρήστης
        search.setFocusTraversable(false);  

        search.setLayoutX(150);      //η διαρύθμισή τους
        search.setLayoutY(30);

        Button btn2 = new Button(); //το κουμπί κράτησης
        btn2.setText("Search");
        
        
        btn2.setOnAction(e -> {              //μόλις το πατήσει 
            Desktop desktop = Desktop.getDesktop();
            try {
                File f = filesystem.findFile(search.getText(), new File("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users"));       //ψαχνει για το όνομα του αρχείου στο προσωπικό του directory
                filesystem.decrypt(user.getKeyGen().getSymmetricKey(user.getLogin()), f, f);        //αφού το βρει το αποκρυπτογραφεί
                desktop.open(f);            //το ανοίγει
                filesystem.encrypt(user.getKeyGen().getSymmetricKey(user.getLogin()), f, f);        //και το ξανακρυπτογραφεί αφού το κλείσει
            } catch (IOException ex) {
                Logger.getLogger(FileSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        
        btn2.setLayoutX(210);  //η διαρύθμισή του
        btn2.setLayoutY(257.5);  

        StackPane root = new StackPane();   //ο root
        pane.getChildren().add(search);     //προσθέτω τα πάντα σε αυτόν
        pane.getChildren().add(btn2);
        pane.getChildren().add(text1);

        root.getChildren().add(pane);

        primaryStage.setScene(new Scene(root, 500, 350));
        primaryStage.show();
    }
    
    public void open(User user){
       Pane pane = new Pane();     //ο πίνακας περιεγχομένων
        pane.setPrefSize(500, 350);
        
        ArrayList<String> unauthorized = new ArrayList();
        
        FileChooser fileChooser = new FileChooser();        //filechooser
        fileChooser.getExtensionFilters().addAll(           //τα αρχεια που ανοίγουν
         new FileChooser.ExtensionFilter("Text Files", "*.txt"),
         new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
         new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
        
        fileChooser.setInitialDirectory(new File("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users"));      //στον χώρο των χρηστών
        Button button = new Button("Επιλέξτε φάκελο για να ανοίξετε");
        button.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);       //ανοίγει τον χώρο των χρηστών
             Desktop desktop = Desktop.getDesktop();
            try {
                filesystem.decrypt(user.getKeyGen().getSymmetricKey(user.getLogin()), selectedFile, selectedFile);      //αποκρυπτογραφεί αυτό που θα επιλέξει
                desktop.open(selectedFile);         //το ανοίγει
                if(!selectedFile.getParentFile().getName().equals(user.getLogin())){        //αν το αρχείο δεν ειναι στο δικό του directory
                    unauthorized.add(selectedFile.getParentFile().getName());       //καταγράφει την κίνηση αυτή
                    //και τη γραφει στου αντίστοιχου χρήστη το αντίστοιχο unauthorized.txt
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users\\" + unauthorized.get(unauthorized.size() - 1) + "\\violations.txt", true))) {
                        writer.append(' ');
                        writer.append("Actor: " + user.getLogin() + " , broke into: " + unauthorized.get(unauthorized.size() - 1) + ", at: " + new java.util.Date());
                        writer.newLine();
                        writer.close();
                    }
                }
                filesystem.encrypt(user.getKeyGen().getSymmetricKey(user.getLogin()), selectedFile, selectedFile);
            } catch (IOException ex) {
                Logger.getLogger(FileSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        });
        
        button.setLayoutX(140);  //η διαρύθμιση των κουμπιών
        button.setLayoutY(102.5);  
        
        StackPane root = new StackPane();
        pane.getChildren().add(button);
        root.getChildren().add(pane);

        primaryStage.setScene(new Scene(root, 500, 350));
        primaryStage.show();
    }
    
    public void list(User user){
        ListView<String> m_listView;
        Group root = new Group();

	// οριζόντιο panel για λίστα 
	HBox listViewPanel = new HBox();
	listViewPanel.setSpacing(10);

	// το κείμενο μετά την επιλογή
	Text label = new Text("Επιλέξτε ένα όνομα για να ανοίξετε");
	label.setFont(Font.font(null, FontWeight.BOLD, 16));
        
        File files = new File("C:\\Users\\panos\\Documents\\NetBeansProjects\\FileSystem\\users\\" + user.getLogin());  //τα αρχεία του χρήστη
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(files.list()));       //τα βάζει στη λίστα

	m_listView = new ListView<String>(FXCollections.observableArrayList(names));    //η λίστα
        m_listView.prefWidth(100);
	m_listView.setMaxWidth(100);
	m_listView.getSelectionModel().selectedItemProperty().addListener((
                ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
           Desktop desktop = Desktop.getDesktop();
            try {
                File f = filesystem.findFile(m_listView.getSelectionModel().getSelectedItem(), files);      //η επιλογή του χρήστη
                filesystem.decrypt(user.getKeyGen().getSymmetricKey(user.getLogin()), f, f);            //το αποκρυπτογραφεί
                desktop.open(f);                //και το ανοίγει
                filesystem.encrypt(user.getKeyGen().getSymmetricKey(user.getLogin()), f, f);        //το ξανακρυπτογραφει
            } catch (IOException ex) {
                Logger.getLogger(FileSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

	listViewPanel.getChildren().addAll(m_listView, label);
	root.getChildren().addAll(listViewPanel);
        
        
	Scene scene = new Scene(root);
	primaryStage.setTitle("Διαχείριση αρχείων");
	primaryStage.setScene(scene);
	primaryStage.show();
    }
    
    public void add(User user){
        Pane pane = new Pane(); //ο πίνακας
        pane.setPrefSize(500, 350);

        Text text1 = new Text();    //εισαγωγικό κείμενο
        text1.setText("Συμπληρώστε τα στοιχεία σας");

        text1.setX(115);    //η διαρύθμισή του
        text1.setY(20);

        TextField add = new TextField();     //τα πεδία συμπλήρωσης
        add.setPromptText("Όνομα αρχείου");    //έχουν προσυμπληρωμένο το τι θέλουμε να συμπληρώσει ο χρήστης
        add.setFocusTraversable(false);  

        add.setLayoutX(150);      //η διαρύθμισή τους
        add.setLayoutY(30);

        

        Button btn2 = new Button(); //το κουμπί κράτησης
        btn2.setText("Προσθήκη");
        
        
        btn2.setOnAction(e -> {     //αφού το πατησει πετάει alert          
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File Creation");
            alert.setHeaderText("Result:");
            try {
                alert.setContentText(filesystem.createFile(add.getText(), user));       //το αποτέλεσμα στο alert είναι αυτό που θα επιστρέψει η συνάρτηση δημιουργίας φακέλου
            } catch (IOException ex) {
                Logger.getLogger(FileSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
            }

            alert.showAndWait(); 
            
        });
        
        btn2.setLayoutX(210);  //η διαρύθμισή του
        btn2.setLayoutY(257.5);  

        StackPane root = new StackPane();   //ο root
        pane.getChildren().add(add);     //προσθέτω τα πάντα σε αυτόν
        pane.getChildren().add(btn2);
        pane.getChildren().add(text1);

        root.getChildren().add(pane);

        primaryStage.setScene(new Scene(root, 500, 350));
        primaryStage.show();
    }
    
}
