package br.com.runaway.editor;

import br.com.vite.map.MapType;
import br.com.vite.tile.collision.CollisionType;
import br.com.vite.tile.set.TileSet;

public class EgyptianTileSet extends TileSet {

	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;

	public EgyptianTileSet() {
		super(18, 10, TILE_WIDTH, TILE_HEIGHT, MapType.ORTHOGONAL, "tiles/tileset.png");

		//Add Collision to TileSet
		for(int j=0; j < 10; j++) {
			for(int i=10; i < 18; i++) {
				collision[j][i] = CollisionType.BLOCK;
			}
		}

	}

}
