package utils;

import java.util.ArrayList;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class GraphicUtils {
	public static ArrayList<DoubleProperty> calculateHorizontalRatios(ArrayList<Region> regions, Pane root) {
		ArrayList<DoubleProperty> horizontalRatios = new ArrayList<DoubleProperty>();
		for(Region r : regions) {
			horizontalRatios.add(new SimpleDoubleProperty(r, r.getId(), r.getWidth() / root.getWidth()));
		}
		
		return horizontalRatios;
	}
	
	public static ArrayList<DoubleProperty> calculateVerticalRatios(ArrayList<Region> regions, Pane root) {
		ArrayList<DoubleProperty> verticalRatios = new ArrayList<DoubleProperty>();
		for(Region r : regions) {
			verticalRatios.add(new SimpleDoubleProperty(r, r.getId(), r.getHeight() / root.getHeight()));
		}
		
		return verticalRatios;
	}
	
	public static void maintainHorizontalRatiosOnResize(ArrayList<DoubleProperty> horizontalRatios, ArrayList<Region> regions, Pane root) {
		double windowWidth = root.getWidth();
		for(Region r : regions) {
			for(DoubleProperty d : horizontalRatios) {
				if(((Region) d.getBean()).getId().equals(r.getId())) {
					double width = windowWidth * d.get();
					r.setPrefWidth(width);
					root.requestLayout();
				}
			}
		}
	}
	
	public static void maintainVerticalRatiosOnResize(ArrayList<DoubleProperty> verticalRatios, ArrayList<Region> regions, Pane root) {
		double windowHeight = root.getHeight();
		for(Region r : regions) {
			for(DoubleProperty d : verticalRatios) {
				if(((Region) d.getBean()).getId().equals(r.getId())) {
					double height = windowHeight * d.get();
					r.setPrefHeight(height);
					root.requestLayout();
				}
			}
		}
	}
	
	public static void displayFilledRows(TableView<?> table) {
		TableHeaderRow headerRow = (TableHeaderRow) table.lookup("TableHeaderRow");

		double otherComponentHeights = table.getInsets().getTop() + table.getInsets().getBottom() + (headerRow != null ? headerRow.getHeight() : 0);
		double rowHeight = (((BorderPane) table.getParent()).getHeight() - otherComponentHeights) / table.getItems().size();
		
		table.setFixedCellSize(rowHeight);
	}
}
