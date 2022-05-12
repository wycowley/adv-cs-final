import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JTextField;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

// import thread

public class Screen extends JPanel implements ActionListener {
    private GridManager grid;
    private BufferedReader in;
    private PrintWriter out;
    private PushbackInputStream pin;
    private int uid = (int)(Math.random()*90000)+10000;
    private int queuedRows;
    private DLList<OpponentGrid> opponentGrids = new DLList<OpponentGrid>();

    public Screen() {
        new Input(this);

        this.playSound("sound/tetris.wav", true);

        this.setLayout(null);
        grid = new GridManager();
        grid.createBlockInQueue();
        grid.createBlockInQueue();
        grid.createBlockInQueue();


        grid.createBlock();

        KeyboardThread keyManager = new KeyboardThread(grid, this);
        Thread keyboardThread = new Thread(keyManager);
        keyboardThread.start();

        this.setFocusable(true);


        

    }

    public Dimension getPreferredSize() {
        //Sets the size of the panel
        return new Dimension(660,680);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,1000,1000);
		
        if(grid.getGameOver() && Var.networking){
            for(int i = 0;i<opponentGrids.size();i++){
                opponentGrids.get(i).drawMe(g,20+Var.opponentWidthBlock*Var.gridWidth*i+20*i,20);
            }
            return;
        }

        DLList<Block> futureBlocks = grid.getFutureBlocks();
        for(int i = 0;i<futureBlocks.size();i++){
            futureBlocks.get(i).drawMe(g,360,20+i*Var.gridHeight*4);
        }


        
        grid.drawMe(g,40,20);

        if(Var.networking){
            if(!grid.getGameOver()){

                for(int i = 0;i<opponentGrids.size();i++){
                    opponentGrids.get(i).drawMe(g,360+Var.opponentWidthBlock*Var.gridWidth*i+20*i,640-20-Var.opponentHeightBlock*Var.gridHeight);
                }
            }
        }

        // draw stored block
        if(grid.getStoredBlock()!=null){
            grid.getStoredBlock().drawMe(g,40,660-Var.heightBlock-5);
        }

        // draw queued blocks
        if(Var.networking){

            g.setColor(new Color(210,210,210));
            g.fillRect(10,20,20,Var.gridHeight*Var.heightBlock);
            if(queuedRows>0){
                // TODO: Change the color dynamically
                g.setColor(Color.RED);
                g.fillRect(10,20+Var.gridHeight*Var.heightBlock-queuedRows*Var.heightBlock,20,queuedRows*Var.heightBlock);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {

    }
    public void playSound(String fileName, boolean loop) {
        if(Var.music==false){
            return;
        }
        try {
            URL url = this.getClass().getClassLoader().getResource(fileName);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(url));
            clip.start();
            if(loop){
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }

        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }
    
    public void animate(){
        while(true){
            try{
                Thread.sleep(300);
            }catch(Exception e){
                e.printStackTrace();
            }
            
            if(!Var.debug){
                grid.moveDownActiveBlock();
            }
            if(Var.networking && pin != null){
                this.readInputStream();
            }
            this.sendGrid();

            if(!grid.isMoving()){
                System.out.println("CREATING NEW BLOCK");
                if(grid.getClearedRows()!=0){
                    playSound("sound/clear.wav", false);

                }
                if(Var.networking){
                    if(grid.getClearedRows()!=0 && queuedRows==0){
                        out.println("lines"+grid.getClearedRows());
                    }
                }
                if(queuedRows>0){
                    if(grid.getClearedRows()!=0){

                        queuedRows = Math.max(0,queuedRows-grid.getClearedRows()*2);
                    }else{
                        if(queuedRows>1){
                            grid.addRows(queuedRows/2);
                        }
                        queuedRows = queuedRows%2;
                    }
                }
                grid.createBlock();
                if(grid.collision('d',grid.getActiveBlock())){
                    grid.gameOver();
                    Var.opponentHeightBlock = 15;
                    Var.opponentWidthBlock = 15;
                }

            }
            this.repaint();
            
        }
    }
    public void poll(){
        if(!Var.networking){
            return;
        }
        String hostName = "10.210.71.146"; 
		int portNumber = 3333;
        Socket serverSocket;
        try {
            serverSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(serverSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            pin = new PushbackInputStream(serverSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("UNABLE TO CONNECT");
            System.out.println(e);
            //TODO: handle exception
        }

    }

    

    
    private void handleNewGrid(String message){
        String excludingUid = message.substring(message.indexOf("uid")+3);
        int uid = Integer.parseInt(excludingUid.split(" ")[0].trim());

        // go through dllist of opponent grids and find the one with the uid
        // update the data then!
        boolean found = false;
        for(int i = 0;i<opponentGrids.size();i++){
            if(opponentGrids.get(i).getUid()==uid){
                opponentGrids.get(i).setData(message.substring(message.indexOf(" ")+1));
                found = true;
                break;
            }
        }
        if(!found){
            System.out.println("There's a new user that has entered!  Hooray!");
            OpponentGrid newGrid = new OpponentGrid();
            newGrid.setData(message.substring(message.indexOf(" ")+1));
            newGrid.setUid(uid);
            opponentGrids.add(newGrid);
        }

    }
    private void sendGrid(){
        String toSend = "uid"+uid+" "+grid.toString();
        if(Var.networking){

            out.println(toSend);
        }
    }
    private void readInputStream(){
        try {
            while(pin.available()!=0){
                String message = in.readLine();
                if(message.contains("lines")){
                    char linesToAdd = message.charAt(message.indexOf("lines")+5);
                    queuedRows += Character.getNumericValue(linesToAdd);
                }
                if(message.contains("uid")){
                    this.handleNewGrid(message);
                }
            }
            
        } catch (IOException e) {
            //TODO: handle exception
            System.out.println("ISSUE WITH SEEING IF THERE IS SOMETHING IN INPUT STREAM");
            System.out.println(e);
        }

    }


}