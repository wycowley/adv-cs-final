public class Manager {
    private DLList<ServerThread> threads;
    private DLList<ServerThread> deadThreads;
    private int readyCount;
    private int deadCount;
    public Manager(){
        threads = new DLList<ServerThread>();
        deadThreads = new DLList<ServerThread>();
    }
    public void add(ServerThread thread){
        threads.add(thread);
    }
    public void remove(ServerThread thread){
        threads.remove(thread);
    }

    public synchronized void update(String message, ServerThread from){
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
        }else if(message.equals("dead")){
            System.out.println("SOMEONE IS DEAD?");
            deadCount++;
            deadThreads.add(from);

            if(deadCount>=threads.size()-1){
                System.out.println("ONLY ONE ALIVE");
                for(int i = 0;i<threads.size();i++){
                    if(deadThreads.contains(threads.get(i))){
                        threads.get(i).update("lose");
                    }else{
                        System.out.println("Sending over a win");
                        // this thread is the winning thread
                        threads.get(i).update("win");
                        // threads.get(i).update("hi");

                    }

                }
            }
        }else if(message.equals("clear")){
            threads.clear();
            deadThreads.clear();
            readyCount = 0;
            deadCount = 0;
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
