package org.signalml.app.action;

import java.awt.event.ActionEvent;
import org.signalml.app.action.selector.TagStyleFocusSelector;
import org.signalml.app.model.TagStylePaletteDescriptor;
import org.signalml.app.view.dialog.TagStylePaletteDialog;

/**
 * An action opening the dialog for creating and editing tag styles presets.
 *
 * @author Piotr Szachewicz
 */
public class EditTagStylePresetsAction extends AbstractFocusableSignalMLAction<TagStyleFocusSelector> {

	/**
	 * Dialog for tag styles preset editon.
	 */
	private TagStylePaletteDialog tagStylePaletteDialog;

	/**
	 * Constructor.
	 * @param messageSource messageSource to be used for localization.
	 * @param tagStyleFocusSelector selector determining which tag
	 * document is now opened
	 */
	public  EditTagStylePresetsAction( TagStyleFocusSelector tagStyleFocusSelector) {
		super( tagStyleFocusSelector);
		setText("action.editTagStylePresets");
		setToolTip("action.editTagStylePresetsToolTip");
		setIconPath("org/signalml/app/icon/palette.png");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TagStylePaletteDescriptor descriptor = new TagStylePaletteDescriptor(null, null);
		tagStylePaletteDialog.showDialog(descriptor, true);
	}

	/**
	 * Sets the dialog opened when this action is called.
	 * @param tagStylePaletteDialog dialog to be opened.
	 */
	public void setTagStylePaletteDialog(TagStylePaletteDialog tagStylePaletteDialog) {
		this.tagStylePaletteDialog = tagStylePaletteDialog;
	}

}
