# two-phase-commit-protocol


Running instructions: To run jar file: "java -jar "

    Enter 5 different port numbers in the Coordinator.java via command line arguments
    Enter the same set of port numbers in the clientDriver.java via command line arguments
    Run the Coordinator.java class
    Run the clientDrive.java class
    Logs with the current phase will be printed on console on the coordinator side
    clientDriver console will print the request status
    Ctrl+c to stop the coordinator and shut down all the servers

Testing: Enter the following commands in clientDriver.java main thread

for GET request:

Interfaces[Server number: 1- 5].beginTransaction(null, serviceType.GET.toString(), "Desired key", null)

for PUT request:

Interfaces[Server number: 1- 5].beginTransaction(transactionID.randomUUID(), serviceType.PUT.toString(), "Desired key", "Desired value")

for DELETE request:

Interfaces[Server number: 1- 5].beginTransaction(transactionID.randomUUID(), serviceType.DEL.toString(), "Desired key", null)
