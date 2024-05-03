package xyz.udw.sugar.ini;


import xyz.udw.sugar.ini.exception.ReadWriteException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * INI 文件读写方法入口类
 * <p>提供简化的静态方法，用于从文件读取出 INI，或导出 INI 到文件中</p>
 */
public class IniReaderWriter {

    private static final IniSerializer FILE_WRITER = new IniSerializer();
    private static final IniSerializer PRETTY_FILE_WRITER = new IniSerializer()
            .setAddSpaceAroundEqualizer(true).setAddSpaceBeforeComment(true);
    private static final IniDeserializer FILE_READER = new IniDeserializer();

    public static Ini loadFromFile(String path) {
        return loadFromFile(path, StandardCharsets.UTF_8);
    }

    public static Ini loadFromFile(String path, Charset charset) {
        try (FileInputStream stream = new FileInputStream(path)){
            return FILE_READER.read(stream, charset);
        } catch (IOException e) {
            throw new ReadWriteException("Exception occurred while loading file", e);
        }
    }

    public static void saveToFile(Ini ini, String path) {
        saveToFile(ini,path,StandardCharsets.UTF_8);
    }

    public static void saveToFile(Ini ini, String path, Charset charset) {
        try (FileOutputStream stream = new FileOutputStream(path)) {
            FILE_WRITER.write(ini, stream, charset);
        } catch (IOException e) {
            throw new ReadWriteException("Exception occurred while saving file", e);
        }
    }

    public static void saveToPrettyFile(Ini ini, String path) {
        saveToPrettyFile(ini,path,StandardCharsets.UTF_8);
    }

    public static void saveToPrettyFile(Ini ini, String path, Charset charset) {
        try (FileOutputStream stream = new FileOutputStream(path)) {
            PRETTY_FILE_WRITER.write(ini, stream, charset);
        } catch (IOException e) {
            throw new ReadWriteException("Exception occurred while saving file", e);
        }
    }

    private IniReaderWriter() {
    }
}