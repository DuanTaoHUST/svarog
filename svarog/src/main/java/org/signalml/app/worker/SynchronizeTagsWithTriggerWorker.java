package org.signalml.app.worker;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.tag.SlopeType;
import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import org.signalml.domain.signal.samplesource.OriginalMultichannelSampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.Tag;

/**
 * A worker which synchronizes tags with trigger for a given document.
 *
 * @author Piotr Szachewicz
 */
public class SynchronizeTagsWithTriggerWorker extends SwingWorkerWithBusyDialog<Void, Object> {

	private static final int BUFFER_SIZE = 1024;

	private SynchronizeTagsWithTriggerParameters parameters;
	private double threshold;
	private SignalDocument signalDocument;
	private StyledTagSet tagSet;

	public SynchronizeTagsWithTriggerWorker(SynchronizeTagsWithTriggerParameters model) {
		super(null);
		this.parameters = model;
		this.threshold = model.getThresholdValue();
		this.signalDocument = model.getSignalDocument();
		this.tagSet = signalDocument.getActiveTag().getTagSet();

		getBusyDialog().setText(_("Synchronizing tags with trigger."));
		getBusyDialog().setCancellable(false);
	}

	@Override
	protected Void doInBackground() throws Exception {
		showBusyDialog();
		OriginalMultichannelSampleSource sampleSource = signalDocument.getSampleSource();

		double[] samples = new double[BUFFER_SIZE];
		int sampleCount = sampleSource.getSampleCount(0);

		int currentSample = 0;
		for (Tag tag: tagSet.getTags()) {
			boolean tagRepositioned = false;

			while(!tagRepositioned && currentSample < sampleCount) {

				int bufferSize = BUFFER_SIZE;
				if (currentSample + bufferSize > sampleCount)
					bufferSize = sampleCount - currentSample;

				sampleSource.getSamples(parameters.getTriggerChannel(), samples, currentSample, bufferSize, 0);

				int i = 1;
				for (; i < samples.length-1; i++) {
					if (isSlopeActivating(samples[i], samples[i+1])) {
						double time = ((double)currentSample + i) / signalDocument.getSamplingFrequency();
						tag.setPosition(time);
						tagRepositioned = true;
						tagSet.editTag(tag);
						break;
					}
				}
				currentSample += i+1;

			}

		}

		return null;
	}

	protected boolean isSlopeActivating(double currentSample, double nextSample) {
		SlopeType slopeType = parameters.getSlopeType();

		switch(slopeType) {
		case ASCENDING: return isSlopeAscending(currentSample, nextSample);
		case DESCENDING: return isSlopeDescending(currentSample, nextSample);
		case BOTH:
			return isSlopeAscending(currentSample, nextSample)
				|| isSlopeDescending(currentSample, nextSample);
		}
		return false;

	}

	protected boolean isSlopeAscending(double currentSample, double nextSample) {
		return (nextSample >= threshold && currentSample < threshold);
	}

	protected boolean isSlopeDescending(double currentSample, double nextSample) {
		return (nextSample < threshold && currentSample >= threshold);
	}

}
