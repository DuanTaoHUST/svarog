package org.signalml.app.view.opensignal;

import org.signalml.app.model.AmplifierConnectionDescriptor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.worker.amplifiers.AmplifierDefinition;
import org.signalml.app.worker.amplifiers.AmplifierDiscoveryWorker;
import org.signalml.app.worker.amplifiers.AmplifierInstance;
import org.signalml.app.worker.amplifiers.DeviceSearchResult;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Allows to choose an amplifier from a list.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierSelectionPanel extends JPanel implements PropertyChangeListener {

        /**
         * List of frequencies.
         */
        private JList amplifiersList = null;
        /**
         * Refresh button.
         */
        private JButton refreshButton = null;
        /**
         * Configure modules button.
         */
        private JButton configureModulesButton = null;
        /**
         * Configure definitions button.
         */
        private JButton configureDefinitionsButton = null;
        /**
         * Message source.
         */
        private MessageSourceAccessor messageSource;
        /**
         * Viewer element manager.
         */
        private ViewerElementManager elementManager;
        /**
         * Amplifier signal source panel.
         */
        private AmplifierSignalSourcePanel sourcePanel;
        /**
         * Current descriptor.
         */
        private AmplifierConnectionDescriptor currentDescriptor;

        /**
         * Default constructor.
         *
         * @param messageSource {@link #messageSource}
         * @param elementManager {@link #elementManager}
         * @param sourcePanel {@link #sourcePanel}
         */
        public AmplifierSelectionPanel(MessageSourceAccessor messageSource,
                ViewerElementManager elementManager,
                AmplifierSignalSourcePanel sourcePanel) {

                super();

                this.messageSource = messageSource;
                this.elementManager = elementManager;
                this.sourcePanel = sourcePanel;

                createInterface();

                currentDescriptor = new AmplifierConnectionDescriptor();
        }

        /**
         * Fills an {@link AmplifierConnectionDescriptor} from this panel.
         * 
         * @param amplifierConnectionDescriptor the descriptor
         */
        public void fillModelFromPanel(AmplifierConnectionDescriptor descriptor) {

                int selectedIndex = getAmplifiersList().getSelectedIndex();

                if (selectedIndex < 0) {
                        descriptor.setAmplifierInstance(null);
                } else {
                        AmplifierInstance selectedInstance;
                        selectedInstance = (AmplifierInstance) getAmplifiersList().getModel().getElementAt(selectedIndex);
                        descriptor.setAmplifierInstance(selectedInstance);
                }
        }

        /**
         * Fills this panel from an {@link AmplifierConnectionDescriptor}.
         *
         * @param descriptor the descriptor
         * @throws AmplifierNotFoundException when the amplifier from the descriptor cannot be found
         */
        public void fillPanelFromModel(AmplifierConnectionDescriptor descriptor) throws SignalMLException {

                if (descriptor.isBciStarted())
                        setEnabledAll(false);
                else
                        setEnabledAll(true);

                // TODO: refresh amp list and try to find the one from the descriptor

                currentDescriptor = descriptor;
        }

        /**
         * Refreshes the list.
         */
        private void refresh() {

                amplifierSelected();

                getAmplifiersList().setEnabled(false);
                getRefreshButton().setEnabled(false);

                getAmplifiersList().setListData(new String[]{messageSource.getMessage("amplifierSelection.search")});

                List<AmplifierDefinition> definitions;

                try {
                        definitions = elementManager.getAmplifierDefinitionPresetManager().getDefinitionList();
                } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, messageSource.getMessage("amplifierSelection.consistency"));
                        setEnabled(true);
                        getAmplifiersList().setListData(new String[]{});
                        return;
                }

                AmplifierDiscoveryWorker discoveryWorker = new AmplifierDiscoveryWorker(definitions);
                discoveryWorker.addPropertyChangeListener(this);
                discoveryWorker.startSearch();
        }

        /**
         * Called when device search is over.
         *
         * @param evt property change event
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if (AmplifierDiscoveryWorker.END_OF_SEARCH.equals(evt.getPropertyName())) {

                        DeviceSearchResult result = (DeviceSearchResult) evt.getNewValue();

                        if (!result.isBluetoothOK()) {
                                JOptionPane.showMessageDialog(this, messageSource.getMessage("amplifierSelection.bluetoothSearchFailed") + result.getBluetoothErrorMsg());
                        }

                        if (!result.isUsbOK()) {
                                JOptionPane.showMessageDialog(this, messageSource.getMessage("amplifierSelection.usbSearchFailed") + result.getUsbErrorMsg());
                        }

                        getAmplifiersList().setListData(result.getResults().toArray());

                        getAmplifiersList().setEnabled(true);
                        getRefreshButton().setEnabled(true);
                }
        }

        /**
         * Creates the interface.
         */
        private void createInterface() {

                setLayout(new BorderLayout(10, 10));


                CompoundBorder amplifiersListBorder = new CompoundBorder(
                        new TitledBorder(messageSource.getMessage("amplifierSelection.amplifiersList")),
                        new EmptyBorder(3, 3, 3, 3));

                JPanel amplifiersListPanel = new JPanel(new BorderLayout(10, 10));
                amplifiersListPanel.setLayout(new BorderLayout(10, 10));
                amplifiersListPanel.setBorder(amplifiersListBorder);

                amplifiersListPanel.add(new JScrollPane(getAmplifiersList()), BorderLayout.CENTER);

                JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                refreshPanel.add(getRefreshButton());
                amplifiersListPanel.add(refreshPanel, BorderLayout.PAGE_END);

                add(amplifiersListPanel, BorderLayout.CENTER);


                CompoundBorder configBorder = new CompoundBorder(
                        new TitledBorder(messageSource.getMessage("amplifierSelection.config")),
                        new EmptyBorder(3, 3, 3, 3));

                JPanel configPanel = new JPanel();
                configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
                configPanel.setBorder(configBorder);
                configPanel.add(getConfigureDefinitionsButton());
                configPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                configPanel.add(getConfigureModulesButton());


                add(configPanel, BorderLayout.PAGE_END);
        }

        /**
         * Gets the amplifiers list.
         *
         * @return the amplifiers list
         */
        private JList getAmplifiersList() {

                if (amplifiersList == null) {
                        amplifiersList = new JList();
                        amplifiersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        amplifiersList.addListSelectionListener(new ListSelectionListener() {

                                @Override
                                public void valueChanged(ListSelectionEvent e) {
                                        amplifierSelected();
                                }
                        });
                }
                return amplifiersList;
        }

        /**
         * Called when an amplifier is selected.
         */
        private void amplifierSelected() {

                fillModelFromPanel(currentDescriptor);
                if (currentDescriptor.getAmplifierInstance() != null)
                        currentDescriptor.getOpenMonitorDescriptor().fillFromAnAmplifierDefinition(
                                currentDescriptor.getAmplifierInstance().getDefinition());
                try {
                        sourcePanel.fillPanelFromModel(currentDescriptor, true);
                } catch (SignalMLException ex) {
                }
        }

        /**
         * Gets the refresh button.
         *
         * @return the refresh button
         */
        private JButton getRefreshButton() {

                if (refreshButton == null) {
                        refreshButton = new JButton(new AbstractAction() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        refresh();
                                }
                        });
                        refreshButton.setText(messageSource.getMessage("amplifierSelection.refresh"));
                }
                return refreshButton;
        }

        /**
         * Gets the configure definitions button.
         *
         * @return the configure definitions button
         */
        private JButton getConfigureDefinitionsButton() {

                if (configureDefinitionsButton == null) {
                        configureDefinitionsButton = new JButton(elementManager.getAmplifierDefinitionConfigAction());
                        configureDefinitionsButton.setText(messageSource.getMessage("action.amplifierDefinitionConfig"));
                        configureDefinitionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                }
                return configureDefinitionsButton;
        }

        /**
         * Gets the configure modules button.
         *
         * @return the configure modules button
         */
        private JButton getConfigureModulesButton() {

                if (configureModulesButton == null) {
                        configureModulesButton = new JButton(elementManager.getOpenBCIModuleConfigAction());
                        configureModulesButton.setText(messageSource.getMessage("action.openBCIModulesConfig"));
                        configureModulesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                }
                return configureModulesButton;
        }

        /**
         * Sets enabled to this panel and all it's children.
         * Clears all fields if enabled == false.
         *
         * @param enabled true or false
         */
        public void setEnabledAll(boolean enabled) {

                setEnabledToChildren(this, enabled);
        }

        /**
         * Sets enabled to a component and all of it's children.
         *
         * @param component target component
         * @param enabled true or false
         * @param omit wheter to omit component
         */
        private void setEnabledToChildren(Component component, boolean enabled) {

                component.setEnabled(enabled);
                if (component instanceof Container) {
                        Component[] children = ((Container) component).getComponents();
                        for (Component child : children) {
                                setEnabledToChildren(child, enabled);
                        }
                }
        }
}