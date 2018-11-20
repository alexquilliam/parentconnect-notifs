package gui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class UserInterface extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(RootController.class.getResource("root.fxml"));
		BorderPane root = loader.load();

		Scene scene = new Scene(root, 600, 400);

		//stage.setMinWidth(600);
		//stage.setMinHeight(400);

		stage.setTitle("ParentCONNECTxp");
		stage.setMaximized(true);
		stage.setScene(scene);

		stage.show();
		
		for(Node n : root.getChildren()) {
			if(n.getId().equals("table")) {
				ResizableTableView<?> table = (ResizableTableView<?>) n;
				
				displayFilledRows(table);
			}
		}
	}
	
	private void displayFilledRows(TableView<?> table) {
		TableHeaderRow headerRow = (TableHeaderRow) table.lookup("TableHeaderRow");

		double otherComponentHeights = table.getInsets().getTop() + table.getInsets().getBottom() + (headerRow != null ? headerRow.getHeight() : 0);
		double rowHeight = (((BorderPane) table.getParent()).getHeight() - otherComponentHeights) / table.getItems().size();
		
		table.setFixedCellSize(rowHeight);
	}

	public static void main(String[] args) throws Exception {
		UserInterface.launch(args);
	}
}
