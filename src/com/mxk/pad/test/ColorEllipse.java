package com.mxk.pad.test;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

public class ColorEllipse extends Ellipse2D.Double {
	private final Color color;

	public ColorEllipse(double x, double y, double w, double h, Color color) {
		// TODO Auto-generated constructor stub
		super(x, y, w, h);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + color.toString();
	}
}
