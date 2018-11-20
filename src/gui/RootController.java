package gui;

import java.util.ArrayList;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import scraper.ClassScore;
import utils.Utils;

public class RootController {
	@FXML private BorderPane root;
	@FXML private ResizableTableView<DisplayableClassScore> table;
	@FXML private TableColumn<DisplayableClassScore, String> courses;
	@FXML private TableColumn<DisplayableClassScore, String> grades;
	@FXML private TableColumn<DisplayableClassScore, String> scores;
	@FXML private TableColumn<DisplayableClassScore, String> changes;
	@FXML private TableColumn<DisplayableClassScore, String> teachers;
	
	public void initialize() {		
		courses.setCellValueFactory(
			new Callback<CellDataFeatures<DisplayableClassScore, String>, ObservableValue<String>>() {
				public ObservableValue<String> call(CellDataFeatures<DisplayableClassScore, String> p) {
					return new ReadOnlyObjectWrapper<>(p.getValue().getCourseName());
				}
			});
		
		grades.setCellValueFactory(
				new Callback<CellDataFeatures<DisplayableClassScore, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<DisplayableClassScore, String> p) {
						return new ReadOnlyObjectWrapper<>(p.getValue().getCourseGrade());
					}
				});
		
		scores.setCellValueFactory(
				new Callback<CellDataFeatures<DisplayableClassScore, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<DisplayableClassScore, String> p) {
						return new ReadOnlyObjectWrapper<>(p.getValue().getCourseScore());
					}
				});
		
		changes.setCellValueFactory(
				new Callback<CellDataFeatures<DisplayableClassScore, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<DisplayableClassScore, String> p) {
						return new ReadOnlyObjectWrapper<>(p.getValue().getCourseChange());
					}
				});
		
		teachers.setCellValueFactory(
				new Callback<CellDataFeatures<DisplayableClassScore, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<DisplayableClassScore, String> p) {
						return new ReadOnlyObjectWrapper<>(p.getValue().getCourseTeacher());
					}
				});
		
		table.getItems().setAll(getDisplayableClassScores());

		ArrayList<IntegerProperty> rowIndexes = new ArrayList<IntegerProperty>();
		
		for(int i = 1; i < table.getItems().size(); i++) {
			rowIndexes.add(new SimpleIntegerProperty(table.getItems().get(i), table.getItems().get(i).getCourseName(), i));
		}
		
		PseudoClass odd = PseudoClass.getPseudoClass("odd");
		PseudoClass even = PseudoClass.getPseudoClass("even");
		
		table.setRowFactory( table -> {
			TableRow<DisplayableClassScore> row = new TableRow<DisplayableClassScore>();
			
			for(IntegerProperty i : rowIndexes) {
				if(i.getBean().equals(row.getItem())) {
					int index = i.get();
					if(index % 2 == 1) {
						row.pseudoClassStateChanged(odd, true);
					}else {
						row.pseudoClassStateChanged(even, true);
					}
				}
			}
			
			return row;
		});
		
		table.heightProperty().addListener((observer, oldValue, newValue) -> {
			displayFilledRows(table);
		});
	}
	
	private void displayFilledRows(TableView<?> table) {
		TableHeaderRow headerRow = (TableHeaderRow) table.lookup("TableHeaderRow");

		double otherComponentHeights = table.getInsets().getTop() + table.getInsets().getBottom() + (headerRow != null ? headerRow.getHeight() : 0);
		double rowHeight = (((BorderPane) table.getParent()).getHeight() - otherComponentHeights) / table.getItems().size();
		
		table.setFixedCellSize(rowHeight);
	}
	
	private ArrayList<DisplayableClassScore> getDisplayableClassScores() {
		ArrayList<DisplayableClassScore> classScores = new ArrayList<DisplayableClassScore>();
		for(ClassScore c : Utils.getClassScores()) {
			classScores.add(new DisplayableClassScore(c));
		}
		
		return classScores;
	}
}
