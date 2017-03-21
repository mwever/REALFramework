import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

public class CrcREALServer implements ICrcREALServer {

	private final int portNumber = 64373;

	public CrcREALServer() {
		final ServerSocket serverSocket = new ServerSocket(this.portNumber);
		final Socket clientSocket = serverSocket.accept();
	}

	@Override
	public String sayHello() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
