
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class SI_Panel extends JPanel {

    private Timer timer;
    private ArrayList<Alien> aliens;
    private Player player;
    private ArrayList<Laser> playerLasers, alienLasers;
    private int laserDelay, laserCounter, alienVx, lives, playerLoss; //delay is # of frames between shots, counter counts frames
    private boolean win, lose;

    public SI_Panel(int width, int height) {
        setBounds(0, 0, width, height);
        lives = 3;
        aliens = new ArrayList<>();
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 10; c++) {
                aliens.add(new Alien(40+(40*c), 40+(40*r), 20));
            }
        }

        alienVx = 1;

        player = new Player(getWidth()/2-15,getHeight()-100);

        playerLasers = new ArrayList<>();
        alienLasers = new ArrayList<>();
        laserDelay = 30; //Game balance later
        laserCounter = laserDelay;
        playerLoss = 0;

        setupKeyListener();
        timer = new Timer(1000/60, e -> update());
        timer.start();

        win = false;
        lose = false;

    }

    public void update(){
        laserCounter ++;
        updateAliens();
        player.move(getWidth());
        updateAlienLasers();
        if (playerLoss > 0){
            playerLoss --;}
        updatePlayerLasers();
        repaint();
    }

    public void updateAliens(){
        boolean hitEdge = false;

        if (aliens.size() <= 0){
            win();
        }

        for(Alien alien: aliens){
            alien.move(alienVx);
            if (alien.getX()<=0){
                hitEdge = true;
            }
            else if (alien.getX() + alien.getSize() >= getWidth()){
                hitEdge = true;
            }
        }
        if(hitEdge){
            alienVx *= -1;
            for(Alien alien: aliens){
                alien.shiftDown();
            }
        }


        //aliens shooting laser beams:
        for (Alien alien : aliens) {
            if (aliens.size() * Math.random() < 0.05) {
                //pick alien at random and fire.
                //make laser at that alien's location, and add it to alienLasers
                Laser laser = new Laser((alien.getX() + (alien.getSize() / 2)), (alien.getY() + alien.getSize()), 5);
                alienLasers.add(laser);
            }
        }

        for (Alien alien: aliens) {
            if (alien.getY() + alien.getSize() >= getHeight() - 60){
                lose();
            }
        }
    }

    public void updatePlayerLasers(){
        for (int i = 0; i < playerLasers.size(); i++) {
            Laser laser = playerLasers.get(i);
            laser.move();
            if (laser.getY() < -10) {
                playerLasers.remove(i);
                i--;
            }
        }

        for(int i = 0; i < playerLasers.size(); i++) {
            Laser laser = playerLasers.get(i);
            for (int a = 0; a < aliens.size(); a++) {
                Alien alien = aliens.get(a);
                if(laser.getHitBox().intersects(alien.getHitBox())){
                    collision(a, i);
                    a = aliens.size();
                    i--;
                }
            }
        }

    }

    public void updateAlienLasers(){
        for (int i = 0; i < alienLasers.size(); i++) {
            Laser laser = alienLasers.get(i);
            laser.move();
            if (laser.getY() > getHeight() + 10) {
                alienLasers.remove(i);
                i--;
            }
        }

        for(int i = 0; i < alienLasers.size(); i++) {
            Laser laser = alienLasers.get(i);
                if(laser.getHitBox().intersects(player.getHitBox())){
                    alienLasers.remove(i);
                    i--;
                    if (playerLoss <= 0)
                    loseLife();
                }
            }

    }

    public void setupKeyListener(){
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (laserCounter >= laserDelay) {
                        Laser laser = new Laser(player.getX() + player.getWidth() / 2, player.getY(), -5);
                        playerLasers.add(laser);
                        laserCounter = 0;
                    }
                }
                else{
                    player.pressed(e.getKeyCode()); //notify play obj that key is down
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player.released(e.getKeyCode()); //notify play obj that key is released
            }

        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 700, 778);
        Font stringFont = new Font( "SansSerif", Font.PLAIN, 18 );
        if (win){
            g2.setColor(Color.WHITE);
            g2.setFont(stringFont);
            g2.drawString("YOU WIN", 308, 290);
        }
        else if(lose){
            g2.setColor(Color.WHITE);
            g2.setFont(stringFont);
            g2.drawString("YOU LOSE", 305, 290);
        }
        else {
            for (Alien alien : aliens) {
                g2.setColor(Color.WHITE);
                alien.draw(g2);
            }
            for (Laser laser : playerLasers) {
                g2.setColor(Color.WHITE);
                laser.draw(g2);
            }
            for (Laser laser : alienLasers) {
                g2.setColor(Color.WHITE);
                laser.draw(g2);
            }
            g2.setColor(Color.WHITE);
            if (playerLoss > 0) {
                Color color = new Color(255, 255 - playerLoss * 3, 255 - playerLoss * 3);
                g2.setColor(color);
            }
            player.draw(g2);
        }
    }

    public void collision(int alien, int laser){
        aliens.remove(alien);
        playerLasers.remove(laser);
    }

    public void loseLife(){
        lives --;
        playerLoss = 85;
        if (lives <= 0){
            lose();
        }
    }

    public void win(){
        win = true;
        timer.stop();
    }

    public void lose(){
        lose = true;
        timer.stop();
    }

}