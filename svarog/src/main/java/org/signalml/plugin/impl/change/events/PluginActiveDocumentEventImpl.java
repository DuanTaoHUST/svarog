/**
 *
 */
package org.signalml.plugin.impl.change.events;

import org.signalml.plugin.export.change.events.PluginActiveDocumentEvent;
import org.signalml.plugin.export.signal.Document;

/**
 * Implementation of the {@link PluginActiveDocumentEvent}.
 * Contains the old and the new value of the active {@link Document}.
 * @author Marcin Szumski
 */
public class PluginActiveDocumentEventImpl extends PluginDocumentEventImpl implements PluginActiveDocumentEvent {

	/**
	 * the old value of the active {@link Document}
	 */
	protected Document oldDocument;

	/**
	 * Constructor. Sets the old and the new value of the active
	 * {@link Document}.
	 * @param newDocument the new and the new value of the active document
	 * @param oldDocument the old and the new value of the active document
	 */
	public PluginActiveDocumentEventImpl(Document newDocument, Document oldDocument) {
		super(newDocument);
		this.oldDocument = oldDocument;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.ActiveDocumentEvent#getOldDocument()
	 */
	@Override
	public Document getOldDocument() {
		return oldDocument;
	}

}
