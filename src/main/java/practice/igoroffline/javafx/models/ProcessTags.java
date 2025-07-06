package practice.igoroffline.javafx.models;

import java.util.List;
import java.util.Map;

public record ProcessTags(List<String> tagCounterList, Map<String, List<String>> tagLines) {}
