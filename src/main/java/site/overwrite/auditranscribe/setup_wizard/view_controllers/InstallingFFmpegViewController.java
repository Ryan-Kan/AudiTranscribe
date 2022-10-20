/*
 * InstallingFFmpegViewController.java
 * Description: View controller that show instructions on how to install FFmpeg.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public Licence as published by the Free Software Foundation, either version 3 of the
 * Licence, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public Licence for more details.
 *
 * You should have received a copy of the GNU General Public Licence along with this program. If
 * not, see <https://www.gnu.org/licenses/>
 *
 * Copyright © AudiTranscribe Team
 */

package site.overwrite.auditranscribe.setup_wizard.view_controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import site.overwrite.auditranscribe.generic.ClassWithLogging;
import site.overwrite.auditranscribe.io.IOMethods;
import site.overwrite.auditranscribe.misc.Theme;
import site.overwrite.auditranscribe.utils.GUIUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * View controller that show instructions on how to install FFmpeg.
 */
public class InstallingFFmpegViewController extends ClassWithLogging implements Initializable {
    // FXML Elements
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Hyperlink ffmpegHyperlink;

    @FXML
    private Button confirmButton;

    // Initialization method
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ffmpegHyperlink.setOnAction(event -> GUIUtils.openURLInBrowser("https://ffmpeg.org/"));
        confirmButton.setOnAction(event -> ((Stage) rootPane.getScene().getWindow()).close());

        log(Level.INFO, "Showing instructions to install FFmpeg");
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
