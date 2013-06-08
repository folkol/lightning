import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class MainWindow extends JFrame {

    private static final int TIMER_FPS = 1000 / 60;
    private static final int WINDOW_HEIGHT = 1000;
    private static final int WINDOW_WIDTH = 1500;

    List<Lightning> lightnings = new ArrayList<Lightning>();

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        GameField gameField = new GameField();
        setContentPane(gameField);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - WINDOW_WIDTH / 2, screenSize.height / 2 - WINDOW_HEIGHT / 2);
        setVisible(true);

        final Timer timer = new Timer(TIMER_FPS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Lightning> deadLightnings = new ArrayList<Lightning>();
                for (Lightning l : lightnings) {
                    l.update();
                    if(l.dead) {
                        deadLightnings.add(l);
                    }
                }
                lightnings.removeAll(deadLightnings);
                repaint();
                System.out.println("Lightnings: " + lightnings.size());
            }
        });
        timer.start();

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    timer.start();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    timer.stop();
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (lightnings.size() < 10) {
                        lightnings.add(new Lightning());
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        new MainWindow();
    }

    @SuppressWarnings("serial")
    class GameField extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setBackground(Color.BLACK);
            g2d.clearRect(0, 0, 2000, 1500);
            for (Lightning l : lightnings) {
                l.draw(g2d);
            }
        }
    }

}
