/* ExampleMethodDialog.java created 2007-10-22
 *
 */

package org.signalml.app.method.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodConfigurer;
import org.signalml.method.Method;
import org.signalml.method.example.ExampleData;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;

/** ExampleMethodDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExampleMethodDialog extends AbstractDialog implements InitializingMethodConfigurer {

	private static final long serialVersionUID = 1L;

	private JTextField textField;
	private JCheckBox noWaitCheckBox;

	public ExampleMethodDialog(MessageSourceAccessor messageSource,Window window) {
		super(messageSource, window,true);
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		ExampleData ed = (ExampleData) model;
		textField.setText(ed.getCount().toString());
		noWaitCheckBox.setSelected(ed.isNoWait());
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		ExampleData ed = (ExampleData) model;
		ed.setCount(new Integer(textField.getText()));
		ed.setNoWait(noWaitCheckBox.isSelected());
	}

	@Override
	public JComponent createInterface() {

		JLabel label = new JLabel(messageSource.getMessage("exampleMethod.count"));
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(150,20));
		textField.setHorizontalAlignment(JTextField.RIGHT);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.add(label);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(textField);

		JLabel noWaitLabel = new JLabel(messageSource.getMessage("exampleMethod.noWait"));
		noWaitCheckBox = new JCheckBox();

		JPanel noWaitPanel = new JPanel();
		noWaitPanel.setLayout(new BoxLayout(noWaitPanel,BoxLayout.X_AXIS));
		noWaitPanel.add(noWaitLabel);
		panel.add(Box.createHorizontalStrut(5));
		noWaitPanel.add(Box.createHorizontalGlue());
		noWaitPanel.add(noWaitCheckBox);

		JPanel interfacePanel = new JPanel(new BorderLayout(3,3));
		CompoundBorder cb = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("exampleMethod.configure")),
		        new EmptyBorder(3,3,3,3)
		);
		interfacePanel.setBorder(cb);

		interfacePanel.add(panel, BorderLayout.CENTER);
		interfacePanel.add(noWaitPanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	@Override
	public void initialize(ApplicationMethodManager manager) {
		// do nothing
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("exampleMethod.configure"));
		super.initialize();
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ExampleData.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean configure(Method method, Object methodDataObj) throws SignalMLException {
		return showDialog(methodDataObj, true);
	}

}