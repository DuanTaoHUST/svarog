package org.signalml.plugin.fftsignaltool;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.signalml.plugin.export.view.AbstractSignalMLAction;

public class SignalFFTPopupAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	private SignalFFTTool tool;

	private double[] powerSpectrum;

	private double[] frequencies;

	private static FFTSignalToolI18nDelegate i18nDelegate;

	protected static final Logger logger = Logger
			   .getLogger(SignalFFTPopupAction.class);
	
	public SignalFFTPopupAction(FFTSignalToolI18nDelegate i18nDelegate) {
		super();
		this.i18nDelegate = i18nDelegate;
		setText("Export FFT to CSV");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		logger.debug("Saving FFT in CSV file");
		if (powerSpectrum == null || frequencies == null) {
			JOptionPane.showMessageDialog(null,
					_("First you have to comupte FFT."),
					_("No FFT computed"),
					JOptionPane.ERROR_MESSAGE);
			logger.debug("No FFT computed");
			return;
		}
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
		fc.setMultiSelectionEnabled(false);
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File csvFile = fc.getSelectedFile();
			try{
				if (!csvFile.getName().toLowerCase().endsWith(".csv"))
					csvFile = new File(csvFile.getAbsolutePath() + ".csv");
				if (!csvFile.exists())
					csvFile.createNewFile();
				String delimiter = (String)JOptionPane.showInputDialog(
	                    null,
	                    _("Type delimiter"),
	                    _("Delimiter selection"),
	                    JOptionPane.QUESTION_MESSAGE,
	                    null,
	                    null,
	                    ";");
				if (delimiter != null && delimiter.length() > 0) {
					logger.debug("Writing FFT to " + csvFile.getCanonicalPath() + " with " + delimiter + " as a delimiter");
					FileWriter fw = new FileWriter(csvFile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					for (int i = 0; i < powerSpectrum.length; ++i)
						bw.write("" + frequencies[i] + delimiter + powerSpectrum[i] + "\n");
					bw.close();
					return;
				}
			} catch (IOException ex) {
				logger.error("IO error during FFT saving");
			}
		}
	}

	public void setPowerAndFrequencies(double[] powerSpectrum,
			double[] frequencies) {
		this.powerSpectrum = powerSpectrum;
		this.frequencies = frequencies;
	}
	
	public static String _(String msgKey) {
		return i18nDelegate._(msgKey);
	}

}
