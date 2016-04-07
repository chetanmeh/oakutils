package com.chetanmeh.oak.state.export

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.apache.jackrabbit.oak.api.PropertyState
import org.apache.jackrabbit.oak.api.Type
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry
import org.apache.jackrabbit.oak.spi.state.NodeState

import javax.jcr.PropertyType


class XmlExporter {
    static final def NAMESPACES = [
            oak : "http://jackrabbit.apache.org/oak/ns/1.0",
            xmpMM : "http://ns.adobe.com/xap/1.0/mm/",
            dc : "http://purl.org/dc/elements/1.1/",
            slingevent : "http://sling.apache.org/jcr/event/1.0",
            sling : "http://sling.apache.org/jcr/sling/1.0",
            granite : "http://www.adobe.com/jcr/granite/1.0",
            dam : "http://www.day.com/dam/1.0",
            cq : "http://www.day.com/jcr/cq/1.0",
            jcr : "http://www.jcp.org/jcr/1.0",
            mix : "http://www.jcp.org/jcr/mix/1.0",
            nt : "http://www.jcp.org/jcr/nt/1.0",
            rep : "internal",
    ]

    String toXml(NodeState state){
        def nsMap = collectNamespaces(state)
        return XmlUtil.serialize(new StreamingMarkupBuilder().with {builder ->
            builder.bind { binding ->
                mkp.xmlDeclaration()
                nsMap.each {ns, namespace ->
                    mkp.declareNamespace((ns) : namespace)
                }
                'jcr:root' {
                    process(binding, state, 'myIndex')
                }
            }
        })
    }

    private Map collectNamespaces(NodeState state){
        def nsMap = [:]
        nsMap['jcr'] = NAMESPACES['jcr']
        collectNamespaces("", state, nsMap)
        return nsMap
    }

    private static void collectNamespaces(String name, NodeState state, def nsMap) {
       if (name.contains(':')){
           String nsPrefix = name.substring(0, name.indexOf(':'))
           String namespace = NAMESPACES[nsPrefix]
           if (!namespace){
               namespace = "internal"
           }
           nsMap.put(nsPrefix, namespace)
       }
        state.childNodeEntries.each{collectNamespaces(it.name, it.nodeState, nsMap)}
    }

    private def process = { binding, NodeState state, String name ->
        binding."$name" (propertiesMap(state)) {
            state.childNodeEntries.each { ChildNodeEntry cne ->
                process (binding, cne.nodeState, cne.name)
            }
        }
    }

    private static Map propertiesMap(NodeState state) {
        def map = [:]
        state.properties.each { PropertyState ps ->
            String value
            if (ps.type == Type.STRING || ps.name == 'jcr:primaryType'){
                value = ps.getValue(Type.STRING)
            } else {
                String typeName = PropertyType.nameFromValue(ps.type.tag())
                value = "{$typeName}${ps.getValue(ps.getType())}"
            }
            map.put(ps.name, value)
        }
        return map
    }
}
