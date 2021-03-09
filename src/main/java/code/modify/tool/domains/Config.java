package code.modify.tool.domains;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * 全局配置类
 */
@Slf4j
public class Config {
    public static String  buildWorkSpace = "build";
    static {
        // 初始化项目构建的根目录
        File buildDir = new File(buildWorkSpace);
        if (!buildDir.exists()){
            log.info("构建根目录不存在, 创建中...");
            buildDir.mkdirs();
        }
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

    private static String getEnvPath(String ... paths){
        return null;
    }

    /**
     * 获取本地环境中需要修改的List项
     * @return
     */
    public List<PD> getLocalPomsList(){
        final D lombokD = new D("org.projectlombok", "lombok", "1.18.10", "provided");
        final D jaxpD = new D("com.sun.org.apache", "jaxp-ri", "1.4");

        //

        return null;
    }

}
