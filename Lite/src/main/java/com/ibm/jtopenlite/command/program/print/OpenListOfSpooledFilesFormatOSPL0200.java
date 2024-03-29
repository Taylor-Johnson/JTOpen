///////////////////////////////////////////////////////////////////////////////
//
// JTOpenLite
//
// Filename: OpenListOfSpooledFilesFormatOSPL0200.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 2011-2012 International Business Machines Corporation and
// others.  All rights reserved.
//
///////////////////////////////////////////////////////////////////////////////
package com.ibm.jtopenlite.command.program.print;

import com.ibm.jtopenlite.Conv;

public class OpenListOfSpooledFilesFormatOSPL0200 implements OpenListOfSpooledFilesFormat<OpenListOfSpooledFilesFormatOSPL0200Listener>
{
  private final char[] charBuffer_ = new char[10];

  public OpenListOfSpooledFilesFormatOSPL0200()
  {
  }

  public String getName()
  {
    return "OSPL0200";
  }

  public int getType()
  {
    return FORMAT_OSPL0200;
  }

  public void format(final byte[] data, final int maxLength, final int recordLength, OpenListOfSpooledFilesFormatOSPL0200Listener listener)
  {
    if (listener == null)
    {
      return;
    }

    int numRead = 0;
    while (numRead+184 <= maxLength)
    {
      int current = numRead;
      String spooledFileName = Conv.ebcdicByteArrayToString(data, numRead, 10, charBuffer_);
      numRead += 10;
      String jobName = Conv.ebcdicByteArrayToString(data, numRead, 10, charBuffer_);
      numRead += 10;
      String jobUser = Conv.ebcdicByteArrayToString(data, numRead, 10, charBuffer_);
      numRead += 10;
      String jobNumber = Conv.ebcdicByteArrayToString(data, numRead, 6, charBuffer_);
      numRead += 6;
      int spooledFileNumber = Conv.byteArrayToInt(data, numRead);
      numRead += 4;
      int totalPages = Conv.byteArrayToInt(data, numRead);
      numRead += 4;
      int currentPage = Conv.byteArrayToInt(data, numRead);
      numRead += 4;
      int copiesLeftToPrint = Conv.byteArrayToInt(data, numRead);
      numRead += 4;
      String outputQueueName = Conv.ebcdicByteArrayToString(data, numRead, 10, charBuffer_);
      numRead += 10;
      String outputQueueLibrary = Conv.ebcdicByteArrayToString(data, numRead, 10, charBuffer_);
      numRead += 10;
      String userData = Conv.ebcdicByteArrayToString(data, numRead, 10, charBuffer_);
      numRead += 10;
      String status = Conv.ebcdicByteArrayToString(data, numRead, 10, charBuffer_);
      numRead += 10;
      String formType = Conv.ebcdicByteArrayToString(data, numRead, 10, charBuffer_);
      numRead += 10;
      String priority = Conv.ebcdicByteArrayToString(data, numRead, 2, charBuffer_);
      numRead += 2;
      byte[] internalJobIdentifier = new byte[16];
      System.arraycopy(data, numRead, internalJobIdentifier, 0, 16);
      numRead += 16;
      byte[] internalSpooledFileIdentifier = new byte[16];
      System.arraycopy(data, numRead, internalSpooledFileIdentifier, 0, 16);
      numRead += 16;
      String deviceType = Conv.ebcdicByteArrayToString(data, numRead, 10, charBuffer_);
      numRead += 10;
      numRead += 2;
      int offsetToExtension = Conv.byteArrayToInt(data, numRead);
      numRead += 4;
      int lengthOfExtension = Conv.byteArrayToInt(data, numRead);
      numRead += 4;
      numRead += 4;
      String dateOpened = Conv.ebcdicByteArrayToString(data, numRead, 7, charBuffer_);
      numRead += 7;
      String timeOpened = Conv.ebcdicByteArrayToString(data, numRead, 6, charBuffer_);
      numRead += 6;
      String printerAssigned = Conv.ebcdicByteArrayToString(data, numRead, 1, charBuffer_);
      numRead += 1;
      String printerName = Conv.ebcdicByteArrayToString(data, numRead, 10, charBuffer_);
      numRead += 10;
      numRead += 8;
      String jobSystemName = null;
      if (offsetToExtension > 0 && lengthOfExtension > 0)
      {
        numRead += (numRead-current-offsetToExtension);
        jobSystemName = Conv.ebcdicByteArrayToString(data, numRead, 8, charBuffer_);
      }
      listener.newSpooledFileEntry(spooledFileName, jobName, jobUser, jobNumber, spooledFileNumber,
                                   totalPages, currentPage, copiesLeftToPrint,
                                   outputQueueName, outputQueueLibrary, userData,
                                   status, formType, priority,
                                   internalJobIdentifier, internalSpooledFileIdentifier,
                                   deviceType, dateOpened, timeOpened, printerAssigned, printerName,
                                   jobSystemName);
    }
  }
}




