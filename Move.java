
/**
 * Write a description of class Move here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Move
{
    // instance variables - replace the example below with your own
    private Direction direction;
    private Planner.moveType moveType;

    /**
     * Constructor for objects of class Move
     */
    public Move(Direction dir, Planner.moveType mtype)
    {
        this.direction = dir;
        this.moveType = mtype;
    }
    
    public Direction getDirection(){
        return this.direction;
    }
    
    public Planner.moveType getMoveType(){
        return this.moveType;
    }

}
