package jp.eisbahn.eclipse.plugins.osde.internal.shindig.export;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.core.runtime.IProgressMonitor;

public class Maven2Exporter {
	
	public static final String ARCHIVE_URL = "http://ftp.riken.jp/net/apache/maven/binaries/apache-maven-2.0.9-bin.zip";
	public static final String DOWNLOAD_FILE_NAME = "apache-maven-2.0.9-bin.zip";

	public void export(File targetDirectory, IProgressMonitor monitor) throws HttpException, IOException {
		GetMethod getMethod = null;
		BufferedInputStream in1 = null;
		BufferedOutputStream out1 = null;
		BufferedInputStream in2 = null;
		BufferedOutputStream out2 = null;
		try {
			monitor.beginTask("Getting Apache Maven2 archive information.", 1);
			HttpClient httpClient = new HttpClient();
			httpClient.getParams().setParameter("http.socket.timeout", new Integer(5000));
			getMethod = new GetMethod(ARCHIVE_URL);
			httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				long contentLength = getMethod.getResponseContentLength();
				monitor.done();
				monitor.beginTask("Download Apache Maven2 archive.", (int)contentLength);
				in1 = new BufferedInputStream(getMethod.getResponseBodyAsStream(), 1024);
				out1 = new BufferedOutputStream(new FileOutputStream(new File(targetDirectory, DOWNLOAD_FILE_NAME)));
				int len;
				byte[] buf = new byte[1024];
				while((len = in1.read(buf, 0, 1024)) != -1) {
					out1.write(buf, 0, len);
					monitor.worked(len);
				}
				out1.flush();
				monitor.done();
				ZipFile zipFile = new ZipFile(new File(targetDirectory, DOWNLOAD_FILE_NAME));
				int size = zipFile.size();
				monitor.beginTask("Extracting Apache Maven2 archive file.", size);
				for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
					ZipEntry entry = e.nextElement();
					if (entry.isDirectory()) {
						new File(entry.getName()).mkdirs();
					} else {
						File parent = new File(targetDirectory, entry.getName()).getParentFile();
						parent.mkdirs();
						in2 = new BufferedInputStream(zipFile.getInputStream(entry));
						out2 = new BufferedOutputStream(new FileOutputStream(new File(targetDirectory, entry.getName())));
						buf = new byte[1024];
						len = 0;
						while ((len = in2.read(buf, 0, 1024)) != -1) {
							out2.write(buf, 0, len);
						}
						out2.flush();
					}
					monitor.worked(1);
				}
			} else {
				// TODO 200以外の時の対処
				throw new IllegalStateException("Status Code = " + statusCode);
			}
		} finally {
			if (getMethod != null) {
				getMethod.releaseConnection();
			}
			if (in1 != null) {
				try {
					in1.close();
				} catch(IOException e) {
				}
			}
			if (out1 != null) {
				try {
					out1.close();
				} catch(IOException e) {
				}
			}
			if (in2 != null) {
				try {
					in2.close();
				} catch(IOException e) {
				}
			}
			if (out2 != null) {
				try {
					out2.close();
				} catch(IOException e) {
				}
			}
			File file = new File(targetDirectory, DOWNLOAD_FILE_NAME);
			if (file.exists()) {
				file.delete();
			}
			monitor.done();
		}
	}

}
