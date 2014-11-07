package br.com.runaway;

import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
import br.com.luvia.core.ApplicationGL;
import br.com.luvia.loader.TextureLoader;
import br.com.runaway.gl.TileGL;
import br.com.runaway.item.Key;
import br.com.runaway.player.TopViewPlayer;
import br.com.runaway.trap.SpikeFloor;
import br.com.runaway.trap.Trap;
import br.com.runaway.ui.LifeBar;
import br.com.tide.input.controller.EasyController;
import br.com.vite.editor.MapEditor;
import br.com.vite.export.MapExporter;
import br.com.vite.tile.Tile;
import br.com.vite.tile.collision.CollisionType;
import br.com.vite.tile.layer.ImageTileFloor;
import br.com.vite.tile.layer.ImageTileObject;

import com.jogamp.opengl.util.texture.Texture;

public class GameApplicationGL extends ApplicationGL {

	public int currentLevel = 1;

	public static final int MAX_LEVEL = 10;

	public static final String PARAM_LEVEL = "level";

	private Texture tile;

	protected float mx = 0;
	protected float my = 0;

	protected boolean click = false;

	private double angleX = 0;

	private double angleY = 0;

	private double angleZ = 0;

	private BufferedLayer layer;

	private LifeBar lifeBar;

	private MapEditor map;

	private TopViewPlayer player;

	private CameraGL camera;

	private List<Trap> traps;

	private Key key;

	private TileGL[][] tiles;

	public GameApplicationGL(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {

		camera = new CameraGL(0,80,1);

		player = new TopViewPlayer(32, 32, null);

		lifeBar = new LifeBar(player);

		loadMap();

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

	private void loadTiles(MapEditor map) {

		Tile[][] mapTiles = map.getTiles();

		tiles = new TileGL[map.getLines()][map.getColumns()];

		for(int j = 0; j < map.getLines(); j++) {
			for(int i = 0; i < map.getColumns(); i++) {
				createTile(j, i, mapTiles[j][i]);		
			}
		}

	}

	private void loadObjects(MapEditor map) {

		Tile[][] tiles = map.getTiles();

		for(int j = 0; j < map.getLines(); j++) {

			for(int i = 0; i < map.getColumns(); i++) {

				ImageTileObject obj = tiles[j][i].getObjectLayer();

				if(obj != null) {

					if("SPIKE".equals(obj.getLabel())) {
						traps.add(new SpikeFloor(i*map.getTileWidth(), j*map.getTileHeight()));
						tiles[j][i].setObjectLayer(null);
					}

					if("KEY".equals(obj.getLabel())) {
						key = new Key(i*map.getTileWidth(), j*map.getTileHeight());
						tiles[j][i].setObjectLayer(null);
					}
				}
			}
		}
	}

	private void createTile(int j, int i, Tile tile) {

		ImageTileFloor floor = tile.getLayer();

		layer.cropImage(floor.getX(), floor.getY(), 32, 32);

		Texture texture = TextureLoader.getInstance().loadTexture(layer.getBuffer());

		tiles[j][i] = new TileGL(j, i, texture);

	}

	@Override
	public void init(GLAutoDrawable drawable) {

		layer = new BufferedLayer("tiles/tileset.png");
		layer.cropImage(32*10, 0, 32, 32);

		tile = TextureLoader.getInstance().loadTexture(layer.getBuffer());

		GL2 gl = drawable.getGL().getGL2();		

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthMask(true);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glDepthRange(0.0f, 1.0f);
	}

	protected void lookCamera(GL2 gl) {
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		double targetx = 0;
		double targety = 0;
		double targetz = 0;

		glu.gluLookAt( camera.getX(), camera.getY(), camera.getZ(), targetx, targety, targetz, 0, 1, 0 );

	}

	protected void drawFloor(GL2 gl) {

		gl.glColor3d(1,1,1);

		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		drawGrid(gl,-1,0);

	}

	private void drawGrid(GL2 gl, double x, double y) {

		double tileSize = 32;

		for(int j = 0; j < map.getLines(); j++) {
			for(int i = 0; i < map.getLines(); i++) {
				TileGL tile = tiles[j][i];

				if(map.getTiles()[j][i].getCollision() == CollisionType.FREE) {
					drawTile(gl, x+j, y+i, tileSize, tile.getTexture());
				} else {

					boolean rightFace = false;

					if(i<map.getColumns()-1) {
						rightFace = map.getTiles()[j][i+1].getCollision() == CollisionType.FREE;
					}

					drawBlock(gl, x+j, y+i, tileSize, tile.getTexture(), rightFace);
				}
			}
		}		

	}

	private void drawBlock(GL2 gl, double x, double y, double tileSize, Texture texture, boolean rightFace) {

		texture.enable(gl);
		texture.bind(gl);

		gl.glBegin(GL2.GL_QUADS);

		//Lower Face
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, 0, y*tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize+tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize+tileSize);

		//Back Face
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, tileSize, y*tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, tileSize, y*tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize);

		//Left Face
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, tileSize, y*tileSize+tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize, tileSize, y*tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize+tileSize);

		//Right Face
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize+tileSize, tileSize, y*tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, tileSize, y*tileSize+tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize+tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize);

		//Front Face
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, tileSize, y*tileSize+tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, tileSize, y*tileSize+tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize+tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize+tileSize);

		//Upper Face
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, tileSize, y*tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, tileSize, y*tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, tileSize, y*tileSize+tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, tileSize, y*tileSize+tileSize);

		gl.glEnd();		

		texture.disable(gl);
	}

	private void drawTile(GL2 gl, double x, double y, double tileSize, Texture texture) {

		texture.enable(gl);
		texture.bind(gl);

		gl.glBegin(GL2.GL_QUADS);

		//(0,0)
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, 0, y*tileSize);

		//(1,0)
		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize);

		//(1,1)
		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize+tileSize);

		//(0,1)
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize+tileSize);

		gl.glEnd();

		texture.disable(gl);
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

	private boolean upArrow = false;
	private boolean downArrow = false;
	private boolean leftArrow = false;
	private boolean rightArrow = false;

	@Override
	public void update(long now) {

		if(upArrow) {
			angleX += 1;
		} else if(downArrow) {
			angleX -= 1;
		}

		if(leftArrow) {
			angleY += 1;
		} else if(rightArrow) {
			angleY -= 1;
		}

	}

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {

		if(event.isKeyDown(KeyEvent.TSK_UP_ARROW)) {

			upArrow = true;

		} else if (event.isKeyUp(KeyEvent.TSK_UP_ARROW)) {

			upArrow = false;
		}

		if(event.isKeyDown(KeyEvent.TSK_DOWN_ARROW)) {

			downArrow = true;

		} else if(event.isKeyUp(KeyEvent.TSK_DOWN_ARROW)) {

			downArrow = false;
		}

		if(event.isKeyDown(KeyEvent.TSK_LEFT_ARROW)) {

			leftArrow = true;

		} else if(event.isKeyUp(KeyEvent.TSK_LEFT_ARROW)) {

			leftArrow = false;
		}

		if(event.isKeyDown(KeyEvent.TSK_RIGHT_ARROW)) {

			rightArrow = true;

		} else if (event.isKeyUp(KeyEvent.TSK_RIGHT_ARROW)) {

			rightArrow = false;
		}

		if(event.isKeyDown(KeyEvent.TSK_VIRGULA)) {

			angleZ += 5;

		} else if(event.isKeyDown(KeyEvent.TSK_PONTO)) {

			angleZ -= 5;

		}

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

		//Transform by Camera
		lookCamera(drawable.getGL().getGL2());

		gl.glRotated(angleX, 1, 0, 0);
		gl.glRotated(angleY, 0, 1, 0);
		gl.glRotated(angleZ, 0, 0, 1);

		//Draw Scene
		drawFloor(gl);


		gl.glFlush();

	}


	@Override
	public void draw(Graphic g) {

		lifeBar.draw(g);

	}

}