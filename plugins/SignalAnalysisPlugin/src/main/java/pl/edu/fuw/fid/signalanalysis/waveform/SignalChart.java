package pl.edu.fuw.fid.signalanalysis.waveform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.apache.commons.math.complex.Complex;
import org.signalml.math.fft.WindowType;
import pl.edu.fuw.fid.signalanalysis.SingleSignal;

/**
 * Chart for the signal displayed in interactive time-frequency map window.
 * Allows to overlay waveform over the signal, in distinctive colour.
 *
 * @author ptr@mimuw.edu.pl
 */
public class SignalChart extends LineChart<Number, Number> {

	private static final String SIGNAL_STYLE = "-fx-stroke-width: 1; -fx-stroke: #000000;";
	private static final String WAVEFORM_STYLE = "-fx-stroke-width: 2; -fx-stroke: #FF0000;";

	private final double[] data;
	private final double samplingFrequency;
	private final ObservableList<XYChart.Series<Number,Number>> series;

	public SignalChart(SingleSignal signal, double tMin, double tMax, NumberAxis xAxis, NumberAxis yAxis) {
		super(xAxis, yAxis);
		int nMin = (int) Math.round(tMin * signal.getSamplingFrequency());
		int nMax = (int) Math.round(tMax * signal.getSamplingFrequency());
		this.data = new double[nMax - nMin];
		this.samplingFrequency = signal.getSamplingFrequency();
		signal.getSamples(nMin, nMax-nMin, data);

		ObservableList<XYChart.Data<Number,Number>> signalData = FXCollections.observableArrayList();
		for (int i=0; i<data.length; ++i) {
			double t = tMin + (tMax - tMin) * i / data.length;
			signalData.add(new XYChart.Data<Number,Number>(t, data[i]));
		}

		ObservableList<XYChart.Data<Number,Number>> waveformData = FXCollections.emptyObservableList();
		this.series = FXCollections.observableArrayList(
			createDataSeries(signalData, SIGNAL_STYLE),
			new XYChart.Series<Number, Number>(waveformData)
		);
		setAnimated(false);
		setCreateSymbols(false);
		setLegendVisible(false);
		setPrefHeight(100);
		setData(series);
	}

	private static XYChart.Series<Number, Number> createDataSeries(ObservableList<XYChart.Data<Number,Number>> data, final String style) {
		XYChart.Series<Number, Number> serie = new XYChart.Series<Number, Number>(data);
		serie.nodeProperty().addListener(new ChangeListener<Node>() {
			@Override
			public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
				if (newValue != null) {
					newValue.setStyle(style);
				}
			}
		});
		return serie;
	}

	public void setWaveform(Waveform wf, WindowType windowType, double t0, Complex coeff) {
		ObservableList<XYChart.Data<Number,Number>> waveformData = WaveformRenderer.compute(wf, t0, windowType, coeff, samplingFrequency);
		series.set(1, createDataSeries(waveformData, WAVEFORM_STYLE));
	}

	public void clearWaveform() {
		ObservableList<XYChart.Data<Number,Number>> waveformData = FXCollections.emptyObservableList();
		series.set(1, new XYChart.Series<Number, Number>(waveformData));
	}

}
