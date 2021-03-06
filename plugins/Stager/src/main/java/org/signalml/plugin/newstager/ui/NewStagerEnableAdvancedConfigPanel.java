/* StagerEnableAdvancedConfigPanel.java created 2008-02-14
 *
 */
package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.i18n.PluginI18n._;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.newstager.data.NewStagerParametersPreset;

/**
 * StagerEnableAdvancedConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerEnableAdvancedConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JCheckBox enableAdvancedCheckBox;

	private NewStagerAdvancedConfigObservable advancedConfigEnabledObservable;

	public NewStagerEnableAdvancedConfigPanel(AbstractDialog owner,
			NewStagerAdvancedConfigObservable advancedConfigEnabledObservable) {
		super();
		this.advancedConfigEnabledObservable = advancedConfigEnabledObservable;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout(3, 3));

		CompoundBorder border = new CompoundBorder(new TitledBorder(
				_("Advanced config")), new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		JLabel enableAdvancedLabel = new JLabel(_("Enable advanced config"));

		add(enableAdvancedLabel, BorderLayout.CENTER);
		add(getEnableAdvancedCheckBox(), BorderLayout.EAST);

	}

	public JCheckBox getEnableAdvancedCheckBox() {
		if (enableAdvancedCheckBox == null) {
			enableAdvancedCheckBox = new JCheckBox();
			enableAdvancedCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (advancedConfigEnabledObservable != null) {
						switch (e.getStateChange()) {
						case ItemEvent.SELECTED:
							advancedConfigEnabledObservable.setEnabled(true);
							return;
						case ItemEvent.DESELECTED:
							advancedConfigEnabledObservable.setEnabled(false);
							return;
						default:
							return;
						}
					}
				}
			});
		}
		return enableAdvancedCheckBox;
	}

	public void fillPanelFromParameters(
			NewStagerParametersPreset parametersPreset) {
		getEnableAdvancedCheckBox().setSelected(
				parametersPreset.enableAdvancedParameters);
	}

	public void fillParametersFromPanel(
			NewStagerParametersPreset parametersPreset) {
		parametersPreset.enableAdvancedParameters = getEnableAdvancedCheckBox()
				.isSelected();
	}

	public void validatePanel(ValidationErrors errors) {
		// nothing to do
	}

}
