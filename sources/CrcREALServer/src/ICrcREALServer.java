import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICrcREALServer extends Remote {
	String sayHello() throws RemoteException;
}
