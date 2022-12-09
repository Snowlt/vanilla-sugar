package sugar.core.simplification;

/**
 * 辅助转换的工具
 * <p>提供类似于 .Net 中 System.Convert 的相关方法</p>
 *
 * @author SnowLT
 * @version 1.4
 * @since 2020/9/5
 */
public class Convert {

    /**
     * 转换到整型，转换失败时返回 0
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Integer toInt(Object value) {
        return toInt(value, 0);
    }

    /**
     * 转换到整型
     *
     * @param value 值
     * @param def   转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Integer toInt(Object value, Integer def) {
        if (value == null) return def;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof Boolean) return (Boolean) value ? 1 : 0;
        if (value instanceof Character) return (int) (Character) value;
        try {
            return (int) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 转换到长整型，转换失败时返回 0
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Long toLong(Object value) {
        return toLong(value, 0L);
    }

    /**
     * 转换到长整型
     *
     * @param value 值
     * @param def   转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Long toLong(Object value, Long def) {
        if (value == null) return def;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof Boolean) return (Boolean) value ? 1L : 0;
        if (value instanceof Character) return (long) (Character) value;
        try {
            return (long) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 转换到 Byte 型，转换失败时返回 0
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Byte toByte(Object value) {
        return toByte(value, (byte) 0);
    }

    /**
     * 转换到 Byte 型
     *
     * @param value 值
     * @param def   转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Byte toByte(Object value, Byte def) {
        if (value == null) return def;
        if (value instanceof Byte) return (Byte) value;
        if (value instanceof Number) return ((Number) value).byteValue();
        if (value instanceof Boolean) return (byte) ((Boolean) value ? 1 : 0);
        if (value instanceof Character) return (byte) (char) (Character) value;
        try {
            return (byte) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 转换到 Short 型，转换失败时返回 0
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Short toShort(Object value) {
        return toShort(value, (short) 0);
    }

    /**
     * 转换到 Short 型
     *
     * @param value 值
     * @param def   转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Short toShort(Object value, Short def) {
        if (value == null) return def;
        if (value instanceof Short) return (Short) value;
        if (value instanceof Number) return ((Number) value).shortValue();
        if (value instanceof Boolean) return (short) ((Boolean) value ? 1 : 0);
        if (value instanceof Character) return (short) (char) (Character) value;
        try {
            return (short) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 转换到单精度浮点型，转换失败时返回 0
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Float toFloat(Object value) {
        return toFloat(value, 0F);
    }

    /**
     * 转换到单精度浮点型
     *
     * @param value 值
     * @param def   转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Float toFloat(Object value, Float def) {
        if (value == null) return def;
        if (value instanceof Float) return (Float) value;
        if (value instanceof Number) return ((Number) value).floatValue();
        if (value instanceof Boolean) return (Boolean) value ? 1F : 0;
        if (value instanceof Character) return (float) (Character) value;
        try {
            return Float.parseFloat(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 转换到双精度浮点型，转换失败时返回 0
     *
     * @param value 值
     * @return 转换后的结果
     */
    public static Double toDouble(Object value) {
        return toDouble(value, 0.0);
    }

    /**
     * 转换到双精度浮点型
     *
     * @param value 值
     * @param def   转换失败时，返回的默认值
     * @return 转换后的结果
     */
    public static Double toDouble(Object value, Double def) {
        if (value == null) return def;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof Boolean) return (Boolean) value ? 1.0 : 0;
        if (value instanceof Character) return (double) (Character) value;
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 转换到字符类型
     * 根据 value 类型和值不同，以下情况会返回 true：
     * <p>
     * 根据 value 类型不同，返回结果有以下情况：
     *  <ol>
     *  <li>null: 返回 <i>默认值</i></li>
     *  <li>Number: 返回强制转换为 (char) 后的值</li>
     *  <li>CharSequence: 如果字符串长度为 <i>1</i> 返回首个字符，否则返回 <i>默认值</i></li>
     *  <li>其他情况: 返回 <i>默认值</i></li>
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
     * @param value 值
     * @param def   value 为 null 或无法转换为字符类型返回的默认值
     * @return 转换后的结果
     */
    public static Character toChar(Object value, Character def) {
        if (value == null) return def;
        if (value instanceof Character) return (Character) value;
        if (value instanceof Number) return (char) ((Number) value).longValue();
        if (value instanceof CharSequence && ((CharSequence) value).length() == 1)
            return ((CharSequence) value).charAt(0);
        return def;
    }

    /**
     * 转换到布尔类型
     * <p>
     * 根据 value 类型和值不同，以下情况会返回 true：
     * <ol>
     *  <li>Boolean: 值为 true 时返回 true</li>
     *  <li>Number: 值不等于 0 时返回 true</li>
     * </ol>
     * 其他情况都会返回 false
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
     *  <li>null: 返回 <i>默认值</i></li>
     *  <li>Boolean: 返回自身的值</li>
     *  <li>Number: 不为 0 返回 true，为 0 返回 false</li>
     *  <li>String 或 CharSequence: 如果字符串内容等于 <i>"true"</i>（忽略大小写）返回 true，否则返回 false</li>
     *  <li>其他情况: 返回 <i>默认值</i></li>
     *  </ol>
     * </p>
     * <pre>
     * 例如：
     *     Convert.toBoolean(1, null) -> true
     *     Convert.toBoolean(0, null) -> false
     *     Convert.toBoolean("TruE", null) -> true
     *     Convert.toBoolean("yes", null) -> false
     *     Convert.toBoolean(null, true) -> true
     *     Convert.toBoolean(new Object, null) -> null
     * </pre>
     *
     * @param value        值
     * @param defaultValue 无法转换时返回的默认值
     * @return 转换后的结果
     */
    public static Boolean toBoolean(Object value, Boolean defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) {
            Number n = (Number) value;
            return n.longValue() != 0 || n.doubleValue() != 0.0;
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
     * 将基本类型数组转为包装类型数组
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
     * 将基本类型数组转为包装类型数组
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
     * 将基本类型数组转为包装类型数组
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
     * 将基本类型数组转为包装类型数组
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
     * 将基本类型数组转为包装类型数组
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
     * 将基本类型数组转为包装类型数组
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
     * 将基本类型数组转为包装类型数组
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
     * 将基本类型数组转为包装类型数组
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

    protected Convert() {
    }

}