package xyz.udw.sugar.ini;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static xyz.udw.sugar.ini.Utils.asList;

class ChainIniAccessorTest {

    @Test
    void addAndSet() {
        Ini example = new Ini();
        Section untitledSection = example.getUntitledSection();
        untitledSection.addComments("Hello", "World");
        untitledSection.set("key", "value");
        untitledSection.addComments("Ini");
        Section section = example.getOrAdd("sec");
        section.set("a", "1");
        section.set("b", "2");
        section.set("c", "3");
        section.addCommentsBefore("b", asList("Before b"));
        example.getOrAdd("void");
        Ini ini = new Ini();
        ini.chainAccess()
                .openUntitledSection()
                .addComment("Hello").addComment("World").set("key", "value").addComment("Ini")
                .closeSection()
                .openSection("sec")
                .set("a", "1").addComment("Before b").set("b", "2").set("c", "3")
                .closeSection()
                .openSection("void").closeSection();
        assertEquals(example, ini);
    }

    @Test
    void section() {
        Ini original = new Ini();
        Section section = original.getOrAdd("sec");
        section.set("key", "1");
        section.set("key2", "2");
        assertEquals("1", section.get("key"));
        assertEquals("2", section.get("key2"));
        ChainSectionAccessor sectionAccessor = original.chainAccess().openSection("sec");
        sectionAccessor.rename("key", "new-key");
        assertEquals("1", section.get("new-key"));
        assertFalse(section.contains("key"));
        sectionAccessor.remove("key2");
        assertFalse(section.contains("key2"));
    }

    @Test
    void ini() {
        Ini original = new Ini();
        Section section = original.getOrAdd("sec");
        section.set("key", "1");
        original.getOrAdd("sec2");
        assertTrue(original.contains("sec"));
        assertEquals("1", original.getItemValue("sec","key"));
        assertTrue(original.contains("sec2"));
        ChainIniAccessor iniAccessor = original.chainAccess().renameSection("sec", "new-sec");
        assertTrue(original.contains("new-sec"));
        assertFalse(original.contains("sec"));
        assertTrue(original.contains("sec2"));
        assertEquals("1", original.getItemValue("new-sec","key"));
        iniAccessor.removeSection("sec2");
        assertTrue(original.contains("new-sec"));
        assertFalse(original.contains("sec2"));
    }
}