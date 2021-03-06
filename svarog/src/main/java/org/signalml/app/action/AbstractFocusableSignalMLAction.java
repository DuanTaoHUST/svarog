/* AbstractFocusableSignalMLAction.java created 2007-10-15
 *
 */

package org.signalml.app.action;

import org.signalml.app.action.selector.ActionFocusEvent;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.domain.tag.StyledMonitorTagSet;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** AbstractFocusableSignalMLAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractFocusableSignalMLAction<T extends ActionFocusSelector> extends AbstractSignalMLAction implements ActionFocusListener {

	static final long serialVersionUID = 1L;

	private T actionFocusSelector;

	protected AbstractFocusableSignalMLAction(T actionFocusSelector) {
		super();
		if (actionFocusSelector == null) {
			throw new NullPointerException("No action focus selector");
		}
		this.actionFocusSelector = actionFocusSelector;
		actionFocusSelector.addActionFocusListener(this);
		setEnabledAsNeeded();
	}

	public T getActionFocusSelector() {
		return actionFocusSelector;
	}

	protected void setActionFocusSelector(T actionFocusSelector) {
		if (actionFocusSelector == null) {
			throw new NullPointerException("No action focus selector");
		}
		if (this.actionFocusSelector != actionFocusSelector) {
			this.actionFocusSelector = actionFocusSelector;
			setEnabledAsNeeded();
		}
	}

	@Override
	public void actionFocusChanged(ActionFocusEvent e) {
		setEnabledAsNeeded();
	}

	protected boolean isSignalDocumentOfflineSignalDocument(SignalDocument signalDocument) {
		return signalDocument != null && !(signalDocument instanceof MonitorSignalDocument);
	}

	protected boolean isTagDocumentAMonitorTagDocument(TagDocument tagDocument) {
		return tagDocument != null && tagDocument.getTagSet() instanceof StyledMonitorTagSet;
	}

}
