package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

import java.net.URL;
import java.util.ResourceBundle;

public class TeamTabController implements Initializable {

    @FXML
    private Text teamName;

    @FXML
    private Text teamContact;

    @FXML
    private Text teamMembers;

    @FXML
    private ImageView teamLogo;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            prop.load(input);
            teamName.setText(prop.getProperty("team.name"));
            teamContact.setText(prop.getProperty("team.contact"));
            teamMembers.setText(String.join("\n", prop.getProperty("team.members").split(", ")));
            Image image = new Image(prop.getProperty("team.logo.path"));
            teamLogo.setImage(image);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
