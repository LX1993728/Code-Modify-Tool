package code.modify.tool.domains;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    @Getter
    private static String gitUsername = "liuxun";
    @Getter
    private static String gitPassword = "liuxun1993728";
    @Getter
    private static String svnUsername = "liuxun";
    @Getter
    private static String svnPassword = "liuxun1993728";

    // 构建工程的根目录
    @Getter
    private static String  buildWorkSpace = "build";
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
        String confLocation = "config.properties";
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

        log.info("config.properties exists, after load config and the values follow......");
        log.info("serverSvnUrl=\t{}", serverSvnUrl);
        log.info("serviceRenewSvnUrl=\t{}", serviceRenewSvnUrl);
        log.info("frontGitUrl=\t{}", frontGitUrl);
        log.info("buildWorkSpace=\t{}", buildWorkSpace);
    }


    /**
     * 获取运行依赖环境的的路径
     * @return
     */
    public static String getGlobalSpace(){
        return buildWorkSpace + File.separator + "server";
    }

    /**
     * 获取依赖环境中需要切换到重构分支service的系统路径
     * @return
     */
    public static String getServiceSpace(){
        return getGlobalSpace() + File.separator + "web" + File.separator + "service";
    }

    /**
     * 获取重构front项目的本地路径
     * @return
     */
    public static String getRenewFrontSpace(){
        return getGlobalSpace() + File.separator + "front";
    }

    /**
     *
     * @param paths
     * @return
     */
    private static String getEnvPath(String ... paths){
        StringBuffer buffer = new StringBuffer(getGlobalSpace()).append(File.separator);
        for (String p : paths){
            buffer.append(p).append(File.separator);
        }

        return buffer.toString();
    }

    /**
     * 获取本地环境中需要修改的List项
     * @return
     */
    public List<PomDependencies> getLocalPomsNeedModifiedList(){
        //  需要修改的代码依赖项
        final Dependency lombokDependency = new Dependency("org.projectlombok", "lombok", "1.18.10", "provided");
        final Dependency jaxpDependency = new Dependency("com.sun.org.apache", "jaxp-ri", "1.4");

        // 获取需要修改的环境内部pom根路径
        String streamagentPath = getEnvPath("show", "streamagent");
        String clientShowPath = getEnvPath("trade-show", "client-show");
        String tradeShowPath = getEnvPath("trade-show");
        String basePath = getEnvPath("base");
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
