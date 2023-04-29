package ca.utoronto.utm.paint;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;

public class PolylineCommand extends PaintCommand {
	private ArrayList<Point> points = new ArrayList<Point>();

	public PolylineCommand() {
		this.setChanged();
		this.notifyObservers();
	}

	public ArrayList<Point> getPoints() {
		return this.points;
	}

	public void add(Point p2, boolean b) {
		if (b) {
			this.points.set(this.points.size() - 1, p2);
		} else {
			this.points.add(p2);
		}
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public void execute(GraphicsContext g) {
		ArrayList<Point> points = this.getPoints();
		g.setStroke(this.getColor());
		for (int i = 0; i < points.size() - 1; i++) {
			Point p1 = points.get(i);
			Point p2 = points.get(i + 1);
			g.strokeLine(p1.x, p1.y, p2.x, p2.y);
		}
	}

}
