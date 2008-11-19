package jp.eisbahn.eclipse.plugins.osde.svn;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import junit.framework.TestCase;

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

public class SvnKitTest extends TestCase {
	
	public void testSVNKit() throws Exception {
		DAVRepositoryFactory.setup();
		SVNURL url = SVNURL.parseURIEncoded("http://svn.apache.org/repos/asf/incubator/shindig/trunk/");
//		String userName = "yoichiro";
//		String userPassword = "maki3!";
		File exportDir = new File("aaa");
		exportDir.mkdirs();
		SVNRepository repository = SVNRepositoryFactory.create(url);
//		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, userPassword);
//		repository.setAuthenticationManager(authManager);
//		SVNNodeKind nodeKind = repository.checkPath("", -1);
		long latestRevision = repository.getLatestRevision();
		ISVNReporterBaton reporterBaton = new ExportReporterBaton(latestRevision);
		ISVNEditor exportEditor = new ExportEditor(exportDir);
		repository.update(latestRevision, null, true, reporterBaton, exportEditor);
		System.out.println("Exported revision: " + latestRevision);
	}
	
	private static class ExportReporterBaton implements ISVNReporterBaton {
		
		private long exportRevision;
		
		public ExportReporterBaton(long exportRevision) {
			super();
			this.exportRevision =exportRevision;
		}

		public void report(ISVNReporter reporter) throws SVNException {
			reporter.setPath("", null, exportRevision, SVNDepth.INFINITY, true);
			reporter.finishReport();
		}
		
	}
	
	private static class ExportEditor implements ISVNEditor {
		
		private File myRootDirectory;
		private SVNDeltaProcessor myDeltaProcessor;
		
		public ExportEditor(File myRootDirectory) {
			super();
			this.myRootDirectory = myRootDirectory;
			myDeltaProcessor = new SVNDeltaProcessor();
		}

		public void abortEdit() throws SVNException {
		}

		public void absentDir(String arg0) throws SVNException {
		}

		public void absentFile(String arg0) throws SVNException {
		}

		public void addDir(String path, String copyFromPath, long copyFromRivision) throws SVNException {
			File newDir = new File(myRootDirectory, path);
			newDir.mkdirs();
		}

		public void addFile(String path, String copyFromPath, long copyFromRevision) throws SVNException {
			File file = new File(myRootDirectory, path);
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
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
			System.out.println("file added: " + path);
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
			myDeltaProcessor.applyTextDelta(null, new File(myRootDirectory, path), false);
		}

		public OutputStream textDeltaChunk(String path, SVNDiffWindow diffWindow) throws SVNException {
			return myDeltaProcessor.textDeltaChunk(diffWindow);
		}

		public void textDeltaEnd(String path) throws SVNException {
			myDeltaProcessor.textDeltaEnd();
		}
		
	}

}
