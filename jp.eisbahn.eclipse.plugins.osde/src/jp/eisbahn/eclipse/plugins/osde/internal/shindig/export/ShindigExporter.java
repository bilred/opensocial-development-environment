package jp.eisbahn.eclipse.plugins.osde.internal.shindig.export;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReporter;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaProcessor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

public class ShindigExporter {
	
	public static final String REPOSITORY_URL = "http://svn.apache.org/repos/asf/incubator/shindig/trunk/";
	
	public void export(File targetDirectory, IProgressMonitor monitor) throws SVNException {
		try {
			monitor.beginTask("Preparing to export Apache Shindig.", 3);
			//
			monitor.subTask("Preparing to connect to the subversion repository.");
			DAVRepositoryFactory.setup();
			SVNURL url = SVNURL.parseURIEncoded(REPOSITORY_URL);
			SVNRepository repository = SVNRepositoryFactory.create(url);
			monitor.worked(1);
			//
			monitor.subTask("Getting the latest revision number.");
			long latestRevision = repository.getLatestRevision();
			monitor.worked(1);
			//
			monitor.subTask("Counting up the files.");
			ISVNReporterBaton reporterBaton = new ExportReporterBaton(latestRevision);
			ExportEditor exportEditor = new ExportEditor(targetDirectory, false, monitor);
			repository.update(latestRevision, null, true, reporterBaton, exportEditor);
			monitor.worked(1);
			monitor.done();
			//
			monitor.beginTask("Exporting Apache Shindig. (Revision: " + latestRevision + ")", exportEditor.getCount());
			//
			reporterBaton = new ExportReporterBaton(latestRevision);
			exportEditor = new ExportEditor(targetDirectory, true, monitor);
			repository.update(latestRevision, null, true, reporterBaton, exportEditor);
		} finally {
			monitor.done();
		}
	}
	
	private static class ExportReporterBaton implements ISVNReporterBaton {
		
		private long exportRevision;
		
		public ExportReporterBaton(long exportRevision) {
			super();
			this.exportRevision = exportRevision;
		}

		public void report(ISVNReporter reporter) throws SVNException {
			reporter.setPath("", null, exportRevision, SVNDepth.INFINITY, true);
			reporter.finishReport();
		}
		
	}
	
	private static class ExportEditor implements ISVNEditor {
		
		private File myRootDirectory;
		private SVNDeltaProcessor myDeltaProcessor;
		
		private boolean fetch;
		private int count;
		private IProgressMonitor monitor;
		
		public ExportEditor(File myRootDirectory, boolean fetch, IProgressMonitor monitor) {
			super();
			this.myRootDirectory = myRootDirectory;
			myDeltaProcessor = new SVNDeltaProcessor();
			this.fetch = fetch;
			count = 0;
			this.monitor = monitor;
		}
		
		public int getCount() {
			return count;
		}

		public void abortEdit() throws SVNException {
		}

		public void absentDir(String arg0) throws SVNException {
		}

		public void absentFile(String arg0) throws SVNException {
		}

		public void addDir(String path, String copyFromPath, long copyFromRivision) throws SVNException {
			if (fetch) {
				File newDir = new File(myRootDirectory, path);
				newDir.mkdirs();
				monitor.worked(1);
			}
			count++;
		}

		public void addFile(String path, String copyFromPath, long copyFromRevision) throws SVNException {
			if (fetch) {
				File file = new File(myRootDirectory, path);
				try {
					file.createNewFile();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
			count++;
		}

		public void changeDirProperty(String arg0, SVNPropertyValue arg1) throws SVNException {
		}

		public void changeFileProperty(String arg0, String arg1, SVNPropertyValue arg2) throws SVNException {
		}

		public void closeDir() throws SVNException {
		}

		public SVNCommitInfo closeEdit() throws SVNException {
			return null;
		}

		public void closeFile(String path, String textChecksum) throws SVNException {
			if (fetch) {
				monitor.subTask(path);
				monitor.worked(1);
			} else {
				monitor.subTask("Counting up the files. (" + count + ")");
			}
		}

		public void deleteEntry(String arg0, long arg1) throws SVNException {
		}

		public void openDir(String arg0, long arg1) throws SVNException {
		}

		public void openFile(String arg0, long arg1) throws SVNException {
		}

		public void openRoot(long arg0) throws SVNException {
		}

		public void targetRevision(long arg0) throws SVNException {
		}

		public void applyTextDelta(String path, String baseChecksum) throws SVNException {
			if (fetch) {
				myDeltaProcessor.applyTextDelta(null, new File(myRootDirectory, path), false);
			}
		}

		public OutputStream textDeltaChunk(String path, SVNDiffWindow diffWindow) throws SVNException {
			if (fetch) {
				return myDeltaProcessor.textDeltaChunk(diffWindow);
			}
			return null;
		}

		public void textDeltaEnd(String path) throws SVNException {
			if (fetch) {
				myDeltaProcessor.textDeltaEnd();
			}
		}
		
	}

}
