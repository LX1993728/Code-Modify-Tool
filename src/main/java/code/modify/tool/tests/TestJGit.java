package code.modify.tool.tests;

import code.modify.tool.domains.Config;
import code.modify.tool.utils.jgit.GitUtil;
import org.eclipse.jgit.api.errors.GitAPIException;

public class TestJGit {
    public static void main(String[] args){
        String remotePath = "http://10.128.41.7/java/front.git";
        String localPath = "C:\\Users\\liuxun\\Desktop\\runtime\\test\\front";
        String username = "liuxun";
        String password = "liuxun1993728";
        final GitUtil gitUtil = new GitUtil(localPath, remotePath, username, password);
        final String workSpace = Config.buildWorkSpace;
        try {
           // gitUtil.cloneBranch("master");
            gitUtil.pull("master");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
