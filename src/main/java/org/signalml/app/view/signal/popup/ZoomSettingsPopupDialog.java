/* ZoomSettingsPopupDialog.java created 2007-10-14
 *
 */

package org.signalml.app.view.signal.popup;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.view.dialog.AbstractPopupDialog;
import org.signalml.app.view.element.SignalZoomSettingsPanel;
import org.signalml.app.view.signal.ZoomSignalTool;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** ZoomSettingsPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ZoomSettingsPopupDialog extends AbstractPopupDialog {

	private static final long serialVersionUID = 1L;

	private SignalZoomSettingsPanel signalZoomSettingsPanel;

	public ZoomSettingsPopupDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public ZoomSettingsPopupDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	public JComponent createInterface() {

		signalZoomSettingsPanel = new SignalZoomSettingsPanel(messageSource, true);

		return signalZoomSettingsPanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		ZoomSignalTool tool = (ZoomSignalTool) model;

		signalZoomSettingsPanel.fillPanelFromModel(tool.getSettings());

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		ZoomSignalTool tool = (ZoomSignalTool) model;

		signalZoomSettingsPanel.fillModelFromPanel(tool.getSettings());
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ZoomSignalTool.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public boolean isFormClickApproving() {
		return true;
	}

}
