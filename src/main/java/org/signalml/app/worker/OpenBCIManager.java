package org.signalml.app.worker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingWorker;
import multiplexer.jmx.client.JmxClient;
import org.signalml.app.action.ConnectMultiplexerActionNoMetadata;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.ProgressDialog;
import org.signalml.app.view.element.ProgressState;
import org.signalml.app.worker.processes.ProcessExitData;
import org.signalml.app.worker.processes.ProcessManager;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Class responsible for starting all OpenBCI modules, sending configuration
 * data to the hashtable and connecting to OpenBCI.
 *
 * @author Tomasz Sawicki
 */
public class OpenBCIManager extends SwingWorker<ProgressState, ProgressState> implements PropertyChangeListener {

        public static final String MX_ID = "mx";
        public static final String HASHTABLE_ID = "hashtable";
        public static final String PINGER_ID = "pinger";
        public final int MAX_PROGRESS = 7;
        public final int PROCESS_SLEEP = 1000;
        /**
         * The message source.
         */
        private MessageSourceAccessor messageSource;
        /**
         * The element manager.
         */
        private ViewerElementManager elementManager;
        /**
         * Connection descriptor.
         */
        private AmplifierConnectionDescriptor descriptor;
        /**
         * The process manager.
         */
        private ProcessManager processManager;
        /**
         * Connect action.
         */
        private ConnectMultiplexerActionNoMetadata connectAction;
        /**
         * Configuration worker.
         */
        private BCIConfigurationWorker configurationWorker;
        /**
         * A lock used to syncronize execution of this worker with events
         * called by sub-workers.
         */
        private final Object lock = new Object();

        /**
         * Default constructor sets values of fields.
         *
         * @param messageSource {@link #messageSource}
         * @param elementManager {@link #elementManager}
         * @param descriptor {@link #descriptor}
         */
        public OpenBCIManager(MessageSourceAccessor messageSource, ViewerElementManager elementManager, AmplifierConnectionDescriptor descriptor) {

                this.messageSource = messageSource;
                this.elementManager = elementManager;
                this.descriptor = descriptor;

                processManager = elementManager.getProcessManager();
                processManager.addPropertyChangeListener(this);

                connectAction = new ConnectMultiplexerActionNoMetadata(elementManager);
                connectAction.addPropertyChangeListener(this);
        }

        /**
         * Extracts data to send from {@link #descriptor}.
         *
         * @return data to send
         */
        private HashMap<String, String> getConfigurationDataToSend() {

                float[] descrGain = descriptor.getOpenMonitorDescriptor().getCalibrationGain();
                float[] descrOffset = descriptor.getOpenMonitorDescriptor().getCalibrationOffset();

                Float[] gain = new Float[descriptor.getOpenMonitorDescriptor().getChannelCount()];
                Float[] offset = new Float[descriptor.getOpenMonitorDescriptor().getChannelCount()];

                for (int i = 0; i < descriptor.getOpenMonitorDescriptor().getChannelCount(); i++) {

                        gain[i] = new Float(descrGain[i]);
                        offset[i] = new Float(descrOffset[i]);
                }


                ConfigurationDataFormatter dataFormatter = new ConfigurationDataFormatter(
                        descriptor.getOpenMonitorDescriptor().getChannelCount(),
                        gain, offset,
                        new Integer((int) descriptor.getOpenMonitorDescriptor().getSamplingFrequency().floatValue()),
                        descriptor.getAmplifierInstance().getDefinition().getChannelNumbers().toArray(new Integer[0]),
                        descriptor.getOpenMonitorDescriptor().getChannelLabels());

                return dataFormatter.formatData();
        }

        /**
         * Gets openBCI modules data.
         *
         * @return openBCI modules data
         * @throws Exception when some data is missing
         */
        private HashMap<String, String> getModulesData() throws Exception {

                HashMap<String, String> modulesData = elementManager.getOpenBCIModulePresetManager().getModulesConfiguration();

                if (!modulesData.containsKey(MX_ID) || !modulesData.containsKey(HASHTABLE_ID) || !modulesData.containsKey(PINGER_ID)) {
                        throw new Exception();
                }

                return modulesData;
        }

        /**
         * Cancels execution, kills all created processes and disconnects from
         * the multiplexer.
         */
        public void cancel() {

                connectAction.cancel();
                if (configurationWorker != null) {
                        configurationWorker.cancel(true);
                }
                processManager.killAll();
                try {
                        JmxClient jmxClient = elementManager.getJmxClient();
                        if (jmxClient != null) {
                                jmxClient.shutdown();
                        }
                        elementManager.setJmxClient(null);
                } catch (InterruptedException e) {
                }
                elementManager.setJmxClient(null);
                cancel(true);
        }

        /**
         * Performs all the execution.
         *
         * @return execution result
         * @throws Exception when interrupted etc
         */
        @Override
        protected ProgressState doInBackground() throws Exception {

                // Kill all running modules first
                processManager.killAll();

                // Get modules data
                HashMap<String, String> modulesData;
                try {
                        modulesData = getModulesData();
                } catch (Exception ex) {
                        return new ProgressState(messageSource.getMessage("opensignal.amplifier.missingModulesData"), -1, MAX_PROGRESS);
                }

                // Start multiplexer
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.startingMultiplexer"), 0, MAX_PROGRESS));
                List<String> mxParameters = new ArrayList<String>();
                mxParameters.add("run_multiplexer");
                mxParameters.add(elementManager.getApplicationConfig().getMultiplexerAddress() + ":"
                        + String.valueOf(elementManager.getApplicationConfig().getMultiplexerPort()));
                processManager.runProcess(MX_ID, modulesData.get(MX_ID), mxParameters);
                Thread.sleep(PROCESS_SLEEP);

                // Start hashtable
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.startingHashtable"), 1, MAX_PROGRESS));
                processManager.runProcess(HASHTABLE_ID, modulesData.get(HASHTABLE_ID), new ArrayList<String>());
                Thread.sleep(PROCESS_SLEEP);

                // Start pinger
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.startingPinger"), 2, MAX_PROGRESS));
                processManager.runProcess(PINGER_ID, modulesData.get(PINGER_ID), new ArrayList<String>());
                Thread.sleep(PROCESS_SLEEP);

                // Connect to openBCI
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.connecting"), 3, MAX_PROGRESS));
                synchronized (lock) {
                        connectAction.actionPerformed(null);
                        lock.wait();
                }

                // Send configuration data
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.sendingConfigData"), 5, MAX_PROGRESS));
                synchronized (lock) {
                        configurationWorker = new BCIConfigurationWorker(messageSource, elementManager.getJmxClient(), getConfigurationDataToSend());
                        configurationWorker.addPropertyChangeListener(this);
                        configurationWorker.execute();
                        lock.wait();
                }

                // Start driver
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.startingAmplifier"), 6, MAX_PROGRESS));
                String amp_id = descriptor.getAmplifierInstance().getDefinition().getName();
                List<String> ampParameters = new ArrayList<String>();
                ampParameters.add(descriptor.getAmplifierInstance().getAddress());                
                processManager.runProcess(amp_id, descriptor.getAmplifierInstance().getDefinition().getDriverPath(), ampParameters);
                Thread.sleep(PROCESS_SLEEP);

                
                return new ProgressState(messageSource.getMessage("success"), MAX_PROGRESS, MAX_PROGRESS);
        }

        /**
         * Publishes progress status to window performing operation.
         *
         * @param chunks chunks
         */
        @Override
        protected void process(List<ProgressState> chunks) {

                for (ProgressState progressState : chunks) {
                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null, progressState);
                }
        }

        /**
         * Processes events from sub-workers.
         *
         * @param evt property change event
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if (ProcessManager.PROCESS_ENDED.equals(evt.getPropertyName())) {

                        String errorMsg;
                        ProcessExitData exitData = (ProcessExitData) evt.getNewValue();
                        if (exitData.getExitCode() != null) {
                                errorMsg = messageSource.getMessage("opensignal.amplifier.processEnded");
                                errorMsg += exitData.getId() + " " + exitData.getExitCode().toString();
                        } else {
                                errorMsg = messageSource.getMessage("opensignal.amplifier.processFailed");
                                errorMsg += exitData.getId();
                        }
                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null, new ProgressState(errorMsg, -1, MAX_PROGRESS));
                        cancel();

                } else if (BCIConfigurationWorker.SENDING_DONE.equals(evt.getPropertyName())) {

                        synchronized (lock) {
                                WorkerResult res = (WorkerResult) evt.getNewValue();
                                if (res.success) {
                                        lock.notify();
                                } else {
                                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null,
                                                new ProgressState(messageSource.getMessage("opensignal.amplifier.sendingFailed"), -1, MAX_PROGRESS));
                                        cancel();
                                }
                        }

                } else if (MultiplexerConnectWorker.JMX_CONNECTION.equals(evt.getPropertyName())) {

                        synchronized (lock) {
                                WorkerResult res = (WorkerResult) evt.getNewValue();
                                if (res.success) {
                                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null,
                                                new ProgressState(messageSource.getMessage("opensignal.amplifier.testingConnection"), 4, MAX_PROGRESS));
                                } else {
                                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null,
                                                new ProgressState(messageSource.getMessage("opensignal.amplifier.connectionFailed"), -1, MAX_PROGRESS));
                                        cancel();
                                }
                        }

                } else if (MultiplexerConnectionTestWorker.CONNECTION_TEST_RESULT.equals(evt.getPropertyName())) {

                        synchronized (lock) {
                                WorkerResult res = (WorkerResult) evt.getNewValue();
                                if (res.success) {
                                        lock.notify();
                                } else {
                                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null,
                                                new ProgressState(messageSource.getMessage("opensignal.amplifier.testFailed"), -1, MAX_PROGRESS));
                                        cancel();
                                }
                        }
                }
        }

        /**
         * When the work is done.
         */
        @Override
        protected void done() {

                try {
                        ProgressState result = get();
                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null, result);                        
                } catch (Exception ex) {
                        ProgressState result = new ProgressState(messageSource.getMessage("failed"), -1, MAX_PROGRESS);
                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null, result);                        
                }
                processManager.removePropertyChangeListener(this);
        }
}