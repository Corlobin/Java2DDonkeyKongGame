package main;

import br.ol.donkey.DonkeyGame;
import br.ol.donkey.infra.Display;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * Main class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                DonkeyGame game = new DonkeyGame();
                Display view = new Display(game);
                JFrame frame = new JFrame();
                frame.setTitle("Java Donkey Kong");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(view);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setVisible(true);
                view.requestFocus();
                view.start();
            }

        });
    }
    
}
