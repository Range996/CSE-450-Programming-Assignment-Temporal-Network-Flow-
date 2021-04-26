import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class Main {

    public static List<SingleFlightSchedule> flightSchedules = new ArrayList<SingleFlightSchedule>();
    public static List<Map<String, Map<Integer, List<Integer>>>> listOfAirports = new ArrayList<>();
    public static Map<String, Map<Integer, Integer>> listOfSchedules = new HashMap<>();


    public static void main(String[] args) throws FileNotFoundException {
        FlightSchedules.readSchedule();
        flightSchedules = FlightSchedules.returnSchedule();
        BuildGraph();

        int source = getSource();
        int target = getTarget();

        int numPoint = listOfAirports.size();
        int graph[][] = new int[numPoint][numPoint];
        int count = 0;
        for (Map<String, Map<Integer, List<Integer>>> airport : listOfAirports) {
            for (Map.Entry indexEntry : airport.entrySet()) {
                Map<Integer, List<Integer>> tempGraph = (Map<Integer, List<Integer>>) indexEntry.getValue();
                List<Integer> tempList = (List<Integer>) tempGraph.values().toArray()[0];
                int[] tempArray = tempList.stream().mapToInt(i -> i).toArray();
                for (int i = 0; i < numPoint; i++) {
                    graph[count][i] = tempArray[i];
                }
            }
            count++;
        }

//        for (int i = 0; i < numPoint; i++) {
//            for (int j = 0; j < numPoint; j++) {
//                System.out.print(graph[i][j] + " ");
//            }
//            System.out.println();
//        }
        int maxFlow = fordFulkerson(graph, source, target, numPoint);

        System.out.print(maxFlow);


        //#######################Important#########################//
        //#######################Important#########################//
        //#######################Important#########################//
        //If you want to see the graph of the network flow uncomment the below code, and check the "output.txt" in the file.
        printResult();
    }

    //the fordFulkerson Algorithm is too complex and I don't have time to build it from scratch.
    //Reference: https://www.geeksforgeeks.org/ford-fulkerson-algorithm-for-maximum-flow-problem/
    public static int fordFulkerson(int graph[][], int s, int t, int numPoint) {
        int u, v;

        // Create a residual graph and fill the residual
        // graph with given capacities in the original graph
        // as residual capacities in residual graph

        // Residual graph where rGraph[i][j] indicates
        // residual capacity of edge from i to j (if there
        // is an edge. If rGraph[i][j] is 0, then there is
        // not)
        int rGraph[][] = new int[numPoint][numPoint];

        for (u = 0; u < numPoint; u++)
            for (v = 0; v < numPoint; v++)
                rGraph[u][v] = graph[u][v];

        // This array is filled by BFS and to store path
        int parent[] = new int[numPoint];

        int max_flow = 0; // There is no flow initially

        // Augment the flow while tere is path from source
        // to sink
        while (bfs(rGraph, s, t, parent, numPoint)) {
            // Find minimum residual capacity of the edhes
            // along the path filled by BFS. Or we can say
            // find the maximum flow through the path found.
            int path_flow = Integer.MAX_VALUE;
            for (v = t; v != s; v = parent[v]) {
                u = parent[v];
                path_flow
                        = Math.min(path_flow, rGraph[u][v]);
            }

            // update residual capacities of the edges and
            // reverse edges along the path
            for (v = t; v != s; v = parent[v]) {
                u = parent[v];
                rGraph[u][v] -= path_flow;
                rGraph[v][u] += path_flow;
            }

            // Add path flow to overall flow
            max_flow += path_flow;
        }

        // Return the overall flow
        return max_flow;
    }


    public static boolean bfs(int rGraph[][], int s, int t, int parent[], int numPoint) {
        // Create a visited array and mark all vertices as
        // not visited
        boolean visited[] = new boolean[numPoint];
        for (int i = 0; i < numPoint; ++i)
            visited[i] = false;

        // Create a queue, enqueue source vertex and mark
        // source vertex as visited
        LinkedList<Integer> queue
                = new LinkedList<Integer>();
        queue.add(s);
        visited[s] = true;
        parent[s] = -1;

        // Standard BFS Loop
        while (queue.size() != 0) {
            int u = queue.poll();

            for (int v = 0; v < numPoint; v++) {
                if (visited[v] == false
                        && rGraph[u][v] > 0) {
                    // If we find a connection to the sink
                    // node, then there is no point in BFS
                    // anymore We just have to set its parent
                    // and can return true
                    if (v == t) {
                        parent[v] = u;
                        return true;
                    }
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }

        // We didn't reach sink in BFS starting from source,
        // so return false
        return false;
    }


    public static void BuildGraph() throws FileNotFoundException {
//        insertSource();
        for (SingleFlightSchedule singleFlightSchedule : flightSchedules) {
            int departTime = singleFlightSchedule.departTime;
            int arriveTime = singleFlightSchedule.arriveTime;
            int capacity = singleFlightSchedule.capacity;
            String departAirport = "";
            String arriveAirport = "";
            String departAirportName = singleFlightSchedule.departAirport;
            String arriveAirportName = singleFlightSchedule.arriveAirport;
            if (departTime > 9) {
                departAirport = departAirportName + departTime;
            } else {
                departAirport = departAirportName + "0" + departTime;
            }


            if (arriveTime > 9) {
                arriveAirport = arriveAirportName + arriveTime;
            } else {
                arriveAirport = arriveAirportName + "0" + arriveTime;
            }

//            System.out.println(departAirport + " " + arriveAirport);


//            int newAddGraph = 0;

            //insert the new depart graph

            insertDepart(departAirport, departAirportName, departTime);


            //insert the arrive graph

            insertArrive(arriveAirport, arriveAirportName, arriveTime);


            //add the new depart and arrive Airport graph into other Airports
            addAirport();

            //get the position of the arrive Airport
            int arrivePosition = getPosition(arriveAirport);

            //add the capacity to the depart Airport
            addCapacity(arrivePosition, departAirport, capacity);


        }

//        insertTarget();
//        addAirport(1);

        addInfiniteIntoSameAirport();

//        printResult();


    }


    public static void insertDepart(String departAirport, String departAirportName,
                                   int departTime) {
        int sizeOfAirports = listOfAirports.size();

        int count = 0;
        CHECK:
        for (Map<String, Map<Integer, List<Integer>>> airport : listOfAirports) {
            for (Map.Entry indexEntry : airport.entrySet()) {
                if (departAirport.equals(indexEntry.getKey())) {
                    break CHECK;
                } else {
                    count++;
                }

            }
        }
        if (count == sizeOfAirports) {
            //insert the depart Airport Position into list
            Map<String, Map<Integer, List<Integer>>> departAirportMap = new HashMap<>();

            //inner depart Map
            Map<Integer, List<Integer>> departAirportPosition = new HashMap<>();
            Integer departPosition = listOfAirports.size();
            int departGraphLength = listOfAirports.size() + 1;
            List<Integer> initialDepartGraph = new ArrayList<>();
            for (int i = 0; i < departGraphLength; i++) {
                Integer zero = 0;
                initialDepartGraph.add(zero);
            }
            departAirportPosition.put(departPosition, initialDepartGraph);
            departAirportMap.put(departAirport, departAirportPosition);
            listOfAirports.add(departAirportMap);

        }


        //add the schedule airport into list of schedules
        addSchedule(departTime, departAirport, departAirportName);



    }


    public static void insertArrive(String arriveAirport, String arriveAirportName, int arriveTime) {
        int sizeOfAirports = listOfAirports.size();
        int count = 0;
        CHECK:
        for (Map<String, Map<Integer, List<Integer>>> airport : listOfAirports) {
            for (Map.Entry indexEntry : airport.entrySet()) {
                if (arriveAirport.equals(indexEntry.getKey())) {
                    break CHECK;
                } else {
                    count++;
                }

            }
        }
        if (count == sizeOfAirports) {
            //insert the arrive Airport Position into list
            Map<String, Map<Integer, List<Integer>>> arriveAirportMap = new HashMap<>();

            //inner arrive Map
            Map<Integer, List<Integer>> arriveAirportPosition = new HashMap<>();
            Integer arrivePosition = listOfAirports.size();
            int arriveGraphLength = listOfAirports.size() + 1;
            List<Integer> initialArriveGraph = new ArrayList<>();
            for (int i = 0; i < arriveGraphLength; i++) {
                Integer zero = 0;
                initialArriveGraph.add(zero);
            }
            arriveAirportPosition.put(arrivePosition, initialArriveGraph);
            arriveAirportMap.put(arriveAirport, arriveAirportPosition);

            listOfAirports.add(arriveAirportMap);

        }


        //add the schedule airport into list of schedules
        addSchedule(arriveTime, arriveAirport, arriveAirportName);



    }

    public static void insertSource() {

        //insert the depart Airport Position into list
        Map<String, Map<Integer, List<Integer>>> departAirportMap = new HashMap<>();

        //inner depart Map
        Map<Integer, List<Integer>> departAirportPosition = new HashMap<>();
        Integer departPosition = 0;
        int departGraphLength = 1;
        List<Integer> initialDepartGraph = new ArrayList<>();
        for (int i = 0; i < departGraphLength; i++) {
            Integer zero = 0;
            initialDepartGraph.add(zero);
        }
        departAirportPosition.put(departPosition, initialDepartGraph);
        departAirportMap.put("source", departAirportPosition);
        listOfAirports.add(departAirportMap);


    }

    public static void insertTarget() {
        int sizeOfAirports = listOfAirports.size();
        //insert the depart Airport Position into list
        Map<String, Map<Integer, List<Integer>>> departAirportMap = new HashMap<>();

        //inner depart Map
        Map<Integer, List<Integer>> departAirportPosition = new HashMap<>();
        Integer departPosition = sizeOfAirports;
        int departGraphLength = sizeOfAirports + 1;
        List<Integer> initialDepartGraph = new ArrayList<>();
        for (int i = 0; i < departGraphLength; i++) {
            Integer zero = 0;
            initialDepartGraph.add(zero);
        }
        departAirportPosition.put(departPosition, initialDepartGraph);
        departAirportMap.put("target", departAirportPosition);
        listOfAirports.add(departAirportMap);

    }


    public static void addAirport() {
//        if (listOfAirports.size() > 2) {
//            if (newAddGraph > 0) {
        int sizeOfAirport = listOfAirports.size();
        for (Map<String, Map<Integer, List<Integer>>> airport : listOfAirports) {

            for (Map.Entry indexEntry : airport.entrySet()) {

                Map<Integer, List<Integer>> tempAirportGraph = (Map<Integer, List<Integer>>) indexEntry.getValue();
                for (Map.Entry entry : tempAirportGraph.entrySet()) {
                    List<Integer> value = (List<Integer>) entry.getValue();
                    int sizeOfList = value.size();
                    for (int i = 0; i < sizeOfAirport - sizeOfList; i++) {
                        value.add(0);
                    }
                    entry.setValue(value);

                }
                indexEntry.setValue(tempAirportGraph);
            }
        }

//            }
//        }
    }

    public static int getSource() {
        int source = 0;
        for (Map.Entry indexEntry : listOfSchedules.entrySet()) {
            if (indexEntry.getKey().equals("LAX")) {
                Map<Integer, Integer> entry = (Map<Integer, Integer>) indexEntry.getValue();
                source = (int) entry.values().toArray()[0];
                break;
            }
        }
        return source;
    }

    public static int getTarget() {
        int target = 0;
        for (Map.Entry indexEntry : listOfSchedules.entrySet()) {
            if (indexEntry.getKey().equals("JFK")) {
                Map<Integer, Integer> entry = (Map<Integer, Integer>) indexEntry.getValue();
                target = (int) entry.values().toArray()[entry.size() - 1];
                break;
            }
        }
        return target;
    }

    public static int getPosition(String Airport) {
        int Position = 0;
        for (Map<String, Map<Integer, List<Integer>>> airport : listOfAirports) {
            for (Map.Entry indexEntry : airport.entrySet()) {
                if (Airport.equals(indexEntry.getKey())) {
                    Map<Integer, List<Integer>> tempArriveAirport = (Map<Integer, List<Integer>>) indexEntry.getValue();
                    for (Map.Entry<Integer, List<Integer>> entry : tempArriveAirport.entrySet()) {
                        Position = entry.getKey();
                    }
                }
            }
        }
        return Position;
    }


    public static void addCapacity(int arrivePosition, String departAirport, int capacity) {
        for (Map<String, Map<Integer, List<Integer>>> airport : listOfAirports) {
            for (Map.Entry indexEntry : airport.entrySet()) {
                if (departAirport.equals(indexEntry.getKey())) {
                    Map<Integer, List<Integer>> tempArriveAirport = (Map<Integer, List<Integer>>) indexEntry.getValue();
                    for (Map.Entry<Integer, List<Integer>> entry : tempArriveAirport.entrySet()) {
                        List<Integer> newArray = entry.getValue();
                        newArray.set(arrivePosition, newArray.get(arrivePosition) + capacity);
                        entry.setValue(newArray);
                    }

                }
            }
        }
    }


    public static void addSchedule(int time, String airport, String airportName) {
        int position = getPosition(airport);

        Map<Integer, Integer> tempSchedule = new HashMap<>();
        for (Map.Entry indexEntry : listOfSchedules.entrySet()) {
            if (airportName.equals(indexEntry.getKey())) {
                tempSchedule = (Map<Integer, Integer>) indexEntry.getValue();
            }
        }
        tempSchedule.put(time, position);

        listOfSchedules.put(airportName, tempSchedule);
    }


    public static void addInfiniteIntoSameAirport() {
        for (Map.Entry scheduleEntry : listOfSchedules.entrySet()) {
            String airportName = (String) scheduleEntry.getKey();
            Map<Integer, Integer> tempSchedule = (Map<Integer, Integer>) scheduleEntry.getValue();

            Integer[] keyArray = tempSchedule.keySet().toArray(new Integer[tempSchedule.size()]);
            Integer[] valueArray = tempSchedule.values().toArray(new Integer[tempSchedule.size()]);
//            for (int i = 0; i < tempSchedule.size(); i++)
//                System.out.println(airportName + " " + keyArray[i] + " " + valueArray[i]);
            for (int i = 0; i < tempSchedule.size() - 1; i++) {
                String preAirport = "";
                if (keyArray[i] > 9) {
                    preAirport = airportName + keyArray[i];
                } else {
                    preAirport = airportName + "0" + keyArray[i];
                }
                int latPosition = valueArray[i + 1];

                for (Map<String, Map<Integer, List<Integer>>> airport : listOfAirports) {
                    for (Map.Entry indexEntry : airport.entrySet()) {
                        if (preAirport.equals(indexEntry.getKey())) {
                            Map<Integer, List<Integer>> tempGraph = (Map<Integer, List<Integer>>) indexEntry.getValue();
                            List<Integer> tempList = (List<Integer>) tempGraph.values().toArray()[0];
                            tempList.set(latPosition, 9999);
                        }
                    }
                }
            }
        }
    }

    public static void printResult() throws FileNotFoundException {
        File file = new File("output.txt");
        PrintStream stream = new PrintStream(file);
        System.setOut(stream);
        for (Map<String, Map<Integer, List<Integer>>> airportEntry : listOfAirports) {
            for (Map.Entry indexEntry : airportEntry.entrySet()) {
                String airport = (String) indexEntry.getKey();
                Map<Integer, List<Integer>> tempAirportGraph = (Map<Integer, List<Integer>>) indexEntry.getValue();
                for (Map.Entry entry : tempAirportGraph.entrySet()) {
                    Integer airportPosition = (Integer) entry.getKey();
                    List<Integer> airportGraph = (List<Integer>) entry.getValue();
                    System.out.println(airport + " " + airportPosition + " " + airportGraph);
                }
            }
        }

        int count = 0;
        for (Map.Entry indexEntry : listOfSchedules.entrySet()) {
            String airPortName = (String) indexEntry.getKey();
            Map<Integer, Integer> tempSchedule = (Map<Integer, Integer>) indexEntry.getValue();
            for (Map.Entry entry : tempSchedule.entrySet()) {
                int time = (int) entry.getKey();
                int position = (int) entry.getValue();
                count++;
                System.out.println(airPortName + time + " " + position);
            }

        }
        System.out.println(count);
    }

}

