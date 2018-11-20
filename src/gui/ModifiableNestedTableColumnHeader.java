package gui;

import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

import javafx.scene.control.TableColumnBase;

@SuppressWarnings("rawtypes")
public class ModifiableNestedTableColumnHeader extends NestedTableColumnHeader {
	public ModifiableNestedTableColumnHeader(TableViewSkinBase skin, TableColumnBase column) {
        super(skin, column);
    }

    @Override
    protected TableColumnHeader createTableColumnHeader(
            TableColumnBase col) {
        return col == null || col.getColumns().isEmpty() || col == getTableColumn() ?
                new ModifiableTableColumnHeader((TableViewSkin) getTableViewSkin(), col) :
                new ModifiableNestedTableColumnHeader(getTableViewSkin(), col);
    }
}
