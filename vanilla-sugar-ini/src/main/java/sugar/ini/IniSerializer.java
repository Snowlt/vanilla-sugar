package sugar.ini;

import sugar.ini.exception.ReadWriteException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * 导出 INI 为文本到文件或流中
 */
public class IniSerializer {
    private String commentPrefix = ";";
    private String lineSeparator = System.lineSeparator();
    private boolean addSpaceAroundEqualizer = false;
    private boolean addSpaceBeforeComment = false;

    /**
     * 获取注释的前缀符号（默认 {@code ";"}）
     *
     * @return 注释的前缀符号
     */
    public String getCommentPrefix() {
        return commentPrefix;
    }

    /**
     * 设置注释的前缀符号
     *
     * @param commentPrefix 注释的前缀符号
     * @return 当前对象（便于链式调用）
     */
    public IniSerializer setCommentPrefix(String commentPrefix) {
        this.commentPrefix = Objects.requireNonNull(commentPrefix);
        return this;
    }

    /**
     * 获取输出时使用的换行符（默认 {@link System#lineSeparator()}）
     *
     * @return 换行符
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * 设置输出时使用的换行符
     *
     * @param lineSeparator 换行符
     * @return 当前对象（便于链式调用）
     */
    public IniSerializer setLineSeparator(String lineSeparator) {
        this.lineSeparator = Objects.requireNonNull(lineSeparator);
        return this;
    }

    /**
     * 获取是否在项（键值对）的等号两侧插入空格（默认 {@code false}）
     *
     * @return 值
     */
    public boolean isAddSpaceAroundEqualizer() {
        return addSpaceAroundEqualizer;
    }

    /**
     * 设置是否在项（键值对）的等号两侧插入空格
     *
     * @param addSpaceAroundEqualizer 设置为 true 时添加空格，false 不添加
     * @return 当前对象（便于链式调用）
     */
    public IniSerializer setAddSpaceAroundEqualizer(boolean addSpaceAroundEqualizer) {
        this.addSpaceAroundEqualizer = addSpaceAroundEqualizer;
        return this;
    }

    /**
     * 获取是否在注释前（{@link #getCommentPrefix()} 后）插入空格（默认 {@code false}）
     *
     * @return 值
     */
    public boolean isAddSpaceBeforeComment() {
        return addSpaceBeforeComment;
    }

    /**
     * 设置是否在注释前（{@link #getCommentPrefix()} 后）插入空格
     *
     * @param addSpaceBeforeComment 设置为 true 时添加空格，false 不添加
     * @return 当前对象（便于链式调用）
     */
    public IniSerializer setAddSpaceBeforeComment(boolean addSpaceBeforeComment) {
        this.addSpaceBeforeComment = addSpaceBeforeComment;
        return this;
    }

    /**
     * 将 INI 内容写出到 {@link Writer} 中
     *
     * @param ini     要导出的 INI 对象
     * @param writer  输出流
     * @throws NullPointerException 如果 {@code ini} / {@code writer} 中含有 {@code null}
     * @throws ReadWriteException 如果读取时发生IO异常
     */
    public void write(Ini ini, Writer writer) {
        try {
            Section untitledSection = ini.getUntitledSection();
            if (untitledSection != null) {
                writeSection(untitledSection, writer);
            }
            for (Ini.IniEntry entry : ini) {
                writer.write('[');
                writer.write(entry.getKey());
                writer.write(']');
                writer.write(lineSeparator);
                writeSection(entry.getValue(), writer);
            }
            writer.flush();
        } catch (IOException e) {
            throw new ReadWriteException("Error when serializing content", e);
        }
    }

    /**
     * 将 INI 内容写出到 {@link OutputStream} 中
     *
     * @param ini     要导出的 INI 对象
     * @param stream  输出流
     * @param charset 字符编码 {@link Charset}
     * @throws NullPointerException 如果 {@code ini} / {@code stream} / {@code charset} 中含有 {@code null}
     * @throws ReadWriteException 如果读取时发生IO异常
     */
    public void write(Ini ini, OutputStream stream, Charset charset) {
        Objects.requireNonNull(ini);
        Objects.requireNonNull(stream);
        write(ini, new OutputStreamWriter(stream, charset));
        // OutputStreamWriter is a wrapper, no need to close
    }

    private void writeSection(Section section, Writer writer) throws IOException {
        if (section.getDanglingText() != null) {
            writer.write(section.getDanglingText());
            writer.write(lineSeparator);
        }
        StringBuilder prefixBuilder = new StringBuilder(commentPrefix);
        if (addSpaceBeforeComment) prefixBuilder.append(' ');
        String combinedPrefix = prefixBuilder.toString();
        String equalizer = addSpaceAroundEqualizer ? " = " : "=";
        section.forEachKeysAndComments((key, value, comment) -> {
            if (key != null) {
                writer.write(key);
                writer.write(equalizer);
                writer.write(value);
            } else {
                writer.write(combinedPrefix);
                writer.write(comment);
            }
            writer.write(lineSeparator);
        });
    }
}
