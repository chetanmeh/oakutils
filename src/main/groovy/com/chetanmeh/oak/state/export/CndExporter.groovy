package com.chetanmeh.oak.state.export

import com.google.appengine.labs.repackaged.com.google.common.base.Strings
import org.apache.jackrabbit.oak.api.PropertyState
import org.apache.jackrabbit.oak.api.Type
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry
import org.apache.jackrabbit.oak.spi.state.NodeState


/**
 * Exports the NodeState in CND like format which provides a compact view
 *
 * <pre>
 *     /oak:index/assetType
 *      - jcr:primaryType = "oak:QueryIndexDefinition"
 *      - compatVersion = 2
 *      - type = "lucene"
 *      - async = "async"
 *      + indexRules
 *          - jcr:primaryType = "nt:unstructured"
 *           + nt:base
 *               + properties
 *                   - jcr:primaryType = "nt:unstructured"
 *                   + assetType
 *                       - propertyIndex = true
 *                       - name = "assetType"
 *  </pre>
 */
class CndExporter {

    String toCNDFormat(NodeState state){
        def result = new StringBuilder()
        copyNode(state, result, 0)
        return result.toString()
    }

    private static void copyNode(NodeState state, StringBuilder buffer, int depth) {
        copyProperties(state, buffer, depth)
        state.childNodeEntries.each { ChildNodeEntry cne ->
            buffer << Strings.repeat(" ", depth + 1) + " + ${cne.name} (${cne.nodeState.getName("jcr:primaryType")})\n"
            copyNode(cne.nodeState, buffer, depth + 1)
        }
    }

    private static void copyProperties(NodeState state, StringBuilder buffer, int depth) {
        state.properties.each { PropertyState ps ->
            if (ps.name == 'jcr:primaryType'){
                return
            }

            String value = ps.getValue(ps.getType()).toString()
            if (ps.type == Type.STRING){
                value = "\"$value\""
            }

            buffer << Strings.repeat(" ", depth + 1) + " - ${ps.name} = $value\n"
        }
    }
}
