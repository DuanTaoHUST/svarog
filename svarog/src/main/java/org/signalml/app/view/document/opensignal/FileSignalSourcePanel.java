/* FileSignalSourcePanel.java created 2011-03-06
 *
 */

package org.signalml.app.view.document.opensignal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JPanel;
import org.signalml.app.config.ApplicationConfiguration;

import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalMLDocument;
import org.signalml.app.model.document.opensignal.OpenFileSignalDescriptor;

import org.signalml.app.view.components.FileChooserPanel;
import org.signalml.app.view.components.dialogs.PleaseWaitDialog;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.worker.document.OpenSignalMLDocumentWorker;
import org.signalml.codec.SignalMLCodec;
import org.signalml.domain.montage.system.EegSystemName;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.plugin.export.SignalMLException;

/**
 * The panel for choosing a file and setting parameters using which the signal
 * file should be opened.
 *
 * @author Piotr Szachewicz
 */
public class FileSignalSourcePanel extends AbstractSignalSourcePanel {

	/**
	 * The panel for choosing a file to be opened.
	 */
	private FileChooserPanel fileChooserPanel;

	/**
	 * The panel for selecting using which method the file should be
	 * opened (is it a raw signal or should a SignalML codec be used).
	 */
	private FileOpenMethodPanel fileOpenMethodPanel;

	/**
	 * A card panel for showing different signal parameters for each
	 * fileOpenMethod.
	 */
	private JPanel cardPanelForSignalParameters;

	/**
	 * The panel for changing parameters for a raw signal document.
	 */
	private SignalParametersPanelForRawSignalFile rawSignalParametersPanel;

	/**
	 * The panel for changing parameters for a SignalML document.
	 */
	private SignalParametersPanelForSignalMLSignalFile signalMLSignalParametersPanel;

	/**
	 * Constructor.
	 * @param viewerElementManager ViewerElementManager to be used by this
	 * panel
	 */
	public FileSignalSourcePanel(ViewerElementManager viewerElementManager) {
		super(viewerElementManager);

	}

	@Override
	protected JPanel createLeftColumnPanel() {
		JPanel leftColumnPanel = new JPanel();
		leftColumnPanel.setLayout(new BorderLayout());
		leftColumnPanel.add(getFileChooserPanel());

		return leftColumnPanel;
	}

	@Override
	protected JPanel createRightColumnPanel() {
		JPanel rightColumnPanel = new JPanel(new BorderLayout());
		rightColumnPanel.add(getFileOpenMethodPanel(), BorderLayout.NORTH);
		rightColumnPanel.add(getCardPanelForSignalParameters(), BorderLayout.CENTER);
		rightColumnPanel.add(getEegSystemSelectionPanel(), BorderLayout.SOUTH);
		return rightColumnPanel;
	}

	/**
	 * Fills the given descriptor using the data set in the components contained
	 * in this panel.
	 * 
	 * @param openFileSignalDescriptor
	 *            the descriptor to be filled
	 */
	public void fillPanelFromModel(OpenFileSignalDescriptor openFileSignalDescriptor) {

		FileOpenSignalMethod method = openFileSignalDescriptor.getMethod();

		fileOpenMethodPanel.setSelectedOpenSignalMethod(method);
		if (method.isRaw()) {
			rawSignalParametersPanel.fillPanelFromModel(openFileSignalDescriptor.getRawSignalDescriptor());
		} else if (method.isSignalML()) {
			getSignalMLSignalParametersPanel().fillPanelFromModel(openFileSignalDescriptor);
		}
		
		String lastFileChooserPath = this.getViewerElementManager().getApplicationConfig().getLastFileChooserPath();
		this.getFileChooserPanel().getFileChooser().setCurrentDirectory(new File(lastFileChooserPath));
	}

	/**
	 * Fills the components in this panel using the data contained in the
	 * given descriptor.
	 * @param descriptor descriptor to be used to filled this panel
	 */
	public void fillModelFromPanel(OpenFileSignalDescriptor descriptor) {
		FileOpenSignalMethod method = fileOpenMethodPanel.getSelectedOpenSignalMethod();
		if (method.isRaw()) {

			RawSignalDescriptor rawSignalDescriptor = descriptor.getRawSignalDescriptor();
			rawSignalParametersPanel.fillModelFromPanel(rawSignalDescriptor);

		} else if (method.isSignalML()) {

			descriptor.setMethod(FileOpenSignalMethod.SIGNALML);
			getSignalMLSignalParametersPanel().fillModelFromPanel(descriptor);
		}

		File selectedFile = fileChooserPanel.getSelectedFile();
		if(selectedFile.exists())
			this.viewerElementManager.getApplicationConfig().setLastFileChooserPath(selectedFile.getParentFile().getPath());
		descriptor.setFile(selectedFile);

		getApplicationConfiguration().setLastFileChooserPath(getFileChooserPanel().getFileChooser().getCurrentDirectory().getAbsolutePath());
	}

	/**
	 * Returns the panel for choosing which signal file should be opened.
	 * @return the panel for choosing which signal file should be opened
	 */
	public FileChooserPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			fileChooserPanel = new FileChooserPanel( ManagedDocumentType.SIGNAL, getApplicationConfiguration());

			String lastFileChooserPath = getApplicationConfiguration().getLastFileChooserPath();
			getFileChooserPanel().getFileChooser().setCurrentDirectory(new File(lastFileChooserPath));
		}
		return fileChooserPanel;
	}

	/**
	 * Returns the panel for choosing which method should be used to open
	 * the selected signal file.
	 * @return the panel for choosing which method should be used to open
	 * the selected signal file.
	 */
	protected FileOpenMethodPanel getFileOpenMethodPanel() {
		if (fileOpenMethodPanel == null) {
			fileOpenMethodPanel = new FileOpenMethodPanel();
			fileOpenMethodPanel.addPropertyChangeListener(this);
		}
		return fileOpenMethodPanel;
	}

	/**
	 * Returns the card panel for showing different signal parameters for each
	 * fileOpenMethod.
	 * @return the card panel for showing different signal parameters for each
	 * fileOpenMethod
	 */
	public JPanel getCardPanelForSignalParameters() {
		if (cardPanelForSignalParameters == null) {
			cardPanelForSignalParameters = new JPanel(new CardLayout());
			cardPanelForSignalParameters.add(getRawSignalParametersPanel(), FileOpenSignalMethod.RAW.toString());
			cardPanelForSignalParameters.add(getSignalMLSignalParametersPanel(), FileOpenSignalMethod.SIGNALML.toString());
		}
		return cardPanelForSignalParameters;
	}

	/**
	 * Returns the panel for setting parameters for a raw signal.
	 * @return the panel for setting parameters for a raw signal
	 */
	public SignalParametersPanelForRawSignalFile getRawSignalParametersPanel() {
		if (rawSignalParametersPanel == null) {
			rawSignalParametersPanel = new SignalParametersPanelForRawSignalFile();
			rawSignalParametersPanel.setApplicationConfiguration(getApplicationConfiguration());
			rawSignalParametersPanel.addPropertyChangeListener(this);
			rawSignalParametersPanel.setSignalFileChooser(getFileChooserPanel().getFileChooser());
		}
		return rawSignalParametersPanel;
	}

	/**
	 * Returns the panel for choosing codec for a SignalML document.
	 * @return the panel for choosing codec for a SignalML document
	 */
	public SignalParametersPanelForSignalMLSignalFile getSignalMLSignalParametersPanel() {
		if (signalMLSignalParametersPanel == null) {
			signalMLSignalParametersPanel = new SignalParametersPanelForSignalMLSignalFile( getViewerElementManager());
			signalMLSignalParametersPanel.addPropertyChangeListener(this);
		}
		return signalMLSignalParametersPanel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if (propertyName.equals(SignalParametersPanelForSignalMLSignalFile.LOAD_METADATA_ACTION_PROPERTY)) {
			loadSignalMLMetadata();
		}
		else if (propertyName.equals(FileOpenMethodPanel.FILE_OPEN_METHOD_PROPERTY_CHANGED)) {
			FileOpenSignalMethod method = (FileOpenSignalMethod) evt.getNewValue();

			CardLayout cl = (CardLayout)(cardPanelForSignalParameters.getLayout());
			cl.show(cardPanelForSignalParameters, method.toString());
		} else if (evt.getSource() instanceof SignalParametersPanelForRawSignalFile
			&& propertyName.equals(AbstractSignalParametersPanel.EEG_SYSTEM_PROPERTY)) {
			EegSystemName eegSystemName = (EegSystemName) (evt.getNewValue() == null ? null : evt.getNewValue());
			if (eegSystemName != null)
				getEegSystemSelectionPanel().setEegSystemByName(eegSystemName);
			else
				getEegSystemSelectionPanel().setEegSystem(getEegSystemSelectionPanel().getSelectedEegSystem());
		} else
			forwardPropertyChange(evt);
	}

	protected void loadSignalMLMetadata() {
		SignalMLCodec codec = (SignalMLCodec) signalMLSignalParametersPanel.getSignalMLOptionsPanel().getSignalMLDriverComboBox().getSelectedItem();
		File file = getFileChooserPanel().getSelectedFile();

		if (codec == null) {
			Dialogs.showError(_("Please select a codec first!"));
			return;
		}
		if (file == null) {
			Dialogs.showError(_("Please select a signalML file first!"));
			return;
		}

		OpenSignalMLDocumentWorker worker = new OpenSignalMLDocumentWorker(codec, file);
		worker.execute();

		SignalMLDocument signalMLDocument = null;
		try {
			signalMLDocument = worker.get();

			int channelCount = signalMLDocument.getChannelCount();
			float samplingFrequency = signalMLDocument.getSamplingFrequency();

			String[] channelLabels = new String[channelCount];
			for (int i = 0; i < channelCount; i++) {
				channelLabels[i] = signalMLDocument.getSampleSource().getLabel(i);
			}

			this.fireNumberOfChannelsChangedProperty(channelCount);

			this.propertyChangeSupport.firePropertyChange(AbstractSignalParametersPanel.CHANNEL_LABELS_PROPERTY, null, channelLabels);
			this.propertyChangeSupport.firePropertyChange(AbstractSignalParametersPanel.SAMPLING_FREQUENCY_PROPERTY, null, samplingFrequency);
			getEegSystemSelectionPanel().fireEegSystemChangedProperty();

			signalMLDocument.closeDocument();
		} catch (Exception e) {
			Dialogs.showError(_("There was an error while loading the file - did you select a correct SignalML file?"));
			e.printStackTrace();
		}
	}

	@Override
	public boolean isMetadataFilled() {
		return true;
	}

	@Override
	public int getChannelCount() {
		return rawSignalParametersPanel.getChannelCount();
	}

	@Override
	public float getSamplingFrequency() {
		return rawSignalParametersPanel.getSamplingFrequency();
	}

	/**
	 * Returns the Svarog application configuration.
	 * @return the Svarog application configuration
	 */
	protected ApplicationConfiguration getApplicationConfiguration() {
		return viewerElementManager.getApplicationConfig();
	}

	@Override
	public void setSamplingFrequency(float samplingFrequency) {
		getRawSignalParametersPanel().getSamplingFrequencyComboBox().setSelectedItem(samplingFrequency);
	}

}