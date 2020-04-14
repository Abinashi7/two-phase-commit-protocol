package client;

public class clients {

    public int[] servers = new int[5];

    public void getServers(String [] args) {

            if (args.length < 5) {

                throw new IllegalArgumentException("Please input 5 different servers");
            }
            // loading all the servers in the server array
            for (int i = 0 ; i < args.length; i++) {
                servers[i] = Integer.parseInt(args[i]);
            }

    }

}
