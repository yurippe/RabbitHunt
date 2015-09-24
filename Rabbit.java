
/**
 * Write a description of class Rabbit here.
 * 
 * @author Morten D. Bech
 * @version September 8, 2015
 */
public class Rabbit extends Animal {
    /**
     * In order to create a new Rabbit we need to provide a
     * model og and position. Do not change the signature or
     * first line of the construction. Appending code after
     * the first line is allowed.
     */
    private Planner planner;
    public Rabbit(Model model, Position position) {
        super(model, position);
        planner = new Planner(this);
    }
    
    /**
     * Decides in which direction the rabbit wants to move.
     */
    @Override
    public Direction decideDirection() {
        //If hasPlan && not inDanger:
        //followPlan()
        //else:
        //makePlan()
        if(planner.hasPlan() && !(planner.isInDanger())){
            return planner.getDirection();
        }
        else{
            planner.makePlan();
            return planner.getDirection();
        }
    }
    
    /**
     * This method is used to retrieve who the authors are.
     */
    public String getCreator() {
        return "Kristian Gausel DAT 7 201509079";
    }
}
