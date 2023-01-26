package fr.wonder.display;

public class Color {

	private int r, g, b, a;

	public int getRed() { return r; }
	public int getGreen() { return g; }
	public int getBlue() { return b; }
	public int getAlpha() { return a; }
	
	public Color(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public static final Color white    = new Color(255, 255, 255);
	public static final Color orange   = new Color(255, 255, 100);
	public static final Color darkGray = new Color(50, 50, 50);
	public static final Color cyan     = new Color(0, 255, 255);
	public static final Color red      = new Color(255, 0, 0);
	public static final Color green    = new Color(0, 255, 0);
	public static final Color blue     = new Color(0, 0, 255);
	
	
}
