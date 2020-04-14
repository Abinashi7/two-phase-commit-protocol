package server;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface mapIF extends Remote
{
     String beginTransaction(UUID messageId, String action,String key,String value) throws RemoteException;
     void recognizeAcknowledgement(UUID messageId, int callBackServer, String action) throws RemoteException;
     void prepareCommand(UUID messageId, String functionality,String key,String value, int callBackServer) throws RemoteException;
     void commitCommand(UUID messageId,  int callBackServer) throws RemoteException;
     void setCurrentServer(int[] OtherServersPorts, int yourPorts ) throws RemoteException;
}
