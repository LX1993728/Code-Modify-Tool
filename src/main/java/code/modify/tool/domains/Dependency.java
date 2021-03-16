package code.modify.tool.domains;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * 简单的pom依赖类
 */
@Data
@RequiredArgsConstructor
public class Dependency {
    private String groupId;
    private String artifactId;
    private String version;
    private String scope;

    public Dependency(String groupId, String artifactId, String version) {
        this.groupId = groupId.trim();
        this.artifactId = artifactId.trim();
        this.version = version.trim();
    }
    public Dependency(String groupId, String artifactId, String version, String scope) {
       this(groupId, artifactId, version);
       this.scope = scope.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dependency)) return false;
        Dependency dependency = (Dependency) o;
        return groupId.equals(dependency.groupId) && artifactId.equals(dependency.artifactId) && version.equals(dependency.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }
}
