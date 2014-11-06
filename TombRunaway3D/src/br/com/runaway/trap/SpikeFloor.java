package br.com.runaway.trap;

import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.layer.ImageLayer;

public class SpikeFloor extends Trap {
	
	private ImageLayer layer;
	
	private static final int DELAY = 2200;
	
	public SpikeFloor(int x, int y) {
		super();
		setBounds(x, y, 32, 32);
		
		layer = new ImageLayer(x, y-6, 32, 38, "traps/spike.png");
	}

	@Override
	public void draw(Graphic g) {
		
		if(active) {
			layer.setYImage(0);
			layer.setH(38);
		} else {
			layer.setYImage(38);
			layer.setH(38);
		}
		
		layer.draw(g);
	}
	public void update(long now) {
		if(!started) {
			started = true;
			activeTime = now;
		}
		if(now > activeTime + DELAY) {
			active = !active;
			activeTime = now;
		}
	}
}
