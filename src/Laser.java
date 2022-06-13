import java.awt.*;

public class Laser {

    private int x, y, width, height, vy;

    public Laser(int x, int y, int vy) {
        this.x = x;
        this.y = y;
        this.width = 2;
        this.height = 5;
        this.vy = vy;
    }

    public void draw(Graphics2D g2){
        g2.drawRect(x,y,width,height);
    }

    public void move(){
        y += vy;
    }

    public Rectangle getHitBox(){
        return new Rectangle(x,y,width,height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
