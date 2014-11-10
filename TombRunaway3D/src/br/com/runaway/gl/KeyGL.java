package br.com.runaway.gl;

import java.awt.Color;

import javax.media.opengl.GL2;

import br.com.luvia.linear.Mesh;

public class KeyGL {
	
	private Mesh mesh;
	
	public KeyGL() {
		super();
		
		mesh = new Mesh("key.obj");
		mesh.setColor(Color.BLACK);
		mesh.setScale(8);
		mesh.setAngleZ(90);
	}
	
	public void update(long now) {
		
		mesh.setOffsetAngleY(2);
		
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public void draw(GL2 gl) {
		mesh.simpleDraw(gl);
	}
	
}
