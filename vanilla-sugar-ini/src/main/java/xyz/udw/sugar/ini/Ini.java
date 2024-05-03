package xyz.udw.sugar.ini;

import xyz.udw.sugar.ini.exception.AccessValueException;

import java.util.*;

/**
 * 表示一个 INI 对象
 */
public class Ini implements Iterable<Ini.IniEntry> {
    private final Map<String, Section> sections = new LinkedHashMap<>();
    private Section defaultSection = new Section();

    /**
     * 获取此 INI 中的某一个区块。
     * <p>如果区块名不存在，则返回 {@code null}。</p>
     *
     * @param name 区块名
     * @return 区块
     * @throws NullPointerException 当 {@code name} 为 {@code null}
     */
    public Section get(String name) {
        return sections.get(Objects.requireNonNull(name));
    }

    /**
     * 获取无标题区块（处于文件头部，没有指定区块名的键值对和数据会被存在这里)。
     *
     * @return 无标题区块
     */
    public Section getUntitledSection() {
        return defaultSection;
    }

    /**
     * 根据区块名获取区块。如果指定的区块不存在，则先创建再返回区块。
     *
     * @param name 区块名
     * @return 区块
     * @throws NullPointerException 当 {@code name} 为 {@code null}
     */
    public Section getOrAdd(String name) {
        return sections.computeIfAbsent(Objects.requireNonNull(name), k->new Section());
    }

    /**
     * 获取区块的总数（不含 {@link #getUntitledSection()}）。
     *
     * @return 区块数量
     */
    public int count() {
        return sections.size();
    }

    /**
     * 构造一个 INI 对象。
     */
    public Ini() {}

    /**
     * 检测此 INI 中是否包含某区块。
     *
     * @param name 区块名
     * @return 包含时返回 true, 不包含返回 false
     * @throws NullPointerException 当 {@code name} 为 {@code null}
     */
    public boolean contains(String name) {
        return sections.containsKey(Objects.requireNonNull(name));
    }

    /**
     * 移除某一个区块。
     *
     * @param name 区块名
     * @return 成功移除返回 true, 否则返回 false
     * @throws NullPointerException 当 {@code name} 为 {@code null}
     */
    public boolean remove(String name) {
        return sections.remove(Objects.requireNonNull(name)) != null;
    }

    /**
     * 将指定区块的名字更改为新区块名，如果区块名不存在则什么也不做。
     *
     * @param name 区块名
     * @param newName 新的区块名
     * @return 成功修改返回 true, 否则返回 false
     * @throws NullPointerException 当 {@code name} 或 {@code newName} 为 {@code null}
     */
    public boolean rename(String name, String newName) {
        Objects.requireNonNull(newName);
        if (name.equals(newName)) return false;
        Section section = sections.remove(name);
        if (section != null) {
            sections.put(newName, section);
            return true;
        }
        return false;
    }

    /**
     * 创建一个当前 INI 对象的副本（深拷贝）。
     *
     * @return 当前对象的副本
     */
    public Ini deepClone() {
        Ini ini = new Ini();
        ini.defaultSection = this.defaultSection.deepClone();
        for (Map.Entry<String, Section> kv : this.sections.entrySet()) {
            ini.sections.put(kv.getKey(), kv.getValue().deepClone());
        }
        return ini;
    }

    /**
     * 清空当前 INI 对象的所有内容，包含默认区块。
     */
    public void clear() {
        clear(true);
    }

    /**
     * 清空当前 INI 对象的所有内容。
     *
     * @param includingUntitled 是否包含 {@link #getUntitledSection()}。传入 true 会清理，false 反之。
     */
    public void clear(boolean includingUntitled) {
        if (includingUntitled) {
            defaultSection.clear();
        }
        sections.forEach((k, v) -> v.clear());
        sections.clear();
    }

    /**
     * 获取所有的区块名。
     * <p>区块名按添加顺序排列。</p>
     *
     * @return 所有的区块名
     */
    public List<String> getSectionNames() {
        return new ArrayList<>(sections.keySet());
    }

    /**
     * 以链式访问操作此 INI 对象
     * @return 链式访问器
     */
    public ChainIniAccessor chainAccess() {
        return new ChainIniAccessor(this);
    }

    // region Quick Access

    /**
     *获取此 INI 中指定项（键值对）中的值。
     * <p>如果区块和键名都存在则返回对应的值，否则返回 {@code null} 替代。</p>
     * @param name 区块名
     * @param key  键名
     * @return 值或 {@code null}
     * @throws NullPointerException 当 {@code name} 或 {@code key} 含有 {@code null}
     */
    public String getItemValue(String name, String key) {
        return getItemValue(name, key, null);
    }

    /**
     *获取此 INI 中指定项（键值对）中的值。
     * <p>如果区块和键名都存在则返回对应的值，否则返回 {@code def} 替代。</p>
     *
     * @param name 区块名
     * @param key  键名
     * @param def  当区块名或键名不存在时的替代返回值
     * @return 值或替代值
     * @throws NullPointerException 当 {@code name} 或 {@code key} 含有 {@code null}
     */
    public String getItemValue(String name, String key, String def) {
        return Optional.ofNullable(get(name)).map(sec -> sec.get(key)).orElse(def);
    }

    /**
     * 向 INI 中添加新项（键值对），如果区块中已有相同键名（Key）的项则会覆盖。
     * <p>如果指定的区块不存在，则先创建再区块中写入。</p>
     *
     * @param name 区块名
     * @param key   键名
     * @param value 值
     * @see Section#set(String, Object)
     * @throws NullPointerException 当 {@code name}，{@code key} 或 {@code value} 含有 {@code null}
     */
    public void setItemValue(String name, String key, String value) {
        getOrAdd(name).set(key, value);
    }

    /**
     *获取此 INI 中指定项（键值对）中的值，并转为 int 返回。
     * @param name 区块名
     * @param key  键名
     * @return 值
     * @throws NullPointerException 当 {@code name} 或 {@code key} 为 {@code null}
     * @throws AccessValueException 当区块名/键（Key）不存在，或值无法转换为 {@code int}
     *
     */
    public int getItemValueAsInt(String name, String key) {
        Section section = get(name);
        if (section == null) throw new AccessValueException("Section " + name + " not found");
        return section.getAsInt(key);
    }

    /**
     * 获取此 INI 中指定项（键值对）中的值，并转为 boolean 返回。
     *
     * @param name 区块名
     * @param key  键名
     * @return 值
     * @throws NullPointerException 当 {@code name} 或 {@code key} 为 {@code null}
     * @throws AccessValueException 当区块名/键（Key）不存在，或值无法转换为 {@code int}
     */
    public boolean getItemValueAsBool(String name, String key) {
        Section section = get(name);
        if (section == null) throw new AccessValueException("Section " + name + " not found");
        return section.getAsBool(key);
    }

    /**
     * 检测此 INI 中是否包含某区块，且区块中包含某项（键值对）。
     *
     * @param name 区块名
     * @param key  键名
     * @return 存在则返回 {@code true} 否则返回 {@code false}
     * @throws NullPointerException 当 {@code name} 或 {@code key} 为 {@code null}
     */
    public boolean containsItemValue(String name, String key) {
        Section section = get(name);
        return section != null && section.contains(key);
    }

    // endregion Quick Access

    @Override
    public Iterator<IniEntry> iterator() {
        return new Itr(sections.entrySet().iterator());
    }

    private static class Itr implements Iterator<IniEntry> {
        private final Iterator<Map.Entry<String, Section>> iterator;

        Itr(Iterator<Map.Entry<String,Section>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public IniEntry next() {
            return new IniEntry(iterator.next());
        }
    }

    public static class IniEntry implements Map.Entry<String, Section> {
        private final Map.Entry<String, Section> entry;

        IniEntry(Map.Entry<String, Section> entry) {
            this.entry = entry;
        }

        @Override
        public String getKey() {
            return entry.getKey();
        }

        @Override
        public Section getValue() {
            return entry.getValue();
        }

        @Override
        public Section setValue(Section value) {
            throw new UnsupportedOperationException();
        }
    }
}
