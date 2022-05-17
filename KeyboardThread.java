// import java.awt.*;
// import java.awt.event.*;
// import javax.swing.JPanel;


public class KeyboardThread implements Runnable{
    private GridManager grid;
    private Screen sc;
    public KeyboardThread(GridManager grid, Screen sc){
        this.grid = grid;
        this.sc = sc;
    }
    public void updateGrid(GridManager grid){
        this.grid = grid;
    }
    public void run(){
        while(true){
            if(Input.keyboard[37]){
                if(grid==null){
                    sc.changeFocused();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
    
                }else{
                    grid.moveLeftActiveBlock();
                }
                sc.repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
    
            }
            if(Input.keyboard[39]){
                if(grid==null){
                    sc.changeFocused();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
    
                }else{
                    grid.moveRightActiveBlock();
                }
                sc.repaint();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
            if(Input.keyboard[38]){
                if(grid==null){

                }else{
                    grid.rotateActiveBlock();
                }
                sc.repaint();

                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
            if(Input.keyboard[32]){
                if(!sc.playing()){
                    if(sc.lobby()){
                        sc.ready();
                    }else{
                        sc.setUp();
                    }
                }else{
                    while(grid.isMoving()){
                        grid.moveDownActiveBlock();
                    }
                }
                sc.repaint();

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
            if(Input.keyboard[10]){
                if(!sc.playing()){
                    sc.setUp();
                }

            }
            if(Input.keyboard[40]){
                if(grid==null){

                }else{
                    grid.moveDownActiveBlock();
                }
                sc.repaint();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
            if(Input.keyboard[67]){
                if(grid==null){

                }else{
                    grid.store();
                }
                sc.repaint();
            }
            try {
                Thread.sleep(10);
                
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
