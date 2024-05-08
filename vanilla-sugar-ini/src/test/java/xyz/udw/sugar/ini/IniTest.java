package xyz.udw.sugar.ini;

import org.junit.jupiter.api.Test;
import xyz.udw.sugar.ini.exception.AccessValueException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static xyz.udw.sugar.ini.Utils.asList;

class IniTest {

    @Test
    void iniSetAndGet() {
        Ini ini = new Ini();
        assertEquals(0, ini.count());

        assertNull(ini.get("sec"));
        Section sec = ini.getOrAdd("sec");
        assertSame(sec, ini.get("sec"));
        assertEquals(1, ini.count());
        Section secVoid = ini.getOrAdd("void");
        assertEquals(2, ini.count());
        assertEquals(asList("sec", "void"), ini.getSectionNames());

        assertTrue(ini.rename("void", "void2"));
        assertEquals(2, ini.count());
        assertSame(secVoid, ini.get("void2"));
        assertEquals(asList("sec", "void2"), ini.getSectionNames());

        assertTrue(ini.remove("void2"));
        assertEquals(1, ini.count());

        ini.clear();
        assertEquals(0, ini.count());
        assertEquals(asList(), ini.getSectionNames());
        assertNotSame(sec, ini.getOrAdd("sec"));
        assertEquals(sec, ini.getOrAdd("sec"));
    }

    @Test
    void valueSetAndGet() {
        Section sec = new Ini().getOrAdd("test");
        assertEquals(0, sec.count());
        assertNull(sec.get("key"));
        assertNull(sec.get("non-exist"));
        assertFalse(sec.contains("key"));
        // first set
        sec.set("key", "value");
        assertEquals("value", sec.get("key"));
        assertNull(sec.get("non-exist"));
        assertTrue(sec.contains("key"));
        assertEquals(1, sec.count());
        // reset
        sec.set("key", "value1");
        assertEquals("value1", sec.get("key"));
        assertTrue(sec.contains("key"));
        assertEquals(1, sec.count());
        // rename
        assertTrue(sec.rename("key", "key1"));
        assertFalse(sec.rename("key", "new-name"));
        assertEquals("value1", sec.get("key1"));
        assertNull(sec.get("key"));
        // second set
        sec.set("key2", "value2");
        assertEquals("value2", sec.get("key2"));
        assertEquals(2, sec.count());
        assertEquals(asList("key1", "key2"), sec.getKeys());
        // remove
        assertTrue(sec.remove("key1"));
        assertFalse(sec.remove("key1"));
        assertEquals(1, sec.count());
        // dangling text
        sec.setDanglingText("Dangling text");
        assertEquals("Dangling text", sec.getDanglingText());
        sec.clear();
        assertEquals(0, sec.count());
        assertNull(sec.get("key1"));
        assertNull(sec.getDanglingText());
    }

    @Test
    void comments() {
        Section sec = new Ini().getOrAdd("test");
        assertEquals(0, sec.count());
        assertEquals(0, sec.countKeyAndComments());
        sec.set("key1", "value1");
        sec.set("key2", "value2");
        assertEquals(2, sec.countKeyAndComments());

        sec.addCommentsBefore("key1", asList("aa", "bb"));
        sec.addCommentsAfter("key1", asList("cc", "dd"));
        sec.addComments("ee", "ff");
        assertEquals(2, sec.count());
        assertEquals(8, sec.countKeyAndComments());
        assertEquals(asList("aa", "bb"), sec.getCommentsBefore("key1"));
        assertEquals(asList("cc", "dd"), sec.getCommentsAfter("key1"));
        assertEquals(asList("cc", "dd"), sec.getCommentsBefore("key2"));
        assertEquals(asList("ee", "ff"), sec.getCommentsAfter("key2"));
        assertEquals(asList("aa", "bb", "cc", "dd", "ee", "ff"), sec.getComments());
        sec.removeCommentsAfter("key1");
        assertEquals(asList("aa", "bb", "ee", "ff"), sec.getComments());
        assertTrue(sec.remove("key2"));
        assertEquals(asList("aa", "bb", "ee", "ff"), sec.getComments());
        assertEquals(asList("ee", "ff"), sec.getCommentsAfter("key1"));
        sec.removeCommentsAfter("key1");
        assertEquals(asList("aa", "bb"), sec.getComments());
        assertEquals(asList(), sec.getCommentsAfter("key1"));
        assertEquals(1, sec.count());
        assertEquals(3, sec.countKeyAndComments());
        sec.removeComments();
        assertEquals(asList(), sec.getComments());
        assertEquals(1, sec.count());
        assertEquals(1, sec.countKeyAndComments());
    }

    @Test
    void commentExceptions() {
        Section sec = new Ini().getOrAdd("test");
        sec.set("key1", "value1");
        assertEquals(1, sec.count());
        assertEquals(1, sec.countKeyAndComments());
        final String nonExistKey = "non-exist";
        assertThrows(AccessValueException.class, () -> sec.getCommentsBefore(nonExistKey));
        assertThrows(AccessValueException.class, () -> sec.getCommentsAfter(nonExistKey));
        assertThrows(AccessValueException.class, () -> sec.removeCommentsBefore(nonExistKey));
        assertThrows(AccessValueException.class, () -> sec.removeCommentsAfter(nonExistKey));
        assertThrows(AccessValueException.class, () -> sec.addCommentsBefore(nonExistKey, asList("comment")));
        assertThrows(AccessValueException.class, () -> sec.addCommentsAfter(nonExistKey, asList("comment")));
        assertEquals(1, sec.count());
        assertEquals(1, sec.countKeyAndComments());
    }

    @Test
    void getItemValue() {
        Ini ini = getExampleIni();
        assertEquals("value1", ini.getItemValue("sec1", "key1"));
        assertEquals("value3", ini.getItemValue("sec2", "key3"));
        assertNull(ini.getItemValue("sec1", "non-exist"));
        assertNull(ini.getItemValue("sec2", "non-exist"));
        assertNull(ini.getItemValue("non-exist-sec", "key1"));
        assertNull(ini.getItemValue("non-exist-sec", "key3"));
    }

    @Test
    void untitledSection() {
        Ini ini = getExampleIni();
        Section untitled = ini.getUntitledSection();
        assertNotNull(untitled);
        assertEquals(0, untitled.count());
        assertEquals(1, untitled.countKeyAndComments());
        // key value
        untitled.set("key1", "value1");
        untitled.set("key2", "value2");
        assertEquals(2, untitled.count());
        assertEquals(3, untitled.countKeyAndComments());
        assertEquals(asList("key1", "key2"), untitled.getKeys());
        // comment
        untitled.addCommentsAfter("key1", asList("aa", "bb"));
        assertEquals(asList("aa", "bb"), untitled.getCommentsBefore("key2"));
        assertEquals(asList("This is a comment", "aa", "bb"), untitled.getComments());
        // clear
        untitled.clear();
        assertNull(untitled.getDanglingText());
        assertEquals(asList(), untitled.getKeys());
        assertEquals(asList(), untitled.getComments());
    }

    @Test
    void deepClone() {
        Ini origin = getExampleIni();
        Ini clone = origin.deepClone();
        assertNotSame(origin, clone);
        assertEquals(origin, clone);
        assertNotNull(clone.get("sec1"));
        assertNotNull(clone.get("sec2"));
        assertNotSame(origin.get("sec1"), clone.get("sec1"));
        assertNotSame(origin.get("sec2"), clone.get("sec2"));
        assertEquals("The dangling text", clone.get("sec2").getDanglingText());
    }

    @Test
    void toCollection() {
        Ini ini = getExampleIni();
        Section untitled = ini.getUntitledSection();
        Section sec1 = ini.get("sec1");
        Section sec2 = ini.get("sec2");
        // Map
        Map<String, String> example1 = new HashMap<>();
        example1.put("key1", "value1");
        example1.put("key2", "value2");
        assertEquals(example1, sec1.toMap());
        Map<String, String> example2 = new HashMap<>();
        example2.put("key3", "value3");
        assertEquals(example2, sec2.toMap());
        // Comment
        assertEquals(asList("This is a comment"), untitled.getComments());
        assertEquals(asList(), sec1.getComments());
        assertEquals(asList("comment before key3", "comment after key3"), sec2.getComments());
    }

    private static Ini getExampleIni() {
        Ini origin = new Ini();
        origin.getUntitledSection().addComments("This is a comment");
        Section orgSec1 = origin.getOrAdd("sec1");
        orgSec1.set("key1", "value1");
        orgSec1.set("key2", "value2");
        Section orgSec2 = origin.getOrAdd("sec2");
        orgSec2.set("key3", "value3");
        orgSec2.addCommentsBefore("key3", asList("comment before key3"));
        orgSec2.addCommentsAfter("key3", asList("comment after key3"));
        orgSec2.setDanglingText("The dangling text");
        return origin;
    }
}