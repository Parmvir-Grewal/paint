package ca.utoronto.utm.paint;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PolylineManipulatorStrategy extends ShapeManipulatorStrategy {
	int count;

	PolylineManipulatorStrategy(PaintModel paintModel) {
		super(paintModel);
		this.count = 0;
	}

	private PolylineCommand polylineCommand;

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseButton.PRIMARY) {
			if (this.count == 0) {
				this.polylineCommand = new PolylineCommand();
				this.polylineCommand.add(new Point((int) e.getX(), (int) e.getY()), false);
				this.polylineCommand.add(new Point((int) e.getX(), (int) e.getY()), false);
				this.count++;
				this.addCommand(polylineCommand);

			} else if (this.count == 1) {
				this.polylineCommand.add(new Point((int) e.getX(), (int) e.getY()), false);

			}
		} else if (e.getButton() == MouseButton.SECONDARY && this.polylineCommand.getPoints().size() >= 3) {
			this.count = 0;
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (this.count == 1) {
			this.polylineCommand.add(new Point((int) e.getX(), (int) e.getY()), true);
		}
	}

}
