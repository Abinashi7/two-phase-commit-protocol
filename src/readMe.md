Running instructions: 
To run jar file: "java -jar <jar file name> <port1> <port2> <port3> <port4> <port5>"
1. Enter 5 different port numbers in the Coordinator.java via command line arguments
2. Enter the same set of port numbers in the clientDriver.java via command line arguments
3. Run the Coordinator.java class
4. Run the clientDrive.java class
5. Logs with the current phase will be printed on console on the coordinator side
6. clientDriver console will print the request status
7. Ctrl+c to stop the coordinator and shut down all the servers

Testing: Enter the following commands in clientDriver.java main thread

for GET request:

Interfaces[Server number: 1- 5].beginTransaction(null, serviceType.GET.toString(), "Desired key", null) 

for PUT request:

Interfaces[Server number: 1- 5].beginTransaction(transactionID.randomUUID(), serviceType.PUT.toString(), "Desired key", "Desired value") 

for DELETE request:

Interfaces[Server number: 1- 5].beginTransaction(transactionID.randomUUID(), serviceType.DEL.toString(), "Desired key", null) 
