package gui;

import java.lang.reflect.Field;
import java.util.List;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableViewSkin;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumnBase;

@SuppressWarnings("rawtypes")
public class ModifiableTableColumnHeader extends TableColumnHeader {
	public ModifiableTableColumnHeader(TableViewSkin skin, TableColumnBase column) {
		super(skin, column);
	}

	@Override
	protected void layoutChildren() {
		try {
			super.layoutChildren();
			Node sortArrow = getSortArrow();
			if (sortArrow == null || !sortArrow.isVisible())
				return;
			if (getSortIconDisplay() == ContentDisplay.RIGHT)
				return;
			double sortWidth = sortArrow.prefWidth(-1);
			double headerWidth = snapSize(getWidth()) - (snappedLeftInset() + snappedRightInset());
			double headerHeight = getHeight() - (snappedTopInset() + snappedBottomInset());

			sortArrow.resize(sortWidth, sortArrow.prefHeight(-1));
			positionInArea(sortArrow, snappedLeftInset(), snappedTopInset(), sortWidth, headerHeight, 0, HPos.CENTER,
					VPos.CENTER);
			getLabel().resizeRelocate(sortWidth, 0, headerWidth - sortWidth, getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final StyleablePropertyFactory<ModifiableTableColumnHeader> FACTORY = new StyleablePropertyFactory<>(
			TableColumnHeader.getClassCssMetaData());

	private static final ContentDisplay DEFAULT_SORT_ICON_DISPLAY = ContentDisplay.RIGHT;

	private static CssMetaData<ModifiableTableColumnHeader, ContentDisplay> CSS_SORT_ICON_DISPLAY = FACTORY
			.createEnumCssMetaData(ContentDisplay.class, "-fx-sort-icon-display",
					header -> header.sortIconDisplayProperty(), DEFAULT_SORT_ICON_DISPLAY);

	private StyleableObjectProperty<ContentDisplay> sortIconDisplay;

	protected StyleableObjectProperty<ContentDisplay> sortIconDisplayProperty() {
		if (sortIconDisplay == null) {
			sortIconDisplay = new SimpleStyleableObjectProperty<>(CSS_SORT_ICON_DISPLAY, this, "sortIconDisplay",
					DEFAULT_SORT_ICON_DISPLAY);

		}
		return sortIconDisplay;
	}

	protected ContentDisplay getSortIconDisplay() {
		return sortIconDisplay != null ? sortIconDisplay.get() : DEFAULT_SORT_ICON_DISPLAY;
	}

	protected void setSortIconDisplay(ContentDisplay display) {
		sortIconDisplayProperty().set(display);
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return FACTORY.getCssMetaData();
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}

	private Node getSortArrow() throws Exception {
		Field field = TableColumnHeader.class.getDeclaredField("sortArrow");
		field.setAccessible(true);

		return (Node) field.get(this);
	}

	private Label getLabel() throws Exception {
		Field field = TableColumnHeader.class.getDeclaredField("label");
		field.setAccessible(true);

		return (Label) field.get(this);
	}
}