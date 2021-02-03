package code.modify.tool.domains;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * 简单的pom依赖类
 */
@Data
@RequiredArgsConstructor
public class D {
    private String groupId;
    private String artifactId;
    private String version;
    private String scope;

    public D(String groupId, String artifactId, String version) {
        this.groupId = groupId.trim();
        this.artifactId = artifactId.trim();
        this.version = version.trim();
    }
    public D(String groupId, String artifactId, String version, String scope) {
       this(groupId, artifactId, version);
       this.scope = scope.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof D)) return false;
        D d = (D) o;
        return groupId.equals(d.groupId) && artifactId.equals(d.artifactId) && version.equals(d.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }
}
