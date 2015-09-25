import java.util.*;
/**
 * Plans the movement of a sneaky and diabolic rabbit. 
 * The rabbit is not the best escape artist, but it sure is hungry!!
 * 
 * @author Kristian Gausel
 * @version 0.1.7
 */
public class Planner
{
    private Rabbit rabbit;
    
    //holds a list of all our planned moves
    private ArrayList<Direction> plan;
    
    //No really used, but can be used to implement planning of more moves safely
    private boolean inDanger = false;
    
    private enum moveType{
        //Don't judge me for bad naming of this enum :(
        CARROT, WAIT_FOX, ESCAPE_FOX, EAT_FOX, NONE;
    }
    
    /** 
     * Constructor for Planner
     * 
     */
    public Planner(Rabbit rabbit)
    {
       this.rabbit = rabbit;
       //initialize the plan ArrayList
       plan = new ArrayList<Direction>();
    }

    /**
     * Makes a plan of future moves
     * 
     */
    public void makePlan()
    {
        //Initialize variables
        int dist;
        Class<?> cls;
        moveType typeOfMove = moveType.NONE;
        
        //Don't use null, as that causes problems. Treat STAY as null.
        Direction plannedDirection = Direction.STAY;
        
        //will be our new plan.
        ArrayList<Direction> tmp = new ArrayList<Direction>();
        

        for(Direction d : Direction.allDirections()){
            //The distance to cls in Direction d
            dist = rabbit.distance(d);
            //The class of the object in the end of Direction d
            cls = rabbit.look(d);
            
            //If we can't see carrots or foxes.
            //None is the neutral object on the map. (Edges and Bushes)
            if(typeOfMove == moveType.NONE){
                
                //If the endpoint is an edge or a bush, move so we
                //have most possible free space to move on.
                if(cls == Edge.class || cls == Bush.class){
                    if(plannedDirection == Direction.STAY){
                        plannedDirection = d;
                    }
                    if(rabbit.distance(plannedDirection) < dist){
                        plannedDirection = d;
                    }
                }
                
                else if(cls == Carrot.class){
                    //if we see a carrot, change priority to go get it
                    plannedDirection = d;
                    typeOfMove = moveType.CARROT;
                }
                
                else if(cls == Fox.class){
                    //If we see a fox...
                    if(rabbit.isBeserk()){
                        //...and are beserking...
                        if(dist == 2){
                         //if we are 2 squares away from the fox, wait for the fox to move
                         //so we eat it, instead of being eaten
                         typeOfMove = moveType.WAIT_FOX;
                         plannedDirection = Direction.STAY;
                        }
                        else{
                        //else move close so we dont waste turns
                            typeOfMove = moveType.EAT_FOX;
                            plannedDirection = d;
                        }

                    }
                    else{
                        //if we are not beserking, run away
                        if(dist < 4){
                            typeOfMove = moveType.ESCAPE_FOX;
                            //REMEMBER, plannedDirection is STILL TOWARDS THE FOX
                            //we change this later, this is to keep a reference to where the fox is.
                            plannedDirection = d;
                        }
                    }
            }
        }
           
        else if(typeOfMove == moveType.CARROT){
            //Check if fox is too close for comfort
            //or there is a closer carrot
            
            if(cls == Fox.class){
                if(!(rabbit.isBeserk())){
                //We are not beserking
                if (dist <=1){
                    //DANGER DANGER, lets escape
                    typeOfMove = moveType.ESCAPE_FOX;
                    plannedDirection = d;
                }
               }else{
                   //We are beserking, so kill the fox, the usual way
                   if(dist == 2){
                         //if we are 2 squares away from the fox, wait for the fox to move
                         //so we eat it, instead of being eaten
                         typeOfMove = moveType.WAIT_FOX;
                         plannedDirection = d;
                        }
                   else{
                       //else move close so we dont waste turns
                       typeOfMove = moveType.EAT_FOX;
                       plannedDirection = d;
                        } 
               }
             
            }
            
            else if(cls == Carrot.class){
                //Check if there are closer carrots, and change priority to these
                if(dist < rabbit.distance(plannedDirection)){
                    plannedDirection = d;
                }
            }
        }
        
        else if(typeOfMove == moveType.ESCAPE_FOX){
            
            //We are set to escape the fox
            if(cls == Carrot.class){
                //If we see a carrot close enough to safely take, we take it so we can kill the fox instead of running
                //as running is not the best bet for this poor rabbit
                if(dist < rabbit.distance(plannedDirection) && rabbit.distance(plannedDirection) > 2){
                    typeOfMove = moveType.CARROT;
                    plannedDirection = d;
                }
            }
            
            else if(cls == Fox.class){
                //Maybe there is a close fox we need to dodge first:
                if(dist < rabbit.distance(plannedDirection)){
                    plannedDirection = d;
                }
            }

        }
        
        else if(typeOfMove == moveType.EAT_FOX){
            //The rabbit is in beserk mode, and will move towards the fox        
            if(cls == Fox.class){
                //If there is a closer fox, focus this
                if(dist < rabbit.distance(plannedDirection)){
                    plannedDirection = d;
                    
                    //If the direction changes, MAYBE we should wait, check:
                   if(rabbit.distance(d) == 2){ 
                    typeOfMove = moveType.WAIT_FOX;
                   }
                }
                

            }
        }
        
        else if(typeOfMove == moveType.WAIT_FOX){
            
            if(cls == Fox.class){
                //If another fox closes, while we wait for the original fox, 
                //change focus to this new fox
                if(dist == 1){
                    plannedDirection = d;
                    typeOfMove = moveType.EAT_FOX;
                }
            }
            
        }
    }
    if(typeOfMove == moveType.WAIT_FOX){
        //Moved down here so we can keep track of which fox we are waiting for
        //(We store the direction of the fox in plannedDirection, untill here, where we dont need it anymore)
        plannedDirection = Direction.STAY; 
    }
    if(typeOfMove == moveType.ESCAPE_FOX){
        //Escape
        Direction left, right;
        int outcomeType = 0; //1 = Left/Right ; 2 = LeftDown/RightDown
        if(rabbit.distance(plannedDirection) <= 3){
            //LeftDown/RightDown
           left = Direction.turn(plannedDirection, 3);
           right = Direction.turn(plannedDirection, 5);
           outcomeType = 2;
        }else{
            //Left/Right
           left = Direction.turn(plannedDirection, 2);
           right = Direction.turn(plannedDirection, 6);
           outcomeType = 1;
        }

        
        ArrayList<Direction> leftright = new ArrayList<Direction>();
        leftright.add(left);
        leftright.add(right);
        
        plannedDirection = getLongestFreeDirection(leftright);
        
        if(!(rabbit.canMove(plannedDirection))){
            //try emergency maneuver, because the previous option didnt work
            if(outcomeType == 1){  
                left = Direction.turn(plannedDirection, 3);
                right = Direction.turn(plannedDirection, 5);
            }else if(outcomeType == 2){
                left = Direction.turn(plannedDirection, 2);
                right = Direction.turn(plannedDirection, 6); 
            }
            
            ArrayList<Direction> leftright2 = new ArrayList<Direction>();
            leftright2.add(left);
            leftright2.add(right);
        
            plannedDirection = getLongestFreeDirection(leftright2);
        }
    }
        //Add the direction here
        //At the moment we always just plan one step
        //but it is technically possible to plan multiple steps by adding
        //them to this list.
        tmp.add(plannedDirection);
        
        //Uncommend to debug:
        //System.out.println(typeOfMove);
        
        //set this.plan to our newly formed plan
        this.plan = tmp;
    }


    /** 
     * Returns whether or not we have a plan we can follow.
     * 
     * 
     */
    public boolean hasPlan(){
       return (plan.size() > 0);
    }
    
    /** 
     * Get's the next direction to take.
     * NOTE: This works much like an iterator, and will also remove the direction from the plan arraylist
     * (Consider renaming to getNextDirection())
     */
    public Direction getDirection(){

        Direction d = plan.get(0);
        plan.remove(0);

        return d;
    }
    
    /** 
     *  Returns whether the rabbit is in danger.
     *  If the rabbit is in danger, consider making a new plan with
     *  makePlan()
     *  (At the moment always returns false, this is because we only plan one move with makePlan())
     */
    public boolean isInDanger(){
        return this.inDanger;
    }
    
    /** 
     * Returns the longest path from a collection of Directions
     * 
     */
    private Direction getLongestFreeDirection(ArrayList<Direction> directions){
        int bestLength = 0;
        Direction bestDir = Direction.STAY;
        for(Direction d : directions){
            Class<?> type = rabbit.look(d);
            //if(type == Fox.class){continue;}
            
            if(bestLength < rabbit.distance(d)) {
                bestLength = rabbit.distance(d);
                bestDir = d;
            }
        }
        
        return bestDir;
        
    }
    
}
