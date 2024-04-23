import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;
// chichi nwosu
// 4th period
public class Graph {

    private String[][] graph; // A two-dimensional adjacency matrix.
    private Stack<Integer> stack; // A last in, first out data structure.

    // Constructor
    public Graph() throws FileNotFoundException {
        // Initialize graph and stack
        graph = new String[AirlineGraph.SIZE][AirlineGraph.SIZE];
        stack = new Stack<>();

        // Initialize graph with "-" indicating no connection
        for (int i = 0; i < AirlineGraph.SIZE; i++) {
            Arrays.fill(graph[i], "-");
        }

        // Load data from connections.dat file to populate the graph
        loadGraphData();
    }

    // Load data from connections.dat file to populate the graph
    private void loadGraphData() throws FileNotFoundException {
        File file = new File("connections.dat");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            int fromIndex = getIndex(parts[0]);
            int toIndex = getIndex(parts[1]);
            int cost = Integer.parseInt(parts[2]);
            graph[fromIndex][toIndex] = String.valueOf(cost);
        }
        scanner.close();
    }

    // Get the index of an airport code from airportCode array
    private int getIndex(String airportCode) {
        for (int i = 0; i < AirlineGraph.SIZE; i++) {
            if (AirlineGraph.airportCode[i].equals(airportCode)) {
                return i;
            }
        }
        return -1; // Not found
    }

    // Returns true if Point edge is connected, false otherwise.
    private boolean adjacent(Point edge) {
        return !graph[edge.x][edge.y].equals("-");
    }

    // Find a path of a given length between two points
    private boolean findPath(int length, Point p) {
        if (length == 1) {
            if (adjacent(p)) {
                stack.push(p.y); // Push the ending city onto the stack
                return true;
            } else {
                for (int node = 0; node < AirlineGraph.SIZE; node++) {
                    if (!graph[p.x][node].equals("-") && findPath(length - 1, new Point(node, p.y))) {
                        stack.push(p.x); // Push the current city/node onto the stack
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Find the shortest path from a source city to every other city
    public int[] shortestPath(String source) {
        int[] dist = new int[AirlineGraph.SIZE];
        boolean[] visited = new boolean[AirlineGraph.SIZE];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[getIndex(source)] = 0;
        for (int count = 0; count < AirlineGraph.SIZE - 1; count++) {
            int u = minDistance(dist, visited);
            visited[u] = true;
            for (int v = 0; v < AirlineGraph.SIZE; v++) {
                if (!visited[v] && !graph[u][v].equals("-") && dist[u] != Integer.MAX_VALUE
                        && dist[u] + Integer.parseInt(graph[u][v]) < dist[v]) {
                    dist[v] = dist[u] + Integer.parseInt(graph[u][v]);
                }
            }
        }
        return dist;
    }

    // Find the vertex with the minimum distance value
    private int minDistance(int[] dist, boolean[] visited) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int v = 0; v < AirlineGraph.SIZE; v++) {
            if (!visited[v] && dist[v] <= min) {
                min = dist[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    // Find the cheapest route from start to end
    public String cheapestRoute(String start, String end) {
        int[] dist = shortestPath(start);
        return findRoute(dist[getIndex(end)], start, end);
    }

    // Find a route with a specified length from start to end
    public String findRoute(int length, String start, String end) {
        stack.clear();
        int startIndex = getIndex(start);
        int endIndex = getIndex(end);
        if (findPath(length, new Point(startIndex, endIndex))) {
            StringBuilder route = new StringBuilder(AirlineGraph.city[startIndex]);
            while (!stack.isEmpty()) {
                route.append(" -> ").append(AirlineGraph.city[stack.pop()]);
            }
            return route.toString() + " Total fare: $" + graph[startIndex][endIndex];
        }
        return "There is no such connection!";
    }

    // Returns a String representation of this Graph object.
    public String toString() {
        StringBuilder sb = new StringBuilder("Airline Graph\n");
        for (int i = 0; i < AirlineGraph.SIZE; i++) {
            for (int j = 0; j < AirlineGraph.SIZE; j++) {
                sb.append(graph[i][j]).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
