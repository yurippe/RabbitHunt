import java.util.List;
import java.util.LinkedList;

/**
 * Enumeration class Direction - write a description of the enum class here
 * 
 * @author Morten D. Bech
 * @version July 29, 2015
 */
public enum Direction
{
    N, NE, E, SE, S, SW, W, NW, STAY;
    
    public static final int MIN_DIRECTION = 0;
    public static final int MAX_DIRECTION = 7;
    
    public static final int columnChange(Direction d) {
        int change = 0;
        switch(d) {
            case W:
            case NW:
            case SW:
                change = -1;
                break;
            case E:
            case NE:
            case SE:
                change = 1;
        }
        return change;
    }
    
    public static final int rowChange(Direction d) {
        int change = 0;
        switch(d) {
            case N:
            case NE:
            case NW:
                change = -1;
                break;
            case S:
            case SE:
            case SW:
                change = 1;
                break;
        }
        return change;
    }
    
    private static final List<Direction> getAllDirections() {
        List<Direction> list = new LinkedList<Direction>();
        list.add(N);
        list.add(NE);
        list.add(E);
        list.add(SE);
        list.add(S);
        list.add(SW);
        list.add(W);
        list.add(NW);
        return list;
    }
    
    public static final Iterable<Direction> allDirections() {
        return (Iterable<Direction>) getAllDirections();
    }
    
    public static final Direction turn(Direction d, int number) {
        List<Direction> list = getAllDirections();
        int start = list.indexOf(d);
        int end = (start + number) % (MAX_DIRECTION - MIN_DIRECTION + 1);
        end = end < 0 ? end + MAX_DIRECTION + 1 : end;
        return list.get(end);
    }
}
