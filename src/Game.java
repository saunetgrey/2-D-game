import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import game2D.*;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. 

// @author: Student ID: 2940037

@SuppressWarnings("serial")
public class Game extends GameCore {
	// Useful game constants
	static int screenWidth = 512;
	static int screenHeight = 384;

	// Game constants
	float 	lift = 0.005f;
	float	gravity = 0.0025f;
	float	jumpSpeed = -0.6f;
	float	moveSpeed = 0.3f;
	int     level = 1;		// Setting the level of the map
	int x = 20+3; 		// Shift in x values for the player's draw box and collision detection
	int y = 57+10;			// Shift in y values for the player's draw box and collision detection
	int h = -57-10;		// Shift in height values for the player's draw box and collision detection
	int w = -100-5;		// Shift in width values for the player's draw box and collision detection
	int x2 = -25;		// Shift in x values for the npc's draw box and collision detection
	int y2 = 30;		// Shift in y values for the npc's draw box and collision detection
	int h2 = 17;		// Shift in height values for the npc's draw box and collision detection
	int w2 = -10;		// Shift in width values for the npc's draw box and collision detection

	// Game state flags
	boolean jumping = false;
	boolean attacking = false;
	boolean attacking2 = false;
	boolean attacking3 = false;
	boolean moveRight = false;
	boolean moveLeft = false;
	boolean running = false;
	boolean debug = false;	
	boolean onGround = false;
	boolean collision = false;
	boolean dies = false;
	boolean restartBtn = false;
	boolean soundPlayed;
	boolean gameEnd;			// To determine if the game has ended
	boolean restart;			// To check if game is restarting to control the background song
	boolean flip;     			// It is true when animation is flipped when walking left

	// Game resources
	Animation idle;
	Animation walk;
	Animation attack;
	Animation attack2;
	Animation attack3;
	Animation run;
	Animation dead;
	Animation jump;
	Animation dead2;
	Animation walk2;
	Image backg;
	Image backg2;
	Image backg3;
	Image backg4;
	Image backg5;
	Sprite	player = null;
	Sound backm;
	Sound backm2;
	ArrayList<Sprite> clouds = new ArrayList<Sprite>();
	ArrayList<Sprite> npc = new ArrayList<Sprite>();
	TileMap tmap = new TileMap();	// Our tile map, note that we load it in init()

	long total;         			// The score will be the total time elapsed since a crash

	/**
	 * The obligatory main method that creates
	 * an instance of our class and starts it running
	 * 
	 * @param args	The list of parameters this program might use (ignored)
	 */
	public static void main(String[] args) {
		Game gct = new Game();
		gct.init();
		// Start in windowed mode with the given screen height and width
		gct.run(true,screenWidth,screenHeight);
	}

	/**
	 * Initialise the class, e.g. set up variables, load images,
	 * create animations, register event handlers.
	 * 
	 * This shows you the general principles but you should create specific
	 * methods for setting up your game that can be called again when you wish to 
	 * restart the game (for example you may only want to load animations once
	 * but you could reset the positions of sprites each time you restart the game).
	 */
	public void init() {    
		gameEnd = false;
		soundPlayed = false;
		npc.clear();
		clouds.clear();

		// Load the tile map and print it out so we can check it is valid
		if (level == 1) {
			tmap.loadMap("maps", "map.txt");	
			backg = loadImage("images/backgroundIMG/1.png");
			backg2 = loadImage("images/backgroundIMG/2.png");
			backg3 = loadImage("images/backgroundIMG/3.png");
			backg4 = loadImage("images/backgroundIMG/4.png");
			backg5 = loadImage("images/backgroundIMG/5.png");
			setSize(tmap.getPixelWidth()/4, tmap.getPixelHeight());
			setVisible(true);
		}
		else {
			tmap.loadMap("maps", "map2.txt");
			backg = loadImage("images/backgroundIMG2/1.png");
			backg2 = loadImage("images/backgroundIMG2/2.png");
			backg3 = loadImage("images/backgroundIMG2/3.png");
			backg4 = loadImage("images/backgroundIMG2/4.png");
			backg5 = loadImage("images/backgroundIMG2/5.png");
			setSize(tmap.getPixelWidth()/4, tmap.getPixelHeight());
			setVisible(true);
		}

		// Create a set of background sprites that we can 
		// rearrange to give the illusion of motion

		idle = new Animation();
		idle.loadAnimationFromSheet("images/Samurai/Idle.png", 6, 1, 80);
		walk = new Animation();
		walk.loadAnimationFromSheet("images/Samurai/Walk.png", 9, 1, 80);
		jump = new Animation();
		jump.loadAnimationFromSheet("images/Samurai/Jump.png", 9, 1, 80);
		attack = new Animation();
		attack.loadAnimationFromSheet("images/Samurai/Attack_2.png", 5, 1, 80);
		attack2 = new Animation();
		attack2.loadAnimationFromSheet("images/Samurai/Attack_1.png", 4, 1, 80);
		attack3 = new Animation();
		attack3.loadAnimationFromSheet("images/Samurai/Attack_3.png", 4, 1, 80);
		dead = new Animation();
		dead.loadAnimationFromSheet("images/Samurai/Dead.png", 6, 1, 80);
		run = new Animation();
		run.loadAnimationFromSheet("images/Samurai/Run.png", 8, 1, 80);

		// Initialise the player with an animation
		player = new Sprite(idle);

		// Load a single cloud animation
		Animation ca = new Animation();
		ca.addFrame(loadImage("images/cloud.png"), 1000);

		// Load npc sprite into an animation
		walk2 = new Animation();
		walk2.loadAnimationFromSheet("images/NPC/walk.png", 6, 1, 80);
		dead2 = new Animation();
		dead2.loadAnimationFromSheet("images/NPC/Dead.png", 5, 1, 80);
		dead2.setLoop(false);
		
		// Create 20 clouds at random positions off the screen to the right
		if (level == 1) {
			createClouds(ca);
			createNpc(walk2);
		}
		else {
			createClouds(ca);
			createNpc(walk2);
		}
		initialiseGame();

		System.out.println(tmap);
	}

	/**
	 *Creates 10 NPCs
	 *
	 *@param 	Animation of the npc
	 */
	public void createNpc (Animation np) {
		Sprite n;	// Temporary reference to a sprite
		for (int c=0; c<9; c++) {	
			n = new Sprite(np);
			if (c < 4) {		
				n.setX((c*100)+1070);
				n.setY(95-100);
				n.setVelocityX(moveSpeed/4);
			}
			if (c >= 4) {		
				n.setX((c*100)+590);
				n.setY(320-100);
				n.setVelocityX(-moveSpeed/4);
			}
			n.show();
			npc.add(n);
		}
	}

	/**
	 *Creates 20 clouds in random a format based on math.random
	 *
	 *@param 	Animation of the cloud
	 */
	public void createClouds (Animation ca) {
		Sprite s;	// Temporary reference to a sprite
		for (int c=0; c<20; c++) {
			s = new Sprite(ca);
			s.setX(screenWidth + (int)(Math.random()*2000.0f));
			s.setY(29+(int)(Math.random()*50.0f));
			s.setVelocityX(-0.05f);
			s.show();
			clouds.add(s);
		}
	}

	/**
	 * You will probably want to put code to restart a game in
	 * a separate method so that you can call it when restarting
	 * the game when the player loses.
	 */
	public void initialiseGame() {
		total = 0;

		player.setPosition(225,230);
		//player.setPosition(1940,230);
		player.setVelocity(0,0);
		player.show();

		if (level == 1) {	
			if (restart == true)
			{
				backm.stopSound();
				restart = false;
			}
			backm = new Sound ("sounds/1st.mid", true);
			backm.start();
			//System.out.println("starting 1st");
		}
		if (level == 2) {
			if (restart == true)
			{
				backm2.stopSound();
				restart = false;
			}
			backm.stopSound();
			backm2 = new Sound ("sounds/2nd.mid", true);
			backm2.start();
			//System.out.println("starting 2nd");
		}
	}

	@Override
	/**
	 * Draw the current state of the game. Note the sample use of
	 * debugging output that is drawn directly to the game screen.
	 * 
	 * @param g 	This is the graphics object
	 */
	public void draw(Graphics2D g) {    	
		// Be careful about the order in which you draw objects - you
		// should draw the background first, then work your way 'forward'

		// First work out how much we need to shift the view in order to
		// see where the player is. To do this, we adjust the offset so that
		// it is relative to the player's position along with a shift
		int xo = -(int)player.getX() + 100;
		int yo = 0; // -(int)player.getY() + 100;

		if (xo > 0)
			xo = 0;
		//changes the offsets once the end of the map is reached                
		else if (player.getX()>=1995) { 
			xo = -(int) 1905;
		}

		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.drawImage(backg, (int) (xo * 0.1f)+8, 0, backg.getWidth(null)+246, backg.getHeight(null)+190, null, null);
		g.drawImage(backg2, (int) (xo * 0.1f)+8, 0, backg2.getWidth(null)+246, backg2.getHeight(null)+190, null, null);
		g.drawImage(backg3, (int) (xo * 0.3f)+8, 0, backg3.getWidth(null)+246, backg3.getHeight(null)+190, null, null);
		g.drawImage(backg4, (int) (xo * 0.4f)+8, 0, backg4.getWidth(null)+246, backg4.getHeight(null)+190, null, null);
		g.drawImage(backg5, (int) (xo * 0.5f)+8, 0, backg5.getWidth(null)+246, backg5.getHeight(null)+190, null, null);

		if (moveLeft) { 
			player.setScale(-1, 1);
			player.drawTransformed(g);
			flip = true;
		}

		// Apply offsets to sprites then draw them
		for (Sprite s: clouds) {
			s.setOffsets(xo,yo);
			s.draw(g);
		}

		// Apply offsets to NPC and transform sprites before drawing them
		for (Sprite n: npc) {
			n.setOffsets(xo,yo);
			n.setScale(2, 2);
			n.drawTransformed(g);	
		}

		// Apply offsets to tile map and draw  it
		tmap.draw(g,xo,yo);         

		// Apply offsets to player and draw 
		player.setOffsets(xo, yo);

		if (!moveLeft) {
			player.draw(g);
		}

		// Show score and status information
		//		String msg = String.format("Score: %d", total/100);
		//		g.setColor(Color.darkGray);
		//		g.drawString(msg, getWidth() - 100, 50);

		if (level >2) {
			backm2.stopSound();
			g.setColor(Color.gray);
			g.fillRect(0, 0, getWidth(), getHeight());
			// Show end game complete screen
			String msg1 = " Game Completed";
			String msg2 = " Press Esc to quit and \"R\" to restart";
			player.hide();
			npc.clear();
			clouds.clear();
			debug = false;
			g.setColor(Color.black);
			g.drawString(msg1, 50, 80);
			g.drawString(msg2, 50, 120);
			g.drawImage(backg3, 70, 140, null);
			//stop();
		}

		if (debug == true) {
			// When in debug mode, you could draw borders around objects
			// and write messages to the screen with useful information.
			// Try to avoid printing to the console since it will produce 
			// a lot of output and slow down your game.
			tmap.drawBorder(g, xo, yo, Color.black);

			g.setColor(Color.red);
			player.drawBoundingBox(g, x, y-10 , w, h+10);		// For the player
			player.drawBoundingBox(g, x+35, y , w+10, h-30);  // For the player's sword

			for (Sprite n: npc) {
				n.drawBoundingBox(g, x2, y2 , w2, h2);
			}

			g.drawString(String.format("Player: %.0f,%.0f", player.getX(),player.getY()),
					getWidth() - 100, 70);
		}
	}

	@Override
	/**
	 * Updates any sprite and checks for collisions
	 * 
	 * @param elapsed The elapsed time between this call and the previous call of elapsed
	 */    
	public void update(long elapsed)
	{
		// Make adjustments to the speed of the sprite due to gravity
		player.setVelocityY(player.getVelocityY()+(gravity*elapsed));    	
		player.setAnimationSpeed(1f);
		for (Sprite n: npc) {
			n.setVelocityY(n.getVelocityY()+(gravity*elapsed));    	
			n.setAnimationSpeed(1.0f);
		}

		// If the player dies
		if (dies) {
			Sound s = new Sound ("sounds/dieSound.wav", false);
			if (soundPlayed == false) {
				s.start();
				soundPlayed = true;
			}
			player.setAnimation(dead, true);
			player.setAnimationSpeed(0.5f);
			player.setVelocity(0, 0);
			if (dead.getCurrentFrameIndex() == 5) {
				player.pauseAnimation();
				dies = false;
				gameEnd = true;
				restart = true;
			}
		}

		// If UP arrow is pressed
		else if (jumping == true && onGround == true) {
			jumpAnimation();
		}

		// If X is pressed
		else if (attacking) {
			player.setAnimation(attack, true);
			hitCollision();
			if (attack.hasLooped()) {
				attacking = false;
			}
		}

		// If C is pressed
		else if (attacking2) {
			player.setAnimation(attack2, false);
			hitCollision();
			//System.out.println("frameChecker is :"+frameChecker);
			//System.out.println("curr frame is                                :"+attack.getCurrentFrameIndex());
			if (attack2.hasLooped()) {
				attacking2 = false;
			}
		}

		// If Z is pressed
		else if (attacking3) {
			player.setAnimation(attack3, false);
			hitCollision();
			if (attack3.hasLooped()) {
				attacking3 = false;
			}
		}

		// If RIGHT arrow is pressed
		else if (moveRight) {
			player.setVelocityX(moveSpeed/2);	
			player.setAnimation(walk, true);
		}

		// If LEFT arrow is pressed
		else if (moveLeft) {
			player.setVelocityX(-moveSpeed/2);
			player.setAnimation(walk, true);
		}

		// If SPACE bar is pressed
		else if (running) {
			player.setVelocityX(moveSpeed);
			player.setAnimation(run, false);
		}

		// When nothing is pressed
		else {
			if (gameEnd ==true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				init();
				run(false,screenWidth,screenHeight);               
			}
			else if(restartBtn == true) {
				level = 1;
				restartBtn = false;
				init();
				run(false,screenWidth,screenHeight);     
			}
			else {
				player.setVelocityX(0);
				player.setAnimation(idle, false);
				jump.setTheLoop(false); 
				attack.setTheLoop(false); 
				attack2.setTheLoop(false); 
				attack3.setTheLoop(false); 
				dead.setTheLoop(false); 
			}
		}

		if (level == 1 && player.getX() >= 2368) {
			level ++;
			init();
			run(false,screenWidth,screenHeight);
		}
		else if (level == 2 && player.getX() >= 2368) {
			level ++;
			gameEnd = true;
			//stop();    
		}

		for (Sprite s: clouds)
			s.update(elapsed);

		for (Sprite s: npc)
			s.update(elapsed);

		// Now update the sprites animation and position
		player.update(elapsed);

		// Then check for any collisions that may have occurred
		//handleScreenEdge(player, tmap, elapsed);
		checkTileCollision(player,tmap, x, y, w, h);
		for (Sprite n: npc) {
			checkTileCollisionForNPC(n,tmap, x2, y2, w2-50, h2-48);
			collision = false;
			//System.out.println("coll " + collision);
			rightAndLeftSidesOfTheCornersForNPC(n, tmap, x2, y2, w2-50, h2-48);
			if (collision == true) {
				n.setVelocityX(-n.getVelocityX()); 
				//System.out.println("coll " + collision);
			}
			if (boundingBoxCollision(player, n, x, y, w, h, x2, y2, w2, h2))
			{
				if (n.getAnimation() == walk2) {
					dies = true;
					//n.setVelocityX(0.5f);
				}
			}
		}
	}

	/**
	 * This method detects the collision between the player's sword and the npc, if
	 * collision takes place, then the npc changes its animation to "dead2" and is then
	 * stationary in that position
	 */ 
	public void hitCollision() {
		for (Sprite n: npc) {
			if (boundingBoxCollision(player, n, x+35, y, w+10, h-30, x2, y2, w2, h2)) {
				if (n.getAnimation() == walk2 )
				{
					n.setAnimation(dead2, true);
					n.setVelocityX(0);
					//System.out.println("u got him");
				}
			}
		}
	}

	/**
	 * This method allows the player to jump, the animation is set to jump only 
	 * once after UP arrow key is pressed. Each frame of the jump animation has a 
	 * specific value for y axis speed, making the jump animation more fluid in 
	 * motion. 
	 */ 
	public void jumpAnimation() {
		player.setAnimation(jump, true);
		//System.out.println("outside the loop "+ jump.hasLooped());
		if (jump.hasLooped()) {
			//System.out.println("inside the loop "+ jump.hasLooped()+"\n");
			jumping = false;
			onGround = false;
		}
		else if (jump.getCurrentFrameIndex() == 1) {
			player.setVelocityY(jumpSpeed);
			if (flip == true) {
				player.setVelocityX(jumpSpeed/3);
				flip = false;
			}
			else {
				player.setVelocityX(-jumpSpeed/3);
			}
			checkTileCollision(player,tmap, x, y, w, h);
		}
		else if (jump.getCurrentFrameIndex() == 2) {
			player.setVelocityY(jumpSpeed/2);
			checkTileCollision(player,tmap, x, y, w, h);
		}
		else if (jump.getCurrentFrameIndex() == 3) {
			player.setVelocityY(jumpSpeed/3);
			checkTileCollision(player,tmap, x, y, w, h);
		}
		else if (jump.getCurrentFrameIndex() == 4) {
			player.setVelocityY(0);
			checkTileCollision(player,tmap, x, y, w, h);
		}
		else if (jump.getCurrentFrameIndex() == 5) {
			player.setVelocityY(-jumpSpeed/3);
			checkTileCollision(player,tmap, x, y, w, h);
		}
		else if (jump.getCurrentFrameIndex() == 6) {
			player.setVelocityY(-jumpSpeed/2);
			checkTileCollision(player,tmap, x, y, w, h);
		}
		else if (jump.getCurrentFrameIndex() == 7) {
			player.setVelocityY(-jumpSpeed);
			checkTileCollision(player,tmap, x, y, w, h);
		}
		//System.out.println("curr frame is     :"+jump.getCurrentFrameIndex());
		else if (jump.getCurrentFrameIndex() == 8) 	{
			player.setVelocityY(-jumpSpeed);
			checkTileCollision(player,tmap, x, y, w, h);
		}
	}
	/**
	 * Checks and handles collisions with the edge of the screen. You should generally
	 * use tile map collisions to prevent the player leaving the game area. This method
	 * is only included as a temporary measure until you have properly developed your
	 * tile maps.
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param tmap		The tile map to check 
	 * @param elapsed	How much time has gone by since the last call
	 */
	public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed)
	{
		// This method just checks if the sprite has gone off the bottom screen.
		// Ideally you should use tile collision instead of this approach
		float difference = s.getY() + s.getHeight() - tmap.getPixelHeight();
		if (difference > 0)	{
			// Put the player back on the map according to how far over they were
			s.setY(tmap.getPixelHeight() - s.getHeight() -(int)(difference)); 
			// and make them bounce
			s.setVelocityY(-s.getVelocityY()*0f);
		}
	}

	/**
	 * Override of the keyPressed event defined in GameCore to catch our
	 * own events
	 * 
	 *  @param e The event that has been generated
	 */
	public void keyPressed(KeyEvent e) 
	{ 
		if (jumping == false && attacking == false && attacking2 == false &&
				attacking3 == false && dies == false){
			int key = e.getKeyCode();

			switch (key)
			{
			case KeyEvent.VK_R     : if (level>2) {restartBtn = true;} break;
			case KeyEvent.VK_UP     : jumping = true; 
			Sound s = new Sound ("sounds/jumpSound.wav", false);
			s.start();
			break;
			case KeyEvent.VK_X      : attacking = true; 
			s = new Sound ("sounds/slashSound.wav", false);
			s.start();
			break;
			case KeyEvent.VK_C      : attacking2 = true; 
			s = new Sound ("sounds/slashSound.wav", false);
			s.start();
			break;
			case KeyEvent.VK_Z      : attacking3 = true; 
			s = new Sound ("sounds/slashSound.wav", false);
			s.start();
			break;
			case KeyEvent.VK_SPACE  : running = true; break;
			case KeyEvent.VK_RIGHT  : moveRight = true; break;
			case KeyEvent.VK_LEFT   : moveLeft = true; break;
			case KeyEvent.VK_ESCAPE : stop(); break;
			case KeyEvent.VK_B 		: debug = !debug; break; // Flip the debug state
			default :  break;
			}
		}
	}

	/** Use the sample code in the lecture notes to properly detect
	 * a bounding box collision between sprites s1 and s2.
	 * 
	 * @return	true if a collision may have occurred, false if it has not.
	 */
	public boolean boundingBoxCollision(Sprite s1, Sprite s2, int i, int j, int k, int l, int i2, int j2, int k2, int l2)
	{
		return (((s1.getX()+i) + (s1.getImage().getWidth(null)+k) > (s2.getX()+i2)) &&
				((s1.getX()+i) < ((s2.getX()+i2) + (s2.getImage().getWidth(null)+k2))) &&
				(((s1.getY()+j) + (s1.getImage().getHeight(null)+l) > (s2.getY()+j2)) &&
						((s1.getY()+j) < (s2.getY()+j2) + (s2.getImage().getHeight(null))+l2)));      	
	}

	/**
	 * Check and handles collisions with a tile map for the
	 * given sprite 's'. Initial functionality is limited...
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param tmap		The tile map to check 
	 * @param i			The shift in x axis
	 * @param j			The shift in y axis 
	 * @param k			The shift in width 
	 * @param l			The shift in height 
	 */
	public void checkTileCollision(Sprite s, TileMap tmap, int i, int j, int k, int l)
	{// Takes a note of a sprite's current position
		float sx = s.getX()+i;
		float sy = s.getY()+j;

		// Find out how wide and how tall a tile is
		float tileWidth = tmap.getTileWidth();
		float tileHeight = tmap.getTileHeight();

		// Divide the sprite’s x coordinate by the width of a tile, to get
		// the number of tiles across the x axis that the sprite is positioned at 
		int	xtile = (int)(sx / tileWidth);
		// The same applies to the y coordinate
		int ytile = (int)(sy / tileHeight);

		// What tile character is at the top left of the sprite s?
		char ch = tmap.getTileChar(xtile, ytile);

		if (ch != '.') // If it's not a dot (empty space), handle it
		{
			s.setY( s.getY()+1);   // You should move the sprite to a position that is not colliding
			s.stop();   // Here we just stop the sprite.
		}

		// The above looked at the top left position, let's look at the bottom left.
		xtile = (int)(sx / tileWidth);
		ytile = (int)((sy + s.getHeight()+l)/ tileHeight);
		ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.') {
			onGround = true;
			s.setY( s.getY()-2); // The sprite moves to a position that is not colliding with tile
			s.stop();  // Here we just stop the sprite.
		}
		rightTopAndBottom(s, tmap, i, j, k, l);
		rightAndLeftSidesOfTheCorners(s, tmap, i, j, k, l);
	}

	/**
	 * Check and handles collisions with a tile map for the
	 * given sprite 's', but only on the side of the sprite just above the bottom corners
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param tmap		The tile map to check 
	 * @param i			The shift in x axis
	 * @param j			The shift in y axis 
	 * @param k			The shift in width 
	 * @param l			The shift in height 
	 */
	public void rightAndLeftSidesOfTheCorners (Sprite s, TileMap tmap, int i, int j, int k, int l){
		// Takes a note of a sprite's current position
		float sx = s.getX()+i;
		float sy = s.getY()+j;

		// Finds out how wide and how tall a tile is
		float tileWidth = tmap.getTileWidth();
		float tileHeight = tmap.getTileHeight();

		// This is the side of the bottom right corner
		int xtile = (int)(sx / tileWidth);
		int ytile = (int)((sy + s.getHeight()+l-10)/ tileHeight);
		char ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.') {
			s.setX( s.getX()+7); // The sprite moves to a position that is not colliding with tile
			s.stop();  // Here we just stop the sprite.
		}

		// Check the side bottom-left corner of the sprite
		xtile = (int)((sx + s.getWidth()+k) / tileWidth); 
		ytile = (int)((sy + s.getHeight()+l-10) / tileHeight);  // Marks just about the bottom right corner
		ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.'){
			s.setX( s.getX()-7);   // The sprite moves to a position that is not colliding with tile
			s.stop();     // Here we just stop the sprite.
		}
	}

	/**
	 * Check and handles collisions with a tile map for the
	 * given sprite 's', but only on the right side of the sprite
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param tmap		The tile map to check 
	 * @param i			The shift in x axis
	 * @param j			The shift in y axis 
	 * @param k			The shift in width 
	 * @param l			The shift in height  
	 */
	public void rightTopAndBottom (Sprite s, TileMap tmap, int i, int j, int k, int l){
		// Take a note of a sprite's current position
		float sx = s.getX()+i;
		float sy = s.getY()+j;

		// Find out how wide and how tall a tile is
		float tileWidth = tmap.getTileWidth();
		float tileHeight = tmap.getTileHeight();

		// Check the top-right corner of the sprite
		int xtile = (int)((sx + s.getWidth()+k)/ tileWidth);
		int ytile = (int)(sy / tileHeight);
		char ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.'){
			s.setY( s.getY()+1);   // The sprite moves to a position that is not colliding with tile
			s.stop();     // Here we just stop the sprite.
		}

		// Check the bottom-right corner of the sprite
		xtile = (int)((sx + s.getWidth()+k) / tileWidth);
		ytile = (int)((sy + s.getHeight()+l) / tileHeight);
		ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.'){
			onGround = true;
			s.setY( s.getY()-2);   // The sprite moves to a position that is not colliding with tile
			s.stop();     // Here we just stop the sprite.
		}
	}

	/**
	 * This handles the events when a key is pressed from the user 
	 * @param e			The user input
	 */
	public void keyReleased(KeyEvent e) { 
		int key = e.getKeyCode();
		switch (key)
		{
		case KeyEvent.VK_ESCAPE : stop(); break;
		case KeyEvent.VK_SPACE  : running = false; break;
		case KeyEvent.VK_LEFT   : moveLeft = false; break;
		case KeyEvent.VK_RIGHT  : moveRight = false; 
		break;
		default :  break;
		}
	}

	/**
	 * Check and handles collisions with a tile map for the
	 * given sprite 's'. Initial functionality is limited...
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param tmap		The tile map to check 
	 * @param i			The shift in x axis
	 * @param j			The shift in y axis 
	 * @param k			The shift in width 
	 * @param l			The shift in height 
	 */
	public void checkTileCollisionForNPC(Sprite s, TileMap tmap, int i, int j, int k, int l)
	{// Takes a note of a sprite's current position
		float sx = s.getX()+i;
		float sy = s.getY()+j;

		// Find out how wide and how tall a tile is
		float tileWidth = tmap.getTileWidth();
		float tileHeight = tmap.getTileHeight();

		// Divide the sprite’s x coordinate by the width of a tile, to get
		// the number of tiles across the x axis that the sprite is positioned at 
		int	xtile = (int)(sx / tileWidth);
		// The same applies to the y coordinate
		int ytile = (int)(sy / tileHeight);

		// What tile character is at the top left of the sprite s?
		char ch = tmap.getTileChar(xtile, ytile);

		if (ch != '.') // If it's not a dot (empty space), handle it
		{
			s.setY( s.getY()+1);   // You should move the sprite to a position that is not colliding
			s.stop();   // Here we just stop the sprite.
		}

		// We need to consider the other corners of the sprite
		// The above looked at the top left position, let's look at the bottom left.
		xtile = (int)(sx / tileWidth);
		ytile = (int)((sy + s.getHeight()+l)/ tileHeight);
		ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.') {
			s.setY( s.getY()-2); // The sprite moves to a position that is not colliding with tile
			s.setVelocityY(0);
			//s.stop();  // Here we just stop the sprite.
		}
		rightTopAndBottomForNPC(s, tmap, i, j, k, l);
	}

	/**
	 * Check and handles collisions with a tile map for the
	 * given sprite 's', but only on the right side of the sprite
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param tmap		The tile map to check 
	 * @param i			The shift in x axis
	 * @param j			The shift in y axis 
	 * @param k			The shift in width 
	 * @param l			The shift in height 
	 */
	public void rightAndLeftSidesOfTheCornersForNPC (Sprite s, TileMap tmap, int i, int j, int k, int l){
		// Takes a note of a sprite's current position
		float sx = s.getX()+i;
		float sy = s.getY()+j;

		// Finds out how wide and how tall a tile is
		float tileWidth = tmap.getTileWidth();
		float tileHeight = tmap.getTileHeight();
		// This is the side of the bottom right corner
		int xtile = (int)(sx / tileWidth);
		int ytile = (int)((sy + s.getHeight()+l-10)/ tileHeight);
		char ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.') {
			collision = true;
			s.setX( s.getX()+7); // The sprite moves to a position that is not colliding with tile
		}

		// Check the bottom-right corner of the sprite
		xtile = (int)((sx + s.getWidth()+k) / tileWidth); 
		ytile = (int)((sy + s.getHeight()+l-10) / tileHeight);  // Marks just about the bottom right corner
		ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.'){
			collision = true;
			s.setX( s.getX()-10);   // The sprite moves to a position that is not colliding with tile
		}
	}

	/**
	 * Check and handles collisions with a tile map for the
	 * given sprite 's', but only on the right side of the sprite
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param tmap		The tile map to check 
	 * @param i			The shift in x axis
	 * @param j			The shift in y axis 
	 * @param k			The shift in width 
	 * @param l			The shift in height 
	 */
	public void rightTopAndBottomForNPC (Sprite s, TileMap tmap, int i, int j, int k, int l){
		// Take a note of a sprite's current position
		float sx = s.getX()+i;
		float sy = s.getY()+j;

		// Find out how wide and how tall a tile is
		float tileWidth = tmap.getTileWidth();
		float tileHeight = tmap.getTileHeight();

		// Check the top-right corner of the sprite
		int xtile = (int)((sx + s.getWidth()+k)/ tileWidth);
		int ytile = (int)(sy / tileHeight);
		char ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.'){
			s.setY( s.getY()+1);   // The sprite moves to a position that is not colliding with tile
			s.stop();     // Here we just stop the sprite.
		}

		// Check the bottom-right corner of the sprite
		xtile = (int)((sx + s.getWidth()+k) / tileWidth);
		ytile = (int)((sy + s.getHeight()+l) / tileHeight);
		ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.'){
			s.setY( s.getY()-2);   // The sprite moves to a position that is not colliding with tile
			s.setVelocityY(0);
			//s.stop();     // Here we just stop the sprite.
		}
	}
}