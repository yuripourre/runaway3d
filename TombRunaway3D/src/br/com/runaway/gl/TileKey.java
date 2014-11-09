package br.com.runaway.gl;

public class TileKey {

	private int xImage;
	
	private int yImage;
	
	public TileKey(int xImage, int yImage) {
		super();
		this.xImage = xImage;
		this.yImage = yImage;
	}

	public int getxImage() {
		return xImage;
	}

	public int getyImage() {
		return yImage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + xImage;
		result = prime * result + yImage;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TileKey other = (TileKey) obj;
		if (xImage != other.xImage)
			return false;
		if (yImage != other.yImage)
			return false;
		return true;
	}
	
}
