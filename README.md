# CSE-450-Programming-Assignment-Temporal-Network-Flow-

This is assignment is easier compared to what I thought, the first method I used was trying to use greedy algorithm mindset, sliced the problem to 24 sub-problem, used each time zone to calculate the flight depart and arrive, however, this cloud lead to another problem, some people will be left in some other airports excludes New York airport.
Then, I read the instruction again, and I tried to solve this problem from the perspective of professor, which means the FordFulkson Algorithm needs to be used in this assignment. And then I considered why I should only use one node for each airport.
![image]/images/scratch nodes.jpg
As the image shows above, I will state the step I achieve this assignment step by step. The LAX and JFK mean the Airport abbreviation, and the number after it means the departure or arrival time.
Before we go through the process, the final result is 6751.
Step 1: data grabbing. Read the whole data from txt file, and insert into an object defined as  . 
Step 2: data processing. The SingleFlightSchedule are stored in a list, named as flightSchedule.  
Then, processing the SingleFlightSchedule data one by one, for example “LAX JFK	0 9 185”  is stored into a list of map named listOfAirports as a map of map data structure. 
As you see above, the LAX depart airport is stored as <LAX00, <0,[0,0]>>, and JFK arrive airport is stored as <JFK09, <1,[0,0]>>.  The LAX00 means the flight took off or arrived at LAX airport at 00 times, in the value of the map, <0,[0,0]>, the key of map, 0, means the outside map <LAX00, <0,[0,0]>>’s position in the whole list of map of map data structure. As you see in <JFK09, <1,[0,0]>>, the position of it is 1. And then we should contain the capacity, in this scenario, the capacity 185 is LAX00 points to JFK09, and the position of JFK09 is 1, then inserting the 185 at the position 2 of the map LAX00, which becomes <LAX00,<0,[0,185]>>. 
	After we build the prototype of the graph, then we should link the same airport's nodes with an ascending order, and the capacity of it is infinite, but considering the real-world situation, the biggest plane can’t take 9999 people, so I set 9999 as the infinite number and insert as the capacity.
The graph will look like below:
  
And below is the nodes of airports and it position in the list:
 

Step3: FordFulkson algorithm:
Ford-Fulkerson Algorithm 
The following is simple idea of Ford-Fulkerson algorithm:
1) Start with initial flow as 0.
2) While there is a augmenting path from source to sink. 
           Add this path-flow to flow.
3) Return flow.
After building the graph, applied the FordFulkson algorithm to the graph, before we do so, we shall transfer the list of airports to a two dimension array.  , numpoinmts means the number of the total nodes.
And the final result is 6751. 


Insturciton:
	Using IntelliJ to open the project, and the JDK version is 8,  . And then build and run the Main.java.
