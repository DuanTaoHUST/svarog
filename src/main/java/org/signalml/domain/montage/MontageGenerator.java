/* MontageGenerator.java created 2007-11-22
 *
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.Errors;

/**
 * This interface for all montage generators allows to create and
 * validate a {@link Montage montage} of a certain type (defined by implementation).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageGenerator extends MessageSourceResolvable, Serializable {

        /**
         * Creates a {@link Montage montage} of a specified type
         * (depending on a type of this generator) from a given montage.
         * @param montage a montage to be used
         * @throws MontageException if there is an error while creating
         * a montage (when it occurs depends on a type of this montage generator)
         */
	void createMontage(Montage montage) throws MontageException;

        /**
         * Checks if a {@link Montage montage} is a valid montage of a
         * specified type (depending on a type of this generator).
         * @param sourceMontage the montage to be checked
         * @param errors Errors object used to report errors
         * @return true if a montage is a valid montage of a specified type,
         * false otherwise
         */
	boolean validateSourceMontage(SourceMontage sourceMontage, Errors errors);

}
