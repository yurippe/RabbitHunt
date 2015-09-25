import java.util.*;
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
     * Moves out of the foxes field of view
     * (Oppgave 1)
     */
    public Direction decideDirection1(){
        
       Direction foxDir = Direction.STAY;
       for(Direction d : Direction.allDirections()){
        Class<?> type = look(d);
        if(type == Fox.class){
            foxDir = d;
            break;
        }
       }
       switch (foxDir){
            case N:
                return getLongestFreeDirection(new ArrayList<Direction>(Arrays.asList(Direction.E, Direction.W, Direction.SW, Direction.SE)));
            case NE:
                return getLongestFreeDirection(new ArrayList<Direction>(Arrays.asList(Direction.S, Direction.W, Direction.NW, Direction.SE)));
            case E:
                return getLongestFreeDirection(new ArrayList<Direction>(Arrays.asList(Direction.N, Direction.S, Direction.NW, Direction.SW)));
            case SE:
                return getLongestFreeDirection(new ArrayList<Direction>(Arrays.asList(Direction.N, Direction.W, Direction.NE, Direction.SW)));
            case S:
                return getLongestFreeDirection(new ArrayList<Direction>(Arrays.asList(Direction.E, Direction.W, Direction.NW, Direction.NE)));
            case SW:
                return getLongestFreeDirection(new ArrayList<Direction>(Arrays.asList(Direction.N, Direction.E, Direction.SE, Direction.NW)));
            case W:
                return getLongestFreeDirection(new ArrayList<Direction>(Arrays.asList(Direction.N, Direction.S, Direction.NE, Direction.SE)));
            case NW:
                return getLongestFreeDirection(new ArrayList<Direction>(Arrays.asList(Direction.NE, Direction.E, Direction.S, Direction.SW)));
            default:
                return Direction.STAY;
       }
    
    }
    
    
        
    /** 
     * Waits for the fox, then runs in circles
     * NOT effective versus more than one fox
     * (Oppgave 2)
     */
        public Direction decideDirection2(){
            //Initialize variables;
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
     * Gets the direction with most free spaces.
     *  
     */
    private Direction getLongestFreeDirection(ArrayList<Direction> directions){
        int bestLength = 0;
        Direction bestDir = Direction.STAY;
        for(Direction d : directions){
            Class<?> type = look(d);
            if(type == Fox.class){
                continue;
            }
            
            if(bestLength < distance(d)) {
                bestLength = distance(d);
                bestDir = d;
            }
        }
        
        return bestDir;
        
    }
    
    /**
     * This method is used to retrieve who the authors are.
     */
    public String getCreator() {
        return "Kristian Gausel DAT 7 201509079";
    }
}
