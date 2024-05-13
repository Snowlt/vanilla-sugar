package sugar.ini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class IniDeserializerTest {

    @Test
    void readNormal() {
        Ini ini = new IniDeserializer().read(Utils.getInputStream("normal.ini"), StandardCharsets.UTF_8);
        assertNotNull(ini);
        assertTrue(ini.contains("Sec1"));
        assertTrue(ini.contains("Sec2"));
        assertEquals(0, ini.getUntitledSection().countKeyAndComments());
        Assertions.assertEquals(Utils.asList("Sec1", "Sec2"), ini.getSectionNames());
        Section section1 = ini.get("Sec1");
        assertNotNull(section1);
        assertEquals("value1", section1.get("key1"));
        assertEquals("value2", section1.get("key2"));
        assertEquals("value3", section1.get("key3"));
        Section section2 = ini.get("Sec2");
        assertNotNull(section2);
        assertEquals("value4", section2.get("key4"));
        assertEquals("value5", section2.get("key5"));
    }

    @Test
    void readAbnormal() {
        Ini ini = new IniDeserializer().read(Utils.getInputStream("abnormal.ini"), StandardCharsets.UTF_8);
        assertNotNull(ini);
        Assertions.assertEquals(Utils.asList("Sec1", "Sec2", "Sec3"), ini.getSectionNames());
        // Untitled Section
        Section untitledSection = ini.getUntitledSection();
        assertNotNull(untitledSection);
        assertEquals(2, untitledSection.count());
        assertEquals(3, untitledSection.countKeyAndComments());
        assertNull(untitledSection.getDanglingText());
        assertEquals("untitled-value1", untitledSection.get("untitled-key1"));
        assertEquals("untitled value 2", untitledSection.get("untitled key 2"));
        // Section 1
        Section section1 = ini.get("Sec1");
        assertNotNull(section1);
        assertEquals(3, section1.count());
        assertEquals(6, section1.countKeyAndComments());
        assertEquals("  Dangling Content In Sec1\n" +
                "\n" +
                "  Next dangling line", section1.getDanglingText());
        Assertions.assertEquals(Utils.asList("Comment1 before key1", "Comment2 before key1"), section1.getCommentsBefore("key1"));
        assertEquals("value1", section1.get("key1"));
        assertEquals("value2\n    value2 next line", section1.get("key2"));
        Assertions.assertEquals(Utils.asList("Comment before key3"), section1.getCommentsBefore("key3"));
        assertEquals("value3", section1.get("key3"));
        // Section 2(Empty)
        Section section2 = ini.get("Sec2");
        assertNotNull(section2);
        assertEquals(0, section2.countKeyAndComments());
        assertNull(section2.getDanglingText());
        // Section 3
        Section section3 = ini.get("Sec3");
        assertNotNull(section3);
        assertEquals(2, section3.count());
        assertEquals("value4", section3.get("key4"));
        assertEquals("value5", section3.get("key5"));
    }

    @Test
    void readTopDangling() {
        final String danglingText = "  Dangling Content In Sec1\n\n  Next dangling line";
        final String filename = "top-dangling.ini";

        Ini ini1 = new IniDeserializer().setDanglingTextOption(IniDeserializer.DanglingTextOptions.TO_COMMENT)
                .read(Utils.getInputStream(filename), StandardCharsets.UTF_8);
        assertEquals(0, ini1.getUntitledSection().countKeyAndComments());
        Section sectionA = ini1.get("Sec1");
        assertNull(sectionA.getDanglingText());
        Assertions.assertEquals(Utils.asList(danglingText, "Comment1 before key1"), sectionA.getCommentsBefore("key1"));
        Assertions.assertEquals(Utils.asList(danglingText, "Comment1 before key1"), sectionA.getComments());

        Ini ini2 = new IniDeserializer().setDanglingTextOption(IniDeserializer.DanglingTextOptions.DROP)
                .read(Utils.getInputStream(filename), StandardCharsets.UTF_8);
        assertEquals(0, ini2.getUntitledSection().countKeyAndComments());
        Section sectionB = ini2.get("Sec1");
        assertNull(sectionB.getDanglingText());
        Assertions.assertEquals(Utils.asList("Comment1 before key1"), sectionB.getCommentsBefore("key1"));
        Assertions.assertEquals(Utils.asList("Comment1 before key1"), sectionB.getComments());

        Ini ini3 = new IniDeserializer().setDanglingTextOption(IniDeserializer.DanglingTextOptions.KEEP)
                .read(Utils.getInputStream(filename), StandardCharsets.UTF_8);
        assertEquals(0, ini3.getUntitledSection().countKeyAndComments());
        Section sectionC = ini3.get("Sec1");
        assertEquals(danglingText, sectionC.getDanglingText());
        Assertions.assertEquals(Utils.asList("Comment1 before key1"), sectionC.getCommentsBefore("key1"));
        Assertions.assertEquals(Utils.asList("Comment1 before key1"), sectionC.getComments());
    }
    
}