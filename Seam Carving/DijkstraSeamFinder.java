
package seamcarving;

import graphs.Edge;
import graphs.Graph;
import graphs.shortestpaths.DijkstraShortestPathFinder;
//import graphs.shortestpaths.ShortestPath;
import graphs.shortestpaths.ShortestPathFinder;

import java.util.Collection;
import java.util.Collections;
//import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
//import java.util.Map;
import java.util.Objects;

public class DijkstraSeamFinder implements SeamFinder {
    private final ShortestPathFinder<Graph<MyVertex, Edge<MyVertex>>, MyVertex, Edge<MyVertex>> pathFinder;

    public DijkstraSeamFinder() {
        this.pathFinder = createPathFinder();
    }

    private static class MyVertex {
        // Note: use "static" in the class header if it doesn't use any non-static fields
        // in the outer class. Otherwise, remove the static keyword.
        private int row;
        private int col;
        public MyVertex(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof MyVertex)) {
                return false;
            }

            MyVertex o = (MyVertex) other;
            if ((this.col == o.col) && (this.row == o.row))
            {
                return true;
            }
            return false;
        }
        @Override
        public int hashCode() {
            // See Objects.hash:
            // https://docs.oracle.com/javase/8/docs/api/java/util/Objects.html#hash-java.lang.Object...-
            return Objects.hash(row, col);
        }
    }

    private class MyGraph implements graphs.Graph<MyVertex, Edge<MyVertex>> {
        // Note: use "static" in the class header if it doesn't use any non-static fields
        // in the outer class. Otherwise, remove the static keyword.
        // fields
        private double[][] picture;

        public MyGraph(double[][] energies) {
            picture = energies; // energies[y][x]
        }

        @Override
        public Collection<Edge<MyVertex>> outgoingEdgesFrom(MyVertex vertex) {
            if (picture == null)
            {
                return Collections.emptyList();
            }
            int numCols = picture[0].length;    // # of rows in actual image
            int numRows = picture.length;       // # of cols in actual image
            Collection<Edge<MyVertex>> edges = new ArrayList<>();
            //MyVertex dummyStart = new MyVertex(-1, -1);

            MyVertex current = vertex;
            if (current.row == -1 && current.col == -1)  // dummy start
            {
                for (int i = 0; i < numRows; i++)
                {
                    MyVertex destination = new MyVertex(i, 0);
                    edges.add(new Edge<>(current, destination, picture[i][0]));
                }
            }
            else if (current.col == numCols - 1)    // dummy end
            {
                MyVertex dummyEnd = new MyVertex(-1, numCols - 1);
                edges.add(new Edge<>(current, dummyEnd, 0));
            }
            else if (current.row == 0)
            {
                MyVertex destination = new MyVertex(0, current.col + 1);
                edges.add(new Edge<>(current, destination, picture[0][current.col + 1]));
                destination = new MyVertex(1, current.col + 1);
                edges.add(new Edge<>(current, destination, picture[1][current.col + 1]));
            }
            else if (current.row == numRows - 1)
            {
                MyVertex destination = new MyVertex(numRows - 2, current.col + 1);
                edges.add(new Edge<>(current, destination, picture[numRows - 2][current.col + 1]));
                destination = new MyVertex(numRows - 1, current.col + 1);
                edges.add(new Edge<>(current, destination, picture[numRows - 1][current.col + 1]));
            }
            else
            {
                MyVertex destination = new MyVertex(current.row - 1, current.col + 1);
                edges.add(new Edge<>(current, destination, picture[current.row - 1][current.col + 1]));
                destination = new MyVertex(current.row, current.col + 1);
                edges.add(new Edge<>(current, destination, picture[current.row][current.col + 1]));
                destination = new MyVertex(current.row + 1, current.col + 1);
                edges.add(new Edge<>(current, destination, picture[current.row + 1][current.col + 1]));
            }
            return edges;
        }
    }

    protected <G extends Graph<V, Edge<V>>, V> ShortestPathFinder<G, V, Edge<V>> createPathFinder() {
        /*
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
        */
        return new DijkstraShortestPathFinder<>();
    }

    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        double[][] transposed = new double[energies[0].length][energies.length];
        for (int i = 0; i < energies.length; i++)
        {
            for (int j = 0; j < energies[0].length; j++)
            {
                transposed[j][i] = energies[i][j];
            }
        }
        return seamHelper(transposed);
    }

    private List<Integer> seamHelper(double[][] energies) {
        MyGraph G = new MyGraph(energies);
        MyVertex dummyStart = new MyVertex(-1, -1);
        MyVertex dummyEnd = new MyVertex(-1, energies[0].length - 1);
        List<MyVertex> spt = pathFinder.findShortestPath(G, dummyStart, dummyEnd).vertices();
        spt.remove(0);
        //spt.remove(spt.size() - 1);
        List<Integer> horizontalSeam = new ArrayList<>(spt.size() - 1);
        for (int i = 0; i < spt.size() - 1; i++) {
            horizontalSeam.add(spt.get(i).row);
        }
        return horizontalSeam;
    }
    // x for horizontal, y for vertical
    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        return seamHelper(energies);
    }
}
