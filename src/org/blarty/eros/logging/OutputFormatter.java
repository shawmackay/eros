/*
   Copyright 2006 Eros Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package org.blarty.eros.logging;

import java.util.logging.Formatter;
import java.util.Date;
import java.text.MessageFormat;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Class that formats the <code>LogRecord</code> for output to the
 * the standard out and file output.<br>
 * This is used in the ErosHandlerImpl as the formatter.
 */
public class OutputFormatter extends Formatter {
    private String lineSep = System.getProperty("line.separator");
    private String format =
                "{0} [{1,date,dd/MM/yy} {1,time,HH:mm:ss}] ({2}) {3}";

    public String format(java.util.logging.LogRecord logRecord) {
        int startIndex = logRecord.getSourceClassName().lastIndexOf(".") + 1;
        int endIndex = logRecord.getSourceClassName().length();
        String source = logRecord.getSourceClassName().substring(startIndex, endIndex);
        StringBuffer level = new StringBuffer(logRecord.getLevel().getName());
        while( "warning".length() > level.length() ) {
            level.append(" ");
        }

        Object[] args = new Object[] { level.toString(),
                                       new Date(logRecord.getMillis()),
                                       source,
                                       logRecord.getMessage() };

        StringBuffer out = new StringBuffer(MessageFormat.format(format, args));

        if(logRecord.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                logRecord.getThrown().printStackTrace(pw);
                out.append(lineSep);
                out.append("\t");
                out.append(sw.toString());
            } catch (Exception exc) {
                //Ignore
            }
        }

        out.append(lineSep);
        return out.toString();
    }

}
