package practice.igoroffline.javafx;

import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import practice.igoroffline.javafx.models.FileLines;
import practice.igoroffline.javafx.models.FileTags;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        final List<FileTags> fileTags = new ArrayList<>();
        final List<Map.Entry<String, List<FileLines>>> sortedTagFiles = new ArrayList<>();
        final Map<String, List<String>> tagLines = new HashMap<>();

        final var textField = new TextField();

        final var btnDir = new Button();
        btnDir.setText("Dir");
        final var btnTagFiles = new Button();
        btnTagFiles.setDisable(true);
        btnTagFiles.setText("TagFiles");

        final var listViewTagCounter = new ListView<String>();
        final var listViewTagFile = new ListView<String>();

        btnDir.setOnAction(event -> {
            final var input = textField.getText();
            System.out.println(input);
            try {
                final var file = new File(input);
                final var isDir = IOUtils.isDirectory(file);
                System.out.println(isDir);
                if (isDir) {
                    Iterator<File> fileIterator = IOUtils.iterateFiles(file);
                    var counterFiles = 0;
                    var counterTags = 0;
                    while (fileIterator.hasNext()) {
                        final var nextFile = fileIterator.next();
                        final List<String> tags = new ArrayList<>();
                        final var lines = IOUtils.readLines(nextFile);
                        for (final var line : lines) {
                            final var tag = extractTagFromLine(line);
                            if (tag.isPresent()) {
                                tags.add(tag.get());
                                counterTags++;
                            }
                        }
                        final var fileLines = new FileLines(nextFile, lines);
                        fileTags.add(new FileTags(fileLines, tags));

                        counterFiles++;
                    }
                    System.out.format("counterFiles= %d, counterTags=%d%n", counterFiles, counterTags);
                    btnTagFiles.setDisable(false);
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });

        btnTagFiles.setOnAction(event -> {
            final var sorted = sortedTagFiles(fileTags);
            sortedTagFiles.addAll(sorted);

            for (final var tagFiles : sortedTagFiles) {
                final var tag = tagFiles.getKey();
                for (final var files : tagFiles.getValue()) {
                    for (final var line : files.lines()) {
                        if (line.contains(TAG_START + tag + TAG_END)) {
                            if (tagLines.containsKey(tag)) {
                                final var linesList = tagLines.get(tag);
                                final List<String> newLinesList = new ArrayList<>(linesList);
                                newLinesList.add(line);
                                tagLines.put(tag, newLinesList);
                            } else {
                                tagLines.put(tag, List.of(line));
                            }
                        }
                    }
                }
            }

            final List<String> tagCounterItems = new ArrayList<>();
            for (final var tagFiles : sortedTagFiles) {
                final var tagCounterToString = tagFiles.getKey() + JOIN_CHAR + tagFiles.getValue().size();
                tagCounterItems.add(tagCounterToString);
            }

            listViewTagCounter.getItems().addAll(tagCounterItems);
        });

        listViewTagCounter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            final var split = newValue.split(SPLIT_REGEX);
            final var tag = split[0];
            final var lines = tagLines.get(tag);
            final List<String> listViewTagFileItems = new ArrayList<>(lines);
            listViewTagFile.getItems().clear();
            listViewTagFile.getItems().addAll(listViewTagFileItems);
        });

        final var hb = new HBox();
        hb.getChildren().addAll(textField, btnDir, btnTagFiles);
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

    private List<Map.Entry<String, List<FileLines>>> sortedTagFiles(List<FileTags> fileTags) {
        Map<String, List<FileLines>> tagFiles = new HashMap<>();

        for (final var fileTag : fileTags) {
            for (final var tag : fileTag.tags()) {
                if (tagFiles.containsKey(tag)) {
                    final var fileLinesList = tagFiles.get(tag);
                    final List<FileLines> newFileLinesList = new ArrayList<>(fileLinesList);
                    newFileLinesList.add(fileTag.fileLines());
                    tagFiles.put(tag, newFileLinesList);
                } else {
                    tagFiles.put(tag, List.of(fileTag.fileLines()));
                }
            }
        }

        return tagFiles.entrySet().stream().sorted(
                Collections.reverseOrder(Comparator.comparingInt(o -> o.getValue().size()))).toList();
    }
}
