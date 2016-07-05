import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.io.FileNotFoundException;

public interface CommonInterface extends java.rmi.Remote {

	
	
	public void connected(String selfName) throws RemoteException;

	public void receiveMoney(double amount, int sender_id) throws RemoteException, FileNotFoundException;

	public void assignMarkerObject(Marker m, int sender_id) throws RemoteException, UnknownHostException, NotBoundException, InterruptedException;

	public String sendState() throws RemoteException;

	//public int[] collectChannelState() throws RemoteException;

	public void sendM(String IP) throws RemoteException, NotBoundException;

	public void getM(Marker m) throws RemoteException;

	public void transmitMoney(double amount, int ID) throws RemoteException, InterruptedException,FileNotFoundException;

	public int incomingChannelValue() throws RemoteException;
	
	public void sendMarkersToOtherProcesses() throws RemoteException,  UnknownHostException, NotBoundException, InterruptedException;

}
