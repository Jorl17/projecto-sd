import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    private ClientConnection conn;
    private String username;
    private String password;
    private String email;
    private Scanner sc;

    Client(){
        super();
        conn = new ClientConnection();
        sc = new Scanner(System.in);
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public void setUsername(String user){
        this.username = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String askUsername(){
        System.out.print("Enter username: ");
        return sc.nextLine();
    }

    private String askPassword(){
        System.out.print("Enter password: ");
        return sc.nextLine();
    }

    private String askEmail(){
        System.out.print("Enter your email address: ");
        return sc.nextLine();
    }

    public ClientConnection getConnection(){
        return this.conn;
    }

    ////
    //  Method responsible for collecting the information needed to create a new topic, and send a request to the TCP Server
    //  in order to create that new topic in the database
    ////
    private boolean createTopic(){
        String nome, descricao;
        boolean repeat = false;

        do{
            System.out.println("Please enter the name of the topic:");
            nome = sc.nextLine();
            if (nome.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);

        repeat = false;
        do{
            System.out.println("Please enter the description of the topic:");
            descricao = sc.nextLine();
            if (descricao.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);

        return conn.createTopic(nome,descricao);
    }

    ////
    //  Method responsible for asking the user information about the topics of an idea
    //  atLeastOneElement - Indicates if the user actually needs to insert a topic or not
    ////
    private ArrayList<String> askTopics(String sentence, boolean atLeastOneElement){
        boolean repeat;
        String response;
        String[] temp;
        ArrayList<String> devolve = new ArrayList<String>();

        do{
            repeat = false;
            System.out.println(sentence);
            response = sc.nextLine();

            if (atLeastOneElement && response.equals("")){//Empty String, going to ask the user again
                System.out.println("Invalid input!");
                repeat = true;
            }else if(response.equals(""))
                return devolve;

        }while (repeat);

        temp = response.split(";");
        for (int i=0;i<temp.length;i++){
            if (!devolve.contains(temp[i]))
                devolve.add(temp[i]);
        }

        return devolve;
    }

    ////
    //  Method responsible for asking the user information about the ideas
    ////
    private ArrayList<Integer> askIdeas(String sentence){
        String ideas;
        String[] temp;
        ArrayList<Integer> devolve = new ArrayList<Integer>();
        int pos = 0, temp_num;
        boolean repeat = false;

        do{
            System.out.println(sentence);
            ideas = sc.nextLine();

            if (ideas.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
                continue;
            }

            temp = ideas.split(";");

            for (String aTemp : temp) {
                try {
                    temp_num = Integer.parseInt(aTemp);
                    if(temp_num == -1) {
                        System.out.println("Okay then, no ideas, fucker!"); //FIXME
                        return devolve;
                    }

                    else if(devolve.contains(temp_num)){
                        System.out.println("You have inserted the same idea twice, please enter again");
                        repeat = true;
                    }
                    devolve.add(temp_num);
                    pos = pos + 1;
                } catch (NumberFormatException n) {
                    System.out.println("Invalid input! Please enter again");
                    repeat = true;
                }
            }
        }while (repeat);

        return devolve;
    }

    ////
    //  Searchs an idea by its idea id and title
    ////
    private void searchIdea(){
        String temp, title;
        int iid = -1, tid = -1;
        boolean repeat;
        Idea[] userSelectedIdea;
        ClientTopic[] listTopics;
        ArrayList<Integer> listIdeasIDs = new ArrayList<Integer>();
        ArrayList<String> listTopicsNames = new ArrayList<String>();

        System.out.println("\n\nWelcome to the Idea's Search Engine!\nWe provide two ways of searching for a topic:" +
                "By its name and by its topic id. You must insert at least one of these fields\n\n");

        do{
            repeat = false;
            System.out.println("Please enter the id of the idea you want to search. If you don't know the idea id just press 'ENTER'");
            temp = sc.nextLine();

            //if the user pressed the enter key then we just ignore the topic id
            if (!temp.equals("")){
                try{
                    iid = Integer.parseInt(temp);
                } catch(NumberFormatException n){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }
        }while (repeat);

        System.out.println("Please enter the title of the idea you want to search. If you don't know the idea title just press 'ENTER'");
        title = sc.nextLine();

        if (iid == -1 && title.equals("")){
            System.out.println("Idea id and/or title were not provided, no idea can be searched");
            return ;
        }

        userSelectedIdea = conn.getIdea(iid,title);

        if (userSelectedIdea == null){
            System.out.println("No idea was found!");
            return;
        }

        System.out.println("\nIdeas found:");
        for (int i=0;i<userSelectedIdea.length;i++){
            listIdeasIDs.add(userSelectedIdea[i].getId());
            System.out.println(userSelectedIdea[i]);
        }

        //Permitir comentar ideia
        iid = -1;
        System.out.println("If you want to comment an idea, please insert its id. Otherwise just press any key");
        try{
            temp = sc.nextLine();
            iid = Integer.parseInt(temp);
        }catch(NumberFormatException n){}//We don't need to handle this exception

         if (listIdeasIDs.contains(iid)){
             //Vamos comentar a ideia - Ir buscar o topico da ideia
              listTopics = conn.getIdeaTopics(iid);

             for (int i=0;i< listTopics.length;i++)
                 listTopicsNames.add(listTopics[i].getTitle());

             commentIdea(listTopicsNames,iid);

         }else
             System.out.println("Idea(s) not comented");
    }

    ////
    // Searchs a topic by its topic id and name
    ////
    private void searchTopic(){
        String name = "", temp;
        int tid = -1;
        boolean repeat;

        System.out.println("\n\nWelcome to the Topic's Search Engine!\nWe provide two ways of searching for a topic:" +
                "By its name and by its topic id. You must insert at least one of these fields\n\n");

        do{
            repeat = false;
            System.out.println("Please enter the id of the topic you want to search. If you don't know the topic id just press 'ENTER'");
            temp = sc.nextLine();

            //if the user pressed the enter key then we just ignore the topic id
            if (!temp.equals("")){
                try{
                    tid = Integer.parseInt(temp);
                } catch(NumberFormatException n){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }
        }while (repeat);


        System.out.println("Please enter the name of the topic you want to search. If you don't know the topic name just press 'ENTER'");
        name = sc.nextLine();

        if (tid == -1 && name.equals("")){
            System.out.println("Topic id and/or name were not provided, no topic can be searched");
            return ;
        }

        ClientTopic userSelectedTopic = conn.getTopic(tid,name);

        if (userSelectedTopic == null){
            System.out.println("No topic was found!");
            return;
        }

        System.out.println("\nTopic found:\n" + userSelectedTopic);

        commentTopic(tid);
    }

    ////
    //  Method responsible for checking if the user hasn't selected the same idea for different relations
    ////
    private boolean checkIdeasRelations(ArrayList<Integer> ideasFor, ArrayList<Integer> ideasAgainst, ArrayList<Integer> ideasNeutral){

        for (Integer anIdeasFor : ideasFor) {
            if (ideasAgainst.contains(anIdeasFor) || ideasNeutral.contains(anIdeasFor)){
                System.out.println("Vou devolver false");
                return false;
            }
        }

        //Here we know that none of the items from ideasFor is in ideasAgainst nor in ideasNeutral
        for (Integer anIdeasAgainst : ideasAgainst){
            if (ideasNeutral.contains(anIdeasAgainst)){
                System.out.println("Vou devolver false2");
                return false;
            }
        }
        return true;
    }

    ////
    //  Method responsible for collecting the information needed to create a new idea, and send a request to the TCP Server in
    //  order to create that new topic in the database
    ////
    private boolean createIdea(){
        String title, description;
        ArrayList<String> topics;
        ArrayList<Integer> ideasFor, ideasAgainst, ideasNeutral;
        int nshares, price, minNumShares;
        boolean repeat = false;

        do{
            System.out.println("Please enter the title of the idea:");
            title = sc.nextLine();
            if (title.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);

        repeat = false;

        do{
            System.out.println("Please enter the description of the idea:");
            description = sc.nextLine();
            if (description.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);


        System.out.println("Please enter the number of shares for the idea:");
        nshares = sc.nextInt();

        System.out.println("Please enter the price of each share of the idea:");
        price = sc.nextInt();

        do{
            System.out.println("Please enter the minimum number of shares you don't want to sell instantaneously for the given idea:");
            minNumShares = sc.nextInt();
            if (minNumShares<0 || minNumShares>nshares)
                System.out.println("Invalid number!");
        }while(minNumShares<0 || minNumShares>nshares);


        sc.nextLine();//Clear the buffer

        topics = askTopics("Please enter the titles of the topics where you want to include your idea (USAGE: topic1;topic2)",true);

        do{
            ideasFor = askIdeas("Is your idea in favor other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            ideasAgainst = askIdeas("Is your idea against other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            ideasNeutral = askIdeas("Is your idea neutral to other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            repeat = checkIdeasRelations(ideasFor,ideasAgainst,ideasNeutral);
            if (!repeat)
                System.out.println("\nInvalid selection of the ideas' relations!Please repeat the selection!\n");
        }while (!repeat);

        return conn.createIdea(title, description,nshares,price,topics,minNumShares,ideasFor,ideasAgainst,ideasNeutral);
    }

    ////
    //  Creates a new idea, commenting directly on a topic
    ////
    private boolean commentIdea(ArrayList<String> topicTitle, int iid){
        String title, description;
        int commentType = -2, nshares, price, minNumShares;
        ArrayList<String> topics;
        ArrayList<Integer> ideasFor, ideasAgainst, ideasNeutral;
        boolean typeInserted = false, repeat = false;

        do{
            System.out.println("Please enter the title of the idea:");
            title = sc.nextLine();
            if (title.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);

        repeat = false;

        do{
            System.out.println("Please enter the description of the idea:");
            description = sc.nextLine();
            if (description.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);

        System.out.println("Please enter the number of shares for the idea:");
        nshares = sc.nextInt();

        System.out.println("Please enter the price of each share of the idea:");
        price = sc.nextInt();

        do{
            System.out.println("Please enter the minimum number of shares you don't want to sell instantaneously for the given idea:");
            minNumShares = sc.nextInt();
            if (minNumShares<0 || minNumShares>nshares)
                System.out.println("Invalid number!");
        }while(minNumShares<0 || minNumShares>nshares);

        sc.nextLine();//Clear the buffer

        topics = askTopics("If you want to include this idea in other topics, please enter their titles (USAGE: topic1;topic2)\n" +
                "If you just want to include the idea on the current topic just press 'Enter'",false);

        for(int i=0;i<topicTitle.size();i++){
            if (!topics.contains(topicTitle.get(i)))
                topics.add(topicTitle.get(i));
        }

        //Ask relation type
        do{
            System.out.println("Please select the relantionship between the idea you choose and the one you are just going to create\n(USAGE: 1-> For; -1->Against; 0-> Neutral)");
            try{
                commentType = sc.nextInt();
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
            }

            sc.nextLine();//Clear the buffer

            //FIXME: DO THIS MORE EFFICIENTLY?
            if(commentType == 1 || commentType == -1 || commentType == 0)
                typeInserted = true;

        }while(!typeInserted);

        do{
            ideasFor = askIdeas("Is your idea in favor other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            ideasAgainst = askIdeas("Is your idea against other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            ideasNeutral = askIdeas("Is your idea neutral to other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            repeat = checkIdeasRelations(ideasFor,ideasAgainst,ideasNeutral);
            if (!repeat)
                System.out.println("\nInvalid selection of the ideas' relations!Please repeat the selection!\n");
        }while (!repeat);

        if (commentType == 1){
            if ( ideasFor == null){
                ideasFor = new ArrayList<Integer>();
                ideasFor.add(iid);
            }else if(!ideasFor.contains(iid))
                ideasFor.add(iid);
        }

        else if(commentType == -1){
            if ( ideasAgainst == null){
                ideasAgainst = new ArrayList<Integer>();
                ideasAgainst.add(iid);
            }else if(!ideasAgainst.contains(iid))
                ideasAgainst.add(iid);
        }

        else{
            if ( ideasNeutral == null){
                ideasNeutral = new ArrayList<Integer>();
                ideasNeutral.add(iid);
            }else if(!ideasNeutral.contains(iid))
                ideasNeutral.add(iid);
        }

        return conn.createIdea(title, description,nshares,price,topics,minNumShares,ideasFor,ideasAgainst,ideasNeutral);
    }

    ////
    //  Prints the Welcome Screen, when the users connects to the Server
    ////
    private int printWelcomeScreen(){
        int choice;

        System.out.println("\n               Welcome!");
        System.out.println("--------------------------------------------------");
        System.out.println("There is no current session opened. Please select how you wish to connect:");
        System.out.println("1 - Login");
        System.out.println("2 - Register");
        System.out.print("Your choice: ");
        try{
            choice = sc.nextInt();
            sc.nextLine();
        }catch(InputMismatchException m){
            choice = 5;//So that we run the cicle again
        }

        return choice;
    }

    ////
    //  Prints the main menu screen, where the user can select what actions to do
    ////
    private int Menu(Scanner sc){
        int choice = -1;
        String line;
        boolean repeat;

        System.out.println("\n\nMain Menu");
        System.out.println("1 - Check a topic");//List all topics and choose one. While "inside" a topic list all ideas
        System.out.println("2 - Create a new topic");
        System.out.println("3 - Submit an idea");
        System.out.println("4 - Delete an idea");
        System.out.println("5 - Show Transaction History");
        System.out.println("6 - Search Idea");
        System.out.println("7 - Search Topic");
        System.out.println("8 - Manage User Ideas");
        System.out.println("0 - Sair");

        do{
            repeat = false;
            System.out.print("Your choice: ");

        ////
        //  FIXME: INCOMPLETE, THERE ARE OPTIONS MISSING!!!!!!!
        ////
            try{
                line = sc.nextLine();
                choice = Integer.parseInt(line);
                System.out.println("AQUI " + choice);
                if(choice < 0 || choice > 8)
                    repeat = true;
            }catch(NumberFormatException n){
                repeat = true;
            }

        }while(repeat);

        return choice;
    }

    ////
    //  Display the Account Settings Menu
    ////
    private void manageUserIdeas(){
        int option = -1;
        String line;
        Idea[] listIdeas;

        //Display user ideas
        listIdeas = conn.getIdeasFromUser();

        System.out.println("\n\nUser Ideas List:");
        for (int i=0;i<listIdeas.length;i++)
            System.out.println(listIdeas[i]);

        do{
            System.out.println("\n\n             Manage User Ideas");
            System.out.println("1 - Check idea's shares prices");
            System.out.println("2 - Set idea's shares prices");
            System.out.println("3 - Check idea's number of shares not to sell instantaneously");
            System.out.println("4 - Set idea's number of shares not to sell instantaneously");
            System.out.println("5 - Check ideas's relantionships");
            System.out.println("6 - Add ideas relantionships");
            System.out.println("0 - Return to Main Menu");
            System.out.print("Your option: ");
            try{
                line = sc.nextLine();
                option = Integer.parseInt(line);
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                option = -1;
            }
        }while (option<0 || option>6);

        switch(option){

            case 1:{
                break;
            }

            default://Go back to Main Menu
                break;
        }
    }

    private void execute(){
        int choice;
        int  login_result = 3;
        boolean stay = true;

        //  Connects to the TCP Primary Server
        conn.connect();

        /** This is here for TESTING
        conn.login("Joca","teste123");
        conn.deleteIdea(3);

        askUsername(); //Loop on something
        **/

        do{
            choice = printWelcomeScreen();

            if (choice < 1 || choice > 2){
                System.out.println("Invalid Choice!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.err.println("Client thread was interrupted");
                    //FIXME: WHAT TO DO WITH THIS EXCEPTION????
                }
            }

        }while (choice < 1 || choice > 2);

        while (stay){
            username = askUsername();
            password = askPassword();

            if (choice == 2){

                email = askEmail();
                Date date = new Date();//Get current date

                if (!conn.register(username,password,email,date)){
                    do{
                        System.out.print("Registration unsucessfull :(\n1-Try login in with another username\n2-Try registration again\nYour choice: ");
                        choice = sc.nextInt();
                        sc.nextLine();//Clear the buffer
                        stay = true;
                    } while (choice!=1 && choice!=2);

                    if (choice==1 || choice==2)
                        continue;
                }
                else{ //Now that the registration is sucessfull is time to login
                    System.out.println("Registration sucessfull");
                    stay = false;
                }
            }

            login_result = conn.login(username,password);

            if (login_result == 3){
                stay = true;
                System.out.println("Login unsucessfull!\nIf you want to register just enter 2, otherwise press any key to login again");
                String temp = sc.nextLine();
                try{
                    if (Integer.parseInt(temp) == 2)
                        choice = 2;
                }catch(NumberFormatException ignored){}//We don't need to handle this exception
            }
            else
                stay = false;
        }

        /*
        while (login_result == 3){

            username = askUsername();
            password = askPassword();

            if (choice == 2){
                while (stay){
                    stay = false;

                    email = askEmail();

                    Date date = new Date();//Get current date

                    if (!conn.register(username,password,email,date)){
                        do{
                            System.out.print("Registration unsucessfull :(\n1-Try registration again\n2-try login in with another username?\nYour choice: ");
                            choice = sc.nextInt();
                            sc.nextLine();//Clear the buffer


                        } while (choice!=1 && choice!=2);
                    }
                    else //Now tht the registration is sucessfull is time to login
                        System.out.println("Registration sucessfull");
                }
            }

            login_result = conn.login(username,password);

            if (login_result == 3){
                System.out.println("Login unsucessfull!\nIf you want to register just enter 2, otherwise press any key to login again");
                String temp = sc.nextLine();
                try{
                    if (Integer.parseInt(temp) == 2)
                        choice = 2;
                }catch(NumberFormatException ignored){}//We don't need to handle this exception
            }
        }
        */
        ////
        //  Login was successfull
        ////
        System.out.println("Login Successfull!");

        mainLoop();
    }

    private void commentTopic(int topic){
        Idea[] ideasList = conn.getTopicIdeas(topic);
        String sentence;
        int iid = -1;

        System.out.println("\nList of Ideas for the given topic:\n");
        for (Idea anIdeasList : ideasList)
            System.out.println(anIdeasList);

        //Now we are going to ask the user if he wants to create an idea
        ClientTopic[] temp = conn.getTopics();

        ArrayList<String> topicName = new ArrayList<String>();
        topicName.add(temp[topic - 1].getTitle());

        sc = new Scanner(System.in);

        System.out.println("If you want to comment an idea, please insert its id. Otherwise just press any key");
        try{
            sentence = sc.nextLine();
            iid = Integer.parseInt(sentence);
        }catch(NumberFormatException n){}//We don't need to handle this exception

        if (iid == -1)
            return ;

        if(!commentIdea(topicName,iid))
            System.err.println("Idea not commented");
        else
            System.out.println("Idea commented with success\n");
    }

    private int listTopics(){
        int selected, min_id_topic = 0, max_id_topic = 0;
        ClientTopic[] topics = conn.getTopics();

        if (topics.length>0)
            min_id_topic = topics[0].getId();

        System.out.println("\n");
        for (Topic t : topics){
            System.out.println(t);
            if(t.getId() > max_id_topic)
                max_id_topic = t.getId();
            else if(t.getId() < min_id_topic)
                min_id_topic = t.getId();
        }

        do{
            System.out.print("Which topic do you want to see? ");
            try{
                selected = sc.nextInt();
            }catch(InputMismatchException m){
                selected = -1;
            }
        }while (selected < min_id_topic || selected > max_id_topic);
        return selected;
    }

    private void mainLoop(){
        int choice, topic, idea;
        boolean stay = true;

        while(stay){
            choice = Menu(sc);

            switch(choice){

                //Logout
                case 0:{
                    System.out.println("Thank you for posting with us, hope you have a nice day! Goodbye!");
                    stay = false;
                    break;
                }

                //Check a topic - List all the topics and ask the user which one he wants. While "inside" a topic list all ideas
                case 1:{
                    topic = listTopics();
                    commentTopic(topic);

                    break;
                }

                //Create a new topic
                case 2:{
                    if (!createTopic())
                        System.out.println("Error while creating a topic! Topic already exists");
                    break;
                }
                //Submit an idea
                case 3:{
                    if (!createIdea())
                        System.out.println("Error while creating an idea!");
                    else
                        System.out.println("Idea created with success");
                    break;
                }

                //Delete an idea
                case 4:{
                    //First we need to check if there is any idea which is fully owned by the user
                    System.out.println("Delete an idea!!!!");
                    break;
                }

                //Show Transaction History
                case 5:{
                    String[] history = conn.showHistory();

                    if (history == null || history.length == 0)
                        System.out.println("There are no previous transactions registered for this user");
                    else{
                        for (String aHistory : history)
                            System.out.println(aHistory);
                    }
                    break;
                }

                //Search idea
                case 6:{
                    searchIdea();
                    break;
                }

                //Search topic
                case 7:{
                    searchTopic();
                    break;
                }

                //Account Settings
                case 8:{
                    manageUserIdeas();
                    break;
                }

                //Wrong choice
                default:{
                    System.out.println("Invalid option!");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.execute();
    }
}
