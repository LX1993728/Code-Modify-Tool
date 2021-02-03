package code.modify.tool.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * 自定义NamespaceContext处理XPath无法读取带有xmlns 头的问题
 */
public class MappedNamespaceContext implements NamespaceContext {

    private final Map<String, String> prefixMap = new HashMap<>();

    public MappedNamespaceContext() {
        prefixMap.put("xml", XMLConstants.XML_NS_URI);
    }

    public MappedNamespaceContext(String prefix, String namespaceUri) {
        this();
        addMapping(prefix, namespaceUri);
    }

    public MappedNamespaceContext declareNamespace(String prefix, String namespaceUri) {
        addMapping(prefix, namespaceUri);
        return this;
    }

    private void addMapping(String prefix, String namespaceUri) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix can't be null");
        } else if (namespaceUri == null) {
            throw new IllegalArgumentException("Namespace URI can't be null");
        }

        // Prevent redefining special prefixes to a wrong URI.
        if (prefix.equals("xmlns")) {
            if (!namespaceUri.equals(XMLConstants.NULL_NS_URI)
                    && !namespaceUri.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
                throw new IllegalArgumentException("Cannot redefine the 'xmlns' prefix");
            }
        } else if (prefix.equals("xml")) {
            if (!namespaceUri.equals(XMLConstants.XML_NS_URI)) {
                throw new IllegalArgumentException("Cannot redefine the 'xml' prefix");
            }
        }

        prefixMap.put(prefix, namespaceUri);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix can't be null");
        }
        return prefixMap.getOrDefault(prefix, XMLConstants.NULL_NS_URI);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        Iterator<String> iterator = getPrefixes(namespaceURI);
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("Namespace URI can't be null");
        }
        List<String> prefixes = prefixMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(namespaceURI))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
        return prefixes.iterator();
    }
}