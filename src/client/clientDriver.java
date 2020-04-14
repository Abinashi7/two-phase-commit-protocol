package client;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;
import server.mapIF;

public class clientDriver {

    public enum serviceType {GET, PUT, DEL}
    static void status(String message){
         System.out.println("Status: "+ message );
    }

    public static void main(String[] args) {
        //client-1
        clients participants = new clients();
        //client-2
        client2 client2 =  new client2();
        participants.getServers(args);
        client2.getServers(args);

        mapIF[] Interfaces = new mapIF[5];
        Registry[] registries = new Registry[5];
        mapIF[] InterfacesClient2 = new mapIF[5];
        Registry[] registriesClient2 = new Registry[5];
        UUID transactionID = null;

        try {

            //getting registry for client-1
            for (int i = 0 ; i < participants.servers.length ; i++) {
                registries[i] = LocateRegistry.getRegistry("LOCALHOST",participants.servers[i]);
                Interfaces[i] = (mapIF) registries[i].lookup("server.mapIF");
            }

            //getting registry for client-2
            for (int i = 0 ; i < client2.servers.length ; i++) {
                registriesClient2[i] = LocateRegistry.getRegistry("LOCALHOST",client2.servers[i]);
                InterfacesClient2[i] = (mapIF) registriesClient2[i].lookup("server.mapIF");
            }



            int server1=0, server2=1,server3=1,server4=1,server5=1;

            /** PUT REQUEST from client 1 */


            status( Interfaces[server1].beginTransaction(transactionID.randomUUID(), serviceType.PUT.toString(), "key1", "Val1"));
            status(Interfaces[server2].beginTransaction(transactionID.randomUUID(), serviceType.PUT.toString(), "key2", "Val2"));
            status( Interfaces[server3].beginTransaction(transactionID.randomUUID(), serviceType.PUT.toString(), "key3", "Val3"));
            status( Interfaces[server4].beginTransaction(transactionID.randomUUID(), serviceType.PUT.toString(), "key4", "Val4"));
            status(Interfaces[server5].beginTransaction(transactionID.randomUUID(), serviceType.PUT.toString(), "key5", "Val5"));

            /** GET REQUEST from client 2 from server 1 only */
            status( InterfacesClient2[server1].beginTransaction(null, serviceType.GET.toString(), "key1", null));
            status( InterfacesClient2[server1].beginTransaction(null, serviceType.GET.toString(), "key2", null));
            status( InterfacesClient2[server1].beginTransaction(null, serviceType.GET.toString(), "key3", null));
            status( InterfacesClient2[server1].beginTransaction(null, serviceType.GET.toString(), "key4", null));
            status( InterfacesClient2[server1].beginTransaction(null, serviceType.GET.toString(), "key5", null));


            /** GET REQUEST from client 1 from all the servers */
            status(Interfaces[server1].beginTransaction(null, serviceType.GET.toString(), "key1", null));
            status(Interfaces[server2].beginTransaction(null, serviceType.GET.toString(), "key2", null));
            status(Interfaces[server3].beginTransaction(null, serviceType.GET.toString(), "key3", null));
            status(Interfaces[server4].beginTransaction(null, serviceType.GET.toString(), "key4", null));
            status(Interfaces[server5].beginTransaction(null, serviceType.GET.toString(), "key5", null));

            /** DEK REQUEST from client 2 */

            status(InterfacesClient2[server1].beginTransaction(transactionID.randomUUID(), serviceType.DEL.toString(), "key1", null));
            status(InterfacesClient2[server2].beginTransaction(transactionID.randomUUID(), serviceType.DEL.toString(), "key2", null));
            status(InterfacesClient2[server3].beginTransaction(transactionID.randomUUID(), serviceType.DEL.toString(), "key3", null));
            status(InterfacesClient2[server4].beginTransaction(transactionID.randomUUID(), serviceType.DEL.toString(), "key4", null));
            status(InterfacesClient2[server5].beginTransaction(transactionID.randomUUID(), serviceType.DEL.toString(), "key5", null));

}catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

	   
  