package org.signalml.app.method.ep.view.time;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.method.ep.EvokedPotentialParameters;

public class EvokedPotentialsTimeSelectionPanel extends TimeSelectionPanel {

	public EvokedPotentialsTimeSelectionPanel() {
		super(_("Averaging time"));
	}

	public void fillModelFromPanel(EvokedPotentialParameters parameters) {
		parameters.setAveragingSelectionStart(getStartTimeSpinner().getValue());
		parameters.setAveragingSelectionEnd(getEndTimeSpinner().getValue());
	}

}
