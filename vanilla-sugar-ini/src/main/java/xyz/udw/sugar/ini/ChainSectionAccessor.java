package xyz.udw.sugar.ini;

/**
 * {@link Section} 的链式访问器
 */
public class ChainSectionAccessor {
    private final ChainIniAccessor accessor;
    private final Section section;

    ChainSectionAccessor(ChainIniAccessor accessor, Section section) {
        this.accessor = accessor;
        this.section = section;
    }

    /**
     * 向区块中添加新项，如果区块中已有相同键名（Key）的项则会覆盖。
     *
     * @param key   键名
     * @param value 值
     * @return 区块的链式访问器
     */
    public ChainSectionAccessor set(String key, String value) {
        section.set(key, value);
        return this;
    }

    /**
     * 将区块中指定项的键名（Key）更改为新键名，如果键名（Key）不存在则什么也不做。
     *
     * @param key    键名
     * @param newKey 新的键名
     * @return 区块的链式访问器
     */
    public ChainSectionAccessor rename(String key, String newKey) {
        section.rename(key, newKey);
        return this;
    }

    /**
     * 移除区块中指定的项。
     *
     * @param key 键名
     * @return 区块的链式访问器
     */
    public ChainSectionAccessor remove(String key) {
        section.remove(key);
        return this;
    }

    /**
     * 在区块末尾添加一条注释。
     *
     * @param comment 注释
     * @return 区块的链式访问器
     */
    public ChainSectionAccessor addComment(String comment) {
        section.addComments(comment);
        return this;
    }

    /**
     * 结束操作当前区块，并返回到上级 {@link Ini}。
     *
     * @return INI 的链式访问器
     */
    public ChainIniAccessor closeSection() {
        return accessor;
    }
}
