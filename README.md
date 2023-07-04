Vanilla Sugar Toolkit
=====================
*A light-weight simplification toolkit for Java*

## 简介

Vanilla Sugar Toolkit 是一个轻量级的 Java 工具类包。

工具包的出发点是将一些 Java 原生 API 未提供（但又很常用）的功能封装为工具类，从而简化编码。

工具包在设计上借鉴了一些其他语言的 API
和语法，封装了针对不同类型（如列表、字符串、基本/包装类型等）的相似操作（例如判空等），实现其他语言中语法糖提供的部分功能，从而化简开发中需额外编写、重复且繁琐的代码。

例如:

- `Check.notEmpty` 支持对数组、列表、字符串等多种对象判空
- `Convert.toInt` 和 `Convert.toLong` 等方法支持根据对象的实际类型自动转为数字
- `Check.isTrue` 提供了类似 JavaScript 中 `if (...)` 语法自动根据类型转为布尔值的效果
- `Indexer` 类的方法提供了类似 Python/C# 中索引器的功能，能获取基本类型数组、对象数组、列表等对象中的元素，支持负数索引
- `Slice` 类提供了 Python 中的切片操作
- ...

### 代码示例

如果需要更为详细的内容，或想快速了解核心方法，可参考：[核心用法介绍](README-CORE-USAGE.md)

以下代码简单展示了一些用法：

```java
public class Example {
    public static void main(String[] args) {
        long[] array = new long[]{1L, 2L, 3L};
        // 判断数组不为空
        System.out.println(array != null && array.length > 0); // true, 原生写法
        System.out.println(Check.notEmpty(array));             // true, 使用工具类

        String s = "123";
        // 判断字符串不为空值
        System.out.println(s != null && !s.isEmpty()); // true, 原生写法
        System.out.println(Check.notEmpty(s));         // true, 使用工具类
        // 获取字符串最后一个字符
        s.charAt(s.length() - 1); // '3', 原生写法
        Indexer.last(s);          // '3', 使用工具类

        List list = Arrays.asList(11, 22, 33);
        // 判断列表不为空
        System.out.println(list != null && list.size() > 0); // true, 原生写法
        System.out.println(Check.notEmpty(list));            // true, 使用工具类
        // 获取列表首个元素
        list.get(0);         // 11, 原生写法
        Indexer.first(list); // 11, 使用工具类
        // 获取列表倒数第二个元素
        list.get(list.size() - 2); // 22, 原生写法
        Indexer.at(list, -2);      // 22, 使用工具类
    }
}
```

## 信息

### 最低支持的 Java 版本

JDK 8

### License

MIT License

### 构建工具

Maven

## 引入工具包帮助

1. 使用 maven 引入

    1. `git clone` 项目到本地
        ```sh
        git clone https://github.com/Snowlt/vanilla-sugar.git
        ```

    2. 安装到本地 maven 仓库

       在终端里切换到项目源码所在目录，使用 maven 执行安装:
        ```sh
        cd vanilla-sugar-toolkit
        mvn install -Dmaven.test.skip=true
        ```
    3. 引入依赖

       在其他项目的 `pom.xml` 文件中引入依赖即可
        ```xml
        <dependencies>
            <dependency>
                <groupId>xyz.udw</groupId>
                <artifactId>vanilla-sugar-toolkit</artifactId>
                <version>1.1</version>
            </dependency>
        </dependencies>
        ```

2. 拷贝

   如果只想单独使用某几个类，也可以直接将源文件复制到其他项目中使用。工具类只使用了 Java 原生
   API，不依赖第三方包问题。

## 起因

这个项目来源于很早以前参考其他语言 API 写的一些工具类代码。因为比较零散后来决定重新设计、整合为一个轻量的工具集合。

现在已有了很多成熟、全面的工具框架，如 `Apache Commons Lang` 等，并没有必要从头造轮子来替代它们。

这个项目作为一个轻量级工具包，希望能以另一种形式提供一点帮助：

1. 简化书写，替代语法糖

   例如对于常用类型进行判空，只需要用 `Check.isEmpty()`
   方法即可。不用区分: `ArrayUtils.isEmpty()`, `CollectionUtils.isEmpty()`, `StringUtils.isEmpty()` 等。

2. 补充少数其他语言中内置的实用功能

   例如工具包增加了 `Slice` 切片、支持逆向的 `Indexer` 索引器功能。

3. 保持轻量

   引入的工具类尽量小巧，只补充： Java 中非常常用的功能、其他语言中常见而 Java（旧版本）未原生提供的功能。
