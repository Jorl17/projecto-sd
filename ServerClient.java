import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

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

    static int limit_characters_topic = 20;//Number of characters for the topic's name

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

        for(;;) {
            Common.Message msg;

            // Read the next Message/Request
            if ( ( msg = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD){
                System.out.println("Error No Message Received!!!");
                break ;
            }


            // Handle the request
            // FIXME: All of these prints are mostly here just for debugging. In practice, they will mean that we've
            // lost the connection to che client. We should drop them in production code
            if ( msg == Common.Message.REQUEST_LOGIN) {
                if ( !handleLogin() ){
                    System.err.println("Error in the handle login method!!!");
                    break ;
                }
            } else if (msg == Common.Message.REQUEST_REG){
                if (!handleRegistration()){
                    System.err.println("Error in the handle registration method!!!");
                    break ;
                }
            } else if ( msg == Common.Message.REQUEST_GETTOPICS){
                if ( !handleListTopicsRequest() ){
                    System.err.println("Error in the handle list topics request method!!!");
                    break ;
                }
            } else if (msg == Common.Message.REQUEST_CREATETOPICS){
                if ( !handleCreateTopicRequest() ){
                    System.err.println("Error in the handle create topics request method!!!");
                    break ;
                }
            }else if (msg == Common.Message.REQUEST_GET_IDEA_BY_IID){
                if ( !handleGetIdeaByIID() ){
                    System.err.println("Error in the handle get idea by IID method!!!");
                    break ;
                }
            }else if(msg == Common.Message.REQUEST_CREATEIDEA){
                if ( !handleCreateIdea()){
                    System.err.println("Error in the handle create idea method!!!");
                    break ;
                }
            }else if (msg == Common.Message.REQUEST_GETTOPICSIDEAS){
                if (!handleGetTopicsIdea()){
                    System.err.println("Error in the handle get topics idea method!!!");
                    break;
                }
            }else if( msg == Common.Message.REQUEST_GET_HISTORY){
                if (!handleGetHistory()){
                    System.err.println("Error in the handle get history method!!!!!");
                    break;
                }
            }else if( msg == Common.Message.REQUEST_GETTOPIC){
                if (!handlegetTopic()){
                    System.err.println("Error in the handle get topic method!!!!!");
                    break;
                }
            }else if (msg == Common.Message.REQUEST_GET_IDEA){
                if (!handleGetIdea()){
                    System.err.println("Error in the handle get idea method");
                    break;
                }
            }
        }

        ////
        //  FIXME: Quando não há topicos para mostrar (porque não há nada na base de dados) tu simplesmente assumes que
        //  houve mega bode na ligação e assumes que o cliente se desligou, porque não dizer simplesmente ao utilizador
        //  "Amigo, não há tópicos para mostrar queres adicionar algum??"
        ////
        if ( !isLoggedIn() )
            System.out.println("Connection to UID "+uid+" dropped!");
        else
            System.out.println("Connection to a client dropped!");
    }

    private boolean handleRegistration(){
        String user, pass, date, email;
        boolean registration; // true = managed to register, false = problems registering

        if ( (user = Common.recvString(inStream)) == null)
            return false;
        if ( (pass = Common.recvString(inStream)) == null)
            return false;
        if ( (email = Common.recvString(inStream)) == null)
            return false;
        if ( (date = Common.recvString(inStream)) == null)
            return false;

        System.out.println("Vou registar:" +  user + " " + pass + " " + email + " " + date);

        try{
            registration = RMIInterface.register(user,pass,email,date);
            System.out.println("Estou no SeverClient e o registration e " + registration);
        } catch(RemoteException r){
            System.err.println("RemoteException in the ServerCliente while trying to register a new user");
            return false;
        }

        if (registration){
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                return false;
        } else {
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                return false;
            else
                System.out.println("Foi enviada mensagem de erro");
            // Here we have to return true to keep the connection to the client alive
            return true;
        }

        System.out.println("O handle registration correu impecavelmente bem e o cliente foi registado com sucesso :)");

        return true;
    }


    private boolean handleCreateTopicRequest(){
        String nome, descricao;
        boolean result = false;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( (nome = Common.recvString(inStream)) == null)
            return false;

        if ( (descricao = Common.recvString(inStream)) == null)
            return false;

        try{
             result = RMIInterface.createTopic(nome,descricao,this.uid);
        }catch(RemoteException e){
            //FIXME: Handle this!
            System.out.println("Existiu uma remoteException no handlecreatetopicrequest! " + e.getMessage());
            return false; /* FIXME: Do this? */
        }

        if (result){
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                return false;
        } else {
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                return false;
        }

        return true;
    }

    private boolean handleGetTopicsIdea(){
        int topicid = -1;
        Idea[] ideaslist = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if ( (topicid = Common.recvInt(inStream)) == -1)
            return false;

        //Now we go to the database and get the ideas

        try{
            ideaslist = RMIInterface.getIdeasFromTopic(topicid);

            if (!Common.sendInt(ideaslist.length,outStream))
                return false;

            //Send ideas
            for (int i=0;i<ideaslist.length;i++)
                ideaslist[i].writeToDataStream(outStream);

        }catch (RemoteException r){
            System.err.println("Error while getting ideas from topic");
            //FIXME: Handle this
        }

        return true;
    }

    private String[] receiveData(){
        String[] data;
        int numIdeas;
        String temp;

        if ( (numIdeas = Common.recvInt(inStream)) == -2)
            return new String[0];

        data = new String[numIdeas];
        for (int i=0;i<numIdeas;i++){
            if( (temp = Common.recvString(inStream)) == null)
                return null;
            data[i] = temp;
        }

        return data;
    }

    private int[] receiveInt(){
        int[] data;
        int numIdeas, temp;

        if ( (numIdeas = Common.recvInt(inStream)) == -2)
            return new int[0];

        data = new int[numIdeas];
        for (int i=0;i<numIdeas;i++){
            if( (temp = Common.recvInt(inStream)) == -1)
                return null;
            data[i] = temp;
        }

        return data;
    }

    private boolean sendTopics(ServerTopic[] topics){

        if(!Common.sendInt(topics.length,outStream))
            return false;

        for (int i=0;i<topics.length;i++){
            if(!topics[i].writeToDataStream(outStream))
                return false;
        }
        return true;
    }

    private boolean setRelations(int iid,int[] ideas, int relationType) throws RemoteException{

        for (int i=0;i<ideas.length;i++){
            if (!RMIInterface.setIdeasRelations(iid, ideas[i], 1)) {
                //Alert the client that the idea is not valid
                if (!Common.sendMessage(Common.Message.ERR_NO_SUCH_IID, outStream))
                    return false;
                if (!Common.sendInt(ideas[i], outStream))
                    return false;
            }
        }
        return true;
    }


    private boolean handleCreateIdea(){
        String title, description, topic;
        String[] topicsArray;
        int[] ideasForArray, ideasAgainstArray, ideasNeutralArray;
        int nshares, price, result, numMinShares;
        boolean result_topics = false, result_shares;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
           return false;

        if ( (title = Common.recvString(inStream)) == null)
            return false;

        if ( (description = Common.recvString(inStream)) == null)
            return false;

        if ( (nshares = Common.recvInt(inStream)) == -1)
            return false;
        if ( (price = Common.recvInt(inStream)) == -1)
            return false;

        if ( (numMinShares = Common.recvInt(inStream)) == -1)
            return false;

        //Receive Topics
        if ( (topicsArray = receiveData()) == null )
            return false;

        //Receive Ideas For
        if ( (ideasForArray = receiveInt()) == null)
            return false;

        //Receive Ideas Against
        if ( (ideasAgainstArray = receiveInt()) == null)
            return false;

        //Receive Ideas Neutral
        if ( (ideasNeutralArray = receiveInt()) == null)
            return false;

        try{
            result = RMIInterface.createIdea(title,description,this.uid);
            //result has the idea id

            if (result < 0){
                if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                    return false;
                return true;
            }

           result_shares = RMIInterface.setSharesIdea(this.uid,result,nshares,price,numMinShares);

            if (!result_shares){
                if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                    return false;
                return true;
            }

            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                return false;

            ////
            //  Take care of the topics
            ////

            //1st - Verify if the topics' names are correct
            for (String aTopicsArray : topicsArray) {
                topic = aTopicsArray;
                if (topic.length() > limit_characters_topic) {//Topic name too long, tell that to the client
                    if (!Common.sendMessage(Common.Message.ERR_TOPIC_NAME, outStream))
                        return false;
                    if (!Common.sendString(topic, outStream))//Send name of the topic that was wrong
                        return false;
                } else if (!Common.sendMessage(Common.Message.MSG_OK, outStream))//Everything went well with the topic
                    return false;

                //2nd - Actually bind them to the idea
                result_topics = RMIInterface.setTopicsIdea(result, topic, uid);
            }

            if (result_topics){
                if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                    return false;
            }else{
                if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                    return false;
            }

            // Take care of the ideas for
            if (!setRelations(result, ideasForArray, 1))
                return false;

            //  Take care of the ideas against
            if (!setRelations(result,ideasAgainstArray,-1))
                return false;

            //  Take care of the ideas neutral
            if(!setRelations(result,ideasNeutralArray,0))
                return false;

            //Everything ok
            if(!Common.sendMessage(Common.Message.MSG_OK,outStream))
                return false;

        }catch(RemoteException r){
            System.err.println("Error while creating a new idea");
            //FIXME: Handle this!
            return false;
        }

        return true;
    }

    private boolean handleGetIdeaByIID() {
        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        int iid;

        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        Idea idea = null;

        try {
            idea = RMIInterface.getIdeaByIID(iid);
        } catch (RemoteException e) {
            System.err.println("RMI exception while fetching an idea by its IID");
            return false; //FIXME: Do we really want to return this? WHAT TO DO WHEN RMI IS DEAD?!
        }

        if ( idea == null) {
            // There is no idea with this ID
            if ( !Common.sendMessage(Common.Message.ERR_NO_SUCH_IID, outStream))
                return false;
        } else {
            // Got the idea, let's send it
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;

            if ( !idea.writeToDataStream(outStream) )
                return false;
        }

        return true;
    }

    ////
    //  Sends the list of the topics to the user
    ////
    private boolean handleListTopicsRequest() {
        ServerTopic temp;
        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        ServerTopic[] topics = null;
        try {
            topics = RMIInterface.getTopics();
            System.out.println("Existem " + topics.length + " topicos");
        } catch (RemoteException e) {
            //FIXME: Handle this
            //e.printStackTrace();
            return false;
        }

        if ( !Common.sendInt(topics.length,outStream) )
            return false;

        for (int i=0;i<topics.length;i++){
            if (!topics[i].writeToDataStream(outStream))
                return false;
        }

        return true;
    }

    ////
    //  Sends an idea to the client
    ////
    private boolean handleGetIdea(){
        int iid;
        String title;
        Idea[] ideas = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if( (iid = Common.recvInt(inStream)) == -1)
            return false;

        if ( (title = Common.recvString(inStream)) == null)
            return false;

        try{
            ideas = RMIInterface.getIdeaByIID(iid,title);
        }catch(RemoteException r){
            return false;
        }

        if (ideas == null){
            if(!Common.sendMessage(Common.Message.ERR_NO_SUCH_IID,outStream))
                return false;
        }
        else{
            //Confirm topic is ok
            if(!Common.sendMessage(Common.Message.TOPIC_OK,outStream))
                return false;

            //Send ideas
            if (!Common.sendInt(ideas.length,outStream))
                return false;

            for (int i=0;i<ideas.length;i++)
                ideas[i].writeToDataStream(outStream);

            //Send final ok
            if(!Common.sendMessage(Common.Message.MSG_OK,outStream))
                return false;
        }

        System.out.println("Going to return true");
        return true;
    }

    ////
    // Sends a topic to the client
    ////
    private boolean handlegetTopic(){
        int tid;
        String name;
        ServerTopic topic = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if( (tid = Common.recvInt(inStream)) == -1)
            return false;

        if ( (name = Common.recvString(inStream)) == null)
            return false;

        try{
            topic = RMIInterface.getTopic(tid,name);
        }catch(RemoteException r){
            return false;
        }

        if (topic == null){
            if(!Common.sendMessage(Common.Message.ERR_TOPIC_NOT_FOUND,outStream))
                return false;
        }
        else{
            //Confirm topic is ok
            if(!Common.sendMessage(Common.Message.TOPIC_OK,outStream))
                return false;

            //Send topic
            topic.writeToDataStream(outStream);

            //Send final ok
            if(!Common.sendMessage(Common.Message.MSG_OK,outStream))
                return false;
        }

        System.out.println("Going to return true");
        return true;
    }

    ////
    //  Sends the history of a given client to that client
    ////
    private boolean handleGetHistory(){
        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        String[] history = null;

        try{
            history = RMIInterface.getHistory(uid);
        }catch(RemoteException r){
            //FIXME: Handle this
            //e.printStackTrace();
            System.out.println("Existiu uma remoteException! " + r.getMessage());
        }

        // MAndar mensagem a dizer que nao ha historico!!!!
        if (history == null){
            System.err.println("HI! I am in the handleGetHistory and history is null!!!");
            return false;
        }

        //Now send the history
        if ( !Common.sendInt(history.length,outStream))
            return false;

        for (String aHistory : history) {
            if (!Common.sendString(aHistory, outStream))
                return false;
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
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
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) ){
                return false;
            }
        }

        // Message was handled successfully
        return true;
    }
}
