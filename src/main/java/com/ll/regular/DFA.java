package com.ll.regular;

import java.util.*;

/**
 * Created by liaoli
 * date: 2019/6/25
 * time: 21:09
 *
 * @author: liaoli
 */
public class DFA {

    public static void main(String[] args) {

    }

    /**
     * 计算闭包
     *
     * @param startNodes
     * @return
     */
    public Set<Node> closure(Set<Node> startNodes) {
        Set<Node> result = new HashSet<>(startNodes);

        Stack<Node> tmp = new Stack<>();
        for (Node startNode : startNodes) {
            tmp.push(startNode);
        }
        while (!tmp.isEmpty()) {
            Node next = tmp.pop();
            if (next.getOutEdges() != null) {
                for (Edge outEdge : next.getOutEdges()) {
                    if (outEdge.iseTransition() && !result.contains(outEdge.getEnd())) {
                        result.add(outEdge.getEnd());
                        tmp.push(outEdge.getEnd());
                    }
                }
            }

        }
        return result;
    }

    public Set<Node> move(Set<Node> nodes, char ch) {
        Set<Node> result = new HashSet<>();
        for (Node node : nodes) {
            if (node.getOutEdges() != null) {
                for (Edge outEdge : node.getOutEdges()) {
                    if (ch == outEdge.getCh()) {
                        result.add(outEdge.getEnd());
                    }
                }
            }
        }
        return result;

    }

    public FaGraph subsetCons(FaGraph faGraph) {
        Map<Set<Node>, Node> all = new HashMap<>();
        Stack<Node> toProcess = new Stack<>();
        Set<Node> init = new HashSet<>();
        init.add(faGraph.getStart());
        Set<Node> initSets = closure(init);
        Node startNode = Node.fromNodes(initSets);
        startNode.setConstructNodeSet(initSets);
        startNode.setStart(true);
        FaGraph dfa = new FaGraph();
        dfa.setCharacterSet(faGraph.getCharacterSet());
        dfa.setStart(startNode);
        dfa.setInput(faGraph.getInput());
        toProcess.push(startNode);
        while (!toProcess.isEmpty()) {
            Node pop = toProcess.pop();
            all.put(pop.getConstructNodeSet(), pop);
            for (Character character : faGraph.getCharacterSet()) {
                Set<Node> move = move(pop.getConstructNodeSet(), character);
                Set<Node> closure = closure(move);
                if (!closure.isEmpty()) {
                    Node node = null;
                    Node oldNode = all.get(closure);
                    if (oldNode != null) {
                        node = oldNode;
                    } else {
                        node = Node.fromNodes(closure);
                        toProcess.push(node);
                        if (node.getConstructNodeSet().contains(faGraph.getEnd())) {
                            node.setAccepted(true);
                        }
                    }
                    Edge.fromNode(pop, node, false, character);
                }
            }
        }
        return dfa;
    }

    private Node transform(Node node, Character ch) {
        if (node.getOutEdges() == null) {
            return null;
        }
        for (Edge outEdge : node.getOutEdges()) {
            if (ch == outEdge.getCh()) {
                return outEdge.getEnd();
            }
        }
        return null;
    }

    public FaGraph hopcroft(FaGraph faGraph) {
        Stack<Set<Node>> partitionSet = new Stack<>();
        Set<Set<Node>> newPartitionSet = new HashSet<>();
        // 每个状态到状态集合的映射
        Map<Node, Set<Node>> nodeSetMap = new HashMap<>();
        Set<Node> acceptedSet = new HashSet<>();
        Set<Node> unacceptedSet = new HashSet<>();
        // 获取初始划分
        FaGraph.getAcceptedNode(faGraph.getStart(), acceptedSet, unacceptedSet);
        partitionSet.push(acceptedSet);
        partitionSet.push(unacceptedSet);
        acceptedSet.forEach(x -> nodeSetMap.put(x, acceptedSet));
        unacceptedSet.forEach(x -> nodeSetMap.put(x, unacceptedSet));
        while (!partitionSet.isEmpty()) {
            Set<Node> nodes = partitionSet.pop();
            if (nodes.size() == 1) {
                newPartitionSet.add(nodes);
            } else {
                boolean partition = false;
                next:
                for (Character ch : faGraph.getCharacterSet()) {
                    // 每个状态到状态集合的映射
                    Map<Node, Set<Node>> tmp = new HashMap<>();
                    // 状态集合 -> 划分组
                    Map<Set<Node>, Set<Node>> map = new HashMap<>();
                    for (Node node : nodes) {
                        Node transform = transform(node, ch);
                        if (transform == null) {
                            if (map.containsKey(null)) {
                                map.get(null).add(node);
                                tmp.put(node, map.get(null));
                            } else {
                                Set<Node> nodeSet = new HashSet<>();
                                nodeSet.add(node);
                                map.put(null, nodeSet);
                                tmp.put(node, nodeSet);
                            }
                        } else {
                            // 从映射里面找，包含全集，必然存在
                            Set<Node> nodeSet = nodeSetMap.get(transform);
                            Set<Node> orDefault = map.getOrDefault(nodeSet, new HashSet<>());
                            orDefault.add(node);
                            if (!map.containsKey(nodeSet)) {
                                map.put(nodeSet, orDefault);
                            }
                            tmp.put(node, orDefault);
                        }
                    }
                    if (map.size() > 1) {
                        // 存在划分
                        // 删除nodeSetMap中受影响的元素
                        for (Node node : nodes) {
                            nodeSetMap.remove(node);
                        }
                        // 添加新的映射
                        nodeSetMap.putAll(tmp);
                        // 继续划分
                        for (Map.Entry<Set<Node>, Set<Node>> entry : map.entrySet()) {
                            partitionSet.push(entry.getValue());
                        }
                        partition = true;
                        break next;
                    }
                }
                if (!partition) {
                    newPartitionSet.add(nodes);
                }

            }
        }
        return constructMinimalDFA(faGraph.cloneGraph(), newPartitionSet);
    }

    private FaGraph constructMinimalDFA(FaGraph faGraph, Set<Set<Node>> newPartitionSet) {
        Map<Node, Set<Node>> nodeToSet = new HashMap<>();
        Map<Node, Node> oldToNewMap = new HashMap<>();
        Node start = null;
        for (Set<Node> nodeSet : newPartitionSet) {
            Node newNode = Node.fromNodes(nodeSet);

            for (Node oldNode : nodeSet) {
                if (oldNode.isStart()) {
                    start = oldNode;
                    faGraph.setStart(newNode);
                    newNode.setStart(true);
                }
                if (oldNode.isAccepted()) {
                    faGraph.setEnd(newNode);
                    newNode.setAccepted(true);
                }
                oldToNewMap.put(oldNode, newNode);
                nodeToSet.put(oldNode, nodeSet);
            }
        }
        dfs(start, nodeToSet, oldToNewMap, new HashSet<>());
        return faGraph;
    }

    private void dfs(Node start, Map<Node, Set<Node>> nodeToSetMap, Map<Node, Node> oldToNewMap, Set<Set<Node>> added) {
        Set<Node> nodeSet = nodeToSetMap.get(start);
        if (added.contains(nodeSet)) {
            return;
        }
        added.add(nodeSet);
        if (start.getOutEdges() != null) {
            Node newStartNode = oldToNewMap.get(start);
            for (Edge outEdge : start.getOutEdges()) {
                Node end = outEdge.getEnd();
                // Set<Node> nodeSet1 = nodeToSetMap.get(end);
                Node newEnd = oldToNewMap.get(end);
                Edge.fromNode(newStartNode, newEnd, false, outEdge.getCh());
                dfs(end, nodeToSetMap, oldToNewMap, added);
            }
        }
    }
}
