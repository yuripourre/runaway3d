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
import br.com.etyllica.core.event.PointerState;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.etyllica.layer.BufferedLayer;
import br.com.runaway.application.CommonApplicationGL;
import br.com.runaway.collision.CollisionHandler;
import br.com.runaway.player.TopViewPlayer;
import br.com.runaway.trap.Trap;
import br.com.runaway.ui.LifeBar;
import br.com.tide.input.controller.Controller;
import br.com.tide.input.controller.EasyController;
import br.com.vite.export.MapExporter;

public class GameApplicationGL extends CommonApplicationGL {

	public int currentLevel = 1;

	public static final int MAX_LEVEL = 10;

	public static final String PARAM_LEVEL = "level";

	protected float mx = 0;
	protected float my = 0;

	protected boolean click = false;

	private final double startAngle = 180;
	
	private double angleX = 0;

	private double angleY = 0;

	private double angleZ = 0;

	private LifeBar lifeBar;

	private TopViewPlayer player;

	private CameraGL camera;

	private CollisionHandler handler;

	private Controller controller;

	public GameApplicationGL(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {

		loadMap();

		handler = new CollisionHandler(map.getMap());

		player = new TopViewPlayer(34, 32, handler);

		controller = new EasyController(player);
		
		camera = new CameraGL(player.getCenter().getX(), 16, player.getCenter().getY());

		lifeBar = new LifeBar(player);

		loading = 100;
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

		loading = 20;
		loadTiles(map);
		loadObjects(map);

		loading = 30;
	}

	@Override
	public void init(GLAutoDrawable drawable) {

		layer = new BufferedLayer("tiles/tileset.png");
		layer.cropImage(32*10, 0, 32, 32);

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

		angleY = player.getAngle();
		
		//Inverted Orientation
		camera.setX(player.getCenter().getY());
		camera.setZ(player.getCenter().getX());

		handler.updateCollision(player);

	}

	private double camSpeed = 0.5; 
	
	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {

		controller.handleEvent(event);

		if(event.isKeyDown(KeyEvent.TSK_A)) {
			offsetX+=camSpeed;
		}

		if(event.isKeyDown(KeyEvent.TSK_D)) {
			offsetX-=camSpeed;
		}
		
		if(event.isKeyDown(KeyEvent.TSK_W)) {
			offsetZ+=camSpeed;
		}
		
		if(event.isKeyDown(KeyEvent.TSK_S)) {
			offsetZ-=camSpeed;
		}

		if(event.isKeyDown(KeyEvent.TSK_N)) {
			offsetY+=camSpeed;
		}
		
		if(event.isKeyDown(KeyEvent.TSK_M)) {
			offsetY-=camSpeed;
		}

		if(event.isKeyDown(KeyEvent.TSK_VIRGULA)) {

			angleZ += 5;

		} else if(event.isKeyDown(KeyEvent.TSK_PONTO)) {

			angleZ -= 5;

		}

		return GUIEvent.NONE;
	}

	private float sensitivity = 10;
	private float lastMouseY = h;
	private float lastMouseX = w;
	
	private double offsetX = 0;
	private double offsetY = 0;
	private double offsetZ = 0;

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

		if(event.getState() == PointerState.MOVE) {

			/*angleX += (my-lastMouseY)/sensitivity;

			lastMouseY = my;
			
			angleY += (mx-lastMouseX)/sensitivity;

			lastMouseX = mx;*/

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
		gl.glTranslated((camera.getX()+offsetX),-(camera.getY()+offsetY),-(camera.getZ()+offsetZ));
		
		//Draw Scene
		drawFloor(gl);


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
		
		g.drawShadow(100, 100, "cx+ox: "+Double.toString(camera.getX()+offsetX));
		g.drawShadow(100, 120, "cy+oy: "+Double.toString(camera.getY()+offsetY));
		g.drawShadow(100, 140, "cz+oz: "+Double.toString(camera.getZ()+offsetZ));
		
		g.setAlpha(60);
		
		drawScene(g);
		
	}
	
	private void drawScene(Graphic g) {
		map.draw(g);

		player.draw(g);
	}

}