import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;

/**
 * Write a description of class Controller here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Controller extends JFrame implements Runnable
{
    private static final long serialVersionUID = 884538872L;
    
    // instance variables -- references to main classes
    private Model model;
    private View view;
    private Controller controller;

    // instance variables -- used for program control
    private boolean animationIsRunning = false;
    private int animationDelay = 10;
    private int lookDelay; 
    private boolean needToRedrawEverything = true;
    private boolean showEveryLook = true;
    private boolean singleStepping = false;

    // instance variables -- display elements
    private JPanel controlPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private JButton stepButton = new JButton("Step");
    private JButton runButton = new JButton("Run");
    private JButton stopButton = new JButton("Stop");
    private JButton resetButton = new JButton("Reset");
    private JButton replayButton = new JButton("Replay");
    private JScrollBar speedBar = new JScrollBar(JScrollBar.HORIZONTAL);
    private JLabel messageLabel = new JLabel("LET THE HUNT BEGIN!");

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        this.controller = this;
        setTitle("Rabbit Hunt");
        setLocation(50, 50);
        createGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Starts and controls the animation
     */
    @Override
    public void run() {
        while(animationIsRunning && !model.isGameOver()) {
            try {
                Thread.sleep(animationDelay);
            } catch(InterruptedException ie) {
                // Nothing done if the sleep fails.
            }
            step();
        }
    }

    /**
     * Tells the model to make one animation "step", then tells
     * the view to display the result.
     */
    private synchronized void step() {
        model.allowSingleMove();
        if(model.isGameOver()) {
            if(model.isRabbitAlive()) {
                messageLabel.setText("THE RABBIT HAS ESCAPED!");
            } else {
                messageLabel.setText("THE FOX(S) ATE THE RABBIT(S) AFTER "
                    + model.getStepsTake() + " TURNS!");
            }
            animationIsRunning = false;
            runButton.setEnabled(false);
            stopButton.setEnabled(false);
            stepButton.setEnabled(false);
            resetButton.setEnabled(true);
            replayButton.setEnabled(true);
        } else {
            messageLabel.setText("Step number " + model.getStepsTake());
        }

        invalidate();
        repaint();
    }

    private void createGUI() {
        // use border layout for main GUI organization
        setLayout(new BorderLayout());

        // put a label at the top of the GUI
        add(BorderLayout.NORTH, messageLabel);

        // put the view in main area of GUI
        add(BorderLayout.CENTER, new JLabel(view));

        // put control panel at bottom of GUI
        add(BorderLayout.SOUTH, controlPanel);
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(BorderLayout.WEST, new JLabel("Speed:"));
        controlPanel.add(BorderLayout.CENTER, speedBar);

        // add actions to the controls (see end of this file)
        stepButton.addActionListener(new StepButtonHandler());
        runButton.addActionListener(new RunButtonHandler());
        stopButton.addActionListener(new StopButtonHandler());
        stopButton.setEnabled(false);
        animationIsRunning = false;
        resetButton.addActionListener(new ResetButtonHandler());
        replayButton.addActionListener(new ReplayButtonHandler());
        replayButton.setEnabled(false);
        speedBar.addAdjustmentListener(new SpeedBarListener());

        // add button panel (with buttons) to control panel
        controlPanel.add(BorderLayout.EAST, buttonPanel);
        buttonPanel.add(stepButton);
        buttonPanel.add(runButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(replayButton);

        // set the speedbar up
        speedBar.setValue(50);
        setDelays(50);
        messageLabel.setText("LET THE HUNT BEGIN!");
        
        // finish up and display the GUI
        pack();
        setResizable(false);
        setVisible(true);
        invalidate();
        repaint();
    }

    /**
     * Computes the animation delay and the delay between "looks," based
     * on the current value of the speedBar.
     *
     * @param value  current value returned from the speedBar (0..90)
     */
    private void setDelays(int value) {
        animationDelay = (int)(2320 - 500 * Math.log(value + 10));
        lookDelay = animationDelay / 2 + 10;
    }

    /**
     * Inner class for handling the Step button.
     */
    class StepButtonHandler implements ActionListener {

        /**
         * Handles the Step button. If the animation is running, 
         * the Step button just halts it (by setting the
         * <code>programIsRunning</code> flag to false); otherwise, the
         * rabbit hunt is advanced one step. While single stepping,
         * all buttons except the Stop button should be enabled.
         *
         * @param e the Event that invoked this handler (ignored).
         */
        public void actionPerformed(ActionEvent e) {
            singleStepping = true;

            if (animationIsRunning) {
                animationIsRunning = false;

                // adjust button states when game ends
                if (!model.isGameOver()) {
                    runButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    resetButton.setEnabled(true);
                }
            }
            else {
                step();
            }
            replayButton.setEnabled(true);
        }
    }

    /**
     * Inner class for handling the Run button.
     */
    class RunButtonHandler implements ActionListener {

        /**
         * Sets the <code>programIsRunning</code> flag to true, and
         * creates and starts an animation Thread to do the animation.
         * While the animation is running, all buttons except the Run
         * button should be enabled.
         *
         * @param e the Event that invoked this handler (ignored).
         */
        public void actionPerformed(ActionEvent e) {
            runButton.setEnabled(false);
            stopButton.setEnabled(true);
            resetButton.setEnabled(false);
            replayButton.setEnabled(false);

            Thread animationThread = new Thread(controller);
            animationIsRunning = true;
            singleStepping = false;
            animationThread.start();
        }
    }

    /**
     * Inner class for handling the Stop button.
     */
    class StopButtonHandler implements ActionListener {

        /**
         * Sets the <code>programIsRunning</code> flag to false, thus
         * causing run() to end the Thread doing the animation.
         * While the animation is stopped, all buttons except
         * the Stop button should be enabled.
         *
         * @param e the Event that invoked this handler (ignored).
         */
        public void actionPerformed(ActionEvent e) {
            animationIsRunning = false;
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
            resetButton.setEnabled(true);
            replayButton.setEnabled(true);
        }
    }

    /**
     * Inner class for handling the Reset button.
     */
    class ResetButtonHandler implements ActionListener {

        /**
         * Recreates the entire setup, including placement of
         * bushes and animals. Cannot be performed while the
         * animation is running. Afterwards, all buttons except
         * the Stop button should be enabled.
         *
         * @param e the Event that invoked this handler (ignored).
         */
        public void actionPerformed(ActionEvent e) {
            model.reset();
            messageLabel.setText("New game");
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
            stepButton.setEnabled(true);
            replayButton.setEnabled(false);
            controller.invalidate();
            controller.repaint();
            needToRedrawEverything = false;
            singleStepping = false;
        }
    }

    /**
     * Inner class for handling the Replay button.
     */
    class ReplayButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            model.replay();
            messageLabel.setText("Instant replay");
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
            stepButton.setEnabled(true);
            controller.invalidate();
            controller.repaint();
            needToRedrawEverything = false;
            singleStepping = false;
        }
    }

    /**
     * Inner class for handling the SpeedBar control.
     */
    class SpeedBarListener implements AdjustmentListener {

        /**
         * Handles the SpeedBar control. Speed control is nonlinear,
         * and the formula used is a hack job that should be replaced.
         *
         * @param e the Event that invoked this handler (ignored).
         */
        public void adjustmentValueChanged(AdjustmentEvent e) {
            int scrollBarValue = e.getValue();
            setDelays(scrollBarValue);
            messageLabel.setText("scrollbar = " + scrollBarValue +
                ", delay = " + animationDelay);
        }
    }
}
