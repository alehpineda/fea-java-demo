package util;

import model.Dof;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testutil.LegacyStateReset;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.ListIterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
@DisplayName("FeScanner")
class FeScannerTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("tokenizes values separated by whitespace and equals signs")
    void tokenizesValuesSeparatedByWhitespaceAndEqualsSigns() throws Exception {
        Path input = writeInput("""
                count = 7
                value = 3.5
                """);

        FeScanner scanner = new FeScanner(input.toString());

        assertThat(scanner.next()).isEqualTo("count");
        assertThat(scanner.readInt()).isEqualTo(7);
        assertThat(scanner.next()).isEqualTo("value");
        assertThat(scanner.readDouble()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("parses ranged degree-of-freedom lists")
    void parsesRangedDegreeOfFreedomLists() throws Exception {
        Path input = writeInput("4 1 3 -5 7");
        FeScanner scanner = new FeScanner(input.toString());
        LinkedList<Dof> dofs = new LinkedList<>();
        ListIterator<Dof> iterator = dofs.listIterator();

        scanner.readNumberList(iterator, 2, 3, 1.5);

        assertThat(dofs)
                .extracting(dof -> dof.dofNum)
                .containsExactly(2, 8, 11, 14, 20);
        assertThat(dofs)
                .extracting(dof -> dof.value)
                .containsExactly(1.5, 1.5, 1.5, 1.5, 1.5);
    }

    @Test
    @DisplayName("moves after a marker line and reads following values")
    void movesAfterAMarkerLineAndReadsFollowingValues() throws Exception {
        Path input = writeInput("""
                # comment
                header
                marker
                42 3.14
                """);
        FeScanner scanner = new FeScanner(input.toString());

        scanner.moveAfterLineWithWord("marker");

        assertThat(scanner.readInt()).isEqualTo(42);
        assertThat(scanner.hasNextDouble()).isTrue();
        assertThat(scanner.nextDouble()).isEqualTo(3.14);
        scanner.close();
    }

    @Test
    @DisplayName("throws readable exceptions for invalid numeric tokens")
    void throwsReadableExceptionsForInvalidNumericTokens() throws Exception {
        Path input = writeInput("answer nope");
        FeScanner scanner = new FeScanner(input.toString());
        scanner.next();

        assertThat(scanner.hasNextDouble()).isFalse();
        assertThat(scanner.hasNext()).isTrue();
        assertThat(scanner.next()).isEqualTo("nope");

        FeScanner invalidIntScanner = new FeScanner(writeInput("value nope").toString());
        invalidIntScanner.next();
        assertThatThrownBy(invalidIntScanner::readInt)
                .isInstanceOf(FeaException.class)
                .hasMessageContaining("Expected integer");

        FeScanner invalidDoubleScanner = new FeScanner(writeInput("value nope").toString());
        invalidDoubleScanner.next();
        assertThatThrownBy(invalidDoubleScanner::readDouble)
                .isInstanceOf(FeaException.class)
                .hasMessageContaining("Expected double");
    }

    @Test
    @DisplayName("resolves sibling file paths relative to the source file")
    void resolvesSiblingFilePathsRelativeToTheSourceFile() throws Exception {
        Path nested = tempDir.resolve("nested");
        Files.createDirectories(nested);
        Path input = nested.resolve("scan.fem");
        Files.writeString(input, "value 1");

        FeScanner scanner = new FeScanner(input.toString());

        assertThat(scanner.resolveSibling("mesh.dat"))
                .isEqualTo(nested.resolve("mesh.dat").toString());
    }

    private Path writeInput(String content) throws Exception {
        Path input = tempDir.resolve("scanner-input.txt");
        Files.writeString(input, content);
        return input;
    }
}
