package br.com.runaway.trap;

import br.com.etyllica.core.Drawable;
import br.com.etyllica.layer.GeometricLayer;

public abstract class Trap extends GeometricLayer implements Drawable {
	
	protected int interval = 500;
	
	protected boolean active = false;
	
	protected boolean started = false;

	protected long activeTime = 0;
	
	public abstract void update(long now);

	public boolean isActive() {
		return active;
	}
	
}
