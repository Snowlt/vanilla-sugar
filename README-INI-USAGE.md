Vanilla Sugar Toolkit - INI 模块用法介绍
=====================

这个文档用于介绍 *vanilla-sugar-ini* 模块的主要内容，以帮助快速上手使用。

*vanilla-sugar-ini* 模块针对 INI 文件格式，提供基于对象的读取、编辑和写入功能。

文档侧重于以功能点的角度进行介绍，介绍关键用法及代码示例 Demo。

详细的方法介绍可参考方法 Javadoc 注释，整个工具包的说明文档请移步至 [README.md](README.md)

---

## 关于 INI

[INI 文件（`*.ini`）](https://en.wikipedia.org/wiki/INI_file)
是一种基于文本的软件配置文件，内容由区块和键值对组成。该格式已成为许多软件使用的非正式标准，一些常见的文件格式后缀为：`.ini`、`.cfg`、`.inf`。

### 格式

不像 JSON / XML 等格式，INI 没有严格的格式标准，每个软件使用/支持的 INI 格式可能会略有不同。

但一般而言，INI 文件由以下部分组成：

- 键值对（Key-value pair）：INI 中实际的数据内容。由 `=`
  符号分隔，前半部分作为键（Key），后半部分为值（Value）。一般如果一行中有多个 `=` 号则以首次出现的位置作为分隔点。
- 注释（Comment）：每行以 `;` 或 `#` 开头的内容被视为注释。一般作为说明性文本而非数据内容存在。
- 区块（Section）：多个键值对（和注释）可作为一组，这样一个组称为*区块*。一个区块头部以 `[` 开始，`]` 结束，中间是区块名（Section
  name）。一个区块的范围由区块头开始，直到遇到下一个区块头部或文件结尾为止。

以下是一个典型的 INI 文件内容：

```ini
[project]
name = Demo
version = 1.0
[database]
; Manually configure DB address
host = localhost
port = 3306
```

文件中包含了两个区块：`project`、`database`。其中 `database` 区块包含了一条注释以及键名分别为：`host`、`port` 的两条键值对。

## 使用

### 快速开始

*vanilla-sugar-ini* 提供了 `Ini` 类用于表示一个 INI 文件，一个 `Ini` 中有多个区块。而一个区块则封装为了 `Section` 类，可以读写区块内的键值对和注释。

`IniReaderWriter` 类作为快捷操作的入口，提供了一些用于文件读写的静态方法。

- `Ini loadFromFile(String path)`: 传入文件路径，读取文件内容并解析为一个 `INI` 对象。

- `void saveToFile(Ini ini, String path)`: 将 `INI` 对象中的内容以文本的方式写入文件。

以下是一份示例代码：

```java
// 新建一个 INI
Ini ini = new Ini();
// 添加一个名为 demo 的区块
Section section = ini.getOrAdd("demo");
// 向 demo 区块中写入两项（键值对）
section.set("aa", "1");
section.set("bb", "2.0");
// 以 UTF-8 编码写入到 test.ini 文件中
IniReaderWriter.saveToFile(ini, "test.ini", StandardCharsets.UTF_8);
```

### 编辑内容

`Ini` 类内部存储了区块的数据，也是读写、编辑 INI 内容的入口。常用的方法如下：

- `Ini()` 构造器: 构造一个 INI 对象

- `get(String)`: 获取此 INI 中的指定区块（一般操作的时间复杂度为 *O(1)* ）

- `getOrAdd(String)`: 获取此 INI 中的指定区块，如果指定的区块不存在，则先创建再返回区块

- `remove(String)`: 移除指定区块

- `getSectionNames()`: 获取所有的区块名

`Section` 类存储了单个区块内的键值对、注释等数据。由于一个区块是与 INI 文件一一对应的，故 `Section` 对象不能直接通过 `new` 实例化，而是需要从一个 `Ini` 对象上获取（例如 `getOrAdd` 方法）。

常用的方法如下：

- `get(String)`: 通过键名获取对应的值（一般操作的时间复杂度为 *O(1)* ）
- `getAsInt(String)` / `getAsBool(String)`: 通过键名获取对应的值并转为特定基本类型
- `set(String, String)`: 添加新的键值对，如果已有相同键名则会覆盖
- `remove(String)`: 移除键值对
- `count()`: 获取此区块键值对的总数
- `getKeys()`: 获取所有的键名
- `addComments(Collection<String>)`: 添加注释
- `getCommentsBefore(String)` / `getCommentsAfter(String)`: 获取指定键名之前/之后的注释

示例代码：

```java
Ini ini = new Ini();
Section projectSection = ini.getOrAdd("project");
projectSection.addComments("Basic project information");
projectSection.set("name", "Demo Project");
projectSection.set("version", "2.0");
Section cacheSection = ini.getOrAdd("cache");
cacheSection.set("type", "redis");
cacheSection.set("host", "127.0.0.1");
cacheSection.set("port", "6379");
cacheSection.remove("port");
```

生成的 INI 内容如下：

```ini
[project]
;Basic project information
name=Demo Project
version=2.0
[cache]
type=redis
host=127.0.0.1
```

### 从 IO 流中读写

当需要直接从 `InputStream` 、`OutputStream` 中读写，或者需要对 IO 操作进行一些自定义调整，可以使用 `IniDeserializer` 和 `IniSerializer` 。

使用 `IniDeserializer` 可以从流中读取内容并生成 INI 对象。常用方法如下：

- `read(InputStream, Charset)` : 从流中读取内容，解析为 `Ini` 对象

- `read(Reader)` : 从流中读取内容，解析为 `Ini` 对象

- `setCommentPrefixes(Set<String>)`: 设置要解析的文件中注释的前缀，不设置的话默认将 `;` 和 `#` 识别为注释前缀

- `setTrimKey(boolean)`: 设置读取区块中键名的时候是否去除首尾空白

- `setTrimValue(boolean)`: 设置读取区块中值的时候是否去除首尾空白

示例代码：

```java
IniDeserializer deserializer = new IniDeserializer();
// 调整一些解析设置
deserializer.setTrimValue(true).setTrimKey(true);
// 从文件流中解析读取
Ini ini = deserializer.read(new FileInputStream("test.ini"), StandardCharsets.UTF_8);
```

使用 `IniSerializer` 可以将 INI 对象写入到流中。常用方法如下：

- `write(Ini, OutputStream, Charset)`: 将 INI 对象写入到流中
- `write(Ini, Writer)`: 将 INI 对象写入到流中
- `setLineSeparator(String)`: 设置输出时使用的换行符，不设置的话默认为 `System.lineSeparator()`
- `setCommentPrefix(String)`: 设置注释的前缀符号，不设置的话默认为 `;`

示例代码：

```java
Ini ini = new Ini();
// 编辑 INI 内容...
IniSerializer serializer = new IniSerializer();
// 调整一些设置
serializer.setAddSpaceAroundEqualizer(true);
// 将 INI 内容写入到 StringWriter 流中
StringWriter writer = new StringWriter();
serializer.write(ini, writer);
```

### 链式操作

对于连续编辑多个键值对、注释的情况，*vanilla-sugar-ini* 提供了链式操作 API。在 `Ini` 对象上调用 `chainAccess()` 方法会返回一个链式访问器，用法如下：

1. 接着借助 `openSection(String)` 可以切换到某个特定区块中操作；

2. 使用 `set(String key, String)` / `addComment(String comment)` 等方法可以编辑区块内容；

3. 操作完单个区块后，使用 `closeSection()` 方法返回上级INI，以继续链式操作其他区块。

示例代码：

```java
Ini ini = new Ini();
ini.chainAccess()
    // 切换到 test 1 区块操作
    .openSection("test 1")
        // 添加一个键值对和注释
        .set("a", "1").addComment("Hello World")
        // 返回到 INI 继续操作
        .closeSection()
    // 切换到 test 2 区块操作
    .openSection("test 2")
        // 添加两个键值对和注释
        .set("b", "2").set("c", "3")
        // 返回到 INI 继续操作
        .closeSection()
    // 添加一个名为 empty 的区块，但不写入任何键值对
    .openSection("empty").closeSection();
```

生成的 INI 内容如下：

```ini
[test 1]
a=1
;Hello World
[test 2]
b=2
c=3
[empty]
```

### 操作无标题区块

有时可能会遇到一些特殊的 INI 文件，其中文件头部没有出现任何区块头部，而是直接出现内容。例如以下的文件中，`project` 区块前还有三行内容：

```ini
; special content
debug=true
hash=0xaf23
[project]
name=Demo Project
```

在首个区块头部前出现的部分（例如示例中的前三行）也可以被视为一个特殊的*无标题区块*（一些程序也称之为 *全局属性* 区块）。*vanilla-sugar-ini* 对无标题区块也提供了解析、编辑的支持。

在 `Ini` 对象上调用 `getUntitledSection()` 方法会返回一个 `Section` 对象，这个区块（对象）就对应了此 INI 文件中的无标题区块。如要编辑直接使用 `Section` 类提供的相关方法即可。

### 处理非标准内容

INI 文件本质上是一个按行分割的文本内容，有时读取时可能会遇到*非标准内容*的行，既不属于注释、也不属于键值对。例如：

```ini
[demo1]
Line 1 ...
key1=123
Line 2 Content
key2=456
[demo2]
key3=789
```

其中的 `Line 1 ...` 和 `Line 2 Content` 两行都是非标准内容。*vanilla-sugar-ini* 对这种情况也做了兼容。

对于不处于区块开头的非标准内容会被视为换行的情况，读取后自动与上一行的末尾合并。例如 `Line 2 Content` 会被视为上一行的一部分，即键值对 `key1` 的值为 `"123\nLine 2 Content"`。

对于处于区块开头的非标准内容（例如 `Line 1 ...`），在解析时由 `IniDeserializer` 类的 `setDanglingTextOption(DanglingTextOptions)` 方法决定如何处理。默认的行为是 `KEEP`（保留），这一行数据也会被存入 `Section` 对象中，通过 `getDanglingText()` 方法可以获取到。

例如，对于示例的 INI 文件内容，解析后 `Ini` 对象示例如下：

```java
Ini ini; // 解析示例后生成的 Ini 对象
Section demo1 = ini.get("demo1");
demo1.get("key1");       // 返回 "123\nLine 2 Content"
demo1.get("key2");       // 返回 "456"
demo1.getDanglingText(); // 返回 "Line 1 ..."
Section demo2 = ini.get("demo2");
demo2.get("key3");       // 返回 "789"
demo2.getDanglingText(); // 返回 null
```

## 额外说明

考虑到 INI 文件的使用场景，*vanilla-sugar-ini* 做了一些针对性处理/优化，以下一些使用时可能要留意的地方：

1. INI 的内容（键值对以及注释）的顺序可能也是具有作用的，故 `Ini` 和 `Section` 对象都会维护插入顺序

2. 所有的类型都是非线程安全的，如果需要跨线程操作同一个对象，请务必要加上读写锁
