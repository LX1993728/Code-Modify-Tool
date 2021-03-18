package code.modify.tool.domains;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 全局配置类
 */
@Slf4j
public class GlobalConfig {
    // 线上分支server对应的svn地址
    @Getter
    private static String serverSvnUrl = "svn://10.2.6.23/TLSHOW/project/branch/global/server";
    // server中子模块service需要切换的重构分支地址
    @Getter
    private static String serviceRenewSvnUrl = "svn://10.2.6.23/TLSHOW/project/trunk/server_renew_global/web/service";
    // front 对应的gitlab地址
    @Getter
    private static String frontGitUrl = "http://10.128.41.7/java/front.git";
    // front 对应的git branch 默认是master
    @Getter
    private static String frontGitBranch = "dev";

    @Getter
    private static String gitUsername = "liuxun";
    @Getter
    private static String gitPassword = "liuxun1993728";
    @Getter
    private static String svnUsername = "liuxun";
    @Getter
    private static String svnPassword = "liuxun1993728";
    // 主要针对front和service而言 常见有三个值 tl_trunk/tl_lm/tl_global
    @Getter
    private static String profile = "tl_trunk";

    // 构建工程的根目录
    @Getter
    private static String  buildWorkSpace = "C:\\Users\\liuxun\\Desktop\\runtime\\test\\build";
    // embedded-maven 命令使用的本地仓库地址
    @Getter
    private static String repositoryPath = null;

    static {
        // 初始化项目构建的根目录
        File buildDir = new File(buildWorkSpace);
        if (!buildDir.exists()){
            log.info("构建根目录不存在, 创建中...");
            buildDir.mkdirs();
        }
        try {
            loadConfProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载仓库与构建目录相关的配置文件 默认文件名是config.properties
    private static void loadConfProperties() throws IOException {
        sepLine("开始加载全局配置", false);
        String confLocation = "config/config.properties";
        File configFile = new File(confLocation);
        if (!configFile.exists()){
            log.info("config.properties not exists, use default config values");
            return;
        }
        Properties props = new Properties();
        props.load(new FileInputStream(configFile));
        serverSvnUrl = props.getProperty("serverSvnUrl", serverSvnUrl);
        serviceRenewSvnUrl = props.getProperty("serviceRenewSvnUrl", serviceRenewSvnUrl);
        frontGitUrl =  props.getProperty("frontGitUrl", frontGitUrl);
        gitUsername =  props.getProperty("gitUsername", gitUsername);
        gitPassword = props.getProperty("gitPassword", gitPassword);
        svnUsername =  props.getProperty("svnUsername", svnUsername);
        svnPassword =  props.getProperty("svnPassword", svnPassword);
        buildWorkSpace =  props.getProperty("buildWorkSpace", buildWorkSpace);
        repositoryPath = props.getProperty("repositoryPath", repositoryPath);
        profile = props.getProperty("profile", profile);
        frontGitBranch = props.getProperty("frontGitBranch", frontGitBranch);

        log.info("config.properties exists, after load config and the values follow......");
        log.info("serverSvnUrl=\t{}", serverSvnUrl);
        log.info("serviceRenewSvnUrl=\t{}", serviceRenewSvnUrl);
        log.info("frontGitUrl=\t{}", frontGitUrl);
        log.info("frontGitBranch=\t{}", frontGitBranch);
        log.info("buildWorkSpace=\t{}", buildWorkSpace);
        log.info("profile=\t{}", profile);
        log.info("repositoryPath=\t{}", repositoryPath);
        sepLine("全局配置加载完毕", true);
    }

    /**
     * 打印日志阶段性的分割线 并携带标题
     * @param title  分割线中间的标题内容
     * @param isWrap  是否换行
     */
    public static void sepLine(String title, boolean isWrap){
        if (StringUtils.isNotEmpty(title)){
            log.info("------------------[ {} ]------------------{}", title, isWrap ? "\n" : "");
        }else {
            log.info("-------------------------------------------{}", isWrap ? "\n" : "");
        }
    }


    /**
     * 获取运行依赖环境的的路径
     * @return
     */
    public static String getGlobalServerSpace(){
        return buildWorkSpace + File.separator + "server";
    }

    /**
     * 获取依赖环境中需要切换到重构分支service的系统路径
     * @return
     */
    public static String getServiceSpace(){
        return getGlobalServerSpace() + File.separator + "web" + File.separator + "service";
    }

    /**
     * 获取重构front项目的本地路径
     * @return
     */
    public static String getRenewFrontSpace(){
        return getBuildWorkSpace() + File.separator + "front";
    }

    /**
     *
     * @param paths
     * @return
     */
    private static String getEnvPath(String ... paths){
        StringBuffer buffer = new StringBuffer(getGlobalServerSpace()).append(File.separator);
        for (String p : paths){
            buffer.append(p).append(File.separator);
        }

        return buffer.toString();
    }

    /**
     * 当当前是测试环境时，在server/web/service/..tl_lm/ 中需要修改数据库端口的文件列表
     * @return
     */
    public static List<String> needModifyDsPortList(){
        List<String> fileList = new ArrayList<>();
        // web/service/src/main/resources/tl_lm
        String fileName = "group.xml";
        String bPath = getEnvPath("web", "service","src", "main", "resources", "tl_lm");
        String filePath1 = bPath + File.separator + fileName;
        String filePath2 = bPath + File.separator + "dev" + File.separator + fileName;
        String filePath3 = bPath + File.separator + "release" + File.separator + fileName;

        fileList.add(filePath1);
        fileList.add(filePath2);
        fileList.add(filePath3);
        return fileList;
    }

    /**
     * 获取本地环境中需要修改的List项
     * @return
     */
    public static List<PomDependencies> getLocalPomsNeedModifiedList(){
        //  需要修改的代码依赖项
        final Dependency lombokDependency = new Dependency("org.projectlombok", "lombok", "1.18.10", "provided");
        final Dependency jaxpDependency = new Dependency("com.sun.org.apache", "jaxp-ri", "1.4");

        // 获取需要修改的环境内部pom根路径
        String streamagentPath = getEnvPath("show", "streamagent");
        String clientShowPath = getEnvPath("trade-show", "client-show");
        String tradeShowPath = getEnvPath("trade-show");
        String basePath = getEnvPath("web", "base");
        String webPath = getEnvPath("web");

        // 封装PD对象: 包含需要修改的依赖项和对应的
        final PomDependencies streamagentPomDependencies = new PomDependencies(streamagentPath, jaxpDependency);
        final PomDependencies clientShowPomDependencies = new PomDependencies(clientShowPath, lombokDependency);
        final PomDependencies tradeShowPomDependencies = new PomDependencies(tradeShowPath, lombokDependency);
        final PomDependencies basePomDependencies = new PomDependencies(basePath, jaxpDependency);
        final PomDependencies webPomDependencies = new PomDependencies(webPath, lombokDependency);

        List<PomDependencies> resultPomDependencies = new ArrayList<>();
        resultPomDependencies.add(streamagentPomDependencies);
        resultPomDependencies.add(clientShowPomDependencies);
        resultPomDependencies.add(tradeShowPomDependencies);
        resultPomDependencies.add(basePomDependencies);
        resultPomDependencies.add(webPomDependencies);

        return resultPomDependencies;
    }

}
