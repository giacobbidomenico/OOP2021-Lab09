package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.lab.reactivegui02.WayOfCounting;

/**
 * Implementation of a reactive gui.
 */
public class AnotherConcurrentGUI extends JFrame { 
    private static final long serialVersionUID = -5415145151L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final long WAIT_SECONDS = 10;
    private final JLabel label = new JLabel();
    /**
     * Builds a new {@link ConcurrentGUI}.
     */
    public AnotherConcurrentGUI() {
        super();
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
        this.setContentPane(mainPanel);
        /*
         * Create and set a new Agent. 
         */
        final Agent agent = new Agent();
        up.addActionListener(e -> agent.setWay(WayOfCounting.UP));
        down.addActionListener(e -> agent.setWay(WayOfCounting.DOWN));
        stop.addActionListener(e -> {
            agent.stopCounting();
            disableButtons(up, down, stop);
        });
        invokeThread(agent);
        /*
         * Create a thread that stop the agent after 10 seconds and 
         * disable the buttons of the gui.
         */
        final Thread stopAgent = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(WAIT_SECONDS));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopRun();
            }
            /**
             * Method that stop the agent and disable the buttons of the GUI.
             */
            private void stopRun() {
                agent.stopCounting();
                disableButtons(up, down, stop);
            }
        };
        invokeThread(stopAgent);
        /*
         * Set the frame. 
         */
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) (screen.getWidth() * WIDTH_PERC);
        final int sh = (int) (screen.getHeight() * HEIGHT_PERC);
        this.setSize(sw, sh);
        this.setLocationByPlatform(true);
        this.setVisible(true);
    }
    /**
     * Method that disable the buttons of the GUI.
     * @param buttons
     * @return 
     */
    private synchronized void disableButtons(final JButton...buttons) {
        for (final JButton button : buttons) {
            button.setEnabled(false);
        }
    }
    /**
     * Method that invoke the Thread.
     * @param agent
     */
    private void invokeThread(final Runnable agent) {
        new Thread(agent).start();
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
