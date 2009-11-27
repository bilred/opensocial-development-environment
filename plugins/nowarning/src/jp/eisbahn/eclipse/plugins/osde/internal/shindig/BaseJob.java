package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

abstract class BaseJob extends Job {

    protected final Logger logger;

    public BaseJob(String jobName, Logger logger) {
        super(jobName);
        this.logger = logger;
    }

    public BaseJob(String jobName) {
        super(jobName);
        this.logger = new Logger(this.getClass());
    }

    @Override
    protected final IStatus run(IProgressMonitor monitor) {
        try {
            runImpl(monitor);
            return Status.OK_STATUS;
        } catch (Exception e) {
            logger.error("Fail to complete: " + getName(), e);
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
        }
    }

    protected abstract void runImpl(IProgressMonitor monitor) throws Exception;
}
