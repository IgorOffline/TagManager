package practice.igoroffline.javafx;

import java.io.File;
import java.util.List;

public record FileTags(File file, List<String> lines, List<String> tags) {
}
