package xyz.udw.sugar.ini;

/**
 * {@link Ini} 的链式访问器
 */
public class ChainIniAccessor {

    private final Ini ini;

    ChainIniAccessor(Ini ini) {
        this.ini = ini;
    }

    /**
     * 将 Ini 中指定区块的名字更改为新区块名，如果区块名不存在则什么也不做。
     *
     * @param sectionName    区块名
     * @param newSectionName 新的区块名
     * @return Ini 链式访问器
     */
    public ChainIniAccessor renameSection(String sectionName, String newSectionName) {
        ini.rename(sectionName, newSectionName);
        return this;
    }

    /**
     * 移除 Ini 中指定的区块。
     *
     * @param sectionName 区块名
     * @return Ini 链式访问器
     */
    public ChainIniAccessor removeSection(String sectionName) {
        ini.remove(sectionName);
        return this;
    }

    /**
     * 打开并操作 INI 中的指定区块，如果区块不存在则会自动添加。
     *
     * @param sectionName 区块名
     * @return 区块的链式访问器
     */
    public ChainSectionAccessor openSection(String sectionName) {
        return new ChainSectionAccessor(this, ini.getOrAdd(sectionName));
    }

    /**
     * 打开并操作 INI 中的无标题区块。
     *
     * @return 区块的链式访问器
     */
    public ChainSectionAccessor openUntitledSection() {
        return new ChainSectionAccessor(this, ini.getUntitledSection());
    }

}
