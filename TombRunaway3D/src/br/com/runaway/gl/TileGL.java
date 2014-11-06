package br.com.runaway.gl;

import com.jogamp.opengl.util.texture.Texture;

public class TileGL {

	private int x = 0;
	
	private int y = 0;
	
	private Texture texture;
	
	public TileGL(int x, int y, Texture texture) {
		super();
		
		this.x = x;
		this.y = y;
		this.texture = texture;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
		
}
