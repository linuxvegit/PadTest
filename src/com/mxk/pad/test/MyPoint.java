package com.mxk.pad.test;

public class MyPoint {
	int row;
	int col;

	public MyPoint(int row, int col) {
		// TODO Auto-generated constructor stub
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		long bits = java.lang.Double.doubleToLongBits(getRow());
		bits ^= java.lang.Double.doubleToLongBits(getCol()) * 31;
		return (((int) bits) ^ ((int) (bits >> 32)));
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		if (!(arg0 instanceof MyPoint))
			return super.equals(arg0);
		MyPoint point = (MyPoint) arg0;
		return (point.getRow() == row) && (point.getCol() == col);
	}

}
