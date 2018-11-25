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
	private boolean collisionInFrame;

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

		// Create and add animals ...
		Image imageOne=new Image("horse.jpg");
		ImageView iv1=new ImageView();
		iv1.setImage(imageOne);
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
		Text startLabel=new Text(message);
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
	 * Updates the state of the game at each timestep. In particular, this method should
	 * move the ball, check if the ball collided with any of the animals, walls, or the paddle, etc.
	 * @param deltaNanoTime how much time (in nanoseconds) has transpired since the last update
	 * @return the current game state
	 */
	public GameState runOneTimestep (long deltaNanoTime) {
		ball.updatePosition(deltaNanoTime);

		double bottomPaddle=paddle.getY()+paddle.getRectangle().getHeight()/2;
		double topPaddle=paddle.getY()-paddle.getRectangle().getHeight()/2;
		double leftPaddle=paddle.getX()-paddle.getRectangle().getWidth()/2;
		double rightPaddle=paddle.getX()+paddle.getRectangle().getWidth()/2;

		double topBall=ball.getY()-ball.getCircle().getRadius();
		double bottomBall=ball.getY()+ball.getCircle().getRadius();

		boolean isInsidePaddle=(bottomBall>=topPaddle && bottomBall<=bottomPaddle)||(topBall>=topPaddle && topBall<=bottomPaddle)||(bottomBall>bottomPaddle&&topBall<topPaddle);
		boolean insidePaddleWidth=ball.getX()>leftPaddle&&ball.getX()<rightPaddle;
		boolean isCollisionWithPaddle=isInsidePaddle&&insidePaddleWidth;
		if((isCollisionWithPaddle && !collisionInFrame) || topBall<=0 && topBall>=-5){
			ball.setVy(-ball.getVy());
			collisionInFrame=true;

		}
		else if(bottomBall>=HEIGHT){
			ball.setVy(-ball.getVy());
			numberOfBottomHits++;
		}
		if(!isCollisionWithPaddle){
			collisionInFrame=false;
		}
		if(ball.getX()+ball.getCircle().getRadius()>=WIDTH-1||ball.getX()-ball.getCircle().getRadius()<=1){
			ball.setVx(-ball.getVx());
		}
		if(numberOfBottomHits==5){
			return GameState.LOST;
		}
		else {
			return GameState.ACTIVE;
		}
	}
}
