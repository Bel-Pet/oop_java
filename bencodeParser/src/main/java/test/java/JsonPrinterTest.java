import error.Reporter;
import printer.JsonPrinter;
import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Expr;
import parser.Parser;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonPrinterTest {

    private String getJsonText(String text) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        Reporter reporter = new Reporter(10);

        List<Expr> expressions = Parser.parse(Lexer.scan(br, reporter), reporter);

        return expressions == null ? null : JsonPrinter.print(expressions);
    }

    @Test
    public void oneString() {
        assertEquals("\"qwerty\"", getJsonText("6:qwerty"));
    }

    @Test
    public void oneNumber() {
        assertEquals("456", getJsonText("i456e"));
    }

    @Test
    public void emptyDictionary() {
        String output = """
                {
                                
                }""";
        assertEquals(output, getJsonText("de"));
    }

    @Test
    public void numberInDictionary() {
        String output = """
                {
                 "adas": 6
                }""";
        assertEquals(output, getJsonText("d 4:adas i6e e"));
    }

    @Test
    public void stringInDictionary() {
        String output = """
                {
                 "adas": "qwe"
                }""";
        assertEquals(output, getJsonText("d 4:adas 3:qwe e"));
    }

    @Test
    public void dictionaryInDictionary() {
        String output = """
                {
                 "qwe":\s
                 {
                  "adas": 6
                 }
                }""";
        assertEquals(output, getJsonText("d 3:qwe d 4:adas i6e e e"));
    }

    @Test
    public void listInDictionary() {
        String output = """
                {
                 "qwe": [456]
                }""";
        assertEquals(output, getJsonText("d 3:qwe l i456e e e"));
    }

    @Test
    public void emptyList() {
        assertEquals("[]", getJsonText("le"));
    }

    @Test
    public void numberInList() {
        assertEquals("[543]", getJsonText("l i543e e"));
    }

    @Test
    public void stringInList() {
        assertEquals("[\"qwe\"]", getJsonText("l 3:qwe e"));
    }

    @Test
    public void listInList() {
        assertEquals("[[543]]", getJsonText("l l i543e e e"));
    }

    @Test
    public void dictionaryInList() {
        String output = """
                [{
                 "rty": 456
                }]""";
        assertEquals(output, getJsonText("l d 3:rty i456e e e"));
    }

    @Test
    public void complexData() {
        String input = """
                d
                4:adas i6e
                3:bsd i5e
                3:fgh l l i435e 5:qwert
                    d   2:gt i12e
                        4:vfrd 2:rt
                    e
                    i78e
                    e
                    e
                6:kjftgy d
                         3:kds i345e
                         e
                3:vfd l 4:dfgg 3:bgf i45e e
                e
                """;

        String output = """
                {
                 "adas": 6,
                 "bsd": 5,
                 "fgh": [[435, "qwert",\s
                 {
                  "gt": 12,
                  "vfrd": "rt"
                 }, 78]],
                 "kjftgy":\s
                 {
                  "kds": 345
                 },
                 "vfd": ["dfgg", "bgf", 45]
                }""";

        assertEquals(output, getJsonText(input));
    }
}
