package gui;

import java.util.ArrayList;
import java.util.Map.Entry;

import analysis.Category;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import utils.GraphicUtils;

@SuppressWarnings("unchecked")
public class CategoryGradesTable extends TableView<DisplayableCategory> {
	public CategoryGradesTable() {}
	
	public CategoryGradesTable(DisplayableClassScore classScore) {
		TableColumn<DisplayableCategory, String> category = new TableColumn<DisplayableCategory, String>("Category");
		TableColumn<DisplayableCategory, String> score = new TableColumn<DisplayableCategory, String>("Score");
		TableColumn<DisplayableCategory, String> grade = new TableColumn<DisplayableCategory, String>("Grade");
		TableColumn<DisplayableCategory, String> weight = new TableColumn<DisplayableCategory, String>("Weight");

		category.setCellValueFactory(
				new Callback<CellDataFeatures<DisplayableCategory, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<DisplayableCategory, String> p) {
						return new ReadOnlyObjectWrapper<>(p.getValue().getCategory());
					}
				});
		
		score.setCellValueFactory(
				new Callback<CellDataFeatures<DisplayableCategory, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<DisplayableCategory, String> p) {
						return new ReadOnlyObjectWrapper<>(p.getValue().getScore());
					}
				});
		
		grade.setCellValueFactory(
				new Callback<CellDataFeatures<DisplayableCategory, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<DisplayableCategory, String> p) {
						return new ReadOnlyObjectWrapper<>(p.getValue().getGrade());
					}
				});
		
		weight.setCellValueFactory(
				new Callback<CellDataFeatures<DisplayableCategory, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<DisplayableCategory, String> p) {
						return new ReadOnlyObjectWrapper<>(p.getValue().getWeight());
					}
				});

		this.setItems(FXCollections.observableArrayList(getDisplayableCategories(classScore)));
		this.getColumns().addAll(category, score, grade, weight);
		
		this.heightProperty().addListener((observer, oldValue, newValue) -> {
			GraphicUtils.displayFilledRows(this);
		});
	}
	
	private ArrayList<DisplayableCategory> getDisplayableCategories(DisplayableClassScore classScore) {
		ArrayList<DisplayableCategory> displayableCategories = new ArrayList<DisplayableCategory>();
		for(Entry<Category, Double> entry : classScore.getCategoryScores().entrySet()) {
			String formattedScore = String.format("%.2f", entry.getValue() * 100);
			
			if(formattedScore.equals("NaN")) {
				displayableCategories.add(new DisplayableCategory(entry.getKey().getAlias(), "−", "−", Integer.toString(entry.getKey().getWeight()) + "%"));
			}else {
				displayableCategories.add(new DisplayableCategory(entry.getKey().getAlias(), formattedScore, calculateGrade(entry.getValue()), Integer.toString(entry.getKey().getWeight()) + "%"));
			}
		}
		
		return displayableCategories;
	}
	
	private String calculateGrade(double score) {
		score *= 100;
		if(score >= 90.0) {
			return "A";
		}else if(score >= 80.0) {
			return "B";
		}else if(score >= 70.0) {
			return "C";
		}else if(score >= 60.0) {
			return "D";
		}else {
			return "E";
		}
	}
}
