package actions.model;

import model.RMI.RMIConnection;
import model.RMI.RMI_Interface;

import java.rmi.RemoteException;

/**
 * Client Bean which acts as our Model. It stores the RMI, the uid and other useful variables associated with the
 * current user session. It is responsible for all interaction with the RMI.
 */
public class Client {
    private final static String RMI_HOST="localhost";


    private RMIConnection rmi;
    private int uid;

    public Client() {
        this.rmi = new RMIConnection(RMI_HOST);
        this.uid = -1;
    }

    /**
     * Calls RMI's login safely. We have chosen to encapsulate it so that we can later on (FIXME) implement retry
     * mechanisms. We will need to indicate the calling function if the RMI fails. <-- FIXME
     * @param username User's username
     * @param password User's password
     * @return On success, returns the user's UID. On failure, -1 indicates an error logging in (no such user(pass).
     * FIXME: Possibly include other error codes to indicate RMI failure
     */
    private int doRMILogin(String username, String password) {
        int ret = 0;
        try {
            ret = rmi.getRMIInterface().login(username, password);
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ret;
    }

    /**
     * Public interface to try to login a client. If successful, current state will be updateed to indicate that this
     * Client represents the user given by this (username,password). Specifically, this.uid will be set to its uid
     * @param username User's username
     * @param password User's password
     * @return
     */
    public boolean doLogin(String username, String password) {
        return (this.uid = doRMILogin(username, password)) != -1;
    }

    public int getUid() {
        return uid;
    }
}