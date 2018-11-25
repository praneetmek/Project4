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

	// Instance variables
	private Ball ball;
	private Paddle paddle;

	private int numberOfBottomHits;
	private int numberOfAnimalsLeft;

	private boolean collisionInFrame;

	ImageView iv1;

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

		numberOfAnimalsLeft=1;

		// Create and add animals ...
		Image imageOne=new Image("horse.jpg");
		iv1=new ImageView();
		iv1.setImage(imageOne);
		iv1.setX(40);
		iv1.setY(40);
		getChildren().add(iv1);


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
	private boolean collisionWithSide(ImageView iv){
		double topBall = ball.getY() - ball.getCircle().getRadius();
		double bottomBall = ball.getY() + ball.getCircle().getRadius();
		double leftBall=ball.getX()-ball.getCircle().getRadius();
		double rightBall=ball.getX()+ball.getCircle().getRadius();

		double topIV=iv.getY();
		double botIV=topIV+iv.getImage().getHeight();
		double leftIV=iv.getX();
		double rightIV=leftIV+iv.getImage().getWidth();

		boolean touchingLeft = (leftBall<rightIV && leftBall>leftIV) &&
				((topBall>topIV && topBall<botIV) || (bottomBall<botIV && bottomBall>topIV));
		boolean touchingRight = (rightBall<rightIV && rightBall>leftIV) &&
				((topBall>topIV && topBall<botIV) || (bottomBall<botIV && bottomBall>topIV));
		return touchingLeft||touchingRight;

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
		double leftBall=ball.getX()-ball.getCircle().getRadius();

		double leftIV=iv1.getX();
		double rightIV=leftIV+iv1.getImage().getWidth();


		if((collisionWith(paddle,ball) && !collisionInFrame) || topBall<=0 && topBall>=-5){
			ball.setVy(-ball.getVy());
			collisionInFrame=true;

		} else if(bottomBall>=HEIGHT){
			ball.setVy(-ball.getVy());
			numberOfBottomHits++;
		} else if(collisionWithSide(iv1)){
			getChildren().remove(iv1);
			ball.setVx(-ball.getVx());
			numberOfAnimalsLeft--;
		}


		if(!collisionWith(paddle,ball)){
			collisionInFrame=false;
		}

		if(ball.getX()+ball.getCircle().getRadius()>=WIDTH-1||ball.getX()-ball.getCircle().getRadius()<=1){
			ball.setVx(-ball.getVx());
		}

		if(numberOfBottomHits==5)
			return GameState.LOST;
		else if(numberOfAnimalsLeft==0)
			return GameState.WON;
		else
			return GameState.ACTIVE;
	}
}
