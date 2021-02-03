package code.modify.tool.domains;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.*;

/**
 * 对应一个pom中 需要修改的依赖项
 */
@Data
public class PD {
    // pom.xml所在的位置
    private String url;
    // 需要修改的依赖项数组
    @Setter(AccessLevel.NONE)
    private Set<D> dSet;

    {
        dSet = new HashSet<>();
    }

    public void addD(D d){
        dSet.add(d);
    }

    public void addD(D ... ds){
        dSet.addAll(Arrays.asList(ds));
    }
}
