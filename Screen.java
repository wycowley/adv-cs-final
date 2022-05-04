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
                            grid.addRows(Character.getNumericValue(linesToAdd));
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
            if(Var.networking){
                System.out.println("TRYING TO SEND IT");
                out.println("lines2");
            }
        }
        repaint();
    }



}