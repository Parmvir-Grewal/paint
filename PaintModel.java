package ca.utoronto.utm.paint;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javafx.scene.canvas.GraphicsContext;

public class PaintModel extends Observable implements Observer {

	public void save(PrintWriter writer) {
		writer.write("PaintSaveFileVersion1.0\n");
		for (PaintCommand c : this.commands) {
			int r = (int) (c.getColor().getRed()*100);
			int g = (int) (c.getColor().getGreen()*100);
			int b = (int) (c.getColor().getBlue()*100);
			boolean fill = c.isFill();
			if (c.getClass().getName().contains("CircleCommand")) {
				CircleCommand circle = (CircleCommand) c;
				writer.write("Circle\n" + "\tcolor:" + r + "," + g + "," + b + "\n\tfilled:" + fill + "\n\tcenter:("
						+ circle.getCentre().x + "," + circle.getCentre().y + ")\n\tradius:" + circle.getRadius()
						+ "\nEnd Circle\n");
			} else if (c.getClass().getName().contains("RectangleCommand")) {
				RectangleCommand rectangle = (RectangleCommand) c;
				writer.write("Rectangle\n" + "\tcolor:" + r + "," + g + "," + b + "\n\tfilled:" + fill + "\n\tp1:("
						+ rectangle.getP1().x + "," + rectangle.getP1().y + ")\n\tp2:(" + rectangle.getP2().x + ","
						+ rectangle.getP2().y + ")\nEnd Rectangle\n");
			} else if (c.getClass().getName().contains("SquiggleCommand")) {
				SquiggleCommand squiggle = (SquiggleCommand) c;
				writer.write("Squiggle\n" + "\tcolor:" + r + "," + g + "," + b + "\n\tfilled:" + fill + "\n\tpoints\n");
				for (Point point : squiggle.getPoints()) {
					writer.write("\t\tpoint:(" + point.x + "," + point.y + ")\n");
				}
				writer.write("\tend points\nEnd Squiggle\n");
			} else if (c.getClass().getName().contains("PolylineCommand")) {
				PolylineCommand polyline = (PolylineCommand) c;
				writer.write("Polyline\n" + "\tcolor:" + r + "," + g + "," + b + "\n\tfilled:" + fill + "\n\tpoints\n");
				for (Point point : polyline.getPoints()) {
					writer.write("\t\tpoint:(" + point.x + "," + point.y + ")\n");
				}
				writer.write("\tend points\nEnd Polyline\n");
			}
		}

		writer.write("End Paint Save File");
		writer.close();

	}

	public void reset() {
		for (PaintCommand c : this.commands) {
			c.deleteObserver(this);
		}
		this.commands.clear();
		this.setChanged();
		this.notifyObservers();
	}

	public void addCommand(PaintCommand command) {
		this.commands.add(command);
		command.addObserver(this);
		this.setChanged();
		this.notifyObservers();
	}

	private ArrayList<PaintCommand> commands = new ArrayList<PaintCommand>();

	public void executeAll(GraphicsContext g) {
		for (PaintCommand c : this.commands) {
			c.execute(g);
		}
	}

	/**
	 * We Observe our model components, the PaintCommands
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.setChanged();
		this.notifyObservers();
	}
}
