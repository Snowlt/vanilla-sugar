package sugar.core.simplification;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ConvertTest {

    @Test
    void enumConvert() {
        assertEquals(0, Convert.enumToInt(TestEnum.ZERO));
        assertEquals(1, Convert.enumToInt(TestEnum.ONE));
        assertNotEquals(2, Convert.enumToInt(TestEnum.ONE));
        assertNull(Convert.enumToInt(null));
    }

    @Test
    void toNumber() {
        assertEquals((byte) 0, Convert.toByte(null));
        assertEquals((short) 0, Convert.toShort(null));
        assertEquals(0, Convert.toInt(null));
        assertEquals(0L, Convert.toLong(null));
        assertEquals(0F, Convert.toFloat(null));
        assertEquals(0.0, Convert.toDouble(null));
        Object nothing = new Object();
        assertEquals((byte) 1, Convert.toByte((byte) 1));
        assertEquals((short) 1, Convert.toShort((short) 1));
        assertEquals(1, Convert.toInt(nothing, 1));
        assertEquals(1, Convert.toLong(nothing, 1L));
        assertEquals(1, Convert.toFloat(nothing, 1F));
        assertEquals(1, Convert.toDouble(nothing, 1D));
        assertNull(Convert.toByte(nothing, null));
        assertNull(Convert.toShort(nothing, null));
        assertNull(Convert.toInt(nothing, null));
        assertNull(Convert.toLong(nothing, null));
        assertNull(Convert.toFloat(nothing, null));
        assertNull(Convert.toDouble(nothing, null));
        String example = "123.321";
        assertEquals((byte) 123, Convert.toByte(example, (byte) -1));
        assertEquals((short) 123, Convert.toShort(example, (short) -1));
        assertEquals(123, Convert.toInt(example, -1));
        assertEquals(123L, Convert.toLong(example, -1L));
        assertEquals(123.321F, Convert.toFloat(example, -1F));
        assertEquals(123.321D, Convert.toDouble(example, -1D));
        assertEquals((byte) 123, Convert.toByte(example, null));
        assertEquals((short) 123, Convert.toShort(example, null));
        assertEquals(123, Convert.toInt(example, null));
        assertEquals(123L, Convert.toLong(example, null));
        assertEquals(123.321F, Convert.toFloat(example, null));
        assertEquals(123.321D, Convert.toDouble(example, null));
    }

    @Test
    void toBool() {
        // Return default
        assertNull(Convert.toBoolean(null, null));
        assertTrue(Convert.toBoolean(null, true));
        assertFalse(Convert.toBoolean(null, false));
        // Parse string(not return default)
        final String sTrue = "tRuE";
        assertTrue(Convert.toBoolean(sTrue, null));
        assertFalse(Convert.toBoolean(sTrue + "0", null));
        assertFalse(Convert.toBoolean(new StringBuilder("false"), null));
        assertFalse(Convert.toBoolean(new StringBuffer("ok"), null));
        // Number
        assertTrue(Convert.toBoolean(1, null));
        assertTrue(Convert.toBoolean(-1, null));
        assertTrue(Convert.toBoolean(new BigDecimal("0.0000001"), null));
        assertFalse(Convert.toBoolean(0, null));
        assertFalse(Convert.toBoolean(0.0, null));
        // Boolean
        assertTrue(Convert.toBoolean(Boolean.TRUE, null));
        assertFalse(Convert.toBoolean(Boolean.FALSE, null));
        // Others
        assertNull(Convert.toBoolean('\0', null));
        assertNull(Convert.toBoolean('A', null));
        assertNull(Convert.toBoolean(new Object(), null));
    }

    @Test
    void toChar() {
        assertEquals('A', Convert.toChar(null, 'A'));
        assertEquals('A', Convert.toChar(65, null));
        assertEquals('A', Convert.toChar(65.0, null));
        assertEquals('a', Convert.toChar("a", null));
        assertEquals('a', Convert.toChar("a", 'b'));
        assertNull(Convert.toChar("aaa", null));
    }

    @Test
    void toArray() {
        assertArrayEquals(new Byte[]{1, 2, 3}, Convert.toArray(new byte[]{1, 2, 3}));
        assertArrayEquals(new Short[]{1, 2, 3}, Convert.toArray(new short[]{1, 2, 3}));
        assertArrayEquals(new Integer[]{1, 2, 3}, Convert.toArray(new int[]{1, 2, 3}));
        assertArrayEquals(new Long[]{1L, 2L, 3L}, Convert.toArray(new long[]{1, 2, 3}));
        assertArrayEquals(new Float[]{1.1F, 2.2F, 3.3F}, Convert.toArray(new float[]{1.1F, 2.2F, 3.3F}));
        assertArrayEquals(new Double[]{1.1D, 2.2D, 3.3D}, Convert.toArray(new double[]{1.1, 2.2, 3.3}));
        assertArrayEquals(new Character[]{'A', 'B', 'C'}, Convert.toArray(new char[]{'A', 'B', 'C'}));
        assertArrayEquals(new Boolean[]{true, true, false, false}, Convert.toArray(new boolean[]{true, true, false, false}));
    }

    public enum TestEnum {
        ZERO, ONE
    }

}