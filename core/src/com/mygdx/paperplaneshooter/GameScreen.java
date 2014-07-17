package com.mygdx.paperplaneshooter;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
	
	private final PaperplaneShooter game;
	private Texture planeImage;
	private Texture ballImage;
	private Texture needleImage;
	private Sound damageSound;
	private Music bgm;
	private OrthographicCamera camera;
	//private SpriteBatch batch;
	private Rectangle plane;
	private Array<Rectangle> ballDrops;
	private Array<Rectangle> needleShots;
	private long lastDropTime;
	private long lastShotTime;
	int score;
	int lifeCount;
	
	public GameScreen(final PaperplaneShooter game){
		this.game = game;
		
		planeImage = new Texture(Gdx.files.internal("paperplane.png"));
		ballImage = new Texture(Gdx.files.internal("paperball.png"));
		needleImage = new Texture(Gdx.files.internal("paperneedle.png"));
		
		damageSound = Gdx.audio.newSound(Gdx.files.internal("damage.ogg"));
		bgm = Gdx.audio.newMusic(Gdx.files.internal("bgm.mp3"));
		bgm.setLooping(true);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 640);
		
		plane = new Rectangle();
		plane.x = 800/2-64/2;
		plane.y = 20;
		plane.width = 64;
		plane.height = 64;
		
		//spawning ball drop
		ballDrops = new Array<Rectangle>();
		spawnBallDrop();
		
		needleShots = new Array<Rectangle>();
		
		this.score = 0;
		this.lifeCount = 3;
	}

	private void spawnBallDrop(){
		Rectangle ballDrop = new Rectangle();
		ballDrop.x = MathUtils.random(0, 800-64);
		ballDrop.y = 640;
		ballDrop.width = 64;
		ballDrop.height = 64;
		ballDrops.add(ballDrop);
		lastDropTime = TimeUtils.millis();
	}
	
	private void fireNeedleShot(){
		Rectangle needleShot = new Rectangle();
		needleShot.x = plane.x+18;
		needleShot.y = plane.y+64;
		needleShot.height = 32;
		needleShot.width = 32;
		needleShots.add(needleShot);
		lastShotTime = TimeUtils.millis();
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "Score", 50, 640-30);
		game.font.draw(game.batch, Integer.toString(score), 50, 640-50);
		game.font.draw(game.batch, "Life", 800-60, 640-30);
		game.font.draw(game.batch, Integer.toString(lifeCount), 800-60, 640-50);
		game.batch.draw(planeImage, plane.x, plane.y);
		for(Rectangle ballDrop: ballDrops){
			game.batch.draw(ballImage, ballDrop.x, ballDrop.y);
		}
		for(Rectangle needleShot: needleShots){
			game.batch.draw(needleImage, needleShot.x, needleShot.y);
		}
		game.batch.end();
		
		//proccess user input
		
		/*if(Gdx.input.isTouched()){
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			plane.x = touchPos.x - 64/2;
			plane.y = touchPos.y - 64/2;
		}*/
		
		if(Gdx.input.isKeyPressed(Keys.LEFT)) plane.x = plane.x - 333*Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) plane.x = plane.x + 333*Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.UP)) plane.y = plane.y + 333*Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.DOWN)) plane.y = plane.y - 333*Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)){
			if(TimeUtils.millis()-lastShotTime > 500) fireNeedleShot();
		}
		
		if(plane.x<0) plane.x = 0;
		if(plane.x>800-64) plane.x = 800-64;
		if(plane.y<0) plane.y = 0;
		if(plane.y>500-64) plane.y = 500-64;
		
		if(TimeUtils.millis()-lastDropTime > 1750) spawnBallDrop();
		
		
		Iterator<Rectangle> iter = ballDrops.iterator();
		Iterator<Rectangle> iter2 = needleShots.iterator();
		Rectangle ballDrop = iter.next();
		
		//while(lifeCount>0){
			while(iter.hasNext()){
				ballDrop = iter.next();
				ballDrop.y = ballDrop.y - 200 * Gdx.graphics.getDeltaTime();
				if(ballDrop.y+64 < 0){
					lifeCount--;
					iter.remove();
				}
				while(iter2.hasNext()){
					Rectangle needleShot = iter2.next();
					needleShot.y = needleShot.y + 300 * Gdx.graphics.getDeltaTime();
					if(needleShot.y+32 > 640) iter2.remove();
					if(needleShot.overlaps(ballDrop)){
						damageSound.play();
						score = score+10;
						iter2.remove();
						iter.remove();
					}
				}
			}
		
		
		if(lifeCount==0){
			game.setScreen(new EndScreen(game));
			pause();
			dispose();
		}
		//Rectangle needleShot = iter2.next();
		/*while(iter.hasNext()){
			ballDrop = iter.next();
			ballDrop.y = ballDrop.y - 200 * Gdx.graphics.getDeltaTime();
			if(ballDrop.y+64 < 0) iter.remove();
		}
		while(iter2.hasNext()){
			Rectangle needleShot = iter2.next();
			needleShot.y = needleShot.y + 200 * Gdx.graphics.getDeltaTime();
			if(needleShot.y+32 > 480) iter2.remove();
			if(needleShot.overlaps(ballDrop)){
				damageSound.play();
				iter2.remove();
				iter.remove();
			}
		}*/		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		bgm.play();
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
				
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		planeImage.dispose();
		ballImage.dispose();
		needleImage.dispose();
		bgm.dispose();
		damageSound.dispose();
	}

}
