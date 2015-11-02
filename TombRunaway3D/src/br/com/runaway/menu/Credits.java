package br.com.runaway.menu;

import java.awt.Color;

import br.com.etyllica.core.animation.script.OpacityAnimation;
import br.com.etyllica.core.event.MouseButton;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.layer.ImageLayer;
import br.com.luvia.core.context.ApplicationGL;
import br.com.luvia.core.video.Graphics3D;

public class Credits extends ApplicationGL {

	private ImageLayer background;
	
	private ImageLayer title;
	
	private ImageLayer label;
	
	public Credits(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {
		
		background = new ImageLayer("menu/background.jpg");
		
		title = new ImageLayer(0, 60, "title.png");
		title.centralizeX(this);
		
		label = new ImageLayer("menu/credits.png");
		label.centralize(this);
		
		OpacityAnimation fadeIn = new OpacityAnimation(label, 5000);
		scene.addAnimation(fadeIn);
		
		loading = 100;
	}
	
	@Override
	public void updateMouse(PointerEvent event) {
		if(event.isButtonDown(MouseButton.MOUSE_BUTTON_LEFT))
			restartGame();
	}
	
	@Override
	public void draw(Graphic g) {
		g.setColor(Color.BLACK);
		g.fillRect(this);
		background.draw(g);
		title.draw(g);
		label.draw(g);
	}
	
	private void restartGame() {
		nextApplication = new MainMenu(w, h);
	}

	@Override
	public void display(Graphics3D gl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Graphics3D gl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(Graphics3D gl, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}

}
