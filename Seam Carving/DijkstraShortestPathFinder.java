package graphs.shortestpaths;

import priorityqueues.ExtrinsicMinPQ;
import priorityqueues.NaiveMinPQ;
import graphs.BaseEdge;
import graphs.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    private Map<V, E> edgeTo;
    private Map<V, Double> distTo;

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new NaiveMinPQ<>();
    }

    @Override
    public Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        edgeTo = new HashMap<V, E>();
        distTo = new HashMap<V, Double>();

        if (Objects.equals(start, end)) {
            return edgeTo;
        }

        ExtrinsicMinPQ<V> priorityQueue = createMinPQ();
        priorityQueue.add(start, 0);
        distTo.put(start, 0.0);

        while (!priorityQueue.isEmpty())
        {
            V current = priorityQueue.removeMin();
            if (Objects.equals(current, end))
            {
                break;
            }

            for (E edge : graph.outgoingEdgesFrom(current)) {
                V nextVertex = edge.to();       // Neighbor
                double weight = edge.weight();

                if (!distTo.containsKey(nextVertex))
                {
                    distTo.put(nextVertex, Double.POSITIVE_INFINITY);
                }

                double oldDist = distTo.get(nextVertex);
                double newDist = distTo.get(current) + weight;

                if (newDist < oldDist)
                {
                    distTo.put(nextVertex, newDist);
                    edgeTo.put(nextVertex, edge);

                    if (priorityQueue.contains(nextVertex)) {
                        priorityQueue.changePriority(nextVertex, newDist);
                    } else {
                        priorityQueue.add(nextVertex, newDist);
                    }
                }
            }
        }
        return edgeTo;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        List<E> edges = new ArrayList<>();

        if (Objects.equals(start, end))
        {
            return new ShortestPath.SingleVertex<>(start);
        }

        if (spt.get(end) == null)
        {
            return new ShortestPath.Failure<>();
        }

        V current = end;
        List<V> visited = new ArrayList<>();
        while (spt.get(current) != null)
        {
            E edge = spt.get(current);
            visited.add(current);
            current = edge.from();
            edges.add(edge);
        }
        Collections.reverse(edges);
        return new ShortestPath.Success<>(edges);
    }
}
