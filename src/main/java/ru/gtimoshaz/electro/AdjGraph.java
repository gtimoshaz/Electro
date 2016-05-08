package ru.gtimoshaz.electro;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by george on 5/7/16.
 */
public class AdjGraph {

    class Edge {
        int to;
        float length;
        EComponentType type;

        public Edge(int to, float length, EComponentType type) {
            this.to = to;
            this.length = length;
            this.type = type;
        }
    }

    private int vCount;

    private ArrayList<Edge>[] adj;
    public int getVertexCount() {
        return vCount;
    }
    ArrayList<AdjGraph> getCycles() {
        return new ArrayList<>();
    }

    public AdjGraph(int vCount) {
        this.vCount = vCount;
        adj = new ArrayList[vCount];
        for (int i = 0; i < vCount; i++) {
            adj[vCount] = new ArrayList<>();
        }
    }

    public void addEdge(int from, int to, float length, EComponentType eComponentType) {
        adj[from].add(new Edge(to, length, eComponentType));
        if (eComponentType == EComponentType.RESISTOR)
            adj[to].add(new Edge(from, length, eComponentType));
    }

}
