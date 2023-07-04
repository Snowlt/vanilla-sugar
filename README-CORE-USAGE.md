Vanilla Sugar Toolkit 核心用法介绍
=====================

这个文档用于介绍工具包的主要内容，以帮助快速上手使用。

文档侧重于以功能点的角度进行介绍，介绍一些核心的工具类方法及代码示例 Demo。

详细的方法介绍可参考方法 Javadoc 注释，工具包的说明文档请移步至 [README.md](README.md)

---

## 检查和判断 - `Check` 类

提供常见的检查和判断方法，例如 null 判断、内容相等判断等。

#### 简述

为了简化代码编写和记忆，`Check` 类中多数作用相似的方法取了相同的名字，例如判断 `String`、`Map` 和 `Collection` 长度不为空的方法名都叫 `notEmpty`；方法内部会自动判断类型，或通过方法重载来实现对应功能。

同时几乎所有方法都可以安全的传入 `null` 作为参数，不会抛出空指针异常。

#### 空判断

`isEmpty` 方法可判断常见可迭代对象（字符串、数组、`Iterable`等）的类型是否为 `null` 或长度为空。例如:

```java
Check.isEmpty("");            // => true
Check.isEmpty(new HashMap<>()); // => true
Check.isEmpty(new Object[]{"a", 1, 'B'});      // => false
Check.isEmpty(Arrays.asList(1, 2).iterator()); // => false
```

`notEmpty` 方法返回相反的结果，即 `Check.notEmpty(...)` 等效于 `!Check.isEmpty(...)`。

#### 常见类型的等效 true 判断

`isTrue` 方法可判断数字、字符、对象等是否等效于 `true`。

> *等效于 `true`* 即隐式转为布尔值，类似于 C/C++/Python/JavaScript 等语言中`if(表达式)` 的判断逻辑。
> 
> 对于不同类型等效于 `true` 的含义稍有不同，例如对于数字是不等于 `0`，对于字符串、集合、字典等对象是长度不为空，详细可参考 Javadoc 说明。

例如：

```java
Check.isTrue(1.23); // => true
Check.isTrue("abc"); // => true
Check.isTrue((Boolean) null); // => false
Check.isTrue(new HashMap<>()); // => false
```

`notTrue` 方法返回相反的结果，即 `Check.notTrue(...)` 等效于 `!Check.isTrue(...)`。

#### 字符串空白判断

`isBlank` 方法可用于判断字符序列中是否为 null、空或全是空白字符。例如：

```java
Check.isBlank(null);    // => true
Check.isBlank("  ");    // => true
Check.isBlank(" abc "); // => false
```

`notBlank` 方法返回相反的结果。

#### 多项 null 判断

以下方法可接收多个参数，可以用于进行与 `null` 的相关判断：

- `allNull`: 所有参数全都等于 `null`
- `anyNull`: 任意一个参数等于 `null`
- `noneNull`: 没有任何参数等于 `null`

以下是一些示例：

```java
Check.allNull(null, null, null); // => true
Check.anyNull(null, 0, "ABC");      // => true
Check.noneNull(0, 'A', "");       // => true
```

#### 多项等效 true 判断

以下方法可接收多个参数，可以用于进行等效于 `true` 的相关判断：

- `allTrue`: 所有参数全都等效于 `true`
- `anyTrue`: 任意一个参数等效于 `true`
- `noneTrue`: 没有任何参数等效于 `true`

以下是一些示例：

```java
Check.allTrue(1, 2.0, 'A'); // => true
Check.anyTrue(null, 0, "ABC"); // => false
Check.noneTrue(null, 0, ""); // => true
```

#### 异或

`Check.xor` 提供了异或运算，但可传入不同的参数类型，方法内部会自动将参数套用 `isTrue` 转为等效布尔值进行运算。例如：

```java
Integer a = 0;
Character b = 'A';
System.out.println(Check.xor(a, b)); // => false
// 等效于
System.out.println(Check.isTrue(a) ^ Check.isTrue(b)); // => false
```

#### 内容相等判断

以下是一些常用的方法，可判断两个常见类型的对象内容（值）是否相等。

- `contentEquals(CharSequence, CharSequence)`: 判断字符序列的内容是否相同

- `equalsAsNumber(Number, Number)`: 判断两个数字类型的数值是否相同，可以自动处理类型转换的问题

- `equalsAsNumber(Number, CharSequence)`: 判断数字的数值，是否和字符串文本表示的数值（按十进制解析）相同

- `equalsAsList(Collection, Collection)`: 将两个集合都视为 `List`，判断两个集合中的内容（对应位置的元素）是否相同
  
  *同 Apache Commons Collections 4 的 `ListUtils.isEqualList` 方法*

- `equalsAsIterable(Object, Object)`: 判断两个可迭代对象（数组、`Collection`、`Iterable`）中的内容（对应位置的元素）是否相同

- `equalsAsSet(Collection, Collection)`: 将两个集合都视为 `Set` 并比较内容是否相等，即 `Check.equalsAsSet(a, b)` 类似 `new HashSet(a).equals(new HashSet(b))`

代码示例如下：

```java
Check.contentEquals(new StringBuilder("ABC"), "ABC"); // => true
Check.equalsAsNumber(12.34D, 12.34F); // => true
Check.equalsAsNumber(1.2, "1.20");    // => true
Check.equalsAsList(Arrays.asList(1, 2, 3), new ArrayDeque<>(Arrays.asList(1, 2, 3))); // => true
Check.equalsAsIterable(new int[]{1, 2, 3}, new ArrayDeque<>(Arrays.asList(1, 2, 3))); // => true
Check.equalsAsSet(Arrays.asList(1, 2), Arrays.asList(2, 1)); // => true
```



## 类型转换 - `Convert` 类

提供转化为常用数据类型的方法。

#### 简述

`Convert` 类中的方法支持传入不同类型的参数进行转换，相较于 Java 内置的 *parse* 方法（例如 `Long.parseLong(String)`）进行了增强，且可简化 `try{...} catch{...}` 异常的场景。

> 对于熟悉 .Net 开发者，这个工具类的设计参考了 .Net 中 Convert 类 API，提供了部分相似的功能。

#### 转为数字 Number

以下方法可以将常见类型的值转换到对应的等效数字类型。

- `toByte`: 转为 Byte 类型
- `toShort`: 转为 Short 类型
- `toInt`: 转为 Integer 类型
- `toLong`: 转为 Long 类型
- `toFloat`: 转为 Float 类型
- `toDouble`: 转为 Double 类型

这些方法都接收两个参数：**待转换对象**和**默认值**，根据不同的待转换对象：

- 对于数字，会强制转换为对应的类型（例如: `Convert.toInt(123.456, null)` 等效于 `(int)123.456`，返回 `123`）
- 对于布尔值，`true` 返回 `1`，`false` 返回 `0`
- 对于字符串，会尝试调用对应的 *parse* 方法（例如: `Convert.toDouble("123.456", null)`
  等效于 `Double.parseDouble("123.456")`）
- 其他情况或者转换失败，将返回**默认值**作为替代

例如：

```java
// 可转换的类型，转为等效的 Character
Convert.toByte('a', null); // => 65
Convert.toInt(true, null); // => 1
Convert.toLong("12345", null); // => 12345
Convert.toDouble("123.456", null); // => 123.456
// 不能转换的类型返回默认值
Convert.toInt("BB", null); // => null
Convert.toInt(null, 123); // => 123
```

同时这些方法也都有只带一个参数的重载版本，在遇到无法转换为对应数字类型的时候会返回 0。 例如：

```java
Convert.toInt("65"); // => 65
Convert.toInt(null); // => 0
Convert.toDouble("123.456"); // => 123.456
Convert.toDouble("ABC"); // => 0.0
```

#### 转为字符 Character

`toChar` 可以将常见类型的值转换到等效的 Unicode 字符。 同时接收第二个参数，作为无法转换时替代返回的默认值。

- 对于字符会直接返回自身
- 对于字符序列(字符串)，如果长度为 1 则会返回首个字符，否则视为转换失败返回默认值
- 对于数字，会强制转换为 char 类型后返回，等效于: `(char) ((Number) value).intValue()`
- 其他情况则视为无效的转换，将返回替代的默认值

例如：

```java
// 可转换的类型，转为等效的 Character
Convert.toChar('a', null); // => 'a'
Convert.toChar(65, null); // => 'A'
Convert.toChar("B", null); // => 'B'
// 不能转换的类型返回默认值
Convert.toChar("BB", null); // => null
Convert.toChar(null, 'A'); // => 'A'
```

#### 转为布尔值 Boolean

`toBoolean` 可以将常见类型的值转换到等效的 Boolean 类型。 同时接收第二个参数，作为无法转换时替代返回的默认值。

- 对于 Boolean 会直接返回自身
- 对于数字，数值为 `0` 则会转为 `false`，其他值则会转为 `true`（浮点数会统一当作double类型来判断）
- 对于字符序列，如果文本内容等于 `"True"`（不区分大小写）则会转为 `true`，其他值则会转为 `false`
- 传入 `null` 时总是返回 `false`

例如：

```java
// 可转换的类型，转为等效的 Boolean
Convert.toBoolean(1, null); // => true
Convert.toBoolean(0.0, null); // => false
Convert.toBoolean("TruE", null); // => true
Convert.toBoolean(null, null); // => false
// 不能转换的类型返回默认值
Convert.toBoolean('A', null); // => null
Convert.toBoolean(new HashMap<>(), null); // => null
Convert.toBoolean(new Object(), false); // => false
```

此方法还提供只有一个参数的重载版本，遇到无法转换的类型时会返回 `false`。例如：

```java
// 可转换的类型，转为等效的 Boolean
Convert.toBoolean("TruE"); // => true
Convert.toBoolean(null); // => false
// 不能转换的类型
Convert.toBoolean('A'); // => false
Convert.toBoolean(new Object()); // => false
```

#### 数组转换

对于以下数组类型之间转换：

| 基本类型数组    | 转换方向 | 包装类型数组    |
|:---------:|:----:|:---------:|
| byte[]    | <--> | Byte[]    |
| short[]   | <--> | Short[]   |
| int[]     | <--> | Integer[] |
| long[]    | <--> | Long[]    |
| float[]   | <--> | Float[]   |
| double[]  | <--> | Double[]  |
| boolean[] | <--> | Boolean[] |

- `toArray` 可将**基本类型数组**转换为**包装类型数组**

- `toPrimitiveArray` 可将**包装类型数组**转换为**基本类型数组**

例如：

```java
// 基本类型数组 => 包装类型数组
int[] primitive = new int[]{2, 4, 6, 8};
Integer[] boxed = Convert.toArray(primitive);
System.out.println(Arrays.toString(primitive)); // [2, 4, 6, 8]
System.out.println(Arrays.toString(boxed));     // [2, 4, 6, 8]
// 包装类型数组 => 基本类型数组
Double[] boxed = new Double[]{1.1, 3.3, 5.5, 7.7};
double[] primitive = Convert.toPrimitiveArray(boxed);
System.out.println(Arrays.toString(boxed));     // [1.1, 3.3, 5.5, 7.7]
System.out.println(Arrays.toString(primitive)); // [1.1, 3.3, 5.5, 7.7]
```

 

## 索引操作 - `Indexer` 类

为可索引的对象（例如列表、字符串等）提供增强的查找功能。

#### 基本用法

常见的方法如下:

- at: 传入可索引对象和下标，获取指定下标处的元素，可用负数索引（负数表示后往前反向索引）
- first: 传入可索引对象，获取第一个元素
- last: 传入可索引对象，获取最后一个元素

这些方法支持操作: 数组、列表 `List`、字符序列 `CharSequence` 、可迭代对象 `Iterable`(见备注)。例如：

```java
double n1 = Indexer.at(new double[]{1.1, 2.2, 3.3}, 2);  // 返回下标为2的数字 3.3
double n2 = Indexer.at(new double[]{1.1, 2.2, 3.3}, -1); // 返回从后往前第一个数字 3.3
char first = Indexer.first("ABC");                       // 返回第一个字符 'A'
Integer last = Indexer.last(Arrays.asList(1, 2, 3));     // 返回最后一个数字 3
try {
    Indexer.first((String) null); // 不能传入 null，会抛出异常
    Indexer.at("", 0);            // 空字符串不存在索引位置0，下标越界，会抛出异常
} catch (Exception e) {
}
```

以上方法都有一个名称相近且以 *OrDefault* 结尾的方法，可多传入一个参数表示默认值，在遇到问题时不抛出异常而会返回默认值。例如：

```java
Indexer.atOrDefault(new int[]{1, 2}, 10, 0); // => 0
Indexer.firstOrDefault("", 'N');             // => 'N'
```

*备注：在索引 `Iterable` 可迭代对象时(`List` 除外)，会使用 `Iterable.iterator()`
执行迭代直到找到目标位置。因此实现类需要遵守每次调用 `iterator()` 都返回新迭代器的约定，否则会发生未知的问题。*



## 切片操作 - `Slice` 类

为支持索引的对象提供切片操作。

#### 简述

切片是对可索引对象（有时也叫序列型对象，如列表、字符串等）的一种高级索引截取方法，用于从序列中取出一部分元素重新组成一个新的序列（比如截取、反转等）。

#### 基本用法

切片工具类基本用法为 `Slice.slice(可索引对象, 起始, 结束, 步长)`， 4 个参数作用分别是：

1. **可索引对象**: 支持数组、列表 `List`、字符序列 `CharSequence` 、`Collection`(视为 List 处理)，长度可为 0 但不能为 `null`
2. **起始**(包含): 开始截取的下标位置，如果省略默认为0
3. **结束**(不包含): 停止截取的下标位置，如果省略默认为序列的长度
4. **步长**: 表示到下一个元素的距离增量(即切片的间隔)，如果省略默认为1，但不能为0

除第一个参数外，其他参数都可以传入 `null` 值，表示省略对应位置的参数。

#### 示例

假设 `List<Integer> lst = List.of(1, 2, 3, 4, 5)`，以下是一些示例：

- `Slice.slice(lst, 0, 3, null)` 表示从第0个元素开始，到第3个元素结束（不包括），步长为1，结果是`[1, 2, 3]`
- `Slice.slice(lst, 1, null, null)` 表示从第1个元素开始，到最后一个元素结束，步长为1，结果是`[2, 3, 4, 5]`
- `Slice.slice(lst, null, 4, null)` 表示从第0个元素开始，到第4个元素结束（不包括），步长为1，结果是`[1, 2, 3, 4]`
- `Slice.slice(lst, null, null, 2)` 表示从第0个元素开始，到最后一个元素结束，步长为2，结果是`[1, 3, 5]`
- `Slice.slice(lst, null, null, -1)` 表示从最后一个元素开始，到第0个元素结束（不包括），步长为-1，结果是`[5, 4, 3, 2, 1]`

#### 与 for 循环对比

`slice` 方法的参数与 `for` 循环中一些关键参数含义相近:

```java
int[] a = {1, 2, 3, 4, 5}; // 原始的可索引对象
ArrayList<Integer> b = new ArrayList<>();
for (int i = 起始下标; i < 结束下标; i += 步长) {
    int value = a[i]; // 从原对象中获取元素并加入到新序列中
    b.add(value);
}
b.toArray(); // 新的对象
```

#### 从 Python 迁移

> 对于熟悉 Python 的开发者，这个 Java 工具类的用法与 Python 中的切片语法（`可索引对象[起始:结束:步长]`）非常相似。

假设有 Java 代码 `List<Integer> lst = List.of(1, 2, 3, 4, 5)` 和 Python 代码 `lst = [1, 2, 3, 4, 5]`，以下是一些对照参考：

| Java代码                            | 等效Python代码  | 结果                |
| --------------------------------- | ----------- | ----------------- |
| `Slice.slice(lst, 0, 3, null)`    | `lst[0:3]`  | `[1, 2, 3]`       |
| `Slice.slice(lst, null, null, 2)` | `lst[::2]`  | `[1, 3, 5]`       |
| `Slice.slice(lst, 0, 3, null)`    | `lst[::-1]` | `[5, 4, 3, 2, 1]` |

 

## 枚举辅助 - `EnumUtils` 类

提供常用的枚举类辅助功能。

#### 代码示例

以下代码中展示了一些常用方法：

```java
public class Demo {
    enum TestEnum {FIRST, SECOND, THIRD}

    public static void main(String[] args) {
        // 获取 TestEnum 中序号为 1（第二个）的枚举项，将输出: SECOND
        System.out.println(EnumUtils.fromOrdinal(TestEnum.class, 1));
        // 获取 TestEnum 中序号为 11 的枚举项，由于不存故返回最后一个参数指定的 TestEnum.FIRST 作为替代，将输出: FIRST
        System.out.println(EnumUtils.fromOrdinal(TestEnum.class, 11, TestEnum.FIRST));
        // 获取 TestEnum 中名为"Second"的项(忽略大小写)，将输出: SECOND
        System.out.println(EnumUtils.fromNameIgnoreCase(TestEnum.class, "Second", null));
        // 获取 TestEnum 中名为"Fourth"的项(忽略大小写)，由于不存故返回最后一个参数指定的 TestEnum.FIRST 作为替代，将输出: FIRST
        System.out.println(EnumUtils.fromNameIgnoreCase(TestEnum.class, "Fourth", TestEnum.FIRST));
        // 获取 TestEnum 所有枚举项的名字，将输出: [FIRST, SECOND, THIRD]
        System.out.println(EnumUtils.getNames(TestEnum.class));
        // 根据 TestEnum 生成一个 Map，Key 为 枚举项的名字，Value 为枚举项序号(ordinal)，将输出: {FIRST=0, SECOND=1, THIRD=2}
        System.out.println(EnumUtils.toMap(TestEnum.class, TestEnum::name, TestEnum::ordinal));
        // 判断传入的对象是否为枚举类型
        System.out.println(EnumUtils.isEnum(TestEnum.SECOND)); // => true
        System.out.println(EnumUtils.isEnum(new Object()));    // => false
    }
}
```

如果枚举类型中带有字段（有时也称为“重枚举”），希望根据某些字段的值转换枚举类型，该工具类也提供了相关的方法。

```java
public class Demo {

    enum HttpStatus {
        INVALID(0), OK(200), NOT_FOUND(404), SERVER_ERROR(500);

        private final int code;

        public int getCode() {
            return code;
        }

        HttpStatus(int code) {
            this.code = code;
        }
    }

    public static void main(String[] args) {
        // 找到 HttpStatus 中 getCode() 等于 500 的枚举项，将输出: SERVER_ERROR
        System.out.println(EnumUtils.getEnum(HttpStatus.class, 500, HttpStatus::getCode));
        // 在 searchRange 中寻找并返回 getCode() 等于 500 的枚举项，如果找不到则返回最后一个参数指定的 HttpStatus.INVALID 作为替代
        // 由于 searchRange 中不包含 HttpStatus.SERVER_ERROR，将输出: INVALID
        HttpStatus[] searchRange = {HttpStatus.OK, HttpStatus.NOT_FOUND};
        System.out.println(EnumUtils.getEnum(searchRange, 500, HttpStatus::getCode, HttpStatus.INVALID));
    }
}
```

 

## 元组 - `Tuple` 类

#### 简述

元组，表示一个用于存放多个值的不可变容器。

类似于不可修改的 `List`（例如 `Collections.unmodifiableList()` 和 `List.of()` 返回的对象），但元组可通过泛型保留每个元素的类型。

例如有时希望让方法同时返回多个不同类型的值（可参考下方代码示例），想保留值的类型但又不想定义新对象，此时就可以使用元组进行包装后返回
*（返回的数量建议不超过 6 个，否则可能会导致代码难以阅读）*。

#### 基本用法

- `Tuple.of(...)` 方法可以创建并返回一个元组对象；
  - 根据传入的元素数量不同会创建出：`Tuple2`, `Tuple3`, ...,`Tuple6` ，这些元组对象都在内部维持了具体的元素类型；
  - 调用 *get* 开头的方法来获取对应位置的元素（序号从0开始，例如 `get0()` 获取第一个元素，`get1`
    获取第二个元素...），返回类型是创建元组时值的类型；
- 对元组对象调用 `get(...)` 方法可传入下标并获取下标位置的元素（从0开始），返回类型是 `Object`；
- 调用元组对象的 `toList()` 和 `toArray()` 方法可以分别生成具有相同元素顺序的 List 和数组；
- 元组实现了 `Iterator<Object>` 接口，可以通过 for-each 语法进行迭代，或者调用 `iterator()` 方法获取迭代器。

#### 代码示例

以下的代码中，`getDifferentRandom` 方法借助元组返回了包含三个不同类型的值：

```java
public class Demo {
    /**
     * 随机生成并返回一个整数、字符和布尔值
     *
     * @param seed 随机种子
     */
    public static Tuple3<Integer, Character, Boolean> getDifferentRandom(int seed) {
        Random random = new Random(seed);
        int a = random.nextInt(100);
        char b = (char) (random.nextInt(27) + 65);
        boolean c = random.nextInt(2) == 1;
        // 使用 Tuple.of(...) 方法创建元组
        return Tuple.of(a, b, c);
    }

    public static void main(String[] args) {
        Tuple3<Integer, Character, Boolean> rand = getDifferentRandom(1);
        // 使用 get?() 方法获取对应类型的值
        Integer randomInt = rand.get0();
        Character randomChar = rand.get1();
        Boolean randomBool = rand.get2();
        // 打印结果，输出: Integer 85, Character B, Boolean false
        System.out.printf("Integer %d, Character %s, Boolean %s", randomInt, randomChar, randomBool);
    }
}
```