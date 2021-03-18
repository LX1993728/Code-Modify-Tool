package code.modify.tool.tests;

import code.modify.tool.utils.embedmaven.MavenCliBuilder;
import com.alibaba.dcm.DnsCacheManipulator;

import java.io.File;

public class TestEmbedMvn {
    static {
        DnsCacheManipulator.loadDnsCacheConfig();
    }
    public static void main(String[] args){
        String localPath = "C:\\Users\\liuxun\\Desktop\\runtime\\test\\front";
        // new MavenCliBuilder(new File(localPath),  "clean", "package").build();
        // new MavenCliBuilder(new File(localPath),  "clean", "compile").buildWithConsole();
         new MavenCliBuilder(new File(localPath),  "-v").buildWithConsole();
    }
}
