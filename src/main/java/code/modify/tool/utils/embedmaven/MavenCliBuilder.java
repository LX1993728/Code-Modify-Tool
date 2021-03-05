package code.modify.tool.utils.embedmaven;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.cli.MavenCli;

/**
 * @author: w3sun
 * @date: 2018/12/16 19:50
 * @description:
 */
@Slf4j
@Data
public class MavenCliBuilder {
    private MavenCli mavenCli;
    private String[] args = null;
    private PrintStream stdout;
    private PrintStream stderr;
    private File localRepository;
    private File workingDirectory;
    private File settingsFile;

    {
        setMavenCli(new MavenCli());
        setArgs(null);
        setStdout(setDefaultStdLog("stdout.log"));
        setStderr(setDefaultStdLog("stderr.log"));
        setLocalRepository(null);
        setWorkingDirectory(null);
        setSettingsFile(setDefaultSettingsFile());
        //下面这行代码不能缺少，否则无法执行
        System.getProperties().setProperty("maven.multiModuleProjectDirectory", "$M2_HOME");
    }

    public MavenCliBuilder(File workingDirectory,
                           File settingsFile,
                           PrintStream stdout,
                           PrintStream stderr,
                           String... args) {
        this.workingDirectory = workingDirectory;
        this.args = args;
        this.settingsFile = settingsFile == null ? this.settingsFile : settingsFile;
        this.stdout = stdout == null ? this.stdout : stdout;
        this.stderr = stderr == null ? this.stderr : stderr;
    }

    public MavenCliBuilder(File workingDirectory,
                           String... args) {
        this(workingDirectory, null, null, null, args);
    }

    /**
     * 获取setting.xml文件的路径
     * @return
     */
    private File setDefaultSettingsFile(){
        final URL url = this.getClass().getClassLoader().getResource("settings.xml");
        File file = new File(url.getFile());
        return file;
    }

    /**
     *
     * @param logName 输入日志的文件名
     * @return
     */
    private PrintStream setDefaultStdLog(String logName){
        try {
            final File file = new File(logName);
            if (!file.exists()){
                file.createNewFile();
            }
            PrintStream stdout = new PrintStream(file);
            return stdout;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * 校验
     * @return
     */
    private void valid(){
        if (getSettingsFile()== null || !getSettingsFile().exists()){
            throw new RuntimeException("the settings.xml for maven not exist");
        }
        if (getWorkingDirectory() == null || !getWorkingDirectory().exists()){
            throw new RuntimeException("this workingDirectory for project not exist");
        }
        if (getLocalRepository() == null || !getLocalRepository().exists()){
            log.error("not set localRepository args, use settings.xml default");
        }
        if (args == null || args.length == 0){
            throw new RuntimeException("please input or set mvn xxx...");
        }
    }

    private void wrapArgs() throws IOException {
        List<String> argsList = new ArrayList<>(Arrays.asList(this.args));
        argsList.add("-gs="+ this.settingsFile.getCanonicalPath());
        if (localRepository != null && localRepository.exists()){
            argsList.add("-Dmaven.repo.local=" + this.localRepository.getCanonicalPath());
        }
        this.args = argsList.toArray(new String[this.args.length]);
    }


    public void build(){
        try {
            valid();
            wrapArgs();
            log.info("start execute mvn ->\t" + Arrays.toString(args));
            mavenCli.doMain(this.args, this.workingDirectory.getCanonicalPath(), this.stdout, this.stderr);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
    }
}
