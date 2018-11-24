 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
 
/**
 * This is the starter code for the JavaFX tutorial.
 * Install and set up JavaFX for your IDE before beginning.
 *
 * @author kathryn_monopoli
 *
 */
public class ShapeExercise extends Application {
    @Override
    public void start(Stage primaryStage) {

        Pane root = new Pane();

        Scene scene = new Scene(root, 500, 300);

        Rectangle rect = new Rectangle( 200, 100, 100, 100);
        rect.setFill(Color.BLUE);
        rect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                System.out.println("hello");
                rect.setFill(Color.RED);
                rect.setTranslateX(200);
                rect.setTranslateY(-100);
            }
        });

        Circle circle=new Circle(250,150,50,Color.GREEN);
        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                circle.setCenterX(mouseEvent.getX());
            }
        });
        circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                System.out.println("hello");
            }
        });

        root.getChildren().add(rect);
        root.getChildren().add(circle);

        primaryStage.setTitle("JavaFX Tutorial");
        primaryStage.setScene(scene);
        primaryStage.show(); // displays the contents of the stage

    }
    
	public static void main(String[] args) {
		launch(args); // starts JavaFX application
	}
}
