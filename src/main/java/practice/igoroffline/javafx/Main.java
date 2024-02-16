package practice.igoroffline.javafx;

import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main extends Application {

    public static final String TAG_START = "[[";
    public static final String TAG_END = "]]";
    public static final String JOIN_CHAR = "$";
    public static final String SPLIT_REGEX = "\\$";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("TagManager");

        final var textField = new TextField();

        final List<FileTags> fileTags = new ArrayList<>();

        final var btnDir = new Button();
        final var btnFileTags = new Button();
        btnFileTags.setDisable(true);
        final var btnTagCounterList = new Button();
        btnTagCounterList.setDisable(true);

        btnDir.setText("Dir");
        btnDir.setOnAction(event -> {
            final var input = textField.getText();
            System.out.println(input);
            try {
                final var file = new File(input);
                final var isDir = FileUtils.isDirectory(file);
                System.out.println(isDir);
                if (isDir) {
                    Iterator<File> iterator = FileUtils.iterateFiles(file, null, true);
                    var counterFiles = 0;
                    var counterTags = 0;
                    while (iterator.hasNext()) {
                        final var next = iterator.next();
                        final List<String> tags = new ArrayList<>();
                        final var lines = FileUtils.readLines(next, StandardCharsets.UTF_8);
                        for (final var line : lines) {
                            final var tag = extractTagFromLine(line);
                            if (tag.isPresent()) {
                                tags.add(tag.get());
                                counterTags++;
                            }
                        }
                        fileTags.add(new FileTags(next, lines, tags));
                        counterFiles++;
                    }
                    System.out.format("counterFiles= %d, counterTags=%d%n", counterFiles, counterTags);
                    btnFileTags.setDisable(false);
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });

        final List<Map.Entry<String, Integer>> tagCounterList = new ArrayList<>();

        btnFileTags.setText("FileTags");
        btnFileTags.setOnAction(event -> {
            final Map<String, Integer> tagCounter = new HashMap<>();
            for (final var fileTag : fileTags) {
                for (final var tag : fileTag.tags()) {
                    if (tagCounter.containsKey(tag)) {
                        int counter = tagCounter.get(tag);
                        counter++;
                        tagCounter.put(tag, counter);
                    } else {
                        tagCounter.put(tag, 1);
                    }
                }
            }
            final var sorted =
                    tagCounter.entrySet().stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).toList();
            tagCounterList.addAll(sorted);

            System.out.println(tagCounterList);
            btnTagCounterList.setDisable(false);
        });

        final var listViewTagCounter = new ListView<String>();

        btnTagCounterList.setText("TagCounterList");
        btnTagCounterList.setOnAction(event -> {
            List<String> items = new ArrayList<>();
            for (final var tagCounter : tagCounterList) {
                final var tagCounterToString = tagCounter.getKey() + JOIN_CHAR + tagCounter.getValue();
                items.add(tagCounterToString);
            }
            listViewTagCounter.getItems().clear();
            listViewTagCounter.getItems().addAll(items);
        });

        final var listViewTagFile = new ListView<String>();
        final List<String> listViewTagFileItems = new ArrayList<>();

        listViewTagCounter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            listViewTagFileItems.clear();
            final var split = newValue.split(SPLIT_REGEX);
            final var tag = split[0];
            for (final var fileTag : fileTags) {
                for (final var line : fileTag.lines()) {
                    if (line.contains(TAG_START + tag + TAG_END)) {
                        listViewTagFileItems.add(line);
                    }
                }
            }
            listViewTagFile.getItems().clear();
            listViewTagFile.getItems().addAll(listViewTagFileItems);
        });

        final var hb = new HBox();
        hb.getChildren().addAll(textField, btnDir, btnFileTags, btnTagCounterList);
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

    public static Optional<String> extractTagFromLine(String line) {

        try {
            final var sb = new StringBuilder();

            var indexStart = -1;
            final var indexEnd = line.indexOf(TAG_END);

            if (indexEnd != -1) {
                indexStart = line.indexOf(TAG_START);
                if (indexStart != -1) {
                    for (int i = indexStart + 2; i < indexEnd; i++) {
                        sb.append(line.charAt(i));
                        if (sb.length() > 31) {
                            return Optional.empty();
                        }
                    }
                }
            }

            return sb.isEmpty() ? Optional.empty() : Optional.of(sb.toString());

        } catch (Exception ex) {

            System.err.println(ex.getMessage());
            return Optional.empty();
        }
    }
}
