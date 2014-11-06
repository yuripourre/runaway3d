package br.com.runaway.ui;

import java.awt.Color;

import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.graphics.SVGColor;
import br.com.runaway.player.TopViewPlayer;

public class LifeBar {
			
	private int offsetX = 640;

	private int offsetY = 50;
	
	private TopViewPlayer player;
	
	public LifeBar(TopViewPlayer player) {
		super();
		
		this.player = player;
	}

	public void draw(Graphic g) {

		int radius = 20;
		
		int spacing = 5;
		
		g.setAlpha(95);
		
		for(int i = 0; i < player.getTotalLife(); i++) {

			if(i < player.getCurrentLife()) {
				g.setColor(SVGColor.CRIMSON);
				g.fillCircle(offsetX+i*(2*radius+spacing), offsetY, radius);
			}
			
			g.setColor(Color.BLACK);
			g.drawCircle(offsetX+i*(2*radius+spacing), offsetY, radius);
			
		}
		
		g.resetOpacity();

	}

}
