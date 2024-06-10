package sugar.lambda;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * 基于 Lambda 方法引用（Method Reference）的反射工具
 * <p>根据 Java Bean 的要求，一个 Bean（POJO） 对象中应该为字段提供 public 的 getter / setter 方法（有时也称为“属性”）。
 * 这种情况下通过一个指向 getter 方法的 Lambda 能更简洁地传递一些“信息”。
 *
 * <p>例如对于一个 Bean 对象：<pre>{@code
 * public class Person {
 *     private String firstName;
 *     public String getFirstName() { return this.firstName; }
 *     public void setFirstName(String firstName) { this.firstName = firstName; }
 * }
 * }</pre>
 * 一个 Lambda {@code Person::getFirstName} 能表达一些等效的反射操作：<ul>
 * <li>获取 Bean 的 Class {@code Person.class}</li>
 * <li>获取 {@code firstName} 字段 {@code Person.class.getField("firstName")}</li>
 * <li>获取 getter 方法 {@code Person.class.getField("firstName")}</li>
 * </ul>
 * 故可以通过 getter 的 Lambda 来更简化的完成一些复杂的操作，例如：<ul>
 * <li>{@link #swap(Object, LambdaGetter, LambdaGetter)} 可交换两个同类型字段的值</li>
 * <li>{@link #copyProperty(Object, Object, LambdaGetter)} 可将一个对象中的指定字段值复制到另一个对象中</li>
 * <li>{@link #parseEnumByProperty(LambdaGetter, Object)} 可根据 getter 将值解析为枚举</li>
 * <li>...</li>
 * </ul>
 *
 * <p>相较于直接使用 {@link Object#getClass()} / {@link java.lang.reflect} 进行反射调用，使用 Lambda 有以下优点：
 * <ul>
 *     <li>Lambda 支持传递泛型（generics），编译代码时编译器会检查字段类型，减少因为类型导致的错误</li>
 *     <li>简化反射中对于类型的处理</li>
 *     <li>遇到跨方法传递字段时，IDE 能对查找代码引用处 / 自动补全 / 重命名提供更好的支持</li>
 *     <li>一些情况下使用 Lambda 替代反射调用 getter 方法可能会轻微地提高性能</li>
 * </ul>
 * <p>
 * 请留意，在此工具类中传入的 Lambda 一定要使用<b>方法引用</b>（如：{@code Person::getFirstName}）
 * 而不是普通的 Lambda 表达式（如：{@code (Person p) -> p.geFirstName()}），且指向的 getter 方法必须为
 * public，否则无法正确解析出相关的 Class 信息。
 * <p>对于需要用到 setter 方法的情况（如：{@link #swap(Object, LambdaGetter, LambdaGetter)} 等），
 * 需要的确保 Bean 对象中同时有 public 且符合命名规范的 getter / setter 方法。例如传入 {@code Person::getFirstName}
 * 工具类会尝试去查找 {@code Person} 类中 public 的 {@code setFirstName(...)} 方法，否则会抛出异常。
 *
 * @author SnowLT
 */
public class LambdaUtils {

    private static final String GETTER_PREFIX = "get";
    private static final String BOOL_GETTER_PREFIX = "is";
    private static final String SETTER_PREFIX = "set";

    private LambdaUtils() {
    }

    private static final Map<LambdaGetter<?, ?>, GetterInfo> LAMBDA_CACHE = new WeakHashMap<>();

    /**
     * 通过方法引用，获取声明 getter 方法的类。一般用于配合其他反射方法使用。
     * <p>例如传入 {@code Person::getFirstName}，则返回 {@code Class<Person>}。
     * <p>如果 getter 方法是从其他类中继承而来的，则返回实际的类。
     * <p>例如以下代码：<pre>{@code
     * public class BaseData { public String getDemo() { return "demo"; } }
     * public class SubData extends BaseData{ }
     * }</pre>
     * 传入 {@code SubData::getDemo}，实际返回的是 {@code Class<BaseData>}。
     *
     * @param <T>    对象的类型
     * @param getter 指向对象字段 getter 方法的方法引用
     * @return getter 方法所在的类
     * @throws LambdaParseException 如果 Lambda 无法被解析，或不是方法引用则抛出
     * @see #findField(LambdaGetter, boolean)
     * @see #findSetterMethod(LambdaGetter)
     * @see #findGetterMethod(LambdaGetter)
     */
    public static <T> Class<? super T> findDeclaredClass(LambdaGetter<T, ?> getter) {
        GetterInfo info = getGetterInfo(getter);
        return info.getDeclaredClass();
    }

    /**
     * 通过方法引用，获取对应字段的 {@link Field}。查找字段时会按小驼峰命名法进行匹配。
     * <p>例如传入 {@code Person::getFirstName}，则等效于调用 {@code Person.class.getDeclaredField("firstName")}。
     *
     * @param <T>    对象的类型
     * @param getter 指向对象字段 getter 方法的方法引用
     * @return {@link Field}
     * @throws LambdaParseException 在类中找不到对应字段，或 Lambda 不是方法引用
     * @see #findField(LambdaGetter, boolean)
     */
    public static <T> Field findField(LambdaGetter<T, ?> getter) {
        return findField(getter, false);
    }

    /**
     * 通过方法引用，获取对应字段的 {@link Field}。查找字段时会优先按小驼峰命名法进行匹配。
     * <p>例如传入 {@code Person::getFirstName}，则等效于调用 {@code Person.class.getDeclaredField("firstName")}。
     * <p>如果找不到这个字段，且 {@code ignoreCase} 设置为 true，则再尝试忽略大小写查找字段。
     *
     * @param <T>        对象的类型
     * @param getter     指向对象字段 getter 方法的方法引用
     * @param ignoreCase 设置为 true 时查找字段会尝试忽略大小写。
     * @return {@link Field}
     * @throws LambdaParseException   Lambda 不是方法引用或无法解析
     * @throws NoSuchElementException 在类中找不到对应字段
     */
    public static <T> Field findField(LambdaGetter<T, ?> getter, boolean ignoreCase) {
        GetterInfo info = getGetterInfo(getter);
        String directName = removeGetterPrefix(info.getMethodName());
        String candidateName = Character.toLowerCase(directName.charAt(0)) + directName.substring(1);
        Field field = null;
        try {
            // 优先查找符合 getter / setter 命名规范的 field
            field = info.getDeclaredClass().getDeclaredField(candidateName);
        } catch (NoSuchFieldException ignored) {
            if (ignoreCase) {
                field = Arrays.stream(info.getDeclaredClass().getDeclaredFields())
                        .filter(f -> f.getName().equalsIgnoreCase(candidateName))
                        .findFirst().orElse(null);
            }
        }
        if (field != null) return field;
        String message = String.format("Unable to find filed \"%s\" by getter in class \"%s\"",
                candidateName, info.getDeclaredClass().getName());
        throw new NoSuchElementException(message);
    }

    /**
     * 通过方法引用，获取 getter 方法的 {@link Method}。
     * <p>例如传入 {@code Person::getFirstName}，则等效于调用 {@code Person.class.getDeclaredMethod("getFirstName")}。
     *
     * @param <T>    对象的类型
     * @param getter 指向对象字段 getter 方法的方法引用
     * @return {@link Method}
     * @throws LambdaParseException   Lambda 不是方法引用或无法解析
     * @throws NoSuchElementException 在类中找不到对应 getter
     */
    public static <T> Method findGetterMethod(LambdaGetter<T, ?> getter) {
        GetterInfo info = getGetterInfo(getter);
        Class<?> c = info.getDeclaredClass();
        try {
            return c.getMethod(info.getMethodName());
        } catch (NoSuchMethodException e) {
            String message = String.format("Unable to find getter method \"%s()\" in class \"%s\", " +
                    "lambda may be an expression rather than a method reference", info.getMethodName(), c.getName());
            throw new NoSuchElementException(message);
        }
    }

    /**
     * 通过方法引用，获取相对应 setter 方法的 {@link Method}。
     * <p>例如传入 {@code Person::getFirstName}，则等效于调用 {@code Person.class.getDeclaredMethod("setFirstName")}。
     *
     * @param <T>    对象的类型
     * @param getter 指向对象字段 getter 方法的方法引用
     * @return {@link Method}
     * @throws LambdaParseException   Lambda 不是方法引用或无法解析
     * @throws NoSuchElementException 在类中找不到对应 setter
     */
    public static <T> Method findSetterMethod(LambdaGetter<T, ?> getter) {
        return findSetterMethod(getter, false);
    }

    /**
     * 通过方法引用，获取相对应 setter 方法的 {@link Method}。
     * <p>例如传入 {@code Person::getFirstName}，则等效于调用 {@code Person.class.getDeclaredMethod("setFirstName")}。
     *
     * @param <T>         对象的类型
     * @param getter      指向对象字段 getter 方法的方法引用
     * @param checkPublic 设置为 true 时会检查找到的 setter 方法是 public 的，否则按照找不到 setter 进行处理
     * @return {@link Method}
     * @throws LambdaParseException   Lambda 不是方法引用或无法解析
     * @throws NoSuchElementException 在类中找不到对应 setter
     */
    private static <T> Method findSetterMethod(LambdaGetter<T, ?> getter, boolean checkPublic) {
        GetterInfo info = getGetterInfo(getter);
        Class<T> c = info.getDeclaredClass();
        String setterName = predictSetterName(info.getMethodName());
        try {
            Method method = c.getMethod(setterName, info.getReturnType());
            if (checkPublic && !Modifier.isPublic(method.getModifiers())) {
                String message = String.format("Setter method \"%s(%s)\" is not public in class \"%s\"",
                        setterName, info.getReturnType().getName(), c.getName());
                throw new NoSuchElementException(message);
            }
            return method;
        } catch (NoSuchMethodException e) {
            String message = String.format("Unable to find setter method \"%s(%s)\" in class \"%s\"",
                    setterName, info.getReturnType().getName(), c.getName());
            throw new NoSuchElementException(message);
        }
    }

    /**
     * 通过方法引用，获取相对应 setter 方法的 {@link Method}，并包装为一个 {@link BiConsumer} 返回。
     * <p>调用 {@link BiConsumer#accept(Object, Object)} 等同于调用 {@link Method#invoke(Object, Object...)}。
     * <p>例如：<pre>{@code
     * Person p = new Person();
     * BiConsumer<Person, String> setterConsumer = findSetterAsConsumer(Person::getFirstName);
     * setterConsumer.invoke(p, "Alan");
     * }</pre>
     * <p>等效于：<pre>{@code
     * try {
     *     findSetter(Person::getFirstName).invoke(p, "Alan");
     * } catch (IllegalAccessException | InvocationTargetException e) {
     *     throw new IllegalStateException();
     * }
     * }</pre>
     * <p>如果无法根据方法引用找出 setter 方法则会直接抛出异常。
     *
     * @param <T>    对象的类型
     * @param <V>    对象中的字段类型
     * @param getter 指向对象字段 getter 方法的方法引用
     * @return {@link BiConsumer}
     * @throws LambdaParseException   Lambda 不是方法引用或无法解析
     * @throws NoSuchElementException 在类中找不到对应 setter
     */
    public static <T, V> BiConsumer<T, V> findSetterAsConsumer(LambdaGetter<T, V> getter) {
        Method setterMethod = findSetterMethod(getter, false);
        return (obj, arg) -> {
            try {
                setterMethod.invoke(obj, arg);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Failed to set value using setter " + setterMethod, e);
            }
        };
    }

    /**
     * 交换对象中同类型的两个字段的，要交换的两个字段通过向 {@code getter1} / {@code getter2} 传入方法引用来指定。
     * <p>工具类会自动解析 getter 的名称并查找对应的 setter 方法，并通过反射机制重新设置字段的值进行调用，并不会做类型转换工作。
     * <p> 例如：<pre>{@code
     * Person p = new Person();
     * p.setFirstName("Aiden");
     * p.setLastName("Pearce");
     * p.toString() // -> "Person{firstName=Aiden, lastName=Pearce}"
     * swap(p, Person::getFirstName, Person::getLastName);
     * p.toString() // -> "Person{firstName=Pearce, lastName=Aiden}"
     * }</pre>
     * <p>
     * 当两个字段值相等时（内存指向相同，{@code getter1.invoke(obj) == getter2.invoke(obj)}），
     * 方法会跳过查找方法或重新赋值的过程来提高速度。
     *
     * @param <T>     对象的类型
     * @param <V>     字段值的类型
     * @param object  对象
     * @param getter1 指向对象第一个字段 getter 方法的方法引用
     * @param getter2 指向对象第二个字段 getter 方法的方法引用
     * @throws LambdaParseException     在类中找不到对应 setter，或 Lambda 不是方法引用时抛出
     * @throws IllegalStateException    通过反射（调用 setter 方法）设置值遇到异常时抛出
     * @throws IllegalArgumentException {@code getter1} / {@code getter2} 返回类型不一致或包含 {@code null} 时抛出
     */
    public static <T, V> void swap(T object, LambdaGetter<T, V> getter1, LambdaGetter<T, V> getter2) {
        if (getter1 == null || getter2 == null) throw new IllegalArgumentException();
        if (object == null) return;
        V v1 = getter1.invoke(object);
        V v2 = getter2.invoke(object);
        if (v1 == v2) return;
        Method setterMethod1 = findSetterMethod(getter1, true);
        GetterInfo getterInfo1 = getGetterInfo(getter1);
        Method setterMethod2 = findSetterMethod(getter2, true);
        GetterInfo getterInfo2 = getGetterInfo(getter2);
        // 提前校验两个 getter 的返回类型是否相同。命中缓存能节省了重新解析 getter 的时间
        if (getterInfo1.getReturnType() != getterInfo2.getReturnType())
            throw new IllegalArgumentException(String.format("%s.%s() and %s.%s() have different return type",
                    getterInfo1.getDeclaredClass().getSimpleName(), getterInfo1.getMethodName(),
                    getterInfo2.getDeclaredClass().getSimpleName(), getterInfo2.getMethodName()));
        try {
            setterMethod1.invoke(object, v2);
            setterMethod2.invoke(object, v1);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to swap value between " + setterMethod1.getName() + " and " +
                    setterMethod2.getName(), e);
        }
    }

    /**
     * 将 {@code source} 对象中的一个字段值复制到 {@code target} 对象的相同字段上。要复制的字段通过方法引用来指定。
     *
     * @param <T>    对象的类型
     * @param <V>    对象中的字段类型
     * @param source the source
     * @param target the target
     * @param getter 指向对象字段 getter 方法的方法引用
     * @throws LambdaParseException  在类中找不到对应 setter，或 Lambda 不是方法引用时抛出
     * @throws IllegalStateException 通过反射（调用 setter 方法）设置值遇到异常时抛出
     */
    public static <T, V> void copyProperty(T source, T target, LambdaGetter<T, V> getter) {
        if (getter == null) throw new IllegalArgumentException();
        if (source == null || target == null) return;
        Method setter = findSetterMethod(getter);
        V v = getter.invoke(source);
        try {
            setter.invoke(target, v);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("设置 " + setter.getName() + " 对应字段的值失败", e);
        }
    }

    /**
     * 找到枚举中，字段值等于 {@code value} 的枚举项，如果匹配不到则返回 {@code null} 替代。
     * 一般用于解析重枚举（内部带字段的枚举）。
     * <p> 例如：<pre>{@code
     * public enum OrderType {
     *      FIRST(1), SECOND(2);
     *      private final Integer order;
     *      OrderType(Integer order) { this.order = order; }
     *      public Integer getOrder() { return this.order; }
     * }
     * parseEnumByField(OrderType::getOrder, 2)  // -> Order.SECOND
     * parseEnumByField(OrderType::getOrder, -1) // -> null
     * }</pre>
     * 由于 Java 的限制，传入的 getter 方法必须定义在枚举类中，不能从接口或 {@link Enum}
     * 中继承。否则会无法正确解析枚举类信息，抛出异常。
     *
     * @param <E>    枚举类型
     * @param <V>    枚举中的字段类型
     * @param getter 枚举中字段的 getter（方法引用）
     * @param value  字段的值
     * @return 如果找不到则返回 {@code other}
     * @throws LambdaParseException 如果 Lambda 指向的不是枚举类，或不是方法引用则抛出
     * @see #parseEnumByProperty(LambdaGetter, Object)
     */
    public static <E extends Enum<E>, V> E parseEnumByProperty(LambdaGetter<E, V> getter, V value) {
        return parseEnumByProperty(getter, value, null);
    }

    /**
     * 找到枚举中，字段值等于 {@code value} 的枚举项，如果匹配不到则返回 {@code defaultValue} 替代。
     * 一般用于解析重枚举（内部带字段的枚举）。
     * <p> 例如：<pre>{@code
     * public enum OrderType {
     *      FIRST(1), SECOND(2), THIRD(3), OTHERS(3);
     *      private final Integer order;
     *      OrderType(Integer order) { this.order = order; }
     *      public Integer getOrder() { return this.order; }
     * }
     * parseEnumByField(OrderType::getOrder, 2, Order.OTHERS)  // -> Order.SECOND
     * parseEnumByField(OrderType::getOrder, -1, Order.OTHERS) // -> Order.OTHERS
     * }</pre>
     * 由于 Java 的限制，传入的 getter 方法必须定义在枚举类中，不能从接口或 {@link Enum}
     * 中继承。否则会无法正确解析枚举类信息，抛出异常。
     *
     * @param <E>          枚举类型
     * @param <V>          枚举中的字段类型
     * @param getter       枚举中字段的 getter（方法引用）
     * @param value        字段的值
     * @param defaultValue 如果匹配不到枚举项则返回这个值替代
     * @return 如果找不到则返回 {@code defaultValue}
     * @throws LambdaParseException     如果 Lambda 指向的不是有效的枚举类，或不是方法引用则抛出
     * @throws IllegalArgumentException 如果传入的 getter 方法无法解析出枚举类的信息则抛出
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>, V> E parseEnumByProperty(LambdaGetter<E, V> getter, V value, E defaultValue) {
        Class<E> enumClass = (Class<E>) findDeclaredClass(getter);
        if (Enum.class.equals(enumClass))
            throw new IllegalArgumentException("Lambda should not refer to method in " + Enum.class.getName());
        E[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null) throw new IllegalArgumentException("Class of declared getter is not enum");
        for (E e : enumConstants) {
            if (Objects.equals(getter.invoke(e), value)) return e;
        }
        return defaultValue;
    }

    private static String predictSetterName(String getterName) {
        if (getterName.startsWith(GETTER_PREFIX)) {
            return SETTER_PREFIX + getterName.substring(3);
        } else if (getterName.startsWith(BOOL_GETTER_PREFIX)) {
            return SETTER_PREFIX + getterName.substring(2);
        } else {
            return getterName;
        }
    }

    private static String removeGetterPrefix(String getterName) {
        if (getterName.startsWith(GETTER_PREFIX)) {
            return getterName.substring(3);
        } else if (getterName.startsWith(BOOL_GETTER_PREFIX)) {
            return getterName.substring(2);
        } else {
            return getterName;
        }
    }

    private static GetterInfo getGetterInfo(LambdaGetter<?, ?> getter) {
        // 在 JDK 8/17 中已测试：无论 lambda 表达式的内容是否相同，每个位置都会生成一个新的 Class 和实例，
        // 故直接缓存这个 lambda 对象作为 key 即可
        GetterInfo info = LAMBDA_CACHE.get(getter);
        if (info != null) return info;
        synchronized (LAMBDA_CACHE) {
            return LAMBDA_CACHE.computeIfAbsent(getter, k -> new GetterInfo(parseSerializedLambda(getter)));
        }
    }

    private static SerializedLambda parseSerializedLambda(LambdaGetter<?, ?> getter) {
        try {
            Method method = getter.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            return (SerializedLambda) method.invoke(getter);
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    "Unable to get method reference, lambda might not implement Serializable", e);
        }
    }


    @SuppressWarnings("unchecked")
    static class GetterInfo {
        private final SerializedLambda serializedLambda;
        private final Class<?> declaredClass;
        private final Class<?> returnType;

        GetterInfo(SerializedLambda serializedLambda) {
            this.serializedLambda = serializedLambda;
            this.declaredClass = paresClass(serializedLambda);
            this.returnType = parseReturnType(serializedLambda, this.declaredClass);
        }

        private Class<?> paresClass(SerializedLambda lambda) {
            try {
                return Class.forName(lambda.getImplClass().replace('/', '.'));
            } catch (ClassNotFoundException e) {
                throw new LambdaParseException("Unable to get target class of method reference");
            }
        }

        private Class<?> parseReturnType(SerializedLambda lambda, Class<?> sourceClass) {
            String methodSignature = lambda.getImplMethodSignature();
            // primitive types, see Class.getName()
            switch (methodSignature) {
                case "()Z":
                    return boolean.class;
                case "()C":
                    return char.class;
                case "()D":
                    return double.class;
                case "()F":
                    return float.class;
                case "()I":
                    return int.class;
                case "()J":
                    return long.class;
                case "()S":
                    return short.class;
                case "()B":
                    return byte.class;
                default:
            }
            // text likes: "()Ljava/lang/Object;"
            int i = methodSignature.indexOf("()L");
            if (i >= 0) {
                String typeName = methodSignature.substring(i + 3, methodSignature.length() - 1).replace('/', '.');
                try {
                    return Class.forName(typeName);
                } catch (ClassNotFoundException ignored) {
                }
            }
            try {
                return sourceClass.getMethod(lambda.getImplMethodName()).getReturnType();
            } catch (NoSuchMethodException ignored) {
            }
            throw new LambdaParseException("Unable to parse getter. " +
                    "Lambda may be an expression rather than a method reference, or getter is not public");
        }

        public SerializedLambda getSerializedLambda() {
            return serializedLambda;
        }

        public String getMethodName() {
            return serializedLambda.getImplMethodName();
        }

        public <T> Class<T> getDeclaredClass() {
            return (Class<T>) declaredClass;
        }

        public <T> Class<T> getReturnType() {
            return (Class<T>) returnType;
        }
    }
}
