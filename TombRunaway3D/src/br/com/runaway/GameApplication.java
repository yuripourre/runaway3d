package br.com.runaway;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import br.com.etyllica.core.context.Application;
import br.com.etyllica.core.context.UpdateIntervalListener;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.linear.PointInt2D;
import br.com.etyllica.effects.light.LightSource;
import br.com.etyllica.effects.light.ShadowLayer;
import br.com.runaway.collision.CollisionHandler;
import br.com.runaway.item.Key;
import br.com.runaway.menu.Congratulations;
import br.com.runaway.menu.GameOver;
import br.com.runaway.player.TopViewPlayer;
import br.com.runaway.trap.SpikeFloor;
import br.com.runaway.trap.Trap;
import br.com.runaway.ui.LifeBar;
import br.com.tide.input.controller.Controller;
import br.com.tide.input.controller.EasyController;
import br.com.vite.editor.MapEditor;
import br.com.vite.export.MapExporter;
import br.com.vite.tile.Tile;
import br.com.vite.tile.layer.ImageTileObject;

public class GameApplication extends Application implements UpdateIntervalListener {

	public int currentLevel = 1;
	
	public static final int MAX_LEVEL = 10;

	public static final String PARAM_LEVEL = "level";

	//GUI Stuff
	private LifeBar lifeBar;

	private MapEditor map;

	private TopViewPlayer player;

	private Controller controller;

	private ShadowLayer shadowMap;

	private LightSource torch;

	private List<Trap> traps;

	private CollisionHandler handler;

	private Key key;

	public GameApplication(int w, int h, int currentLevel) {
		super(w, h);
		
		this.currentLevel = currentLevel;
	}

	@Override
	public void load() {

		loadMap();
		
		loading = 40;

		handler = new CollisionHandler(map.getMap());

		player = new TopViewPlayer(32, 32, handler);

		controller = new EasyController(player);

		loading = 50;
		
		shadowMap = new ShadowLayer(x, y, w, h);
		torch = new LightSource(player.getX(), player.getY(), 120);

		lifeBar = new LifeBar(player);

		loading = 100;
		
		updateAtFixedRate(30, this);
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
		
		loading = 20;
		loadObjects(map);
		
		loading = 30;		
	}
	
	private void loadObjects(MapEditor map) {
		
		traps = new ArrayList<Trap>();

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

	@Override
	public void timeUpdate(long now) {
		player.update(now);

		checkTrapCollisions(now);

		checkKeyCollision(now);

		int p1x = player.getX()+player.getLayer().getTileW()/2;
		int p1y = player.getY()+player.getLayer().getTileH()/2;

		torch.setCoordinates(p1x-torch.getW()/2, p1y-torch.getH()/2);

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

	@Override
	public void draw(Graphic g) {

		drawScene(g);

		lifeBar.draw(g);
	}

	private void drawScene(Graphic g) {
		map.draw(g);

		for(Trap trap : traps) {
			trap.draw(g);	
		}

		if(key!=null)
			key.draw(g);

		player.draw(g);

		shadowMap.drawLights(g, torch);
	}

	@Override
	public void updateKeyboard(KeyEvent event) {
		controller.handleEvent(event);
	}
	
}
