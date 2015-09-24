
/**
 * Write a description of class Animal here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Animal
{
    private Model model;
    private Position position;
    
    /**
     * Constructor for objects of class Animal.
     *
     * @param model     the model needed to use Model.look and Model.distance methods
     * @param position  the position on the field in which to place this animal
     */
    public Animal(Model model, Position position) {
        this.model = model;
        this.position = position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }
    
    public Position getPosition() {
        return position;
    }
    
    /**
     * Return a random direction.
     */
    public Direction randomDirection() {
        int rest = Model.random(Direction.MIN_DIRECTION, Direction.MAX_DIRECTION + 1);
        return Direction.turn(Direction.N, rest);
    }
    
    /**
     * Finds the distance to the first visible thing in the given
     * direction, starting from this animal's current position.
     *
     * @param direction the direction in which to look
     * @return the type of the object seen
     */
    public Class<?> look(Direction d) {
        return model.look(position, d);
    }
    
    /**
     * Finds the distance to the first visible thing in the given
     * direction, starting from this animal's current position.
     * If nothing is found before the edge -1 is returned.
     *
     * @param direction the direction in which to look
     * @return the distance to the object seen in number of steps
     */
    public int distance(Direction direction) {
        return model.distance(position, direction);
    }
    
    public boolean canMove(Direction direction) {
        return model.canMove(position, direction);
    }
    
    public boolean isBeserk() {
        return model.isRabbitBeserk(this);
    }
    
    /**
     * Decides what direction to move next.
     *
     * @return the desired direction
     */
    public abstract Direction decideDirection();
}
