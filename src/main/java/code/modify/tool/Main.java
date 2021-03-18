package code.modify.tool;

import code.modify.tool.front.RenewFrontBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) throws Exception{
        // log.info("args={}", args);
        RenewFrontBuilder.pullAndModifyCode();
        RenewFrontBuilder.BuildEnvAndRenewFront();
        RenewFrontBuilder.copyRenewFrontJarAndUpload();
    }
}
