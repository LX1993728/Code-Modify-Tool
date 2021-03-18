package code.modify.tool.tests;

import code.modify.tool.domains.GlobalConfig;
import code.modify.tool.utils.jgit.GitUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestJGit {
    public static void main(String[] args){
        String remotePath = "http://10.128.41.7/java/front.git";
        String localPath = "C:\\Users\\liuxun\\Desktop\\runtime\\test\\front";
        String username = "liuxun";
        String password = "liuxun1993728";
        final GitUtil gitUtil = new GitUtil(localPath, remotePath, username, password);
        try {
            // gitUtil.cloneBranch("master");
            gitUtil.pull("master");
            String currentBranch = gitUtil.getCurrentBranch();
            String remoteRepositoryUrl = gitUtil.getRemoteRepositoryUrl();
            log.info("currentBranch = \t{}\nremoteRepositoryUrl= \t{}", currentBranch, remoteRepositoryUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
