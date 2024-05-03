package xyz.udw.sugar.ini;

import xyz.udw.sugar.ini.exception.ReadWriteException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 解析和读取 INI 文件数据
 */
public class IniDeserializer {

    /**
     * 对于区块顶部文本（非注释文本）的处理方式
     */
    public enum DanglingTextOptions {
        /**
         * 保留原文
         */
        KEEP,
        /**
         * 当作注释处理
         */
        TO_COMMENT,
        /**
         * 丢弃此行
         */
        DROP
    }

    private DanglingTextOptions danglingTextOption = DanglingTextOptions.KEEP;
    private Set<String> commentPrefixes = new HashSet<>(Arrays.asList(";", "#"));
    private boolean trimSectionName = true;
    private boolean trimKey = true;
    private boolean trimValue = true;
    private boolean trimComment = true;

    /**
     * 获取解析 INI 时如何处理区块顶部非注释文本（默认为 {@link DanglingTextOptions#KEEP}）
     *
     * @return 处理方式
     */
    public DanglingTextOptions getDanglingTextOption() {
        return danglingTextOption;
    }

    /**
     * 设置解析 INI 时如何处理区块顶部非注释文本
     *
     * @param danglingTextOption 处理方式
     * @return 当前对象 （便于链式调用）
     */
    public IniDeserializer setDanglingTextOption(DanglingTextOptions danglingTextOption) {
        if (danglingTextOption == null) throw new IllegalArgumentException();
        this.danglingTextOption = Objects.requireNonNull(danglingTextOption);
        return this;
    }

    /**
     * 获取要解析的文件中注释的前缀（默认为 {@code ";", "#"}）
     *
     * @return 注释的前缀
     */
    public Set<String> getCommentPrefixes() {
        return commentPrefixes;
    }

    /**
     * 设置要解析的文件中注释的前缀（可配置多个）
     *
     * @param commentPrefixes 注释前缀
     * @return 当前对象 （便于链式调用）
     */
    public IniDeserializer setCommentPrefixes(Set<String> commentPrefixes) {
        if (commentPrefixes == null || commentPrefixes.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.commentPrefixes = commentPrefixes;
        return this;
    }

    /**
     * 设置要解析的文件中注释的前缀（可配置多个）
     *
     * @param commentPrefixes 注释前缀
     * @return 当前对象 （便于链式调用）
     */
    public IniDeserializer setCommentPrefixes(String... commentPrefixes) {
        if (commentPrefixes == null || commentPrefixes.length == 0) {
            throw new IllegalArgumentException();
        }
        this.commentPrefixes = new HashSet<>(Arrays.asList(commentPrefixes));
        return this;
    }

    /**
     * 获取读取区块标题（区块名）的时候是否去除首尾空白（默认 {@code true}）
     *
     * @return 值
     */
    public boolean isTrimSectionName() {
        return trimSectionName;
    }

    /**
     * 设置读取区块标题（区块名）的时候是否去除首尾空白
     *
     * @param trimSectionName 设置为 true 去除空白，false 保留空白
     * @return 当前对象 （便于链式调用）
     */
    public IniDeserializer setTrimSectionName(boolean trimSectionName) {
        this.trimSectionName = trimSectionName;
        return this;
    }

    /**
     * 获取读取区块中键名的时候是否去除首尾空白（默认 {@code true}）
     *
     * @return 值
     */
    public boolean isTrimKey() {
        return trimKey;
    }

    /**
     * 设置读取区块中键名的时候是否去除首尾空白
     *
     * @param trimKey 设置为 true 去除空白，false 保留空白
     * @return 当前对象 （便于链式调用）
     */
    public IniDeserializer setTrimKey(boolean trimKey) {
        this.trimKey = trimKey;
        return this;
    }

    /**
     * 获取读取区块中值的时候是否去除首尾空白（默认 {@code true}）
     *
     * @return 值
     */
    public boolean isTrimValue() {
        return trimValue;
    }

    /**
     * 设置读取区块中值的时候是否去除首尾空白
     *
     * @param trimValue 设置为 true 去除空白，false 保留空白
     * @return 当前对象 （便于链式调用）
     */
    public IniDeserializer setTrimValue(boolean trimValue) {
        this.trimValue = trimValue;
        return this;
    }

    /**
     * 获取读取注释的时候是否去除首尾空白（默认 {@code true}）
     *
     * @return 值
     */
    public boolean isTrimComment() {
        return trimComment;
    }

    /**
     * 设置读取注释的时候是否去除首尾空白
     *
     * @param trimComment 设置为 true 去除空白，false 保留空白
     * @return 当前对象 （便于链式调用）
     */
    public IniDeserializer setTrimComment(boolean trimComment) {
        this.trimComment = trimComment;
        return this;
    }

    /**
     * 从 {@link Reader} 中读取 INI 内容
     *
     * @param reader  输入流
     * @throws NullPointerException 如果 {@code reader} 为 {@code null}
     * @throws ReadWriteException 如果读取时发生IO异常
     */
    public Ini read(Reader reader) {
        Ini ini = new Ini();
        try (BufferedReader br = new BufferedReader(reader)) {
            loadToIni(br, ini);
        } catch (IOException e) {
            throw new ReadWriteException("Error when deserializing content", e);
        }
        return ini;
    }

    /**
     * 从 {@link Reader} 中读取 INI 内容
     *
     * @param stream  输入流
     * @param charset 字符编码 {@link Charset}
     * @throws NullPointerException 如果 {@code stream} / {@code charset} 中含有 {@code null}
     * @throws ReadWriteException 如果读取时发生IO异常
     */
    public Ini read(InputStream stream, Charset charset) {
        return read(new InputStreamReader(stream, charset));
    }

    private void loadToIni(BufferedReader br, Ini ini) throws IOException {
        String[] prefixes = this.commentPrefixes.toArray(new String[0]);
        Section sec = ini.getUntitledSection();
        // Uses a head node to store dangling text (if exist), and makes adding node easier
        Content head = new Content();
        Content tail = head;
        String line, parsed;
        while ((line = br.readLine()) != null) {
            if ((parsed = parseComment(line, prefixes)) != null) {
                tail.next = Content.ofComment(parsed);
                tail = tail.next;
                continue;
            }
            if ((parsed = parseSectionName(line)) != null) {
                flushIntoSection(sec, head);
                tail = head;
                sec = ini.getOrAdd(parsed);
                continue;
            }
            int eqIdx = line.indexOf('=');
            if (eqIdx != -1) {
                String key = line.substring(0, eqIdx);
                String value = line.substring(eqIdx + 1);
                tail.next = Content.ofKey(key, value);
                tail = tail.next;
                continue;
            }
            tail.values.add(line);
        }
        flushIntoSection(sec, head);
    }

    private void flushIntoSection(Section sec, Content head) {
        if (!head.values.isEmpty()) {
            if (this.danglingTextOption == DanglingTextOptions.KEEP) {
                sec.setDanglingText(head.joinValues());
            }else if(this.danglingTextOption == DanglingTextOptions.TO_COMMENT){
                sec.addComments(Collections.singletonList(head.joinValues()));
            }
        }
        for (Content p = head.next; p != null; ) {
            if (p.key != null) {
                String key = p.key;
                String value = p.joinValues();
                if (this.trimKey) key = key.trim();
                if (this.trimValue) value = value.trim();
                sec.set(key, value);
            } else {
                String comment = p.joinValues();
                if (this.trimComment) comment = comment.trim();
                sec.addComments(Collections.singletonList(comment));
            }
            // Manually clear node can help GC, which like LinkedList
            Content next = p.next;
            p.next = null;
            p.values = null;
            p.key = null;
            p = next;
        }
        // Resets head node
        head.values.clear();
        head.next = null;
    }

    private String parseSectionName(String s) {
        int beg = s.indexOf('[');
        if (beg == -1) return null;
        int end = s.lastIndexOf(']');
        if (end == -1 || end < beg) return null;
        String name = s.substring(beg + 1, end);
        return this.trimSectionName ? name.trim() : name;
    }

    private String parseComment(String s, String[] prefixes) {
        for (String prefix : prefixes) {
            int i = s.indexOf(prefix);
            if (i == 0 || (i != -1 && isBlank(s, i)))
                return s.substring(i + prefix.length());
        }
        return null;
    }

    private static boolean isBlank(String s, int end) {
        for (int i = 0; i < end; i++)
            if (!Character.isWhitespace(s.charAt(i))) return false;
        return true;
    }

    private static class Content {
        private String key;
        private List<String> values = new ArrayList<>(1);
        private Content next;

        String joinValues() {
            return String.join("\n", this.values);
        }

        private static Content ofKey(String key, String value) {
            Content content = new Content();
            content.key = key;
            content.values.add(value);
            return content;
        }

        private static Content ofComment(String comment) {
            Content content = new Content();
            content.values.add(comment);
            return content;
        }
    }

}
