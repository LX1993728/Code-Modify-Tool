package code.modify.tool.utils.pomxml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import code.modify.tool.services.MappedNamespaceContext;
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;


/**
 * 操作pom文件 修改依赖的工具类
 * 尝试使用XPath修改依赖
 * TODO// 存在的缺陷: 关于头部标签project的xmlns 会自动格式化头
 */
@Slf4j
public class DXmlUtil {


    /**
     *  通过XPath更新依赖, 会造成project头xmlns的格式变成一行，和原先的格式不一致
     * @param url
     * @param groupId
     * @param artifactId
     * @param version
     * @param scope
     * @return
     */
    public static boolean addOrUpdateXmlByXpath(String url,
                                         String groupId,
                                         String artifactId,
                                         String version,
                                         String scope) {

        String pomUrl = url+ File.separator +"pom.xml";
        try {
            File file = new File(pomUrl);
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            Document doc = domFactory.newDocumentBuilder().parse(file);
            XPath xpath = XPathFactory.newInstance().newXPath();
            NamespaceContext nsContext =
                    new MappedNamespaceContext("prefix", "http://maven.apache.org/POM/4.0.0");
            xpath.setNamespaceContext(nsContext);

            String pattern = String.format("//prefix:dependency[prefix:groupId='%s' and prefix:artifactId = '%s']",
                    groupId.trim(), artifactId.trim());
            String pattern2 = String.format("//prefix:dependencies");

            NodeList groupList = (NodeList)xpath.evaluate(pattern, doc, XPathConstants.NODESET);
            if (groupList.getLength() > 0){
                final Node dNode = groupList.item(0);
                // 更新version 或 scope
                if (version != null && version.length() > 0){
                    Node vNode = (Node) xpath.evaluate("prefix:version", dNode, XPathConstants.NODE);
                    if (vNode != null){
                        vNode.setTextContent(version);
                    }else {
                        final Element versionElement = doc.createElement("version");
                        versionElement.setTextContent(version);
                        dNode.appendChild(versionElement);
                    }
                    log.info("更新依赖的version....");
                }
                if (scope != null && scope.length() > 0){
                    Node vNode = (Node) xpath.evaluate("prefix:scope", dNode, XPathConstants.NODE);
                    if (vNode != null){
                        vNode.setTextContent(version);
                    }else {
                        final Element scopeElement = doc.createElement("scope");
                        scopeElement.setTextContent(scope);
                        dNode.appendChild(scopeElement);
                    }
                    log.info("更新依赖的scope....");
                }
            }else {
                NodeList dsList = (NodeList)xpath.evaluate(pattern2, doc, XPathConstants.NODESET);
                Node ds = null;
                if (dsList.getLength() > 0){
                    // 如果pom.xml中包含了dependencies标签
                    ds = dsList.item(0);
                }else {
                    NodeList psList = (NodeList)xpath.evaluate("//prefix:project", doc, XPathConstants.NODESET);
                    // 但pom.xml文件中一定会存在project标签
                    Node pNode = psList.item(0);
                    Element dsEle = doc.createElement("dependencies");
                    pNode.appendChild(dsEle);
                    ds = dsEle;
                }
                Element dEle = doc.createElement("dependency");
                Element gEle = doc.createElement("groupId");
                Element aEle = doc.createElement("artifactId");
                gEle.setTextContent(groupId);
                aEle.setTextContent(artifactId);
                dEle.appendChild(gEle);
                dEle.appendChild(aEle);
                if (version != null && !version.isEmpty()){
                    Element vEle = doc.createElement("version");
                    vEle.setTextContent(version);
                    dEle.appendChild(vEle);
                }
                if (scope != null && !scope.isEmpty()){
                    Element sEle = doc.createElement("scope");
                    sEle.setTextContent(scope);
                    dEle.appendChild(sEle);
                }
                ds.appendChild(dEle);

            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // 下边几行是处理缩进和换行的
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");
            transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "6");
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    public static void main(String[] args){
        // testParsePomXml();
        addOrUpdateXmlByXpath("poms2", "org.projectlombok", "lombokXXX", "1.0.0", "");
    }

    private static void testParsePomXmlByXpath(){
        try {
            String pomUrl = "poms"+ File.separator +"pom.xml";
            File file = new File(pomUrl);
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            Document doc = domFactory.newDocumentBuilder().parse(file);
            XPath xpath = XPathFactory.newInstance().newXPath();
            NamespaceContext nsContext =
                    new MappedNamespaceContext("prefix", "http://maven.apache.org/POM/4.0.0");
            xpath.setNamespaceContext(nsContext);

            /*
            筛选的
            String pattern = "//prefix:dependency/prefix:groupId[.='com.google.appengine']"
                    + "[../prefix:artifactId[text()='appengine-maven-plugin'"
                    + " or text()='gcloud-maven-plugin']]";
             */
            // String pattern = "//prefix:dependency";
            String pattern = "//prefix:dependency[prefix:groupId='org.projectlombok' and prefix:artifactId = 'lombok']";
            NodeList nodes = (NodeList)xpath.evaluate(pattern, doc, XPathConstants.NODESET);
            System.out.println(nodes.getLength());
            System.out.println("groupId\t\tartifactId\t\tversion\t\tscope");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String groupId = (String) xpath.evaluate("prefix:groupId", node, XPathConstants.STRING);
                String artifactId = (String) xpath.evaluate("prefix:artifactId", node, XPathConstants.STRING);
                String version = (String) xpath.evaluate("prefix:version", node, XPathConstants.STRING);
                String scope = (String) xpath.evaluate("prefix:scope", node, XPathConstants.STRING);
                log.info(groupId+ "\t\t" +artifactId+ "\t\t" + version + "\t\t" +scope);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

    }
}
