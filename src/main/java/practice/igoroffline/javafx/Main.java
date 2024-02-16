package practice.igoroffline.javafx;

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
import java.util.stream.Collectors;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Hello World!");

        final var textField = new TextField();

        final List<FileTags> fileTags = new ArrayList<>();

        final var btnDir = new Button();
        btnDir.setText("Dir");
        btnDir.setOnAction(event -> {
            final var input = textField.getText();
            System.out.println(input);
            try {
                var file = new File(input);
                var isDir = FileUtils.isDirectory(file);
                System.out.println(isDir);
                if (isDir) {
                    Iterator<File> iterator = FileUtils.iterateFiles(file, null, true);
                    var counterFiles = 0;
                    var counterTags = 0;
                    while (iterator.hasNext()) {
                        var next = iterator.next();
                        List<String> tags = new ArrayList<>();
                        var lines = FileUtils.readLines(next, StandardCharsets.UTF_8);
                        for (var line : lines) {
                            var tag = extractTagFromLine(line);
                            if (tag.isPresent()) {
                                tags.add(tag.get());
                                counterTags++;
                            }
                        }
                        fileTags.add(new FileTags(next, tags));
                        counterFiles++;
                    }
                    System.out.format("counterFiles= %d, counterTags=%d%n", counterFiles, counterTags);
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });

        final var btnFileTags = new Button();
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
            var sorted =
                    tagCounter.entrySet().stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toList());
            System.out.println(sorted);
        });

        final var hb = new HBox();
        hb.getChildren().addAll(textField, btnDir, btnFileTags);
        hb.setSpacing(10);
        hb.setPadding(new Insets(10, 10, 10, 10));

        final var root = new StackPane();
        root.getChildren().add(hb);
        stage.setScene(new Scene(root, 1280, 720));
        stage.show();
    }

    public static Optional<String> extractTagFromLine(String line) {

        try {
            var sb = new StringBuilder();

            var indexStart = -1;
            var indexEnd = line.indexOf("]]");

            if (indexEnd != -1) {
                indexStart = line.indexOf("[[");
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
