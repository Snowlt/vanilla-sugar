package xyz.udw.sugar.ini;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static xyz.udw.sugar.ini.Utils.loadAsString;

class IniSerializerTest {

    @Test
    void saveNormal() throws IOException {
        Ini ini = new Ini();
        Section sec1 = ini.getOrAdd("Sec1");
        sec1.addComments("Comment before key1", "and Value1");
        sec1.set("key1", "value1");
        sec1.addComments("Comment before key2", "and Value2");
        sec1.set("key2", "value2");
        sec1.set("key3", "value3");
        Section sec2 = ini.getOrAdd("Sec2");
        sec2.set("key4", "value4");
        sec2.set("key5", "value5");
        IniSerializer serializer = new IniSerializer();
        try (StringWriter writer = new StringWriter()) {
            serializer.setLineSeparator("\n")
                    .setAddSpaceAroundEqualizer(true)
                    .setAddSpaceBeforeComment(true);
            serializer.write(ini, writer);
            String result = trimTailNewLine(writer);
            String expected = loadAsString("normal.ini");
            assertEquals(expected, result);
        }
        try (StringWriter writer = new StringWriter()) {
            serializer.setLineSeparator("\n")
                    .setAddSpaceAroundEqualizer(false)
                    .setAddSpaceBeforeComment(false)
                    .setCommentPrefix("#");
            serializer.write(ini, writer);
            String result = trimTailNewLine(writer);
            String expected = loadAsString("normal.ini")
                    .replace("; ", "#")
                    .replace(" = ", "=");
            assertEquals(expected, result);
        }
    }

    @Test
    void saveTopDangling() throws IOException {
        Ini ini = new Ini();
        Section sec1 = ini.getOrAdd("Sec1");
        sec1.setDanglingText("  Dangling Content In Sec1\n\n  Next dangling line");
        sec1.addComments("Comment1 before key1");
        sec1.set("key1", "value1");
        sec1.set("key2", "value2");
        ini.getOrAdd("Sec2");
        IniSerializer serializer = new IniSerializer();
        try (StringWriter writer = new StringWriter()) {
            serializer.setLineSeparator("\n")
                    .setAddSpaceAroundEqualizer(true)
                    .setAddSpaceBeforeComment(true);
            serializer.write(ini, writer);
            String result = trimTailNewLine(writer);
            String expected = loadAsString("top-dangling.ini");
            assertEquals(expected, result);
        }
    }

    private String trimTailNewLine(StringWriter writer) {
        StringBuffer buffer = writer.getBuffer();
        if (buffer.charAt(buffer.length() - 1) == '\r') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        if (buffer.charAt(buffer.length() - 1) == '\n') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }
}