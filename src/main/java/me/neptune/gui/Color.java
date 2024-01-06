/**
 * A class to represent Colors and their respective functions.
 */

package me.neptune.gui;

import org.apache.commons.lang3.StringUtils;
import org.joml.Vector3f;

public class Color {

	public int r;
	public int g;
	public int b;

	public float hue;
	public float saturation;
	public float luminance;

	/**
	 * Color Constructor using RGB color space.
	 * 
	 * @param r Red component of a Color.
	 * @param g Green component of a Color.
	 * @param b Blue component of a Color.
	 */
	public Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 * Color Constructor using HSV color space.
	 * 
	 * @param hue
	 * @param saturation
	 * @param luminance
	 */
	public Color(float hue, float saturation, float luminance) {
		this.setHSV(hue, saturation, luminance);
	}

	/**
	 * Sets the RGB and HSV fields using inputs from an HSV color space.
	 * @param hue        The hue of the HSV color space.
	 * @param saturation The saturation of the HSV color space.
	 * @param luminance
	 */
	public void setHSV(float hue, float saturation, float luminance) {
		this.hue = hue;
		this.saturation = saturation;
		this.luminance = luminance;
		Color vec = hsv2rgb(hue, saturation, luminance);
		if (vec != null) {
			this.r = vec.r;
			this.g = vec.g;
			this.b = vec.b;
		}
	}

	/**
	 * Sets the RGB fields using inputs from an RGB color space.
	 * 
	 * @param r Red component of a Color.
	 * @param g Green component of a Color.
	 * @param b Blue component of a Color.
	 */
	public void setRGB(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 * Returns the Color as a string in format RRRGGGBBB.
	 * 
	 * @return
	 */
	public String getColorAsString() {
		String rs = Integer.toString((int) (r));
		String gs = Integer.toString((int) (g));
		String bs = Integer.toString((int) (b));
		return rs + gs + bs;
	}

	/**
	 * Returns the color as an Integer for Minecraft Rendering.
	 * 
	 * @return The color as an integer.
	 */
	public int getColorAsInt() {
		// Perform shifts and Bitwise AND to get color value in integer format.
		int R = ((this.r) << 16) & 0x00FF0000;
		int G = ((this.g) << 8) & 0x0000FF00;
		int B = (this.b) & 0x000000FF;

		// Return the color as a combination of these values.
		return 0xFF000000 | R | G | B;
	}

	/**
	 * Returns the color as a string in Hex format.
	 * 
	 * @return The color represented as Hex.
	 */
	public String getColorAsHex() {
		return String.format("#%06X", this.getColorAsInt());
	}

	/**
	 * Gets the Red component as a float.
	 * 
	 * @return Red component as a float.
	 */
	public float getRedFloat() {
		return ((float) this.r) / 255.0f;
	}

	/**
	 * Gets the Green component as a float.
	 * 
	 * @return Green component as a float.
	 */
	public float getGreenFloat() {
		return ((float) this.g) / 255.0f;
	}

	/**
	 * Gets the Blue component as a float.
	 * 
	 * @return Blue component as a float.
	 */
	public float getBlueFloat() {
		return ((float) this.b) / 255.0f;
	}

	/**
	 * Converts RGB codes to a String.
	 * 
	 * @param r Red component.
	 * @param g Green component.
	 * @param b Blue component.
	 * @return Color as a String.
	 */
	public static String rgbToString(int r, int g, int b) {
		String rs = Integer.toString((int) (r));
		String gs = Integer.toString((int) (g));
		String bs = Integer.toString((int) (b));
		return rs + gs + bs;
	}

	/**
	 * Converts RGB codes to an Integer.
	 * 
	 * @param r Red component.
	 * @param g Green component.
	 * @param b Blue component.
	 * @return Color as an Integer.
	 */
	public static int rgbToInt(int r, int g, int b) {
		String rs = Integer.toString((int) (r));
		String gs = Integer.toString((int) (g));
		String bs = Integer.toString((int) (b));
		return Integer.parseInt(rs + gs + bs);
	}

	/**
	 * Converts RGB codes to Hex.
	 * 
	 * @param r Red component.
	 * @param g Green component.
	 * @param b Blue component.
	 * @return Color as Hex.
	 */
	public static int convertRGBToHex(int r, int g, int b) {
		String strr = StringUtils.leftPad(Integer.toHexString(r), 2, '0');
		String strg = StringUtils.leftPad(Integer.toHexString(g), 2, '0');
		String strb = StringUtils.leftPad(Integer.toHexString(b), 2, '0');
		String string = strr + strg + strb;
		return Integer.parseInt(string, 16);
	}

	/**
	 * Converts Hex codes to RGB.
	 * 
	 * @param hex Color as Hex.
	 * @return Color from Hex Code.
	 */
	public static Color convertHextoRGB(String hex) {
		String RString = hex.charAt(1) + "" + hex.charAt(2);
		String GString = hex.charAt(3) + "" + hex.charAt(4);
		String BString = hex.charAt(5) + "" + hex.charAt(6);

		float r = Integer.valueOf(RString, 16);
		float g = Integer.valueOf(GString, 16);
		float b = Integer.valueOf(BString, 16);
		return new Color(r, g, b);
	}

	/**
	 * Converts HSV into RGB color space.
	 * 
	 * @param hue        The hue of the HSV color space.
	 * @param saturation The saturation of the HSV color space.
	 * @param luminance  The luminance of the HSV color space.
	 * @return The color represented by HSV.
	 */
	public static Color hsv2rgb(float hue, float saturation, float luminance) {
		// Get the side that the colour is contained in.
		float h = (hue / 60);
		float chroma = luminance * saturation;
		float x = chroma * (1 - Math.abs((h % 2) - 1));

		// Depending on the side, set the Chroma component to the correct Color.
		Vector3f rgbVec;
		if (h >= 0 && h <= 1) {
			rgbVec = new Vector3f(chroma, x, 0);
		} else if (h >= 1 && h <= 2) {
			rgbVec = new Vector3f(x, chroma, 0);
		} else if (h >= 2 && h <= 3) {
			rgbVec = new Vector3f(0, chroma, x);
		} else if (h >= 3 && h <= 4) {
			rgbVec = new Vector3f(0, x, chroma);
		} else if (h >= 4 && h <= 5) {
			rgbVec = new Vector3f(x, 0, chroma);
		} else if (h >= 5 && h <= 6) {
			rgbVec = new Vector3f(chroma, 0, x);
		} else {
			rgbVec = null;
		}

		// If the Color does exist, add luminance and convert to RGB.
		if (rgbVec != null) {
			float m = luminance - chroma;
			return new Color((int) (255.0f * (rgbVec.x + m)), (int) (255.0f * (rgbVec.y + m)),
					(int) (255.0f * (rgbVec.z + m)));
		}
		return null;
	}

}
