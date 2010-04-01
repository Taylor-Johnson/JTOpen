///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: PTFGroupList.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2003 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

 
/**
* Allows you to retrieve a list of all PTF groups that are known to a system. You can then use PTFGroup to
* get detailed information for a specific PTF group.
**/
public class PTFGroupList
{
  private static final String copyright = "Copyright (C) 1997-2003 International Business Machines Corporation and others.";

  private AS400 system_;  

                                                         
  /**
   * Constructs a PTFGroupList object. 
   * @param system The system.
  **/
  public PTFGroupList(AS400 system)
  {
      if(system == null) throw new NullPointerException("system");
      setSystem(system);
  }

                       
  /**
   * Returns a list of all PTF groups that are known to the system.
   * @return The array of PTFGroups. 
  **/
  public PTFGroup[] getPTFGroup()
  throws AS400Exception,
         AS400SecurityException,
         ErrorCompletingRequestException,
         InterruptedException,
         IOException,
         ObjectDoesNotExistException
  {

    int ccsid = system_.getCcsid();
    ConvTable conv = ConvTable.getTable(ccsid, null);

    ProgramParameter[] parms = new ProgramParameter[4];
    parms[0] = new ProgramParameter(conv.stringToByteArray(PTFGroup.USER_SPACE_NAME));        //qualified user space name
    try { parms[0].setParameterType(ProgramParameter.PASS_BY_REFERENCE); } catch(PropertyVetoException pve) {}
    parms[1] = new ProgramParameter(conv.stringToByteArray("LSTG0100"));        //Format name
    try { parms[1].setParameterType(ProgramParameter.PASS_BY_REFERENCE); } catch(PropertyVetoException pve) {}
    parms[2] = new ProgramParameter(BinaryConverter.intToByteArray(ccsid));     //CCSID
    try { parms[2].setParameterType(ProgramParameter.PASS_BY_REFERENCE); } catch(PropertyVetoException pve) {}
    parms[3] = new ProgramParameter(new byte[4]);                               // error code
    try { parms[3].setParameterType(ProgramParameter.PASS_BY_REFERENCE); } catch(PropertyVetoException pve) {}

    ServiceProgramCall pc = new ServiceProgramCall(system_, "/QSYS.LIB/QPZGROUP.SRVPGM", "QpzListPtfGroups", ServiceProgramCall.NO_RETURN_VALUE, parms);
    byte[] buf = null;
    synchronized(PTFGroup.USER_SPACE_NAME)        
    {
      UserSpace us = new UserSpace(system_, PTFGroup.USER_SPACE_PATH);
      us.setMustUseProgramCall(true);
      us.setMustUseSockets(true); // We have to do it this way since UserSpace will otherwise make a native ProgramCall.
      us.create(256*1024, true, "", (byte)0, "User space for PTF Group list", "*EXCLUDE");
      try
      {
        if (!pc.run())
        {
          throw new AS400Exception(pc.getMessageList());
        }
        int size = us.getLength();
        buf = new byte[size];
        us.read(buf, 0);
      }
      finally
      {
        us.close();
      }
    }
    int startingOffset = BinaryConverter.byteArrayToInt(buf, 124);      
    int numEntries = BinaryConverter.byteArrayToInt(buf, 132);
    int entrySize = BinaryConverter.byteArrayToInt(buf, 136);
    int entryCCSID = BinaryConverter.byteArrayToInt(buf, 140);
    conv = ConvTable.getTable(entryCCSID, null);
    int offset = 0;
    PTFGroup[] ptfs = new PTFGroup[numEntries];
    for (int i=0; i<numEntries; ++i)
    {
      offset = startingOffset + (i*entrySize);
      String ptfGroupName = conv.byteArrayToString(buf, offset, 60);
      offset += 60;
      String ptfGroupDescription = conv.byteArrayToString(buf, offset, 100);
      offset += 100;
      int ptfGroupLevel = BinaryConverter.byteArrayToInt(buf, offset);
      offset += 4;
      int ptfGroupStatus = BinaryConverter.byteArrayToInt(buf, offset);
      offset += 4;
      ptfs[i] = new PTFGroup(system_, ptfGroupName, ptfGroupDescription, ptfGroupLevel, ptfGroupStatus);
    }
    return ptfs;
  }
  
  /**
   * Sets the system.
   * @param system The system used to get a list of PTF groups.
  **/
  public void setSystem(AS400 system)
  {
    if (system == null) throw new NullPointerException("system");

    system_ = system;
  }

  /**
  * Returns the system used to get a list of PTF groups.
  * @return the system name.
  **/
  public AS400 getSystem()
  {
      return system_;
  }

}