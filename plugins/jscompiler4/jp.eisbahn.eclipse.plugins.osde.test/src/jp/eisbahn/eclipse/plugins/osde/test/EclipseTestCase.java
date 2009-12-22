package jp.eisbahn.eclipse.plugins.osde.test;

import static org.junit.Assert.assertNotNull;
import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.junit.Before;

public abstract class EclipseTestCase {

    protected static final NullProgressMonitor NULL_PROGRESS_MONITOR = new NullProgressMonitor();

	AbstractUIPlugin PLUGIN;

	@Before
	protected void setUp() throws Exception {
		PLUGIN = Activator.getDefault();
		assertNotNull(PLUGIN);
	}

	void delay(long delay) {
		Display display = Display.getCurrent();
		if (display != null) {
			long endAt = System.currentTimeMillis() + delay;
			while (System.currentTimeMillis() < endAt) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
				display.update();
			}
		} else {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException ignored) {
			}
		}

	}

	void waitForJobs(){
		while(Job.getJobManager().currentJob() != null){
			delay(1000);
		}
	}

	void delayAndWaitForJobs(long delay){
		delay(delay);
		waitForJobs();
	}

}
