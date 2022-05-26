import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;

public class Runner {

    public static void main(String args[]) {

        JFrame frame = new JFrame("Tetris");

        Screen sc = new Screen();
        frame.add(sc);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        PrintWriter out = sc.getWriter();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                
            }
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing");
                sc.dead();
                exitProcedure();
            }
            @Override
            public void windowClosed(WindowEvent e) {
                
            }
            @Override
            public void windowIconified(WindowEvent e) {
                
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
                
            }

            @Override
            public void windowActivated(WindowEvent e) {
                
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                
            }
            public void exitProcedure() {
                frame.dispose();
                System.exit(0);
            }
            
        });
        
          
        sc.animate();

    }
}