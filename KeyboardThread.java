import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;


public class KeyboardThread implements Runnable{
    private GridManager grid;
    private Screen sc;
    public KeyboardThread(GridManager grid, Screen sc){
        this.grid = grid;
        this.sc = sc;
    }

    public void run(){
        while(true){
            if(Input.keyboard[37]){
                grid.moveLeftActiveBlock();
                sc.repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //TODO: handle exception
                }
    
            }
            if(Input.keyboard[39]){
                grid.moveRightActiveBlock();
                sc.repaint();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //TODO: handle exception
                }
            }
            if(Input.keyboard[38]){
                grid.rotateActiveBlock();
                sc.repaint();

                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    //TODO: handle exception
                }
            }
            if(Input.keyboard[32]){
                while(grid.isMoving()){
                    grid.moveDownActiveBlock();
                }
                sc.repaint();

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    //TODO: handle exception
                }
            }
            if(Input.keyboard[40]){
                grid.moveDownActiveBlock();
                sc.repaint();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //TODO: handle exception
                }
            }
            if(Input.keyboard[67]){
                grid.store();
                sc.repaint();
            }
            try {
                Thread.sleep(10);
                
            } catch (InterruptedException e) {
                //TODO: handle exception
            }
        }
    }
}
