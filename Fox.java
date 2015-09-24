
/**
 * Represents a fox.
 * 
 * @author Morten D. Bech
 * @version September 10, 2015
 */
public class Fox extends Animal {
    private Direction latest;
    private boolean haveSeenRabbit;
    
    public Fox(Model model, Position position) {
        super(model, position);
        latest = Direction.STAY;
        haveSeenRabbit = false;
    }

    /**
     * Controls the movement of the fox.
     *
     * @return the direction in which the fox wishes to move.
     */
    @Override
    public Direction decideDirection() {
        Class<?> observed = null;
        
        for(Direction d : Direction.allDirections()) {
            observed = look(d);
            if(observed == Rabbit.class) {
                haveSeenRabbit = true;
                latest = d;
                return latest;
            }
        }
        
        if(haveSeenRabbit && canMove(latest)) {
            return latest;
        }
        
        if(latest != Direction.STAY && canMove(latest)) {
            return latest;
        }
        
        Direction direction = randomDirection();
        int i = 1;
        while(!canMove(direction) && i < 8) {
            direction = Direction.turn(direction, 1);
            i++;
        }
      
        if(canMove(direction)) {
            haveSeenRabbit = false;
            return latest = direction;
        } else {
            return latest;
        }
    }
}
