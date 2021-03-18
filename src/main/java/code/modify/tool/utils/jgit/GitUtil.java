package code.modify.tool.utils.jgit;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

@Slf4j
public final class GitUtil {
    private String localPath, localGitPath, remotePath;
    private Repository localRepository;
    private String username;
    private String password;
    private Git git;

    public GitUtil(String localPath, String remotePath,String username ,String password) {
        this.username = username;
        this.password = password;
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.localGitPath = this.localPath + "/.git";
        try {
            localRepository = new FileRepository(localGitPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        git = new Git(localRepository);
    }

    /**
     * 创建本地仓库
     *
     * @throws IOException
     */
    public void create() throws IOException {
        Repository repository = new FileRepository(localGitPath);
        repository.create();
        log.info("create success");
    }

    /**
     * clone克隆远程分支到本地
     *
     * @param branchName
     * @throws GitAPIException
     */
    public void cloneBranch(String branchName) throws GitAPIException {
        Git.cloneRepository()
                .setURI(remotePath)
                .setBranch(branchName)
                .setDirectory(new File(localPath))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.username, this.password))
                .call();
        log.info("clone success");
    }

    /**
     * pull远程代码
     *
     * @param branchName 远程分支名称
     * @throws Exception
     */
    public void pull(String branchName) throws Exception {
        git.pull().setRemoteBranchName(branchName)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.username, this.password))
                .call();
        log.info("pull success");
    }

    /**
     * 将单个文件加入Git
     *
     * @param fileName 添加文件名
     * @throws Exception
     */
    public void add(String fileName) throws Exception {
        File myFile = new File(localPath + fileName);
        myFile.createNewFile();
        git.add().addFilepattern(fileName).call();
        log.info("add success");
    }

    /**
     * 将增加的所有文件加入Git
     *
     * @throws Exception
     */
    public void addAll() throws Exception {
        git.add().addFilepattern(".").call();
        log.info("add success");
    }


    /**
     * 提交文件
     *
     * @param message 提交备注
     * @throws Exception
     */
    public void commit(String message) throws Exception {
        final CommitCommand commitCommand = git.commit().setMessage(message);
        commitCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.username, this.password));
        commitCommand.call();
        log.info("commit success");
    }

    /**
     * 同步远程仓库
     *
     * @throws Exception
     */
    public void push() throws Exception {
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.username, this.password)).call();
        log.info("push success");
    }

    /**
     *
     * @return 获取当前分支
     * @throws IOException
     */
    public String getCurrentBranch() throws IOException {
        return git.getRepository().getBranch();
    }

    /**
     * 获取当前仓库对应的远程地址
     * @return
     */
    public String getRemoteRepositoryUrl(){
        return git.getRepository().getConfig().getString("remote", "origin", "url");
    }

}

