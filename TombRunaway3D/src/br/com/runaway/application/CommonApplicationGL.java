package br.com.runaway.application;

import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import br.com.abby.util.CameraGL;
import br.com.etyllica.layer.BufferedLayer;
import br.com.luvia.core.context.ApplicationGL;
import br.com.luvia.loader.TextureLoader;
import br.com.runaway.gl.KeyGL;
import br.com.runaway.gl.SpikeTrapGL;
import br.com.runaway.gl.TileGL;
import br.com.runaway.gl.TileKey;
import br.com.runaway.gl.TrapGL;
import br.com.runaway.item.Key;
import br.com.runaway.trap.SpikeFloor;
import br.com.runaway.trap.Trap;
import br.com.vite.editor.MapEditor;
import br.com.vite.tile.Tile;
import br.com.vite.tile.collision.CollisionType;
import br.com.vite.tile.layer.ImageTileFloor;
import br.com.vite.tile.layer.ImageTileObject;

import com.jogamp.opengl.util.texture.Texture;

public abstract class CommonApplicationGL extends ApplicationGL {

	protected Key key;
	protected KeyGL keyModel;

	protected MapEditor map;

	protected TileGL[][] tiles;

	protected List<Trap> traps;
	protected List<TrapGL> trapModels;

	protected BufferedLayer layer;

	private Map<TileKey, Texture> textureMap = new HashMap<TileKey, Texture>();
	
	protected double tileSize = 32;

	public CommonApplicationGL(int w, int h) {
		super(w, h);
	}

	public void load() {

	}

	protected void loadTiles(MapEditor map) {

		Tile[][] mapTiles = map.getTiles();

		tiles = new TileGL[map.getLines()][map.getColumns()];

		loadAllTextures();

		for(int j = 0; j < map.getLines(); j++) {
			for(int i = 0; i < map.getColumns(); i++) {
				createTile(j, i, mapTiles[j][i]);		
			}
		}

	}

	private void createTile(int j, int i, Tile tile) {

		ImageTileFloor floor = tile.getLayer();

		Texture texture = getOrLoad(floor);

		tiles[j][i] = new TileGL(j, i, texture);

	}

	private Texture getOrLoad(ImageTileFloor floor) {

		TileKey key = new TileKey(floor.getX(), floor.getY());

		Texture texture = textureMap.get(key);

		return texture;

	}

	private void loadAllTextures() {

		if(!textureMap.isEmpty())
			return;

		for(int j = 0; j<10; j++) {

			for(int i = 0; i<18; i++) {

				TileKey key = new TileKey(i*32, j*32);

				layer.cropImage(i*32, j*32, 32, 32);

				Texture texture = TextureLoader.getInstance().loadTexture(layer.getBuffer());
				textureMap.put(key, texture);
			}

		}

	}

	protected void loadObjects(MapEditor map) {

		Tile[][] tiles = map.getTiles();

		for(int j = 0; j < map.getLines(); j++) {

			for(int i = 0; i < map.getColumns(); i++) {

				ImageTileObject obj = tiles[j][i].getObjectLayer();

				if(obj != null) {

					if("SPIKE".equals(obj.getLabel())) {

						Trap spike = new SpikeFloor(i*map.getTileWidth(), j*map.getTileHeight());

						traps.add(spike);

						int x = j*map.getTileWidth()+map.getTileWidth()/2;
						int y = i*map.getTileHeight()+map.getTileHeight()/2;

						SpikeTrapGL spikeGL = new SpikeTrapGL(x, y, spike);						
						trapModels.add(spikeGL);

						tiles[j][i].setObjectLayer(null);
					}

					if("KEY".equals(obj.getLabel())) {
						key = new Key(i*map.getTileWidth(), j*map.getTileHeight());
						keyModel.getMesh().setCoordinates(-(j*map.getTileWidth()+map.getTileWidth()/2), 25, i*map.getTileHeight()+map.getTileHeight()/2);
						tiles[j][i].setObjectLayer(null);
					}
				}
			}
		}
	}

	/*protected void lookCamera(GL2 gl, CameraGL camera) {
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		double targetx = camera.getX()+1;
		double targety = camera.getY();
		double targetz = camera.getZ();

		glu.gluLookAt( camera.getX(), camera.getY(), camera.getZ(), targetx, targety, targetz, 0, 1, 0 );

	}*/

	protected void drawFloor(GL2 gl) {

		gl.glColor3d(1,1,1);

		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		drawGrid(gl,-1,0);

	}

	private void drawGrid(GL2 gl, double x, double y) {

		for(int j = 0; j < map.getLines(); j++) {
			for(int i = 0; i < map.getColumns(); i++) {
				TileGL tile = tiles[j][i];

				if(map.getTiles()[j][i].getCollision() == CollisionType.FREE) {
					drawTile(gl, x-j, y+i, tileSize, tile.getTexture());
				} else {

					boolean rightFace = false;

					if(i<map.getColumns()-1) {
						rightFace = map.getTiles()[j][i+1].getCollision() == CollisionType.FREE;
					}

					drawBlock(gl, x-j, y+i, tileSize, tile.getTexture(), rightFace);
				}
			}
		}		

	}

	private void drawBlock(GL2 gl, double x, double y, double tileSize, Texture texture, boolean rightFace) {

		texture.enable(gl);
		texture.bind(gl);

		double height = tileSize*2;
		
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
		gl.glVertex3d(x*tileSize, height, y*tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, height, y*tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize);

		//Left Face
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, height, y*tileSize+tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize, height, y*tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize+tileSize);

		//Right Face
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize+tileSize, height, y*tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, height, y*tileSize+tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize+tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize);

		//Front Face
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, height, y*tileSize+tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, height, y*tileSize+tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize+tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize+tileSize);

		//Upper Face
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, height, y*tileSize);

		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, height, y*tileSize);

		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, height, y*tileSize+tileSize);

		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, height, y*tileSize+tileSize);

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

		//(0,0)
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, tileSize, y*tileSize);

		//(1,0)
		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, tileSize, y*tileSize);

		//(1,1)
		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, tileSize, y*tileSize+tileSize);

		//(0,1)
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, tileSize, y*tileSize+tileSize);

		gl.glEnd();

		texture.disable(gl);
	}

}
