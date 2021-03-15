package code.modify.tool.domains;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
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
    public List<PD> getLocalPomsNeedModifiedList(){
        //  需要修改的代码依赖项
        final D lombokD = new D("org.projectlombok", "lombok", "1.18.10", "provided");
        final D jaxpD = new D("com.sun.org.apache", "jaxp-ri", "1.4");

        // 获取需要修改的环境内部pom根路径
        String streamagentPath = getEnvPath("show", "streamagent");
        String clientShowPath = getEnvPath("trade-show", "client-show");
        String tradeShowPath = getEnvPath("trade-show");
        String basePath = getEnvPath("base");
        String webPath = getEnvPath("web");

        // 封装PD对象: 包含需要修改的依赖项和对应的
        final PD streamagentPD = new PD(streamagentPath, jaxpD);
        final PD clientShowPD = new PD(clientShowPath, lombokD);
        final PD tradeShowPD = new PD(tradeShowPath, lombokD);
        final PD basePD = new PD(basePath, jaxpD);
        final PD webPD = new PD(webPath, lombokD);

        List<PD> resultPDS = new ArrayList<>();
        resultPDS.add(streamagentPD);
        resultPDS.add(clientShowPD);
        resultPDS.add(tradeShowPD);
        resultPDS.add(basePD);
        resultPDS.add(webPD);

        return resultPDS;
    }

}
