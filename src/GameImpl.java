import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseEvent;
import javafx.event.*;
import com.sun.javafx.application.PlatformImpl;
import javafx.scene.text.Text;

import java.util.*;

public class GameImpl extends Pane implements Game {
	/**
	 * Defines different states of the game.
	 */
	public enum GameState {
		WON, LOST, ACTIVE, NEW
	}

	// Constants
	/**
	 * The width of the game board.
	 */
	public static final int WIDTH = 400;
	/**
	 * The height of the game board.
	 */
	public static final int HEIGHT = 600;

	private final double SPEED_FACTOR=1.5;

	// Instance variables
	private Ball ball;
	private Paddle paddle;

	private int numberOfBottomHits;
	private ArrayList<ImageView> animals;

	private boolean collisionInFrame;

	private ImageView iv1,iv2,iv3,iv4, iv5, iv6, iv7, iv8,iv9,iv10,iv11,iv12,iv13,iv14,iv15,iv16;

	/**
	 * Constructs a new GameImpl.
	 */
	public GameImpl () {
		setStyle("-fx-background-color: white;");

		restartGame(GameState.NEW);
	}

	public String getName () {
		return "Zutopia";
	}

	public Pane getPane () {
		return this;
	}

	private void restartGame (GameState state) {
		getChildren().clear();  // remove all components from the game

		// Create and add ball
		ball = new Ball();
		getChildren().add(ball.getCircle());  // Add the ball to the game board

		animals=new ArrayList<ImageView>();

		// Create and add animals ...
		Image horse = new Image("horse.jpg");
		Image duck = new Image("duck.jpg");
		Image goat = new Image("goat.jpg");

		iv1=new ImageView();
		iv1.setImage(horse);
		iv1.setX(40);
		iv1.setY(20);
		getChildren().add(iv1);
		animals.add(iv1);


		iv2=new ImageView();
		iv2.setImage(duck);
		iv2.setX(130);
		iv2.setY(20);
		getChildren().add(iv2);
		animals.add(iv2);


		iv3=new ImageView();
		iv3.setImage(goat);
		iv3.setX(220);
		iv3.setY(20);
		getChildren().add(iv3);
		animals.add(iv3);


		iv4=new ImageView();
		iv4.setImage(horse);
		iv4.setX(310);
		iv4.setY(20);
		getChildren().add(iv4);
		animals.add(iv4);

		iv5=new ImageView();
		iv5.setImage(duck);
		iv5.setX(40);
		iv5.setY(80);
		getChildren().add(iv5);
		animals.add(iv5);

		iv6=new ImageView();
		iv6.setImage(goat);
		iv6.setX(130);
		iv6.setY(80);
		getChildren().add(iv6);
		animals.add(iv6);


		iv7=new ImageView();
		iv7.setImage(horse);
		iv7.setX(220);
		iv7.setY(80);
		getChildren().add(iv7);
		animals.add(iv7);

		iv8=new ImageView();
		iv8.setImage(duck);
		iv8.setX(310);
		iv8.setY(80);
		getChildren().add(iv8);
		animals.add(iv8);

		iv9=new ImageView();
		iv9.setImage(goat);
		iv9.setX(40);
		iv9.setY(140);
		getChildren().add(iv9);
		animals.add(iv9);

		iv10=new ImageView();
		iv10.setImage(horse);
		iv10.setX(130);
		iv10.setY(140);
		getChildren().add(iv10);
		animals.add(iv10);

		iv11=new ImageView();
		iv11.setImage(duck);
		iv11.setX(220);
		iv11.setY(140);
		getChildren().add(iv11);
		animals.add(iv11);

		iv12=new ImageView();
		iv12.setImage(goat);
		iv12.setX(310);
		iv12.setY(140);
		getChildren().add(iv12);
		animals.add(iv12);

		iv13=new ImageView();
		iv13.setImage(horse);
		iv13.setX(40);
		iv13.setY(200);
		getChildren().add(iv13);
		animals.add(iv13);

		iv14=new ImageView();
		iv14.setImage(duck);
		iv14.setX(130);
		iv14.setY(200);
		getChildren().add(iv14);
		animals.add(iv14);

		iv15=new ImageView();
		iv15.setImage(goat);
		iv15.setX(220);
		iv15.setY(200);
		getChildren().add(iv15);
		animals.add(iv15);

		iv16=new ImageView();
		iv16.setImage(horse);
		iv16.setX(310);
		iv16.setY(200);
		getChildren().add(iv16);
		animals.add(iv16);


		// Create and add paddle
		paddle = new Paddle();
		getChildren().add(paddle.getRectangle());  // Add the paddle to the game board

		// initializes bottom hits to 0
		numberOfBottomHits=0;

		collisionInFrame=false;
		// Add start message
		final String message;
		if (state == GameState.LOST) {
			message = "Game Over\n";
		} else if (state == GameState.WON) {
			message = "You won!\n";
		} else {
			message = "";
		}
		final Label startLabel = new Label(message + "Click mouse to start");
		startLabel.setLayoutX(WIDTH / 2 - 50);
		startLabel.setLayoutY(HEIGHT / 2 + 100);
		getChildren().add(startLabel);


		// Add event handler to start the game
		setOnMouseClicked(new EventHandler<MouseEvent> () {
			@Override
			public void handle (MouseEvent e) {
				GameImpl.this.setOnMouseClicked(null);

				// As soon as the mouse is clicked, remove the startLabel from the game board
					getChildren().remove(startLabel);
				run();
			}
		});

		// Add another event handler to steer paddle...
		setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				paddle.moveTo(mouseEvent.getX(),mouseEvent.getY());
			}
		});
	}

	/**
	 * Begins the game-play by creating and starting an AnimationTimer.
	 */
	public void run () {
		// Instantiate and start an AnimationTimer to update the component of the game.
		new AnimationTimer () {
			private long lastNanoTime = -1;
			public void handle (long currentNanoTime) {
				if (lastNanoTime >= 0) {  // Necessary for first clock-tick.
					GameState state;
					if ((state = runOneTimestep(currentNanoTime - lastNanoTime)) != GameState.ACTIVE) {
						// Once the game is no longer ACTIVE, stop the AnimationTimer.
						stop();
						// Restart the game, with a message that depends on whether
						// the user won or lost the game.
						restartGame(state);
					}
				}
				// Keep track of how much time actually transpired since the last clock-tick.
				lastNanoTime = currentNanoTime;
			}
		}.start();
	}

	/**
	 * Checks if the ball is in collision with the paddle
	 * @param paddle player's paddle
	 * @param nball ball which collides with paddle
	 * @return
	 */
	private boolean collisionWith(Paddle paddle,Ball nball){
		double bottomPaddle = paddle.getY() + paddle.getRectangle().getHeight()/2;
		double topPaddle = paddle.getY() - paddle.getRectangle().getHeight()/2;
		double leftPaddle = paddle.getX() - paddle.getRectangle().getWidth()/2;
		double rightPaddle = paddle.getX() + paddle.getRectangle().getWidth()/2;

		double topBall = nball.getY() - nball.getCircle().getRadius();
		double bottomBall = nball.getY() + nball.getCircle().getRadius();

		boolean isInsidePaddleHeight = (bottomBall>=topPaddle && bottomBall<=bottomPaddle) ||
				(topBall>=topPaddle && topBall<=bottomPaddle) ||
				(bottomBall>bottomPaddle&&topBall<topPaddle);
		boolean insidePaddleWidth = nball.getX() > leftPaddle&&nball.getX() < rightPaddle;
		boolean isCollisionWithPaddle = isInsidePaddleHeight && insidePaddleWidth;

		return isCollisionWithPaddle;
	}

	private boolean collisionWithTop(ImageView iv){
		if(getChildren().contains(iv)) {
			double topBall = ball.getY() - ball.getCircle().getRadius();
			double bottomBall = ball.getY() + ball.getCircle().getRadius();
			double leftBall = ball.getX() - ball.getCircle().getRadius();
			double rightBall = ball.getX() + ball.getCircle().getRadius();

			double topIV = iv.getY();
			double botIV = topIV + iv.getImage().getHeight();
			double leftIV = iv.getX();
			double rightIV = leftIV + iv.getImage().getWidth();
			boolean inColumn = ((leftBall >= leftIV && leftBall <= rightIV) || (rightBall <= rightIV && rightBall >= leftIV));

			boolean touchingTop = (bottomBall >= topIV && bottomBall <= botIV) && inColumn;
			boolean touchingBot = (topBall >= topIV && topBall <= botIV) && inColumn;
			if (touchingTop || touchingBot) {
				getChildren().remove(iv);
				animals.remove(iv);

			}
			return touchingTop || touchingBot;
		}
		else{
			return false;
		}

	}

	/**
	 * Updates the state of the game at each timestep. In particular, this method should
	 * move the ball, check if the ball collided with any of the animals, walls, or the paddle, etc.
	 * @param deltaNanoTime how much time (in nanoseconds) has transpired since the last update
	 * @return the current game state
	 */
	public GameState runOneTimestep (long deltaNanoTime) {
		ball.updatePosition(deltaNanoTime);
		double topBall = ball.getY() - ball.getCircle().getRadius();
		double bottomBall = ball.getY() + ball.getCircle().getRadius();



		if((collisionWith(paddle,ball) && !collisionInFrame)){
			ball.setVy(-ball.getVy());
			collisionInFrame=true;
		} else if(topBall<=0 && topBall>=-5){
			ball.setVy(Math.pow(SPEED_FACTOR/Math.sqrt(2),16-animals.size())* Ball.INITIAL_VY);
			collisionInFrame=true;

		} else if(bottomBall>=HEIGHT){
			ball.setVy(-Math.pow(SPEED_FACTOR/Math.sqrt(2),16-animals.size())* Ball.INITIAL_VY);
			numberOfBottomHits++;
		} else if(collisionWithTop(iv1)||collisionWithTop(iv2)||collisionWithTop(iv3) ||collisionWithTop(iv4)
				||collisionWithTop(iv5)||collisionWithTop(iv6)||collisionWithTop(iv7)||collisionWithTop(iv8)
				||collisionWithTop(iv9)||collisionWithTop(iv10)||collisionWithTop(iv11)||collisionWithTop(iv12)
				||collisionWithTop(iv13)||collisionWithTop(iv14)||collisionWithTop(iv15)||collisionWithTop(iv16)){
			//ball.setVy(-ball.getVy());
			ball.setVx((SPEED_FACTOR/Math.sqrt(2)*ball.getVx()));
			ball.setVy((-SPEED_FACTOR/Math.sqrt(2)*ball.getVy()));
			System.out.println("collision with top");
		}


		if(!collisionWith(paddle,ball)){
			collisionInFrame=false;
		}

		if(ball.getX()+ball.getCircle().getRadius()>=WIDTH-1){
			ball.setVx(-Math.pow(SPEED_FACTOR/Math.sqrt(2),16-animals.size())* Ball.INITIAL_VX);
		}else if(ball.getX()-ball.getCircle().getRadius()<=1){
			ball.setVx(Math.pow(SPEED_FACTOR/Math.sqrt(2),16-animals.size())* Ball.INITIAL_VX);
		}

		if(numberOfBottomHits==5)
			return GameState.LOST;
		else if(animals.size()==0)
			return GameState.WON;
		else
			return GameState.ACTIVE;
	}
}
