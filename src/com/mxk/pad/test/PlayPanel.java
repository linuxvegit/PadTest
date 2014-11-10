package com.mxk.pad.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.plaf.SliderUI;

public class PlayPanel extends JPanel {
	private static final int ROWS = 5;
	private static final int COLS = 6;

	private static final Color[] COLORS = { Color.RED, Color.BLUE, Color.GREEN,
			Color.YELLOW, Color.MAGENTA, Color.PINK };

	private int width;
	private int height;
	private int beadWidth;
	private boolean initialized = false;
	private boolean isSwap = false;
	private boolean canMove = true;

	private ColorEllipse[][] ellipse2ds = new ColorEllipse[ROWS][COLS];
	private ColorEllipse current = null;
	private int emptyRow;
	private int emptyCol;

	private Set<MyPoint> eliminateBeads;
	private List<ArrayList<MyPoint>> eliminateList;

	public PlayPanel() {
		// TODO Auto-generated constructor stub
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseMotionHandler());
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		if (!initialized) {
			freshSize();
			while (checkAndEliminate())
				fallBeads();
			initialized = true;
		}
		Graphics2D graphics2d = (Graphics2D) g;
		drawBeads(graphics2d);
	}

	private void freshSize() {
		width = getWidth();
		height = getHeight();
		beadWidth = Math.min(width / COLS, height / ROWS);
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				ellipse2ds[i][j] = new ColorEllipse(j * beadWidth, i
						* beadWidth, beadWidth, beadWidth,
						COLORS[(int) (Math.random() * COLORS.length)]);
			}
		}
	}

	private void drawBeads(Graphics2D graphics2d) {
		for (ColorEllipse[] ellipses : ellipse2ds) {
			for (ColorEllipse ellipse : ellipses) {
				if (ellipse != null) {
					graphics2d.setPaint(ellipse.getColor());
					graphics2d.fill(ellipse);
					graphics2d.draw(ellipse);
				}
			}
		}
		if (current != null) {
			graphics2d.setPaint(current.getColor());
			graphics2d.fill(current);
			graphics2d.draw(current);
		}
	}

	private ColorEllipse findAndTake(Point2D point) {
		int col = correctCol(point);
		int row = correctRow(point);
		if (ellipse2ds[row][col] != null
				&& ellipse2ds[row][col].contains(point)) {
			ColorEllipse ellipse = ellipse2ds[row][col];
			ellipse2ds[row][col] = null;
			emptyRow = row;
			emptyCol = col;
			ellipse.setFrame(point.getX() - beadWidth / 2, point.getY()
					- beadWidth / 2, beadWidth, beadWidth);
			repaint();
			return ellipse;
		}
		return null;
	}

	private void drop(Point2D point) {
		if (current != null) {
			int col = correctCol(point);
			int row = correctRow(point);
			ellipse2ds[emptyRow][emptyCol] = current;
			current.setFrame(emptyCol * beadWidth, emptyRow * beadWidth,
					beadWidth, beadWidth);
			current = null;
		}
		repaint();
	}

	public boolean checkAndEliminate() {
		if (eliminateList == null)
			(eliminateList = new ArrayList<ArrayList<MyPoint>>()).clear();
		else
			eliminateList.clear();
		if (eliminateBeads == null)
			(eliminateBeads = new HashSet<MyPoint>()).clear();
		else
			eliminateBeads.clear();
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 1; col < COLS - 1; ++col) {
				col = checkRowBead(row, col);
			}
		}
		for (int col = 0; col < COLS; ++col) {
			for (int row = 1; row < ROWS - 1; ++row) {
				row = checkColBead(row, col);
			}
		}
		System.out.println("" + eliminateBeads.size());
		if (eliminateBeads.isEmpty()) {
			canMove = true;
			return false;
		}
		buildList();
		eliminate();
		return true;
	}

	private void eliminate() {
		for (int i = 0; i < eliminateList.size(); ++i) {
			ArrayList<MyPoint> list = eliminateList.get(i);
			System.out.println("-list->>" + list.size());
			for (int j = 0; j < list.size(); ++j) {
				MyPoint point = list.get(j);
				ellipse2ds[point.getRow()][point.getCol()] = null;
			}
			repaint();
		}
		eliminateBeads.clear();
	}

	private void fallBeads() {
		System.out.println("run fall");
		for (int j = 0; j < COLS; ++j) {
			boolean noOne = false;
			for (int i = ROWS - 1; i >= 0; --i) {
				if (ellipse2ds[i][j] == null) {
					if (noOne)
						ellipse2ds[i][j] = new ColorEllipse(j * beadWidth, i
								* beadWidth, beadWidth, beadWidth,
								COLORS[(int) (Math.random() * COLORS.length)]);
					else {
						noOne = true;
						for (int k = i - 1; k >= 0; --k) {
							if (ellipse2ds[k][j] != null) {
								noOne = false;
								beadMotion(k, j, i, j);
								break;
							}
						}
						if (noOne)
							ellipse2ds[i][j] = new ColorEllipse(
									j * beadWidth,
									i * beadWidth,
									beadWidth,
									beadWidth,
									COLORS[(int) (Math.random() * COLORS.length)]);
					}
				}
			}
		}
		repaint();
		canMove = true;
	}

	private int checkRowBead(int row, int col) {
		canMove = false;
		int result = col;
		if (!getColor(row, col).equals(getColor(row, col - 1)))
			return result;
		if (!getColor(row, col).equals(getColor(row, col + 1)))
			return result;

		for (int i = -1; i < 2; ++i) {
			if (!eliminateBeads.contains(new MyPoint(row, col + i)))
				eliminateBeads.add(new MyPoint(row, col + i));
		}

		result = col + 1;
		for (int i = col + 2; i < COLS; ++i) {
			if (getColor(row, col).equals(getColor(row, i))) {
				if (!eliminateBeads.contains(new MyPoint(row, i)))
					eliminateBeads.add(new MyPoint(row, i));
				++result;
			} else
				break;
		}

		return result;
	}

	private int checkColBead(int row, int col) {
		int result = row;
		if (!getColor(row, col).equals(getColor(row - 1, col)))
			return result;
		if (!getColor(row, col).equals(getColor(row + 1, col)))
			return result;

		for (int i = -1; i < 2; ++i) {
			if (!eliminateBeads.contains(new MyPoint(row + i, col)))
				eliminateBeads.add(new MyPoint(row + i, col));
		}

		result = row + 1;
		for (int i = row + 2; i < ROWS; ++i) {
			if (getColor(row, col).equals(getColor(i, col))) {
				if (!eliminateBeads.contains(new MyPoint(i, col)))
					eliminateBeads.add(new MyPoint(i, col));
				++result;
			} else
				break;
		}

		return result;
	}

	private void buildList() {
		System.out.println(eliminateBeads.toString());
		Queue<MyPoint> queue = new LinkedList<MyPoint>();
		while (!eliminateBeads.isEmpty()) {
			queue.clear();
			// queue.offer(eliminateBeads.get(0));
			// ArrayList<MyPoint> list = new ArrayList<MyPoint>();
			// list.add(eliminateBeads.get(0));
			// eliminateBeads.remove(0);
			Iterator<MyPoint> iter = eliminateBeads.iterator();
			MyPoint aPoint = iter.next();
			queue.add(aPoint);
			ArrayList<MyPoint> list = new ArrayList<MyPoint>();
			list.add(aPoint);
			eliminateBeads.remove(aPoint);
			while (!queue.isEmpty()) {
				MyPoint center = queue.poll();
				// UP
				if ((center.getRow() - 1 >= 0)
						&& (eliminateBeads.contains(new MyPoint(
								center.getRow() - 1, center.getCol())))
						&& getColor(center.getRow(), center.getCol()).equals(
								getColor(center.getRow() - 1, center.getCol()))) {
					MyPoint point = new MyPoint(center.getRow() - 1,
							center.getCol());
					list.add(point);
					queue.offer(point);
					eliminateBeads.remove(point);
				}
				// DOWN
				if ((center.getRow() + 1 < ROWS)
						&& (eliminateBeads.contains(new MyPoint(
								center.getRow() + 1, center.getCol())))
						&& getColor(center.getRow(), center.getCol()).equals(
								getColor(center.getRow() + 1, center.getCol()))) {
					MyPoint point = new MyPoint(center.getRow() + 1,
							center.getCol());
					list.add(point);
					queue.offer(point);
					eliminateBeads.remove(point);
				}
				// LEFT
				if ((center.getCol() - 1 >= 0)
						&& (eliminateBeads.contains(new MyPoint(
								center.getRow(), center.getCol() - 1)))
						&& getColor(center.getRow(), center.getCol()).equals(
								getColor(center.getRow(), center.getCol() - 1))) {
					MyPoint point = new MyPoint(center.getRow(),
							center.getCol() - 1);
					list.add(point);
					queue.offer(point);
					eliminateBeads.remove(point);
				}
				// RIGHT
				if ((center.getCol() + 1 < COLS)
						&& (eliminateBeads.contains(new MyPoint(
								center.getRow(), center.getCol() + 1)))
						&& getColor(center.getRow(), center.getCol()).equals(
								getColor(center.getRow(), center.getCol() + 1))) {
					MyPoint point = new MyPoint(center.getRow(),
							center.getCol() + 1);
					list.add(point);
					queue.offer(point);
					eliminateBeads.remove(point);
				}
			}
			eliminateList.add(list);
		}
	}

	private int correctCol(Point2D point) {
		return Math
				.max(0, Math.min((int) (point.getX() / beadWidth), COLS - 1));

	}

	private int correctCol(int col) {
		return Math.max(0, Math.min(col, COLS - 1));
	}

	private int correctRow(Point2D point) {
		return Math
				.max(0, Math.min((int) (point.getY() / beadWidth), ROWS - 1));
	}

	private int correctRow(int row) {
		return Math.max(0, Math.min(row, ROWS - 1));
	}

	private Color getColor(int row, int col) {
		row = correctRow(row);
		col = correctCol(col);
		return (ellipse2ds[row][col] == null) ? null : ellipse2ds[row][col]
				.getColor();
	}

	private void beadMotion(int oldRow, int oldCol, int newRow, int newCol) {
		oldRow = correctRow(oldRow);
		oldCol = correctCol(oldCol);
		if (ellipse2ds[oldRow][oldCol] == null)
			return;
		newRow = correctRow(newRow);
		newCol = correctCol(newCol);
		if (ellipse2ds[newRow][newCol] != null)
			return;
		ellipse2ds[newRow][newCol] = ellipse2ds[oldRow][oldCol];
		ellipse2ds[newRow][newCol].setFrame(newCol * beadWidth, newRow
				* beadWidth, beadWidth, beadWidth);
		ellipse2ds[oldRow][oldCol] = null;
	}

	private class MouseHandler extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mousePressed(e);
			if (!canMove)
				return;
			current = findAndTake(e.getPoint());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseReleased(e);
			if (!canMove)
				return;
			drop(e.getPoint());
			while (isSwap && checkAndEliminate())
				fallBeads();
			isSwap = false;
		}
	}

	private class MouseMotionHandler extends MouseMotionAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseDragged(e);
			if (!canMove)
				return;
			if (current != null) {
				Point2D point = e.getPoint();
				current.setFrame(point.getX() - beadWidth / 2, point.getY()
						- beadWidth / 2, beadWidth, beadWidth);

				int col = correctCol(point);
				int row = correctRow(point);
				if (ellipse2ds[row][col] != null
						&& ellipse2ds[row][col].contains(point)) {
					isSwap = true;
					beadMotion(row, col, emptyRow, emptyCol);
					emptyRow = row;
					emptyCol = col;
				}

				repaint();
			}
		}
	}
}
