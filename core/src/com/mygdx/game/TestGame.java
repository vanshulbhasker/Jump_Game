package com.mygdx.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.soap.Text;

public class TestGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture m;
	Texture dizzyMan;
	Rectangle manRectangle;
	Rectangle edgeRectangle;
	ArrayList<Integer> coinsX=new ArrayList<Integer>();
	ArrayList<Integer> coinsY=new ArrayList<Integer>();
	ArrayList<Integer> bombsX=new ArrayList<Integer>();
	ArrayList<Integer> bombsY=new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles=new ArrayList<>();
	ArrayList<Rectangle> bombRectangles=new ArrayList<>();
	Texture bomb;
	int bombCount;
	Texture coin;
	int coinCount;
	int manPosition;
	int pause;
	int manY;
	int faults=0;
	float gravity,velocity;
	int score=0;
	BitmapFont font,gameOverText,faultFont;
	int gameState=0;

	Random random;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man=new Texture[4];
		man[0]=new Texture("frame-1.png");
		man[1]=new Texture("frame-2.png");
		man[2]=new Texture("frame-3.png");
		man[3]=new Texture("frame-4.png");
		manPosition=0;
		manY=Gdx.graphics.getHeight() / 2;
		velocity=0;
		gravity=1.1f;
		pause=0;
		coin=new Texture("coin.png");
		bomb=new Texture("bomb.png");
		random=new Random();
		manRectangle=new Rectangle();
		edgeRectangle=new Rectangle();
		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(5);
		gameOverText=new BitmapFont();
		gameOverText.setColor(Color.WHITE);
		gameOverText.getData().setScale(10);
		faultFont=new BitmapFont();
		faultFont.setColor(Color.WHITE);
		faultFont.getData().setScale(5);
		dizzyMan=new Texture("dizzy-1.png");
	}
	public void makeCoin(){
		float height=random.nextFloat()*Gdx.graphics.getHeight();
		coinsY.add((int)(height));
		coinsX.add(Gdx.graphics.getWidth());

	}
	public void makeBomb(){
		float height=random.nextFloat()*Gdx.graphics.getHeight();
		bombsY.add((int)(height));
		bombsX.add(Gdx.graphics.getWidth());

	}
	@Override
	public void render () {

		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState==0){
			//waiting to start
			if(Gdx.input.justTouched()){
				gameState=1;
			}

		}
		else if (gameState==1){
			//game is live

			//bombs

			if(bombCount<320){
				bombCount++;
			}
			else{
				bombCount=0;
				makeBomb();
			}
			bombRectangles.clear();
			for(int i=0;i<bombsX.size();i++){
				batch.draw(bomb,bombsX.get(i),bombsY.get(i));
				bombsX.set(i,bombsX.get(i)-18);
				bombRectangles.add(new Rectangle(bombsX.get(i),bombsY.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			//coins
			if(coinCount<80){
				coinCount++;
			}
			else{
				coinCount=0;
				makeCoin();
			}
			coinRectangles.clear();
			for(int i=0;i<coinsX.size();i++){
				batch.draw(coin,coinsX.get(i),coinsY.get(i));
				coinsX.set(i,coinsX.get(i)-16);
				coinRectangles.add(new Rectangle(coinsX.get(i),coinsY.get(i),coin.getWidth(),coin.getHeight()));
			}
			if(Gdx.input.justTouched()){
				velocity=-30;

			}
			if(pause<8){
				pause++;
			}
			else {
				pause=0;

				if (manPosition >= 3) {
					manPosition = 0;
				} else {
					manPosition++;
				}

			}
			m = man[manPosition];
			velocity+=gravity;
			manY-=velocity;
			if(manY<=0){
				manY=0;
			}
			if(manY>=Gdx.graphics.getHeight()- m.getHeight()-25){
				manY=Gdx.graphics.getHeight()- m.getHeight()-25;
			}

		}
		else if(gameState==2){
			//game over
			gameOverText.draw(batch,"GAME OVER \n SCORE : "+String.valueOf(score),120,1720);
			if(Gdx.input.justTouched()){
				gameState=1;
				manY=Gdx.graphics.getHeight() / 2;
				score=0;
				velocity=0;
				coinsY.clear();
				coinsX.clear();
				coinRectangles.clear();
				coinCount=0;
				bombsY.clear();
				bombsX.clear();
				bombRectangles.clear();
				bombCount=0;
				faults=0;
			}
		}
		m=man[manPosition];
		if(gameState==2){
			batch.draw(dizzyMan,Gdx.graphics.getWidth() / 2 - m.getWidth() / 2, manY);
		}
		else {
			batch.draw(m, Gdx.graphics.getWidth() / 2 - m.getWidth() / 2, manY);
		}
		manRectangle= new Rectangle(Gdx.graphics.getWidth() / 2 - m.getWidth() / 2, manY,man[manPosition].getWidth(),man[manPosition].getHeight());
		edgeRectangle= new Rectangle(10, 75,10,Gdx.graphics.getHeight()-20);

		for (int i=0;i<coinRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,coinRectangles.get(i))){
				score++;

				coinRectangles.remove(i);
				coinsX.remove(i);
				coinsY.remove(i);
				break;
			}
			if(Intersector.overlaps(edgeRectangle,coinRectangles.get(i))){
				coinRectangles.remove(i);
				coinsX.remove(i);
				coinsY.remove(i);
				faults++;
				if(faults>=3){
					gameState=2;
					break;
				}
			}
		}
		for (int i=0;i<bombRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,bombRectangles.get(i))){
				gameState=2;
			}
		}
		font.draw(batch,"SCORE : "+String.valueOf(score),20,75);
		faultFont.draw(batch,"FAULTS : "+String.valueOf(faults),660,75);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();

	}
}
