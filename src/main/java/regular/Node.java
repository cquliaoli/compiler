package regular;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by liaoli
 * date: 2019/6/25
 * time: 9:35
 *
 * @author: liaoli
 */
public class Node {

    public static int index=100;
    /**
     * 是否是开始态
     */
    private boolean start;
    /**
     * 是否是接收态
     */
    private boolean accepted;
    /**
     * 状态
     */
    private int position;
    /**
     * 入度
     */
    private List<Edge>inputEdges;
    /**
     * 出度
     */
    private List<Edge>outEdges;


    /**
     * 由哪些节点合并
     */
    private Set<Integer> constructPosSet;

    private Set<Node> constructNodeSet;

    public static Node fromNodes(Set<Node>nodes){
        Node node = new Node(index++);
        node.setConstructPosSet(new HashSet<>());
        node.setConstructNodeSet(new HashSet<>());

        for (Node node1 : nodes) {
            node.getConstructPosSet().add(node1.position);
            node.getConstructNodeSet().add(node1);
        }
        return node;
    }

    public Node(int pos) {
        position=pos;
    }

    public Set<Node> getConstructNodeSet() {
        return constructNodeSet;
    }

    public void setConstructNodeSet(Set<Node> constructNodeSet) {
        this.constructNodeSet = constructNodeSet;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public List<Edge> getInputEdges() {
        return inputEdges;
    }

    public void setInputEdges(List<Edge> inputEdges) {
        this.inputEdges = inputEdges;
    }

    public List<Edge> getOutEdges() {
        return outEdges;
    }

    public void setOutEdges(List<Edge> outEdges) {
        this.outEdges = outEdges;
    }

    public Set<Integer> getConstructPosSet() {
        return constructPosSet;
    }

    public void setConstructPosSet(Set<Integer> constructPosSet) {
        this.constructPosSet = constructPosSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return position == node.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }


}
