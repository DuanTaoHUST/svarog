/* FileSelectPanel.java
 *
 */
package org.signalml.app.view.element;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This class represents a panel which may be used to select a file.
 * It contains text fields where a file name can be entered, a label for this
 * field (which can be set in the constructor) and a button which opens a dialog
 * using which a file path can be selected more conveniently.
 */
public class FileSelectPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(FileSelectPanel.class);
	private MessageSourceAccessor messageSource;

	/**
	 * Label for the fileNameField.
	 */
	private JLabel selectFileLabel;

	/**
	 * A text field where file name can be entered.
	 */
	private JTextField fileNameField;

	/**
	 * A button for opening a file chooser dialog using which a file path
	 * can be selected more conveniently.
	 */
	private JButton browseButton;

        /**
         * List of filters the user can choose from. The key is the file description,
         * the value is the file extensions array. If the selected file doesn't exist,
         * first extension in the array will be added to it's name.
         */
        private HashMap<String, String[]> filters;

        /**
         * Indicates if the "All files" filter is available
         */
        private boolean allFilters;

        /**
         * Browse button action.
         */
        private BrowseButtonAction browseButtonAction;

	/**
	 * This is the default constructor
	 */
	public FileSelectPanel(MessageSourceAccessor messageSource, String selectFilePrompt) {
		super();
		this.messageSource = messageSource;
		this.selectFileLabel = new JLabel(selectFilePrompt);
		initialize();
	}

	/**
	 * This constructor takes two additional parameters: {@link #filters} and {@link #allFilters}
	 */
	public FileSelectPanel(MessageSourceAccessor messageSource, String selectFilePrompt, HashMap<String, String[]> filters, boolean allFilters) {
		super();
		this.messageSource = messageSource;
		this.selectFileLabel = new JLabel(selectFilePrompt);
                this.filters = filters;
                this.allFilters = allFilters;
		initialize();
	}

	/**
	 * Initializes this panel.
	 */
	private void initialize() {

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.weightx = 100;
		c.insets = new Insets(0, 2, 0, 2);
		add(selectFileLabel, c);
		c.weightx = 0;
		c.gridx = 1;
		add(getFileNameField(), c);
		c.gridx = 2;
		add(getChangeButton(), c);

	}

	/**
	 * Returns the {@link JTextField} which can be used to input
	 * a file name.
	 * @return the {@link JTextField} used in this panel
	 */
	protected JTextField getFileNameField() {
		if (fileNameField == null) {
			fileNameField = new JTextField(20);
		}
		return fileNameField;
	}

	/**
	 * Returns a {@link JButton} which opens a {@link JFileChooser} to
	 * which allows to select a file path.
	 * @return a {@link JButton} allowing to browse through directories
	 * and choose a file path
	 */
	protected JButton getChangeButton() {
		if (browseButton == null) {
			browseButton = new JButton(messageSource.getMessage("fileSelectPanel.browseButtonLabel"));
                        if (filters == null)
                                browseButtonAction = new BrowseButtonAction();
                        else
                                browseButtonAction = new BrowseButtonAction(filters, allFilters);
                        browseButton.addActionListener(browseButtonAction);
		}
		return browseButton;
	}

	/**
	 * Sets the file name which shows in the file name text field.
	 * @param fileName a value of file name to be set
	 */
	public void setFileName(String fileName) {
		this.fileNameField.setText(fileName);
	}

	/**
	 * Returns the file name selected in this panel.
	 * @return a file name which was selected using this panel.
	 */
	public String getFileName() {
		return this.fileNameField.getText();
	}

        /**
         * Whether the file chooser should return a relative path.
         *
         * @param ret if true, a relative path will be returned
         */
        public void returnRelativePath(boolean ret) {
                browseButtonAction.setReturnRelativePath(ret);
        }

	/**
	 * Returns whether a file was selected using this panel or not.
	 * @return true if the file is selected, false otherwise
	 */
	public boolean isFileSelected() {
		String t = getFileNameField().getText();
		if (t != null && !"".equals(t)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sets the state (enabled/disabled) of this component.
	 * @param enabled true if this panel should be enabled, false otherwise.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		selectFileLabel.setEnabled(enabled);
		fileNameField.setEnabled(enabled);
		browseButton.setEnabled(enabled);
	}

	/**
	 * Class responsible for the action performed after pressing Browse button.
	 * Shows a window in which the user can choose a file name's path and changes the
	 * text field containing the file name in the current FileSelectPanel.
	 */
	private class BrowseButtonAction implements ActionListener {

		/**
		 * A file chooser to select files with. Shows in response to
		 * this action.
		 */
		private JFileChooser fileChooser;

                /**
                 * List of filters the user can choose from. The key is the file description,
                 * the value is the file extensions array.
                 */
                private HashMap<String, String[]> filters;

                /**
                 * If true the file chooser will return a relative path.
                 */
                private boolean returnRelativePath = false;

		/**
		 * Creates a new BrowseButtonAction and initializes it.
		 */
		public BrowseButtonAction() {
			super();
			fileChooser = new JFileChooser();
			fileChooser.setApproveButtonText(messageSource.getMessage("ok"));
		}

                /**
                 * Creates a new BrowseButtonAction and initializes it with filters.
                 *
                 * @param filters filter collection
                 * @param allFilters wheter the all files filter should be included
                 */
		public BrowseButtonAction(HashMap<String, String[]> filters, boolean allFilters) {
			super();
			fileChooser = new JFileChooser();
			fileChooser.setApproveButtonText(messageSource.getMessage("ok"));

                        fileChooser.setAcceptAllFileFilterUsed(allFilters);

                        for (String description : filters.keySet()) {

                                FileNameExtensionFilter filter = new FileNameExtensionFilter(description, filters.get(description));
                                fileChooser.addChoosableFileFilter(filter);
                        }

                        this.filters = filters;
		}

                /**
                 * Wheter the file chooser should return a relative path.
                 *
                 * @param ret if true a relative path will be returned
                 */
                public void setReturnRelativePath(boolean ret) {
                        returnRelativePath = ret;
                }

                /**
                 * Gets a relative path.
                 *
                 * @param absolutePath the absolute path
                 * @return the relative path
                 */
                private String getRelativePath(String absolutePath) {
                        
                        String basePath = (new File(".")).getAbsolutePath();                        
                        String pathSeparator = "/";

                        File f = new File(absolutePath);
                        boolean isDir = f.isDirectory();

                        String[] base = basePath.split(Pattern.quote(pathSeparator), -1);
                        String[] target = absolutePath.split(Pattern.quote(pathSeparator), 0);

                        String common = "";
                        int commonIndex = 0;

                        for (int i = 0; i < target.length && i < base.length; i++) {
                                if (target[i].equals(base[i])) {
                                        common += target[i] + pathSeparator;
                                        commonIndex++;
                                }
                                else break;
                        }

                        if (commonIndex == 0)
                        {
                                return absolutePath;
                        }

                        String relative = "";
                        if (base.length == commonIndex) {
                                relative = "." + pathSeparator;
                        } else {
                                int numDirsUp = base.length - commonIndex - (isDir ? 0 : 1);
                                for (int i = 1; i <= numDirsUp; i++) {
                                        relative += ".." + pathSeparator;
                                }
                        }

                        if (absolutePath.length() > common.length()) {
                                relative += absolutePath.substring(common.length());
                        }

                        return relative;
                }

		@Override
		public void actionPerformed(ActionEvent e) {

			int returnVal = fileChooser.showOpenDialog(FileSelectPanel.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();

				try {
					getFileNameField().setText(selectedFile.getCanonicalPath());
				} catch (IOException e1) {
					getFileNameField().setText("");
				}

                                if (fileChooser.getFileFilter() instanceof FileNameExtensionFilter)
                                {
                                        FileNameExtensionFilter filter = (FileNameExtensionFilter) fileChooser.getFileFilter();
                                        String extension = filters.get(filter.getDescription())[0];

                                        if (!getFileNameField().getText().endsWith(extension))
                                                getFileNameField().setText(getFileNameField().getText() + "." + extension);
                                }

                                if (returnRelativePath) {
                                        getFileNameField().setText(getRelativePath(getFileNameField().getText()));
                                }
			}

		}
	}
}