/* IIRDesigner.java created 2010-09-16
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

/**
 * This class represents a designer capable of designing digital filters, i.e. calculating
 * the {@link FilterCoefficients} according to the given {@link TimeDomainSampleFilter
 * specification}.
 *
 * @author Piotr Szachewicz
 */
public class IIRDesigner {

	/**
	 * Returs the {@link FilterCoefficients coefficients} of a digital filter
	 * which meets the given filter specification.
	 *
	 * @param approximationFunctionType the type of the approximation function which
	 * should be used to design the filter.
	 * @param type the type of the filter (lowpass, highpass, bandpass or bandstop)
	 * @param passbandEdgeFrequencies the passband edge frequencies
	 * @param stopbandEdgeFrequencies the stopband edge frequencies
	 * @param passbandRipple maximum ripple allowed for the filter to have in the passband [dB]
	 * @param stopbandAttenuation minimum attenuation required for the filter to have in the stopband [dB]
	 * @param samplingFrequency the sampling frequency at which the filter will operate
	 * @return the {@link FilterCoefficients} of the filter designed which meets given specification.
	 */
	public static FilterCoefficients designDigitalFilter(ApproximationFunctionType approximationFunctionType, FilterType type, double[] passbandEdgeFrequencies, double[] stopbandEdgeFrequencies, double passbandRipple, double stopbandAttenuation, double samplingFrequency) throws BadFilterParametersException {

		if (approximationFunctionType.isButterworth()) {

			ButterworthIIRDesigner iirdesigner = new ButterworthIIRDesigner();
			return iirdesigner.designDigitalFilter(samplingFrequency, type, passbandEdgeFrequencies, stopbandEdgeFrequencies, passbandRipple, passbandRipple);

		}
		else if (approximationFunctionType.isChebyshev1()) {

			Chebyshev1IIRDesigner iirdesigner = new Chebyshev1IIRDesigner();
			return iirdesigner.designDigitalFilter(samplingFrequency, type, passbandEdgeFrequencies, stopbandEdgeFrequencies, passbandRipple, passbandRipple);

		}
		else if (approximationFunctionType.isChebyshev2()) {

			Chebyshev2IIRDesigner iirdesigner = new Chebyshev2IIRDesigner();
			return iirdesigner.designDigitalFilter(samplingFrequency, type, passbandEdgeFrequencies, stopbandEdgeFrequencies, passbandRipple, passbandRipple);

		}
		else if (approximationFunctionType.isElliptic()) {

			EllipticIIRDesigner iirdesigner = new EllipticIIRDesigner();
			return iirdesigner.designDigitalFilter(samplingFrequency, type, passbandEdgeFrequencies, stopbandEdgeFrequencies, passbandRipple, passbandRipple);

		}
		else
			throw new BadFilterParametersException("This approximation function type is not supported by the IIRFilterDesigner.");

	}

}