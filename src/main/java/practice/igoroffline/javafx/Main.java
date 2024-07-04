package practice.igoroffline.javafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import practice.igoroffline.javafx.models.ProcessTags;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private final Service service = new Service();
    private ProcessTags processTags;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("TagManager");

        final var tfDirectoryUrl = new TextField();

        final var btnSearch = new Button();
        btnSearch.setText("Search");

        final var listViewTagCounter = new ListView<String>();
        final var listViewTagFile = new ListView<String>();

        btnSearch.setOnAction(event -> {
            final var directoryUrl = tfDirectoryUrl.getText();
            System.out.println(directoryUrl);
            final var fileTags = service.searchDirectory(directoryUrl);
            processTags = service.processTags(fileTags);
            listViewTagCounter.getItems().addAll(processTags.tagCounterList());
        });

        listViewTagCounter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            final var split = newValue.split(Service.SPLIT_REGEX);
            final var tag = split[0];
            final var lines = processTags.tagLines().get(tag);
            final List<String> listViewTagFileItems = new ArrayList<>(lines);
            listViewTagFile.getItems().clear();
            listViewTagFile.getItems().addAll(listViewTagFileItems);
        });

        final var hb = new HBox();
        hb.getChildren().addAll(tfDirectoryUrl, btnSearch);
        hb.setSpacing(10);
        hb.setPadding(new Insets(10, 10, 10, 10));
        final var vb = new VBox();
        vb.getChildren().addAll(hb, listViewTagCounter, listViewTagFile);
        vb.setSpacing(10);
        vb.setPadding(new Insets(10, 10, 10, 10));

        final var root = new StackPane();
        root.getChildren().add(vb);
        stage.setScene(new Scene(root, 1280, 720));
        stage.show();
    }
}
