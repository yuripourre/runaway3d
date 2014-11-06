package br.com.runaway.menu;

import java.awt.Color;

import br.com.etyllica.animation.listener.OnAnimationFinishListener;
import br.com.etyllica.animation.scripts.OpacityAnimation;
import br.com.etyllica.context.Application;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.etyllica.layer.ImageLayer;

public class GameOver extends Application implements OnAnimationFinishListener {

	private ImageLayer background;	
	
	public GameOver(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {
		
		background = new ImageLayer("menu/gameover.jpg");
		
		OpacityAnimation fadeIn = new OpacityAnimation(background, 10000);
		fadeIn.setInterval(0, 0xff);
		fadeIn.setListener(this);
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

	@Override
	public void onAnimationFinish(long now) {
		restartGame();
	}
	
	private void restartGame() {
		nextApplication = new MainMenu(w, h);
	}

}
