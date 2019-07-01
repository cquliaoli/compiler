package regular;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liaoli
 * date: 2019/6/25
 *
 * @author: liaoli
 */
public class FaGraph {


    private static final char E = 'ε';

    private Node start;

    private Node end;

    private static int pos = 0;

    private Set<Character> characterSet = new HashSet<>();

    private String input;

    public  FaGraph cloneGraph(){
        FaGraph faGraph = new FaGraph();
        faGraph.setCharacterSet(characterSet);
        faGraph.setInput(input);
        faGraph.setStart(start);
        faGraph.setEnd(end);
        return faGraph;
    }

    public static FaGraph fromChar(char ch) {
        FaGraph faGraph = new FaGraph();
        Node start = new Node(pos++);
        Node end = new Node(pos++);
        Edge.fromNode(start, end, false, ch);
        faGraph.setEnd(end);
        faGraph.setStart(start);

        return faGraph;
    }

    public static FaGraph reduceCat(FaGraph a, FaGraph b) {
        FaGraph faGraph = new FaGraph();
        faGraph.setStart(a.getStart());
        faGraph.setEnd(b.getEnd());
        Edge.fromNode(a.getEnd(), b.getStart(), true, E);
        return faGraph;
    }

    public static FaGraph reduceAlt(FaGraph a, FaGraph b) {
        FaGraph faGraph = new FaGraph();
        Node start = new Node(pos++);
        Node end = new Node(pos++);
        faGraph.setEnd(end);
        faGraph.setStart(start);
        Edge.fromNode(start, a.getStart(), true, E);
        Edge.fromNode(start, b.getStart(), true, E);
        Edge.fromNode(a.getEnd(), end, true, E);
        Edge.fromNode(b.getEnd(), end, true, E);
        return faGraph;
    }

    public static FaGraph reduceStar(FaGraph a) {
        FaGraph faGraph = new FaGraph();
        Node start = new Node(pos++);
        Node end = new Node(pos++);
        faGraph.setEnd(end);
        faGraph.setStart(start);
        Edge.fromNode(start, a.getStart(), true, E);
        Edge.fromNode(a.getEnd(), end, true, E);
        Edge.fromNode(start, end, true, E);
        Edge.fromNode(a.getEnd(), a.getStart(), true, E);
        return faGraph;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Set<Character> getCharacterSet() {
        return characterSet;
    }

    public void setCharacterSet(Set<Character> characterSet) {
        this.characterSet = characterSet;
    }

    public Node getStart() {
        return start;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public Node getEnd() {
        return end;
    }

    public void setEnd(Node end) {
        this.end = end;
    }

    public String toGraphViz() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%s%n", "rankdir=LR;"));
        sb.append(String.format("%s%n", "size=\"5,3\""));
        sb.append(String.format("%s [style=filled;color=lightgrey,shape = circle];%n",start.getPosition()));
        sb.append(String.format("%s %s;%n", "node [shape = doublecircle];",end.getPosition()));
        sb.append(String.format("%s%s [shape=polygon,style=filled;color=lightgrey;sides=4,label=\"%s\"]%n",
                "t",FaGraph.pos++, input));
        dfs(sb, start, new HashSet<>());
        return sb.toString();
    }
    public String dfaToGraphViz(){
        Set<Integer>accepted = new HashSet<>();
        dfs(start,accepted,new HashSet<>());
        StringBuffer sb = new StringBuffer();
        sb.append("      rankdir=LR;\n" +
                "      size=\"5,3\"\n" +
                "      "+start.getPosition()+" [style=filled;color=lightgrey,shape = circle];\n"+
                "      node [shape = doublecircle];" + StringUtils.join(accepted," ") +";\n"+
                "      node [shape = circle];\n");
        sb.append(String.format("      t%s [shape=polygon,style=filled;color=lightgrey;" +
                "sides=4,label=\"%s\"]\n",FaGraph.pos++, input));
        dfs(sb, start, new HashSet<>());
        return sb.toString();
    }

    private void dfs(Node a,Set<Integer>posSet,Set<Node>added){
        added.add(a);
        if(a.getOutEdges()!=null){
            for (Edge outEdge : a.getOutEdges()) {
                if(outEdge.getEnd().isAccepted()){
                    posSet.add(outEdge.getEnd().getPosition());
                }
                if(!added.contains(outEdge.getEnd())){

                    dfs(outEdge.getEnd(),posSet,added);
                }
            }
        }
    }
    private void dfs(StringBuffer sb, Node a, Set<Edge> addedEdges) {
        if (a.getOutEdges() != null) {
            for (Edge outEdge : a.getOutEdges()) {
                // 	LR_2 -> LR_4 [ label = "S(A)" ];
                if (!addedEdges.contains(outEdge)) {
                    String ch = String.valueOf(outEdge.getCh());
                    sb.append(String.format("%10s->%s [ label =\"%s\"]%n", a.getPosition(), outEdge.getEnd().getPosition(),
                            ch));
                    addedEdges.add(outEdge);
                    dfs(sb, outEdge.getEnd(), addedEdges);
                }

            }
        }
    }
    public static void getAcceptedNode(Node a ,Set<Node>accepted, Set<Node>unaccepted){
        if(accepted.contains(a)||unaccepted.contains(a)){
            return;
        }
        if(a.isAccepted()){
            accepted.add(a);
        }else{
            unaccepted.add(a);
        }
        if(a.getOutEdges()!=null){
            for (Edge outEdge : a.getOutEdges()) {
                getAcceptedNode(outEdge.getEnd(),accepted,unaccepted);
            }
        }
    }
}
