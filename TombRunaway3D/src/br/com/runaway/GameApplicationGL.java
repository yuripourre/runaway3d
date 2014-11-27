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
import br.com.tide.input.controller.JoystickController;
import br.com.vite.export.MapExporter;

public class GameApplicationGL extends CommonApplicationGL {

	public int currentLevel = 1;

	public static final int MAX_LEVEL = 10;

	public static final String PARAM_LEVEL = "level";

	private final double startAngle = 180;

	private LifeBar lifeBar;

	private TopViewPlayer player;

	private CameraGL camera;

	private CollisionHandler handler;

	private Controller controller;
	private Controller joystick;

	private boolean reloading = false;

	protected boolean debug = false;

	public GameApplicationGL(int w, int h, int currentLevel) {
		super(w, h);

		this.currentLevel = currentLevel;
	}

	private void loadMap() {

		int level = currentLevel;

		loadingInfo = "Loading Level "+level;
		loading = 1;

		try {
			map = MapExporter.load("map"+level+".json");
		} catch (FileNotFoundException e) {
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

		keyModel = new KeyGL();

		loadMap();

		reload();

		GL2 gl = drawable.getGL().getGL2();

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthMask(true);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glDepthRange(0.0f, 1.0f);
		 
		gl.glEnable(GL2.GL_LIGHTING);		

		float ambientLight[] = { 0.3f, 0.3f, 0.0f, 1.0f }; 
		float diffuseLight[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float specularLight[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0); 
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specularLight, 0);
		gl.glLightf(GL2.GL_LIGHT0, GL2.GL_LINEAR_ATTENUATION, 1.0f);
		
		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);

		gl.glEnable(GL2.GL_COLOR_MATERIAL);		
	}

	private void reload() {
		handler = new CollisionHandler(map.getMap());

		player = new TopViewPlayer(34, 32, handler);

		controller = new EasyController(player);
		joystick = new JoystickController(player);

		//double px = player.getCenter().getX();
		//double py = player.getCenter().getY();
		
		double px = player.getDx()+player.getLayer().getTileW()/2;
		double py = player.getDy()+player.getLayer().getTileH()/2;
		
		camera = new CameraGL(px, 16, py);
		
		lifeBar = new LifeBar(player);
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

		double px = player.getDx()+player.getLayer().getTileW()/2;
		double py = player.getDy()+player.getLayer().getTileH()/2;
		
		//Inverted Orientation
		camera.setX(py);
		camera.setZ(px);

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

			reloading = true;
			currentLevel++;
			loadMap();
			reload();
			reloading = false;

		} else {
			nextApplication = new Congratulations(w, h);
		}
	}

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {

		controller.handleEvent(event);
		joystick.handleEvent(event);

		if(event.isKeyUp(KeyEvent.TSK_D)) {
			debug = !debug;
		}


		return GUIEvent.NONE;
	}
	
	@Override
	public GUIEvent updateMouse(PointerEvent event) {
		
		return GUIEvent.NONE;
	}

	@Override
	public void preDisplay(GLAutoDrawable drawable, Graphic g) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(0f, 0f, 0f, 1);

	}

	@Override
	public void display(GLAutoDrawable drawable) {

		if(reloading)
			return;
		
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glRotated(player.getAngle()+startAngle, 0, 1, 0);
				
		gl.glTranslated(camera.getX(),-camera.getY(),-camera.getZ());
				
		float position[] = { (float)-camera.getX(),(float)camera.getY(),(float)camera.getZ(), 1 };
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
		
		keyModel.draw(gl);
				
		gl.glEnable(GL2.GL_LIGHTING);
		
		//Draw Scene
		drawFloor(gl);

		for(TrapGL trap: trapModels) {
			trap.draw(gl);
		}		

		gl.glFlush();
		
		gl.glDisable(GL2.GL_LIGHTING);

	}

	@Override
	public void draw(Graphic g) {

		if(reloading) {
			return;
		}

		lifeBar.draw(g);

		if(debug) {
			g.drawShadow(40, 40, "px: "+Integer.toString(player.getX()));
			g.drawShadow(40, 60, "py: "+Integer.toString(player.getY()));
			g.drawShadow(40, 80, "pa: "+Double.toString(player.getAngle()));

			g.drawShadow(40, 100, "cx: "+Double.toString(camera.getX()));
			g.drawShadow(40, 120, "cy: "+Double.toString(camera.getY()));
			g.drawShadow(40, 140, "cz: "+Double.toString(camera.getZ()));

			g.setAlpha(50);

			drawScene(g);
		}

	}

	private void drawScene(Graphic g) {
		map.draw(g);

		player.draw(g);
	}

}