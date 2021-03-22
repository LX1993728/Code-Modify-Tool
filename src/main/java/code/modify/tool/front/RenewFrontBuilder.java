package code.modify.tool.front;

import code.modify.tool.domains.GlobalConfig;
import code.modify.tool.domains.MvnBuilder;
import code.modify.tool.domains.PomDependencies;
import code.modify.tool.utils.ReplaceUtil;
import code.modify.tool.utils.embedmaven.MavenCliBuilder;
import code.modify.tool.utils.jgit.GitUtil;
import code.modify.tool.utils.pomxml.DVtdUtil;
import code.modify.tool.utils.svnkit.SVNKitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.tmatesoft.svn.core.SVNException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RenewFrontBuilder {

    /**
     * 检出global/server代码后, 需要修改的项目依赖环境的本地依赖 以及数据库配置等
     */
    public static void pullAndModifyCode() throws IOException, SVNException, GitAPIException {
        // step1: 判断构建工程是否存在
        GlobalConfig.sepLine("开始新版代码环境处理...", true);
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

        String profile = GlobalConfig.getProfile();
        if (profile.equalsIgnoreCase("tl_trunk")){
            log.info("当前是测试环境 profile={}, service数据源端口修改为8067...", profile);
            for (String gPath : GlobalConfig.needModifyDsPortList()) {
                ReplaceUtil.replaceTextInFile(gPath, "8066", "8067");
            }
        }else {
            log.info("当前非测试环境 profile={}, service数据源端口修改为8066...", profile);
            for (String gPath : GlobalConfig.needModifyDsPortList()) {
                ReplaceUtil.replaceTextInFile(gPath, "8067", "8066");
            }
        }

        GlobalConfig.sepLine("开始处理重构新版front对应的代码", true);
        String renewFrontSpace = GlobalConfig.getRenewFrontSpace();
        File rfFile = new File(renewFrontSpace);
        GitUtil gitUtil = new GitUtil(renewFrontSpace, GlobalConfig.getFrontGitUrl(), GlobalConfig.getGitUsername(), GlobalConfig.getGitPassword());
        if (!rfFile.exists()){
            log.info("renew front space 不存在, 正在clone...");
            gitUtil.cloneBranch(GlobalConfig.getFrontGitBranch());
        }else {
            String localRenewFrontUrl = gitUtil.getRemoteRepositoryUrl();
            if (!StringUtils.isEmpty(localRenewFrontUrl) && localRenewFrontUrl.trim().equals(GlobalConfig.getFrontGitUrl())){
                log.info("renew front space 已经存在, 正在检查分支...");
                if (gitUtil.getCurrentBranch().trim().equals(GlobalConfig.getFrontGitBranch().trim())){
                    log.info("renew front space 当前分支为: {} 和配置指定的分支: {} 相同, 不需要切换分支...", gitUtil.getCurrentBranch(),
                            GlobalConfig.getFrontGitBranch());
                }else {
                    log.info("renew front space 当前分支为: {} 和配置指定的分支: {} 不相同, 代码分支切换中...", gitUtil.getCurrentBranch(),
                            GlobalConfig.getFrontGitBranch());
                    gitUtil.checkout(GlobalConfig.getFrontGitBranch());
                }
                log.info("renew front space 当前分支为: {}, 代码更新中...", gitUtil.getCurrentBranch());
                gitUtil.pull(GlobalConfig.getFrontGitBranch());
            }else {
                log.info("renew front space 文件夹存在, 但仓库存在错误,删除中...");
                FileUtils.deleteQuietly(rfFile);
                log.info("删除文件夹后重新克隆renew front space...");
                gitUtil.cloneBranch(GlobalConfig.getFrontGitBranch());
            }
        }

        // step7: 对所依赖的环境模块依次进行install
        log.info("server space && renew front space is Over, TODO://处理环境的install工作");
    }

    /**
     * 对重构后的相关环境依赖以及front进行打包
     */
    public static void BuildEnvAndRenewFront(){
        GlobalConfig.sepLine("", false);
        GlobalConfig.sepLine("开始进行front依赖环境的打包", true);
        final List<MvnBuilder> envMvnBuilds = GlobalConfig.getEnvMvnBuilds();
        for (MvnBuilder mb : envMvnBuilds){
            String path = mb.getPath();
            final String[] commands = mb.getCommands();
            log.info("开始执行maven命令 path={}\t  command={}\t", path, commands);
            new MavenCliBuilder(new File(path), commands).build();
            log.info("执行maven命令完毕 path={}\t  command={}\t", path, commands);
        }

        GlobalConfig.sepLine("front依赖环境打包完毕, 开始进行front的打包", true);
        final String frontPath = MvnBuilder.getFrontPath();
        final String[] frontCommands = MvnBuilder.getFrontCommands();
        new MavenCliBuilder(new File(frontPath), frontCommands).buildWithConsole();

        GlobalConfig.sepLine("front 打包完毕...", true);
        // 开始进行front包的备份

    }

    /**
     * 将打包好的front 运行jar包重命名拷贝到指定的路径并上传
     */
    public static void copyRenewFrontJarAndUpload(){
        //TODO://
    }

    public static void main(String[] args) throws IOException, SVNException, GitAPIException {
       pullAndModifyCode();
       BuildEnvAndRenewFront();
    }
}
