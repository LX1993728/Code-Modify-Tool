package code.modify.tool.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ReplaceUtil {
    /**
     *
     * @param filePath  文件所在的路径
     * @param regex  文件文本中查找匹配的字符串或正则
     * @param replacement  要替换成的内容
     * @throws IOException
     */
    public static void replaceTextInFile(String filePath,
                                         String regex,
                                         String replacement) throws IOException {
        Path path = Paths.get(filePath);
        final Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8);
        final List<String> replacedLines = lines.map(line -> {
            line = line.replaceAll(regex, replacement);
            log.debug(line);
            return line;
        }).collect(Collectors.toList());
        Files.write(path, replacedLines, StandardCharsets.UTF_8);
        lines.close();
    }

    public static void main(String[] args){
        String filePath = "poms2/group.xml";
        String regex = "8066";
        String replacement = "8067";

        try {
            replaceTextInFile(filePath, regex, replacement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
