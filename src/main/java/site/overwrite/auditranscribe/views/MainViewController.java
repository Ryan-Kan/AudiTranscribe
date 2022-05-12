/*
 * MainViewController.java
 *
 * Created on 2022-02-09
 * Updated on 2022-05-12
 *
 * Description: Contains the main view's controller class.
 */

package site.overwrite.auditranscribe.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.javatuples.Quartet;
import site.overwrite.auditranscribe.io.audt_file.ProjectIOHandlers;
import site.overwrite.auditranscribe.io.PropertyFile;
import site.overwrite.auditranscribe.io.db.ProjectsDB;
import site.overwrite.auditranscribe.utils.MiscUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Helper classes
class SortByTimestamp implements Comparator<Quartet<Long, String, String, String>> {
    @Override
    public int compare(
            Quartet<Long, String, String, String> o1, Quartet<Long, String, String, String> o2
    ) {
        return (int) -(o1.getValue0() - o2.getValue0());  // Sort in descending order
    }
}

class CustomListCell extends ListCell<Quartet<Long, String, String, String>> {
    // FXML elements
    HBox content;

    StackPane shortNameDisplayArea;
    Rectangle shortNameRectangle;
    Text shortNameText;

    Label nameLabel;
    Label lastOpenedDateLabel;
    Label filepathLabel;

    public CustomListCell() {
        // Call superclass initialization method
        super();

        // Create all labels and texts
        nameLabel = new Label();
        lastOpenedDateLabel = new Label();
        filepathLabel = new Label();

        shortNameText = new Text();

        // Set CSS classes on text labels
        nameLabel.getStyleClass().add("project-name-label");
        lastOpenedDateLabel.getStyleClass().add("last-opened-date-label");
        filepathLabel.getStyleClass().add("filepath-label");

        shortNameText.getStyleClass().add("short-name-text");

        // Create the short name display area
        shortNameRectangle = new Rectangle();
        shortNameRectangle.getStyleClass().add("short-name-rectangle");

        shortNameRectangle.setFill(Color.TRANSPARENT);  // Todo: randomly assign colours
        shortNameRectangle.setWidth(50);
        shortNameRectangle.setHeight(50);

        shortNameDisplayArea = new StackPane();
        shortNameDisplayArea.getChildren().addAll(shortNameRectangle, shortNameText);

        // Set the content
        HBox nameAndDateBox = new HBox(nameLabel, lastOpenedDateLabel);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        HBox.setHgrow(lastOpenedDateLabel, Priority.ALWAYS);
        nameAndDateBox.setSpacing(10);

        VBox nameDateAndFilepathBox = new VBox(nameAndDateBox, filepathLabel);
        nameDateAndFilepathBox.setSpacing(5);

        content = new HBox(shortNameDisplayArea, nameDateAndFilepathBox);
        content.setSpacing(15);
    }

    @Override
    protected void updateItem(Quartet<Long, String, String, String> object, boolean empty) {
        // Call superclass method
        super.updateItem(object, empty);

        // Ensure that the object is not null and non-empty
        if (object != null & !empty) {
            // Convert the timestamp to a date string
            lastOpenedDateLabel.setText(
                    "[Last opened on " +
                            MiscUtils.formatDate(new Date(object.getValue0()), "yyyy-MM-dd HH:mm") +
                            "]"
            );
            nameLabel.setText(object.getValue1());
            filepathLabel.setText(object.getValue2());
            shortNameText.setText(object.getValue3());
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }
}

// Main class
public class MainViewController implements Initializable {
    // Attributes
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    // FXML Elements
    @FXML
    private Label versionLabel;

    @FXML
    private Button newProjectButton, openProjectButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private ListView<Quartet<Long, String, String, String>> projectsListView;

    // Initialization method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Quartet<Long, String, String, String>> projects = new ArrayList<>();

        try {
            // Get the projects database
            ProjectsDB projectsDB = new ProjectsDB();

            // Get all projects' records
            Map<Integer, Pair<String, String>> projectRecords = projectsDB.getAllProjects();

            // Get all the keys
            Set<Integer> keys = projectRecords.keySet();

            // For each file, get their last accessed time and generate the shortened name
            for (int key : keys) {
                // Get both the filepath and the filename
                Pair<String, String> values = projectRecords.get(key);
                String filepath = values.getKey();
                String filename = values.getValue();

                // Get the last accessed time of the file
                BasicFileAttributes attributes = Files.readAttributes(Path.of(filepath), BasicFileAttributes.class);
                FileTime lastAccessTime = attributes.lastAccessTime();
                long lastAccessTimestamp = lastAccessTime.toMillis();

                // Get the shortened name of the file name
                filename = filename.substring(0, filename.length() - 5);  // Exclude the ".audt" at the end
                String shortenedName = MiscUtils.getShortenedName(filename);

                // Add to the list of projects
                projects.add(new Quartet<>(
                        lastAccessTimestamp, filename, filepath, shortenedName
                ));
            }

            // Sort the list of projects by the last access timestamp
            projects.sort(new SortByTimestamp());

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        // Convert the `projects` list to an FXML `ObservableList`
        ObservableList<Quartet<Long, String, String, String>> projectsList = FXCollections.observableList(projects);

        // Handle the rest of the startup
        try {
            // Get the project properties file
            PropertyFile projectPropertiesFile = new PropertyFile("project.properties");

            // Update the version label with the version number
            versionLabel.setText("Version " + projectPropertiesFile.getProperty("version"));

            // Add methods to buttons
            newProjectButton.setOnAction(ProjectIOHandlers::newProject);
            openProjectButton.setOnAction(ProjectIOHandlers::openProject);

            // Write the projects list
            projectsListView.setItems(projectsList);
            projectsListView.setCellFactory(customListCellListView -> new CustomListCell());

            // Report that the main view is ready to be shown
            logger.log(Level.INFO, "Main view ready to be shown");

        } catch (IOException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
