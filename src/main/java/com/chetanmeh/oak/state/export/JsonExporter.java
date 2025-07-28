package com.chetanmeh.oak.state.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.commons.json.JsopBuilder;
import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeState;

public class JsonExporter {

    public String toJson(NodeState state) {
        JsopBuilder builder = new JsopBuilder();
        Map<String, Object> map = toMap(state);
        write(map, builder);
        return JsopBuilder.prettyPrint(builder.toString());
    }

    private static void write(Map<String, Object> map, JsopBuilder target) {
        target.object();
        ArrayList<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            Object value = map.get(key);
            if (value == null || !(value instanceof Map)) {
                target.key(key);
                writeObject(value, target);
            }
        }
        for (String key : keys) {
            Object value = map.get(key);
            if (value instanceof Map) {
                target.key(key);
                writeObject(value, target);
            }
        }
        target.endObject();
    }

    @SuppressWarnings("unchecked")
    private static void writeObject(Object value, JsopBuilder target) {
        if (value == null) {
            target.value(null);
        } else if (value instanceof Boolean) {
            target.value((Boolean) value);
        } else if (value instanceof Integer) {
            target.value((Integer) value);
        } else if (value instanceof Long) {
            target.value((Long) value);
        } else if (value instanceof Map) {
            write((Map<String, Object>) value, target);
        } else if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            target.array();
            for (Object o : list) {
                writeObject(o, target);
            }
            target.endArray();
        } else {
            target.value(value.toString());
        }
    }

    public Map<String, Object> toMap(NodeState state) {
        Map<String, Object> result = new HashMap<>();
        return copyNode(state, result);
    }

    private static Map<String, Object> copyNode(NodeState state, Map<String, Object> result) {
        copyProperties(state, result);
        for (ChildNodeEntry cne : state.getChildNodeEntries()) {
            Map<String, Object> nodeMap = new HashMap<>();
            result.put(cne.getName(), nodeMap);
            copyNode(cne.getNodeState(), nodeMap);
        }
        return result;
    }

    private static Map<String, Object> copyProperties(NodeState state, Map<String, Object> map) {
        for (PropertyState ps : state.getProperties()) {
            map.put(ps.getName(), ps.getValue(ps.getType()));
        }
        return map;
    }
}
