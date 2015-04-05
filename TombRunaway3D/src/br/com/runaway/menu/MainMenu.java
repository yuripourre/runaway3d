package br.com.runaway.menu;

import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.etyllica.gui.Button;
import br.com.etyllica.gui.label.TextLabel;
import br.com.etyllica.layer.ImageLayer;
import br.com.luvia.core.context.ApplicationGL;
import br.com.luvia.core.video.Graphics3D;
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
		background.simpleDraw(g);		
		title.simpleDraw(g);
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
	public void init(Graphics3D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(Graphics3D g, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void display(Graphics3D g) {
		// TODO Auto-generated method stub
		
	}

}
