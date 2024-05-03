package xyz.udw.sugar.ini;


import xyz.udw.sugar.ini.exception.AccessValueException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 INI 对象内部的一个区块
 */
public class Section implements Iterable<Map.Entry<String, String>> {

    private final Map<String, String> items = new HashMap<>();
    private final List<Node> nodes = new ArrayList<>();

    /**
     * 处于区块顶部，但不含注释前缀的文本
     */
    private String danglingText = null;

    /**
     * 处于区块顶部的注释
     */
    private List<String> topComments = null;

    Section() {
    }

    /**
     * 获取此区块中指定项（键值对）中的值。
     * <p>如果键名（Key）不存在，则返回 {@code null}。</p>
     *
     * @param key 键名
     * @return 值
     * @throws NullPointerException 当 {@code key} 为 {@code null}
     */
    public String get(String key) {
        return items.get(Objects.requireNonNull(key));
    }

    /**
     * 向区块中添加新项（键值对），如果区块中已有相同键名（Key）的项则会覆盖。
     * <p>传入的值会自动调用 {@link Object#toString()} 转为 {@link String} 类型。</p>
     *
     * @param key   键名
     * @param value 值
     * @throws NullPointerException 当 {@code key} 或 {@code value} 为 {@code null}
     */
    public void set(String key, Object value) {
        if (items.put(Objects.requireNonNull(key), value.toString()) == null) {
            nodes.add(new Node(key));
        }
    }

    /**
     * 获取此区块内项（键值对）的总数。
     *
     * @return 项的数量
     */
    public int count() {
        return items.size();
    }

    /**
     * 获取此区块内项（键值对）以及注释的总数。
     *
     * @return 项的数量
     */
    public int countKeyAndComments() {
        return countList(topComments) + nodes.stream()
                .mapToInt(node -> 1 + countList(node.getComments())).sum();
    }

    /**
     * 获取此区块中指定项（键值对）的值，当键（Key）不存在时返回 {@code def} 替代。
     *
     * @param key 键名
     * @param def 当键名不存在时的替代返回值
     * @return 值或替代值
     * @throws NullPointerException 当 {@code key} 为 {@code null}
     */
    public String get(String key, String def) {
        String value = get(key);
        return value != null ? value : def;
    }

    /**
     * 获取此区块中指定项（键值对）的值，并转为 {@code int} 返回。
     *
     * @param key 键名
     * @return 值
     * @throws NullPointerException 当 {@code key} 为 {@code null}
     * @throws AccessValueException 当键（Key）不存在，或值无法转换为 {@code int}
     */
    public int getAsInt(String key) {
        String value = items.get(Objects.requireNonNull(key));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new AccessValueException("Unable parse value to int for key \"" + key + "\"");
        }
    }

    /**
     * 获取此区块中指定项（键值对）的值，并转为 {@code long} 返回。
     *
     * @param key 键名
     * @return 值
     * @throws NullPointerException 当 {@code key} 为 {@code null}
     * @throws AccessValueException 当键（Key）不存在，或值无法转换为 {@code long}
     */
    public long getAsLong(String key) {
        String value = items.get(Objects.requireNonNull(key));
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new AccessValueException("Unable parse value to long for key \"" + key + "\"");
        }
    }

    /**
     * 获取此区块中指定项（键值对）的值，并转为 {@code boolean} 返回。
     *
     * @param key 键名
     * @return 值
     * @throws NullPointerException 当 {@code key} 为 {@code null}
     * @see Boolean#parseBoolean(String) 转换方法
     */
    public boolean getAsBool(String key) {
        return Boolean.parseBoolean(items.get(Objects.requireNonNull(key)));
    }


    /**
     * 检测此区块中是否包含某项（键值对）。
     *
     * @param key 键名
     * @return 包含时返回 true, 不包含返回 false
     * @throws NullPointerException 当 {@code key} 为 {@code null}
     */
    public boolean contains(String key) {
        return items.containsKey(Objects.requireNonNull(key));
    }

    /**
     * 移除指定项（键值对）。
     *
     * @param key 键名
     * @return 成功移除返回 True, 否则返回 False
     * @throws NullPointerException 当 {@code key} 为 {@code null}
     */
    public boolean remove(String key) {
        if (items.remove(Objects.requireNonNull(key)) == null) {
            return false;
        }
        int i = findKeyIndex(key);
        Node node = nodes.remove(i);
        if (node == null) return false;
        if (i == 0) {
            appendTopComments(node.getComments());
        } else {
            nodes.get(i - 1).appendComments(node.getComments());
        }
        return true;
    }

    /**
     * 将指定项的键名（Key）更改为新键名，如果键名（Key）不存在则什么也不做。
     *
     * @param key    键名
     * @param newKey 新的键名
     * @return 成功修改返回 true, 否则返回 false
     * @throws NullPointerException 当 {@code key} 或 {@code newKey} 为 {@code null}
     */
    public boolean rename(String key, String newKey) {
        Objects.requireNonNull(newKey);
        if (key.equals(newKey)) return false;
        String value = items.remove(key);
        if (value != null) {
            items.put(newKey, value);
            for (Node node : nodes) {
                if (node.getKey().equals(key)) {
                    node.setKey(newKey);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 清空当前区块的所有内容。
     */
    public void clear() {
        items.clear();
        nodes.clear();
        topComments = null;
        danglingText = null;
    }

    /**
     * 获取所有项的键名（Key）。
     * <p>键名（Key）按添加顺序排列。</p>
     *
     * @return 所有的键
     */
    public List<String> getKeys() {
        return nodes.stream().map(Node::getKey).collect(Collectors.toList());
    }

    // region Comment Operation

    /**
     * 添加注释。
     *
     * @param contents 注释的内容
     */
    public void addComments(String... contents) {
        if (contents == null) return;
        addComments(Arrays.asList(contents));
    }

    /**
     * 添加注释。
     *
     * @param contents 注释的内容
     */
    public void addComments(Collection<String> contents) {
        List<String> filtered = filterNonNull(contents);
        if (filtered.isEmpty()) return;
        int size = nodes.size();
        if (size == 0) appendTopComments(filtered);
        else nodes.get(size - 1).appendComments(filtered);
    }

    /**
     * 获取指定项（键值对）到上一项（键值对）之间的全部注释。
     *
     * @param key 键名
     * @throws AccessValueException 当键（Key）不存在或为 {@code null}
     */
    public List<String> getCommentsBefore(String key) {
        int i = findKeyIndex(key);
        if (i == 0) return wrapList(topComments);
        return wrapList(nodes.get(i - 1).getComments());
    }

    /**
     * 在指定项（键值对）到上一项（键值对）之间添加注释，如果已有注释则合并到末尾。
     *
     * @param key      键名
     * @param contents 注释的内容
     * @throws AccessValueException 当键（Key）不存在或为 {@code null}
     */
    public void addCommentsBefore(String key, Collection<String> contents) {
        int i = findKeyIndex(key);
        if (i == 0) appendTopComments(contents);
        else nodes.get(i - 1).appendComments(contents);
    }

    /**
     * 移除指定项（键值对）到上一项（键值对）之间的全部注释。
     *
     * @param key 键名
     * @throws AccessValueException 当键（Key）不存在或为 {@code null}
     */
    public void removeCommentsBefore(String key) {
        int i = findKeyIndex(key);
        if (i == 0) topComments = null;
        else nodes.get(i - 1).removeComments();
    }

    /**
     * 获取指定项（键值对）到下一项（键值对）之间的全部注释。
     *
     * @param key 键名
     * @throws AccessValueException 当键（Key）不存在或为 {@code null}
     */
    public List<String> getCommentsAfter(String key) {
        Node node = findKeyNode(key);
        return wrapList(node.getComments());
    }

    /**
     * 在指定项（键值对）到下一项（键值对）之间添加注释，如果已有注释则合并到末尾。
     *
     * @param key      键名
     * @param contents 注释的内容
     * @throws AccessValueException 当键（Key）不存在或为 {@code null}
     */
    public void addCommentsAfter(String key, Collection<String> contents) {
        Node node = findKeyNode(key);
        node.appendComments(contents);
    }

    /**
     * 在指定项（键值对）到下一项（键值对）之间添加注释，如果已有注释则合并到末尾。
     *
     * @param key 键名
     * @throws AccessValueException 当键（Key）不存在或为 {@code null}
     */
    public void removeCommentsAfter(String key) {
        Node node = findKeyNode(key);
        node.removeComments();
    }

    /**
     * 获取所有的注释。
     * <p>注释按添加顺序排列。</p>
     *
     * @return 注释
     */
    public List<String> getComments() {
        List<String> comments = this.topComments;
        Stream<String> top = comments != null ? comments.stream() : Stream.empty();
        Stream<String> inNode = nodes.stream().map(Node::getComments).filter(Objects::nonNull)
                .flatMap(List::stream);
        return Stream.concat(top, inNode).collect(Collectors.toList());
    }

    /**
     * 移除所有的注释。
     */
    public void removeComments() {
        this.topComments = null;
        nodes.forEach(Node::removeComments);
    }

    /**
     * 获取区块的顶部文本（非注释文本）。
     *
     * @return 顶部文本。如果顶部文本不存在则返回 {@code null}
     */
    public String getDanglingText() {
        return danglingText;
    }

    /**
     * 设置区块的顶部文本（非注释文本）。
     * <p>由于顶部文本不遵守注释的格式，写入 INI 文件后可能会导致兼容性问题，请谨慎使用此功能。</p>
     *
     * @param danglingText 要设置的顶部文本。或者传入 {@code null} 清空顶部文本
     */
    public void setDanglingText(String danglingText) {
        this.danglingText = danglingText;
    }

    // endregion Comment Operation

    // region Collection Conversion

    /**
     * 将区块中项（键值对）生成为一个新 {@link Map}。
     *
     * @return 区块中的项
     */
    public Map<String, String> toMap() {
        return new HashMap<>(items);
    }

    /**
     * 返回一个可访问此区块内键值对的迭代器
     *
     * @return 迭代器
     */
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return Collections.unmodifiableMap(items).entrySet().iterator();
    }

    // endregion Collection Conversion

    // region Inner Access

    /**
     * 创建一个当前 Section 对象的副本（深拷贝）。
     *
     * @return 当前对象的副本
     */
    Section deepClone() {
        Section section = new Section();
        section.items.putAll(this.items);
        section.appendTopComments(this.topComments);
        section.danglingText = this.danglingText;
        for (Node n : this.nodes) {
            Node nn = new Node(n.getKey());
            nn.appendComments(n.getComments());
            section.nodes.add(nn);
        }
        return section;
    }

    <E extends Exception> void forEachKeysAndComments(ContentConsumer<E> consumer) throws E {
        if (topComments != null && !topComments.isEmpty()) {
            for (String comment : topComments) {
                consumer.accept(null, null, comment);
            }
        }
        for (Node node : nodes) {
            String key = node.getKey();
            String value = items.get(key);
            List<String> comments = node.getComments();
            boolean emptyComments = comments == null || comments.isEmpty();
            consumer.accept(key, value != null ? value : "", null);
            if (!emptyComments) {
                for (String comment : comments) {
                    consumer.accept(null, null, comment);
                }
            }
        }
    }

    interface ContentConsumer<E extends Exception> {
        void accept(String key, String value, String comment) throws E;
    }

    // endregion Inner Access

    /**
     * Find index of key.
     *
     * @throws AccessValueException if key not found
     */
    private int findKeyIndex(String key) {
        if (!items.containsKey(key)) throwInvalidKey(key);
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node.getKey().equals(key)) return i;
        }
        throwInvalidKey(key);
        throw new AccessValueException("");
    }

    /**
     * Return an iterator stopped at specified key position.
     *
     * @throws AccessValueException if key not found
     */
    private Node findKeyNode(String key) {
        return nodes.get(findKeyIndex(key));
    }

    private void appendTopComments(Collection<String> comments) {
        if (comments == null || comments.isEmpty()) return;
        if (topComments == null) topComments = new ArrayList<>(comments);
        else topComments.addAll(comments);
    }

    private static List<String> filterNonNull(Collection<String> contents) {
        if (contents == null || contents.isEmpty()) return Collections.emptyList();
        return contents.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static void throwInvalidKey(String key) {
        throw new AccessValueException("Key \"" + key + "\" not found");
    }

    private static <T> List<T> wrapList(List<T> list) {
        return list == null || list.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    private static <T> int countList(List<T> list) {
        return list == null ? 0 : list.size();
    }

    /**
     * 区块中的数据节点，由键名 + 尾部注释 组成
     */
    private static class Node {
        private String key;
        private List<String> comments;

        public Node(String key) {
            this.key = key;
        }

        private void appendComments(Collection<String> comments) {
            if (comments == null || comments.isEmpty()) return;
            if (this.comments == null) this.comments = new ArrayList<>(comments);
            else this.comments.addAll(comments);
        }

        private void removeComments() {
            if (this.comments != null) this.comments.clear();
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getComments() {
            return comments;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(key).append(" =").append("\n");
            if (comments != null && !comments.isEmpty())
                comments.forEach(s -> builder.append("# ").append(s).append('\n'));
            builder.deleteCharAt(builder.length() - 1);
            return builder.toString();
        }
    }

}
