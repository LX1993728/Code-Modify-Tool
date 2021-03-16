package code.modify.tool.domains;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.*;

/**
 * 对应一个pom中 需要修改的依赖项
 */
@Data
public class PomDependencies {
    // pom.xml所在的位置
    private String url;
    // 需要修改的依赖项数组
    @Setter(AccessLevel.NONE)
    private Set<Dependency> dependencySet;

    {
        dependencySet = new HashSet<>();
    }

    public PomDependencies() {
    }

    public PomDependencies(String url, Dependency... dependencies) {
        this.url = url;
        this.dependencySet.addAll(Arrays.asList(dependencies));
    }

    public void addD(Dependency dependency){
        dependencySet.add(dependency);
    }

    public void addD(Dependency... dependencies){
        dependencySet.addAll(Arrays.asList(dependencies));
    }
}
