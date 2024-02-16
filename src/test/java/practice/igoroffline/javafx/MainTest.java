package practice.igoroffline.javafx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class MainTest {

    @Test
    void tag() {
        var tag = Main.extractTagFromLine("my first line [[hello]]");
        Assertions.assertEquals(tag, Optional.of("hello"));
    }
}
