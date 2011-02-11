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


/**
 * Title:        Eros Logging Service<p>
 * Description:  Project to provide distributed system logging.<p>
 * Copyright:    Copyright (c) C. Lunn<p>
 * Company:      Countrywide Assured<p>
 * @author C. Lunn
 * @version 1.0
 */
package org.jini.projects.eros;

import java.io.File;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Date;

/**
 * Class that provides all the file input/output
 * functions required for the client side implementation.
 * Provides all the file creation and deletion for logging detail persistance.
 * <p>
 * Also provides the file monitoring for persisted logging details that have
 * not been logged successfully.  Any old process files will also be attempted
 * to be logged.
 */
public class ErosFileProcessor implements Runnable, Serializable {
    static final long serialVersionUID = 6293306778373245298L;
    private final static String FILE_PREFIX = "LoggingDetail_";
    private static boolean debug = Boolean.getBoolean("org.jini.projects.eros.debug");
    private static ErosFileProcessor fileProcessor;
    private ErosServiceMonitor monitor = null;

    /**
     * Private constructor to implement Singleton method.
     */
    private ErosFileProcessor(ErosServiceMonitor monitor) {
        this.monitor = monitor;
    }

    /** 
     * Singleton creation method.
     * @param monitor The service monitor to use for any persisted details.
     */
    public static synchronized void startFileProcessor(ErosServiceMonitor monitor) {
        if( fileProcessor == null ) {
            fileProcessor = new ErosFileProcessor(monitor);
            Thread t = new Thread(fileProcessor);
            t.start();
        }
    }

    /** 
     * Serializes the Entry supplied to disk.  Upto 10 attempts will be made
     * to persist the object before an exception is thrown.
     * @param logDetail The entry that should be persisted to disk
     * @return The <code>File</code> reference that contains
     *          the persisted <code>LogDetail</code> entry.
     */    
    public static File persistDetails(LogDetail logDetail) {
        File outFile = null;
        try {
            //Try and create a new logging file, only allow 10 retries for
            //this operation.
            int retries = 0;
            boolean success = false;
            while( !success && retries++ < 10) {
                outFile = new File(System.getProperty("user.dir") +
                                                File.separator +
                                                FILE_PREFIX +
                                                Math.random() + ".obj");
                success = outFile.createNewFile();
            }

            //Only write to the output file if a new file was successfully
            //created.  Otherwise could be overwriting an existing file.
            if( success ) {
                ObjectOutputStream oos = new ObjectOutputStream(
                                           new BufferedOutputStream(
                                                new FileOutputStream(outFile)));
                oos.writeObject(logDetail);
                oos.flush();
                oos.close();

                if(debug) System.out.println(   "ErosFileProcessor" + 
                                                " - Output file created - " +
                                                outFile.getAbsolutePath());

            } else {
                if(debug) System.out.println(   "ErosFileProcessor" + 
                                                " - No output file created, " +
                                                "detail not persisted");
                outFile = null;
            }

        } catch (Exception exc) {
            if(debug) {
                System.out.println( "ErosFileProcessor" + 
                        " - Error occurred persisting logging details");
                exc.printStackTrace();
            }
        }

        return outFile;
    }

    /** 
     * Creates a physical file for the <code>File</code> reference
     * supplied.  A new error file will be created and the file reference
     * supplied copied to it.
     * @param detailFile The reference for which a physical file should
     *                   be created
     * @return Indicates whether the file creation was successful
     */    
    public static boolean createErrorFile(File detailFile) {
        boolean success = false;
        try {
            //Try and create a new error file, only allow 10 retries for
            //this operation.
            int retries = 0;
            File errorFile = null;
            while( !success && retries++ < 10) {
                errorFile = new File(System.getProperty("user.dir") +
                                                File.separator +
                                                FILE_PREFIX +
                                                Math.random() + ".err");
                if( !errorFile.exists() )
                    success = detailFile.renameTo(errorFile);
            }
            if( success ) {
                if(debug) System.out.println(   "ErosFileProcessor" +
                                                " - Error file created - " +
                                                errorFile.getAbsolutePath());
            } else {
                if(debug) System.out.println("ErosFileProcessor" + 
                                            " - Unable to create error file.");
            }

        } catch (Exception exc) {
            if(debug) {
                System.out.println("ErosFileProcessor" + 
                                    " - Error occured creating error file.");
                exc.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Deletes the physical file for the file reference supplied
     * @param file The reference for the file that should be deleted
     */    
    public static void deleteFile(File file) {
        try {
            if( !file.delete() ) {
                if(debug) System.out.println("ErosFileProcessor" + 
                                            " - Error occured deleting file.");
            } else {
                if(debug) System.out.println("ErosFileProcessor" + 
                                            " - File deleted successfully - " +
                                            file.getAbsolutePath());
            }

        } catch (Exception exc) {
            if(debug) {
                System.out.println("ErosFileProcessor" + 
                                    " - Error occured deleting file.");
                exc.printStackTrace();
            }
        }
    }

    /**
     * Starts the processor which monitors for files that need
     * processing or are in error.
     */    
    public void run() {
        while(true) {
            try {
                File errorFileDir = new File(System.getProperty("user.dir"));
                File[] errorFiles = errorFileDir.listFiles(new ErrorFilenameFilter(".err"));
                for(int i=0; i < errorFiles.length; i++) {
                    //Keep each file in it's own error block, otherwise if first
                    //file always causes an error, no other files will be processed.
                    ObjectInputStream ois = null;
                    try {
                        //Check the file still exists. Could possibly have two
                        //threads processing the same set of files.
                        if( errorFiles[i].exists() && errorFiles[i].canRead() ) {
                            String path = errorFiles[i].getAbsolutePath();
                            path = path.substring(0, path.lastIndexOf(".err")) + ".prc";
                            File procFile = new File(path);
                            //Rename to ensure only one process has this file
                            if( errorFiles[i].renameTo(procFile) ) {
                                ois = new ObjectInputStream(
                                            new BufferedInputStream(
                                                new FileInputStream(procFile)));
                                Object obj = ois.readObject();
                                ois.close();
                                if( obj instanceof LogDetail ) {
                                    if( monitor.log((LogDetail)obj) ) {
                                        this.deleteFile(procFile);
                                        if(debug) System.out.println(
                                                "ErosFileProcessor" +
                                                " - Error file logged with server.");
                                    } else {
                                        boolean success = procFile.renameTo(errorFiles[i]);
                                        if(debug) {
                                            System.out.println("ErosFileProcessor" + 
                                                " - Unable to log error file with " + 
                                                "server. Error file restore " +
                                                "success = " + success);
                                        }
                                    }
                                } else {
                                    if(debug) System.out.println(
                                                "ErosFileProcessor" + 
                                                " - Error file doesn't contain " +
                                                "LogDetail object.");
                                }
                            } else {
                                if(debug) System.out.println("ErosFileProcessor" + 
                                                    " - Can't rename error file");
                            }
                        }
                    } catch (Exception exc) {
                        if(debug) exc.printStackTrace();
                        try {
                            if( ois != null)
                                ois.close();
                        } catch (Exception oisExc) {
                            if(debug) oisExc.printStackTrace();
                        }
                    }
                }

                //Then check for old process files that require changing back
                //to error files
                File[] processFiles = new File(System.getProperty("user.dir") +
                        File.separator).listFiles(new ErrorFilenameFilter(".prc"));
                for(int i=0; i < processFiles.length; i++) {
                    try {
                        long time = processFiles[i].lastModified() + (5 * 60 * 1000);
                        if(time < new Date().getTime()) {
                            if(debug) System.out.println("ErosFileProcessor" + 
                                        " - Trying to rename old process file");
                            this.createErrorFile(processFiles[i]);
                        }
                    } catch (Exception exc) {
                        if(debug) exc.printStackTrace();
                    }
                }

                //Then check for old object files that require changing back
                //to error files
                File[] objectFiles = new File(System.getProperty("user.dir") +
                        File.separator).listFiles(new ErrorFilenameFilter(".obj"));
                for(int i=0; i < objectFiles.length; i++) {
                    try {
                        long time = objectFiles[i].lastModified() + (5 * 60 * 1000);
                        if(time < new Date().getTime()) {
                            if(debug) System.out.println("ErosFileProcessor" + 
                                        " - Trying to rename old object file");
                            this.createErrorFile(objectFiles[i]);
                        }
                    } catch (Exception exc) {
                        if(debug) exc.printStackTrace();
                    }
                }
                                
            } catch (Exception exc) {
                if(debug) exc.printStackTrace();
            }

            //Pause processing after each iteration through the error files.
            try {
                Thread.sleep(10 * 1000);
            } catch (Exception exc) {}

        }
    }

    /**
     * <code>FilenameFilter</code> implementation that filters
     * for error files as defined by the Eros project.
     */        
    class ErrorFilenameFilter implements FilenameFilter, Serializable {
        private String extension = null;
        
        /**
         * Creates the filter.
         * @param extension The extension of the error files
         */
        public ErrorFilenameFilter(String extension) {
            this.extension = extension;
        }

        /**
         * Determines if the file supplied is a valid error file
         * @param file The <code>File</code> reference
         * @param name The name of the file to check
         * @return If the file supplied is a valid error file
         */        
        public boolean accept(File file, String name) {
            return name.startsWith(FILE_PREFIX) && name.endsWith(extension);
        }
    }

}
