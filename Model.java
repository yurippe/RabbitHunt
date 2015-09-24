import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import java.util.ArrayList;

/**
 * Write a description of class Model here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Model
{
    private ConcurrentHashMap<Position, Object> field;
    private ConcurrentHashMap<Position, Integer> beserkCounter;
    private ArrayList<Fox> foxes;
    private ArrayList<Rabbit> rabbits;

    private final int numberOfColumns;
    private final int numberOfRows;

    private static final Random randomNumberGenerator = new Random();
    private long oldRandomSeed;

    private boolean isUnderConstruction = true;
    private boolean gameIsOver;
    private boolean rabbitIsAlive;
    private boolean isRabbitsTurn = true;
    private int stepsTaken = 1;

    private int noOfRabbits;
    private int noOfFoxes;
    private int noOfBushes;
    private int noOfCarrots;

    public static final int MAX_NUMBER_OF_STEPS = 100;

    public Model(int numberOfColumns, int numberOfRows) {
        field = new ConcurrentHashMap<Position, Object>();
        beserkCounter = new ConcurrentHashMap<Position, Integer>();
        foxes = new ArrayList<Fox>();
        rabbits = new ArrayList<Rabbit>();
        this.numberOfColumns = numberOfColumns;
        this.numberOfRows = numberOfRows;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public Object getObjectAtPosition(Position p) {
        return field.get(p);
    }

    public boolean isGameOver() {
        return gameIsOver;
    }

    public boolean isRabbitAlive() {
        return rabbitIsAlive;
    }

    public int getStepsTake() {
        return stepsTaken;
    }
    
    public boolean isUnderConstruction() {
        return isUnderConstruction;
    }

    /**
     * Utility method to choose a random integer from min
     * to max, inclusive.
     *
     * @param min  the smallest number to be returned
     * @param max  the largest number to be returned
     * @return a random number N, where min &lt;= N &lt; max
     */
    public static final int random(int min, int max) {
        return randomNumberGenerator.nextInt(max - min) + min;
    }

    /**
     * Sets up a new hunt with the same number of elements
     */
    public void reset() {
        reset(noOfRabbits, noOfFoxes, noOfBushes, noOfCarrots);
    }

    /**
     * Sets up a new hunt.
     */
    public void reset(int noOfRabbits, int noOfFoxes, int noOfBushes, int noOfCarrots) {
        // Game state
        gameIsOver = false;
        rabbitIsAlive = true;
        isRabbitsTurn = false;
        stepsTaken = 0;

        // populate using new random numbers
        oldRandomSeed = randomNumberGenerator.nextLong();
        randomNumberGenerator.setSeed(oldRandomSeed);
        this.noOfRabbits = noOfRabbits;
        this.noOfFoxes = noOfFoxes;
        this.noOfBushes = noOfBushes;
        this.noOfCarrots = noOfCarrots;
        populate();
    }

    /**
     * Sets up the same hunt all over again, using old random seed.
     */
    public void replay() {
        // Game state
        gameIsOver = false;
        rabbitIsAlive = true;
        isRabbitsTurn = false;
        stepsTaken = 0;

        // populate using new random numbers
        randomNumberGenerator.setSeed(oldRandomSeed);
        populate();
    }

    private void populate() {
        // protect against calls during creation of game
        isUnderConstruction = true;

        // remove any previous contents of field
        field.clear();
        beserkCounter.clear();
        rabbits.clear();
        foxes.clear();
        

        // put the rabbit in a random location
        ArrayList<Position> rabbitPositions = new ArrayList<Position>();
        for(int i = 0; i < noOfRabbits; i++) {
            Position rabbitPosition;
            do {
                rabbitPosition = new Position(random(0, getNumberOfColumns()), random(0, getNumberOfRows()));
            } while(field.containsKey(rabbitPosition));
            isRabbitsTurn = true;
            Rabbit rabbit = new Rabbit(this, rabbitPosition);
            field.put(rabbitPosition, rabbit);
            rabbits.add(rabbit);
            rabbitPositions.add(rabbitPosition);
            beserkCounter.put(rabbitPosition, 0);
        }

        // put the fox in a random location, not too close to the rabbit
        for(int i = 0; i < noOfFoxes; i++) {
            Position foxPosition;
            do {
                foxPosition = new Position(random(0, getNumberOfColumns()), random(0, getNumberOfRows()));
            } while(!validFoxPosition(rabbitPositions, foxPosition));
            isRabbitsTurn = false;
            //System.out.println("Placing fox");
            Fox fox = new Fox(this, foxPosition);
            field.put(foxPosition, fox);
            foxes.add(fox);
        }

        Carrot carrot = new Carrot();
        for(int i = 0; i < noOfCarrots; i++) {
            Position carrotPosition = new Position(random(0, getNumberOfColumns()), random(0, getNumberOfRows()));
            if(!field.containsKey(carrotPosition)) {
                field.put(carrotPosition, carrot);
            } else {
                i--;
            }
        }

        Bush bush = new Bush();
        for(int i = 0; i < noOfBushes; i++) {
            Position bushPosition = new Position(random(0, getNumberOfColumns()), random(0, getNumberOfRows()));
            if(!field.containsKey(bushPosition)) {
                field.put(bushPosition, bush);
            } else {
                i--;
            }
        }

        // finish
        isUnderConstruction = false;
    }

    private boolean validFoxPosition(ArrayList<Position> rabbitPositions, Position foxPosition) {
        for(Position rabbitPosition : rabbitPositions) {
            double distance = Position.getDistance(rabbitPosition, foxPosition);
            //System.out.println("Fox position: " + foxPosition + ", distance: " + distance);
            if(distance < (getNumberOfColumns() + getNumberOfRows()) / 5) {
                return false;
            }
        }
        return !field.containsKey(foxPosition);
    }

    public void allowSingleMove() {
        if(gameIsOver) {
            return;
        }

        // decide whose turn it is now (change isRabbitsTurn)
        isRabbitsTurn = !isRabbitsTurn;

        if(isRabbitsTurn) {
            for(Rabbit rabbit : rabbits) {
                Object fieldObject = field.get(rabbit.getPosition());
                if(fieldObject.equals(rabbit)) {
                    moveRabbit(rabbit);
                }
            }
            
            // Update beserk counters
            for(Rabbit rabbit : rabbits) {
                Integer count = beserkCounter.get(rabbit.getPosition());
                if(count > 0) {
                    beserkCounter.put(rabbit.getPosition(), count - 1);
                }
            }
            
            //System.err.println(beserkCounter);
        } else {
            for(Fox fox : foxes) {
                Object fieldObject = field.get(fox.getPosition());
                if(fieldObject.equals(fox)) {
                    moveFox(fox);
                }
            }
        }
        // increment steps taken; check for end of game after fox's turn.
        if(isRabbitsTurn) {
            stepsTaken++;
        } else if (stepsTaken >= MAX_NUMBER_OF_STEPS) {
            gameIsOver = true;
        }
    }

    private void moveRabbit(Rabbit rabbit) {
        Direction decision = rabbit.decideDirection();

        if(decision == Direction.STAY) {
            return;
        }

        Position from = rabbit.getPosition();

        if(!validMove(from, decision)) {
            return;
        }
        
        Position to = from.getAdjacent(decision);
        Object other = field.get(to);

        if(other instanceof Bush || other instanceof Rabbit) {
            return;
        }

        // Don't allow suicide!
        if(!isRabbitBeserk(rabbit) && other instanceof Fox) {
            return;
        }

        // Eat carrot
        if(other instanceof Carrot) {
            Integer count = beserkCounter.get(from);
            beserkCounter.put(from, count + 31);
        }
        
        // Eat Fox, when in beserk mode
        if(other instanceof Fox) {
            Fox fox = (Fox) other;
            field.remove(to);
            foxes.remove(fox);
            if(foxes.isEmpty()) {
                gameIsOver = true;
                rabbitIsAlive = true;
            }
        }

        field.put(to, rabbit);
        rabbit.setPosition(to);
        field.remove(from);
        Integer count = beserkCounter.remove(from);
        beserkCounter.put(to, count);
    }

    private void moveFox(Fox fox) {
        Direction decision = fox.decideDirection();
        
        if(decision == Direction.STAY) {
            return;
        }
        
        Position from = fox.getPosition();

        if(!validMove(from, decision)) {
            return;
        }
        
        Position to = from.getAdjacent(decision);
        Object other = field.get(to);

        if(other instanceof Bush || other instanceof Carrot || other instanceof Fox) {
            return;
        }

        if(other instanceof Rabbit) {
            rabbits.remove(other);
            field.remove(to);
            beserkCounter.remove(to);
            if(rabbits.isEmpty()) {
                gameIsOver = true;
                rabbitIsAlive = false;
            }
        }

        field.put(to, fox);
        fox.setPosition(to);
        field.remove(from);
    }
    
    private boolean validMove(Position p, Direction d) {
        if(d == null) {
            return false;
        }
        
        if(p == null) {
            return false;
        }
        
        Position q = p.getAdjacent(d);

        if(q == null) {
            return false;
        }
        
        if(!q.validFieldPosition(this)) {
            return false;
        }
        
        if(!(Position.getDistance(p, q) <= Math.sqrt(2))) {
            return false;
        }
        
        return true;
    }
    
    public boolean canMove(Position p, Direction d) {
        if(d == Direction.STAY) {
            return true;
        }
        
        if(!validMove(p, d)) {
            return false;
        }
        
        Object from = field.get(p);
        Position q = p.getAdjacent(d);
        Object to = field.get(q);
        
        if(from == null) {
            return false;
        }
        
        if(to instanceof Bush) {
            return false;
        }
        
        if(from instanceof Fox && to instanceof Fox) {
            return false;
        }
        
        if(from instanceof Rabbit && to instanceof Rabbit) {
            return false;
        }
        
        if(from instanceof Fox && to instanceof Carrot) {
            return false;
        }
        
        // Do not allow suicide of Rabbit jumping on to Fox
        if(from instanceof Rabbit && to instanceof Fox && !isRabbitBeserk((Animal) from)) {
            return false;
        }
        
        return true;
    }

    public Class<?> look(Position p, Direction d) {
        // Check for illegal request
        if(!checkRequest("look", p, d)) {
            return null;
        }

        Position newPosition = p;
        while(true) {
            newPosition = newPosition.getAdjacent(d);
            if(!newPosition.validFieldPosition(this)) {
                return Edge.class;
            }

            Object o = field.get(newPosition);
            if(o instanceof Rabbit) {
                return Rabbit.class;
            }
            if(o instanceof Fox) {
                return Fox.class;
            }
            if(o instanceof Bush) {
                return Bush.class;
            }
            if(o instanceof Carrot) {
                return Carrot.class;
            }
        }
    }

    public int distance(Position p, Direction d) {
        // Check for illegal request
        if(!checkRequest("distance", p, d)) {
            return -1;
        }

        int steps = 0;
        Position newPosition = p;
        while(true) {
            newPosition = newPosition.getAdjacent(d);
            steps++;
            if(!newPosition.validFieldPosition(this) || field.get(newPosition) != null) {
                return steps;
            }
        }
    }
    
    public boolean isRabbitBeserk(Animal animal) {
        Integer result = beserkCounter.get(animal.getPosition());
        if(result == null) {
            return false;
        }
        return result > 0;
    }

    private boolean checkRequest(String methodName, Position p, Direction d) {
        if(isUnderConstruction) {
            System.out.println("Error! Call to " + methodName
                + " while the hunt is still under construction!");
            return false;
        }

        Object o = field.get(p);

        if(!(o instanceof Animal)) {
            System.out.println("Error! It is illegal to move non-Animal objects. "
                + "Call originated in method" + methodName);
            return false;
        }

        if(isRabbitsTurn && !(o instanceof Rabbit)) {
            System.out.println("Error! Illegal call by fox: "
                + methodName + "(" + p + ", " + d + ")");
            return false;
        }

        if(!isRabbitsTurn && o instanceof Rabbit) {
            System.out.println("Error! Illegal call by rabbit: "
                + methodName + "(" + p + ", " + d + ")");
            return false;
        }

        return true;
    }
}
