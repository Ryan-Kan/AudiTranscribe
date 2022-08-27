/*
 * ThemeSetupViewController.java
 *
 * Created on 2022-08-27
 * Updated on 2022-08-27
 *
 * Description: View controller that helps the user set up the theme.
 */

package site.overwrite.auditranscribe.setup_wizard.view_controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import site.overwrite.auditranscribe.io.IOMethods;
import site.overwrite.auditranscribe.io.data_files.DataFiles;
import site.overwrite.auditranscribe.misc.MyLogger;
import site.overwrite.auditranscribe.misc.Theme;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * View controller that helps the user set up the theme.
 */
public class ThemeSetupViewController implements Initializable {
    // FXML elements
    @FXML
    private AnchorPane rootPane;

    @FXML
    private ChoiceBox<Theme> themeChoiceBox;

    @FXML
    private Button confirmButton;

    // Initialization method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Theme theme : Theme.values()) themeChoiceBox.getItems().add(theme);

        themeChoiceBox.setValue(Theme.values()[DataFiles.SETTINGS_DATA_FILE.data.themeEnumOrdinal]);
        themeChoiceBox.setOnAction(event -> {
            setThemeOnScene(themeChoiceBox.getValue());
            MyLogger.log(Level.INFO, "Changed theme to " + themeChoiceBox.getValue(), this.getClass().getName());
        });

        confirmButton.setOnAction(event -> ((Stage) rootPane.getScene().getWindow()).close());

        MyLogger.log(Level.INFO, "Showing theme setup view", this.getClass().getName());
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

    /**
     * Method that gets the theme ordinal.
     * @return An integer, representing the ordinal of the theme selected.
     */
    public int getSelectedThemeOrdinal() {
        return themeChoiceBox.getValue().ordinal();
    }
}
