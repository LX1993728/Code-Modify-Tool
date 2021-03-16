package code.modify.tool.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonSerialUtil {

    /**
     * 从指定的json文件加载到指定的对象
     * @param path
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T getObjectFromFile(String path, Class<T> clazz) throws IOException {
        File file = new File(path);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if (!file.exists()){
            return null;
        }
        final String jsonStr = IOUtils.toString(FileUtils.openInputStream(file), StandardCharsets.UTF_8);
        final T t = JSON.parseObject(jsonStr, clazz);
        return t;
    }

    /**
     *
     * @param path 文件路径
     * @param jsonContent 需要持久化的json字符串内容
     */
    public static void writeJsonStrToFile(String path, String jsonContent) throws IOException {
        File file = new File(path);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if (!file.exists()){
            file.createNewFile();
        }
        FileUtils.writeStringToFile(file, jsonContent, StandardCharsets.UTF_8);
    }

}
