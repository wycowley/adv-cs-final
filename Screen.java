import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

// import thread

public class Screen extends JPanel implements ActionListener {
    private GridManager grid;
    private BufferedReader in;
    private PrintWriter out;
    private PushbackInputStream pin;
    private KeyboardThread keyManager;
    private int uid = (int)(Math.random()*90000)+10000;
    private int queuedRows;
    private int points;

    private boolean started = false;
    private boolean single = false;
    private boolean lobby = false;
    private boolean ready = false;

    private boolean gameFinished = false;
    private boolean won = false;

    private double tick = 0;
    private int speed = 100;


    private DLList<OpponentGrid> opponentGrids = new DLList<OpponentGrid>();

    public Screen() {
        this.setLayout(null);

        new Input(this);
        keyManager = new KeyboardThread(grid, this);
        Thread keyboardThread = new Thread(keyManager);
        keyboardThread.start();

        this.setFocusable(true);


        

    }
    public void startGame(){

        this.playSound("sound/tetris.wav", true);
        System.out.println("Started starting game");
        grid = new GridManager();
        keyManager.updateGrid(grid);

        grid.createBlockInQueue();
        grid.createBlock();

        speed = 300;
        started = true;


    }
    public Dimension getPreferredSize() {
        //Sets the size of the panel
        return new Dimension(660,680);
    }
    public boolean playing(){
        return this.started;
    }
    public boolean lobby(){
        return this.lobby;
    }
    public void ready(){
        if(ready==false){
            ready = true;
            out.println("ready");
        }else{
            ready = false;
            out.println("notready");
        }
    }
    public void changeFocused(){
        single = !single;
        repaint();
    }
    public void setUp(){
        if(single){
            Var.networking = false;
            this.startGame();
        }else{
            if(lobby)
                return;
            lobby = true;
            poll();
            repaint();
            // do the lobby crap
        }
    }
    public boolean gameFinished(){
        return this.gameFinished;
    }
    public void returnToMenu(){
        out.println("clear");
        this.gameFinished = false;
        this.lobby = false;
        this.started = false;
        this.ready = false;
        this.single = false;
        Var.opponentHeightBlock = 5;
        Var.opponentWidthBlock = 5;
        this.points = 0;
        this.queuedRows = 0;
        this.opponentGrids.clear();
        this.grid = null;
        this.keyManager.updateGrid(null);
        this.repaint();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,1000,1000);


        // drawn when just opening the app
        if(grid==null && lobby == false){
            g.setColor(new Color(0,0,0));
            g.fillRect(0,0,1000,1000);
    
            g.setColor(new Color(30,30,30));
            if(single){
                for(int i = 0;i<10;i++){
                    for(int j = 0;j<10;j++){
                        Block.drawSquare(g,330+i*33, (int)(Math.sin((tick/180.0*Math.PI*10+((double)(i+9))/2))*40+700-j*33), new Color(255,j*20,j*20),33,33);
                    }
                }

            }else{
                for(int i = 0;i<10;i++){
                    for(int j = 0;j<10;j++){
                        Block.drawSquare(g,i*33, (int)(Math.sin((tick/180.0*Math.PI*10+((double)i)/2))*40+700-j*33), new Color(j*20,j*20,255),33,33);
                    }
                }
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));

            this.centeredString(g, "Multiplayer", 660/4, 300);
            this.centeredString(g, "Single Player", 660/4+660/4*2, 300);


            g.setFont(new Font("Arial", Font.BOLD, 20));

            this.centeredString(g,"Use arrow keys to navigate",330,600);
            this.centeredString(g,"Press space or enter to start",330,620);

            // g.drawString("Use arrow keys to navigate", 100, 400);
            // g.drawString("Press space or enter to start", 100, 400);
            return;
        }else if(grid==null && lobby){
            g.setColor(new Color(0,0,0));
            g.fillRect(0,0,1000,1000);

            // drawn when selected multiplayer, but the game has not yet started
            if(ready){
                for(int i = 0;i<5;i++){
                    for(int j = 0;j<5;j++){
                        Block.drawSquare(g,i*(660/5), j*(680/5), new Color(i*20+50,200,j*20+50),660/5,680/5);
                    }
                }
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
    
                this.centeredString(g,"Ready",330,340);
                g.setFont(new Font("Arial", Font.BOLD, 20));

                this.centeredString(g,"Press space to unready",330,600);


            }else{
                for(int i = 0;i<5;i++){
                    for(int j = 0;j<5;j++){
                        Block.drawSquare(g,i*(660/5), j*(680/5), new Color(255,i*20+50,j*20+50),660/5,680/5);
                    }
                }
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
    
                this.centeredString(g,"Ready up!",330,340);

                g.setFont(new Font("Arial", Font.BOLD, 20));
                this.centeredString(g,"Press space to ready up",330,600);
                this.centeredString(g,"Make sure to wait until people are in the lobby",330,620);
                this.centeredString(g,"Make sure someone is running the server",330,640);

            }
            return;
        }

		
        if(grid.getGameOver() && Var.networking && !gameFinished){
            for(int i = 0;i<opponentGrids.size();i++){
                opponentGrids.get(i).drawMe(g,20+Var.opponentWidthBlock*Var.gridWidth*i+20*i,20);
            }
            return;
        }
        if(gameFinished && Var.networking){
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            if(won){
                for(int i = 0;i<5;i++){
                    for(int j = 0;j<5;j++){
                        Block.drawSquare(g,i*(660/5), j*(680/5), new Color(i*20+50,200,j*20+50),660/5,680/5);
                    }
                }
                g.setColor(Color.WHITE);
                this.centeredString(g,"You won!",330,340);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                this.centeredString(g,"Press space to return to menu",330,600);
    
            }else{
                for(int i = 0;i<5;i++){
                    for(int j = 0;j<5;j++){
                        Block.drawSquare(g,i*(660/5), j*(680/5), new Color(255,i*20+50,j*20+50),660/5,680/5);
                    }
                }
                g.setColor(Color.WHITE);

                this.centeredString(g,"You lost!",330,340);
            }
            g.setFont(new Font("Arial", Font.BOLD, 20));
            this.centeredString(g,"Press space to return to menu",330,600);
            return;
        }

        DLList<Block> futureBlocks = grid.getFutureBlocks();
        for(int i = 0;i<3;i++){
            futureBlocks.get(i).drawMe(g,360,20+i*Var.gridHeight*4);
        }


        
        grid.drawMe(g,40,20);

        if(Var.networking){
            if(!grid.getGameOver()){

                for(int i = 0;i<opponentGrids.size();i++){
                    opponentGrids.get(i).drawMe(g,360+Var.opponentWidthBlock*Var.gridWidth*i+20*i,640-20-Var.opponentHeightBlock*Var.gridHeight);
                }
            }
            
        }else{
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.setColor(Color.BLACK);
            g.drawString(points+" points", Var.widthBlock*Var.gridWidth+60, Var.heightBlock*Var.gridHeight+20);
        }

        // draw stored block
        if(grid.getStoredBlock()!=null){
            grid.getStoredBlock().drawMe(g,40,660-Var.heightBlock-5);
        }

        // draw queued rows
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
    private void centeredString(Graphics g, String s,int centeredX, int y){
        Graphics2D g2d = (Graphics2D) g;
        int stringLen = (int)g2d.getFontMetrics().getStringBounds(s, g2d).getWidth();
        g.drawString(s, centeredX-stringLen/2, y);

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
                Thread.sleep(speed);
            }catch(Exception e){
                e.printStackTrace();
            }
            tick++;
            if(lobby){
                try {
                    if(pin==null){
                        continue;
                    }
                    if(pin.available()!=0){
                        String input = in.readLine();
                        if(input.equals("start")){
                            Var.networking = true;
                            lobby = false;
    
                            this.startGame();
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
                continue;
            }
            if(!started){
                repaint();
                continue;
            }
            
            if(!Var.debug){
                grid.moveDownActiveBlock();
            }
            this.sendGrid();

            if(Var.networking && pin != null){
                this.readInputStream();
            }
            if(grid==null){
                repaint();
                continue;
            }

            if(!grid.isMoving()){
                // System.out.println("CREATING NEW BLOCK");
                if(grid.getClearedRows()!=0){
                    playSound("sound/clear.wav", false);
                    
                    switch(grid.getClearedRows()){
                        case 1:
                            points+=40;
                            break;
                        case 2:
                            points+=100;
                            break;
                        case 3:
                            points+=300;
                            break;
                        case 4:
                            points+=1200;
                            break;
                        
                    }
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
                    out.println("dead");
                    Var.opponentHeightBlock = 15;
                    Var.opponentWidthBlock = 15;
                }

            }
            this.repaint();
            
        }
    }
    public void poll(){
        // if(!Var.networking){
        //     return;
        // }
        System.out.println("Trying to connect");

        String hostName = Var.ip; 
		int portNumber = 3333;
        Socket serverSocket;
        try {
            serverSocket = new Socket(hostName, portNumber);

            out = new PrintWriter(serverSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            pin = new PushbackInputStream(serverSocket.getInputStream());
            System.out.println("Connected");
        } catch (IOException e) {
            System.out.println("UNABLE TO CONNECT");
            System.out.println(e);
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
        if(grid==null)
            return;
        String toSend = "uid"+uid+" "+grid.toString();
        if(Var.networking){

            out.println(toSend);
        }
    }
    private void readInputStream(){
        try {
            while(pin.available()!=0){
                String message = in.readLine();
                if(message.contains("win")){
                    System.out.println("YOU WIN!");
                    grid.gameOver();
                    this.handleMultiplayerWin();
                }
                if(message.contains("lose")){
                    System.out.println("YOU LOSE");
                    this.handleMultiplayerLoss();

                }
                if(message.contains("lines")){
                    char linesToAdd = message.charAt(message.indexOf("lines")+5);
                    queuedRows += Character.getNumericValue(linesToAdd);
                }
                if(message.contains("uid")){
                    this.handleNewGrid(message);
                }
            }
            
        } catch (IOException e) {
            System.out.println("ISSUE WITH SEEING IF THERE IS SOMETHING IN INPUT STREAM");
            System.out.println(e);
        }

    }
    private void handleMultiplayerWin(){
        this.gameFinished = true;
        this.won = true;
        this.started = false;
        this.ready = false;
        this.lobby = false;
        this.speed = 100;
        // grid = null;


    }
    private void handleMultiplayerLoss(){
        this.gameFinished = true;
        this.won = false;
        this.started = false;
        this.ready = false;
        this.lobby = false;
        this.speed = 100;

        // grid = null;

    }
    


}