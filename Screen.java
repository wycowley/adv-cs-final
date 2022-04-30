import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JTextField;



public class Screen extends JPanel implements ActionListener, KeyListener {
    private String name = "";
    private GridManager grid;
    private int blocknum = 0;
    public Screen() {
        this.setLayout(null);
        grid = new GridManager();
        grid.createBlock();
        this.setFocusable(true);

        addKeyListener(this);
        

    }

    public Dimension getPreferredSize() {
        //Sets the size of the panel
        return new Dimension(340,640);
    }

    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
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
            grid.moveDownActiveBlock();
            if(!grid.isMoving()){
                System.out.println("CREATING NEW BLOCK");
                grid.createBlock();
            }
            this.repaint();
            
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
        repaint();
    }



}