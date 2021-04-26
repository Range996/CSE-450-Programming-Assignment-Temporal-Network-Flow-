import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class FlightSchedules {
    public static final String[] AIRPORTS= {"LAX","JFK","SFO","PHX","SEA","DEN","ATL","ORD","BOS","IAD"};
    public static List<SingleFlightSchedule> flightSchedules = new ArrayList<SingleFlightSchedule>();

    public static void readSchedule() throws FileNotFoundException {
        FileReader file = new FileReader("flights.txt");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            String currentLine = sc.nextLine();
            CHECK:
            if ((currentLine.replaceAll("\\s+","")).length() > 0) {
                String[] currentSchedule = currentLine.split("\\s+");
                SingleFlightSchedule newSchedule = new SingleFlightSchedule();
                String departAirport;

                if (Arrays.asList(AIRPORTS).contains(currentSchedule[0])) {
                    departAirport = currentSchedule[0];
                }
                else
                    break CHECK;

                String arriveAirport;
                if (Arrays.asList(AIRPORTS).contains(currentSchedule[0])) {
                    arriveAirport = currentSchedule[1];
                }
                else
                    break CHECK;
                int departTime = Integer.parseInt(currentSchedule[2]);
                int arriveTime = Integer.parseInt(currentSchedule[3]);
                int capacity = Integer.parseInt(currentSchedule[4]);
                newSchedule.departAirport = departAirport;
                newSchedule.arriveAirport = arriveAirport;
                newSchedule.departTime = departTime;
                newSchedule.arriveTime = arriveTime;
                newSchedule.capacity = capacity;
                flightSchedules.add(newSchedule);
            }
        }
        sc.close();
    }

    public static List<SingleFlightSchedule> returnSchedule() {
        return flightSchedules;
    }
}
