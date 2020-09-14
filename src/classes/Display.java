package classes;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Display extends JFrame implements KeyListener, Runnable {
	private static final long serialVersionUID = 1L;
	private int score; // Player score in game
	private int rectX; // X coordinate of P1
	private int rectY; // Y coordinate of P1
	private int randX; // X coordinate of apple
	private int randY; // Y coordinate of apple
	private int level = 1; // Game level
	private int time; // Time left to finish game
	private Image bg; // Background image
	private Image snake; // The snake image
	private Image apple; // The apple image
	private Image gameOverBG; // Background for game over
	private Random rand; // Number randomizer for apple
	private boolean loaded = false; // Checks if pics are loaded
	private boolean gameEnds = false; // Checks if game is over
	private boolean hitSide; // Checks if snake hits side of window
	private boolean moving; // Checks if the snake is moving
	private boolean snakeAteApple; // Checks if the snake ate the apple
	private boolean gameWon = false; // Checks if game is won(Score = 150)
	private Direction direction; // Direction snake is moving
	private Thread thread1; // Thread for P1 to control snake movement
	private Thread thread2; // Thread to control time
	/**
	 * Height & Width of the window
	 */
	public static final int HEIGHT = 732;
	public static final int WIDTH = 1267;
	/**
	 * Width of the snake and each checkerboard square, respectively
	 */
	public static final int SNAKE_SQUARE_WIDTH = 50;
	public static final int CHECKERBOARD_SQUARE_WIDTH = 70;
	public static final int SLEEP_TIME = 15; // Time that thread sleeps when
												// moving

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display screen = new Display();
		screen.repaint();
		screen.loadPictures();
	}

	// CONSTRUCTOR
	public Display() {
		setTitle("Test");
		setSize(WIDTH, HEIGHT);
		direction = Direction.DEFAULT;
		setResizable(false);
		setUndecorated(true);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(this);
		hitSide = false;
		moving = false;
		snakeAteApple = true;
		thread1 = new Thread(this);
		thread2 = new Thread() {
			public void run() {
				while (time > 0) {
					time--;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					repaint();
					if (gameWon) {
						boolean continued = false;
						while (!continued) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (!gameWon) {
								continued = true;
							}
						}
					}
				}
			}
		};
		rand = new Random();
		rectX = CHECKERBOARD_SQUARE_WIDTH / 2 - (SNAKE_SQUARE_WIDTH / 2);
		rectY = CHECKERBOARD_SQUARE_WIDTH / 2 - (SNAKE_SQUARE_WIDTH / 2);
	}

	/**
	 * Handles all visual text, images, and backgrounds
	 */
	public void paint(Graphics g) {
		if (loaded && !gameEnds && !gameWon) {
			if(level == 2 && direction != Direction.DEFAULT) {
				System.out.println("This is level 2");
			}
			g.drawImage(bg, 0, 0, null);
			if (snakeAteApple) {
				randX = rand.nextInt(17) + 1;
				randX = randX * CHECKERBOARD_SQUARE_WIDTH - apple.getWidth(null);
				randY = rand.nextInt(9) + 1;
				randY = randY * CHECKERBOARD_SQUARE_WIDTH - apple.getHeight(null);
				snakeAteApple = false;
			}
			g.drawImage(apple, randX, randY, null);
			g.drawImage(snake, rectX, rectY, null);
			g.setColor(Color.WHITE);
			setFont(new Font("Score: " + Integer.toString(score), Font.BOLD, 20));
			g.drawString("P1 Score: " + Integer.toString(score), 10, 25);
			g.drawString("Level " + level, WIDTH - 90 - (2 * level / 10), 25);
			if (level >= 1) {
				if (!moving) {
					time = 200 - (level * 10);
				}
				g.drawString("Time Left: " + time, 10, HEIGHT - 10);
			}
			g.dispose();
		}
		if (gameEnds) {
			g.drawImage(gameOverBG, 0, 0, null);
			g.setColor(Color.WHITE);
			setFont(new Font("Arial", Font.BOLD, 30));
			g.drawString("Game Over! Your Score was " + Integer.toString(score)
					+ "! Press the Enter key to close the window.", 10, 32);
			g.dispose();
		}
		if (gameWon) {
			g.drawImage(gameOverBG, 0, 0, null);
			g.setColor(Color.WHITE);
			setFont(new Font("Arial", Font.BOLD, 30));
			g.drawString("Congratulations! You won! Your Score was " + Integer.toString(score)
					+ "! Press the Enter key to close the window. Or, you can press the control key on your keyboard to advance to the next level.",
					10, 32);
			if (level == 1) {
				g.drawString("WARNING: Future levels will have a time limit", (WIDTH / 2) - 320, HEIGHT - 150);
			}
			g.dispose();
		}
	}

	/**
	 * Transfers images from computer to java objects
	 */
	private void loadPictures() {
		bg = new ImageIcon("images/checkerboard.jpeg/").getImage();
		snake = new ImageIcon("images/snake.png/").getImage();
		snake = snake.getScaledInstance(SNAKE_SQUARE_WIDTH, SNAKE_SQUARE_WIDTH, Image.SCALE_SMOOTH);
		apple = new ImageIcon("images/apple.png/").getImage();
		apple = apple.getScaledInstance(SNAKE_SQUARE_WIDTH, SNAKE_SQUARE_WIDTH, Image.SCALE_SMOOTH);
		gameOverBG = new ImageIcon("images/black game over screen.jpg/")
				.getImage();
		loaded = true;
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			System.exit(0);
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			direction = Direction.RIGHT;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			direction = Direction.LEFT;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			direction = Direction.UP;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			direction = Direction.DOWN;
		}
		if (e.getKeyCode() == KeyEvent.VK_CONTROL && gameWon) {
			if (gameWon) {
				gameWon = false;
				level++;
				score = 0;
				rectX = CHECKERBOARD_SQUARE_WIDTH / 2 - (SNAKE_SQUARE_WIDTH / 2);
				rectY = CHECKERBOARD_SQUARE_WIDTH / 2 - (SNAKE_SQUARE_WIDTH / 2);
				direction = Direction.DEFAULT;
				repaint();
			}
		}
		if (!moving) {
			thread1.start();
			moving = true;
			thread2.start();

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		while (!hitSide && !gameWon) {
			switch (direction) {
			case RIGHT:
				while (!hitSide && !gameWon && direction.equals(Direction.RIGHT)) {
					rectX += 2;
					repaint();
					helper();
				}
				break;

			case LEFT:
				while (!hitSide && !gameWon && direction.equals(Direction.LEFT)) {
					rectX -= 2;
					repaint();
					helper();
				}
				break;

			case UP:
				while (!hitSide && !gameWon && direction.equals(Direction.UP)) {
					rectY -= 2;
					repaint();
					helper();
				}
				break;

			case DOWN:
				while (!hitSide && !gameWon && direction.equals(Direction.DOWN)) {
					rectY += 2;
					repaint();
					helper();
				}
				break;

			default:
				break;
			}
			if (gameEnds || gameWon) {
				repaint();
			}
			if (gameWon) {
				boolean continued = false;
				while (!continued) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!gameWon) {
						continued = true;
						hitSide = false;
						direction = Direction.DEFAULT;
					}
				}
			}
		}
		return;
	}

	/**
	 * Performs all basic operations that allow the snake to move.
	 * 
	 * Call to snakeHitSide(x, y) routinely checks if the snake hits the edges of
	 * the map.
	 * 
	 * Call to eatApple() routinely checks if the snake encounters an apple.
	 * 
	 * The if statement checks if the score is equal to or greater than 150, ending
	 * the game if true.
	 * 
	 * The try catch block handles the movement of the snake. The snake's movement
	 * gets progressively faster as the score increases by reducing the Thread sleep
	 * time.
	 */
	public void helper() {
		hitSide = snakeHitSide(rectX, rectY);
		snakeAteApple = eatApple();
		if (score >= 50) {
			gameWon = true;
		}
		try {
			Thread.sleep(SLEEP_TIME - (score / 10));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the snake hits the side of the window
	 * 
	 * @param x X coordinate of snake
	 * @param y Y coordinate of snake
	 * @return T/F if snake hits side of window
	 */
	private boolean snakeHitSide(int x, int y) {
		if (x + 10 <= 0 || x + SNAKE_SQUARE_WIDTH - 10 >= WIDTH || y + 10 <= 0
				|| y + SNAKE_SQUARE_WIDTH - 10 >= HEIGHT) {
			gameEnds = true;
			return true;
		}
		return false;
	}

	/**
	 * Checks if the snake eats the apple
	 * 
	 * @return T/F if snake eats apple
	 */
	private boolean eatApple() {
		if ((rectX + SNAKE_SQUARE_WIDTH - 10 >= randX && rectX + 10 <= randX + apple.getWidth(null))
				&& (rectY + SNAKE_SQUARE_WIDTH - 10 >= randY && rectY + 10 <= randY + apple.getHeight(null))) {
			score += 10;
			return true;
		}
		return false;
	}

}
