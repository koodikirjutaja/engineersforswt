package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

import java.net.URL;
import java.util.ResourceBundle;

public class TeamTabController implements Initializable {

    private static final Logger log = LogManager.getLogger(TeamTabController.class);

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
        log.info("TeamTabController Initializing");
        log.debug("TeamTabController-initalize");
        Properties prop = new Properties();
        log.debug("TeamTabController-initalize: Created properties");
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                log.debug("TeamTabController-initalize: Cant find properties");
                System.out.println("Sorry, unable to find application.properties");
                log.info("Sorry, unable to find application.properties");
                return;
            }
            log.debug("TeamTabController-initalize: Found properties");
            log.info("Found properties");
            prop.load(input);
            teamName.setText(prop.getProperty("team.name"));
            teamContact.setText(prop.getProperty("team.contact"));
            teamMembers.setText(String.join("\n", prop.getProperty("team.members").split(", ")));
            log.debug("TeamTabController-initalize: Set field values");
            Image image = new Image(prop.getProperty("team.logo.path"));
            teamLogo.setImage(image);
            log.debug("TeamTabController-initalize: Set image path");
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error("TeamTabController-initialize: " + ex.getMessage(), ex);
        }
        log.info("TeamTabController Initialized");
    }
}
