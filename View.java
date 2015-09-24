import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
/**
 * Write a description of class View here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class View implements Icon
{
    private Model model;
    private int width;
    private int height;
    private Icon rabbitIcon = new ImageIcon("rabbit.png");
    private Icon rabbitBeserkIcon = new ImageIcon("rabbitbeserk.png");
    private Icon foxIcon = new ImageIcon("fox.png");
    private Icon bushIcon = new ImageIcon("bush.png");
    private Icon carrotIcon = new ImageIcon("carrot.png");
    
    
    public View(Model model) {
        this.model = model;
        width = model.getNumberOfColumns() * (20 + 1) + 1;
        height = model.getNumberOfRows() * (20 + 1) + 1;
    }
    
    public int getIconHeight() {
        return height;
    }
    
    public int getIconWidth() {
        return width;
    }
    
    public void paintIcon(Component c, Graphics g, int x, int y) {
        for(int i=0; i<model.getNumberOfColumns(); i++) {
            for(int j=0; j<model.getNumberOfRows(); j++) {
                drawWhiteSquare(g, x, y, i, j);
            }
        }
        
        for(int i=0; i<model.getNumberOfColumns(); i++) {
            for(int j=0; j<model.getNumberOfRows(); j++) {
                Object obj = model.getObjectAtPosition(new Position(i,j));
                
                if(obj instanceof Rabbit) {
                    rabbitIcon.paintIcon(c, g, x + cellLeft(i) + 2, y + cellTop(j) + 2);
                }
                
                if(obj instanceof Fox) {
                    foxIcon.paintIcon(c, g, x + cellLeft(i) + 2, y + cellTop(j) + 2);
                }
                
                if(obj instanceof Bush) {
                    bushIcon.paintIcon(c, g, x + cellLeft(i) + 1, y + cellTop(j) + 2);
                }
                
                if(obj instanceof Carrot) {
                    carrotIcon.paintIcon(c, g, x + cellLeft(i) + 2, y + cellTop(j) + 2);
                }
            }
        }
        
        // draw vertical lines
        g.setColor(Color.BLACK);
        for (int i = 0; i <= model.getNumberOfColumns(); i++) {
            int left = cellLeft(i);
            g.drawLine(x + left, 0, x + left, height);
        }

        // draw horizontal lines
        for (int i = 0; i <= model.getNumberOfRows(); i++) {
            int top = cellTop(i);
            g.drawLine(x, y + top, x + width, y + top);
        }
    }
    
    private void drawWhiteSquare(Graphics g, int x, int y, int i, int j) {
        g.setColor(Color.WHITE);
        g.fillRect(x + cellLeft(i) + 1, y + cellTop(j) + 1, 20, 20);
    }
    
    private int cellTop(int row) {
        return (row * height) / model.getNumberOfRows();
    }
    
    private int cellLeft(int column) {
        return (column * width) / model.getNumberOfColumns();
    }
}
