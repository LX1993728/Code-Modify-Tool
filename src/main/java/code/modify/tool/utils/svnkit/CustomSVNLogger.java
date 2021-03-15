package code.modify.tool.utils.svnkit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.tmatesoft.svn.util.SVNDebugLogAdapter;
import org.tmatesoft.svn.util.SVNLogType;

import java.util.logging.Level;

/**
 * 配置SVN日志的控制台打印
 */
@Slf4j
public class CustomSVNLogger extends SVNDebugLogAdapter {
    @Override
    public void log(SVNLogType svnLogType, Throwable throwable, Level level) {
        log.error("{}/t{}/t{}", svnLogType, throwable, level);
    }

    @Override
    public void log(SVNLogType svnLogType, String s, Level level) {
        if (svnLogType.equals(SVNLogType.WC)){
            log.info("{} --- \t{}",svnLogType.getShortName(), s);
        }
    }

    @Override
    public void log(SVNLogType svnLogType, String s, byte[] bytes) {
        if (svnLogType.equals(SVNLogType.WC)){
            log.info("{} ++++ \t{}\t{}", svnLogType.getShortName(), s, StringUtils.newString(bytes, "GBK"));
        }
    }
}
