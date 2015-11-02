package br.com.runaway.menu;

import java.awt.Color;

import br.com.etyllica.core.animation.OnAnimationFinishListener;
import br.com.etyllica.core.animation.script.OpacityAnimation;
import br.com.etyllica.core.event.MouseButton;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.layer.ImageLayer;
import br.com.luvia.core.context.ApplicationGL;
import br.com.luvia.core.video.Graphics3D;

public class GameOver extends ApplicationGL implements OnAnimationFinishListener {

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
	}

	@Override
	public void onAnimationFinish(long now) {
		restartGame();
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
