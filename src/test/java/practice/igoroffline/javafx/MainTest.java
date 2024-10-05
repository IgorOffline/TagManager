package practice.igoroffline.javafx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class MainTest {

    @Test
    void tag() {
        final var tag = Service.extractTagFromLine("my first line [[hello]]");
        Assertions.assertEquals(tag, Optional.of("hello"));
    }

    @Test
    void split() {
        final var tagCounter = "hello$10";
        final var split = tagCounter.split(Service.SPLIT_REGEX);
        Assertions.assertEquals(split[0], "hello");
        Assertions.assertEquals(split[1], "10");
    }
}
