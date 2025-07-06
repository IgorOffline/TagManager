package practice.igoroffline.javafx.models;

import java.io.File;
import java.util.List;

public record FileLines(File file, List<String> lines) {}
