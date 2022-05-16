public class Manager {
    private DLList<ServerThread> threads;
    private int readyCount;
    public Manager(){
        threads = new DLList<ServerThread>();
    }
    public void add(ServerThread thread){
        threads.add(thread);
    }

    public void update(String message, ServerThread from){
        if(message.equals("ready") || message.equals("notready")){
            if(message.equals("ready")){
                readyCount++;
            }else{
                readyCount--;
            }
            System.out.println(readyCount+" are connected");
            if(readyCount == threads.size()){
                for(int i = 0; i < threads.size(); i++){
                    threads.get(i).update("start");
                }
            }
        }else{

            // System.out.println("Trying to send "+message);
            for(int i = 0; i < threads.size(); i++){
                if(!threads.get(i).equals(from)){
                    threads.get(i).update(message);
                }
            }
        }
    }
}
