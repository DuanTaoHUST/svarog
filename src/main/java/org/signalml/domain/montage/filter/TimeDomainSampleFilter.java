/* TimeDomainSampleFilter.java created 2008-02-01
 *
 */

package org.signalml.domain.montage.filter;

import org.signalml.util.ResolvableString;
import org.springframework.context.MessageSourceResolvable;

/**
 * This abstract class holds description of a
 * {@link SampleFilterDefinition sample filter} in a time domain.
 * Contains filters feedback and feedforward coefficients.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class TimeDomainSampleFilter extends SampleFilterDefinition {

	private static final long serialVersionUID = 1L;

	private final static Object[] ARGUMENTS = new Object[0];

	// these must be built by the constructor
	protected transient String[] messageCodes;
	protected transient String[] effectCodes;
	protected transient String defaultEffectDescription;

        /**
         * an array of filter feedback coefficients
         */
	protected transient double aCoefficients[];

        /**
         * an array of filter feedforward coefficients
         */
	protected transient double bCoefficients[];

	protected transient int margin;

        /**
         * the number of sequential passes of this filter
         */
	protected int passCount;

        /**
         * Returns an array of filters feedback coefficients.
         * @return an array of filters feedback coefficients
         */
	public double[] getACoefficients() {
		return aCoefficients;
	}

        /**
         * Returns an array of filters feedforward coefficients.
         * @return an array of filters feedforward coefficients
         */
	public double[] getBCoefficients() {
		return bCoefficients;
	}

	public int getMargin() {
		return margin;
	}

        /**
         * Returns the number of sequential passes of this filter.
         * @return the number of sequential passes of this filter
         */
	public int getPassCount() {
		return passCount;
	}

        /**
         * Sets the number of sequential passes of this filter.
         * @param passCount the number of sequential passes to be set
         */
	public void setPassCount(int passCount) {
		this.passCount = passCount;
	}

	@Override
	public MessageSourceResolvable getEffectDescription() {
		return new ResolvableString(effectCodes, new Object[] { passCount }, defaultEffectDescription);
	}

	@Override
	public String getDefaultEffectDescription() {
		return defaultEffectDescription;
	}

	@Override
	public SampleFilterType getType() {
		return SampleFilterType.TIME_DOMAIN;
	}

	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return messageCodes;
	}

	@Override
	public String getDefaultMessage() {
		return "Time domain filter " + getClass().getSimpleName();
	}

}
