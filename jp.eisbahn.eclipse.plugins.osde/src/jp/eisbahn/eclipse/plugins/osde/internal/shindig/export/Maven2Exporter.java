package jp.eisbahn.eclipse.plugins.osde.internal.shindig.export;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

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
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
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
				in = new BufferedInputStream(getMethod.getResponseBodyAsStream(), 1024);
				out = new BufferedOutputStream(new FileOutputStream(new File(targetDirectory, DOWNLOAD_FILE_NAME)));
				int len;
				byte[] buf = new byte[1024];
				while((len = in.read(buf, 0, 1024)) != -1) {
					out.write(buf, 0, len);
					monitor.worked(len);
				}
				monitor.done();
				// TODO Archiveファイルの展開
			} else {
				// TODO 200以外の時の対処
				throw new IllegalStateException("Status Code = " + statusCode);
			}
		} finally {
			if (getMethod != null) {
				getMethod.releaseConnection();
			}
			if (in != null) {
				try {
					in.close();
				} catch(IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch(IOException e) {
				}
			}
			monitor.done();
		}
	}

}
