///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: Trace.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2001 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PrintStream;                                         // @D5A
import java.util.Date;
import java.util.StringTokenizer;                                   // $D0A
import java.util.Hashtable;                                         // $W1A

/**
  The Trace class logs trace points and diagnostic messages.  Each trace
  point and diagnostic message is logged by category.  The valid categories are:
  <br>
  <ul>
  <li>DATASTREAM<br>
  This category is used by Toolbox classes to log data flow between the
  local host and the remote system.  It is not intended for use by
  application classes.
  <li>DIAGNOSTIC<br>
  This category is used to log object state information.
  <li>ERROR<br>
  This category is used to log errors that cause an exception.
  <li>INFORMATION<br>
  This category is used to track the flow of control through the code.
  <li>WARNING<br>
  This category is used to log errors that are recoverable.
  <li>CONVERSION<br>
  This category is used by Toolbox classes to log character set
  conversions between Unicode and native code pages.  It is not intended
  for use by application classes.
  <li>PROXY<br>
  This category is used by Toolbox classes to log data flow between the
  client and the proxy server.  It is not intended for use by application classes.
  </ul>

  <P>
  The caller can enable or disable all tracing or specific
  trace categories.  Enabling or disabling one category does not
  affect other categories.  Once appropriate category traces
  are enabled, trace must be turned on to get trace data.  For example,

  <UL>
  <pre>
      // By default, trace is disabled for all categories and
      // tracing is off.
      ..
      Trace.setTraceErrorOn(true);          // Enable error messages
      Trace.setTraceWarningOn(true);        // Enable warning messages

      Trace.setTraceOn(true);               // Turn trace on for all
                                            // Enabled categories

      ..
      Trace.setTraceOn(false);              // Turn trace off
      ..
      Trace.setTraceOn(true);               // Turn trace back on
      ..
      Trace.setTraceInformationOn(true);    // Enable informational messages.
                                            // Since trace is still on, no
                                            // other action is required to
                                            // get this data

      ..
      Trace.setTraceWarningOn(false);       // Disable warning messages.  Since
                                            // trace is still on, the other
                                            // categories will still be logged
  </pre>
  </UL>

  The traces are logged to standard out by default.  A file name can
  be provided to log to a file.  File logging is only possible in an
  application as most browsers do not allow access to the local file system.

  <P>
  Trace data can also be specified by component.  Trace data is always
  written to the default log but component tracing provides a way
  to write trace data to a separate log.

  <P>
  The following example logs data for Function123 into log file
  c:\Function123.log, and logs data for Function456 into log file
  c:\Function456.log.  Data for these two components is also traced to
  the normal trace file (standard output in this case since that is the
  default for normal tracing).  The result is three sets of data --
  a file containing trace data for only Function123, a file containing trace
  data for only Function456, and standard output which records all trace
  data.  In the example a Java String object is used to indicate the
  component, but any object can be used.
  <UL>
  <PRE>
      // tracing is off by default
      ..
      String cmpF123 = "Function123";      // More efficient to create an object
      String cmpF456 = "Function456";      // than many String literals.

                                             // Specify where data should go
                                             // for the two components.
      Trace.setFileName(cmpF123, "c:\\Function123.log");
      Trace.setFileName(cmpF456, "c:\\Function456.log");

      Trace.setTraceInformationOn(true);     // Trace only the information category.
      Trace.setTraceOn(true);                // Turn tracing on.
      ..
      Trace.log(cmpF123, Trace.INFORMATION, "I am here");
      ..
      Trace.log(cmpF456, Trace.INFORMATION, "I am there");
      ..
      Trace.log(cmpF456, Trace.INFORMATION, "I am everywhere");
      ..
      Trace.setTraceOn(false);               // Turn tracing off.
  </PRE>
  </UL>

  <P>
  Component tracing provides an easy way to write application specific
  trace data to a log or standard output.  Any application and the
  Toolbox classes can use trace to log messages.  With component tracing,
  application data can be easily separated from other data.  For example,

  <UL>
  <pre>
      String myComponent = "com.myCompany";      // More efficient to create an object
                                                 // than many String literals.

      Trace.setFileName("c:\\bit.bucket");       // Send default trace data to
                                                 // a file.

      Trace.setTraceInformationOn(true);         // Enable information messages.
      Trace.setTraceOn(true);                    // Turn trace on.

      ...

                             // Since no file was specified, data for
                             // myComponent goes to standard output.
                             // Other information messages are sent to a file.
      Trace.log(myComponent, Trace.INFORMATION, "my trace data");

  </pre>
  </UL>


  <P>
  Two techniques can be used to log information:

  <UL>
  <pre>
      ..
      // Let the Trace method determine if logging should occur
      Trace.log(Trace.INFORMATION, "I got here...");
      ..
      // Pre-determine if we should log.  This may be more efficient
      // if a lot of processing in needed to generate the trace data.
      if (Trace.isTraceOn() && Trace.isTraceInformationOn())
      {
            Trace.log(Trace.INFORMATION, "I got here...");
      }
  </pre>
  </UL>

  <P>
  It is suggested that programs provide some mechanism to enable tracing at run-time, so
  that the modification and recompilation of code is not necessary.  Two possibilities
  for that mechanism are a command line argument (for applications) or a menu option
  (for applications and applets).

  <p>
  In addition, tracing can be set using the "com.ibm.as400.access.Trace.category"
  and "com.ibm.as400.access.Trace.file" <a href="../../../../SystemProperties.html">system properties</a>.
 **/


public class Trace
{
  private static final String copyright = "Copyright (C) 1997-2001 International Business Machines Corporation and others.";



    private static boolean traceOn_;
    private static boolean traceInfo_;
    private static boolean traceWarning_;
    private static boolean traceError_;
    private static boolean traceDiagnostic_;
    private static boolean traceDatastream_;
    private static boolean traceConversion_;
    private static boolean traceProxy_;                           // @D0A
    private static boolean traceThread_;                          // @D3A
    private static boolean traceJDBC_;                            // @D5A
    private static boolean tracePCML_;

    private static String fileName_ = null;
    private static PrintWriter destination_ = new PrintWriter(System.out, true);

    private static Hashtable printWriterHash = new Hashtable();      // @W1A
    private static Hashtable fileNameHash    = new Hashtable();      // @W1A

    /**
      Data stream trace category.  This category is used by Toolbox classes
      to log data flow between the local host and the remote system.  It is
      not intended for use by application classes.
     **/
    public  static final int DATASTREAM = 0;
    private static final int FIRST_ONE  = 0; // @W1A

    /**
      Diagnostic message trace category.  This category is used to log object
      state information.
     **/
    public static final int DIAGNOSTIC = 1;
    /**
      Error message trace category.  This category is used to log errors that
      cause an exception.
     **/
    public static final int ERROR = 2;
    /**
      Information message trace category.  This category is used to track
      the flow of control through the code.
     **/
    public static final int INFORMATION = 3;
    /**
      Warning message trace category.  This category is used to log errors
      that are recoverable.
     **/
    public static final int WARNING = 4;
    /**
      Character set conversion trace category.  This category is used by Toolbox
      classes to log conversions between Unicode and native code pages.  It is
      not intended for use by application classes.
     **/
    public static final int CONVERSION = 5;

    /**
      Proxy trace category.  This category is used by Toolbox classes to log data
      flow between the client and the proxy server.  It is not intended for
      use by application classes.
     **/
    public static final int PROXY = 6;                           // @D0A


    // This is used so we don't have to change our bounds checking every time we add a new trace category.
    private static final int LAST_ONE = 7; // @D3A

    // The following are trace categories which cannot be log()-ed to directly.
    /**
      Thread trace category.  This category is used to enable or disable tracing of thread
      information. This is useful when debugging multi-threaded applications. Trace
      information cannot be directly logged to this category.
     **/
    // @E1D private static final int THREAD = 99; // @D3A

    /**
      All trace category. This category is
      used to enable or disable tracing for all of the other categories at
      once. Trace information cannot be directly logged to this category.
    **/
    // @E1D private static final int ALL = 100; //@D2A


    // @D0A
    static
    {
        loadTraceProperties ();
    }



    // This is only here to prevent the user from constructing a Trace object.
    private Trace()
    {
    }

    /**
      Returns the trace file name.
      @return  The file name if logging to file.  If logging to System.out,
      null is returned.
     **/
    public static String getFileName()
    {
     return fileName_;
    }


    /**
      Returns the trace file name for the specified component.  Null
      is returned if no file name has been set for the component.
      @return  The file name for the specified component.  Null is
               returned if no file name has been set for the component.
     **/
    public static String getFileName(Object component)
    {
      if (component == null)
         throw new NullPointerException("component");

      return (String) fileNameHash.get(component);
    }


    /**
      Returns the PrintWriter object.
      @return  The PrintWriter object for the trace data output.
     **/
    public static PrintWriter getPrintWriter()
    {
     return destination_;
    }


    /**
      Returns the print writer object for the specified component.  Null
      is returned if no writer or file name has been set.  If a file
      name for a component is set, that component automatically
      gets a print writer.
      @return  The print writer object for the specified component.
      If no writer or file name has been set, null is returned.
     **/
    public static PrintWriter getPrintWriter(Object component)
    {
      if (component == null)
         throw new NullPointerException("component");

      return (PrintWriter) printWriterHash.get(component);
    }

    

    //@D2A
    /**
      Indicates if all of the tracing categories are enabled.
      @return  true if all categories are traced; false otherwise.
     **/
    public static final boolean isTraceAllOn()
    {
        return traceConversion_ && traceDatastream_ && traceDiagnostic_ &&
             traceError_ && traceInfo_ && traceProxy_ &&
             traceWarning_ && traceThread_ && traceJDBC_ && tracePCML_;                 //@D3C @D5C
    }


    /**
      Indicates if character set conversion tracing is enabled.
      @return  true if conversions are traced; false otherwise.
     **/
    public static final boolean isTraceConversionOn()
    {
     return traceConversion_;
    }

    /**
      Indicates if data stream tracing is enabled.
      @return  true if data streams are traced; false otherwise.
     **/
    public static final boolean isTraceDatastreamOn()
    {
     return traceDatastream_;
    }

    /**
      Indicates if diagnostic tracing is enabled.
      @return  true if diagnostic messages are traced; false otherwise.
     **/
    public static final boolean isTraceDiagnosticOn()
    {
     return traceDiagnostic_;
    }

    /**
      Indicates if error tracing is enabled.
      @return  true if error messages are traced; false otherwise.
     **/
    public static final boolean isTraceErrorOn()
    {
     return traceError_;
    }

    /**
      Indicates if information tracing is enabled.
      @return  true if information messages are traced; false otherwise.
     **/
    public static final boolean isTraceInformationOn()
    {
     return traceInfo_;
    }


    /**
     *  Indicates if JDBC tracing is enabled.
     *  @return true if JDBC messages are traced; false otherwise.
     **/
    public static final boolean isTraceJDBCOn()             // @D5A
    {
       return traceJDBC_;
    }

    /**
      Indicates if overall tracing is enabled.  If this is false, no tracing occurs.
      @return  true if tracing is enabled; false otherwise.
     **/
    public static final boolean isTraceOn()
    {
     return traceOn_;
    }


    /**
     *  Indicates if PCML tracing is enabled.
     *  @return true if PCML messages are traced; false otherwise.
     **/
    public static final boolean isTracePCMLOn()             // @D5A
    {
       return tracePCML_;
    }

    // @D0A
    /**
      Indicates if proxy tracing is enabled.
      @return  true if proxy tracing is enabled; false otherwise.
     **/
    public static final boolean isTraceProxyOn()
    {
     return traceProxy_;
    }

    // @D3A
    /**
      Indicates if thread tracing is enabled.
      @return  true if thread tracing is enabled; false otherwise.
     **/
    public static final boolean isTraceThreadOn()
    {
     return traceThread_;
    }


    /**
      Indicates if warning tracing is enabled.
      @return  true if warning messages are traced; false otherwise.
     **/
    public static final boolean isTraceWarningOn()
    {
     return traceWarning_;
    }



    // @D0A
    static void loadTraceProperties ()
    {
            // Load and apply the trace categories system property.
        String categories = SystemProperties.getProperty(SystemProperties.TRACE_CATEGORY);
        if (categories != null) {
            setTraceOn (true);
            StringTokenizer tokenizer = new StringTokenizer (categories, ", ;");
            while (tokenizer.hasMoreTokens ()) {
                String category = tokenizer.nextToken ();
                if (category.equalsIgnoreCase ("datastream"))
                    setTraceDatastreamOn (true);
                else if (category.equalsIgnoreCase ("diagnostic"))
                    setTraceDiagnosticOn (true);
                else if (category.equalsIgnoreCase ("error"))
                    setTraceErrorOn (true);
                else if (category.equalsIgnoreCase ("information"))
                    setTraceInformationOn (true);
                else if (category.equalsIgnoreCase ("warning"))
                    setTraceWarningOn (true);
                else if (category.equalsIgnoreCase ("conversion"))
                    setTraceConversionOn (true);
                else if (category.equalsIgnoreCase ("proxy"))
                    setTraceProxyOn (true);
                else if (category.equalsIgnoreCase ("thread")) //@D3A
                    setTraceThreadOn (true); //@D3A
                else if (category.equalsIgnoreCase ("jdbc"))   // @D5A
                    setTraceJDBCOn (true);   // @D5A
                else if (category.equalsIgnoreCase ("pcml"))   // @D5A
                    setTracePCMLOn (true);   // @D5A
                else if (category.equalsIgnoreCase ("all")) //@D2A
                    setTraceAllOn (true); //@D2A
                else {
                    if (isTraceOn ())
                        Trace.log (Trace.WARNING, "Trace category not valid: " + category + ".");
                }
            }
        }

        // Load and apply the trace file system property.
        String file = SystemProperties.getProperty (SystemProperties.TRACE_FILE);
        if (file != null) {
            try {
                setFileName (file);
            }
            catch (IOException e) {
                if (isTraceOn ())
                    Trace.log (Trace.WARNING, "Trace file not valid: " + file + ".", e);
            }
        }
    }

    // Log time stamp information in the trace.
    private static void logTimeStamp(Object component, PrintWriter pw) //@W1C
    {
      if (component != null)                                   //@W1A
         if (component.toString() != null)                     //@W1A
            pw.print("[" + component.toString() + "]  ");      //@W1A

      if (traceThread_)                                        //@D3A @W1C
      {                                                        //@D3A @W1C
        pw.print(Thread.currentThread().toString());           //@D3A @W1C
        pw.print("  ");                                        //@D3A @W1C
      }                                                        //@D3A @W1C

      pw.print((new Date()).toString());                       // @W1C
      pw.print("  ");                                          // @W1C
    }


    // This is the routine that actually writes to the log.
    private static final void logData(Object    component,
                                      int       category,
                                      String    message,
                                      Throwable e)
    {
       if (traceOn_ && traceCategory(category))
       {
          // Validate parameters.
          //@D2 - note: It doesn't make sense to log something to Trace.ALL,
          // so we count it as an illegal argument.
       // if (category < FIRST_ONE || category > LAST_ONE) // @D0C @D3C
       // {
       //    throw new ExtendedIllegalArgumentException("category ("
       //          + Integer.toString(category)
       //          + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID);
       // }

          // First, write to the default log
          synchronized(destination_)
          {
             // If component tracing is being used, log the component name to
             // the default log as well as the specific component log.
             if ( component != null && getFileName(component) != null )        //$W2A
             {                                                                 //$W2A
                logTimeStamp(component, destination_);                         //$W2A
                destination_.println(message);                                 //$W2A
             }                                                                 //$W2A
             else                                                              //$W2A
             {                                                                 //$W2A
                // Only trace to the default log if we are not doing component
                // tracing.  This will avoid duplicate messages in the default
                // log.
                if ( component == null )                                       //$W2A
                {                                                              //$W2A
                   logTimeStamp(null, destination_);                           //$W2A
                   destination_.println(message);                              //$W2A
                }                                                              //$W2A
             }                                                                 //$W2A

             if (e != null)
                 e.printStackTrace(destination_);
             else if (category == ERROR)
                new Throwable().printStackTrace(destination_);
          }

          if (component != null)
          {
             PrintWriter pw = (PrintWriter) printWriterHash.get(component);
             if (pw == null)
             {
                pw = new PrintWriter(System.out, true);
                printWriterHash.put(component, pw);
             }
             synchronized(pw)
             {
                logTimeStamp(component, pw);
                pw.println(message);
                if (e != null)
                   e.printStackTrace(pw);
                else if (category == ERROR)
                   new Throwable().printStackTrace(pw);
             }
          }
       }
    }

    /**
      Logs a message in the specified category.  If the category is disabled,
      nothing is logged.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC,
      ERROR, INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
     **/
    public static final void log(int category, String message)
    {
       //@W1 - validating the category was moved to the common routine

       if (message == null)
       {
           throw new NullPointerException("message");
       }

       logData(null, category, message, null);
    }


    /**
      Logs a message for the specified component for the specified category.
      If the category is disabled, nothing is logged.  If no print writer
      or file name has been set for the component, nothing is logged.
      @param  component The component to trace.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC,
      ERROR, INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
     **/
    public static final void log(Object component, int category, String message)
    {
       //@W1 - validating the category was moved to the common routine

       if (message == null)
       {
           throw new NullPointerException("message");
       }
       if (component == null)
       {
           throw new NullPointerException("component");
       }

       logData(component, category, message, null);
    }


    /**
      Logs a message in the specified category.  If the category is disabled, nothing is logged.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR, INFORMATION,
      WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
      @param  e  The Throwable object that contains the stack trace to log.
     **/
    public static final void log(int category, String message, Throwable e)
    {
       //@W1 - validating the category was moved to the common routine

       if (message == null)
       {
          throw new NullPointerException("message");
       }
       if (e == null)
       {
          throw new NullPointerException("e");
       }

       logData(null, category, message, e);
    }


    /**
      Logs a message in the specified category for the specified component.
      If the category is disabled, nothing is logged.
      @param  component The component to trace.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR,
      INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
      @param  e  The Throwable object that contains the stack trace to log.
     **/
    public static final void log(Object component, int category, String message, Throwable e)
    {
       //@W1 - validating the category was moved to the common routine
       if (message == null)
       {
          throw new NullPointerException("message");
       }
       if (e == null)
       {
          throw new NullPointerException("e");
       }
       if (component == null)
       {
          throw new NullPointerException("component");
       }

       logData(component, category, message, e);
    }

    /**
      Logs a message in the specified category.  If the category is disabled, nothing is logged.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR,
              INFORMATION, WARNING, CONVERSION, PROXY].
      @param  e  The Throwable object that contains the stack trace to log.
     **/
    public static final void log(int category, Throwable e)
    {
       if (e.getLocalizedMessage() == null)                           //$B2A
          log(category, "Exception does not contain a message.", e);  //$B2A
       else                                                           //$B2A
          log (category, e.getLocalizedMessage (), e);                //$B2C
    }


    /**
      Logs a message in the specified category for the specified component.
      If the category is disabled, nothing is logged.
      @param  component The component to trace.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR,
              INFORMATION, WARNING, CONVERSION, PROXY].
      @param  e  The Throwable object that contains the stack trace to log.
     **/
    public static final void log(Object component, int category, Throwable e)
    {
       if (e.getLocalizedMessage() == null)                                       //$B2A
          log(component, category, "Exception does not contain a message.", e);   //$B2A
       else                                                                       //$B2A
          log (component, category, e.getLocalizedMessage (), e);                 //$B2C
    }



    /**
      Logs a message and an integer value in the specified category.  If the
      category is disabled, nothing is logged.  The integer value is appended
      to the end of the message, preceded by two blanks.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR,
                        INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
      @param  value  The integer value to log.
     **/
    public static final void log(int category, String message, int value)
    {
       if (message == null)
          throw new NullPointerException("message");
       else
          log(category, message + "  " + value);
    }



    /**
      Logs a message and an integer value in the specified category for the
      specified component.  If the
      category is disabled, nothing is logged.  The integer value is appended
      to the end of the message, preceded by two blanks.
      @param  component The component to trace.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR,
                        INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
      @param  value  The integer value to log.
     **/
    public static final void log(Object component, int category, String message, int value)
    {
        if (message == null)
           throw new NullPointerException("message");
        else
           log(component, category, message + "  " + value);
    }


    /**
      Logs a SSL message based on the SSLException integer values to the specified category.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR,
                        INFORMATION, WARNING, CONVERSION, PROXY].
      @param  sslCategory The SSLException category.
      @param  sslError    The SSLException error.
      @param  sslInt1     The SSLException Int1.
     **/
    static final void logSSL(int category, int sslCategory, int sslError, int sslInt1)            //$B1A
    {
        log(Trace.ERROR, "An SSLException occurred, turn on DIAGNOSTIC tracing to see the details.");
        log(category, "SSL Category: " + sslCategory);
        log(category, "SSL Error: " + sslError);
        log(category, "SSL Int1: " + sslInt1);
    }


    /**
      Logs a message and a boolean value in the specified category.
      If the category is disabled, nothing is logged.  The boolean
      value is appended to the end of the message, preceded by two blanks.
      true is logged for true, and false is logged for false.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR,
                        INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
      @param  value  The boolean data to log.
     **/
    public static final void log(int category, String message, boolean value)
    {
       if (message == null)
         throw new NullPointerException("message");
       else
          log(category, message + "  " + value);
    }

    /**
      Logs a message and a boolean value in the specified category
      for the specified component.
      If the category is disabled, nothing is logged.  The boolean
      value is appended to the end of the message, preceded by two blanks.
      true is logged for true, and false is logged for false.
      @param  component The component to trace.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR,
                        INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
      @param  value  The boolean data to log.
     **/
    public static final void log(Object component, int category, String message, boolean value)
    {
       if (message == null)
          throw new NullPointerException("message");
       else
          log(component, category, message + "  " + value);
    }




    /**
      Logs a message and byte data in the specified category.  If the category is disabled, nothing is logged.  The byte data is appended to the end of the message, sixteen bytes per line.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR, INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
      @param  data  The bytes to log.
     **/
    public static final void log(int category, String message, byte[] data)
    {
        if (data == null)
        {
           if (message == null)                             //@D2a
              throw new NullPointerException("message");    //@D2a

           log(category, message + "  " + data);
        }
        else
        {
            log(category, message, data, 0, data.length);
        }
    }




    /**
      Logs a message and byte data in the specified category for the
      specified component.  If the category is disabled, nothing is logged.
      The byte data is appended to the end of the message, sixteen bytes per line.
      @param  component The component to trace.
      @param  category  The message category [DATASTREAM, DIAGNOSTIC, ERROR,
                        INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
      @param  data  The bytes to log.
     **/
    public static final void log(Object component, int category, String message, byte[] data)
    {
        if (data == null)
        {
            if (message == null)                             //@D2a
               throw new NullPointerException("message");    //@D2a

            log(component, category, message + "  " + data);
        }
        else
        {
            log(component, category, message, data, 0, data.length);
        }
    }



    /**
      Logs a message and byte data in the specified category.  If the
      category is disabled, nothing is logged.  The byte data is
      appended to the end of the message, sixteen bytes per line.
      @param category The message category [DATASTREAM, DIAGNOSTIC, ERROR,
                      INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
      @param  data  The bytes to log.
      @param  offset  The start offset in the data.
      @param  length  The number of bytes of data to log.
     **/

    public static final void log(int category, String message, byte[] data, int offset, int length)
    {
     // Validate parameters.
     //@D2 - note: It doesn't make sense to log something to Trace.ALL,
     //  so we count it as an illegal argument.
     //if (category < FIRST_ONE || category > LAST_ONE)                  // @D0C @D3C
     //{
     //    throw new ExtendedIllegalArgumentException("category ("
     //                  + Integer.toString(category)
     //                  + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID);
     //}
     if (message == null)
     {
         throw new NullPointerException("message");
     }
     if (data == null)
     {
         throw new NullPointerException("data");
     }

     if (traceOn_ && traceCategory(category))
     {
         synchronized(destination_)
         {
          logTimeStamp(null, destination_);
          destination_.println(message);
          printByteArray(destination_, data, offset, length);
          if (category == ERROR)
          {
              new Throwable().printStackTrace(destination_);
          }
         }
     }
    }


    /**
      Logs a message and byte data in the specified category
      for the specified component.  If the
      category is disabled, nothing is logged.  The byte data is
      appended to the end of the message, sixteen bytes per line.
      @param  component The component to trace.
      @param category The message category [DATASTREAM, DIAGNOSTIC, ERROR,
                      INFORMATION, WARNING, CONVERSION, PROXY].
      @param  message  The message to log.
      @param  data  The bytes to log.
      @param  offset  The start offset in the data.
      @param  length  The number of bytes of data to log.
     **/
    public static final void log(Object component, int category, String message,
                                 byte[] data, int offset, int length)
    {
        // Validate parameters.
        //@D2 - note: It doesn't make sense to log something to Trace.ALL,
        // so we count it as an illegal argument.
     //if (category < DATASTREAM || category > LAST_ONE)                  // @D0C @D3C
     //{
     //    throw new ExtendedIllegalArgumentException("category ("
     //                  + Integer.toString(category)
     //                  + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID);
     //}
     if (message == null)
     {
         throw new NullPointerException("message");
     }
     if (data == null)
     {
         throw new NullPointerException("data");
     }
     if (component == null)
     {
         throw new NullPointerException("category");
     }

     if (traceOn_ && traceCategory(category))
     {
        PrintWriter pw = (PrintWriter) printWriterHash.get(component);
        if (pw == null)
        {
           pw = new PrintWriter(System.out, true);
           printWriterHash.put(component, pw);
        }

        synchronized(pw)
        {
           logTimeStamp(component, pw);
           pw.println(message);
           printByteArray(pw, data, offset, length);
           if (category == ERROR)
           {
               new Throwable().printStackTrace(pw);
           }
        }
        log(category, message, data, offset, length);
     }
    }


    // Logs data from a byte array starting at offset for the length specified.
    // Output sixteen bytes per line, two hexidecimal digits per byte, one
    // space between bytes.
    private static void printByteArray(PrintWriter pw_, byte[] data, int offset, int length)
    {
     for (int i = 0; i < length; i++, offset++)
     {
         int leftDigitValue = (data[offset] >>> 4) & 0x0F;
         int rightDigitValue = data[offset] & 0x0F;
         // 0x30 = '0', 0x41 = 'A'
         char leftDigit = leftDigitValue < 0x0A ? (char)(0x30 + leftDigitValue) : (char)(leftDigitValue - 0x0A + 0x41);
         char rightDigit = rightDigitValue < 0x0A ? (char)(0x30 + rightDigitValue) : (char)(rightDigitValue - 0x0A + 0x41);
         pw_.print(leftDigit);
         pw_.print(rightDigit);
         pw_.print(" ");

         if ((i & 0x0F ) == 0x0F)
         {
          pw_.println();
         }
     }
     if (((length - 1) & 0x0F) != 0x0F)
     {
         // Finish the line of data.
         pw_.println();
     }
    }


    //@D2A
    /**
      Sets tracing for all categories on or off.  The actual tracing does
      not happen unless tracing is on.
      @param  traceAll  If true, tracing for each category is on;
                        otherwise, tracing for each category is off.
      @see  Trace#setTraceOn
     **/
    public static void setTraceAllOn(boolean traceAll)
    {
      traceConversion_ = traceAll;
      traceDatastream_ = traceAll;
      traceDiagnostic_ = traceAll;
      traceError_      = traceAll;
      traceInfo_       = traceAll;
      setTraceJDBCOn(traceAll);    // @D6C
      setTracePCMLOn(traceAll);    // @D6C
      traceProxy_      = traceAll;
      traceThread_     = traceAll; //@D3A
      traceWarning_    = traceAll;
    }


    /**
      Sets character set conversion tracing on or off.  The actual tracing
      does not happen unless tracing is on.
      @param  traceConversion  If true, conversion tracing is on;
                               otherwise, conversion tracing is off.
      @see  Trace#setTraceOn
     **/
    public static void setTraceConversionOn(boolean traceConversion)
    {
     traceConversion_ = traceConversion;
    }

    /**
      Sets data stream tracing on or off.  The actual tracing does not
      happen unless tracing is on.
      @param  traceDatastream  If true, data stream tracing is on;
                               otherwise, data stream tracing is off.
      @see  Trace#setTraceOn
     **/
    public static void setTraceDatastreamOn(boolean traceDatastream)
    {
     traceDatastream_ = traceDatastream;
    }

    /**
      Sets diagnostic tracing on or off.  The actual tracing does not
      happen unless tracing is on.
      @param  traceDiagnostic  If true, diagnostic tracing is on;
                               otherwise, diagnostic tracing is off.
      @see  Trace#setTraceOn
     **/
    public static void setTraceDiagnosticOn(boolean traceDiagnostic)
    {
     traceDiagnostic_ = traceDiagnostic;
    }

    /**
      Sets error tracing on or off.  The actual tracing does not happen
      unless tracing is on.
      @param  traceError  If true, error tracing is on; otherwise,
                          error tracing is off.
      @see  Trace#setTraceOn
     **/
    public static void setTraceErrorOn(boolean traceError)
    {
     traceError_ = traceError;
    }

    /**
      Sets the trace file name.  If the file exists, output is appended to
      it.  If the file does not exist, it is created.
      @param  fileName  The log file name.  If this is null, output goes to System.out.
      @exception  IOException  If an error occurs while accessing the file.
     **/
    public static synchronized void setFileName(String fileName) throws IOException
    {
        // Flush the current destination stream.
     destination_.flush();

     if (fileName != null)
     {
            // Create a FileOutputStream and PrintWriter to handle the trace data.  If the file exists we want to append to it.
         File file = new File(fileName);
         FileOutputStream os = new FileOutputStream(fileName, file.exists());
         destination_ = new PrintWriter(os, true);

         PrintStream ps = new PrintStream(os, true);         // @D5A
         java.sql.DriverManager.setLogStream(ps);            // @D5A
         com.ibm.as400.data.PcmlMessageLog.setLogStream(ps); // @D5A

         fileName_ = fileName;
     }
     else
     {
         if (fileName_ != null)
            destination_.close();

         PrintStream ps = new PrintStream(System.out, true); // @D5A
         java.sql.DriverManager.setLogStream(ps);            // @D5A
         com.ibm.as400.data.PcmlMessageLog.setLogStream(ps); // @D5A

         fileName_ = null;
         destination_ = new PrintWriter(System.out, true);
     }
    }


    /**
      Sets the trace file name for the specified component.
      If the file exists, output is appended to
      it.  If the file does not exist, it is created.
      @param  fileName  The log file name.  If this is null, output goes to System.out.
      @param  component Trace data for this component goes to file <code>fileName</code>.
      @exception  IOException  If an error occurs while accessing the file.
     **/
    public static synchronized void setFileName(Object component, String fileName)
                               throws IOException
    {
       if (component == null)
          throw new NullPointerException("component");

       PrintWriter pw = (PrintWriter) printWriterHash.remove(component);
       if (pw != null)
          pw.flush();

       String oldName = (String) fileNameHash.remove(component);
       if (oldName != null)
          pw.close();

       if (fileName != null)
       {
          // Create a FileOutputStream and PrintWriter to handle the trace data.  If the file exists we want to append to it.
          File file = new File(fileName);
          FileOutputStream os = new FileOutputStream(fileName, file.exists());
          fileNameHash.put(component, fileName);
          pw = new PrintWriter(os, true);
          printWriterHash.put(component, pw); 
       }
       else
       {
          pw = new PrintWriter(System.out, true);
          printWriterHash.put(component, pw);
       }
    }



    /**
      Sets the PrintWriter object.  All further trace output is sent to it.
      @param  obj  The PrintWriter object.  If this is null, output goes to System.out.
      @exception  IOException  If an error occurs while accessing the file.
     **/
    public static synchronized void setPrintWriter(PrintWriter obj) throws IOException
    {
     // Flush the current destination stream.
     destination_.flush();

     if (fileName_ != null)
     {
         destination_.close();
         fileName_ = null;
     }

     if (obj != null)
         destination_ = obj;
     else
         destination_ = new PrintWriter(System.out, true);
    }

    /**
      Sets the PrintWriter object for the specified component.
      All further trace output for this component is sent to the writer.
      @param  component Trace data for this component goes to writer <code>obj</code>.
      @param  obj  The PrintWriter object.  If this is null, output goes to System.out.
      @exception  IOException  If an error occurs while accessing the file.
     **/
    public static synchronized void setPrintWriter(Object component, PrintWriter obj)
                               throws IOException
    {
       if (component == null)
          throw new NullPointerException("component");

       PrintWriter pw = (PrintWriter) printWriterHash.remove(component);
       if (pw != null)
          pw.flush();

       String fileName = (String) fileNameHash.remove(component);
       if (fileName != null)
          pw.close();

       if (obj != null)
           pw = obj;
       else
           pw = new PrintWriter(System.out, true);

       printWriterHash.put(component, pw);
    }


    /**
      Sets information tracing on or off.  The actual tracing does
      not happen unless tracing is on.
      @param  traceInformation  If true, information tracing is on;
              otherwise, information tracing is off.
      @see  Trace#setTraceOn
     **/
    public static void setTraceInformationOn(boolean traceInformation)
    {
     traceInfo_ = traceInformation;
    }


    /**
     *  Sets JDBC tracing on or off.  The actual tracing does not happend
     *  unless tracing is on.
     *  @param traceJDBC If true, JDBC tracing is on; otherwise,
     *                   JDBC tracing is off.
     **/
    public static void setTraceJDBCOn(boolean traceJDBC)           // @D5A
    {
       traceJDBC_ = traceJDBC;
       
       if (traceJDBC)
       {  
          if (fileName_ == null)
             java.sql.DriverManager.setLogStream(new PrintStream(System.out, true));
       }
       else
          java.sql.DriverManager.setLogStream(null);
    }


    /**
      Sets tracing on or off.  When this is off nothing is logged in any
      category, even those that are on.  When this is on, tracing occurs
      for all categories that are also on.
      @param  traceOn  If true, tracing is on; otherwise, all tracing is disabled.
     **/
    public static void setTraceOn(boolean traceOn)
    {  traceOn_ = traceOn;
       if (traceOn_)                                    //$D1A
          destination_.println("Toolbox for Java - " + Copyright.version);   // @A1C //@W1A //@D4C
    }


    /**
     *  Sets PCML tracing on or off.  The actual tracing does not happend
     *  unless tracing is on.
     *  @param tracePCML If true, PCML tracing is on; otherwise,
     *                   PCML tracing is off.
     **/
    public static void setTracePCMLOn(boolean tracePCML)           // @D5A
    {
        tracePCML_ = tracePCML;

        try                                                                                                                            // @D7A
        {                                                                                                                               // @D7A
            com.ibm.as400.data.PcmlMessageLog.setTraceEnabled(tracePCML);                                           
        }                                                                                                                               // @D7A
        catch (NoClassDefFoundError e)                                                                                         // @D7A
        {                                                                                                                               // @D7A
            destination_.println("Unable to enable PCML tracing:  NoClassDefFoundError - PcmlMessageLog");    // @D7A
        }                                                                                                                               // @D7A
    }


    // @D0A
    /**
      Sets proxy stream tracing on or off.  The actual tracing does not
      happen unless tracing is on.
      @param  traceProxy  If true, proxy tracing is on;
                          otherwise, proxy tracing is off.
      @see  Trace#setTraceOn
     **/
    public static void setTraceProxyOn(boolean traceProxy)
    {
     traceProxy_ = traceProxy;
    }

    // @D3A
    /**
      Sets thread tracing on or off.  The actual tracing does not happen
      unless tracing is on.
      @param  traceError  If true, thread tracing is on;
                          otherwise, thread tracing is off.
      @see  Trace#setTraceOn
     **/
    public static void setTraceThreadOn(boolean traceThread)
    {
     traceThread_ = traceThread;
    }


    /**
      Sets warning tracing on or off.  The actual tracing does not happen
      unless tracing is enabled.
      @param  traceWarning  If true, warning tracing is enabled;
                            otherwise, warning tracing is disabled.
      @see  Trace#setTraceOn
     **/
    public static void setTraceWarningOn(boolean traceWarning)
    {
     traceWarning_ = traceWarning;
    }

    // Indicates if this category is being traced or not.
    private static boolean traceCategory(int category)        // @D5C
    {
     boolean trace = false;
     switch (category)
     {
         case INFORMATION:
          trace = traceInfo_;
          break;
         case WARNING:
          trace = traceWarning_;
          break;
         case ERROR:
          trace = traceError_;
          break;
         case DIAGNOSTIC:
          trace = traceDiagnostic_;
          break;
         case DATASTREAM:
          trace = traceDatastream_;
          break;
         case CONVERSION:
          trace = traceConversion_;
          break;
         case PROXY:                   
          trace = traceProxy_;         
          break;                       
         default:
          throw new ExtendedIllegalArgumentException("category ("
                   + Integer.toString(category)
                   + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID);
     }

     return trace;
    }
}
