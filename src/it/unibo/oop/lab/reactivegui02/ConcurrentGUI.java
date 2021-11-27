package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Class that implements a reactive GUI.
 */
public class ConcurrentGUI {
    private final JFrame frame = new JFrame();
    private final JLabel label = new JLabel();
    /**
     * Builds a new {@link ConcurrentGUI}.
     */
    public ConcurrentGUI() {
        /*
         * Create the main panel.
         */
        final JPanel mainPanel = new JPanel(); 
        mainPanel.add(label);
        /*
         * Create 3 buttons and insert them in the mainPanel
         */
        final JButton up = new JButton("up");
        mainPanel.add(up);
        final JButton down = new JButton("down");
        mainPanel.add(down);
        final JButton stop = new JButton("stop");
        mainPanel.add(stop);
        frame.setContentPane(mainPanel);
        /*
         * Create and set a new Agent. 
         */
        final Agent agent = new Agent();
        up.addActionListener(e -> agent.setWay(WayOfCounting.UP));
        down.addActionListener(e -> agent.setWay(WayOfCounting.DOWN));
        stop.addActionListener(e -> {
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });
        new Thread(agent).start();
        /*
         * Set the frame. 
         */
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) screen.getWidth();
        final int sh = (int) screen.getHeight();
        frame.setSize(sw / 2, sh / 2);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
    /**
     * Class that implements the Agent.
     */
    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile WayOfCounting way = WayOfCounting.UP;
        private int counter;
        /**
         * Instructions that the Agent execute.
         */
        @Override
        public void run() {
            while (!this.stop) {
                try {
                    if (this.way == WayOfCounting.UP) {
                        this.counter++;
                    }
                    if (this.way == WayOfCounting.DOWN) {
                        this.counter--;
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            label.setText(Integer.toString(counter));
                        }
                    });
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         * Set how the counter is incremented.
         * @param way
         */
        public void setWay(final WayOfCounting way) {
            this.way = way;
        }
        /**
         * Method that stop the counter.
         */
        public void stopCounting() {
            this.stop = true;
        }
    }
}
