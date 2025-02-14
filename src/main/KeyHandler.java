package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import base.InvitePlayerForm;
import base.LoginForm;
import base.SignupForm;
import base.UserDAO;
import entity.EnemyPlayer;
import entity.Player;
import networking.GameServer;
import networking.OnlineUser;

public class KeyHandler implements KeyListener{
	
	GamePanel gp;
	public boolean upPressed, downPressed, leftPressed, rightPressed,
		shootKeyPressed;

	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		// TITLE STATE
		if (gp.gameState == GamePanel.titleState) {			
			switch (code) {
			case KeyEvent.VK_W:
				gp.playSE(Sound.cursor, 10);
				if (gp.ui.commandNum == 0) {
					gp.ui.commandNum = 2;
				} else {
					gp.ui.commandNum--;
				}
				break; 
			case KeyEvent.VK_S:
				gp.playSE(Sound.cursor, 10);
				if (gp.ui.commandNum == 2) {
					gp.ui.commandNum = 0;
				} else {
					gp.ui.commandNum++;
				}
				break; 
			case KeyEvent.VK_ENTER:
				// LOG IN
				if (gp.ui.commandNum == 0 && gp.loginForm == null && gp.signupForm == null) {
					gp.loginForm = new LoginForm(gp);
				}
				// SIGN UP
				if (gp.ui.commandNum == 1 && gp.loginForm == null && gp.signupForm == null) {
					gp.signupForm = new SignupForm(gp);
				}
				// TODO: Ukloni korisnika iz liste na serveru
				// EXIT
				if (gp.ui.commandNum == 2) {
                    if (gp.user != null) {
                        System.out.println("Logging out the user...");
                    	UserDAO.logoutUser(gp.user.getUsername());;
                    }
                    System.exit(0);
                }
				break; 
			}
		}
		
		// IN GAME STATE
		if (gp.gameState == GamePanel.inGameState) {
			switch (code) {
			case KeyEvent.VK_W: 
				upPressed = true;
				break;
			case KeyEvent.VK_S:
				downPressed = true;
				break;
			case KeyEvent.VK_A:
				leftPressed = true;
				break;
			case KeyEvent.VK_D:
				rightPressed = true;
				break;
			case KeyEvent.VK_SPACE:
				shootKeyPressed = true;
				break;
			}
		}
		
		// PLAY STATE
		if (gp.gameState == GamePanel.playState) {
			switch (code) {
			case KeyEvent.VK_W:
				gp.playSE(Sound.cursor, 10);
				if (gp.ui.commandNum == 0) {
					gp.ui.commandNum = 2;
				} else {
					gp.ui.commandNum--;
				}
				break; 
			case KeyEvent.VK_S:
				gp.playSE(Sound.cursor, 10);
				if (gp.ui.commandNum == 2) {
					gp.ui.commandNum = 0;
				} else {
					gp.ui.commandNum++;
				}
				break; 
			case KeyEvent.VK_ENTER:
				// SINGLEPLAYER
				if (gp.ui.commandNum == 0 && gp.invitePlayerForm == null) {
					UserDAO.updateUserState(gp.user.getUsername(), UserDAO.inGame);
					gp.stopMusic();
					gp.playMusic(Sound.battleMusic);
					gp.gameState = GamePanel.inGameState;
				}
				// INVITE A PLAYER INTO BATTLE
				if (gp.ui.commandNum == 1 && gp.invitePlayerForm == null) {
					gp.invitePlayerForm = new InvitePlayerForm(gp);
				}
				// TODO: Ukloni korisnika iz liste na serveru
				// EXIT
				if (gp.ui.commandNum == 2) {
                    if (gp.user != null) {
                    	System.out.println("Logging out the user...");
                    	gp.socketClient.sendDataToServer(GameServer.logoutPacket, gp.user.getUsername());
                    	if(UserDAO.logoutUser(gp.user.getUsername())) {
                    		System.out.println("User je uspesno odlogovan!");
                    	}
                    	else {
                    		System.out.println("User nije odlogovan!");
                    	}
                    }
                    System.exit(0);
                }
				break; 
			}
		}
		
		//TODO: Spojiti u jedan game end state
		// WIN STATE
		if (gp.gameState == GamePanel.winState) {
			switch (code) {
			case KeyEvent.VK_W:
				gp.playSE(Sound.cursor, 10);
				if (gp.ui.commandNum == 0) {
					gp.ui.commandNum = 1;
				} else {
					gp.ui.commandNum--;
				}
				break; 
			case KeyEvent.VK_S:
				gp.playSE(Sound.cursor, 10);
				if (gp.ui.commandNum == 1) {
					gp.ui.commandNum = 0;
				} else {
					gp.ui.commandNum++;
				}
				break; 
			case KeyEvent.VK_ENTER:
				// BACK TO MAIN MENU
				if (gp.ui.commandNum == 0) {
					UserDAO.updateUserState(gp.user.getUsername(), UserDAO.online);
					gp.gameState = GamePanel.playState;
					gp.stopMusic();
					gp.playMusic(Sound.mainMusic);
					
					gp.player = new Player(gp, gp.keyH);
					gp.enemy = new EnemyPlayer(gp);
					gp.tileM.loadMap("/maps/mapa35x35.txt");
				}
				// TODO: Ukloni korisnika iz liste na serveru
				// EXIT
				if (gp.ui.commandNum == 1) {
                    if (gp.user != null) {
                    	System.out.println("Logging out the user...");
                    	//gp.socketServer.onlineUsers.remove(gp.socketServer.getOnlineUserByUsername(gp.user.getUsername()));
                    	gp.socketClient.sendDataToServer(GameServer.logoutPacket, gp.user.getUsername());
                    	if(UserDAO.logoutUser(gp.user.getUsername())) {
                    		System.out.println("User je uspesno odlogovan!");
                    	}
                    	else {
                    		System.out.println("User nije odlogovan!");
                    	}
                    }
                    System.exit(0);
                }
				break; 
			}
		}
		
		//LOSE STATE
		if (gp.gameState == GamePanel.loseState) {
			switch (code) {
			case KeyEvent.VK_W:
				gp.playSE(Sound.cursor, 10);
				if (gp.ui.commandNum == 0) {
					gp.ui.commandNum = 1;
				} else {
					gp.ui.commandNum--;
				}
				break; 
			case KeyEvent.VK_S:
				gp.playSE(Sound.cursor, 10);
				if (gp.ui.commandNum == 1) {
					gp.ui.commandNum = 0;
				} else {
					gp.ui.commandNum++;
				}
				break; 
			case KeyEvent.VK_ENTER:
				// BACK TO MAIN MENU
				if (gp.ui.commandNum == 0) {
					UserDAO.updateUserState(gp.user.getUsername(), UserDAO.online);
					gp.gameState = GamePanel.playState;
					gp.stopMusic();
					gp.playMusic(Sound.mainMusic);
					
					gp.player = new Player(gp, gp.keyH);
					gp.enemy = new EnemyPlayer(gp);
					gp.tileM.loadMap("/maps/mapa35x35.txt");
				}
				// TODO: Ukloni korisnika iz liste na serveru
				// EXIT
				if (gp.ui.commandNum == 1) {
                    if (gp.user != null) {
                    	System.out.println("Logging out the user...");
                    	gp.socketClient.sendDataToServer(GameServer.logoutPacket, gp.user.getUsername());
                    	if(UserDAO.logoutUser(gp.user.getUsername())) {
                    		System.out.println("User je uspesno odlogovan!");
                    	}
                    	else {
                    		System.out.println("User nije odlogovan!");
                    	}
                    }
                    System.exit(0);
                }
				break; 
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPressed = false;
		}
		
		if(code == KeyEvent.VK_S) {
			
			downPressed = false;
		}
		
		if(code == KeyEvent.VK_A) {
			
			leftPressed = false;
		}
		
		if(code == KeyEvent.VK_D) {
			
			rightPressed = false;
		}
		if (code == KeyEvent.VK_SPACE) {
			
			shootKeyPressed = false;
		}
		
	}

}
