package org.signalml.app.worker.processes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs a process and notifies the {@link ProcessManager} when it ends.
 *
 * @author Tomasz Sawicki
 */
public class ProcessSubject extends Thread {

        /**
         * {@link ProcessBuilder} that will start the process.
         */
        private ProcessBuilder processBuilder;

        /**
         * {@link Process} object.
         */
        private Process process = null;

        /**
         * {@link ProcessManager} to notify when the process ends.
         */
        private ProcessManager processManager;

        /**
         * Process id.
         */
        private String id;

        /**
         * Constructor prepares the process.
         *
         * @param id the process id
         * @param path path to the executable file
         * @param parameters command line parameters
         */
        public ProcessSubject(String id, String path, List<String> parameters, ProcessManager processManager) {

                File executable = new File(path);
                ArrayList<String> command = new ArrayList<String>();
                command.add("./" + executable.getName());
                command.addAll(parameters);
                
                String directory = executable.getAbsolutePath().substring(0, executable.getAbsolutePath().length() - executable.getName().length());

                this.id = id;
                this.processManager = processManager;
                this.processBuilder = new ProcessBuilder(command);
                this.processBuilder.directory(new File(directory));
        }
        
        private void pipe(final InputStream src, final PrintStream dest) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        byte[] buffer = new byte[1024];
                        for (int n = 0; n != -1; n = src.read(buffer)) {
                            dest.write(buffer, 0, n);
                        }
                    } catch (IOException e) { // just exit
                    }
                }
            }).start();
        }

        /**
         * Gets the process.
         *
         * @return the process
         */
        public Process getProcess() {

                return process;
        }

        /**
         * Starts the process with a given delay
         */
        public void startProcess() {

                this.start();
        }

        /**
         * Kills the process.
         */
        public void killProcess() {

                this.interrupt();
        }

        /**
         * Starts the process, and waits for it to finish.
         * The {@link #processManager} is notified afterwards.
         */
        @Override
        public void run() {

                try {
                        try {
                                process = processBuilder.start();
                                pipe(process.getErrorStream(), System.err);
                                pipe(process.getInputStream(), System.out);

                               
                        } catch (IOException ex) {
                        		ex.printStackTrace();
                                process = null;
                        }

                        if (process != null) {
                                process.waitFor();
                                processManager.processEnded(id, process.exitValue());
                        } else {
                                processManager.processEnded(id, null);
                        }

                } catch (InterruptedException ex) {
                		ex.printStackTrace();
                        if (process != null) process.destroy();
                }
        }
}