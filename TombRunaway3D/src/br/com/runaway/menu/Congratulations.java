package br.com.runaway.menu;

import java.awt.Color;

import javax.media.opengl.GLAutoDrawable;

import br.com.etyllica.animation.scripts.OpacityAnimation;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.etyllica.layer.ImageLayer;
import br.com.luvia.core.ApplicationGL;

public class Congratulations extends ApplicationGL {

	private ImageLayer background;	
	
	public Congratulations(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {
		
		background = new ImageLayer("menu/congratulations.jpg");
		
		OpacityAnimation fadeIn = new OpacityAnimation(background, 5000);
		fadeIn.setInterval(0, 0xff);
		scene.addAnimation(fadeIn);
		
		loading = 100;
	}
	
	public GUIEvent updateMouse(PointerEvent event) {
	
		if(event.isButtonDown(MouseButton.MOUSE_BUTTON_LEFT))
			restartGame();
		
		return null;
	}
	
	@Override
	public void draw(Graphic g) {
		g.setColor(Color.BLACK);
		g.fillRect(this);
		background.draw(g);
	}
	
	private void restartGame() {
		nextApplication = new MainMenu(w, h);
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}

}
