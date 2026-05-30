package integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

final class ResultFileAssertions {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("[-+]?(?:\\d+\\.\\d*|\\.\\d+|\\d+)(?:[eE][-+]?\\d+)?");

    private ResultFileAssertions() {
    }

    static void assertNumericContentClose(Path actual, Path expected, double tolerance) throws IOException {
        List<Double> actualNumbers = extractNumbers(actual);
        List<Double> expectedNumbers = extractNumbers(expected);
        assertThat(actualNumbers).hasSameSizeAs(expectedNumbers);
        for (int i = 0; i < actualNumbers.size(); i++) {
            assertThat(actualNumbers.get(i))
                    .as("numeric token %s in %s", i, actual.getFileName())
                    .isCloseTo(expectedNumbers.get(i), within(tolerance));
        }
    }

    private static List<Double> extractNumbers(Path path) throws IOException {
        Matcher matcher = NUMBER_PATTERN.matcher(Files.readString(path));
        List<Double> numbers = new ArrayList<>();
        while (matcher.find()) {
            numbers.add(Double.parseDouble(matcher.group()));
        }
        return numbers;
    }
}
