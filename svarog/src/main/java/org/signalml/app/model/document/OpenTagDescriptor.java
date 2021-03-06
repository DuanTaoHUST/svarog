/* OpenTagDescriptor.java created 2007-10-07
 *
 */

package org.signalml.app.model.document;

import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;

/** OpenTagDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenTagDescriptor {

	private TagDocument existingDocument;
	private SignalDocument parent;

	public SignalDocument getParent() {
		return parent;
	}

	public void setParent(SignalDocument parent) {
		this.parent = parent;
	}

	public TagDocument getExistingDocument() {
		return existingDocument;
	}

	public void setExistingDocument(TagDocument existingDocument) {
		this.existingDocument = existingDocument;
	}

}
