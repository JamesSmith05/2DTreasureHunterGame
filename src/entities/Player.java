package entities;

import logic.KeyHandler;
import logic.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.*;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    public int hasKey = 0;
    public boolean hasBoots;

    public Player(GamePanel gp, KeyHandler keyH) {

        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - gp.tileSize / 2;
        screenY = gp.screenHeight / 2 - gp.tileSize / 2;

        solidArea = new Rectangle(12, 16, 24, 28);  //adjust for player collision box
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
        hasBoots = false;
    }

    public void getPlayerImage() {

        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/resources/player/mage_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/resources/player/mage_up_2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/resources/player/mage_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/resources/player/mage_down_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/resources/player/mage_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/resources/player/mage_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/resources/player/mage_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/resources/player/mage_right_2.png"));
            stationary = ImageIO.read(getClass().getResourceAsStream("/resources/player/mage_stationary.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {

        if ((keyH.upPressed) || (keyH.downPressed) || (keyH.leftPressed) || (keyH.rightPressed)) {
            if (keyH.upPressed) {
                direction = "up";
            }  else if (keyH.downPressed) {
                direction = "down";
            }  else if (keyH.leftPressed) {
                direction = "left";
            }  else if (keyH.rightPressed) {
                direction = "right";
            }

            int originalSpeed = speed;

            if (hasBoots && keyH.shiftPressed) {
                speed += 2;
            }
            else if (keyH.altPressed){
                speed -= 2;
            }
            //Check tile collision
            collisionOn = false;
            gp.cChecker.checkTile(this);

            //Check object collision
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);
            //if Collision is false player can move

            if (!collisionOn) {
                switch (direction) {
                    case "up": worldY -= speed ;break;
                    case "down": worldY += speed;break;
                    case "left": worldX -= speed;break;
                    case "right": worldX += speed;break;
                    }
                }



            spriteCounter++;
            if (spriteCounter > 12) {
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
            speed = originalSpeed;
        }




    }

    public void pickUpObject(int i) {

        if (i != 999) {
            String objectName = gp.obj[i].name;

            switch (objectName) {
                case "Key":
                    gp.playSE(1);
                    hasKey++;
                    gp.obj[i] = null;
                    gp.ui.showMessage("You got a key!");
                    break;
                case "Door":
                    if (hasKey > 0) {
                        gp.playSE(3);
                        gp.obj[i] = null;
                        hasKey--;
                        gp.ui.showMessage("You used a Key!");
                    }
                    else{
                        gp.ui.showMessage("You don't have any keys");
                    }
                    break;
                case "Boots":
                    gp.playSE(2);
                    hasBoots = true;
                    gp.obj[i] = null;
                    gp.ui.showMessage("Hold shift to sprint");
                    break;
                case "Chest":
                    gp.ui.gameFinished = true;
                    gp.stopMusic();
                    gp.playSE(4);
                    break;
            }
        }
    }

    public void draw(Graphics g2) {

        BufferedImage image = null;

        switch (direction) {
            case "stationary":
                image = stationary;
                break;
            case "up":
                if (spriteNum == 1) {
                    image = up1;
                }
                if (spriteNum == 2) {
                    image = up2;
                }
                break;
            case "down":
                if (spriteNum == 1) {
                    image = down1;
                }
                if (spriteNum == 2) {
                    image = down2;
                }
                break;
            case "left":
                if (spriteNum == 1) {
                    image = left1;
                }
                if (spriteNum == 2) {
                    image = left2;
                }
                break;
            case "right":
                if (spriteNum == 1) {
                    image = right1;
                }
                if (spriteNum == 2) {
                    image = right2;
                }
                break;
        }
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);


    }
}
