package sample;

import javafx.scene.control.Button;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Controller implements Initializable {

    private final int TICKS_IN_PERIOD = 30;
    private final int MILLISECONDS_IN_TICK = 10;
    private final double EPS = 1e-18;

    private final ScheduledExecutorService DrawScheduler =
            Executors.newScheduledThreadPool(1);

    private final ScheduledExecutorService RequestScheduler =
            Executors.newScheduledThreadPool(1);

    @FXML
    private GridPane root;

    @FXML
    private Canvas canvas;

    @FXML
    private StackPane stackPane;

    @FXML
    private ImageView map;

    @FXML
    private Button first;
    @FXML
    private Button second;
    @FXML
    private Button third;

    private EventHandler<ActionEvent> changeFloor;

    private int level;

    private Double xTranslation = 60.484129 - 876;
    private Double yTranslation = 15.418381 - 413.0;

    private Double scale = (1008.0 - 876.0) / (60.484351 - 60.484129);

    private int ticks;
    private List<Gyro> curList;

    private Image gyro;


    URL imageUrl;


    public void mockCurList() {
        curList = new LinkedList<Gyro>();
        for (int i = 0; i < 1; ++i) {
            List<Position> route = new LinkedList<>();
            Random rand = new Random();
            for (int j = 0; j < 5; ++j) {
                route.add(new Position(rand.nextInt() % 100, rand.nextInt() % 100, 2));
            }
            for (int j = 0; j < 2; ++j) {
                route.add(new Position(rand.nextInt() % 100, rand.nextInt() % 100, 1));
            }
            Gyro g = new Gyro();
            g.setRoute(route);
            g.setSpeed(1);
            curList.add(new Gyro());
        }
    }


    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        mockCurList();
        imageUrl = getClass().getResource("/first.png");
        try {
            Image image = SwingFXUtils.toFXImage(ImageIO.read(imageUrl), null);
            map.setImage(image);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        level = 1;
        ticks = 0;

        imageUrl = getClass().getResource("/gyro.jpg");
        try {
            gyro = SwingFXUtils.toFXImage(ImageIO.read(imageUrl), null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        final ScheduledFuture<?> drawHandler =
                DrawScheduler.scheduleAtFixedRate(new RunnableDrawer(), 0, 5, SECONDS);

//        final ScheduledFuture<?> requestHandler =
//                RequestScheduler.scheduleAtFixedRate(new RunnableRequester(), 0, 1, SECONDS);

        changeFloor  = new EventHandler<ActionEvent>() {
            public void handle(final javafx.event.ActionEvent event) {
                Button button = (Button)event.getTarget();
                switch (button.getId()) {
                    case "first":
                        imageUrl = getClass().getResource("/first.png");
                        try {
                            Image image = SwingFXUtils.toFXImage(ImageIO.read(imageUrl), null);
                            map.setImage(image);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        level =1;
                        break;
                    case "second":
                        imageUrl = getClass().getResource("/second.png");
                        try {
                            Image image = SwingFXUtils.toFXImage(ImageIO.read(imageUrl), null);
                            map.setImage(image);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        level =2;
                        break;
                    case "third":
                        imageUrl = getClass().getResource("/third.png");
                        try {
                            Image image = SwingFXUtils.toFXImage(ImageIO.read(imageUrl), null);
                            map.setImage(image);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        level = 3;
                        break;
                }
            }
        };

        first.setOnAction(changeFloor);
        second.setOnAction(changeFloor);
        third.setOnAction(changeFloor);

        map.fitWidthProperty().bind(stackPane.widthProperty());

    }

    private void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx,
                                  double tlpy, int w, int h, double width, double height) {
        gc.save(); // saves the current state on stack, including the current transform
        rotate(gc, angle, width / 2, height / 2);
        gc.drawImage(image, tlpx, tlpy, w, h);
        gc.restore(); // back to original state (before rotation)
    }


    public void drawRoute(Gyro g) {
        List<Position> route = g.getRoute();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        for (int i = 0; i < route.size() - 1; ++i) {
            if (route.get(i + 1).getFloor() != level) {
                break;
            }
            gc.setFill(Color.SEAGREEN);
            gc.setLineWidth(5);
            gc.strokeLine(route.get(i).getX(), route.get(i).getY(),
                    route.get(i + 1).getX().intValue(), route.get(i + 1).getY());
        }

        double angle = Math.atan2(route.get(1).getY() - route.get(0).getY(), route.get(1).getX() - route.get(0).getX());
        Canvas gyroCanvas = new Canvas(canvas.getWidth(), canvas.getHeight());
        GraphicsContext gyroGc = gyroCanvas.getGraphicsContext2D();
        drawRotatedImage(gyroGc, gyro, angle, route.get(0).getX(), route.get(0).getY(), 40, 40,
                canvas.getWidth(), canvas.getHeight());

        stackPane.getChildren().add(gyroCanvas);
    }


    public void processTrajectory(Gyro gyro) {
        List<Position> trajectory = gyro.getRoute();
        double speed = gyro.getSpeed(); // (pixel / tick?)
        double curTime = MILLISECONDS_IN_TICK;
        while(curTime > EPS && trajectory.size() > 1) {
            Position distance = Position.subtract(trajectory.get(1), trajectory.get(0));
            Double distanceValue =  Position.calculateDistance(distance);
            if (trajectory.get(1).getFloor() != trajectory.get(0).getFloor()) {

            }
            else {
               Double deltaDistanceValue = speed * MILLISECONDS_IN_TICK;
                if (distanceValue > deltaDistanceValue + EPS) {
                    Position newPosition = Position.sum(trajectory.get(0),
                            Position.mult(Position.subtract(trajectory.get(1), trajectory.get(0)), deltaDistanceValue / distanceValue));
                    trajectory.remove(0);
                    trajectory.add(0, newPosition);
                } else if (distanceValue < deltaDistanceValue - EPS) {
                    curTime = curTime - distanceValue/ speed;
                    trajectory.remove(0);
                }
                else {
                    trajectory.remove(0);
                }
            }
        }
    }


    private Position translateCoordinates(Position pos) {
        return new Position(pos.getX() - xTranslation, pos.getY() - yTranslation, pos.getFloor());
    }

    final class RunnableDrawer implements Runnable {

        public void run() {
            try {
                if (ticks % TICKS_IN_PERIOD == 0) {
//                    curList = Main.getInstance().getResult();
                }
                else {
                    for (Gyro g : curList) {
                        if (g.getRoute().get(0).getFloor() != level)
                            continue;
                        processTrajectory(g);
                    }
                }
                for (Gyro g : curList) {
                    if (g.getRoute().get(0).getFloor() != level)
                        continue;
                    drawRoute(g);
                }
                ticks++;
            }  catch (Exception e) {
                Main.getInstance().addResult(new LinkedList<Gyro>()); // Assuming I want to know that an invocation failed
            }
        }

    };

}
