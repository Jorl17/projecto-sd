import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

////
// This class, which implements an independent thread, is responsible for handling all requests from a given client
// (given to us referenced by its socket)
//
public class ServerClient implements Runnable {
    private Socket socket = null;
    private DataOutputStream outStream = null;
    private DataInputStream inStream = null;

    private Registry RMIregistry = null;
    private RMI_Interface RMIInterface = null;

    // The client's uid. -1 means not logged in.
    private int uid = -1;

    public ServerClient(Socket currentSocket) {
        this.socket = currentSocket;
        try {
            this.outStream = new DataOutputStream(currentSocket.getOutputStream());
            this.inStream = new DataInputStream(currentSocket.getInputStream());
            initRMIConnection();
        } catch (IOException e) {
            System.err.println("Error constructing a new ServerClient (did the connection die?");
        }
    }

    ////
    // FIXME: This doesn't seem well thought out. We should have ONE RMI for all clients. For now,
    // we'll keep this code, but we should fix it ASAP.
    //
    private boolean initRMIConnection() {

        try {
            RMIregistry = LocateRegistry.getRegistry(7000);
            RMIInterface = (RMI_Interface) RMIregistry.lookup("academica");
        } catch (RemoteException e) {
            System.err.println("Remote Exception no ServerClient!");
            return false;
        } catch (NotBoundException n) {
            System.err.println("NotBoundException no ServerClient!");
            return false;
        }

        return true;
    }

    private boolean isLoggedIn() {
        return uid != -1;
    }


    @Override
    public void run() {

        ServerTopic topic = new ServerTopic(5, "Hello, Ladies", "Hakuna Matata");

        for(;;) {
            Common.Message msg;
            int intMsg;

            // Read the next Message/Request
            if ( ( msg = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD)
                break ;


            // Handle the request
            if ( msg == Common.Message.MSG_LOGIN)
                if ( !handleLogin() )
                    break ;

            if ( msg == Common.Message.MSG_GETTOPICS)
                if ( !handleListTopicsRequest() )
                    break ;

        }

        if ( !isLoggedIn() )
            System.out.println("Connection to UID "+uid+" dropped!");
        else
            System.out.println("Connection to a client dropped!");
    }

    private boolean handleListTopicsRequest() {
        if ( !isLoggedIn() ) {
            Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN,outStream);
            return true; // Message was handled (note that we return true because not being logged in is not a
                         // connection error
        }

        ServerTopic[] topics = null;
        try {
            topics = RMIInterface.getTopics();
        } catch (RemoteException e) {
            //FIXME: Handle this
            e.printStackTrace();
        }

        if ( topics == null )
            return false; //There was an error and there are no topics...

        if ( !Common.sendInt(topics.length,outStream) )
            return false;

        for (ServerTopic t : topics)
            if(!t.writeToDataStream(outStream))
                return false;

        return true;
    }

    ////
    //  Only returns false if we lost connection
    ////
    private boolean handleLogin() {
        String user, pwd;

        if ( isLoggedIn() ) {
            // Can't login without logging out!
            Common.sendMessage(Common.Message.MSG_ERR,outStream);
            return true; // Message was handled (note that we return true because not being logged in is not a
                         // connection error
        }

        if ( (user = Common.recvString(inStream)) == null)
            return false;
        if ( (pwd = Common.recvString(inStream)) == null)
            return false;

        try {
            uid = RMIInterface.login(user, pwd);
        } catch (RemoteException e) {
            System.err.println("Remote exception while handling login!");
            return false; //FIXME: we should do something about a remote exception!
        }

        if (uid != -1){
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                return false;
        } else {
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                return false;
        }

        // Message was handled successfully
        return true;
    }
}
