package com.chetanmeh.oak.state.export;

import com.google.common.base.Strings;
import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.api.Type;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeState;

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
public class CndExporter {

    public String toCNDFormat(NodeState state) {
        StringBuilder result = new StringBuilder();
        copyNode(state, result, 0);
        return result.toString();
    }

    private static void copyNode(NodeState state, StringBuilder buffer, int depth) {
        copyProperties(state, buffer, depth);
        for (ChildNodeEntry cne : state.getChildNodeEntries()) {
            String primaryType = cne.getNodeState().getName("jcr:primaryType");
            String typeText = !primaryType.equals("nt:unstructured") ? "(" + primaryType + ")" : "";
            buffer.append(Strings.repeat(" ", depth + 1)).append(" + ").append(cne.getName()).append(" ").append(typeText).append("\n");
            copyNode(cne.getNodeState(), buffer, depth + 1);
        }
    }

    private static void copyProperties(NodeState state, StringBuilder buffer, int depth) {
        for (PropertyState ps : state.getProperties()) {
            String value = ps.getValue(ps.getType()).toString();
            if (ps.getName().equals("jcr:primaryType") && value.equals("nt:unstructured")) {
                continue;
            }

            if (ps.getType() == Type.STRING) {
                value = "\"" + value + "\"";
            }

            buffer.append(Strings.repeat(" ", depth + 1)).append(" - ").append(ps.getName()).append(" = ").append(value).append("\n");
        }
    }
}

