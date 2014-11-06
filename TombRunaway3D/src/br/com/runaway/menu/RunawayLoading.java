package br.com.runaway.menu;

import java.awt.Color;
import java.awt.Font;

import br.com.etyllica.context.load.GenericLoadApplication;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.layer.ImageLayer;
import br.com.etyllica.theme.Theme;
import br.com.etyllica.theme.ThemeManager;

public class RunawayLoading extends GenericLoadApplication {

	private ImageLayer background;

	private ImageLayer title;
	
	private Font f;
	
	private Font p;
	
	private float rectW = w*2/3;
	private float rectX = w/2-rectW/2;
	private float rectY = h/2+100;
	private float rectH = 32;
		
	public RunawayLoading(int x, int y, int w, int h) {
		super(x, y, w, h);
		
		background = new ImageLayer("menu/background.jpg");

		title = new ImageLayer(0, 60, "title.png");
		title.centralizeX(this);
		
		Theme theme = ThemeManager.getInstance().getTheme();
		
		f = new Font(theme.getFontName(), theme.getFontStyle(), 26);
		p = new Font(theme.getFontName(), theme.getFontStyle(), 18);
	}
		
	@Override
	public void draw(Graphic g) {

		background.draw(g);
		title.draw(g);
		
		g.setFont(f);
				
		g.setColor(Color.WHITE);
		g.drawStringShadowX(280-y, phrase);

		g.drawRect(rectX, rectY, rectW, rectH);
		g.fillRect(rectX+2, rectY+2, (int)((rectW*fill)/100)-3, rectH-3);
		
		g.setFont(p);
		g.drawStringShadow(rectX, rectY, rectW, rectH, percent, Color.BLACK);
		
	}
	
}
