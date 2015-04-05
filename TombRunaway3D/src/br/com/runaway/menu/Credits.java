package br.com.runaway.menu;

import br.com.etyllica.animation.scripts.OpacityAnimation;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.input.mouse.MouseButton;
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
	
	public GUIEvent updateMouse(PointerEvent event) {
	
		if(event.isButtonDown(MouseButton.MOUSE_BUTTON_LEFT))
			restartGame();
				
		return null;
	}
	
	public GUIEvent updateKeyboard(KeyEvent event) {
		if(event.isKeyDown(KeyEvent.TSK_ESC))
			restartGame();
		
		return null;
	}
	
	@Override
	public void draw(Graphic g) {
		background.simpleDraw(g);
		title.simpleDraw(g);
		label.simpleDraw(g);
	}
	
	private void restartGame() {
		nextApplication = new MainMenu(w, h);
	}

	@Override
	public void display(Graphics3D arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Graphics3D arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(Graphics3D arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}

}
