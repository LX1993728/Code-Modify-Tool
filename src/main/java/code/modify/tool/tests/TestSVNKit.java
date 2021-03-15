package code.modify.tool.tests;

import code.modify.tool.utils.svnkit.SVNKitUtil;
import lombok.extern.slf4j.Slf4j;
import org.tmatesoft.svn.core.SVNException;

import java.io.File;
import java.io.IOException;

@Slf4j
public class TestSVNKit {

    public static void main(String[] args){
        String svnUrl = "svn://10.2.6.23/TLSHOW/project/branch/global/server/web/service";
        String svnUsername = "liuxun";
        String svnPassword = "liuxun1993728";
        // 需要注意的是 需要在target的最后目录节点要和项目名一致 且没有项目检出之前 不要创建这个文件夹
        String targetPath = "C:\\Users\\liuxun\\Desktop\\runtime\\test\\service";

        final File targetDir = new File(targetPath);
        if (!targetDir.exists()){
            log.info("项目不存在, 尝试检出...");
            // 测试检出代码
            SVNKitUtil.checkOut(svnUrl, svnUsername, svnPassword, targetPath);
            SVNKitUtil.doCleanup(svnUsername,svnPassword, targetPath);
        }else {
            // 测试更新代码
            log.info("项目已存在, 尝试更新....");
            SVNKitUtil.doUpdate(svnUsername, svnPassword, targetPath);
        }

        String branchUrl = "svn://10.2.6.23/TLSHOW/project/trunk/server_renew_global/web/service";

        // 测试切换分支
        try {
            SVNKitUtil.switchToBranch(branchUrl, svnUsername, svnPassword, targetPath);
        } catch (SVNException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
