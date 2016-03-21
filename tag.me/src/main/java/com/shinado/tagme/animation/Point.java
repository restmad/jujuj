package com.shinado.tagme.animation;

public class Point extends android.graphics.Point{

	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}

	public Point(float x, float y) {
		this.x = (int) x;
		this.y = (int) y;
	}
	
}
