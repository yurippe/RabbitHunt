import java.util.*;
/**
 * Write a description of class Planner here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Planner
{
    private Rabbit rabbit;
    private HashMap<Direction, Direction> invertedDirections;
    private HashMap<Direction, List<Direction>> closestDirections;
    
    private ArrayList<Direction> plan;
    
    private boolean inDanger = false;
    /**
     * Constructor for objects of class Planner
     */
    
    private enum moveType{
        CARROT, WAIT_FOX, ESCAPE_FOX, EAT_FOX, NONE;
    }
    public Planner(Rabbit rabbit)
    {
       this.rabbit = rabbit;
       
        invertedDirections = new HashMap<Direction, Direction>();
        invertedDirections.put(Direction.N, Direction.S);
        invertedDirections.put(Direction.S, Direction.N);
        invertedDirections.put(Direction.W, Direction.E);
        invertedDirections.put(Direction.E, Direction.W);
        invertedDirections.put(Direction.NE, Direction.SW);
        invertedDirections.put(Direction.SW, Direction.NE);
        invertedDirections.put(Direction.NW, Direction.SE);
        invertedDirections.put(Direction.SE, Direction.NW);
        
        closestDirections = new HashMap<Direction, List<Direction>>();
        closestDirections.put(Direction.N, Arrays.asList(Direction.NE, Direction.NW));
        closestDirections.put(Direction.S, Arrays.asList(Direction.SE, Direction.SW));
        closestDirections.put(Direction.W, Arrays.asList(Direction.SW, Direction.NW));
        closestDirections.put(Direction.E, Arrays.asList(Direction.NE, Direction.SE));
        closestDirections.put(Direction.NE, Arrays.asList(Direction.N, Direction.E));
        closestDirections.put(Direction.SW, Arrays.asList(Direction.S, Direction.W));
        closestDirections.put(Direction.NW, Arrays.asList(Direction.N, Direction.W));
        closestDirections.put(Direction.SE, Arrays.asList(Direction.S, Direction.E));
        
        plan = new ArrayList<Direction>();
    }


    public void makePlan()
    {
        
        int dist;
        Class<?> cls;
        moveType typeOfMove = moveType.NONE;
        
        Direction plannedDirection = Direction.STAY;

        ArrayList<Direction> tmp = new ArrayList<Direction>();
        

        for(Direction d : Direction.allDirections()){
            dist = rabbit.distance(d);
            cls = rabbit.look(d);
            
            if(typeOfMove == moveType.NONE){
                if(cls == Edge.class || cls == Bush.class){
                    if(plannedDirection == Direction.STAY){
                        plannedDirection = d;
                    }
                    if(rabbit.distance(plannedDirection) < dist){
                        plannedDirection = d;
                    }
                }
                
                else if(cls == Carrot.class){
                    plannedDirection = d;
                    typeOfMove = moveType.CARROT;
                }
                
                else if(cls == Fox.class){
                    if(rabbit.isBeserk()){
                        if(dist == 2){
                         typeOfMove = moveType.WAIT_FOX;
                         plannedDirection = Direction.STAY;
                        }
                        else{
                            typeOfMove = moveType.EAT_FOX;
                            plannedDirection = d;
                        }

                    }
                    else{
                        if(dist < 4){
                            typeOfMove = moveType.ESCAPE_FOX;
                            //REMEMBER, plannedDirection is STILL TOWARDS THE FOX
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
                if(dist >= 3 && rabbit.distance(plannedDirection) > 1){
                    
                }else if (dist <=1){
                    //DANGER DANGER
                    typeOfMove = moveType.ESCAPE_FOX;
                    plannedDirection = d;
                }
               }else{
                   if(dist == 2){
                         typeOfMove = moveType.WAIT_FOX;
                         plannedDirection = d;
                        }
                   else{
                            typeOfMove = moveType.EAT_FOX;
                            plannedDirection = d;
                        } 
               }
             
            }
            
            else if(cls == Carrot.class){
                if(dist < rabbit.distance(plannedDirection)){
                    plannedDirection = d;
                }
            }
        }
        
        else if(typeOfMove == moveType.ESCAPE_FOX){
            if(cls == Carrot.class){
                if(dist < rabbit.distance(plannedDirection) && rabbit.distance(plannedDirection) > 2){
                    typeOfMove = moveType.CARROT;
                    plannedDirection = d;
                }
            }
            
            else if(cls == Fox.class){
                if(dist < rabbit.distance(plannedDirection)){
                    plannedDirection = d;
                }
            }

        }
        
        else if(typeOfMove == moveType.EAT_FOX){
                    
            if(cls == Fox.class){
                if(dist < rabbit.distance(plannedDirection)){
                    plannedDirection = d;
                    
                   if(rabbit.distance(d) == 2){ 
                    //If the direction changes, MAYBE we should wait, check:
                    typeOfMove = moveType.WAIT_FOX;
                   }
                }
                

            }
        }
        
        else if(typeOfMove == moveType.WAIT_FOX){
            
            if(cls == Fox.class){
                if(dist == 1){
                    plannedDirection = d;
                    typeOfMove = moveType.EAT_FOX;
                }
            }
            
        }
    }
    if(typeOfMove == moveType.WAIT_FOX){
        plannedDirection = Direction.STAY; //Moved down here so we can keep track of which fox we are waiting for
    }
    if(typeOfMove == moveType.ESCAPE_FOX){
        //Escape
        Direction left, right;
        int outcomeType = 0; //1 = L/R ; 2 = LD/RD
        if(rabbit.distance(plannedDirection) <= 3){
           left = Direction.turn(plannedDirection, 3);
           right = Direction.turn(plannedDirection, 5);
           outcomeType = 2;
        }else{
           left = Direction.turn(plannedDirection, 2);
           right = Direction.turn(plannedDirection, 6);
           outcomeType = 1;
        }

        
        ArrayList<Direction> leftright = new ArrayList<Direction>();
        leftright.add(left);
        leftright.add(right);
        
        plannedDirection = getLongestFreeDirection(leftright);
        
        if(!(rabbit.canMove(plannedDirection))){
            if(outcomeType == 1){
                //try emergency maneuver
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
        tmp.add(plannedDirection);
        
        //System.out.println(typeOfMove);
        plan = tmp;
    }


    
    public boolean hasPlan(){
       return (plan.size() > 0);
    }
    
    public Direction getDirection(){

        Direction d = plan.get(0);
        plan.remove(0);

        return d;
    }
    
    public boolean isInDanger(){
        return this.inDanger;
    }
    
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
