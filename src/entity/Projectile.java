package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import main.GamePanel;
import main.Sound;
import networking.GameServer;

public class Projectile extends Entity{
	
	public Entity user;
	public GamePanel gp;
	public Graphics2D g2;	
	
	public boolean readyToFire;
	public boolean hitEnemy;
	
	public Projectile(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		solidArea = new Rectangle(24, 24, 1, 1);
		solidAreaDefaultX = 24;
		solidAreaDefaultY = 24;
		speed = 10;
		attack = 12;
		alive = false;
		maxLife = 90;
		life = maxLife;
		
		readyToFire = true;

		getImage();	
	}
	
	public void getImage() {

		up1 = setup("/projectile/fireball_up_1");
		down1 = setup("/projectile/fireball_down_1");
		left1 = setup("/projectile/fireball_left_1");
		right1 = setup("/projectile/fireball_right_1");
		
		up2 = setup("/projectile/fireball_up_2");
		down2 = setup("/projectile/fireball_down_2");
		left2 = setup("/projectile/fireball_left_2");
		right2 = setup("/projectile/fireball_right_2");
	}
	
	public void set(int worldX, int worldY, String direction, boolean alive, Entity user) {
		
		this.worldX = worldX;
		this.worldY = worldY;
		this.direction = direction;
		this.alive = alive;
		this.user = user;
		this.life = maxLife;
		
		// Projektil je ispaljen, ready parametar postavljamo na false
		readyToFire = false;
		System.out.println("Fired!");
		
		// Mehanizam punjenja
		loading();			
	}
	
	public void loading() {
		long currTime = System.nanoTime();
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					gp.playSE(Sound.reload, 20);
					Thread.sleep(500);					
					readyToFire = true;
					System.out.println("Ready to fire!");
					System.out.println("Vreme punjenja: " + (System.nanoTime()-currTime)/1000000000.0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void update() {
		
		int[] hitCoordinates = new int[2];
		if (user == gp.player) {
			hitCoordinates = gp.cChecker.checkTile(this);
			gp.cChecker.checkEnemyCollision(this);
		}
		if (collisionOn) {
			
			gp.socketClient.sendDataToServer(GameServer.stopEnemyProjectilePacket, "1");
			alive = false;
			collisionOn = false;
			System.out.println("===================================================================");
			System.out.println("Damaged row: " + hitCoordinates[0] + "\nDamaged col: " + hitCoordinates[1]);
			System.out.println("===================================================================");
			gp.player.damageWall(hitCoordinates[0], hitCoordinates[1]);
			
		} else if (hitEnemy) {

			gp.enemy.dyingCounter = 0;
			gp.enemy.dying = true;
			
			gp.socketClient.sendDataToServer(GameServer.stopEnemyProjectilePacket, "0");
			System.out.println("Protivnik pogodjen!");
			alive = false;
			hitEnemy = false;
			gp.player.damageEnemy();
			
		} else {
			switch (direction) {
				case "up": worldY -= speed; break;
				case "down": worldY += speed; break;
				case "left": worldX -= speed; break;
				case "right": worldX += speed; break;		
			}
		}
		
		life--;
		if (life < 0) {
			alive = false;
		}
		
		spriteNum++;
		if (spriteNum > 2) {
			spriteNum = 1;
		}
	}

}
