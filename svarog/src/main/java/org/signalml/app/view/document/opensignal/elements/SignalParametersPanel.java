package org.signalml.app.view.document.opensignal.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.app.view.common.components.ResolvableComboBox;
import org.signalml.app.view.common.components.spinners.FloatSpinner;
import org.signalml.app.view.common.components.spinners.IntegerSpinner;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;

/**
 * Panel allowing to present and edit signal parameters.
 *
 * @author Tomasz Sawicki
 * @author Piotr Szachewicz
 */
public class SignalParametersPanel extends JPanel {

	public static final String NUMBER_OF_CHANNELS_PROPERTY = "numberOfChannelsChangedProperty";
	public static final int CHANNEL_COUNT_MAX = 1024;

	protected AbstractOpenSignalDescriptor openSignalDescriptor;

	private JComboBox samplingFrequencyComboBox;
	private IntegerSpinner channelCountSpinner;
	private ResolvableComboBox byteOrderComboBox;
	private ResolvableComboBox sampleTypeComboBox;
	private FloatSpinner pageSizeSpinner;
	private IntegerSpinner blocksPerPageSpinner;

	/**
	 * Default constructor. Creates the interface.
	 *
	 */
	public SignalParametersPanel() {
		super();
		createInterface();
	}

	/**
	 * Creates the interface.
	 */
	private void createInterface() {

		setBorder(new CompoundBorder(new TitledBorder(_("Signal parameters")),
									 new EmptyBorder(3, 3, 3, 3)));

		setLayout(new BorderLayout(0, 10));

		JPanel fieldsPanel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(8, 8, 8, 8);

		this.createFieldsPanelElements(fieldsPanel, constraints, 0);

		add(fieldsPanel);

		setEnabledToAll(false);
	}

	/**
	 * Fills a {@link JPanel} object with component, regarding
	 * {@link GridBagConstraints} object.
	 *
	 * @param fieldsPanel
	 *            an object to be filled with components
	 * @param constraints
	 *            an object regarding to which components should be placed in
	 *            fieldsPanel
	 * @param startingRow
	 *            a row to start from
	 *
	 * @return a number of rows that fieldsPanel contains after performing all
	 *         operations
	 */

	protected int createFieldsPanelElements(JPanel fieldsPanel,
											GridBagConstraints constraints, int startingRow) {
		JLabel samplingFrequencyLabel = new JLabel(_("Sampling frequency: "));
		JLabel channelCountLabel = new JLabel(_("Channel count: "));
		JLabel byteOrderLabel = new JLabel(_("Byte order: "));
		JLabel sampleTypeLabel = new JLabel(_("Sample type: "));
		JLabel pageSizeLabel = new JLabel(_("Page size: "));
		JLabel blocksPerPageLabel = new JLabel(_("Number of blocks per page: "));

		int row = startingRow;
		fillConstraints(constraints, 0, row, 0, 0, 1);
		fieldsPanel.add(samplingFrequencyLabel, constraints);
		fillConstraints(constraints, 1, row, 1, 0, 1);
		fieldsPanel.add(getSamplingFrequencyComboBox(), constraints);
		row++;

		fillConstraints(constraints, 0, row, 0, 0, 1);
		fieldsPanel.add(channelCountLabel, constraints);
		fillConstraints(constraints, 1, row, 1, 0, 1);
		fieldsPanel.add(getChannelCountSpinner(), constraints);
		row++;

		fillConstraints(constraints, 0, row, 0, 0, 1);
		fieldsPanel.add(byteOrderLabel, constraints);
		fillConstraints(constraints, 1, row, 1, 0, 1);
		fieldsPanel.add(getByteOrderComboBox(), constraints);
		row++;

		fillConstraints(constraints, 0, row, 0, 0, 1);
		fieldsPanel.add(sampleTypeLabel, constraints);
		fillConstraints(constraints, 1, row, 1, 0, 1);
		fieldsPanel.add(getSampleTypeComboBox(), constraints);
		row++;

		fillConstraints(constraints, 0, row, 0, 0, 1);
		fieldsPanel.add(pageSizeLabel, constraints);
		fillConstraints(constraints, 1, row, 1, 0, 1);
		fieldsPanel.add(getPageSizeSpinner(), constraints);
		row++;

		fillConstraints(constraints, 0, row, 0, 0, 1);
		fieldsPanel.add(blocksPerPageLabel, constraints);
		fillConstraints(constraints, 1, row, 1, 0, 1);
		fieldsPanel.add(getBlocksPerPageSpinner(), constraints);
		row++;
		return row;

	}

	/**
	 * Method fills a {@link GridBagConstraints} object with data.
	 *
	 * @param constraints
	 *            a {@link GridBagConstraints}
	 * @param gridx
	 *            {@link GridBagConstraints#gridx}
	 * @param gridy
	 *            {@link GridBagConstraints#gridy}
	 * @param weightx
	 *            {@link GridBagConstraints#weightx}
	 * @param weighty
	 *            {@link GridBagConstraints#weighty}
	 * @param gridwidth
	 *            {@link GridBagConstraints#gridwidth}
	 */
	protected void fillConstraints(GridBagConstraints constraints, int gridx,
								   int gridy, int weightx, int weighty, int gridwidth) {

		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		constraints.gridwidth = gridwidth;
	}

	/**
	 * Returns the sampling frequency combo box.
	 *
	 * @return the sampling frequency combo box
	 */
	public JComboBox getSamplingFrequencyComboBox() {

		if (samplingFrequencyComboBox == null) {
			samplingFrequencyComboBox = new JComboBox();
			samplingFrequencyComboBox.setEditable(true);
		}
		return samplingFrequencyComboBox;
	}

	/**
	 * Returns the channel count field
	 *
	 * @return the channel count field
	 */
	protected IntegerSpinner getChannelCountSpinner() {

		if (channelCountSpinner == null) {
			channelCountSpinner = new IntegerSpinner(new SpinnerNumberModel(4,
					1, CHANNEL_COUNT_MAX, 1));
			channelCountSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					int numberOfChannels = getChannelCountSpinner().getValue();
					fireNumberOfChannelsChanged(numberOfChannels);
				}
			});

		}
		return channelCountSpinner;
	}

	/**
	 * Notifies all listeners that the number of channels has changed.
	 *
	 * @param numberOfChannels
	 *            the new number of channels
	 */
	protected void fireNumberOfChannelsChanged(int numberOfChannels) {
		firePropertyChange(NUMBER_OF_CHANNELS_PROPERTY, 0, numberOfChannels);
	}

	/**
	 * Returns the byte order combo box.
	 *
	 * @return the byte order combo box
	 */
	protected ResolvableComboBox getByteOrderComboBox() {

		if (byteOrderComboBox == null) {
			byteOrderComboBox = new ResolvableComboBox();
			byteOrderComboBox.setModel(new DefaultComboBoxModel(
										   RawSignalByteOrder.values()));
		}
		return byteOrderComboBox;
	}

	/**
	 * Returns the sample type combo box.
	 *
	 * @return the sample type combo box
	 */
	protected ResolvableComboBox getSampleTypeComboBox() {

		if (sampleTypeComboBox == null) {
			sampleTypeComboBox = new ResolvableComboBox();
			sampleTypeComboBox.setModel(new DefaultComboBoxModel(
											RawSignalSampleType.values()));
		}
		return sampleTypeComboBox;
	}

	/**
	 * Returns the page size spinner.
	 *
	 * @return the page size spinner
	 */
	protected FloatSpinner getPageSizeSpinner() {

		if (pageSizeSpinner == null) {
			pageSizeSpinner = new FloatSpinner(new SpinnerNumberModel(20.0F,
											   0.1F, 100000.0F, 0.1F));
		}
		return pageSizeSpinner;
	}

	/**
	 * Returns the blocks per page field.
	 *
	 * @return the blocks per page field
	 */
	protected IntegerSpinner getBlocksPerPageSpinner() {

		if (blocksPerPageSpinner == null) {
			blocksPerPageSpinner = new IntegerSpinner(new SpinnerNumberModel(4,
					1, 200, 1));
		}
		return blocksPerPageSpinner;
	}


	public int getChannelCount() {
		return getChannelCountSpinner().getValue();
	}

	public float getSamplingFrequency() {
		return Float.parseFloat(getSamplingFrequencyComboBox()
								.getSelectedItem().toString());
	}

	protected void setEnabledAsNeeded(AbstractOpenSignalDescriptor openSignalDescriptor) {

		if (openSignalDescriptor == null) {
			getSamplingFrequencyComboBox().setSelectedIndex(-1);
			getChannelCountSpinner().setValue(0);
			setEnabledToAll(false);
			return;
		}

		if (openSignalDescriptor instanceof RawSignalDescriptor) {
			setEnabledToAll(true);
			getSamplingFrequencyComboBox().setEditable(true);
		}
		else if (openSignalDescriptor instanceof ExperimentDescriptor) {
			ExperimentDescriptor experiment = (ExperimentDescriptor) openSignalDescriptor;

			if (experiment.getStatus() == ExperimentStatus.NEW) {
				getSamplingFrequencyComboBox().setEnabled(true);
				getSamplingFrequencyComboBox().setEditable(false);
			} else {
				setEnabledToAll(false);
			}
		}

	}

	protected void setEnabledToAll(boolean enabled) {
		getSamplingFrequencyComboBox().setEnabled(enabled);
		getChannelCountSpinner().setEnabled(enabled);
		getByteOrderComboBox().setEnabled(enabled);
		getSampleTypeComboBox().setEnabled(enabled);
	}

	public void fillPanelFromModel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		this.openSignalDescriptor = openSignalDescriptor;

		setEnabledAsNeeded(openSignalDescriptor);
		if (openSignalDescriptor == null)
			return;

		if (openSignalDescriptor instanceof RawSignalDescriptor) {
			RawSignalDescriptor rawSignalDescriptor = (RawSignalDescriptor) openSignalDescriptor;

			if (rawSignalDescriptor.getByteOrder() != null)
				getByteOrderComboBox().setSelectedItem(rawSignalDescriptor.getByteOrder());
			if (rawSignalDescriptor.getSampleType() != null)
				getSampleTypeComboBox().setSelectedItem(rawSignalDescriptor.getSampleType());
		} else if (openSignalDescriptor instanceof ExperimentDescriptor) {
			ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) openSignalDescriptor;
			List<Float> samplingFrequencies = experimentDescriptor.getAmplifier().getSamplingFrequencies();
			getSamplingFrequencyComboBox().setModel(new DefaultComboBoxModel(samplingFrequencies.toArray()));

			float samplingFrequency = experimentDescriptor.getSignalParameters().getSamplingFrequency();
			getSampleTypeComboBox().setSelectedItem(samplingFrequency);
		}
		SignalParameters signalParameters = openSignalDescriptor.getSignalParameters();

		getSamplingFrequencyComboBox().setSelectedItem(signalParameters.getSamplingFrequency());
		getChannelCountSpinner().setValue(signalParameters.getChannelCount());
		getPageSizeSpinner().setValue(signalParameters.getPageSize());
		getBlocksPerPageSpinner().setValue(signalParameters.getBlocksPerPage());

		fireNumberOfChannelsChanged(signalParameters.getChannelCount());
	}

	public void fillModelFromPanel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		if (openSignalDescriptor instanceof RawSignalDescriptor) {
			RawSignalDescriptor rawSignalDescriptor = (RawSignalDescriptor) openSignalDescriptor;
			rawSignalDescriptor.setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());
			rawSignalDescriptor.setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());
		}

		SignalParameters signalParameters = openSignalDescriptor.getSignalParameters();
		signalParameters.setSamplingFrequency(getSamplingFrequency());
		signalParameters.setChannelCount(getChannelCount());
		signalParameters.setPageSize(getPageSizeSpinner().getValue());
		signalParameters.setBlocksPerPage(getBlocksPerPageSpinner().getValue());

		if (openSignalDescriptor instanceof ExperimentDescriptor) {
			ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) openSignalDescriptor;
			signalParameters.setChannelCount(experimentDescriptor
											 .getAmplifier().getSelectedChannels().size());
		}
	}

}
