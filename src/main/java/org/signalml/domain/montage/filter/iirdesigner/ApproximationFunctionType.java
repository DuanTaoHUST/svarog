/* ApproximationFunctionType.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.springframework.context.MessageSourceResolvable;

/**
 * This enumeration type represents the type of the approximation function which
 * is used to design (i. e. calculate the coefficients for) a {@link TimeDomainSampleFilter}.
 *
 * @author Piotr Szachewicz
 */
public enum ApproximationFunctionType implements MessageSourceResolvable {

	BUTTERWORTH("butterworth"),
	CHEBYSHEV1("chebyshev1"),
	CHEBYSHEV2("chebyshev2"),
	ELLIPTIC("elliptic");

	private String name;

	private ApproximationFunctionType(String name) {
		this.name = name;
	}

	public boolean isButterworth() {
		return (this == BUTTERWORTH);
	}

	public boolean isChebyshev1() {
		return (this == CHEBYSHEV1);
	}

	public boolean isChebyshev2() {
		return (this == CHEBYSHEV2);
	}

	public boolean isElliptic() {
		return (this == ELLIPTIC);
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "signalSelectionType." + name };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}

}