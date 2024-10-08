package practice.igoroffline.javafx;

import practice.igoroffline.javafx.models.FileLines;
import practice.igoroffline.javafx.models.FileTags;
import practice.igoroffline.javafx.models.ProcessTags;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Service {

    public static final String TAG_START = "[[";
    public static final String TAG_END = "]]";
    public static final String JOIN_CHAR = "$";
    public static final String SPLIT_REGEX = "\\$";

    public List<FileTags> searchDirectory(String directoryUrl) {

        final List<FileTags> fileTags = new ArrayList<>();

        try {
            final var file = new File(directoryUrl);
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
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        return fileTags;
    }

    public ProcessTags processTags(List<FileTags> fileTags) {

        final List<String> tagCounterList = new ArrayList<>();
        final Map<String, List<String>> tagLines = new HashMap<>();

        final var sorted = sortTagFiles(fileTags);
        final List<Map.Entry<String, List<FileLines>>> sortedTagFiles = new ArrayList<>(sorted);

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

        for (final var tagFiles : sortedTagFiles) {
            final var tagCounterToString = tagFiles.getKey() + JOIN_CHAR + tagFiles.getValue().size();
            tagCounterList.add(tagCounterToString);
        }

        return new ProcessTags(tagCounterList, tagLines);
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

    private List<Map.Entry<String, List<FileLines>>> sortTagFiles(List<FileTags> fileTags) {
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
