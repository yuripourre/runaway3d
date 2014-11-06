package br.com.runaway.editor.item;

import br.com.vite.tile.layer.ImageTileObject;

public class KeyTileObject extends ImageTileObject {

	public KeyTileObject() {
		super("item/key.png");
		
		offsetX = 18;
		offsetY = -10;
		
		this.label = "KEY";
	}

}
