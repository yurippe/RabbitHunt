
/**
 * This class is responsible for starting the Rabbit Hunt.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RabbitHunt
{
    private static volatile RabbitHunt system = null;
    private final int numberOfRows = 20;
    private final int numberOfColumns = 20;
    private Model model;
    private View view;
    private Controller controller;
    
    private RabbitHunt() {
        model = new Model(numberOfColumns, numberOfRows);
        view = new View(model);
        controller = new Controller(model, view);
    }
    
    public synchronized static void populate() {
        populateWithCarrots(1, 1, 20, 0);
    }
    
    public synchronized static void populate(int noOfRabbits, int noOfFoxes, int noOfBushes) {
        populateWithCarrots(noOfRabbits, noOfFoxes, noOfBushes, 0);
    }
    
    public synchronized static void populateWithCarrots() {
        populateWithCarrots(1, 1, 20, 5);
    }
    
    public synchronized static void populateWithCarrots(int noOfRabbits, int noOfFoxes, int noOfBushes, int noOfCarrots) {
        if(system == null) {
            system = new RabbitHunt();
        }
        
        system.model.reset(noOfRabbits, noOfFoxes, noOfBushes, noOfCarrots);
        system.controller.invalidate();
        system.controller.repaint();
    }
}
