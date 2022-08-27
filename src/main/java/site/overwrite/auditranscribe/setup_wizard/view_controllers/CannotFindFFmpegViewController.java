/*
 * CannotFindFFmpegViewController.java
 *
 * Created on 2022-06-19
 * Updated on 2022-08-27
 *
 * Description: View controller of the view that reports the FFmpeg could not be found.
 */

package site.overwrite.auditranscribe.setup_wizard.view_controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import site.overwrite.auditranscribe.io.IOMethods;
import site.overwrite.auditranscribe.misc.MyLogger;
import site.overwrite.auditranscribe.misc.Theme;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * View controller of the view that reports the FFmpeg could not be found.
 */
public class CannotFindFFmpegViewController implements Initializable {
    // Attributes
    private boolean isSpecifyManually;

    // FXML Elements
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button specifyManuallyButton, readInstructionsAgainButton;

    // Initialization method
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        specifyManuallyButton.setOnMouseClicked(event -> {
            isSpecifyManually = true;
            ((Stage) rootPane.getScene().getWindow()).close();
        });
        readInstructionsAgainButton.setOnMouseClicked(event -> {
            isSpecifyManually = false;
            ((Stage) rootPane.getScene().getWindow()).close();
        });

        MyLogger.log(Level.INFO, "Can't find FFmpeg", this.getClass().getName());
    }

    // Getter methods

    /**
     * Method that returns the value of <code>isSpecifyManually</code>.
     *
     * @return Value of <code>isSpecifyManually</code>.
     */
    public boolean getIsSpecifyManually() {
        return isSpecifyManually;
    }

    // Public methods

    /**
     * Method that sets the scene's theme.
     *
     * @param theme Theme to set.
     */
    public void setThemeOnScene(Theme theme) {
        rootPane.getStylesheets().clear();  // Reset the stylesheets first before adding new ones

        rootPane.getStylesheets().add(IOMethods.getFileURLAsString("views/css/base.css"));
        rootPane.getStylesheets().add(IOMethods.getFileURLAsString("views/css/" + theme.cssFile));
    }
}
