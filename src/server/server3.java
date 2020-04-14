//package server;
//
//import java.rmi.NotBoundException;
//import java.rmi.RemoteException;
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//import java.rmi.server.UnicastRemoteObject;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class server3 implements mapIF {
//    int curServer;
//    int[] participants = new int[4];
//    private int threadCount=0;
//    boolean Consent=false;
//
//    /** This map is a temporary storage to key-value and service. When all the steps are done for 2-pc, this memory will be pushed into global memory and then erased*/
//    private Map<UUID, Request3> temporaryStorage = new HashMap<>();
//    /** This map is to collect all the votes from other servers after sending a can commit? or prepare message. It will contain a UUID(which is key for the current request) and port number and it's vote(boolean) as a value */
//    private  Map<UUID,Map<Integer,Boolean>> prepareInformation = new HashMap<>();
//    /** This map is to collect all the votes from other servers after a coordinator has recieved all the votes and now wants servers to commit globally and collect confirmation from each of them. It will contain a UUID(which is key for the current request) and port number and it's vote(boolean) as a value */
//    private  Map<UUID,Map<Integer,Boolean>> commitInformation = new HashMap<>();
//    /** This map stores the global information after all the commit phases are passed*/
//    private Map<Integer, Map<String, String>> globalMap = new HashMap<>();
//
//    /**
//     * Method to store and delete key-value from global storage after 2-phase commit and retrieve from globalMap when GET is requested
//     * @param service service request from client
//     * @param key the key to store
//     * @param value the val to store
//     * @return The message if the transaction is successful or not
//     */
//    public String commitGlobally(String key,String value, String service) {
//        StringBuilder status = new StringBuilder();
//        if(service.equalsIgnoreCase("GET")){
//            getThreads();
//            this.threadCount++;
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if(globalMap.get(curServer).containsKey(key)){
//                String val=  globalMap.get(curServer).get(key);
//                System.out.println(timeStamp()+" GET request: the value for key: "+ key+" is "+ val);
//                status.append(timeStamp()).append(" GET request: the value for key: ").append(key).append(" is ").append(val);
//                this.threadCount--;
//            }else{
//                System.out.println( timeStamp()+" GET request: Key "+key+ " is not in the storage");
//                status.append(timeStamp()).append(" GET request: Key ").append(key).append(" is not in the storage");
//            }
//
//        }else if(service.equalsIgnoreCase("PUT")){
//            getThreads();
//            this.threadCount++;
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if(!globalMap.containsKey(curServer)){
//                globalMap.put(curServer, new HashMap<>());
//                globalMap.get(curServer).put(key, value);
//                System.out.println(timeStamp()+" PUT request: "+"Key: " +key+" value: "+ value+" written in global memory");
//                status.append(timeStamp()).append("PUT request: ").append("Key: ").append(key).append(" value: ").append(value).append(" written in global memory");
//                this.threadCount--;
//            }else {
//                globalMap.get(curServer).put(key, value);
//                System.out.println(timeStamp()+" PUT request: "+"Key: " +key+" value: "+ value+" written in global memory");
//                status.append(timeStamp()).append("PUT request: ").append("Key: ").append(key).append(" value: ").append(value).append(" written in global memory");
//                this.threadCount--;
//            }
//
//        }else if(service.equalsIgnoreCase("DEL")){
//            getThreads();
//            this.threadCount++;
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if(globalMap.get(curServer).containsKey(key)){
//                String val=  globalMap.get(curServer).remove(key);
//                System.out.println(timeStamp()+" DEL request: the value for key: "+ key+" Value: "+ val+" is deleted from global memory");
//                status.append(timeStamp()).append(" DEL request: the value for key: ").append(key).append(" Value: ").append(val).append(" is deleted from global memory");
//                this.threadCount--;
//            }else{
//                System.out.println(timeStamp()+" DEL request: Key is not in the storage");
//                status.append(timeStamp()).append(" DEL request: Key is not in the storage");
//                this.threadCount--;
//            }
//        }else{
//            System.err.println("Please enter a valid service");
//            status.append("Please enter a valid service");
//        }
//
//        return status.toString();
//    }
//
//    /**
//     * For the current time stamp
//     * @return Get the timestamp in the format : Date-> (DD:MM:YEAR) Time-> (HR:MIN:MS)
//     */
//    private String timeStamp(){
//        return "(" + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ") ";
//    }
//
//    /**
//     * Method to accommodate update requests(PUT/DEL) and retrieve from globalMap when GET is requested
//     * @param transactID a token ID of the current transaction
//     * @param service service request from client
//     * @param key the key to store
//     * @param value the val to store
//     * @return The message if the transaction is successful or not
//     */
//    @Override
//    public String beginTransaction(UUID transactID, String service, String key, String value) {
//        if(service.equalsIgnoreCase("GET")){
//            return commitGlobally(key, value,service);
//        }else{
//            // adding to temporary storage
//            Request3 val = new Request3();
//            val.key=key;
//            val.val=value;
//            val.service=service;
//
//            this.temporaryStorage.put(transactID, val);
//            System.err.println(timeStamp()+" Entering the Prepare phase");
//            sendPrepareCommand(transactID, service, key, value);
//
//            boolean consentAfterPrepare = collectVotes(transactID, "Prepare");
//
//            if(!consentAfterPrepare){
//                return "Not true in the prepare phase";
//            }
//            System.err.println(timeStamp()+" Entering the Commit phase");
//            // Sending the commit command to all the participants
//            sendCommitCommand( transactID);
//
//            boolean consentAfterCommit = collectVotes(transactID, "Commit");
//
//            if(!consentAfterCommit){
//                return "Not true in the Global Commit phase";
//            }
//            // If everything goes correct till this point, temporary storage will be released and stored in the permanent memory
//            Request3 reqestToCommitGlobally = this.temporaryStorage.get(transactID);
//
//            if(reqestToCommitGlobally != null ){
//                String result = this.commitGlobally(reqestToCommitGlobally.key, reqestToCommitGlobally.val, reqestToCommitGlobally.service);
//                this.temporaryStorage.remove(transactID);
//                return result;
//            }else{
//                throw  new IllegalArgumentException(" Transaction ID not found");
//            }
//
//        }
//    }
//
//    /**
//     * Method to send the global commit message from coordinator to all the participants. Collect all the votes from each and every participant(servers) and store it in the prepareStorage map.
//     * @param transactionID ID of the transaction
//     */
//
//    void  sendCommitCommand(UUID transactionID){
//        this.commitInformation.put(transactionID, new HashMap<>());
//
//        for( int participant: this.participants){
//            this.commitInformation.get(transactionID).put(participant, false);
//            try {
//                Registry registry = LocateRegistry.getRegistry(participant);
//                mapIF stub = (mapIF) registry.lookup("server.mapIF");
//
//                stub.commitCommand(transactionID, curServer);
//            } catch (RemoteException | NotBoundException e) {
//                printLog("Commit phase did not succeed, removing data from temporary storage");
//            }
//
//        }
//
//    }
//
//    /**
//     * This method collects the vote from all the servers after each phase.
//     * @param transactionID the current transaction ID
//     * @param transactionPhase commit or prepare phase
//     * @return true if all the participants vote yes, false otherwise
//     */
//    boolean collectVotes(UUID transactionID, String transactionPhase){
//
//        int tries = 2;
//        int votes=0;
//
//        for(int i=0; i< tries; i++){
//
//            try {
//                Thread.sleep(200);
//            } catch (Exception e) {
//                printLog("Thread sleep failed");
//            }
//            // Votes for the prepare phase
//            if(transactionPhase.equalsIgnoreCase("Prepare")){
//                Map<Integer, Boolean> participants = this.prepareInformation.get(transactionID);
//
//                for( int participant: this.participants ){
//                    if(participants.get(participant)){
//                        votes++; //collecting votes from the participants
//                    }
//                }
//                // Votes for the Commit phase
//            }else if(transactionPhase.equalsIgnoreCase("Commit")){
//                Map<Integer, Boolean> participants = this.commitInformation.get(transactionID);
//                for( int participant: this.participants ){
//                    if(participants.get(participant)){
//                        votes++;
//                    }
//                }
//            }
//            if(votes==4){
//                return true;
//            }
//        }
//
//        return false; // this means we don't have all the votes
//
//    }
//
//    /**
//     * Method to send prepare message from coordinator to all the participants. Collect all the votes from each and every participant(servers) and store it in the prepareStorage map.
//     * @param transactionID ID of the transaction
//     * @param service the client's request
//     * @param key the key
//     * @param value the value of the associated key
//     */
//    void sendPrepareCommand(UUID transactionID, String service, String key, String value){
//        // Initializing storage for the prepare phase
//        this.prepareInformation.put(transactionID, (new HashMap<>()));
//        // send prepare message and get status from each server
//        for(int i=0; i< this.participants.length; i++){
//            try{
//
//                this.prepareInformation.get(transactionID).put(this.participants[i], false);
//                Registry registry = LocateRegistry.getRegistry(this.participants[i]);
//                mapIF currentServer = (mapIF) registry.lookup("server.mapIF");
////
//                currentServer.prepareCommand(transactionID, service, key, value, curServer);
//            }catch(Exception ex)
//            {
//                printLog("Prepare phase did not succeed, removing data from temporary storage");
//            }
//        }
//
//    }
//
//    /**
//     * Method to add the current transaction in the temporary storage until all the phases are passed
//     * @param transactionID the transaction ID
//     * @param service the service requested by the client
//     * @param key the key
//     * @param value the value
//     */
//    void addTempStorage(UUID transactionID, String service, String key, String value){
//
//        // adding to temporary storage
//        Request3 val = new Request3();
//        val.key=key;
//        val.val=value;
//        val.service=service;
//
//        this.temporaryStorage.put(transactionID, val);
//    }
//
//    /**
//     * Print the current timestamp
//     * @param message The log message to append with the current timestamp
//     */
//    void printLog(String message){
//        System.out.println(timeStamp()+" -> "+message);
//    }
//
//    /**
//     * This method recognize the type of transaction phase and store the transaction in its
//     * appropriate memory
//     * @param transactionID the transaction ID
//     * @param currentServer the current server
//     * @param action the phase of the transaction
//     */
//    @Override
//    public void recognizeAcknowledgement(UUID transactionID, int currentServer, String action) {
//        if(action.equalsIgnoreCase("Prepare")){
//            // TODO: 2020-03-21  need to check
//            this.prepareInformation.get(transactionID).put(currentServer, true); // true means it acknowledges
//        }else if(action.equalsIgnoreCase("Commit")){
//            this.commitInformation.get(transactionID).put(currentServer, true);
//        }
//        printLog("Server: "+currentServer+" Sent its acknowledgement");
//    }
//
//    /**
//     * This method is used for the commit phase. It commits the key-value pair in the permanent/global memory. And if successful, sends back it's acknowledgement
//     * @param messageId the current transaction ID
//     * @param currentserver the current server
//     */
//    @Override
//    public void commitCommand(UUID messageId, int currentserver) {
//
//        Request3 requestToCommit = this.temporaryStorage.get(messageId);
//
//        if(requestToCommit != null){
//            this.commitGlobally(requestToCommit.key, requestToCommit.val, requestToCommit.service);
//            this.temporaryStorage.remove(messageId); // erase the temporary storage since the transaction is final now
//
//            // sending back acknowledgement
//            try{
//                Registry registry = LocateRegistry.getRegistry(currentserver);
//                mapIF stub = (mapIF) registry.lookup("server.mapIF");
//                stub.recognizeAcknowledgement(messageId, curServer, "Commit");
//
//            }catch(Exception ex)
//            {
//                printLog("Something went wrong in sending Ack, removing data from temporary storage");
//                this.temporaryStorage.remove(messageId);
//            }
//
//        }else{
//            throw new IllegalArgumentException(" the transaction id is not founf");
//        }
//
//    }
//
//
//    /**
//     * This method is used in the Prepare phase. it get's the registry of the current server and send it's acknowledgement if successful
//     * @param transactionID the current transaction ID
//     * @param service service asked by the client
//     * @param key the key
//     * @param value the value
//     * @param server the current server
//     */
//    @Override
//    public void prepareCommand(UUID transactionID, String service, String key, String value, int server) {
//        addTempStorage(transactionID, service, key, value);
//        // Sending back acknowledgement from this server
//        try{
//            Registry registry = LocateRegistry.getRegistry(server);
//            mapIF stub = (mapIF) registry.lookup("server.mapIF");
//
//            stub.recognizeAcknowledgement(transactionID, curServer, "Prepare");
//
//        }catch(Exception ex)
//        {
//            printLog("Prepare phase was not successful, removing data from temporary storage");
//            this.temporaryStorage.remove(transactionID);
//        }
//
//    }
//
//    /**
//     * Setting the current port number of the server
//     * @param allservers all the servers
//     * @param curServer the current server's port number
//     */
//    @Override
//    public void setCurrentServer(int[] allservers, int curServer) {
//        this.curServer=curServer;
//        this.participants=allservers;
//    }
//
//    /**
//     * To implement read write lock per thread
//     */
//    private void getThreads(){
//        while(threadCount>0){
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    public static void main(String[] args){
//        Coordinator.loadServers(9192);
//        try{
////            Coordinator.servs.add(new server3());
//            Coordinator.setServer(new server3());
//
//            server5 svr = (server5) Coordinator.servs.get(2);
//            mapIF stub = (mapIF) UnicastRemoteObject.exportObject(svr, 0);
//            Registry registry = LocateRegistry.createRegistry(9192);
//            registry.bind("server.mapIF", stub);
////            registerOtherServers(serverInfo.servers, serverInfo.servers[i]);
//            Coordinator.registerOtherServers(9192);
//
//            System.out.println("Server-3" +" is running at port"+9192);
//        } catch (Exception e) {
//            System.err.println("Server exception: " + e.toString());
//        }
//
//        // thread per participant
////        Thread serverThread=new Thread();
////        serverThread.start();
//    }
//}
//
//
///**
// * class instance to store the transaction info
// */
//class Request3 {
//    String key;
//    String val;
//    String service;
//}
//
//
