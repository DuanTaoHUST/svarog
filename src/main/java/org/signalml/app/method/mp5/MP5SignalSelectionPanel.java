/* MP5SignalSelectionPanel.java created 2007-10-30
 *
 */
package org.signalml.app.method.mp5;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.springframework.context.support.MessageSourceAccessor;

/** MP5SignalSelectionPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5SignalSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JTextField signalTextField;
	private JTextField selectionTextField;

	public MP5SignalSelectionPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("mp5Method.dialog.inputData")),
		        new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		add(getSignalTextField());
		add(Box.createVerticalStrut(5));
		add(getSelectionTextField());

	}

	public JTextField getSignalTextField() {
		if (signalTextField == null) {
			signalTextField = new JTextField();
			signalTextField.setPreferredSize(new Dimension(200,25));
			signalTextField.setEditable(false);
		}
		return signalTextField;
	}

	public JTextField getSelectionTextField() {
		if (selectionTextField == null) {
			selectionTextField = new JTextField();
			selectionTextField.setPreferredSize(new Dimension(200,25));
			selectionTextField.setEditable(false);
		}
		return selectionTextField;
	}

}
