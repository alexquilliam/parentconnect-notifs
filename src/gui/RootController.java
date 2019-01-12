package gui;

import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import scraper.ClassScore;
import utils.GraphicUtils;
import utils.Utils;

public class RootController {
	@FXML private BorderPane root;
	@FXML private TableView<DisplayableClassScore> table;
	@FXML private TableColumn<DisplayableClassScore, String> courses;
	@FXML private TableColumn<DisplayableClassScore, String> grades;
	@FXML private TableColumn<DisplayableClassScore, Hyperlink> scores;
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
				new Callback<CellDataFeatures<DisplayableClassScore, Hyperlink>, ObservableValue<Hyperlink>>() {
					public ObservableValue<Hyperlink> call(CellDataFeatures<DisplayableClassScore, Hyperlink> p) {
						Hyperlink link = new Hyperlink(p.getValue().getCourseScore());
						link.setOnAction(e -> {	
							link.visitedProperty().set(false);
							
							FXMLLoader loader = new FXMLLoader(CategoryGradesController.class.getResource("category_grades.fxml"));
							BorderPane root = null;
							try {
								root = loader.load();
							}catch (Exception ex) {
								ex.printStackTrace();
							}
							
							Stage stage = new Stage();
							
							CategoryGradesTable categoryGrades = new CategoryGradesTable(p.getValue());
							categoryGrades.setId("table");
							categoryGrades.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
							
							root.setCenter(categoryGrades);
							
							stage.setScene(new Scene(root, 600, 400));
							stage.setTitle(p.getValue().getCourseName());
							stage.getIcons().add(new Image("icon.png"));
							stage.setResizable(false);
							
							stage.show();
						});
						
						return new ReadOnlyObjectWrapper<Hyperlink>(link);
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
			GraphicUtils.displayFilledRows(table);
		});
	}
	
	private ArrayList<DisplayableClassScore> getDisplayableClassScores() {
		ArrayList<DisplayableClassScore> classScores = new ArrayList<DisplayableClassScore>();
		for(ClassScore c : Utils.getClassScores()) {
			classScores.add(new DisplayableClassScore(c));
		}
		
		return classScores;
	}
}
