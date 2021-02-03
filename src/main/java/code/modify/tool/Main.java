package code.modify.tool;

import code.modify.tool.domains.D;
import code.modify.tool.domains.PD;
import code.modify.tool.utils.DMvnUtil;

import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception{
        final DMvnUtil depencyTool = new DMvnUtil();
        // 定义需要修改的pom的数组

        final PD pd1 = new PD();
        pd1.setUrl("poms");
        pd1.addD(new D[]{
                new D("org.projectlombok","lombok","1.18.10","provided")
        });
        updateDForPom(pd1);

    }

    public static void updateDForPom(PD pd){
        String url = pd.getUrl();
        final Set<D> dSet = pd.getDSet();
        for (D d : dSet){
            DMvnUtil.addOrUpdateXml(url, d.getGroupId(), d.getArtifactId(), d.getVersion(), d.getScope());
        }
    }
}
