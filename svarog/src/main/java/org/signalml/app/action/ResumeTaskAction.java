/* ResumeTaskAction.java created 2007-10-19
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogI18n._;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TaskFocusSelector;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.task.Task;

/** ResumeTaskAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ResumeTaskAction extends AbstractFocusableSignalMLAction<TaskFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ResumeTaskAction.class);

	private ApplicationTaskManager taskManager;

	public ResumeTaskAction(TaskFocusSelector taskFocusSelector) {
		super(taskFocusSelector);
		setText(_("Resume"));
		setIconPath("org/signalml/app/icon/resume.png");
		setToolTip(_("Resume this task"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		Task targetTask = getActionFocusSelector().getActiveTask();
		if (targetTask == null) {
			return;
		}

		synchronized (targetTask) {
			if (targetTask.getStatus().isResumable()) {
				taskManager.resumeTask(targetTask);
			}
		}

	}

	public void setEnabledAsNeeded() {
		boolean enabled = false;

		Task targetTask = getActionFocusSelector().getActiveTask();
		if (targetTask != null) {
			enabled = targetTask.getStatus().isResumable();
		}

		setEnabled(enabled);
	}

	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(ApplicationTaskManager taskManager) {
		this.taskManager = taskManager;
	}

}
