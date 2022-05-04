import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JTextField;

import java.io.*;
import java.net.*;

public class Screen extends JPanel implements ActionListener, KeyListener {
    private GridManager grid;
    private BufferedReader in;
    private PrintWriter out;
    private PushbackInputStream pin;
    private int uid = (int)(Math.random()*90000)+10000;
    private int queuedRows;
    private DLList<OpponentGrid> opponentGrids = new DLList<OpponentGrid>();

    public Screen() {
        this.setLayout(null);
        grid = new GridManager();
        grid.createBlockInQueue();
        grid.createBlockInQueue();
        grid.createBlockInQueue();

        grid.createBlock();
        this.setFocusable(true);

        addKeyListener(this);
        

    }

    public Dimension getPreferredSize() {
        //Sets the size of the panel
        return new Dimension(460,640);
    }

    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
        DLList<Block> futureBlocks = grid.getFutureBlocks();

        for(int i = 0;i<futureBlocks.size();i++){
            futureBlocks.get(i).drawMe(g,360,20+i*Var.gridHeight*4);
        }
        grid.drawMe(g,20,20);
    }

    public void actionPerformed(ActionEvent e) {

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
                System.out.println("LOOKING FOR A MESSAGE");
                try {
                    if(pin.available()!=0){
                        String message = in.readLine();
                        System.out.println(message);
                        if(message.contains("lines")){
                            char linesToAdd = message.charAt(message.indexOf("lines")+5);
                            queuedRows += Character.getNumericValue(linesToAdd);
                        }
                        if(message.contains("uid")){
                            String excludingUid = message.substring(message.indexOf("uid")+3);
                            int uid = Integer.parseInt(excludingUid.split("\n")[0]);

                            // go through dllist of opponent grids and find the one with the uid
                            // update the data then!
                            boolean found = false;
                            for(int i = 0;i<opponentGrids.size();i++){
                                if(opponentGrids.get(i).getUid()==uid){
                                    opponentGrids.get(i).setData(message.substring(message.indexOf("\n")+1));
                                    found = true;
                                    break;
                                }
                            }
                            if(!found){
                                System.out.println("There's a new user that has entered!  Hooray!");
                                OpponentGrid newGrid = new OpponentGrid();
                                newGrid.setData(message.substring(message.indexOf("\n")+1));
                                newGrid.setUid(uid);
                                opponentGrids.add(newGrid);
                            }
                        }
                    }
                    
                } catch (IOException e) {
                    //TODO: handle exception
                    System.out.println("ISSUE WITH SEEING IF THERE IS SOMETHING IN INPUT STREAM");
                    System.out.println(e);
                }

            }
            if(!grid.isMoving()){
                System.out.println("CREATING NEW BLOCK");
                if(grid.getClearedRows()!=0){
                    out.println("lines"+grid.getClearedRows());
                }
                if(queuedRows>0){
                    grid.addRows(queuedRows);
                    queuedRows = 0;
                }
                grid.createBlock();
            }
            this.repaint();
            
        }
    }
    public void poll(){
        String hostName = "localhost"; 
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

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void keyPressed(KeyEvent e){
        System.out.println(e.getKeyCode());
        // arrow left
        if(e.getKeyCode()==37){
            grid.moveLeftActiveBlock();
        }
        // arrow right
        if(e.getKeyCode()==39){
            grid.moveRightActiveBlock();
        }
        if(e.getKeyCode()==40){
            grid.moveDownActiveBlock();
        }
        if(e.getKeyCode()==32){
            while(grid.isMoving()){
                grid.moveDownActiveBlock();
            }
        }
        if(e.getKeyCode()==38){
            grid.rotateActiveBlock();
        }

        if(e.getKeyCode()==84){
            queuedRows++;
            // if(Var.networking){
            //     System.out.println("TRYING TO SEND IT");
            //     out.println("lines2");
            // }
        }
        repaint();
    }



}