package code.modify.tool.tests;

import code.modify.tool.utils.svnkit.SVNKitUtil;

public class TestSVNKit {

    public static void main(String[] args){
        String svnUrl = "svn://10.2.6.23/TLSHOW/project/branch/global/server/web/service";
        String svnUsername = "liuxun";
        String svnPassword = "liuxun1993728";
        // 需要注意的是 需要在target的最后目录节点要和项目名一致 且没有项目检出之前 不要创建这个文件夹
        String targetPath = "C:\\Users\\liuxun\\Desktop\\runtime\\test\\service";

        // 测试检出代码
        SVNKitUtil.checkOut(svnUrl, svnUsername, svnPassword, targetPath);
        SVNKitUtil.doCleanup(svnUsername,svnPassword, targetPath);

        // 测试更新代码
        SVNKitUtil.doUpdate(svnUsername, svnPassword, targetPath);
    }
}
