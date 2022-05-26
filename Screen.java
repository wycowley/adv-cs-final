import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;


public class Screen extends JPanel implements ActionListener {
    private GridManager grid;
    private BufferedReader in;
    private PrintWriter out;
    private PushbackInputStream pin;
    private KeyboardThread keyManager;
    private JTextField ipField;

    private int uid = (int)(Math.random()*90000)+10000;
    private int queuedRows;
    private int points;

    private boolean started = false;
    private boolean single = false;
    private boolean lobby = false;
    private boolean ready = false;
    private boolean instructions = false;

    private boolean gameFinished = false;
    private boolean won = false;

    private double tick = 0;
    private int speed = 100;

    private String prompt = "";
    private Socket serverSocket;

    private DLList<OpponentGrid> opponentGrids = new DLList<OpponentGrid>();
    private Clip mainMusic;
    private FileReader reader;
    private FileWriter writer;


    public Screen() {
        this.setLayout(null);

        new Input(this);
        keyManager = new KeyboardThread(grid, this);
        Thread keyboardThread = new Thread(keyManager);
        keyboardThread.start();

        this.read();

        this.setFocusable(true);

        ipField = new JTextField();
        ipField.setBounds(300, 550, 200, 50);
        ipField.setVisible(false);
        ipField.addActionListener(this);


        this.add(ipField);
        this.playSound("sound/tetris.wav", true);


        

    }
    public void startGame(){

        System.out.println("Started starting game");
        grid = new GridManager();
        keyManager.updateGrid(grid);

        grid.createBlockInQueue();
        grid.createBlock();

        speed = 350;
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
    public boolean instructions(){
        return this.instructions;
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
    public void changeInstructions(){
        instructions = !instructions;
        prompt = "";
        repaint();
    }
    public void setUp(){
        prompt = "";
        if(single){
            Var.networking = false;
            points = 0;
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
        if(Var.networking){

            out.println("clear");
        }
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
    
    private void drawMenu(Graphics g){
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
        this.centeredString(g,"Press m to mute sound",330,640);

        this.centeredString(g,"Press i for instructions",330,50);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(new Color(255,0,0));
        this.centeredString(g,prompt,330,80);

        // g.drawString("Use arrow keys to navigate", 100, 400);
        // g.drawString("Press space or enter to start", 100, 400);

    }
    private void drawLobby(Graphics g){
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

    }
    private void drawOpponentsGameOver(Graphics g){
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        this.centeredString(g,"You lost, sorry!",330,55);
        for(int i = 0;i<opponentGrids.size();i++){
            opponentGrids.get(i).drawMe(g,20+(Var.opponentWidthBlock*Var.gridWidth+20)*(i%3),70+(Var.opponentHeightBlock*Var.gridHeight+20)*(i/3));
        }
        return;

    }
    private void drawMultiplayerGameOver(Graphics g){
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
    }
    private void drawSingleplayerGameOver(Graphics g){
        for(int i = 0;i<5;i++){
            for(int j = 0;j<5;j++){
                Block.drawSquare(g,i*(660/5), j*(680/5), new Color(i*20+50,200,j*20+50),660/5,680/5);
            }
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        this.centeredString(g,"Great job!",330,340);
        g.setFont(new Font("Arial", Font.BOLD, 40));

        this.centeredString(g,"Total score: "+points,330,380);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        this.centeredString(g,"Press space to return to menu",330,600);

    }
    private void drawInstructions(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(0,0,1000,1000);
        ipField.setVisible(true);


        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        this.centeredString(g,"Instructions",330,90);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        this.centeredString(g,"Press i again to close",330,50);
        this.centeredString(g,"You can play multiplayer or singleplayer",330,150);
        this.centeredString(g,"Select gamemode desired on the menu screen",330,190);
        this.centeredString(g,"If playing multiplayer, server must have started already",330,210);
        this.centeredString(g,"If playing multiplayer, do not ready up until more have connected",330,230);
        this.centeredString(g,"Once in game, use left and right arrow keys to move current block",330,270);
        this.centeredString(g,"Up arrow key rotates the block",330,290);
        this.centeredString(g,"Down arrow key drops the block faster",330,310);
        this.centeredString(g,"Space drops the block immediately",330,330);
        this.centeredString(g,"C stores the block so it can be used later",330,350);

        this.centeredString(g,"Fill in complete rows to clear them",330,390);
        this.centeredString(g,"If blocks touch the top, you lose",330,410);

        this.centeredString(g,"In multiplayer, you can see other people's boards",330,450);
        this.centeredString(g,"When clearing rows, they are sent to opponents",330,470);
        this.centeredString(g,"Game ends when everyone but one person is alive",330,490);
        this.centeredString(g,"If server is not hosted at ip: "+Var.ip+",",330,530);

        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Update IP: ",100,590);


        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(new Color(255,0,0));
        this.centeredString(g,prompt,330,665);

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,1000,1000);

        if(!instructions){

            ipField.setVisible(false);
        }

        if(instructions){
            this.drawInstructions(g);
            return;
        }
        if(grid==null && lobby == false){
            this.drawMenu(g);
            return;
        }else if(grid==null && lobby){
            this.drawLobby(g);
            return;
        }
        if(grid.getGameOver() && Var.networking && !gameFinished){
            System.out.println("Drawing "+tick);
            this.drawOpponentsGameOver(g);
            return;
        }
        if(gameFinished && Var.networking){
            this.drawMultiplayerGameOver(g);
            return;
        }else if(gameFinished && !Var.networking){
            this.drawSingleplayerGameOver(g);
            return;

        }

        // draw queued blocks
        DLList<Block> futureBlocks = grid.getFutureBlocks();
        for(int i = 0;i<3;i++){
            futureBlocks.get(i).drawMe(g,360,20+i*Var.gridHeight*4);
        }


        // draw grid
        grid.drawMe(g,40,20);

        if(Var.networking){
            // draw other players grid
            if(!grid.getGameOver()){
                for(int i = 0;i<opponentGrids.size();i++){
                    opponentGrids.get(i).drawMe(g,360+(Var.opponentWidthBlock*Var.gridWidth+20)*(i/2),640-(20+Var.opponentHeightBlock*Var.gridHeight)*(i%2+1));
                }
            }
        }else{
            // draw points
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
    public void mute(){
        if(mainMusic.isOpen()){
            mainMusic.close();
            Var.music = false;
        }else{
            Var.music = true;
            playSound("sound/tetris.wav",true);
           
        }
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==ipField){
            String entry = ipField.getText();
            if(entry.trim().equals("")){
                ipField.setText("");
                ipField.setFocusable(false);
                ipField.setFocusable(true);

            }else{

                Var.ip = entry;
                prompt = "IP updated to "+entry;
                ipField.setText("");
                ipField.setFocusable(false);
                ipField.setFocusable(true);

                this.write();
            }    
            repaint();
        }
    }
    public void playSound(String fileName, boolean loop) {
        if(Var.music==false){
            return;
        }
        try {
            URL url = this.getClass().getClassLoader().getResource(fileName);
            Clip clip = AudioSystem.getClip();
            if(loop){
                // audioInputStream = ;
                clip.open(AudioSystem.getAudioInputStream(url));
                clip.start();
                
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                mainMusic = clip;
            }else{
                clip.open(AudioSystem.getAudioInputStream(url));
                clip.start();
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
                System.out.println(e);
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
                        if(input.equals("kickout")){
                            Var.networking = false;
                            lobby = false;
                            prompt = "Wait for previous game to end";
                            serverSocket.close();
                            // this.startGame();
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
            if(grid.getGameOver()){
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
                    speed =  Math.max(350-points/70,230);
                }

                int queuedRemoved = 0;
                if(queuedRows>0){
                    if(grid.getClearedRows()!=0){

                        queuedRows = Math.max(0,queuedRows-grid.getClearedRows()*2);
                        queuedRemoved = queuedRows-grid.getClearedRows()*2;
                    }else{
                        if(queuedRows>1){
                            grid.addRows(queuedRows/2);
                            this.playSound("sound/add.wav",false);
                        }
                        queuedRows = queuedRows%2;
                    }
                }
                if(Var.networking){
                    if(grid.getClearedRows()!=0 && queuedRows==0){
                        int clearedRows;
                        if(grid.getClearedRows()==4){
                            clearedRows = (int)(6.0/(Math.max((double)opponentGrids.size()/1.5,1.0)));
                        }else if(grid.getClearedRows()==3){
                            clearedRows = (int)(4.0/(Math.max((double)opponentGrids.size()/1.5,1.0)));
                        }else if(grid.getClearedRows()==2){
                            clearedRows = (int)(2.0/(Math.max((double)opponentGrids.size()/1.5,1.0)));
                        }else{
                            clearedRows = (int)(1.0/(Math.max((double)opponentGrids.size()/1.5,1.0)));
                        }
                        clearedRows-=queuedRemoved;
                        out.println("lines"+clearedRows);
                    }

                }
                grid.createBlock();
                if(grid.collision('d',grid.getActiveBlock())){
                    grid.gameOver();
                    if(Var.networking){

                        out.println("dead");
                    }else{

                        handleSingleplayerLoss();
                    }
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
        try {
            serverSocket = new Socket();
            SocketAddress address = new InetSocketAddress(hostName, portNumber);
            serverSocket.connect(address, 5000);
            out = new PrintWriter(serverSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            pin = new PushbackInputStream(serverSocket.getInputStream());
            System.out.println("Connected");

        } catch (IOException e) {
            System.out.println("UNABLE TO CONNECT");
            System.out.println(e);

            lobby = false;
            prompt = "Unable to connect to IP: "+Var.ip;
            repaint();
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
    private void handleSingleplayerLoss(){
        this.gameFinished = true;
        this.won = false;
        this.started = false;
        this.ready = false;
        this.lobby = false;
        this.speed = 100;

        // grid = null;
    }
    private void write(){

        File file = new File("./resources/IP.TXT");

        try {
            writer = new FileWriter(file);
            writer.write(Var.ip);
            writer.flush();
            writer.close(); //Make sure to close when done reading
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }
    private void read(){

        File file = new File("./resources/IP.TXT");
        try {
            reader = new FileReader(file);
            char[] text = new char[50];
            reader.read(text); // reads the content and put it to the array
            //You can also convert the char array to String, 
            String str = new String( text );
            Var.ip = str.trim();
            reader.close(); //Make sure to close when done reading
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    


}