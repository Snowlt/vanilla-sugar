package sugar.tool;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class RandUtilsTest {

    @Test
    void shuffleList() {
        List<Integer> example = getSeq(1, 10);
        assertThrows(UnsupportedOperationException.class, () -> RandUtils.shuffleSelf(example));
        int count = 0;
        for (int i = 0; i < 10; i++) {
            List<Integer> shuffle = RandUtils.shuffle(example);
            assertEquals(10, shuffle.size());
            shuffle.forEach(e -> assertTrue(example.contains(e)));
            if (example.equals(shuffle)) {
                count++;
            }
        }
        assertTrue(count <= 2);
    }

    @Test
    void shuffleArray() {
        Integer[] example = getSeq(1, 10).toArray(new Integer[0]);
        Integer[] backup = Arrays.copyOf(example, example.length);
        Set<Integer> elements = Arrays.stream(example).collect(Collectors.toSet());
        int count = 0;
        for (int i = 0; i < 10; i++) {
            Integer[] shuffle = RandUtils.shuffle(example);
            assertEquals(backup.length, shuffle.length);
            assertArrayEquals(backup, example);
            assertNotSame(example, shuffle);
            assertTrue(elements.containsAll(Arrays.asList(shuffle)));
            if (Arrays.equals(example, shuffle)) {
                count++;
            }
        }
        assertTrue(count <= 2);
        RandUtils.shuffleSelf(example);
        assertTrue(elements.containsAll(Arrays.asList(example)));
        assertFalse(Arrays.equals(backup, example));
    }

    @Test
    void sample() {
        List<Integer> list = getSeq(1, 10);
        int size = 5;
        final int loop = 10;
        Set<List<Integer>> results = new HashSet<>(loop);
        for (int i = 0; i < loop; i++) {
            List<Integer> sample = RandUtils.sample(list, size);
            assertEquals(size, sample.size());
            sample.forEach(e -> assertTrue(list.contains(e)));
            results.add(new ArrayList<>(sample));
        }
        assertTrue(results.size() >= loop - 2);
    }

    @Test
    void randIntInclusive() {
        int count = 0;
        int min = Integer.MAX_VALUE - 1;
        for (int i = 100; i > 0; i--) {
            if (RandUtils.randIntBetween(min, Integer.MAX_VALUE) == Integer.MAX_VALUE) {
                count++;
            }
        }
        assertNotEquals(0, count);
        assertThrows(IllegalArgumentException.class, () -> RandUtils.randIntBetween(2, 1));
    }

    @Test
    void randBytes() {
        assertThrows(IllegalArgumentException.class, () -> RandUtils.randBytes(-1));
        assertArrayEquals(new byte[0], RandUtils.randBytes(0));
        assertEquals(3, RandUtils.randBytes(3).length);
    }

    static List<Integer> getSeq(int start, int stop) {
        return Collections.unmodifiableList(IntStream.range(start, stop + 1).boxed().collect(Collectors.toList()));
    }
}