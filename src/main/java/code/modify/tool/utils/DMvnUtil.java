package code.modify.tool.utils;

import lombok.extern.log4j.Log4j;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 操作pom文件 修改依赖的工具类
 * 但是会修改 pom文件的格式，不方便SVN/Git 等版本工具进行比较
 */
@Log4j
public class DMvnUtil {
    /**
     *
     * @param url
     * @param groupId
     * @param artifactId
     * @param version
     * @return
     */
    public static boolean addOrUpdateXml(String url,
                                         String groupId,
                                         String artifactId,
                                         String version,
                                         String scope) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        String pomUrl = url+ File.separator +"pom.xml";
        groupId = groupId.trim();
        artifactId = artifactId.trim();
        version = version.trim();
        log.info("*************** \t开始处理 " + pomUrl + "\t***************");
        try {
            FileInputStream fis = new FileInputStream(new File(pomUrl));
            Model model = reader.read(fis);
            List<Dependency> dependencies = model.getDependencies();
            Boolean existDependency = false;
           for (Dependency d : dependencies){
               if (d.getGroupId().equals(groupId) && d.getArtifactId().equals(artifactId)){
                   existDependency = true;
                   d.setVersion(version);
                   if (scope != null && !scope.isEmpty()){
                       d.setScope(scope);
                   }
                   log.info(String.format("**\t修改依赖:\tgroupId=%s\tartifactId=%s\tversion=%s",groupId, artifactId, version));
               }
           }
           if (!existDependency){
               Dependency addDependency = new Dependency();
               addDependency.setGroupId(groupId);
               addDependency.setArtifactId(artifactId);
               addDependency.setVersion(version);
               if (scope != null && !scope.isEmpty()){
                   addDependency.setScope(scope);
               }
               log.info(String.format("**\t新增依赖:\tgroupId=%s\tartifactId=%s\tversion=%s",groupId, artifactId, version));
               dependencies.add(addDependency);
           }

            model.setDependencies(dependencies);
            MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();
            mavenXpp3Writer.write(new FileWriter(pomUrl),model);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 删除pom中的依赖
     * @param url
     * @param groupId
     * @param artifactId
     * @return
     */
    public static boolean deleteXml(String url, String groupId, String artifactId) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        String pomUrl = url+ File.separator +"pom.xml";
        groupId = groupId.trim();
        artifactId = artifactId.trim();
        try {
            FileInputStream fis = new FileInputStream(new File(pomUrl));
            Model model = reader.read(fis);
            List<Dependency> dependencies = model.getDependencies();
            for (Dependency d:dependencies) {
                if(artifactId.equals(d.getArtifactId()) && groupId.equals(d.getGroupId())){
                    dependencies.remove(d);
                    break;
                }
            }
            MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();
            mavenXpp3Writer.write(new FileWriter(pomUrl),model);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return true;
    }
}
