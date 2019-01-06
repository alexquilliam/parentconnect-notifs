package gui;

import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TableRow;
import javafx.scene.layout.BorderPane;

public class CategoryGradesController {
	@FXML private BorderPane root;
	@FXML private CategoryGradesTable table;

	public void initialize() {
		ArrayList<IntegerProperty> rowIndexes = new ArrayList<IntegerProperty>();

		for (int i = 1; i < table.getItems().size(); i++) {
			rowIndexes.add(new SimpleIntegerProperty(table.getItems().get(i), table.getItems().get(i).getCategory(), i));
		}

		PseudoClass odd = PseudoClass.getPseudoClass("odd");
		PseudoClass even = PseudoClass.getPseudoClass("even");

		table.setRowFactory(table -> {
			TableRow<DisplayableCategory> row = new TableRow<DisplayableCategory>();

			for (IntegerProperty i : rowIndexes) {
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
	}
}
