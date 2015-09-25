
/**
 * Write a description of class Rabbit here.
 * 
 * @author Morten D. Bech
 * @version September 8, 2015
 */
public class Rabbit extends Animal {

    private Planner planner;
    
    /**
     * In order to create a new Rabbit we need to provide a
     * model og and position. Do not change the signature or
     * first line of the construction. Appending code after
     * the first line is allowed.
     */
    public Rabbit(Model model, Position position) {
        super(model, position);
        planner = new Planner(this);
    }
    
    /**
     * Decides in which direction the rabbit wants to move.
     */
    @Override
    public Direction decideDirection(){
    
        
        return Direction.STAY;
    }
        
    /** 
     * Waits for the fox, then runs in circles
     * NOT effective versus more than one fox
     * 
     */
        public Direction decideDirection2(){
            Class<?> c;
            
            for(Direction d : Direction.allDirections()){
                c = look(d);
                
                //Be daring, and wait for the fox to get close
                if(c == Fox.class && distance(d) == 1){
                    //turn 135 degrees away from the fox
                    Direction d1 = Direction.turn(d, 3);
                    //Check to see if we can move here
                    if(canMove(d1)){
                        return d1;
                    }else{
                        //turn some more and try again, if not - defeat
                        return Direction.turn(d1,3);
                    }
                   
            }
        }
        return Direction.STAY;
       }
    
    /** 
    * Uses the planner to get a direction
    * 
    */
    public Direction decideDirectionR() {
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
