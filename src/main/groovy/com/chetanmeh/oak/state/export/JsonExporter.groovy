package com.chetanmeh.oak.state.export

import groovy.json.JsonOutput
import org.apache.jackrabbit.oak.api.PropertyState
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry
import org.apache.jackrabbit.oak.spi.state.NodeState


class JsonExporter {

    public String toJson(NodeState state){
        return JsonOutput.prettyPrint(JsonOutput.toJson(toMap(state)))
    }

    public Map toMap(NodeState state){
        def result = [:]
        return copyNode(state, result)
    }

    private static Map copyNode(NodeState state, Map result){
        copyProperties(state, result)
        state.childNodeEntries.each {ChildNodeEntry cne ->
            def nodeMap = [:]
            result.put(cne.name, nodeMap)
            copyNode(cne.nodeState, nodeMap)
        }
        return result
    }

    private static Map copyProperties(NodeState state, Map map) {
        state.properties.each {PropertyState ps ->
            map.put(ps.name, ps.getValue(ps.getType()))
        }
        return map
    }
}
