/* PreciseSelectionAction.java created 2007-10-05
 *
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalPlotFocusSelector;
import org.signalml.app.view.dialog.SignalSelectionDialog;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.signal.BoundedSignalSelection;
import org.signalml.domain.signal.SignalSelection;
import org.springframework.context.support.MessageSourceAccessor;

/** PreciseSelectionAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PreciseSelectionAction extends AbstractFocusableSignalMLAction<SignalPlotFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(PreciseSelectionAction.class);

	private SignalSelectionDialog signalSelectionDialog;

	public PreciseSelectionAction(MessageSourceAccessor messageSource, SignalPlotFocusSelector signalPlotFocusSelector) {
		super(messageSource, signalPlotFocusSelector);
		setText("action.preciseSelection");
		setIconPath("org/signalml/app/icon/preciseselection.png");
		setToolTip("action.preciseSelectionToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		SignalPlot plot = getActionFocusSelector().getActiveSignalPlot();
		if (plot == null) {
			logger.warn("Target plot doesn't exist");
			return;
		}
		SignalView view = plot.getView();

		SignalSelection signalSelection = view.getSignalSelection(plot);
		BoundedSignalSelection boundedSignalSelection = new BoundedSignalSelection(signalSelection);
		boundedSignalSelection.setMaxPage(plot.getPageCount());
		boundedSignalSelection.setMaxBlock(plot.getBlockCount());
		boundedSignalSelection.setMaxTime(plot.getMaxTime());
		boundedSignalSelection.setPageSize(plot.getPageSize());
		boundedSignalSelection.setBlocksPerPage(plot.getBlocksPerPage());
		boundedSignalSelection.setSamplingFrequency(plot.getSamplingFrequency());
		boundedSignalSelection.setChannels(plot.getSignalChain().getLabels());

		boolean ok = signalSelectionDialog.showDialog(boundedSignalSelection, true);
		if (ok) {
			view.setSignalSelection(plot, boundedSignalSelection.getSelection());
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalPlot() != null);
	}

	public SignalSelectionDialog getSignalSelectionDialog() {
		return signalSelectionDialog;
	}

	public void setSignalSelectionDialog(SignalSelectionDialog signalSelectionDialog) {
		this.signalSelectionDialog = signalSelectionDialog;
	}

}
