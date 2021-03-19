package code.modify.tool.domains;

import lombok.Getter;

/**
 * maven  命令构造器
 */
public class MvnBuilder {
    // 执行maven命令的路径
    @Getter
    private String path;
    // 在指定路径下要执行的mvn命令,(注意不包含mvn)
    @Getter
    private String[] commands;

    public MvnBuilder(String  path, String ... commands){
        this.path = path;
        this.commands = commands;
    }

    // front 项目的路径
    @Getter
    public static String frontPath;
    // front对应的打包命令
    @Getter
    public static String[] frontCommands;
    static {
        frontPath =  GlobalConfig.getRenewFrontSpace();
        String profile = GlobalConfig.getProfile();
        frontCommands = new String[]{"clean", "package", "-P", profile};
    }
}
