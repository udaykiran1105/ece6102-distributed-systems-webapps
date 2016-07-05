import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface ChandyComms extends java.rmi.Remote {

	public void connected(String selfName) throws RemoteException;

	public void receiveImage(double amount, int sender_id) throws RemoteException, FileNotFoundException,IOException;

	public void unfreezeState() throws RemoteException,  UnknownHostException, NotBoundException, IOException;
	
	public void sendMarker(Marker m, int sender_id) throws RemoteException, UnknownHostException, NotBoundException, InterruptedException;

	public void transmitImage(double amount, int ID) throws RemoteException, InterruptedException,FileNotFoundException,IOException;

	public void sendMarkersToOtherProcesses() throws RemoteException,  UnknownHostException, NotBoundException, InterruptedException;

}
