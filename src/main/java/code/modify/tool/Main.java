package code.modify.tool;

import code.modify.tool.domains.Dependency;
import code.modify.tool.domains.PomDependencies;
import code.modify.tool.utils.pomxml.DMvnUtil;

import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception{
        final DMvnUtil depencyTool = new DMvnUtil();
        // 定义需要修改的pom的数组

        final PomDependencies pomDependencies1 = new PomDependencies();
        pomDependencies1.setUrl("poms");
        pomDependencies1.addD(new Dependency[]{
                new Dependency("org.projectlombok","lombok","1.18.10","provided")
        });
        updateDForPom(pomDependencies1);

    }

    public static void updateDForPom(PomDependencies pomDependencies){
        String url = pomDependencies.getUrl();
        final Set<Dependency> dependencySet = pomDependencies.getDependencySet();
        for (Dependency dependency : dependencySet){
            DMvnUtil.addOrUpdateXml(url, dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getScope());
        }
    }
}
