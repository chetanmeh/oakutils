package com.chetanmeh.oak.state.export

import org.apache.jackrabbit.oak.spi.state.NodeState


class NodeStateExporter {

    static String toJson(NodeState state){
        return new JsonExporter().toJson(state)
    }

    static Map toMap(NodeState state){
        return new JsonExporter().toMap(state)
    }

    static String toCND(NodeState state){
        return new CndExporter().toCNDFormat(state)
    }

    static String toXml(NodeState state){
        return new XmlExporter().toXml(state)
    }
}
