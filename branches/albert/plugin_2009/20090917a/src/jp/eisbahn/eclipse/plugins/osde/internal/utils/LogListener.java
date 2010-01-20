/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

/**
 * Listener class that listens to logging event.
 * This listener writes logs into a file.
 * 
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class LogListener implements ILogListener {

    private FileWriter fout;
    private String path; // path under which the log file is located
   
    public LogListener() {
        path = System.getProperty("java.io.tmpdir");
        openOutputStream();
    }
    
    public LogListener(String path) {
    	this.path = path;
    	openOutputStream();
    }
    
    public void openOutputStream() {
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
    
    /**
     * Closes the file writer to ensure there will be no memory leak
     * This function is called before the activator detaches the log listener
     */
    public void dispose() {
    	try {
    		fout.close();
    	} catch (IOException ioe) {
    		System.err.println("Can't close the file writer in the log listener.");
    		ioe.printStackTrace();
    	}
    }
}