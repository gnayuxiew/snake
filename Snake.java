import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Snake extends Application {
    private int width = 30;
    private int height = 30;
    private int blockSize = 10;
    private int[] snake = new int[width * height];//每一节的数字
    private Rectangle[] rectangles = new Rectangle[width * height];
    private int len = 2;
    private Pane pane = new Pane();
    private int foodIndex;
    private int direction = 1;
    private int mutex;//防止连续点击两次键盘造成的bug
    private ArrayList<Integer> list = new ArrayList<>();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(pane, width * blockSize, height * blockSize);
        stage.setScene(scene);
        stage.show();
        gameInit();
        Thread game = new Thread(this::move);
        game.start();
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            switch (code) {
                case W:
                    if (direction != 2 && mutex == 0) {
                        direction = 0;
                        mutex = 1;
                    }
                    break;
                case D:
                    if (direction != 3 && mutex == 0) {
                        direction = 1;
                        mutex = 1;
                    }
                    break;
                case S:
                    if (direction != 0 && mutex == 0) {
                        direction = 2;
                        mutex = 1;
                    }
                    break;
                case A:
                    if (direction != 1 && mutex == 0) {
                        direction = 3;
                        mutex = 1;
                    }
                    break;
            }
        });
    }

    private void gameInit() {
        for (int i = 0; i < rectangles.length; i++) {
            rectangles[i] = new Rectangle(blockSize, blockSize);
            rectangles[i].setX(i % width * blockSize);
            rectangles[i].setY(i / width * blockSize);
            rectangles[i].setSmooth(false);
            pane.getChildren().add(rectangles[i]);
            rectangles[i].setVisible(false);
            list.add(i);
        }
        snake[0] = 1;
        snake[1] = 0;
        for (int i = 0; i < len; i++) {
            rectangles[snake[i]].setVisible(true);//初始化蛇身体
            list.remove((Integer) snake[i]);
        }
        generateFood();
    }

    private void generateFood() {
        list.clear();
        for (int i = 0; i < width * height; i++) {
            list.add(i);
        }
        for (int i = 0; i < len; i++) {
            list.remove((Integer) snake[i]);
        }
        if (len < width * height) {
            foodIndex = (int) (Math.random() * list.size());
            foodIndex = list.get(foodIndex);
            rectangles[foodIndex].setFill(Color.BLUE);
            rectangles[foodIndex].setVisible(true);
        } else {
            rectangles[snake[0]].setFill(Color.BLACK);
            System.out.println("Win");
            return;
        }

    }

    private void move() {
        while (true) {
            if (isBite()) {//判断有没有咬到自己
                rectangles[snake[0]].setFill(Color.RED);
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            switch (direction) {
                case 0:
                    if (snake[0] / width == 0) {
                        rectangles[snake[0]].setFill(Color.RED);
                        return;
                    }
                    if (rectangles[snake[0]].getX() == rectangles[foodIndex].getX() && rectangles[snake[0]].getY() - rectangles[foodIndex].getY() == blockSize) {
                        eat();
                    } else {
                        rectangles[snake[len - 1]].setVisible(false);//尾巴消失
                        if (len - 1 >= 0) System.arraycopy(snake, 0, snake, 1, len - 1);
                        snake[0] -= width;
                    }
                    break;
                case 1://右
                    if (snake[0] % width == width - 1) {
                        rectangles[snake[0]].setFill(Color.RED);
                        return;
                    }
                    if (rectangles[snake[0]].getY() == rectangles[foodIndex].getY() && rectangles[snake[0]].getX() - rectangles[foodIndex].getX() == -blockSize) {
                        eat();
                    } else {
                        rectangles[snake[len - 1]].setVisible(false);
                        if (len - 1 >= 0) System.arraycopy(snake, 0, snake, 1, len - 1);
                        snake[0] += 1;
                    }
                    break;
                case 2://下
                    if (snake[0] / width == height - 1) {
                        rectangles[snake[0]].setFill(Color.RED);
                        return;
                    }
                    if (rectangles[snake[0]].getX() == rectangles[foodIndex].getX() && rectangles[snake[0]].getY() - rectangles[foodIndex].getY() == -blockSize) {
                        eat();
                    } else {
                        rectangles[snake[len - 1]].setVisible(false);
                        if (len - 1 >= 0) System.arraycopy(snake, 0, snake, 1, len - 1);
                        snake[0] += width;
                    }
                    break;
                case 3:
                    if (snake[0] % width == 0) {
                        rectangles[snake[0]].setFill(Color.RED);
                        return;
                    }
                    if (rectangles[snake[0]].getY() == rectangles[foodIndex].getY() && rectangles[snake[0]].getX() - rectangles[foodIndex].getX() == blockSize) {
                        eat();
                    } else {
                        rectangles[snake[len - 1]].setVisible(false);
                        if (len - 1 >= 0) System.arraycopy(snake, 0, snake, 1, len - 1);
                        snake[0] -= 1;
                    }
                    break;
            }
            rectangles[snake[0]].setVisible(true);//显示新蛇头
            mutex = 0;
        }
    }

    private boolean isBite() {
        for (int i = 1; i < len; i++) {
            if (snake[0] == snake[i]) {
                return true;
            }
        }
        return false;
    }

    private void eat() {
        if (len >= 0) System.arraycopy(snake, 0, snake, 1, len);
        len++;
        snake[0] = foodIndex;
        rectangles[foodIndex].setFill(Color.BLACK);
        generateFood();
    }
}
