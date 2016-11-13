package view;

import java.awt.Point;

import model.Field;

/**
 * Converter converts the position of a field to the left upper corner pixel of that field
 * relative to the gameboard. wfld and hfld are the width and height of an unrotated field
 * in pixels.
 */
public class Converter {
	private Point pos2xypx[];
	private int hfld;
	private int wfld;

	/* generated at Sun 13 Nov 19:42:34 CET 2016 */
	public Converter(int wfld, int hfld) {
		pos2xypx = new Point[Field.Nfields];
		this.wfld = wfld;
		this.hfld = hfld;

		pos2xypx[ 0] = mkpt(10, 10);
		pos2xypx[ 1] = mkpt( 9, 10);
		pos2xypx[ 2] = mkpt( 8, 10);
		pos2xypx[ 3] = mkpt( 7, 10);
		pos2xypx[ 4] = mkpt( 6, 10);
		pos2xypx[ 5] = mkpt( 5, 10);
		pos2xypx[ 6] = mkpt( 4, 10);
		pos2xypx[ 7] = mkpt( 3, 10);
		pos2xypx[ 8] = mkpt( 2, 10);
		pos2xypx[ 9] = mkpt( 1, 10);
		pos2xypx[10] = mkpt( 0, 10);
		pos2xypx[11] = mkpt( 0,  9);
		pos2xypx[12] = mkpt( 0,  8);
		pos2xypx[13] = mkpt( 0,  7);
		pos2xypx[14] = mkpt( 0,  6);
		pos2xypx[15] = mkpt( 0,  5);
		pos2xypx[16] = mkpt( 0,  4);
		pos2xypx[17] = mkpt( 0,  3);
		pos2xypx[18] = mkpt( 0,  2);
		pos2xypx[19] = mkpt( 0,  1);
		pos2xypx[20] = mkpt( 0,  0);
		pos2xypx[21] = mkpt( 1,  0);
		pos2xypx[22] = mkpt( 2,  0);
		pos2xypx[23] = mkpt( 3,  0);
		pos2xypx[24] = mkpt( 4,  0);
		pos2xypx[25] = mkpt( 5,  0);
		pos2xypx[26] = mkpt( 6,  0);
		pos2xypx[27] = mkpt( 7,  0);
		pos2xypx[28] = mkpt( 8,  0);
		pos2xypx[29] = mkpt( 9,  0);
		pos2xypx[30] = mkpt(10,  0);
		pos2xypx[31] = mkpt(10,  1);
		pos2xypx[32] = mkpt(10,  2);
		pos2xypx[33] = mkpt(10,  3);
		pos2xypx[34] = mkpt(10,  4);
		pos2xypx[35] = mkpt(10,  5);
		pos2xypx[36] = mkpt(10,  6);
		pos2xypx[37] = mkpt(10,  7);
		pos2xypx[38] = mkpt(10,  8);
		pos2xypx[39] = mkpt(10,  9);
	}

	/**
	 * cornerRelPx takes a field position and returns the upper left corner pixel of the field
	 * relative to the gameboard origin. The Point must then be translated to absolute coordinates
	 * and rotated.
	 */
	public Point cornerRelPx(int pos) {
		pos %= Field.Nfields;
		Point pt = pos2xypx[pos];
		return new Point(pt.x, pt.y);
	}

	public Point middleRelPx(int pos) {
		Point p = cornerRelPx(pos);

		if(isPortrait(pos))
			return new Point(p.x + wfld/2, p.y + hfld/2);
		else if(isLandscape(pos))
			return new Point(p.x + hfld/2, p.y + wfld/2);
		else                                                    /* corners */
			return new Point(p.x + hfld/2, p.y + hfld/2);
	}

	/**
	 * isPortrait: returns true if the field in question is in 'portrait' orientation,
	 * that is, has more height than width, as is the case at the top and at the bottom.
	 */
	public boolean isPortrait(int pos) {
		return pos > 0 && pos < 10 || pos > 20 && pos < 30;
	}

	/**
	 * isLandscape: returns true if the field in question is in 'landscale' orientation,
	 * that is, has more width than height, as is the case on the left and the right.
	 */
	public boolean isLandscape(int pos) {
		return pos > 10 && pos < 20 || pos > 30 && pos <= 39;
	}

	/**
	 * mkpt takes a field in the left-handed coordinate system where (0|0) is at free parking
	 * and returns the pixel of the upper left corner of the field relative to the gameboard origin. 
	 */
	private Point mkpt(int fx, int fy) {
		int pxx, pxy; /* pixel x and y */

		if(fx == 0)
			pxx = 0;
		else
			pxx = hfld + (fx-1) * wfld;

		if(fy == 0)
			pxy = 0;
		else
			pxy = hfld + (fy-1) * wfld;

		return new Point(pxx, pxy); 
	}
}
