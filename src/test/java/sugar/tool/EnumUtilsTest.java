package sugar.tool;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class EnumUtilsTest {

    @Test
    void isEnum() {
        assertTrue(EnumUtils.isEnum(TestEnum.SECOND));
        assertFalse(EnumUtils.isEnum(null));
    }

    @Test
    void getEnum() {
        assertEquals(TestEnum.FIRST, EnumUtils.getEnum(TestEnum.values(), "I", TestEnum::getS));
        assertEquals(TestEnum.SECOND, EnumUtils.getEnum(TestEnum.values(), 2, TestEnum::getNum));
        assertEquals(TestEnum.THIRD, EnumUtils.getEnum(TestEnum.values(), "III", TestEnum::getS));
        // Default value test
        assertNull(EnumUtils.getEnum(new TestEnum[0], "III", TestEnum::getS));
        assertNull(EnumUtils.getEnum(TestEnum.values(), "V", TestEnum::getS));
        assertNull(EnumUtils.getEnum(TestEnum.values(), "V", TestEnum::getS, null));
        assertEquals(TestEnum.SECOND, EnumUtils.getEnum(TestEnum.values(), "V", TestEnum::getS, TestEnum.SECOND));
        assertEquals(TestEnum.SECOND, EnumUtils.getEnum(null, "III", TestEnum::getS, TestEnum.SECOND));
        // Class type test
        assertEquals(TestEnum.THIRD, EnumUtils.getEnum(TestEnum.class, "III", TestEnum::getS));
        assertNull(EnumUtils.getEnum(TestEnum.class, "V", TestEnum::getS));
        assertNull(EnumUtils.getEnum(TestEnum.class, null, TestEnum::getS));
        try {
            EnumUtils.getEnum((Class<TestEnum>) null, "V", TestEnum::getS);
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    void fromOrdinal() {
        assertEquals(TestEnum.FIRST, EnumUtils.fromOrdinal(TestEnum.class, 0));
        assertEquals(TestEnum.SECOND, EnumUtils.fromOrdinal(TestEnum.class, 1));
        assertEquals(TestEnum.THIRD, EnumUtils.fromOrdinal(TestEnum.class, 2, null));
        assertNull(EnumUtils.fromOrdinal(TestEnum.class, -1, null));
        assertNull(EnumUtils.fromOrdinal(TestEnum.class, 4, null));
        try {
            EnumUtils.fromOrdinal(null, 0, null);
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    void fromOrdinalImplicit() {
        assertEquals(TestEnum.FIRST, EnumUtils.fromOrdinalImplicit(TestEnum.class, 0, null));
        assertEquals(TestEnum.SECOND, EnumUtils.fromOrdinalImplicit(TestEnum.class, 1, null));
        assertNull(EnumUtils.fromOrdinalImplicit(Object.class, 0, null));
        assertEquals(TestEnum.FIRST, EnumUtils.fromOrdinalImplicit(Object.class, 0, TestEnum.FIRST));
        try {
            EnumUtils.fromOrdinalImplicit(null, 0, null);
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    void fromNameIgnoreCase() {
        assertEquals(TestEnum.FIRST, EnumUtils.fromNameIgnoreCase(TestEnum.class, "first", null));
        assertEquals(TestEnum.THIRD, EnumUtils.fromNameIgnoreCase(TestEnum.class, "third", null));
        assertEquals(TestEnum.SECOND, EnumUtils.fromNameIgnoreCase(TestEnum.class, "", TestEnum.SECOND));
        assertNull(EnumUtils.fromNameIgnoreCase(TestEnum.class, "no", null));
        try {
            EnumUtils.fromNameIgnoreCase(null, "", null);
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    void toMap() {
        Map<Integer, String> map1 = EnumUtils.toMap(TestEnum.class, TestEnum::ordinal, TestEnum::name);
        assertEquals(3, map1.size());
        assertEquals("FIRST", map1.get(0));
        assertNull(map1.get(4));
        Map<Integer, String> map2 = EnumUtils.toMap(TestEnum.class, TestEnum::getNum, TestEnum::getS);
        assertEquals(3, map2.size());
        assertEquals("III", map2.get(3));
        assertNull(map2.get(0));
    }

    @Test
    void toList() {
        List<Integer> list = EnumUtils.toList(TestEnum.class, TestEnum::getNum);
        assertEquals(3, list.size());
        assertEquals(Arrays.asList(1, 2, 3), list);
        run(() -> EnumUtils.toList(TestEnum.class, null));
        run(() -> EnumUtils.toList(null, TestEnum::getNum));
    }

    @Test
    void getNames() {
        List<String> list = EnumUtils.getNames(TestEnum.class);
        assertEquals(3, list.size());
        assertEquals(Arrays.asList("FIRST", "SECOND", "THIRD"), list);
        run(() -> EnumUtils.getNames(null));
    }

    public enum TestEnum {
        FIRST(1, "I"), SECOND(2, "II"), THIRD(3, "III");

        private final int num;
        private final String s;

        TestEnum(int num, String s) {
            this.num = num;
            this.s = s;
        }

        public int getNum() {
            return num;
        }

        public String getS() {
            return s;
        }
    }

    static void run(Supplier<?> supplier) {
        try {
            supplier.get();
            fail("Should not run");
        } catch (IllegalArgumentException ignored) {
        }
    }
}