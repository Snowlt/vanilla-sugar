package sugar.core.simplification;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 辅助转换的工具
 * <p>可根据入参的类型和值，自动转换为其他类型</p>
 * 设计参考自 C/C++ 中的基本类型转换 和 .Net 中 System.Convert 的相关方法
 *
 * @author SnowLT
 * @version 1.5
 * @since 2020/9/5
 */
public class Convert {

    /**
     * 转换到整型，转换失败时返回 0
     * <p>等效于 {@code toInt(value, 0)}</p>
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Integer toInt(Object value) {
        return toInt(value, 0);
    }

    /**
     * 转换到整型
     * <p>
     * 根据 value 类型不同，返回结果有以下情况：
     *  <ol>
     *  <li>null: 返回 <i>默认值</i></li>
     *  <li>Number: 返回强制转换为 int 后的值</li>
     *  <li>Boolean: true 返回 1，false 返回 0</li>
     *  <li>Character: 返回字符的 ASCII 码（同char 显式转换为 int 后的值）</li>
     *  <li>其他类型: 先通过 {@code toString()} 转为字符串，再尝试解析为数字，解析失败则返回 <i>默认值</i></li>
     *  </ol>
     * </p>
     *
     * @param value        值
     * @param defaultValue 转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Integer toInt(Object value, Integer defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof Boolean) return (boolean) value ? 1 : 0;
        if (value instanceof Character) return (int) (Character) value;
        try {
            return (int) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转换到长整型，转换失败时返回 0
     * <p>等效于 {@code toLong(value, 0L)}</p>
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Long toLong(Object value) {
        return toLong(value, 0L);
    }

    /**
     * 转换到长整型
     * <p>
     * 根据 value 类型不同，返回结果有以下情况：
     *  <ol>
     *  <li>null: 返回 <i>默认值</i></li>
     *  <li>Number: 返回强制转换为 long 后的值</li>
     *  <li>Boolean: true 返回 1，false 返回 0</li>
     *  <li>Character: 返回字符的 ASCII 码（同char 显式转换为 int 后的值）</li>
     *  <li>其他类型: 先通过 {@code toString()} 转为字符串，再尝试解析为数字，解析失败则返回 <i>默认值</i></li>
     *  </ol>
     * </p>
     *
     * @param value        值
     * @param defaultValue 转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Long toLong(Object value, Long defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof Boolean) return (boolean) value ? 1L : 0;
        if (value instanceof Character) return (long) (Character) value;
        try {
            return (long) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转换到 Byte 型，转换失败时返回 0
     * <p>等效于 {@code toByte(value, (byte) 0)}</p>
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Byte toByte(Object value) {
        return toByte(value, (byte) 0);
    }

    /**
     * 转换到 Byte 型
     * <p>
     * 根据 value 类型不同，返回结果有以下情况：
     *  <ol>
     *  <li>null: 返回 <i>默认值</i></li>
     *  <li>Number: 返回强制转换为 byte 后的值</li>
     *  <li>Boolean: true 返回 1，false 返回 0</li>
     *  <li>Character: 返回字符的 ASCII 码（同char 显式转换为 int 后的值）</li>
     *  <li>其他类型: 先通过 {@code toString()} 转为字符串，再尝试解析为数字，解析失败则返回 <i>默认值</i></li>
     *  </ol>
     * </p>
     *
     * @param value        值
     * @param defaultValue 转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Byte toByte(Object value, Byte defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Byte) return (Byte) value;
        if (value instanceof Number) return ((Number) value).byteValue();
        if (value instanceof Boolean) return (byte) ((boolean) value ? 1 : 0);
        if (value instanceof Character) return (byte) (char) (Character) value;
        try {
            return (byte) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转换到 Short 型，转换失败时返回 0
     * <p>等效于 {@code toShort(value, (short) 0)}</p>
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Short toShort(Object value) {
        return toShort(value, (short) 0);
    }

    /**
     * 转换到 Short 型
     * <p>
     * 根据 value 类型不同，返回结果有以下情况：
     *  <ol>
     *  <li>null: 返回 <i>默认值</i></li>
     *  <li>Number: 返回强制转换为 short 后的值</li>
     *  <li>Boolean: true 返回 1，false 返回 0</li>
     *  <li>Character: 返回字符的 ASCII 码（同char 显式转换为 int 后的值）</li>
     *  <li>其他类型: 先通过 {@code toString()} 转为字符串，再尝试解析为数字，解析失败则返回 <i>默认值</i></li>
     *  </ol>
     * </p>
     *
     * @param value        值
     * @param defaultValue 转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Short toShort(Object value, Short defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Short) return (Short) value;
        if (value instanceof Number) return ((Number) value).shortValue();
        if (value instanceof Boolean) return (short) ((boolean) value ? 1 : 0);
        if (value instanceof Character) return (short) (char) (Character) value;
        try {
            return (short) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转换到单精度浮点型，转换失败时返回 0
     * <p>等效于 {@code toFloat(value, 0F)}</p>
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Float toFloat(Object value) {
        return toFloat(value, 0F);
    }

    /**
     * 转换到单精度浮点型
     * <p>
     * 根据 value 类型不同，返回结果有以下情况：
     *  <ol>
     *  <li>null: 返回 <i>默认值</i></li>
     *  <li>Number: 返回强制转换为 float 后的值</li>
     *  <li>Boolean: true 返回 1，false 返回 0</li>
     *  <li>Character: 返回字符的 ASCII 码（同char 显式转换为 int 后的值）</li>
     *  <li>其他类型: 先通过 {@code toString()} 转为字符串，再尝试解析为数字，解析失败则返回 <i>默认值</i></li>
     *  </ol>
     * </p>
     *
     * @param value        值
     * @param defaultValue 转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Float toFloat(Object value, Float defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Float) return (Float) value;
        if (value instanceof Number) return ((Number) value).floatValue();
        if (value instanceof Boolean) return (boolean) value ? 1F : 0;
        if (value instanceof Character) return (float) (Character) value;
        try {
            return Float.parseFloat(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转换到双精度浮点型，转换失败时返回 0
     * <p>等效于 {@code toDouble(value, 0.0)}</p>
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Double toDouble(Object value) {
        return toDouble(value, 0.0);
    }

    /**
     * 转换到双精度浮点型
     * <p>
     * 根据 value 类型不同，返回结果有以下情况：
     *  <ol>
     *  <li>null: 返回 <i>默认值</i></li>
     *  <li>Number: 返回强制转换为 double 后的值</li>
     *  <li>Boolean: true 返回 1，false 返回 0</li>
     *  <li>Character: 返回字符的 ASCII 码（同char 显式转换为 int 后的值）</li>
     *  <li>其他类型: 先通过 {@code toString()} 转为字符串，再尝试解析为数字，解析失败则返回 <i>默认值</i></li>
     *  </ol>
     * </p>
     *
     * @param value        值
     * @param defaultValue 转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Double toDouble(Object value, Double defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof Boolean) return (boolean) value ? 1.0 : 0;
        if (value instanceof Character) return (double) (Character) value;
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转换到字符类型
     * <p>
     * 根据 value 类型不同，返回结果有以下情况：
     *  <ol>
     *  <li>null: 返回 <i>默认值</i></li>
     *  <li>Number: 返回强制转换为 char 后的值</li>
     *  <li>CharSequence: 如果字符串长度为 <i>1</i> 返回首个字符，否则返回 <i>默认值</i></li>
     *  <li>其他类型: 返回 <i>默认值</i></li>
     *  </ol>
     * </p>
     * <pre>
     * 例如：
     *     Convert.toChar('a', null) -> 'a'
     *     Convert.toChar(65, null) -> 'A'
     *     Convert.toChar("B", null) -> 'B'
     *     Convert.toChar("CD", null) -> null
     *     Convert.toChar(null, 'A') -> 'A'
     * </pre>
     *
     * @param value        值
     * @param defaultValue value 为 null 或无法转换为字符类型返回的默认值
     * @return 转换后的结果
     */
    public static Character toChar(Object value, Character defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Character) return (Character) value;
        if (value instanceof Number) return (char) ((Number) value).longValue();
        if (value instanceof CharSequence && ((CharSequence) value).length() == 1)
            return ((CharSequence) value).charAt(0);
        return defaultValue;
    }

    /**
     * 转换到布尔类型
     * <p>
     * 根据 value 类型和值不同，以下情况会返回 true：
     * <ol>
     *  <li>Boolean: 返回自身的值</li>
     *  <li>Number: 不为 0 返回 {@code true}，为 0 返回 {@code false}</li>
     *  <li>String 或 CharSequence: 如果字符串内容等于 <i>"true"</i>（忽略大小写）返回 {@code true}，否则返回 {@code false}</li>
     * </ol>
     * 传入 {@code null} 值和其他情况都会返回 false
     * </p>
     *
     * @param value 值
     * @return 转换后的结果
     * @see #toBoolean(Object, Boolean)
     */
    public static boolean toBoolean(Object value) {
        return toBoolean(value, false);
    }

    /**
     * 转换到布尔类型
     * <p>
     * 根据 value 类型不同，返回结果有以下情况：
     *  <ol>
     *  <li>{@code null} 值: 返回 {@code false}</li>
     *  <li>Boolean: 返回自身的值</li>
     *  <li>Number: 不为 0 返回 {@code true}，为 0 返回 {@code false}</li>
     *  <li>CharSequence: 如果字符串内容等于 <i>"true"</i>（忽略大小写）返回 {@code true}，否则返回 {@code false}</li>
     *  </ol>
     *  其他情况视为无法直接转换为布尔类型，返回 <i>默认值</i></li>
     * </p>
     * <pre>
     * 例如：
     *     Convert.toBoolean(1, null) -> true
     *     Convert.toBoolean(0, null) -> false
     *     Convert.toBoolean("TruE", null) -> true
     *     Convert.toBoolean("yes", null) -> false
     *     Convert.toBoolean(null, null) -> false
     *     Convert.toBoolean(new HashMap<>(), null) -> null
     *     Convert.toBoolean(new Object, true) -> true
     * </pre>
     *
     * @param value        值
     * @param defaultValue 无法转换时返回的默认值
     * @return 转换后的结果
     */
    public static Boolean toBoolean(Object value, Boolean defaultValue) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) {
            if (value instanceof BigDecimal) return !BigDecimal.ZERO.equals(value);
            if (value instanceof BigInteger) return !BigInteger.ZERO.equals(value);
            if (value instanceof Float) return !value.equals(0f);
            return ((Number) value).doubleValue() != 0.0;
        }
        if (value instanceof CharSequence) {
            CharSequence str = (CharSequence) value;
            if (str.length() != 4) return false;
            final char ch0 = str.charAt(0);
            final char ch1 = str.charAt(1);
            final char ch2 = str.charAt(2);
            final char ch3 = str.charAt(3);
            return ((ch0 == 't' || ch0 == 'T') && (ch1 == 'r' || ch1 == 'R') &&
                    (ch2 == 'u' || ch2 == 'U') && (ch3 == 'e' || ch3 == 'E'));
        }
        return defaultValue;
    }

    /**
     * 枚举转为整数（序号），使用 {@link Enum#ordinal()}，如果传入值为 null 则返回 null
     *
     * @param enumerate 枚举
     * @return 转换后的结果
     */
    public static Integer enumToInt(Enum<?> enumerate) {
        return enumerate != null ? enumerate.ordinal() : null;
    }

    /**
     * 将基本类型数组转为包装类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 包装类型数组
     */
    public static Byte[] toArray(byte[] array) {
        if (array == null) return null;
        Byte[] na = new Byte[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将基本类型数组转为包装类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 包装类型数组
     */
    public static Short[] toArray(short[] array) {
        if (array == null) return null;
        Short[] na = new Short[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将基本类型数组转为包装类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 包装类型数组
     */
    public static Integer[] toArray(int[] array) {
        if (array == null) return null;
        Integer[] na = new Integer[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将基本类型数组转为包装类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 包装类型数组
     */
    public static Long[] toArray(long[] array) {
        if (array == null) return null;
        Long[] na = new Long[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将基本类型数组转为包装类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 包装类型数组
     */
    public static Float[] toArray(float[] array) {
        if (array == null) return null;
        Float[] na = new Float[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将基本类型数组转为包装类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 包装类型数组
     */
    public static Double[] toArray(double[] array) {
        if (array == null) return null;
        Double[] na = new Double[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将基本类型数组转为包装类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 包装类型数组
     */
    public static Character[] toArray(char[] array) {
        if (array == null) return null;
        Character[] na = new Character[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将基本类型数组转为包装类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 包装类型数组
     */
    public static Boolean[] toArray(boolean[] array) {
        if (array == null) return null;
        Boolean[] na = new Boolean[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将包装类型数组转为基本类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 基本类型数组
     * @throws NullPointerException 如果原数组中有元素为 null
     */
    public static byte[] toPrimitiveArray(Byte[] array) {
        if (array == null) return null;
        byte[] na = new byte[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将包装类型数组转为基本类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 基本类型数组
     * @throws NullPointerException 如果原数组中有元素为 null
     */
    public static short[] toPrimitiveArray(Short[] array) {
        if (array == null) return null;
        short[] na = new short[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将包装类型数组转为基本类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 基本类型数组
     * @throws NullPointerException 如果原数组中有元素为 null
     */
    public static int[] toPrimitiveArray(Integer[] array) {
        if (array == null) return null;
        int[] na = new int[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将包装类型数组转为基本类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 基本类型数组
     * @throws NullPointerException 如果原数组中有元素为 null
     */
    public static long[] toPrimitiveArray(Long[] array) {
        if (array == null) return null;
        long[] na = new long[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将包装类型数组转为基本类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 基本类型数组
     * @throws NullPointerException 如果原数组中有元素为 null
     */
    public static float[] toPrimitiveArray(Float[] array) {
        if (array == null) return null;
        float[] na = new float[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将包装类型数组转为基本类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 基本类型数组
     * @throws NullPointerException 如果原数组中有元素为 null
     */
    public static double[] toPrimitiveArray(Double[] array) {
        if (array == null) return null;
        double[] na = new double[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];

        return na;
    }

    /**
     * 将包装类型数组转为基本类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 基本类型数组
     * @throws NullPointerException 如果原数组中有元素为 null
     */
    public static char[] toPrimitiveArray(Character[] array) {
        if (array == null) return null;
        char[] na = new char[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    /**
     * 将包装类型数组转为基本类型数组，如果传入 null 也会返回 null
     *
     * @param array 数组
     * @return 基本类型数组
     * @throws NullPointerException 如果原数组中有元素为 null
     */
    public static boolean[] toPrimitiveArray(Boolean[] array) {
        if (array == null) return null;
        boolean[] na = new boolean[array.length];
        for (int i = 0; i < array.length; i++) na[i] = array[i];
        return na;
    }

    protected Convert() {
    }

}