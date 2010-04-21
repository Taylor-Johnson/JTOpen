///////////////////////////////////////////////////////////////////////////////
//                                                                             
// AS/400 Toolbox for Java - OSS version                                       
//                                                                             
// Filename: IFSLockBytesRep.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.IOException;
import java.io.InputStream;



/**
Lock bytes reply.
**/
class IFSLockBytesRep extends IFSDataStream
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

  private static final int RETURN_CODE_OFFSET = 22;
  private static final int FILE_SIZE_OFFSET = 24;
  private static final int TEMPLATE_LENGTH = 8;

/**
Construct a lock bytes reply.
**/
  IFSLockBytesRep()
  {
  }

  // Get the copyright.
  private static String getCopyright()
  {
    return Copyright.copyright;
  }

/**
Generate a new instance of this type.
@return a reference to the new instance
**/
  public Object getNewDataStream()
  {
    return new IFSLockBytesRep();
  }

/**
Determine the return code.
@return the return code
**/
  int getReturnCode()
  {
    return get16bit( RETURN_CODE_OFFSET);
  }

/**
Generates a hash code for this data stream.
@return the hash code
**/
  public int hashCode()
  {
    return 0x800A;
  }
}

