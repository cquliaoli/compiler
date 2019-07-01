package com.ll.regular;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by liaoli
 * date: 2019/6/25
 *
 * @author: liaoli
 */
public class Edge {

    private Node start;

    private Node end;

    private char ch;

    private boolean eTransition;

    public static Edge fromNode(Node start, Node end, boolean eTransition, char ch) {
        Edge edge = new Edge();
        edge.setStart(start);
        edge.setEnd(end);
        if (eTransition) {
            edge.seteTransition(true);
            edge.setCh(ch);
        } else {
            edge.setCh(ch);
        }
        if (start.getOutEdges() == null) {
            start.setOutEdges(new ArrayList<>());
        }
        start.getOutEdges().add(edge);
        if (end.getInputEdges() == null) {
            end.setInputEdges(new ArrayList<>());
        }
        end.getInputEdges().add(edge);
        return edge;
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

    public char getCh() {
        return ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    public boolean iseTransition() {
        return eTransition;
    }

    public void seteTransition(boolean eTransition) {
        this.eTransition = eTransition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return ch == edge.ch &&
                eTransition == edge.eTransition &&
                Objects.equals(start, edge.start) &&
                Objects.equals(end, edge.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, ch, eTransition);
    }
}
