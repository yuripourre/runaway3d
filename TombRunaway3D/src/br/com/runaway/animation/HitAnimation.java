package br.com.runaway.animation;

import br.com.etyllica.core.animation.AnimationHandler;
import br.com.etyllica.core.animation.script.OpacityAnimation;
import br.com.runaway.player.TopViewPlayer;

public class HitAnimation {

	private OpacityAnimation firstAnimation;
	
	private OpacityAnimation lastAnimation;
	
	public HitAnimation(TopViewPlayer player) {
		super();
		
		firstAnimation = new OpacityAnimation(player.getLayer(), 500);
		firstAnimation.setInterval(0xff, 0);
		
		lastAnimation = new OpacityAnimation(player.getLayer(), 500);
		lastAnimation.setInterval(0, 0xff);
		
		player.getLayer().setOpacity(0xff);
		
		firstAnimation.addNext(lastAnimation);
		
		lastAnimation.setListener(player);
	}
		
	public void startAnimation(long now) {
		firstAnimation.start(now);
		AnimationHandler.getInstance().add(firstAnimation);
	}
	
}
