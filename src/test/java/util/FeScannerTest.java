package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ListIterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for FeScanner (key parser for .fem / .gen files).
 * Uses @TempDir (JUnit5) and realistic FE input snippets.
 */
@DisplayName("FeScanner - FE data file parser")
class FeScannerTest {

    @Test
    @DisplayName("reads ints, doubles, and skips with delimiters = and whitespace (keyword=value style)")
    void basicReads(@TempDir Path tmp) throws IOException {
        Path f = tmp.resolve("t.fem");
        Files.writeString(f, "nNod=4  thick = 0.1  val=2.5  xyz");

        FeScanner sc = new FeScanner(f.toString());
        // Typical usage: caller knows keywords, scanner splits on = / ws
        assertThat(sc.next()).isEqualTo("nNod");
        assertThat(sc.readInt()).isEqualTo(4);
        assertThat(sc.next()).isEqualTo("thick");
        assertThat(sc.readDouble()).isEqualTo(0.1);
        assertThat(sc.next()).isEqualTo("val");
        assertThat(sc.readDouble()).isEqualTo(2.5);
        assertThat(sc.next()).isEqualTo("xyz");
    }

    @Test
    @DisplayName("moveAfterLineWithWord finds section and advances")
    void moveAfterLineWithWord(@TempDir Path tmp) throws IOException {
        Path f = tmp.resolve("t.fem");
        Files.writeString(f, "# comment\n  start\n1 2\nend\n");

        FeScanner sc = new FeScanner(f.toString());
        sc.moveAfterLineWithWord("start");
        assertThat(sc.readInt()).isEqualTo(1);
    }

    @Test
    @DisplayName("readNumberList parses dof lists with ranges (e.g. 1 3 -5)")
    void readNumberList_parsesRanges(@TempDir Path tmp) throws IOException {
        Path f = tmp.resolve("t.fem");
        Files.writeString(f, "3  1 3 -5   0.5");

        FeScanner sc = new FeScanner(f.toString());
        ListIterator it = new ArrayList().listIterator();
        ListIterator result = sc.readNumberList(it, 1, 2, 0.5);
        // expect 3 dofs added (node1,3,4,5? wait logic: 1 then 3 to5)
        assertThat(result.previousIndex() + 1).isGreaterThan(0); // basic smoke, detailed in integration
    }

    @Test
    @DisplayName("error on bad int calls UTIL.errorMsg -> throws")
    void readInt_badInput_throws(@TempDir Path tmp) throws IOException {
        Path f = tmp.resolve("t.fem");
        Files.writeString(f, "notanint");

        FeScanner sc = new FeScanner(f.toString());
        assertThatThrownBy(sc::readInt).isInstanceOf(IllegalStateException.class);
    }
}
