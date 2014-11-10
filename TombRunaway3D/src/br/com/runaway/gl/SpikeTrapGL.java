package br.com.runaway.gl;

import java.awt.Color;

import javax.media.opengl.GL2;

import br.com.luvia.linear.Mesh;
import br.com.runaway.trap.Trap;

public class SpikeTrapGL extends TrapGL {

	private Trap trap;
	
	private Mesh hole;
	
	private Mesh spike;
	
	public SpikeTrapGL(int x, int y, Trap trap) {
		super();
		
		this.trap = trap;
		
		hole = new Mesh("holes.obj");
		hole.setColor(Color.BLACK);
		hole.setScale(5);
		hole.setCoordinates(-x, 0, y);
		
		spike = new Mesh("spikes.obj");
		spike.setColor(Color.WHITE);
		spike.setScale(5);
		spike.setCoordinates(-x, 0, y);
		
	}
	
	public void draw(GL2 gl) {
		hole.simpleDraw(gl);
		
		if(trap.isActive())
			spike.simpleDraw(gl);
	}
	
}
