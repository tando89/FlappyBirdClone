package com.tando.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import sun.rmi.runtime.Log;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	//image background
	Texture background;
	Texture gameover;
	//new batch for shape
	//TODO: Uncomment to demo
    //ShapeRenderer shapeRenderer;
	Texture[] birds;
	//flip between bird 0 and bird 1
	int flapState = 0;
	float birdY = 0;
	//how fast the bird will be moving
	float velocity = 0;
	//state 0: start the game
	int gameState = 0;
	float gravity = 2;
    //shape of the bird for collision detection. Circle fit perfectly for the bird
	Circle birdCircle;
	//Scores variables
	int score = 0;
	int scoringTube = 0;
	//font for score
	BitmapFont font;

	//Tubes the top and the bottom
	Texture topTube;
	Texture bottomTube;

	//The gap between tubes  can make the game easier or harder
	float gap = 700; //1000 would be super easy
	//The distance of the tubes up/down
	float maxTubeOffset;
	//Generate random gaps
	Random randomGenerator;
    //move the tube by creating velocity for them
	float tubeVelocity = 4;
	//4 set of tubes
	int numberOfTubes = 4;
	//tubes' coordinate. This will be change since the tubes move.
	float[] tubeX = new float[numberOfTubes];
	//offset will be different for any pair of tubes
	float[] tubeOffset = new float[numberOfTubes];
	//distance between tubes
	float distanceBetweenTubes;
    //Create rectangle shapes for collision detect. The rectangles will be covered the tubes images
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	@Override
	public void create () {
		//create a Sprite
		batch = new SpriteBatch();
		//backgroud and the gameover graphic are textures
		background = new Texture("bg.png");
		//gameover image
		gameover = new Texture("flappybirdgameover.png");

		//TODO: Uncomment to demo
		//shapeRenderer = new ShapeRenderer();
		
		birdCircle = new Circle();
		//font style for score
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		font.getData().setScale(10);
        //the bird's images are 2 texture the bird itself and the winds.
		birds = new Texture[2];
		//bird 0 will be the bird
		birds[0] = new Texture("bird.png");
		//bird 1 will be the wings
		birds[1] = new Texture("bird2.png");


		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");

		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;

		randomGenerator = new Random();
        //Distance between tubes
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4; // dividing by 2 only will be hard, 3/4 will be more playable
        //array of 4 top tubes
		topTubeRectangles = new Rectangle[numberOfTubes];
		//array of 4 bottom tubes
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		startGame();

	}
	//start game method
	public void startGame() {
		//start height position
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

		for (int i = 0; i < numberOfTubes; i++) {

			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			//defines the rectangles
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();

		}
	}


	@Override
	public void render () {
		batch.begin();
		//display the background first
		//getWidth() and getHeight() methods to make the background full screen
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//start processing when user touches the screen ( state 1 the game is running)
		if (gameState == 1) {

			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
				//Score increases by 1 each time
				score++;

				Gdx.app.log("Score", String.valueOf(score));
                //Score is counted by the bird passes the tube without collision
				if (scoringTube < numberOfTubes - 1) {
					scoringTube ++;
				}
				else {
					scoringTube = 0;
				}
			}

			if (Gdx.input.justTouched()) {
				Gdx.app.log("tapped", "yep!");

				//gameState = 1;
				// the height of the bird when tap
				velocity = -30;


			}

			//display the tubes. They loop forever until the bird hit them
            //The tubes will be displayed randomly
			for (int i = 0; i < numberOfTubes; i++) {
 				//check if the 4 tubes go off the screen from the left
				if (tubeX[i] < - topTube.getWidth()) {
                    //if it is true, we move the tubes back to the right to make a loop
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    //Randomized the height of the tubes
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

				} else {
					//if it is not on the left of screen, continue to move it to the left
					tubeX[i] = tubeX[i] - tubeVelocity;



				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
				//rectangles are overlapped the tubes images. The position should be the same location of the tubes on screen.
				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], topTube.getWidth(), topTube.getHeight());

			}

			//bird responds to taps

			if (birdY > 0) {
				//increase the velocity each time the render loop called
				velocity = velocity + gravity;
				//decrease the position of the bird by the velocity (fall faster)
				birdY -= velocity;

			} else {
				//State 2 is when the game is over
				gameState = 2;
			}
		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {

				gameState = 1;
			}
		//when the game is over, display the gameover image
		} else if (gameState == 2) {
			batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);
			//After gameover, user touches screen again, restart the game
			if (Gdx.input.justTouched()) {

				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}
		//Bird's wing flip beteen each other
		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}


		//display the bird at the centen of screen lastly
		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth(), birdY);
		//display score
		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
		//Circle will be overlapped of the bird image
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2,  birds[flapState].getHeight() / 2);

		//TODO: Uncomment to demo

		/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);*/

		//Detect the collision
		for (int i = 0; i < numberOfTubes; i++) {
			//TODO: Uncomment to demo
			/*shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], topTube.getWidth(), topTube.getHeight());*/

            //check for the circle and rectangles intersection (if they hit each other or not)
			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
				Gdx.app.log("Collision" , "Yes!" );

				gameState = 2;
			}
		}

		//TODO: Uncomment to demo
		//shapeRenderer.end();
	}
	


}
