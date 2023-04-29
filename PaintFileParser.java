package ca.utoronto.utm.paint;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;

/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about its effort
 * to parse a file. After a successful parse, an instance will have an ArrayList
 * of PaintCommand suitable for rendering. If there is an error in the parse,
 * the instance stores information about the error. For more on the format of
 * Version 1.0 of the paint save file format, see the associated documentation.
 * 
 * @author
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage = ""; // error encountered during parse
	private PaintModel paintModel;

	/**
	 * Below are Patterns used in parsing
	 */
	private Pattern pFileStart = Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd = Pattern.compile("^EndPaintSaveFile$");

	private Pattern pCircleStart = Pattern.compile("^Circle$");
	private Pattern pCircleEnd = Pattern.compile("^EndCircle$");
	private Pattern pRectangleStart = Pattern.compile("^Rectangle$");
	private Pattern pRectangleEnd = Pattern.compile("^EndRectangle$");
	private Pattern pSquiggleStart = Pattern.compile("^Squiggle$");
	private Pattern pSquiggleEnd = Pattern.compile("^EndSquiggle$");
	private Pattern pPolylineStart = Pattern.compile("^Polyline$");
	private Pattern pPolylineEnd = Pattern.compile("^EndPolyline$");
	private Pattern pPointsStart = Pattern.compile("^points$");
	private Pattern pPointsEnd = Pattern.compile("^endpoints$");
	// ADD MORE!!

	/**
	 * Store an appropriate error message in this, including lineNumber where the
	 * error occurred.
	 * 
	 * @param mesg
	 */
	private void error(String mesg) {
		this.errorMessage = "Error in line " + lineNumber + " " + mesg;
	}

	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * Parse the inputStream as a Paint Save File Format file. The result of the
	 * parse is stored as an ArrayList of Paint command. If the parse was not
	 * successful, this.errorMessage is appropriately set, with a useful error
	 * message.
	 * 
	 * @param inputStream the open file to parse
	 * @param paintModel  the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
		this.paintModel = paintModel;
		this.errorMessage = "";

		// During the parse, we will be building one of the
		// following commands. As we parse the file, we modify
		// the appropriate command.

		CircleCommand circleCommand = null;
		RectangleCommand rectangleCommand = null;
		SquiggleCommand squiggleCommand = null;
		PolylineCommand polylineCommand = null;

		try {
			int state = 0;
			Matcher m;
			String l;

			this.lineNumber = 0;
			while ((l = inputStream.readLine()) != null) {
				l = l.replaceAll("\\s+", ""); //
				this.lineNumber++;
				System.out.println(lineNumber + " " + l + " " + state);
				switch (state) {
				case 0:
					m = pFileStart.matcher(l);
					if (m.matches()) {
						state = 1;
						break;
					}
					error("Expected Start of Paint Save File");
					return false;
				case 1: // Looking for the start of a new object or end of the save file
					m = pCircleStart.matcher(l);
					if (m.matches()) {
						circleCommand = new CircleCommand(new Point(0, 0), 0);
						state = 2;
						break;
					}
					m = pRectangleStart.matcher(l);
					if (m.matches()) {
						rectangleCommand = new RectangleCommand(new Point(0, 0), new Point(0, 0));
						state = 3;
						break;
					}
					m = pSquiggleStart.matcher(l);
					if (m.matches()) {
						squiggleCommand = new SquiggleCommand();
						state = 4;
						break;
					}
					m = pPolylineStart.matcher(l);
					if (m.matches()) {
						polylineCommand = new PolylineCommand();
						state = 5;
						break;
					}
					m = pFileEnd.matcher(l);
					if (m.matches()) {
						state = 6;
						break;
					}

				case 2:
					if (l.contains("color")) {
						String[] color = l.split(",");
						int r = Integer.parseInt(color[0].split(":")[1]);
						int g = Integer.parseInt(color[1]);
						int b = Integer.parseInt(color[2]);
						circleCommand.setColor(Color.rgb(r, g, b));
						break;
					}
					if (l.contains("filled")) {
						String[] fill = l.split(":");
						boolean filled = Boolean.parseBoolean(fill[1]);
						circleCommand.setFill(filled);
						break;
					}
					if (l.contains("center")) {
						String[] centerPoints = l.split(",");
						Point centre = new Point(Integer.parseInt(centerPoints[0].split("\\(")[1]),
								Integer.parseInt(centerPoints[1].split("\\)")[0]));
						circleCommand.setCentre(centre);
						break;
					}
					if (l.contains("radius")) {
						int radius = Integer.parseInt(l.split(":")[1]);
						circleCommand.setRadius(radius);
						break;
					}
					m = pCircleEnd.matcher(l);
					if (m.matches()) {
						this.paintModel.addCommand(circleCommand);
						state = 1;
						break;
					}

				case 3:
					if (l.contains("color")) {
						String[] color = l.split(",");
						int r = Integer.parseInt(color[0].split(":")[1]);
						int g = Integer.parseInt(color[1]);
						int b = Integer.parseInt(color[2]);
						rectangleCommand.setColor(Color.rgb(r, g, b));
						break;
					}
					if (l.contains("filled")) {
						String[] fill = l.split(":");
						boolean filled = Boolean.parseBoolean(fill[1]);
						rectangleCommand.setFill(filled);
						break;
					}
					if (l.contains("p1")) {
						String[] points = l.split(",");
						Point p1 = new Point(Integer.parseInt(points[0].split("\\(")[1]),
								Integer.parseInt(points[1].split("\\)")[0]));
						rectangleCommand.setP1(p1);
						break;
					}
					if (l.contains("p2")) {
						String[] points = l.split(",");
						Point p2 = new Point(Integer.parseInt(points[0].split("\\(")[1]),
								Integer.parseInt(points[1].split("\\)")[0]));
						rectangleCommand.setP2(p2);
						break;
					}
					m = pRectangleEnd.matcher(l);
					if (m.matches()) {
						this.paintModel.addCommand(rectangleCommand);
						state = 1;
						break;
					}
				case 4:
					if (l.contains("color")) {
						String[] color = l.split(",");
						int r = Integer.parseInt(color[0].split(":")[1]);
						int g = Integer.parseInt(color[1]);
						int b = Integer.parseInt(color[2]);
						squiggleCommand.setColor(Color.rgb(r, g, b));
						break;
					}
					if (l.contains("filled")) {
						String[] fill = l.split(":");
						boolean filled = Boolean.parseBoolean(fill[1]);
						squiggleCommand.setFill(filled);
						break;
					}

					if (l.contains("point:")) {
						String[] points = l.split(",");
						Point p1 = new Point(Integer.parseInt(points[0].split("\\(")[1]),Integer.parseInt(points[1].split("\\)")[0]));
						System.out.println("fwefef");
						squiggleCommand.add(p1);
						break;
					}
					m = pSquiggleEnd.matcher(l);
					if (m.matches()) {
						this.paintModel.addCommand(squiggleCommand);
						state = 1;
						break;
					}
				case 5:
					if (l.contains("color")) {
						String[] color = l.split(",");
						int r = Integer.parseInt(color[0].split(":")[1]);
						int g = Integer.parseInt(color[1]);
						int b = Integer.parseInt(color[2]);
						polylineCommand.setColor(Color.rgb(r, g, b));
						break;
					}
					if (l.contains("filled")) {
						String[] fill = l.split(":");
						boolean filled = Boolean.parseBoolean(fill[1]);
						polylineCommand.setFill(filled);
						break;
					}

					if (l.contains("point:")) {
						String[] points = l.split(",");
						Point p1 = new Point(Integer.parseInt(points[0].split("\\(")[1]),Integer.parseInt(points[1].split("\\)")[0]));
						polylineCommand.add(p1, false);
						break;
					}
					m = pPolylineEnd.matcher(l);
					if (m.matches()) {
						this.paintModel.addCommand(polylineCommand);
						state = 1;
						break;
					}
				case 6:
					break;
				}
			}
		} catch (Exception e) {

		}
		return true;
	}
}
