package server;
import server.mapIF;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


/**
 * This class carries the logic for a coordinator. It appends and creates the registery for all the participant servers
 * and bind it to the map Interface that connects a client to all the services available.
 */
 class Coordinator extends Thread {

    private static serverHelper serverInfo = new serverHelper();
    private static Server[] allServers = new Server[5];
    private static int serverIndex=1;

    public static void main(String[] args) {
        serverInfo.loadServers(args);
        for (int i = 0 ; i < serverInfo.getSize() ; i++)
        {
            try{
                allServers[i] = new Server();
                mapIF stub = (mapIF) UnicastRemoteObject.exportObject(allServers[i], 0);

                Registry registry = LocateRegistry.createRegistry(serverInfo.servers[i]);
                registry.bind("server.mapIF", stub);
                registerOtherServers(serverInfo.servers, serverInfo.servers[i]);
                System.out.println("Server "+serverIndex++ +" is running at port"+serverInfo.servers[i]);
                System.out.println(serverInfo.getSize());
//                System.out.println(size());

            } catch (Exception e) {
                System.err.println("Server exception: " + e.toString());
            }

            // thread per participant
            Thread serverThread=new Thread();
            serverThread.start();
        }


    }

    public static void register(String[] args){


        try{

            serverInfo.loadServers(args);
            mapIF stub = (mapIF) UnicastRemoteObject.exportObject(new Server(), 0);
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
            registry.bind("server.mapIF", stub);
            registerOtherServers(serverInfo.servers, Integer.parseInt(args[0]));
            System.out.println("Server "+serverIndex++ +" is running at port"+Integer.parseInt(args[0]));
            System.out.println(serverInfo.getSize());
            System.out.println(serverInfo.getSize());

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
        }

        // thread per participant
        Thread serverThread=new Thread();
        serverThread.start();

    }

//    public static int size(){
//        int size=0;
//        for(Server server: allServers){
//            if(server!=null){
//                size++;
//            }
//        }
//        return size;
//    }
    /**
     * Method to get getRegistery of participants that are other than the current server
     * @param servers  all the servers
     * @param port each port number
     */
    private static void registerOtherServers(int[] servers, int port)
    {
        try{
            Registry registry = LocateRegistry.getRegistry(port);

            mapIF stub = (mapIF) registry.lookup("server.mapIF");

            int curIndex = 0;
            int[] other = new int[4];
            for (int i = 0 ; i < servers.length ; i++) {
                if (servers[i] != port) {
                    other[curIndex] = servers[i];
                    curIndex++;
                }
            }
            stub.setCurrentServer(other, port); // set the current server's info
        }
        catch(Exception ex) {
            System.err.println("Unable to connect to server: " + port);
            System.err.println(ex.getMessage());
        }
    }
}



