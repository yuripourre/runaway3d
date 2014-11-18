package br.com.runaway.menu;

import javax.media.opengl.GLAutoDrawable;

import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.etyllica.gui.Button;
import br.com.etyllica.gui.label.TextLabel;
import br.com.etyllica.layer.ImageLayer;
import br.com.luvia.core.ApplicationGL;
import br.com.runaway.GameApplicationGL;

public class MainMenu extends ApplicationGL {

	private ImageLayer background;

	private ImageLayer title;
	
	private static final int INITIAL_LEVEL = 1;
	
	private Button playButton;
	
	private Button creditsButton;
	
	public MainMenu(int w, int h) {
		super(w, h);
		
		loadApplication = new RunawayLoading(x, y, w, h);		
	}

	public void doOpenGame() {
		nextApplication = new GameApplicationGL(w, h, INITIAL_LEVEL);
	}

	public void doOpenCredits() {
		nextApplication = new Credits(w, h);		
	}

	@Override
	public void load() {

		background = new ImageLayer("menu/background.jpg");

		title = new ImageLayer(0, 60, "title.png");
		title.centralizeX(this);

		int buttonWidth = 200;

		playButton = new Button(w/2-buttonWidth/2, 300, buttonWidth, 60);
		playButton.setLabel(new TextLabel("Novo Jogo"));
		
		creditsButton = new Button(w/2-buttonWidth/2, 380, buttonWidth, 60);
		creditsButton.setLabel(new TextLabel("Creditos"));
		
		add(playButton);
		add(creditsButton);
		
		loading = 100;
	}

	@Override
	public void draw(Graphic g) {
		background.draw(g);
		title.draw(g);
	}
	
	public GUIEvent updateMouse(PointerEvent event) {
		
		if(event.isButtonUp(MouseButton.MOUSE_BUTTON_LEFT)) {
			
			if(playButton.onMouse(event)) {
				doOpenGame();
			}
			
			if(creditsButton.onMouse(event)) {
				doOpenCredits();
			}
		}
		
		return GUIEvent.NONE;		
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
