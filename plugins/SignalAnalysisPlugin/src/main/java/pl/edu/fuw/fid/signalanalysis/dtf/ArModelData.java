package pl.edu.fuw.fid.signalanalysis.dtf;

/**
 * Spectral function x(f), consisting of array of values,
 * one for every given frequency.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ArModelData {

	public final int length;
	public final double[] freqcs;
	public final double[] values;

	public ArModelData(int length) {
		this.length = length;
		this.freqcs = new double[length];
		this.values = new double[length];
	}

}
