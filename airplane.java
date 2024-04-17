import java.util.Stack;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

interface AirlineGraph {
    static final int SIZE = 10;
    static final String airportCode[] = { "BOS", "CHI", "DFW", "DEN", "HNL", "IAH", "MIA", "JFK", "PHX", "SFO" };
    static final String city[] = { "Boston, MA", "Chicago, IL", "Dallas-Ft Worth, TX", "Denver, CO", "Honolulu, HI", "Houston, TX", "Miami, FL", "New York, NY", "Phoenix, AX", "San Francisco, CA" };
    abstract String findRoute(int length, String start, String end);
}

public class Graph implements AirlineGraph {

    private int[][] graph; // A two-dimensional adjacency matrix.
    private Stack<Integer> stack; // A last in, first out data structure.

    public Graph() {
        graph = new int[AirlineGraph.SIZE][AirlineGraph.SIZE];
        stack = new Stack<>();
        fillGraph();
    }

    private void fillGraph() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("connections.dat"));
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                for (int col = 0; col < parts.length; col++) {
                    graph[row][col] = Integer.parseInt(parts[col]);
                }
                row++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    initializeGraphFromFile("connections.dat");
    }

    private void initializeGraphFromFile(String filename) {
        try {
            Scanner scanner = new Scanner(new File(filename));
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE && scanner.hasNextInt(); col++) {
                    graph[row][col] = scanner.nextInt();
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


initializeGraphFromFile("connections.dat");
    }

    private void initializeGraphFromFile(String filename) {
        try {
            Scanner scanner = new Scanner(new File(filename));
            for (int row = 0; row < AirlineGraph.SIZE; row++) {
                for (int col = 0; col < AirlineGraph.SIZE; col++) {
                    if (scanner.hasNextInt()) {
                        graph[row][col] = scanner.nextInt();
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
*/
    private int findAirportCode(String airportCode) {
        for (int i = 0; i < AirlineGraph.SIZE; i++) {
            if (AirlineGraph.airportCode[i].equals(airportCode)) {
                return i;
            }
        }
        return -1; // Not found
    }

    private boolean adjacent(Point edge) {
        return graph[edge.x][edge.y] > 0;
    }

    private boolean findPath(int length, Point p) {
        if (length == 1) {
            if (adjacent(p)) {
                stack.push(p.y); // Push the ending city onto the stack
                return true;
            } else {
                for (int node = 0; node < AirlineGraph.SIZE; node++) {
                    if (graph[p.x][node] > 0 && findPath(length - 1, new Point(node, p.y))) {
                        stack.push(p.x); // Push the current city/node onto the stack
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int[] shortestPath(String source) {
        int[] dist = new int[AirlineGraph.SIZE];
        boolean[] visited = new boolean[AirlineGraph.SIZE];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[findAirportCode(source)] = 0;
        for (int count = 0; count < AirlineGraph.SIZE - 1; count++) {
            int u = minDistance(dist, visited);
            visited[u] = true;
            for (int v = 0; v < AirlineGraph.SIZE; v++) {
                if (!visited[v] && graph[u][v] != 0 && dist[u] != Integer.MAX_VALUE && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }
        return dist;
    }

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

    public String cheapestRoute(String start, String end) {
        int[] dist = shortestPath(start);
        return findRoute(dist[findAirportCode(end)], start, end);
    }

    public String findRoute(int length, String start, String end) {
        stack.clear();
        int startIndex = findAirportCode(start);
        int endIndex = findAirportCode(end);
        if (findPath(length, new Point(startIndex, endIndex))) {
            StringBuilder route = new StringBuilder(AirlineGraph.city[startIndex]);
            while (!stack.isEmpty()) {
                route.append(" -> ").append(AirlineGraph.city[stack.pop()]);
            }
            return route.toString() + " Total fare: $" + graph[startIndex][endIndex];
        } else {
            return "There is no such connection!";
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Airline Graph\n");
        for (int i = 0; i < AirlineGraph.SIZE; i++) {
            for (int j = 0; j < AirlineGraph.SIZE; j++) {
                sb.append(graph[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
