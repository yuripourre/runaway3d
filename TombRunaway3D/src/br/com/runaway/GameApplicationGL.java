package br.com.runaway;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import br.com.abby.util.CameraGL;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.etyllica.layer.BufferedLayer;
import br.com.etyllica.linear.PointInt2D;
import br.com.runaway.application.CommonApplicationGL;
import br.com.runaway.collision.CollisionHandler;
import br.com.runaway.gl.KeyGL;
import br.com.runaway.gl.TrapGL;
import br.com.runaway.menu.Congratulations;
import br.com.runaway.menu.GameOver;
import br.com.runaway.player.TopViewPlayer;
import br.com.runaway.trap.Trap;
import br.com.runaway.ui.LifeBar;
import br.com.tide.input.controller.Controller;
import br.com.tide.input.controller.EasyController;
import br.com.vite.export.MapExporter;

public class GameApplicationGL extends CommonApplicationGL {

	public int currentLevel = 4;

	public static final int MAX_LEVEL = 10;

	public static final String PARAM_LEVEL = "level";

	protected float mx = 0;
	protected float my = 0;

	protected boolean click = false;

	private final double startAngle = 180;

	private double angleY = 0;

	private LifeBar lifeBar;

	private TopViewPlayer player;

	private CameraGL camera;

	private CollisionHandler handler;

	private Controller controller;

	public GameApplicationGL(int w, int h) {
		super(w, h);
	}


	private void loadMap() {

		int level = currentLevel;

		loadingInfo = "Loading Level "+level;
		loading = 1;

		try {
			map = MapExporter.load("map"+level+".json");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		loading = 10;

		map.disableGridShow();
		map.disableCollisionShow();
		map.disableCurrentTileShow();

		traps = new ArrayList<Trap>();
		trapModels = new ArrayList<TrapGL>();

		loading = 20;
		loadTiles(map);
		loadObjects(map);

		loading = 30;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		layer = new BufferedLayer("tiles/tileset.png");
		layer.cropImage(32*10, 0, 32, 32);
		
		keyModel = new KeyGL();

		loadMap();

		handler = new CollisionHandler(map.getMap());

		player = new TopViewPlayer(34, 32, handler);

		controller = new EasyController(player);

		camera = new CameraGL(player.getCenter().getX(), 16, player.getCenter().getY());

		lifeBar = new LifeBar(player);

		GL2 gl = drawable.getGL().getGL2();

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthMask(true);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glDepthRange(0.0f, 1.0f);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glViewport (x, y, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);

		gl.glLoadIdentity();

		float aspect = (float)width / (float)height; 

		glu.gluPerspective(60, aspect, 1, 1000);

		gl.glMatrixMode(GL2.GL_MODELVIEW);

		gl.glLoadIdentity();

	}

	@Override
	public void update(long now) {

		player.update(now);
		keyModel.update(now);
		
		checkTrapCollisions(now);

		checkKeyCollision(now);

		angleY = player.getAngle();

		//Inverted Orientation
		camera.setX(player.getCenter().getY());
		camera.setZ(player.getCenter().getX());

		handler.updateCollision(player);

	}
	
	private void checkTrapCollisions(long now) {

		PointInt2D center = player.getCenter();

		for(Trap trap : traps) {
			trap.update(now);

			if(trap.isActive() && !player.isInvincibility()) {

				if(trap.colideCirclePoint(center.getX(), center.getY())) {
					trapCollision(now);
				}
			}
		}		
	}

	private void checkKeyCollision(long now) {
		if(key == null)
			return;

		PointInt2D center = player.getCenter();
		
		if(key.colideCirclePoint(center.getX(), center.getY())) {
			nextLevel();
		}
	}

	private void trapCollision(long now) {
		player.loseLife(now);
		
		if(player.getCurrentLife() < 0)
			nextApplication = new GameOver(w, h);
	}

	private void nextLevel() {

		int level = currentLevel;

		if(level < MAX_LEVEL) {

			session.put(PARAM_LEVEL, level+1);

			nextApplication = new GameApplication(w, h, level+1);

		} else {
			nextApplication = new Congratulations(w, h);
		}
	}

	private double camSpeed = 0.5; 

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {

		controller.handleEvent(event);

		return GUIEvent.NONE;
	}

	@Override
	public GUIEvent updateMouse(PointerEvent event) {

		mx = event.getX();
		my = event.getY();

		if(event.isButtonDown(MouseButton.MOUSE_BUTTON_LEFT)) {
			camera.setZ(camera.getZ()+0.1f);
			click = true;
		}

		if(event.isButtonUp(MouseButton.MOUSE_BUTTON_LEFT)) {
			camera.setZ(camera.getZ()-0.1f);
			click = false;
		}

		return GUIEvent.NONE;
	}

	@Override
	public void preDisplay(GLAutoDrawable drawable, Graphic g) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(1f, 1f, 1f, 1);

	}

	@Override
	public void display(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glRotated(angleY+startAngle, 0, 1, 0);
		//gl.glTranslated((camera.getX()+offsetX),-(camera.getY()+offsetY),-(camera.getZ()+offsetZ));
		gl.glTranslated(camera.getX(),-camera.getY(),-camera.getZ());

		//Draw Scene
		drawFloor(gl);

		keyModel.draw(gl);
		
		for(TrapGL trap: trapModels) {
			trap.draw(gl);	
		}		

		gl.glFlush();

	}

	@Override
	public void draw(Graphic g) {

		lifeBar.draw(g);

		g.drawShadow(40, 40, "px: "+Integer.toString(player.getX()));
		g.drawShadow(40, 60, "py: "+Integer.toString(player.getY()));
		g.drawShadow(40, 80, "pa: "+Double.toString(player.getAngle()));

		g.drawShadow(40, 100, "cx: "+Double.toString(camera.getX()));
		g.drawShadow(40, 120, "cy: "+Double.toString(camera.getY()));
		g.drawShadow(40, 140, "cz: "+Double.toString(camera.getZ()));

		g.setAlpha(50);

		drawScene(g);

	}

	private void drawScene(Graphic g) {
		map.draw(g);

		player.draw(g);
	}

}