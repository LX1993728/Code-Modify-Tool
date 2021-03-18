package code.modify.tool.front;

import code.modify.tool.domains.GlobalConfig;
import code.modify.tool.domains.PomDependencies;
import code.modify.tool.utils.pomxml.DVtdUtil;
import code.modify.tool.utils.svnkit.SVNKitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.tmatesoft.svn.core.SVNException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RenewFrontBuilder {

    /**
     * 检出global/server代码后, 需要修改的项目依赖环境的本地依赖 并build
     */
    public static void pullAndBuildEnvironment() throws IOException, SVNException {
        // step1: 判断构建工程是否存在
        GlobalConfig.sepLine("开始新版打包", true);
        String serverSpacePath = GlobalConfig.getGlobalServerSpace();
        File  gsFile = new File(serverSpacePath);

        // step2: 判断工程内环境依赖项目server是否存在 [对应的文件夹存在 & 进入文件夹内获取远程path与对应配置相同]
        if (!gsFile.exists()){
            log.info("server workspace不存在, 正在检出...");
            SVNKitUtil.checkOut(GlobalConfig.getServerSvnUrl(), GlobalConfig.getSvnUsername(),
                    GlobalConfig.getSvnPassword(), serverSpacePath);
        }else{
            // step3: 如果不存在则进行对应server的代码检出, 如果存在则进行项目的更新操作
            String localServerUrl = SVNKitUtil.getRemoteUrl(GlobalConfig.getSvnUsername(),
                    GlobalConfig.getSvnPassword(), serverSpacePath, true);
            if (localServerUrl != null && localServerUrl.trim().equals(GlobalConfig.getServerSvnUrl())){
                log.info("server workspace已存在, 正在更新...");
                SVNKitUtil.doUpdate(GlobalConfig.getSvnUsername(),GlobalConfig.getSvnPassword(),
                        serverSpacePath);
            }else {
                log.info("server文件夹存在, 但仓库存在错误,删除中...");
                FileUtils.deleteQuietly(gsFile);
                log.info("删除后重新检出server workspace...");
                SVNKitUtil.checkOut(GlobalConfig.getServerSvnUrl(), GlobalConfig.getSvnUsername(),
                        GlobalConfig.getSvnPassword(), serverSpacePath);
            }
        }

        String serviceSpace = GlobalConfig.getServiceSpace();
        String serviceCurUrl = SVNKitUtil.getRemoteUrl(GlobalConfig.getSvnUsername(), GlobalConfig.getSvnUsername(), serviceSpace, true);
        // step4: 判断server/web/service模块的分支path是否和对应的重构分支相同
        // step5: 相同的话则更新代码，不相同的话则切换分支再更新
        if (serviceCurUrl.trim().equals(GlobalConfig.getServiceRenewSvnUrl())){
            log.info("service 模块的分支是重构分支, 代码更新中...");
            SVNKitUtil.doUpdate(GlobalConfig.getSvnUsername(), GlobalConfig.getSvnPassword(), serviceSpace);
        }else {
            log.info("service 模块的分支非重构分支, 分支切换中...");
            SVNKitUtil.switchToBranch(GlobalConfig.getServiceRenewSvnUrl(), GlobalConfig.getSvnUsername(),
                    GlobalConfig.getSvnPassword(), serviceSpace);
            log.info("service 模块的分支已经切换到重构分支, 代码更新中...");
            SVNKitUtil.doUpdate(GlobalConfig.getSvnUsername(), GlobalConfig.getSvnPassword(), serverSpacePath);
        }

        // step6: server内的修改列表，采用代码修改工具进行代码修改
        GlobalConfig.sepLine("Server 工作空间以及service重构分支已完毕, 本地代码修改中", true);
        final List<PomDependencies> needModifiedList = GlobalConfig.getLocalPomsNeedModifiedList();
        AtomicInteger i = new AtomicInteger(1);
        needModifiedList.forEach(pd -> {
            String path = pd.getPath();
            // 首先以及封装了 [查询对应的文件是否已经具备此依赖] 的功能
            log.info("++++++++++ 开始处理第 {} 项修改\t 路径为 {}", i, path);
            pd.getDependencySet().forEach(d ->{
                String gId = d.getGroupId();
                String aId = d.getArtifactId();
                String v = d.getVersion();
                String scope = d.getScope();
                DVtdUtil.addOrUpdateXmlByXpath(path, gId, aId, v, scope);
                log.info("内容修改 groupId={}\tartifiactId={}\tversion={}\tscope={} 完毕", gId, aId, v, scope);
            });
            i.getAndIncrement();
            GlobalConfig.sepLine("", true);
        });

        // step7: 对所依赖的环境模块依次进行install
        log.info("TODO:// 处理环境的install 工作");
    }

    /**
     * 对重构后的front进行打包
     */
    public static void pullAndBuildRenewFront(){
        // step1: 判断front工程是否存在 [对应的文件夹存在 && 具有对应的分支地址]

        // step2: 如果不存在则进行代码的检出，否则进行代码的更新

        // step3: 查看当前的分支是否是指定的分支，如果不是则切换为对应的分支

        // step4: 最后按照profile进行打包

        // step5: 将打好的包重命名，备份后传输到指定的位置
    }

    public static void main(String[] args) throws IOException, SVNException {
        pullAndBuildEnvironment();
    }
}
