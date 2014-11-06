package br.com.runaway.editor.item;

import br.com.vite.tile.layer.ImageTileObject;

public class SpikeTileObject extends ImageTileObject {

	public SpikeTileObject() {
		super("item/spike.png");
		
		offsetX = 16;
		offsetY = -2;
		
		this.label = "SPIKE";
	}

}
