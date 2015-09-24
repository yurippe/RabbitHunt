import java.util.List;
import java.util.LinkedList;

/**
 * Write a description of class Position here.
 * 
 * @author Morten D. Bech (mdbech@cs.au.dk)
 * @version July 29, 2015
 */
public class Position
{
    private int column;
    private int row;
    
    public Position(int column, int row) {
        this.column = column;
        this.row = row;
    }
    
    public int getColumn() {
        return column;
    }
    
    public int getRow() {
        return row;
    }
    
    public boolean equals(Object o) {
        if(o == null) return false;
        if(o == this) return true;
        if(o.getClass() != Position.class) return false;
        
        Position other = (Position) o;
        return column == other.column && row == other.row;
    }
    
    public int hashCode() {
        return 13 * column + 31 * row;
    }
    
    public String toString() {
        return getClass().getName() + "[c=" + column + ",r=" + row + "]";
    }
    
    public boolean validFieldPosition(Model m) {
        return 0 <= getRow() && getRow() < m.getNumberOfRows() &&
                0 <= getColumn() && getColumn() < m.getNumberOfColumns();
    }
    
    public Position getAdjacent(Direction d) {
        return new Position(getColumn() + Direction.columnChange(d), getRow() + Direction.rowChange(d));
    }
    
    public static final Iterable<Position> getNeighbourhood(Position center, Model m, boolean includeCenter) {
        List<Position> list = new LinkedList<Position>();
        
        if(includeCenter) {
            list.add(center);
        }
        
        for(Direction d : Direction.allDirections()) {
            Position neighbour = center.getAdjacent(d);
            if(neighbour.validFieldPosition(m)) {
                list.add(neighbour);
            }
        }
        
        return (Iterable<Position>) list;
    }
    
    public static final double getDistance(Position a, Position b) {
        int deltaColumn = Math.abs(a.getColumn() - b.getColumn());
        int deltaRow = Math.abs(a.getRow() - b.getRow());
        return Math.sqrt(deltaColumn * deltaColumn + deltaRow * deltaRow);
    }
}
