package practice.igoroffline.javafx;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class IOUtils {

    public static boolean isDirectory(File file) {
        try {
            return FileUtils.isDirectory(file);
        } catch (Exception ex) {
            return false;
        }
    }

    public static Iterator<File> iterateFiles(File file) {
        try {
            return FileUtils.iterateFiles(file, null, true);
        } catch (Exception ex) {
            return Collections.emptyIterator();
        }
    }

    public static List<String> readLines(File file) {
        try {
            return FileUtils.readLines(file, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
