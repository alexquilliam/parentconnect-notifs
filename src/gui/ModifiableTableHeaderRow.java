package gui;

import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

@SuppressWarnings("rawtypes")
public class ModifiableTableHeaderRow extends TableHeaderRow {
    public ModifiableTableHeaderRow(TableViewSkinBase tableSkin) {
        super(tableSkin);
    }

    @Override
    protected NestedTableColumnHeader createRootHeader() {
        return new ModifiableNestedTableColumnHeader(getTableSkin(), null);
    }
}
