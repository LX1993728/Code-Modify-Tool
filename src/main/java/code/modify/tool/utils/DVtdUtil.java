package code.modify.tool.utils;

import com.ximpleware.*;
import lombok.extern.log4j.Log4j;

import java.io.File;

/**
 * 使用轻量级工具VDT-XML 解析修改pom.xml
 * 参考网址1: https://vtd-xml.sourceforge.io/codeSample/cs7.html
 * 参考网址2: https://vtd-xml.sourceforge.io/codeSample/cs1.html
 * 参考网址3: https://vtd-xml.sourceforge.io/codeSample/cs10.html
 */
@Log4j
public class DVtdUtil {
    public static boolean addOrUpdateXmlByXpath(String url,
                                                String groupId,
                                                String artifactId,
                                                String version,
                                                String scope){
        String pomUrl = url + File.separator +"pom.xml";
        /**
         * xm: 用于插入element字符串xm.insertBeforeElement("<b/>\n\t"); 和 更新element的内容 xm.updateToken
         * vn: 用于移动游标cursor位置的
         * ap: 用于支持XPath表达式定位元素的
         */
        VTDGen vg = new VTDGen(); // Instantiate VTDGen
        try {
        if (vg.parseFile(pomUrl, true)){
            VTDNav vn = vg.getNav();
            XMLModifier xm = new XMLModifier(); //Instantiate XMLModifier
            AutoPilot ap = new AutoPilot(vn);
            ap.declareXPathNameSpace("prefix","http://maven.apache.org/POM/4.0.0");
            log.info("---- 有效解析 ----");
            xm.bind(vn);
            String pattern = String.format("//prefix:dependency[prefix:groupId='%s' and prefix:artifactId = '%s']",
                    groupId.trim(), artifactId.trim());
            ap.selectXPath(pattern);
            // 打印XPath选择的元素标签
            printEleByXpath(ap, vn);
            // 需要重置XPath游标
            ap.resetXPath();
            int r1 = ap.evalXPath();
            if (r1 != -1 && version != null && !version.isEmpty()){
                if (vn.toElement(VTDNav.FC, "version")){ // 说明找到指定的依赖标签，并将游标移至当前dependency下的version处
                    final int vIndex = vn.getText();
                    if (vIndex != -1){
                        String oldVersion = vn.toNormalizedString(vIndex);
                        xm.updateToken(vIndex, version);
                        log.info("---- 在指定依赖中找到version,旧值为 " + oldVersion + " 更新为" + version + " ----");
                    }
                }else { // 找到了指定的依赖，但是依赖中不包含version标签,则需要在当前的依赖中插入指定的新version
                    if (vn.toElement(VTDNav.FC, "artifactId")){
                        xm.insertAfterElement(String.format("\n\t\t\t<version>%s</version>", version));
                        log.info("---- 在指定依赖中未找到version, 插入为" + version + " ----");
                    }
                }
            }

            ap.resetXPath(); // 并不能确定scope是在version标签前还是后, 所以需要重新定位指定的dependency
            int r2 = ap.evalXPath();
            if (r2 != -1 && scope != null && !scope.isEmpty()){
                if (vn.toElement(VTDNav.FC, "scope")){
                    final int sIndex = vn.getText();
                    if (sIndex != -1){
                        String oldScope = vn.toNormalizedString(sIndex);
                        xm.updateToken(sIndex, scope);
                        log.info("---- 在指定依赖中找到scope,旧值为 " + oldScope + " 更新为" + scope + " ----");
                    }
                }else {
                    if (vn.toElement(VTDNav.FC, "artifactId")){
                        xm.insertAfterElement(String.format("\n\t\t\t<scope>%s</scope>", scope));
                        log.info("---- 在指定依赖中未找到scope, 插入为" + scope + " ----");
                    }
                }
            }
            String pattern2 = "//prefix:dependencies";
            if (r1 == -1){
                // 需要判断是否存在<dependencies>标签
                ap.selectXPath(pattern2);
            }

            xm.output(pomUrl);
        }

        }catch (Exception e){
            log.info(e.getMessage(), e);
        }
        return true;
    }

    /**
     * 打印指定表达式包含标签的内容
     * @param ap
     * @param vn
     * @throws NavException
     * @throws XPathEvalException
     */
    private static void printEleByXpath(AutoPilot ap, VTDNav vn) throws NavException, XPathEvalException {
        int result = -1;
        int count = 0;
        while((result = ap.evalXPath())!=-1){
            System.out.print(""+result+" ");
            System.out.print("Element name ==> "+vn.toString(result));
            int t = vn.getText(); // get the index of the text (char data or CDATA)
            if (t!=-1)
                log.info(" Text ==> "+vn.toNormalizedString(t));
            log.info("\n ============================== ");
            count++;
        }
        log.info("Total # of element "+count);
    }

    public static void main(String[] args){
        addOrUpdateXmlByXpath("poms2", "org.projectlombok", "lombok", "1.0.0", "");
    }
}
