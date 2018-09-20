package com.hegyi.botond;

import com.sun.javafx.scene.traversal.Direction;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GameScene extends Scene {
	public static final int PIXELSIZE = 25;

	private GraphicsContext gc;
	private Canvas canvas;
	private final int WIDTH = 1000;
	private final int HEIGHT = 700;

	private Food food;
	private Snake snake;

	private boolean paused = true;

	public GameScene(Parent root) {
		super(root);

		canvas = new Canvas(WIDTH, HEIGHT);
		((Pane) root).getChildren().add(canvas);

		gc = canvas.getGraphicsContext2D();

		food = new Food(PIXELSIZE, PIXELSIZE);
		snake = new Snake(new Point2D(PIXELSIZE, 0),
				new Point2D(0, 0), PIXELSIZE);

		initFirstFrame();

		initActionHandlers();

		snake.setDirection(Direction.RIGHT);
		paused = false;

		new myTimer().start();
	}

	private void initActionHandlers() {
		this.setOnKeyPressed(e -> {
			switch (e.getCode()) {
				case RIGHT: {
					snake.setDirection(Direction.RIGHT);
					break;
				}
				case LEFT: {
					snake.setDirection(Direction.LEFT);
					break;
				}
				case DOWN: {
					snake.setDirection(Direction.DOWN);
					break;
				}
				case UP: {
					snake.setDirection(Direction.UP);
					break;
				}
				case ESCAPE: {
					paused = !paused;
				}
			}
		});
	}

	private void initFirstFrame() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, WIDTH, HEIGHT);

		food.setRandomPosition(WIDTH, HEIGHT);
		food.render(gc);

		snake.render(gc);

		paintGrid(gc);
	}

	private void paintGrid(GraphicsContext gc) {
		gc.setStroke(Color.GRAY);
		for (int i = 0; i < WIDTH; i += PIXELSIZE) {
			gc.strokeLine(i, 0, i, HEIGHT);
		}
		for (int i = 0; i < HEIGHT; i += PIXELSIZE) {
			gc.strokeLine(0, i, WIDTH, i);
		}
	}

	private class myTimer extends AnimationTimer {
		private long lastUpdate = 0;

		@Override
		public void handle(long now) {
			// if the game isn't paused it will refresh the screen in every 100 milliseconds
			if (!paused) {
				if (now - lastUpdate >= 100_000_000) {
					lastUpdate = now;

					gc.setFill(Color.BLACK);
					gc.fillRect(0, 0, getWidth(), getHeight());

					if (snake.intersect(food)) {
						food.setRandomPosition(1000, 700);
					}

					food.render(gc);

					snake.render(gc);

					paintGrid(gc);
					snake.move();
				}
			} else {
				gc.setFill(Color.WHITE);
				Font.loadFont(getClass().getClassLoader().getResourceAsStream("font/lunchds.ttf"), 30);
				gc.setFont(new Font("Lunchtime Doubly So Regular", 30));
				gc.setTextAlign(TextAlignment.CENTER);
				gc.setTextBaseline(VPos.CENTER);
				//gc.fillText("Paused!\nYour score:", WIDTH/2, HEIGHT/2);
				// TODO: display the player score after it's implemented
				gc.fillText("Paused!", WIDTH/2, HEIGHT/2);
			}
		}
	}
}
