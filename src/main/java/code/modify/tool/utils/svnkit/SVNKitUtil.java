package code.modify.tool.utils.svnkit;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.util.DefaultSVNDebugLogger;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.util.ISVNDebugLog;
import org.tmatesoft.svn.util.SVNDebugLog;

@Slf4j
public class SVNKitUtil {

    // 更新状态 true:没有程序在执行更新，反之则反
    public static Boolean DoUpdateStatus = true;

    // 声明SVN客户端管理类
    private static SVNClientManager ourClientManager;

    /**
     * SVN检出
     *
     * @return Boolean
     */
    public static Boolean checkOut(String svnUrl, String svnUsername, String svnPassword, String targetPath) {
        // 初始化库。 必须先执行此操作。具体操作封装在setupLibrary方法中。
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();

        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();

        // 相关变量赋值
        SVNURL repositoryURL = null;
        try {
            repositoryURL = SVNURL.parseURIEncoded(svnUrl);
        } catch (SVNException e) {
            e.printStackTrace();
            return false;
        }

        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);

        // 实例化客户端管理类
        ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, svnUsername, svnPassword);

        // 要把版本库的内容check out到的目录
        File wcDir = new File(targetPath);

        // 通过客户端管理类获得updateClient类的实例。
        SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
        updateClient.setDebugLog(getSvnLogger());
        updateClient.setIgnoreExternals(false);

        // 执行check out 操作，返回工作副本的版本号。
        long workingVersion = -1;
        try {
            if (!wcDir.exists()) {
                workingVersion = updateClient.doCheckout(repositoryURL, wcDir, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);
            } else {
                ourClientManager.getWCClient().doCleanup(wcDir);
                workingVersion = updateClient.doCheckout(repositoryURL, wcDir, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);
            }
        } catch (SVNException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        log.info("把版本：" + workingVersion + " check out 到目录：" + wcDir + "中。");
        return true;

    }

    /**
     * 解除svn Luck
     *
     * @return Boolean
     */
    public static Boolean doCleanup(String svnUsername, String svnPassword, String targetPath) {
        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        // 实例化客户端管理类
        ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, svnUsername, svnPassword);

        // 要把版本库的内容check out到的目录
        File wcDir = new File(targetPath);
        if (wcDir.exists()) {
            try {
                ourClientManager.getWCClient().doCleanup(wcDir);
            } catch (SVNException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static void switchToBranch(final String branchUrl,
                                      final String svnUsername,
                                      final String svnPassword,
                                      final String targetPath) throws SVNException, IOException {
        final File baseDir = new File(targetPath);
        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        // 实例化客户端管理类
        ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, svnUsername, svnPassword);
        final SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
        updateClient.setDebugLog(getSvnLogger());
        updateClient.doSwitch(baseDir,
                SVNURL.parseURIEncoded(branchUrl),
                SVNRevision.HEAD,
                SVNRevision.HEAD,
                SVNDepth.INFINITY,
                false,
                false);

    }

    private static ISVNDebugLog getSvnLogger(){
        final ISVNDebugLog svnDebugLogger = new CustomSVNLogger();
        SVNDebugLog.setDefaultLog(svnDebugLogger);
        return svnDebugLogger;
    }

    /**
     * 更新svn
     *
     * @return int(-1更新失败，1成功，0有程序在占用更新)
     */
    public static int doUpdate(String svnUsername, String svnPassword, String targetPath) {
        if (!SVNKitUtil.DoUpdateStatus) {
            log.info("更新程序已经在运行中，不能重复请求！");
            return 0;
        }
        SVNKitUtil.DoUpdateStatus = false;
        /*
         * For using over http:// and https://
         */
        try {
            DAVRepositoryFactory.setup();

            ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
            // 实例化客户端管理类
            ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, svnUsername, svnPassword);
            // 要更新的文件
            File updateFile = new File(targetPath);
            // 获得updateClient的实例
            SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
            updateClient.setDebugLog(getSvnLogger());
            updateClient.setIgnoreExternals(false);
            // 执行更新操作
            long versionNum = updateClient.doUpdate(updateFile, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
            log.info("工作副本更新后的版本：" + versionNum);
            DoUpdateStatus = true;
            return 1;
        } catch (SVNException e) {
            DoUpdateStatus = true;
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Svn提交 list.add("a.txt")也可直接添加单个文件; list.add("aaa")添加文件夹将添加夹子内所有的文件到svn,预添加文件必须先添加其所在的文件夹;
     *
     * @param fileRelativePathList 文件相对路径
     * @return Boolean
     */
    public static Boolean doCommit(List<String> fileRelativePathList, String svnUsername, String svnPassword, String targetPath) {
        // 注意：执行此操作要先执行checkout操作。因为本地需要有工作副本此范例才能运行。
        // 初始化支持svn://协议的库
        SVNRepositoryFactoryImpl.setup();

        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        // 实例化客户端管理类
        ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options,svnUsername, svnPassword);
        // 要提交的文件夹子
        File commitFile = new File(targetPath);
        // 获取此文件的状态（是文件做了修改还是新添加的文件？）
        SVNStatus status = null;
        File addFile = null;
        String strPath = null;
        try {
            if (fileRelativePathList != null && fileRelativePathList.size() > 0) {
                for (int i = 0; i < fileRelativePathList.size(); i++) {
                    strPath = fileRelativePathList.get(i);
                    addFile = new File(targetPath + "/" + strPath);
                    status = ourClientManager.getStatusClient().doStatus(addFile, true);
                    // 如果此文件是新增加的则先把此文件添加到版本库，然后提交。
                    if (null == status || status.getContentsStatus() == SVNStatusType.STATUS_UNVERSIONED) {
                        // 把此文件增加到版本库中
                        ourClientManager.getWCClient().doAdd(addFile, false, false, false, SVNDepth.INFINITY, false, false);
                        log.info("add");
                    }
                }
                // 提交此文件
                ourClientManager.getCommitClient().doCommit(new File[] { commitFile }, true, "", null, null, true, false, SVNDepth.INFINITY);
                log.info("commit");
            }
            // 如果此文件不是新增加的，直接提交。
            else {
                ourClientManager.getCommitClient().doCommit(new File[] { commitFile }, true, "", null, null, true, false, SVNDepth.INFINITY);
                log.info("commit");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // log.info(status.getContentsStatus());
        return true;
    }

    /**
     * Svn提交
     *
     * @param fileRelativePath 文件相对路径
     * @return Boolean
     */
    public static Boolean doCommit(String fileRelativePath,String svnUsername, String svnPassword, String targetPath) {
        // 注意：执行此操作要先执行checkout操作。因为本地需要有工作副本此范例才能运行。
        // 初始化支持svn://协议的库
        SVNRepositoryFactoryImpl.setup();

        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        // 实例化客户端管理类
        ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, svnUsername, svnPassword);
        // 要提交的文件夹子
        File commitFile = new File(targetPath);
        // 获取此文件的状态（是文件做了修改还是新添加的文件？）
        SVNStatus status = null;
        File addFile = null;
        try {
            if (fileRelativePath != null && fileRelativePath.trim().length() > 0) {
                addFile = new File(targetPath + "/" + fileRelativePath);
                status = ourClientManager.getStatusClient().doStatus(addFile, true);
                // 如果此文件是新增加的则先把此文件添加到版本库，然后提交。
                if (null == status || status.getContentsStatus() == SVNStatusType.STATUS_UNVERSIONED) {
                    // 把此文件增加到版本库中
                    ourClientManager.getWCClient().doAdd(addFile, false, false, false, SVNDepth.INFINITY, false, false);
                    log.info("add");
                }
                // 提交此文件
                ourClientManager.getCommitClient().doCommit(new File[] { commitFile }, true, "", null, null, true, false, SVNDepth.INFINITY);
                log.info("commit");
            }
            // 如果此文件不是新增加的，直接提交。
            else {
                ourClientManager.getCommitClient().doCommit(new File[] { commitFile }, true, "", null, null, true, false, SVNDepth.INFINITY);
                log.info("commit");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // log.info(status.getContentsStatus());
        return true;
    }

    /**
     * 将文件导入并提交到svn 同路径文件要是已经存在将会报错
     *
     * @param dirPath 文件所在文件夹的路径
     * @return Boolean
     */
    public static Boolean doImport(String dirPath, String svnUrl, String svnUsername, String svnPassword) {
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        // 相关变量赋值
        SVNURL repositoryURL = null;
        try {
            repositoryURL = SVNURL.parseURIEncoded(svnUrl);
        } catch (SVNException e) {
            e.printStackTrace();
        }

        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        // 实例化客户端管理类
        ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, svnUsername, svnPassword);
        // 要把此目录中的内容导入到版本库
        File impDir = new File(dirPath);
        // 执行导入操作
        SVNCommitInfo commitInfo = null;
        try {
            commitInfo = ourClientManager.getCommitClient().doImport(impDir, repositoryURL, "import operation!", null, false, false, SVNDepth.INFINITY);
        } catch (SVNException e) {
            e.printStackTrace();
            return false;
        }
        log.info(commitInfo.toString());
        return true;
    }

    public static void main(String[] args) {
        // log.info(checkOut());

//        log.info(doCleanup());
        // log.info(doCleanup());
    }

}
