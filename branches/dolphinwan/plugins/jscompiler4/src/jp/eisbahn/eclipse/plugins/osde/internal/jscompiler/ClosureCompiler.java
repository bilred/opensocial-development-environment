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

package jp.eisbahn.eclipse.plugins.osde.internal.jscompiler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.base.Charsets;
import com.google.common.io.LimitInputStream;
import com.google.javascript.jscomp.BasicErrorManager;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.DefaultCodingConvention;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.WarningLevel;

/**
 * A wrapper class around the Google Closure Compiler providing a simplified
 * API for OSDE. This implementation is a simplified version of
 * <code>com.google.javascript.jscomp.AbstractCompilerRunner</code>.
 * Note that all binary dependencies to the compiler are hidden inside this
 * class. 
 *
 * @author Dolphin Chi-Ngai Wan
 */
public class ClosureCompiler implements JavaScriptCompiler {
    private final Charset inputCharset;
    private final List<String> inputFilePaths;
    private final String outputFilePath;
    private final Reporter reporter;

    /**
     * Creates an compiler instance. Note that compiler instances are not
     * reusable.
     *
     * @param input The absolute path to an input JavaScript source file.
     * @param outputFile The absolute path to compiled source file.
     * @param charset The encoding of the input file.
     * @param reporter
     */
    public ClosureCompiler(String input, String outputFile, String charset, Reporter reporter) {
        this.reporter = reporter;
        this.inputCharset = Charset.forName(charset);
        this.inputFilePaths = Collections.singletonList(input);
        this.outputFilePath = outputFile;
    }

    public InputStream compile() throws IOException {
        com.google.javascript.jscomp.Compiler.setLoggingLevel(Level.WARNING);

        Compiler compiler = createCompiler();
        CompilerOptions options = createOptions();

        JSSourceFile[] inputs = createInputs();
        JSSourceFile[] externs = createExterns();

        com.google.javascript.jscomp.Result result = compiler.compile(externs, inputs, options);

        reportIssues(result.errors, result.warnings);

        String compiledSource = result.success ? compiler.toSource() : "";
        return new ByteArrayInputStream(compiledSource.getBytes(options.outputCharset.name()));
    }

    private void reportIssues(JSError[] errors, JSError[] warnings) {
        for (JSError error : errors) {
            reporter.reportIssue(true, error.description, error.lineNumber);
        }

        for (JSError warning : warnings) {
            reporter.reportIssue(false, warning.description, warning.lineNumber);
        }
    }

    private CompilerOptions createOptions() {
        CompilerOptions options = new CompilerOptions();

        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
        WarningLevel.DEFAULT.setOptionsForWarningLevel(options);

        options.setCodingConvention(new DefaultCodingConvention());
        options.closurePass = true;
        options.jsOutputFile = outputFilePath;

        // Let the outputCharset be the same as the input charset... except if
        // we're reading in UTF-8 by default.  By tradition, we've always
        // output ASCII to avoid various hiccups with different browsers,
        // proxies and firewalls.
        options.outputCharset = (inputCharset == Charsets.UTF_8) ? Charsets.US_ASCII : inputCharset;

        return options;
    }

    /**
     * Creates a compiler replacing the default error manager because it writes
     * to stdout and stderr which affects the Eclipse's 'Console' view.
     */
    private Compiler createCompiler() {
        return new Compiler(new BasicErrorManager() {
            @Override
            public void println(CheckLevel level, JSError error) {
                // noop.
            }

            @Override
            protected void printSummary() {
                // noop.
            }
        });
    }

    /**
     * Creates a list of JavaScript files which contains browser-predefined
     * JavaScript object names that should be preserved under optimization.
     */
    private JSSourceFile[] createExterns() throws IOException {
        // The externs.zip file is bundled inside the compiler's jar.
        InputStream input = Compiler.class.getResourceAsStream("/externs.zip");
        ZipInputStream zip = new ZipInputStream(input);
        List<JSSourceFile> externs = new ArrayList<JSSourceFile>();
        for (ZipEntry entry; (entry = zip.getNextEntry()) != null;) {
            LimitInputStream entryStream = new LimitInputStream(zip, entry.getSize());
            externs.add(JSSourceFile.fromInputStream(entry.getName(), entryStream));
        }

        return externs.toArray(new JSSourceFile[externs.size()]);
    }

    private JSSourceFile[] createInputs() throws IOException {
        if (inputFilePaths.isEmpty()) {
            throw new IllegalArgumentException("source files not specified");
        }

        List<JSSourceFile> inputs = new ArrayList<JSSourceFile>(inputFilePaths.size());
        for (String filename : inputFilePaths) {
            JSSourceFile newFile = JSSourceFile.fromFile(filename, inputCharset);
            inputs.add(newFile);
        }

        return inputs.toArray(new JSSourceFile[inputs.size()]);
    }

}
