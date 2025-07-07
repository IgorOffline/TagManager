package practice.igoroffline.javafx.models;

import java.nio.file.attribute.FileTime;
import java.util.List;

public record FileTags(FileLines fileLines, List<String> tags, FileTime lastModifiedTime) {}
