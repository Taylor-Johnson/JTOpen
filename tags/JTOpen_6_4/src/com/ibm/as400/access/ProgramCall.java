///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  ProgramCall.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2003 International Business Machines Corporation and
// others.  All rights reserved.
//
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Vector;

import com.ibm.as400.resource.RJob;

/**
 The ProgramCall class allows a user to call an i5/OS system program, pass parameters to it (input and output), and access data returned in the output parameters after the program runs.  Use ProgramCall to call programs.  To call service programs, use ServiceProgramCall.
 <P>The following example demonstrates the use of Program Call:
 <br>
 <pre>
 *    // Call programs on system named "Hal."
 *    AS400 system = new AS400("Hal");
 *    ProgramCall program = new ProgramCall(system);
 *    try
 *    {
 *        // Initialize the name of the program to run.
 *        String programName = "/QSYS.LIB/TESTLIB.LIB/TESTPROG.PGM";
 *        // Set up the 3 parameters.
 *        ProgramParameter[] parameterList = new ProgramParameter[3];
 *        // First parameter is to input a name.
 *        AS400Text nametext = new AS400Text(8);
 *        parameterList[0] = new ProgramParameter(nametext.toBytes("John Doe"));
 *        // Second parameter is to get the answer, up to 50 bytes long.
 *        parameterList[1] = new ProgramParameter(50);
 *        // Third parameter is to input a quantity and return a value up to 30 bytes long.
 *        byte[] quantity = new byte[2];
 *        quantity[0] = 1;  quantity[1] = 44;
 *        parameterList[2] = new ProgramParameter(quantity, 30);
 *        // Set the program name and parameter list.
 *        program.setProgram(programName, parameterList);
 *        // Run the program.
 *        if (program.run() != true)
 *        {
 *            // Report failure.
 *            System.out.println("Program failed!");
 *            // Show the messages.
 *            AS400Message[] messagelist = program.getMessageList();
 *            for (int i = 0; i < messagelist.length; ++i)
 *            {
 *                // Show each message.
 *                System.out.println(messagelist[i]);
 *            }
 *        }
 *        // Else no error, get output data.
 *        else
 *        {
 *            AS400Text text = new AS400Text(50);
 *            System.out.println(text.toObject(parameterList[1].getOutputData()));
 *            System.out.println(text.toObject(parameterList[2].getOutputData()));
 *        }
 *    }
 *    catch (Exception e)
 *    {
 *        System.out.println("Program " + program.getProgram() + " issued an exception!");
 *        e.printStackTrace();
 *    }
 *    // Done with the system.
 *    system.disconnectAllServices();
 </pre>
 <p>NOTE:  When getting the AS400Message list from programs, users no longer have to create a MessageFile to obtain the program help text.  The load() method can be used to retrieve additional message information. Then the getHelp() method can be called directly on the AS400Message object returned from getMessageList().  Here is an example:
 <pre>
 *    if (program.run("myPgm", myParmList) != true)
 *    {
 *        // Show messages.
 *        AS400Message[] messageList = program.getMessageList();
 *        for (int i = 0; i < messageList.length; ++i)
 *        {
 *            // Show each message.
 *            System.out.println(messageList[i].getText());
 *            // Load additional message information.
 *            messageList[i].load();
 *            //Show help text.
 *            System.out.println(messageList[i].getHelp());
 *        }
 *    }
 </pre>
 <p>NOTE:  When the program runs within the host server job, the library list will be the initial library list specified in the job description in the user profile.
 @see  ProgramParameter
 @see  AS400Message
 @see  ServiceProgramCall
 **/
public class ProgramCall implements Serializable
{
    private static final String CLASSNAME = "com.ibm.as400.access.ProgramCall";
    static
    {
        if (Trace.traceOn_) Trace.logLoadPath(CLASSNAME);
    }

    static final long serialVersionUID = 4L;

    // Constants that indicate how thread safety was determined.
    private static final int BY_DEFAULT = 0;
    private static final int BY_PROPERTY = 1;
    private static final int BY_SET_METHOD = 2;

    // The system where the program is located.
    AS400 system_ = null;
    // The full IFS path name of the program.
    String program_ = "";
    // The library that contains the program.
    String library_ = "";
    // The name of the program.
    String name_ = "";
    // Program parameters.
    ProgramParameter[] parameterList_ = new ProgramParameter[0];
    // The messages returned by the program.
    AS400Message[] messageList_ = new AS400Message[0];
    // Thread safety of program.
    boolean threadSafety_ = false;
    // How thread safety was determined.
    private int threadSafetyDetermined_ = BY_DEFAULT;
    // The number of messages to retrieve.
    int messageOption_ = AS400Message.MESSAGE_OPTION_UP_TO_10;  // Default for compatibility.

    // Implemenation object shared with command call, interacts with server or native methods.
    transient RemoteCommandImpl impl_ = null;

    // List of action completed event bean listeners.
    transient Vector actionCompletedListeners_ = null;  // Set on first add.
    // List of property change event bean listeners.
    transient PropertyChangeSupport propertyChangeListeners_ = null;  // Set on first add.
    // List of vetoable change event bean listeners.
    transient VetoableChangeSupport vetoableChangeListeners_ = null;  // Set on first add.

    /**
     Constructs a ProgramCall object.  The system, program, and parameters must be set before using any method requiring a connection to the system.
     **/
    public ProgramCall()
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Constructing ProgramCall object.");
        checkThreadSafetyProperty();
    }

    /**
     Constructs a ProgramCall object.  It uses the specified system. The program and parameters must be provided later.
     @param  system  The system on which to run the program.
     **/
    public ProgramCall(AS400 system)
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Constructing ProgramCall object, system: " + system);
        if (system == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'system' is null.");
            throw new NullPointerException("system");
        }

        system_ = system;
        checkThreadSafetyProperty();
    }

    /**
     Constructs a program call object.  It uses the specified system, program name, and parameter list.
     @param  system  The system on which to run the program.
     @param  program  The program name as a fully qualified path name in the library file system.  The library and program name must each be 10 characters or less.
     @param  parameterList  A list of up to 35 parameters with which to run the program.
     **/
    public ProgramCall(AS400 system, String program, ProgramParameter[] parameterList)
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Constructing ProgramCall object, system: " + system + " program: " + program);
        if (system == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'system' is null.");
            throw new NullPointerException("system");
        }

        system_ = system;
        checkThreadSafetyProperty();

        try
        {
            setProgram(program, parameterList);
        }
        catch (PropertyVetoException e)
        {
            Trace.log(Trace.ERROR, "Unexpected PropertyVetoException:", e);
            throw new InternalErrorException(InternalErrorException.UNEXPECTED_EXCEPTION);
        }
    }

    /**
     Adds an ActionCompletedListener.  The specified ActionCompletedListener's <b>actionCompleted</b> method will be called each time a program has run.  The ActionCompletedListener object is added to a list of ActionCompletedListeners managed by this ProgramCall.  It can be removed with removeActionCompletedListener.
     @param  listener  The listener object.
     **/
    public void addActionCompletedListener(ActionCompletedListener listener)
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Adding action completed listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        synchronized (this)
        {
            // If first add.
            if (actionCompletedListeners_ == null)
            {
                actionCompletedListeners_ = new Vector();
            }
            actionCompletedListeners_.addElement(listener);
        }
    }

    /**
     Adds a ProgramParameter to the parameter list.
     @param  parameter  The ProgramParameter.
     @exception  PropertyVetoException  If the change is vetoed.
     **/
    public void addParameter(ProgramParameter parameter) throws PropertyVetoException
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Adding parameter to parameter list.");
        if (parameter == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'parameter' is null.");
            throw new NullPointerException("parameter");
        }

        int oldLength = parameterList_.length;
        ProgramParameter[] newParameterList = new ProgramParameter[oldLength + 1];
        System.arraycopy(parameterList_, 0, newParameterList, 0, oldLength);
        newParameterList[oldLength] = parameter;
        setParameterList(newParameterList);
    }

    /**
     Adds a PropertyChangeListener.  The specified PropertyChangeListener's <b>propertyChange</b> method will be called each time the value of any bound property is changed.  The PropertyChangeListener object is added to a list of PropertyChangeListeners managed by this ProgramCall.  It can be removed with removePropertyChangeListener.
     @param  listener  The listener object.
     **/
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Adding property change listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        synchronized (this)
        {
            // If first add.
            if (propertyChangeListeners_ == null)
            {
                propertyChangeListeners_ = new PropertyChangeSupport(this);
            }
            propertyChangeListeners_.addPropertyChangeListener(listener);
        }
    }

    /**
     Adds a VetoableChangeListener.  The specified VetoableChangeListener's <b>vetoableChange</b> method will be called each time the value of any constrained property is changed.
     @param  listener  The listener object.
     **/
    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Adding vetoable change listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        synchronized (this)
        {
            // If first add.
            if (vetoableChangeListeners_ == null)
            {
                vetoableChangeListeners_ = new VetoableChangeSupport(this);
            }
            vetoableChangeListeners_.addVetoableChangeListener(listener);
        }
    }

    // Chooses the appropriate implementation, synchronize to protect impl_ object.
    synchronized void chooseImpl() throws AS400SecurityException, IOException
    {
        if (system_ != null) system_.signon(false);
        if (impl_ == null)
        {
            if (system_ == null)
            {
                /*
                if (AS400.onAS400)
                {
                    impl_ = (RemoteCommandImpl)AS400.loadImpl("com.ibm.as400.access.RemoteCommandImplNative");
                    if (impl_ != null) return;
                }*/
                Trace.log(Trace.ERROR, "Attempt to connect to command server before setting system." );
                throw new ExtendedIllegalStateException("system", ExtendedIllegalStateException.PROPERTY_NOT_SET);
            }

            impl_ = (RemoteCommandImpl)system_.loadImpl3("com.ibm.as400.access.RemoteCommandImplNative", "com.ibm.as400.access.RemoteCommandImplRemote", "com.ibm.as400.access.RemoteCommandImplProxy");
            impl_.setSystem(system_.getImpl());
        }
    }

    // Fires the action completed event.
    void fireActionCompleted()
    {
        Vector targets = (Vector)actionCompletedListeners_.clone();
        ActionCompletedEvent event = new ActionCompletedEvent(this);
        for (int i = 0; i < targets.size(); ++i)
        {
            ActionCompletedListener target = (ActionCompletedListener)targets.elementAt(i);
            target.actionCompleted(event);
        }
    }

    /**
     Returns an RJob object which represents the server job in which the program will be run.  The information contained in the RJob object is invalidated by <code>AS400.disconnectService()</code> or <code>AS400.disconnectAllServices()</code>.
     <br>Typical uses include:
     <br>(1) before run() to identify the job before calling the program;
     <br>(2) after run() to see what job the program ran under (to identify the job log, for example).
     <p><b>Note:</b> This method is not supported in the Toolbox proxy environment.
     @return  The job in which the program will be run.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the system.
     @exception  InterruptedException  If this thread is interrupted.
     @deprecated  Use getServerJob() instead.
     **/
    public RJob getJob() throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Getting job.");
        chooseImpl();
        String jobInfo = impl_.getJobInfo(threadSafety_);
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Constructing RJob for job: " + jobInfo);
        // Contents of the "job information" string:  The name of the user job that the thread is associated with.  The format of the job name is a 10-character simple job name, a 10-character user name, and a 6-character job number.
        return new RJob(system_, jobInfo.substring(0, 10).trim(), jobInfo.substring(10, 20).trim(), jobInfo.substring(20, 26).trim());
    }

    /**
     Returns the list of messages returned from running the program.  It will return an empty list if the program has not been run yet or if there are no messages.
     @return  The array of messages returned by the program.
     **/
    public AS400Message[] getMessageList()
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Getting message list.");
        return messageList_;
    }

    /**
     Returns the option for how many messages will be retrieved.
     @return  A constant indicating how many messages will be retrieved.  Valid values are:
     <ul>
     <li>AS400Message.MESSAGE_OPTION_UP_TO_10
     <li>AS400Message.MESSAGE_OPTION_NONE
     <li>AS400Message.MESSAGE_OPTION_ALL
     </ul>
     **/
    public int getMessageOption()
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Getting message option:", messageOption_);
        return messageOption_;
    }

    /**
     Returns the list of parameters.  It will return an empty list if not previously set.
     @return  The list of parameters.
     **/
    public ProgramParameter[] getParameterList()
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Getting parameter list.");
        return parameterList_;
    }

    /**
     Returns the integrated file system pathname for the program.  It will return an empty string ("") if not previously set.
     @return  The integrated file system pathname for the program.
     **/
    public String getProgram()
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Getting program: " + program_);
        return program_;
    }

    /**
     Returns a Job object which represents the server job in which the program will be run.
     The information contained in the Job object is invalidated by <code>AS400.disconnectService()</code> or <code>AS400.disconnectAllServices()</code>.
     <br>Typical uses include:
     <br>(1) before run() to identify the job before calling the program;
     <br>(2) after run() to see what job the program ran under (to identify the job log, for example).
     <p><b>Note:</b> This method is not supported in the Toolbox proxy environment.
     @return  The job in which the program will be run.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the system.
     @exception  InterruptedException  If this thread is interrupted.
     @see #getJob
     **/
    public Job getServerJob() throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Getting job.");
        chooseImpl();
        String jobInfo = impl_.getJobInfo(threadSafety_);
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Constructing Job for job: " + jobInfo);
        // Contents of the "job information" string:  The name of the user job that the thread is associated with.  The format of the job name is a 10-character simple job name, a 10-character user name, and a 6-character job number.
        return new Job(system_, jobInfo.substring(0, 10).trim(), jobInfo.substring(10, 20).trim(), jobInfo.substring(20, 26).trim());
    }

    /**
     Returns the system on which the program is to be run.
     @return  The system on which the program is to be run.  If the system has not been set, null is returned.
     **/
    public AS400 getSystem()
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Getting system: " + system_);
        return system_;
    }

    /**
     Returns the thread on which the program would be run, if it were to be called on-thread.  Returns null if either:
     <ul compact>
     <li> The client is communicating with the system through sockets.
     <li> The program has not been marked as thread safe.
     </ul>
     @return  The thread on which the program would be run.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  IOException  If an error occurs while communicating with the system.
     **/
    public Thread getSystemThread() throws AS400SecurityException, IOException
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Getting system thread.");
        chooseImpl();
        Thread currentThread = impl_.getClass().getName().endsWith("ImplNative") ? Thread.currentThread() : null;
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "System thread: " + currentThread);
        return currentThread;
    }

    static boolean isThreadSafetyPropertySet()
    {
        return getThreadSafetyProperty() != null;
    }

    static String getThreadSafetyProperty()
    {
        return SystemProperties.getProperty(SystemProperties.PROGRAMCALL_THREADSAFE);
    }

    // Check thread safety system property.
    private void checkThreadSafetyProperty()
    {
        String property = getThreadSafetyProperty();
        if (property == null)  // Property not set.
        {
            if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Thread safe system property not set, thread safety property remains unspecified.");
        }
        else
        {
            threadSafety_ = property.equalsIgnoreCase("true");
            threadSafetyDetermined_ = BY_PROPERTY;
            if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Thread safe system property: " +  property);
        }
    }

    /**
     Indicates whether or not the program will actually get run on the current thread.
     <br>Note: If the program is run on-thread, it will run in a different job than if it were run off-thread.
     @return  true if the program will be run on the current thread; false otherwise.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the system.
     @exception  InterruptedException  If this thread is interrupted.
     **/
    public boolean isStayOnThread() throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Checking if program will actually get run on the current thread.");
        chooseImpl();
        boolean isStayOnThread = (threadSafety_ && impl_.getClass().getName().endsWith("ImplNative"));
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Program will actually get run on the current thread: ", isStayOnThread);
        return isStayOnThread;
    }

    /**
     Indicates whether or not the program will be assumed thread-safe, according to the settings specified by <code>setThreadSafe()</code> or the <code>com.ibm.as400.access.ProgramCall.threadSafe</code> property.
     <br>Note: If the program is run on-thread, it will run in a different job than if it were run off-thread.
     @return  true if the program will be assumed thread-safe; false otherwise.
     @see  #isStayOnThread
     **/
    public boolean isThreadSafe()
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Checking if program will be assumed thread-safe: " + threadSafety_);
        return threadSafety_;
    }

    // Deserializes and initializes the transient data.
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "De-serializing ProgramCall object.");
        in.defaultReadObject();

        // impl_ remains null.
        // actionCompletedListeners_ remains null.
        // propertyChangeListeners_ remains null.
        // vetoableChangeListeners_ remains null.

        // See if this object was serialized when its thread-safe behavior was determined by a system property (and not explicitly specified by setThreadSafe()).  This property may have since changed, so we want to reflect the current property.
        if (threadSafetyDetermined_ != BY_SET_METHOD)
        {
            String property = SystemProperties.getProperty(SystemProperties.PROGRAMCALL_THREADSAFE);
            if (property == null)  // Property is not set.
            {
                threadSafety_ = false;
                threadSafetyDetermined_ = BY_DEFAULT;
                if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Thread safe system property not set, thread safety property changed to unspecified.");
            }
            else
            {
                threadSafety_ = property.equalsIgnoreCase("true");
                threadSafetyDetermined_ = BY_PROPERTY;
                if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Thread safe system property: " + property);
            }
        }
    }

    /**
     Removes the ActionCompletedListener.  If the ActionCompletedListener is not on the list, nothing is done.
     @param  listener  The listener object.
     **/
    public void removeActionCompletedListener(ActionCompletedListener listener)
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Removing action completed listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        // If we have listeners.
        if (actionCompletedListeners_ != null)
        {
            actionCompletedListeners_.removeElement(listener);
        }
    }

    /**
     Removes the PropertyChangeListener.  If the PropertyChangeListener is not on the list, nothing is done.
     @param  listener  The listener object.
     **/
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Removing property change listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        // If we have listeners.
        if (propertyChangeListeners_ != null)
        {
            propertyChangeListeners_.removePropertyChangeListener(listener);
        }
    }

    /**
     Removes the VetoableChangeListener.  If the VetoableChangeListener is not on the list, nothing is done.
     @param  listener  The listener object.
     **/
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Removing vetoable change listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        // If we have listeners.
        if (vetoableChangeListeners_ != null)
        {
            vetoableChangeListeners_.removeVetoableChangeListener(listener);
        }
    }

    /**
     Runs the program on the system.  The program and parameter list need to be set prior to this call.
     @return  true if program ran successfully; false otherwise.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the system.
     @exception  InterruptedException  If this thread is interrupted.
     @exception  ObjectDoesNotExistException  If the object does not exist on the system.
     **/
    public boolean run() throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException
    {
        if (Trace.traceOn_) Trace.log(Trace.INFORMATION, "Running program: " + program_);
        if (program_.length() == 0)
        {
            Trace.log(Trace.ERROR, "Attempt to run before setting program.");
            throw new ExtendedIllegalStateException("program", ExtendedIllegalStateException.PROPERTY_NOT_SET);
        }

        // Validate that all the program parameters have been set.
        for (int i = 0; i < parameterList_.length; ++i)
        {
            if (parameterList_[i] == null)
            {
                Trace.log(Trace.ERROR, "Parameter list value " + i + " null.");
                throw new ExtendedIllegalArgumentException("parameterList[" + i + "] (" + parameterList_[i] + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID);
            }
        }

        chooseImpl();

        // Run the program.
        try
        {
            boolean result = impl_.runProgram(library_, name_, parameterList_, threadSafety_, messageOption_);
            // Retrieve the messages.
            messageList_ = impl_.getMessageList();
            // Set our system object into each of the messages.
            if (system_ != null)
            {
                for (int i = 0; i < messageList_.length; ++i)
                {
                    messageList_[i].setSystem(system_);
                }
            }

            // Fire action completed event.
            if (actionCompletedListeners_ != null) fireActionCompleted();
            return result;
        }
        catch (ObjectDoesNotExistException e)
        {
            // Retrieve the messages.
            messageList_ = impl_.getMessageList();
            // Set our system object into each of the messages.
            if (system_ != null)
            {
                for (int i = 0; i < messageList_.length; ++i)
                {
                    messageList_[i].setSystem(system_);
                }
            }
            throw e;
        }
    }

    /**
     Sets the program name and the parameter list and runs the program on the system.
     @param  program  The fully qualified integrated file system path name to the program.  The library and program name must each be 10 characters or less.
     @param  parameterList  The list of parameters with which to run the program.
     @return  true if program ran successfully, false otherwise.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the system.
     @exception  InterruptedException  If this thread is interrupted.
     @exception  ObjectDoesNotExistException  If the object does not exist on the system.
     @exception  PropertyVetoException  If a change is vetoed.
     **/
    public boolean run(String program, ProgramParameter[] parameterList) throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException, PropertyVetoException
    {
        setProgram(program, parameterList);
        return run();
    }

    /**
     Sets the list of parameters to pass to the program.
     @param  parameterList  A list of up to 35 parameters with which to run the program.
     @exception  PropertyVetoException  If a change is vetoed.
     **/
    public void setParameterList(ProgramParameter[] parameterList) throws PropertyVetoException
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Setting parameter list.");
        if (parameterList == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'parameterList' is null.");
            throw new NullPointerException("parameterList");
        }
        else if (parameterList.length > 35)
        {
            Trace.log(Trace.ERROR, "Length of parameter 'parameterList' is not valid:", parameterList.length);
            throw new ExtendedIllegalArgumentException("parameterList.length (" + parameterList.length + ")", ExtendedIllegalArgumentException.LENGTH_NOT_VALID);
        }

        if (propertyChangeListeners_ == null && vetoableChangeListeners_ == null)
        {
            parameterList_ = parameterList;
        }
        else
        {
            ProgramParameter[] oldValue = parameterList_;
            ProgramParameter[] newValue = parameterList;

            if (vetoableChangeListeners_ != null)
            {
                vetoableChangeListeners_.fireVetoableChange("parameterList", oldValue, newValue);
            }
            parameterList_ = newValue;
            if (propertyChangeListeners_ != null)
            {
                propertyChangeListeners_.firePropertyChange("parameterList", oldValue, newValue);
            }
        }
    }

    /**
     Sets the path name of the program and the parameter list.
     @param  program  The fully qualified integrated file system path name to the program.  The library and program name must each be 10 characters or less.
     @param  parameterList  A list of up to 35 parameters with which to run the program.
     @exception  PropertyVetoException  If a change is vetoed.
     **/
    public void setProgram(String program, ProgramParameter[] parameterList) throws PropertyVetoException
    {
        // Validate and set program.
        setProgram(program);
        // Validate and set parmlist.
        setParameterList(parameterList);
    }

    /**
     Sets the path name of the program.
     @param  program  The fully qualified integrated file system path name to the program.  The library and program name must each be 10 characters or less.
     @exception  PropertyVetoException  If the change is vetoed.
     **/
    public void setProgram(String program) throws PropertyVetoException
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Setting program: " + program);
        if (program == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'program' is null.");
            throw new NullPointerException("program");
        }
        // Verify program is valid IFS path name.
        QSYSObjectPathName ifs = new QSYSObjectPathName(program, "PGM");

        if (propertyChangeListeners_ == null && vetoableChangeListeners_ == null)
        {
            library_ = ifs.getLibraryName();
            name_ = ifs.getObjectName();
            program_ = program;
        }
        else
        {
            String oldValue = program_;
            String newValue = program;

            if (vetoableChangeListeners_ != null)
            {
                vetoableChangeListeners_.fireVetoableChange("program", oldValue, newValue);
            }
            library_ = ifs.getLibraryName();
            name_ = ifs.getObjectName();
            program_ = newValue;
            if (propertyChangeListeners_ != null)
            {
                propertyChangeListeners_.firePropertyChange("program", oldValue, newValue);
            }
        }
    }

    /**
     Specifies the option for how many messages should be retrieved.  By default, to preserve compatability, only the messages sent to the program caller and only up to ten messages are retrieved.  This property will only take affect on systems that support the new option.  
     @param  messageOption  A constant indicating how many messages to retrieve.  Valid values are:
     <ul>
     <li>AS400Message.MESSAGE_OPTION_UP_TO_10
     <li>AS400Message.MESSAGE_OPTION_NONE
     <li>AS400Message.MESSAGE_OPTION_ALL
     </ul>
     **/
    public void setMessageOption(int messageOption)
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Setting message option:", messageOption);
        // Validate the messageOption parameter.
        if (messageOption < 0 || messageOption > 2)
        {
            Trace.log(Trace.ERROR, "Parameter 'messageOption' is not valid.");
            throw new ExtendedIllegalArgumentException("messageOption (" + messageOption + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID);
        }
        messageOption_ = messageOption;
    }

    /**
     Sets the system to run the program.  The system cannot be changed once a connection is made to the system.
     @param  system  The system on which to run the program.
     @exception  PropertyVetoException  If the change is vetoed.
     **/
    public void setSystem(AS400 system) throws PropertyVetoException
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Setting system: " + system);
        if (system == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'system' is null.");
            throw new NullPointerException("system");
        }
        if (impl_ != null)
        {
            Trace.log(Trace.ERROR, "Cannot set property 'system' after connect.");
            throw new ExtendedIllegalStateException("system", ExtendedIllegalStateException.PROPERTY_NOT_CHANGED);
        }

        if (propertyChangeListeners_ == null && vetoableChangeListeners_ == null)
        {
            system_ = system;
        }
        else
        {
            AS400 oldValue = system_;
            AS400 newValue = system;

            if (vetoableChangeListeners_ != null)
            {
                vetoableChangeListeners_.fireVetoableChange("system", oldValue, newValue);
            }
            system_ = newValue;
            if (propertyChangeListeners_ != null)
            {
                propertyChangeListeners_.firePropertyChange("system", oldValue, newValue);
            }
        }
    }

    /**
     Specifies whether or not the program should be assumed thread-safe.  The default is false.
     <br>Note: This method does not modify the actual program object on the system.
     <br>Note: If the program is run on-thread, it will run in a different job than if it were run off-thread.
     @param  threadSafe  true if the program should be assumed to be thread-safe; false otherwise.
     **/
    public void setThreadSafe(boolean threadSafe)
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Setting thread safe: " + threadSafe);
        if (propertyChangeListeners_ == null)
        {
            threadSafety_ = threadSafe;
            threadSafetyDetermined_ = BY_SET_METHOD;
        }
        else
        {
            Boolean oldValue = new Boolean(threadSafety_);
            Boolean newValue = new Boolean(threadSafe);

            threadSafety_ = threadSafe;
            threadSafetyDetermined_ = BY_SET_METHOD;

            propertyChangeListeners_.firePropertyChange ("threadSafe", oldValue, newValue);
        }
    }

    /**
     Returns the string representation of this program call object.
     @return  The string representing this program call object.
     **/
    public String toString()
    {
        return "ProgramCall (system: " + system_ + " program: " + program_ + "):" + super.toString();
    }
}