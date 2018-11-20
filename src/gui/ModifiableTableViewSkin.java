package gui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;

import javafx.scene.control.TableView;

public class ModifiableTableViewSkin<T> extends TableViewSkin<T> {
    public ModifiableTableViewSkin(TableView<T> table) {
        super(table);
    }

    @Override
    protected TableHeaderRow createTableHeaderRow() {
        return new ModifiableTableHeaderRow(this);
    }
}
