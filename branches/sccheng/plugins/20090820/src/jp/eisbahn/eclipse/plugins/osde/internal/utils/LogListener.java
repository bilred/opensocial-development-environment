package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

/**
 * Listener class that listens to logging event.
 * This listener will write logs into a file.
 * 
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class LogListener implements ILogListener {

    private FileWriter fout;
   
    public LogListener(){
        String path = System.getProperty("user.dir");
        try {
        	fout = new FileWriter(path + "\\log.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Writing log file to " + path + "\\log.log");
    }
   
    public void logging(IStatus status, String plugin) {
        try {
            fout.write(status.getSeverity() + ": " + status.getMessage());
            fout.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}