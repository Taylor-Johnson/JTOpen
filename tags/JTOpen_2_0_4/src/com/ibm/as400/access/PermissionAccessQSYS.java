///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: PermissionAccessQSYS.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.beans.PropertyVetoException;

/**
 * The PermissionAccessQSYS class is provided to retrieve the user's
 * permission information.
 *
**/
class PermissionAccessQSYS extends PermissionAccess
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";


    /**
     * Constructs a PermissionAccessQSYS object.
     *
    **/
    public PermissionAccessQSYS(AS400 system)
    {
        super(system);
    }

    /**
     * Adds the authorized user or user permission.
     * @param objName The object the authorized user will be added to.
     * @param permission The permission of the new authorized user.
     * @exception AS400Exception If the AS/400 system returns an error message.
     * @exception AS400SecurityException If a security or authority error occurs.
     * @exception ConnectionDroppedException If the connection is dropped unexpectedly.
     * @exception ErrorCompletingRequestException If an error occurs before the request is completed.
     * @exception InterruptedException If this thread is interrupted.
     * @exception IOException If an error occurs while communicationg with the AS/400.
     * @exception PropertyVetoException If the change is vetoed.
     * @exception ServerStartupException If the AS/400 server cannot be started.
     * @exception UnknownHostException If the AS/400 system cannot be located.
     *
    **/
    public void addUser(String objName,UserPermission permission)
         throws AS400Exception,
                   AS400SecurityException,
                   ConnectionDroppedException,
                   ErrorCompletingRequestException,
                   InterruptedException,
                   IOException,
                   ServerStartupException,
                   UnknownHostException,
                   PropertyVetoException
    {
        CommandCall addUser= getAddCommand(as400_,objName,permission);

        if (addUser.run()!=true)
        {
           AS400Message[] msgList = addUser.getMessageList();
           throw new AS400Exception(msgList);
        }
        return;

    }

    /**
     * Returns the command to add a authorized user.
    **/
    private static CommandCall getAddCommand(AS400 sys, String objName,UserPermission permission)
    {
        QSYSObjectPathName objectPathName = new QSYSObjectPathName(objName);
        String objectType = objectPathName.getObjectType();
        if (!objectType.equals("AUTL"))
            return getChgCommand(sys,objName,permission);
        QSYSPermission qsysPermission = (QSYSPermission)permission;
        String userProfile=qsysPermission.getUserID();
        String object = objectPathName.getObjectName();

        String command = "ADDAUTLE"
                         +" AUTL("+object+")"
                         +" USER("+userProfile+")"
                         +" AUT(*EXCLUDE)";
        CommandCall cmd = new CommandCall(sys, command); //@A2C
        cmd.setThreadSafe(false); // ADDAUTLE isn't threadsafe.  @A2A @A3C
        return cmd;               //@A2C
    }

    /**
     * Returns the command to clear a authorized user's permission.
    **/
    private static CommandCall getClrCommand(AS400 sys, String objName,UserPermission permission)
    {
        QSYSObjectPathName objectPathName = new QSYSObjectPathName(objName);
        QSYSPermission qsysPermission = (QSYSPermission)permission;
        String userProfile=qsysPermission.getUserID();
        String object = objectPathName.getObjectName();
        if (!objectPathName.getLibraryName().equals("QSYS"))
        {
            object = objectPathName.getLibraryName()+"/"+object;
        }
        String objectType = objectPathName.getObjectType();
        if (objectType.toUpperCase().equals("MBR"))
            objectType = "FILE";

        String command;

        if (objectType.equals("AUTL"))
        {
            command = "CHGAUTLE"
                      +" AUTL("+object+")"
                      +" USER("+userProfile+")"
                      +" AUT(*EXCLUDE)";
        } else
        {
            command="GRTOBJAUT"
                    +" OBJ("+object+")"
                    +" OBJTYPE(*"+objectType+")"
                    +" USER("+userProfile+")"
                    +" AUT(*EXCLUDE)";
        }
        CommandCall cmd = new CommandCall(sys, command); //@A2C
        cmd.setThreadSafe(false); // CHGAUTLE,GRTOBJAUT not threadsafe.  @A2A @A3C
        return cmd;               //@A2C
    }

    /**
     * Returns the command to change a authorized user's permission.
     * @param objName The object that the authorized information will be
     *  set to.
     * @param permission The permission that will be changed.
     * @return The command to remove a authorized user.
     *
    **/
    private static CommandCall getChgCommand(AS400 sys, String objName,UserPermission permission)
    {
        QSYSObjectPathName objectPathName = new QSYSObjectPathName(objName);
        QSYSPermission qsysPermission = (QSYSPermission)permission;
        String userProfile=qsysPermission.getUserID();
        String object = objectPathName.getObjectName();
        if (!objectPathName.getLibraryName().equals("QSYS"))
        {
            object = objectPathName.getLibraryName()+"/"+object;
        }
        String objectType = objectPathName.getObjectType();
        if (objectType.toUpperCase().equals("MBR"))
            objectType = "FILE";

        boolean objMgt = qsysPermission.isManagement();
        boolean objExist = qsysPermission.isExistence();
        boolean objAlter = qsysPermission.isAlter();
        boolean objRef = qsysPermission.isReference();
        boolean objOpr = qsysPermission.isOperational();
        boolean datAdd = qsysPermission.isAdd();
        boolean datDlt = qsysPermission.isDelete();
        boolean datRead = qsysPermission.isRead();
        boolean datUpdate = qsysPermission.isUpdate();
        boolean datExecute = qsysPermission.isExecute();
        boolean autListManagement = qsysPermission.isAuthorizationListManagement();
        String authority="";

        if(objMgt==true)
             authority=authority+"*OBJMGT ";
        if(objExist==true)
             authority=authority+"*OBJEXIST ";
        if(objAlter==true)
             authority=authority+"*OBJALTER ";
        if(objRef==true)
             authority=authority+"*OBJREF ";
        if(objOpr==true)
             authority=authority+"*OBJOPR ";
        if(datAdd==true)
             authority=authority+"*ADD ";
        if(datDlt==true)
             authority=authority+"*DLT ";
        if(datRead==true)
             authority=authority+"*READ ";
        if(datUpdate==true)
             authority=authority+"*UPD ";
        if(datExecute==true)
             authority=authority+"*EXECUTE ";
        if(objectType.equals("AUTL")&&autListManagement)
             authority=authority+"*AUTLMGT";
        if (authority.equals(""))
            authority = "*EXCLUDE";

        String command;
        if (objectType.equals("AUTL"))
        {
            command = "CHGAUTLE"
                      +" AUTL("+object+")"
                      +" USER("+userProfile+")"
                      +" AUT("+authority+")";
        } else
        {
            command="GRTOBJAUT"
                    +" OBJ("+object+")"
                    +" OBJTYPE(*"+objectType+")"
                    +" USER("+userProfile+")"
                    +" AUT("+authority+")";
        }
        CommandCall cmd = new CommandCall(sys, command); //@A2C
        cmd.setThreadSafe(false); // CHGAUTLE,GRTOBJAUT not threadsafe.  @A2A @A3C
        return cmd;                //@A2C
    }

    /**
     * Returns the command to remove a authorized user.
     * @param objName The object the authorized user will be removed from.
     * @param userName The profile name of the authorized user.
     * @return The command to remove a authorized user.
     *
    **/
    private static CommandCall getRmvCommand(AS400 sys, String objName,String userName)
    {
        QSYSObjectPathName objectPathName = new QSYSObjectPathName(objName);
        String objectType = objectPathName.getObjectType();
        boolean threadSafe;      //@A2A
        String command,object;
        if (objectType.equals("AUTL"))
        {
            object = objectPathName.getObjectName();
            command = "RMVAUTLE"
                      +" AUTL("+object+")"
                      +" USER("+userName+")";
            threadSafe = false; // RMVAUTLE isn't threadsafe.  @A2A @A3C
        } else if (objectType.equals("MBR"))
        {
            object = "QSYS.LIB/";
            if (!objectPathName.getLibraryName().equals(""))
                object += objectPathName.getLibraryName()+".LIB/";
            object += objectPathName.getObjectName()+".FILE";

            command="CHGAUT"
                    +" OBJ('"+object+"')"
                    +" USER("+userName+")"
                    +" DTAAUT(*NONE)"
                    +" OBJAUT(*NONE)";
            threadSafe = true; //@A2A
        } else
        {
            command="CHGAUT"
                    +" OBJ('"+objName+"')"
                    +" USER("+userName+")"
                    +" DTAAUT(*NONE)"
                    +" OBJAUT(*NONE)";
            threadSafe = true; //@A2A
        }
        CommandCall cmd = new CommandCall(sys, command); //@A2C
        cmd.setThreadSafe(threadSafe);  //@A2A
        return cmd;                     //@A2C
    }

    /**
     * Returns the user's permission retrieved from the system.
     * @return The user's permission retrieved from the system.
     * @exception UnsupportedEncodingException The Character Encoding is not supported.
     *
    **/
    public UserPermission getUserPermission(Record userRecord)
         throws UnsupportedEncodingException
    {
        String profileName=((String)userRecord.getField("profileName")).trim();
        String userOrGroup=((String)userRecord.getField("userOrGroup")).trim();
        String dataAuthority=((String)userRecord.getField("dataAuthority")).trim();
        String autListMgt=((String)userRecord.getField("autListMgt")).trim();
        String objMgt=((String)userRecord.getField("objMgt")).trim();
        String objExistence=((String)userRecord.getField("objExistence")).trim();
        String objAlter=((String)userRecord.getField("objAlter")).trim();
        String objRef=((String)userRecord.getField("objRef")).trim();
        String reserved1=((String)userRecord.getField("reserved1")).trim();
        String objOperational=((String)userRecord.getField("objOperational")).trim();
        String dataRead=((String)userRecord.getField("dataRead")).trim();
        String dataAdd=((String)userRecord.getField("dataAdd")).trim();
        String dataUpdate=((String)userRecord.getField("dataUpdate")).trim();
        String dataDelete=((String)userRecord.getField("dataDelete")).trim();
        String dataExecute=((String)userRecord.getField("dataExecute")).trim();
        String reserved2=((String)userRecord.getField("reserved2")).trim();
        QSYSPermission permission;
        permission =new QSYSPermission(profileName);
        permission.setGroupIndicator(getIntValue(userOrGroup));

        permission.setAuthorizationListManagement(getBooleanValue(autListMgt));
        permission.setManagement(getBooleanValue(objMgt));
        permission.setExistence(getBooleanValue(objExistence));
        permission.setAlter(getBooleanValue(objAlter));
        permission.setReference(getBooleanValue(objRef));
        permission.setOperational(getBooleanValue(objOperational));
        permission.setRead(getBooleanValue(dataRead));
        permission.setAdd(getBooleanValue(dataAdd));
        permission.setUpdate(getBooleanValue(dataUpdate));
        permission.setDelete(getBooleanValue(dataDelete));
        permission.setExecute(getBooleanValue(dataExecute));
        if (dataAuthority.toUpperCase().equals("*AUTL"))
        {
            permission.setFromAuthorizationList(true);
        }
        return permission;
    }

    /**
     * Removes the authorized user.
     * @param objName The object the authorized user will be removed from.
     * @param userName The profile name of the authorized user.
     * @exception AS400Exception If the AS/400 system returns an error message.
     * @exception AS400SecurityException If a security or authority error occurs.
     * @exception ConnectionDroppedException If the connection is dropped unexpectedly.
     * @exception ErrorCompletingRequestException If an error occurs before the request is completed.
     * @exception InterruptedException If this thread is interrupted.
     * @exception IOException If an error occurs while communicationg with the AS/400.
     * @exception PropertyVetoException If the change is vetoed.
     * @exception ServerStartupException If the AS/400 server cannot be started.
     * @exception UnknownHostException If the AS/400 system cannot be located.
     *
    **/
    public void removeUser(String objName,String userName)
         throws AS400Exception,
                   AS400SecurityException,
                   ConnectionDroppedException,
                   ErrorCompletingRequestException,
                   InterruptedException,
                   IOException,
                   ServerStartupException,
                   UnknownHostException,
                   PropertyVetoException
    {
        CommandCall removeUser = getRmvCommand(as400_,objName,userName);

        if (removeUser.run()!=true)
        {
           AS400Message[] msgList = removeUser.getMessageList();
           throw new AS400Exception(msgList);
        }
        return;
    }

    /**
     * Returns the authorized users' permissions.
     * @return A vector of authorized users' permission.
     * @exception AS400Exception If the AS/400 system returns an error message.
     * @exception AS400SecurityException If a security or authority error occurs.
     * @exception ConnectionDroppedException If the connection is dropped unexpectedly.
     * @exception ErrorCompletingRequestException If an error occurs before the request is completed.
     * @exception InterruptedException If this thread is interrupted.
     * @exception IOException If an error occurs while communicationg with the AS/400.
     * @exception PropertyVetoException If the change is vetoed.
     * @exception ServerStartupException If the AS/400 server cannot be started.
     * @exception UnknownHostException If the AS/400 system cannot be located.
    **/
    public synchronized void setAuthority(String objName,UserPermission permission)
         throws AS400Exception,
                   AS400SecurityException,
                   ConnectionDroppedException,
                   ErrorCompletingRequestException,
                   InterruptedException,
                   IOException,
                   ServerStartupException,
                   UnknownHostException,
                   PropertyVetoException
    {
        CommandCall setAuthority = getClrCommand(as400_,objName,permission);
        if (setAuthority.run()!=true)
        {
           AS400Message[] msgList = setAuthority.getMessageList();
           throw new AS400Exception(msgList);
        }

        setAuthority = getChgCommand(as400_,objName,permission);
        if (setAuthority.run()!=true)
        {
           AS400Message[] msgList = setAuthority.getMessageList();
           throw new AS400Exception(msgList);
        }
        return;
    }

    /**
     * Sets authorization list of the object.
     * @param objName The object that the authorization list will be set to.
     * @param autList The authorization list that will be set.
     * @param oldValue The old authorization list will be replaced.
     * @exception AS400Exception If the AS/400 system returns an error message.
     * @exception AS400SecurityException If a security or authority error occurs.
     * @exception ConnectionDroppedException If the connection is dropped unexpectedly.
     * @exception ErrorCompletingRequestException If an error occurs before the request is completed.
     * @exception InterruptedException If this thread is interrupted.
     * @exception IOException If an error occurs while communicationg with the AS/400.
     * @exception PropertyVetoException If the change is vetoed.
     * @exception ServerStartupException If the AS/400 server cannot be started.
     * @exception UnknownHostException If the AS/400 system cannot be located.
     *
    **/
    public synchronized void setAuthorizationList(String objName,String autList,String oldValue)
            throws AS400Exception,
                   AS400SecurityException,
                   ConnectionDroppedException,
                   ErrorCompletingRequestException,
                   InterruptedException,
                   IOException,
                   ServerStartupException,
                   UnknownHostException,
                   PropertyVetoException
    {
        QSYSObjectPathName objectPathName = new QSYSObjectPathName(objName);
        String object = objectPathName.getObjectName();
        if (!objectPathName.getLibraryName().equals("QSYS"))
        {
            object = objectPathName.getLibraryName()+"/"+object;
        }
        String objectType = objectPathName.getObjectType();
        if (objectType.toUpperCase().trim().equals("MBR"))
            objectType = "FILE";

        CommandCall setAUTL=new CommandCall(as400_);
        String cmd;
        if (!oldValue.toUpperCase().equals("*NONE")&&
            autList.toUpperCase().equals("*NONE"))
        {
            cmd = "RVKOBJAUT"
                  +" OBJ("+object+")"
                  +" OBJTYPE(*"+objectType+")"
                  +" AUTL("+oldValue+")";
        } else
        {
            cmd = "GRTOBJAUT"
                  +" OBJ("+object+")"
                  +" OBJTYPE(*"+objectType+")"
                  +" AUTL("+autList+")";
        }
        setAUTL.setCommand(cmd);
        setAUTL.setThreadSafe(false); // RVKOBJAUT,GRTOBJAUT not threadsafe.  @A2A @A3C
        if (setAUTL.run()!=true)
        {
           AS400Message[] msgList = setAUTL.getMessageList();
           throw new AS400Exception(msgList);
        }
        return;
    }

    /**
     * Sets from authorization list of the object.
     * @param objName The object the authorized list will be set to.
     * @param fromAutl true if the permission is from the authorization list;
     * false otherwise.
     * @exception AS400Exception If the AS/400 system returns an error message.
     * @exception AS400SecurityException If a security or authority error occurs.
     * @exception ConnectionDroppedException If the connection is dropped unexpectedly.
     * @exception ErrorCompletingRequestException If an error occurs before the request is completed.
     * @exception InterruptedException If this thread is interrupted.
     * @exception IOException If an error occurs while communicationg with the AS/400.
     * @exception PropertyVetoException If the change is vetoed.
     * @exception ServerStartupException If the AS/400 server cannot be started.
     * @exception UnknownHostException If the AS/400 system cannot be located.
     *
    **/
    public synchronized void setFromAuthorizationList(String objName,boolean fromAutl)
            throws AS400Exception,
                   AS400SecurityException,
                   ConnectionDroppedException,
                   ErrorCompletingRequestException,
                   InterruptedException,
                   IOException,
                   ServerStartupException,
                   UnknownHostException,
                   PropertyVetoException
    {
        QSYSObjectPathName objectPathName = new QSYSObjectPathName(objName);
        String object = objectPathName.getObjectName();
        if (!objectPathName.getLibraryName().equals("QSYS"))
        {
            object = objectPathName.getLibraryName()+"/"+object;
        }
        String objectType = objectPathName.getObjectType();
        if (objectType.toUpperCase().equals("MBR"))
            objectType = "FILE";

        CommandCall fromAUTL=new CommandCall(as400_);
        String cmd;
        if (fromAutl)
            cmd = "GRTOBJAUT"
                  +" OBJ("+object+")"
                  +" OBJTYPE(*"+objectType+")"
                  +" USER(*PUBLIC)"
                  +" AUT(*AUTL)";
        else
            cmd = "GRTOBJAUT"
                  +" OBJ("+object+")"
                  +" OBJTYPE(*"+objectType+")"
                  +" USER(*PUBLIC)"
                  +" AUT(*EXCLUDE)";
        fromAUTL.setCommand(cmd);
        fromAUTL.setThreadSafe(false); // GRTOBJAUT isn't threadsafe.  @A2A @A3C
        if (fromAUTL.run()!=true)
        {
           AS400Message[] msgList = fromAUTL.getMessageList();
           throw new AS400Exception(msgList);
        }
        return;
    }

    /**
     * Sets the sensitivity level of the object.
     * @param objName The object that the sensitivity level will be set to.
     * @param sensitivityLevel The sensitivity level that will be set.
     * @exception AS400Exception If the AS/400 system returns an error message.
     * @exception AS400SecurityException If a security or authority error occurs.
     * @exception ConnectionDroppedException If the connection is dropped unexpectedly.
     * @exception ErrorCompletingRequestException If an error occurs before the request is completed.
     * @exception InterruptedException If this thread is interrupted.
     * @exception IOException If an error occurs while communicationg with the AS/400.
     * @exception PropertyVetoException If the change is vetoed.
     * @exception ServerStartupException If the AS/400 server cannot be started.
     * @exception UnknownHostException If the AS/400 system cannot be located.
     *
    **/
    public synchronized void setSensitivity(String objName,int sensitivityLevel)
            throws AS400Exception,
                   AS400SecurityException,
                   ConnectionDroppedException,
                   ErrorCompletingRequestException,
                   InterruptedException,
                   IOException,
                   ServerStartupException,
                   UnknownHostException,
                   PropertyVetoException
    {
        return;
    }

}

