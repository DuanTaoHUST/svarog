/**
 * 
 */
package org.signalml.plugin.loader;

import java.awt.Window;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JComponent;

import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This {@link AbstractDialog dialog} allows to manage plug-in options.
 * Contains one {@link PluginPanel panel} in border layout.
 * @see PluginPanel the funtion of this dialog
 * @author Marcin Szumski
 */
public class PluginDialog extends AbstractDialog {

	/**
	 * an array of plug-in {@link PluginState states}
	 */
	private ArrayList<PluginState> descriptions;
	
	/**
	 * an array of directories in which plug-ins are
	 * stored
	 */
	private ArrayList<File> pluginDirs;
	
	/**
	 * Constructor.
	 * @param messageSource the source of messages
	 * @param w the parent window
	 * @param isModal specifies whether dialog blocks input to other
     * windows when shown. <code>null</code> value and unsupported modality
     * types are equivalent to <code>MODELESS</code>
     * @param descriptions an array of plug-in {@link PluginState states}
     * @param pluginDirs an array of directories in which plug-ins are
	 * stored
	 */
	public PluginDialog(MessageSourceAccessor messageSource, Window w, boolean isModal, ArrayList<PluginState> descriptions,
			ArrayList<File> pluginDirs) {
		super(messageSource, w, isModal);
		this.descriptions = descriptions;
		this.pluginDirs = pluginDirs;
	}

	private static final long serialVersionUID = 1L;
	
	/**
	 * the actual panel with options
	 */
	private PluginPanel pluginPanel = null;

	/* (non-Javadoc)
	 * @see org.signalml.app.view.dialog.AbstractDialog#createInterface()
	 */
	@Override
	protected JComponent createInterface() {
		if (pluginPanel ==  null)
			pluginPanel = new PluginPanel(descriptions, pluginDirs);
		return pluginPanel;
	}

	/* (non-Javadoc)
	 * @see org.signalml.app.view.dialog.AbstractDialog#supportsModelClass(java.lang.Class)
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ArrayList.class.isAssignableFrom(clazz);
	}

	/* (non-Javadoc)
	 * @see org.signalml.app.view.dialog.AbstractDialog#fillDialogFromModel(java.lang.Object)
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		ArrayList<PluginState> descriptions = (ArrayList<PluginState>) model;
		pluginPanel.fillPanelFromModel(descriptions);

	}

	/* (non-Javadoc)
	 * @see org.signalml.app.view.dialog.AbstractDialog#fillModelFromDialog(java.lang.Object)
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		ArrayList<PluginState> descriptions = (ArrayList<PluginState>) model;
		pluginPanel.fillModelFromPanel(descriptions);
	}


}
