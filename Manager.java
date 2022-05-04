public class Manager {
    private DLList<ServerThread> threads;
    public Manager(){
        threads = new DLList<ServerThread>();
    }
    public void add(ServerThread thread){
        threads.add(thread);
    }

    public void update(String message, ServerThread from){
        // System.out.println("Trying to send "+message);
        for(int i = 0; i < threads.size(); i++){
            if(!threads.get(i).equals(from)){
                threads.get(i).update(message);
            }
        }
    }
}
