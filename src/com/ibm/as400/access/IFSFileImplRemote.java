///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: IFSFileImplRemote.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2004 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.InterruptedIOException;
import java.io.IOException;
import java.util.Vector;
import java.beans.PropertyVetoException;

/**
 Provides a full remote implementation for the IFSFile class.
 **/
class IFSFileImplRemote
implements IFSFileImpl
{
  // Used for debugging only.  This should always be false for production.
  // When this is false, all debug code will theoretically compile out.
  private static final boolean DEBUG = false;  // @B2A

  private static final boolean IS_RESTART_NAME = true;  // mnemonic for use in argument lists
  private static final boolean NO_RESTART_NAME = IS_RESTART_NAME; // mnemonic
  private static final boolean SORT_LIST = true;  // mnemonic
  private static final int     NO_MAX_GET_COUNT = -1;  // mnemonic
  private static final int     UNINITIALIZED = -1;

  // Static initialization code.
  static
  {
    // Add all byte stream reply data streams of interest to the
    // server's reply data stream hash table.
    AS400Server.addReplyStream(new IFSListAttrsRep(), AS400.FILE);
    AS400Server.addReplyStream(new IFSOpenRep(), AS400.FILE);
    AS400Server.addReplyStream(new IFSCreateDirHandleRep(), AS400.FILE);
    AS400Server.addReplyStream(new IFSQuerySpaceRep(), AS400.FILE);
    AS400Server.addReplyStream(new IFSReturnCodeRep(), AS400.FILE);
  }

  transient private IFSListAttrsRep attributesReply_; // "list attributes" reply

  private IFSFileDescriptorImplRemote fd_ = new IFSFileDescriptorImplRemote(); // @B2A

  private boolean isSymbolicLink_;
  private boolean determinedIsSymbolicLink_;
  private boolean sortLists_;  // whether file-lists are returned from the File Server in sorted order



  /**
   Determines if the applet or application can read from the integrated file system object represented by this object.
   **/
  public int canRead()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    return (fd_.checkAccess(IFSOpenReq.READ_ACCESS, IFSOpenReq.OPEN_OPTION_FAIL_OPEN)); //@D1C
  }


  /**
   Determines if the applet or application can write to the integrated file system object represented by this object.
   **/
  public int canWrite()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    return (fd_.checkAccess(IFSOpenReq.WRITE_ACCESS, IFSOpenReq.OPEN_OPTION_FAIL_OPEN)); //@D1C
  }


  /**
   Copies the current file to the specified path.
   **/
  public boolean copyTo(String destinationPath, boolean replace)
    throws IOException, AS400SecurityException, ObjectAlreadyExistsException
  {
    fd_.connect();
    if (Trace.traceOn_ && replace==false && fd_.getSystemVRM() < 0x00050300) {
      Trace.log(Trace.WARNING, "Server is V5R2 or lower, so the 'Do not replace' argument will be ignored.");
    }

    // If the source is a directory, verify that the destination doesn't already exist.
    if (isDirectory() == IFSReturnCodeRep.SUCCESS &&
        exists(destinationPath) == IFSReturnCodeRep.SUCCESS) {
      throw new ObjectAlreadyExistsException(destinationPath, ObjectAlreadyExistsException.OBJECT_ALREADY_EXISTS);
    }

    return fd_.copyTo(destinationPath, replace);
  }

  /**
   @D3a created0 is a new method
   Determines the time that the integrated file system object represented by this object was created.
   **/
  public long created()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    long creationDate = 0L;


    // Attempt to list the attributes of the specified file.
    // Note: Do not use cached attributes, since they may be out of date.
    IFSListAttrsRep attrs = getAttributeSetFromServer(fd_.path_);
    if (attrs != null)
    {
      attributesReply_ = attrs;
      creationDate = attrs.getCreationDate();
    }

    return creationDate;
  }




  /**
   If files does not exist, create it.  If the files
   does exist, return an error.  The goal is to atomically
   create a new file if and only if the file does not
   yet exist.
  **/
  // @D1 - new method because of changes to java.io.file in Java 2.

  public int createNewFile()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    return (fd_.checkAccess(IFSOpenReq.WRITE_ACCESS, IFSOpenReq.OPEN_OPTION_CREATE_FAIL));
  }




  /**
   Deletes the integrated file system object represented by this object.
   **/
  public int delete()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    // Convert the path name to the server CCSID.
    byte[] pathname = fd_.converter_.stringToByteArray(fd_.path_);

    // Determine if this is a file or directory and instantiate the
    // appropriate type of delete request.
    IFSDataStreamReq req =
      new IFSDeleteFileReq(pathname, fd_.preferredServerCCSID_);
    try
    {
      if (isDirectory() == IFSReturnCodeRep.SUCCESS)
      {
        req = new IFSDeleteDirReq(pathname, fd_.preferredServerCCSID_);
      }
    }
    catch (Exception e)
    {
      if (Trace.traceOn_) Trace.log(Trace.WARNING,
                 "Unable to determine if file or directory.\n" + e.toString());
    }

    // Delete this entry.
    ClientAccessDataStream ds = null;
    try
    {
      // Send a delete request.
      ds = (ClientAccessDataStream) fd_.server_.sendAndReceive(req);
    }
    catch(ConnectionDroppedException e)
    {
      Trace.log(Trace.ERROR, "Byte stream server connection lost.");
      fd_.connectionDropped(e);
    }
    catch(InterruptedException e)
    {
      Trace.log(Trace.ERROR, "Interrupted");
      throw new InterruptedIOException(e.getMessage());
    }

    // Verify that the request was successful.
    int rc = 0;
    if (ds instanceof IFSReturnCodeRep)
    {
      rc = ((IFSReturnCodeRep) ds).getReturnCode();
      if (rc != IFSReturnCodeRep.SUCCESS)
      {
        if (Trace.traceOn_) Trace.log(Trace.ERROR, "IFSReturnCodeRep return code = ", rc);
      }
    }
    else
    {
      // Unknown data stream.
      Trace.log(Trace.ERROR, "Unknown reply data stream ", ds.data_);
      throw new
        InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                               InternalErrorException.DATA_STREAM_UNKNOWN);
    }

    // Clear any cached file attributes.
    attributesReply_ = null;

    return (rc);
  }

  //@B1A Moved code from isDirectory() to support determining if a file is a directory
  //without a call to the server.
  /**
   Determines if a file is a directory without a call to the server.
   **/
  private boolean determineIsDirectory(IFSListAttrsRep attributeList)
  {
    boolean answer = false;
    // Determine if the file attributes indicate a directory.
    // Don't need to check if attributeList == null because it has already been
    // checked by the two methods that call it.  Also don't check converter
    // because it is set by a connect() method before calling this.
    String name = fd_.converter_.byteArrayToString(attributeList.getName(/*fd_.serverDatastreamLevel_*/));
    switch (attributeList.getObjectType())
    {
      case IFSListAttrsRep.DIRECTORY:
      case IFSListAttrsRep.FILE:
         answer = ((attributeList.getFixedAttributes() & IFSListAttrsRep.FA_DIRECTORY) != 0);
         break;
      case IFSListAttrsRep.AS400_OBJECT:
         // Server libraries and database files look like directories
         String nameUpper = name.toUpperCase();         // @C2a
         answer = (nameUpper.endsWith(".LIB") ||
                   nameUpper.endsWith(".FILE")); //B1C Changed path_ to name
                   //@C2c
                   //@B1D Removed code that checked for file separators
                   //|| path_.endsWith(".LIB" + IFSFile.separator) ||
                   //path_.endsWith(".FILE" + IFSFile.separator));
         break;
      default:
         answer = false;
    }
    return answer;
  }

  //@B1A Moved code from isFile() to support determining if a file is a file
  //without a call to the server.
  /**
   Determines if a file is a file without a call to the server.
   **/
  private boolean determineIsFile(IFSListAttrsRep attributeList)
  {
    boolean answer = false;
    // Determine if the file attributes indicate a file.
    // Don't need to check if attributeList == null because it has already been
    // checked by the two methods that call it.   Also don't check converter
    // because it is set by a connect() method before calling this.
    String name = fd_.converter_.byteArrayToString(attributeList.getName(/*fd_.serverDatastreamLevel_*/));
    switch(attributeList.getObjectType())
    {
      case IFSListAttrsRep.DIRECTORY:
      case IFSListAttrsRep.FILE:
         answer = ((attributeList.getFixedAttributes() & IFSListAttrsRep.FA_DIRECTORY) == 0);
         break;
      case IFSListAttrsRep.AS400_OBJECT:
         //Server libraries and database files look like directories.
         String nameUpper = name.toUpperCase();         // @C2a
         answer = !(nameUpper.endsWith(".LIB") ||
                    nameUpper.endsWith(".FILE")); //B1C Changed path_ to name
                  //@C2c
                  //@B1D Removed code that checked for file separators
                  //|| path_.endsWith(".LIB" + IFSFile.separator) ||
                  //path_.endsWith(".FILE" + IFSFile.separator));
         break;
      default:
         answer = false;
    }
    return answer;
  }

  /**
   Determines if the integrated file system object represented by this object exists.
   **/
  public int exists()
    throws IOException, AS400SecurityException
  {
    return exists(fd_.path_);
  }


  //@B4a
  /**
   Determines if the integrated file system object represented by this object exists.
   **/
  private int exists(String name)
    throws IOException, AS400SecurityException
  {
    int returnCode = IFSReturnCodeRep.SUCCESS;

    // Ensure that we are connected to the server.
    fd_.connect();

    // @A8D if (attributesReply_ == null)
    // @A8D {
      returnCode = IFSReturnCodeRep.FILE_NOT_FOUND;
      // Attempt to list the attributes of the specified file.
      try
      {
        IFSListAttrsRep attrs = getAttributeSetFromServer(name);
        if (attrs != null)
        {
          returnCode = IFSReturnCodeRep.SUCCESS;
          attributesReply_ = attrs;
        }
      }
      catch (AS400SecurityException e)
      {
        returnCode = IFSReturnCodeRep.ACCESS_DENIED_TO_DIR_ENTRY;
      }
    // @A8D }
    return (returnCode);
  }


  /**
   Returns the path name of the integrated file system object represented by this object.  This is the full path starting at the root directory.
   @return The absolute path name of this integrated file system object.
   **/
  String getAbsolutePath()
  {
    return fd_.path_;
  }


  /**
   Get a list attribute reply from the server for a single entity (get the attributes
   of a specific object, not the attributes of every file in a directory).
  **/
  // @D1 - new method because of changes to java.io.file in Java 2.

  private IFSListAttrsRep getAttributeSetFromServer(String filePath)
    throws IOException, AS400SecurityException
  {
    IFSListAttrsRep reply = null;

    // Attempt to list the attributes of the specified file.
    Vector replys = listAttributes(filePath, NO_MAX_GET_COUNT, null, NO_RESTART_NAME, !SORT_LIST);
    // Note: This does setFD() on each returned IFSListAttrsRep.

    // If this is a directory then there must be exactly one reply.
    if (replys != null && replys.size() == 1)
    {
      reply = (IFSListAttrsRep) replys.elementAt(0);
    }

    return reply;
  }


  // @B4a
  /**
   Returns the file's data CCSID.  Returns -1 if failure or if directory.
   **/
  public int getCCSID()
    throws IOException, AS400SecurityException
  {
    fd_.connect();
    return fd_.getCCSID();
  }


  /**
   Determines the amount of unused storage space in the file system.
   @return The number of bytes of storage available.

   @exception ConnectionDroppedException If the connection is dropped unexpectedly.
   @exception ExtendedIOException If an error occurs while communicating with the server.
   @exception InterruptedIOException If this thread is interrupted.
   @exception ServerStartupException If the server cannot be started.
   @exception UnknownHostException If the server cannot be located.

   **/
  public long getFreeSpace()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    try
    {
      fd_.connect();
    }
    catch (AS400SecurityException e)
    {
      throw new ExtendedIOException(fd_.path_, ExtendedIOException.ACCESS_DENIED);
    }

    // The File Server team advises us to make two queries:
    // First query the space available to the user (within the user profile's "Maximum Storage Allowed" limit).
    // Then query the total space available in the file system.
    // The smaller of the two values is what we should then report to the application.

    long freeSpaceForUser = 0L;  // free space available to the user profile
    long freeSpaceInFileSystem = 0L; // actual total free space in file system

    // First, query the free space available to the user within their limit.

    ClientAccessDataStream ds = null;
    try
    {
      // Issue a query space request.
      IFSQuerySpaceReq req = new IFSQuerySpaceReq(0);  // 0 indicates "return attributes for user"
      ds = (ClientAccessDataStream) fd_.server_.sendAndReceive(req);
    }
    catch(ConnectionDroppedException e)
    {
      Trace.log(Trace.ERROR, "Byte stream server connection lost.");
      fd_.connectionDropped(e);
    }
    catch(InterruptedException e)
    {
      Trace.log(Trace.ERROR, "Interrupted");
      throw new InterruptedIOException(e.getMessage());
    }

    // Verify the reply.
    int rc = 0;
    if (ds instanceof IFSQuerySpaceRep)
    {
      freeSpaceForUser = ((IFSQuerySpaceRep) ds).getFreeSpace();
    }
    else if (ds instanceof IFSReturnCodeRep)
    {
      rc = ((IFSReturnCodeRep) ds).getReturnCode();
      if (rc != IFSReturnCodeRep.SUCCESS)
      {
        Trace.log(Trace.ERROR, "IFSReturnCodeRep return code = ", rc);
      }
      throw new ExtendedIOException(fd_.path_, rc);
    }
    else
    {
      // Unknown data stream.
      Trace.log(Trace.ERROR, "Unknown reply data stream ",
                ds.getReqRepID());
      throw new
        InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                               InternalErrorException.DATA_STREAM_UNKNOWN);
    }

    if (Trace.traceOn_) {
      if (freeSpaceForUser == IFSQuerySpaceRep.NO_MAX) {
        Trace.log(Trace.DIAGNOSTIC, "MaxStorage appears to be set to *NOMAX");
      }
      else {
        Trace.log(Trace.DIAGNOSTIC, "MaxStorage does not appear to be set to *NOMAX");
      }
    }


    // Now prepare to issue second query, to determine the total actual free space in the file system.

    // To query the file system, we need to specify a "working directory handle" to the directory.  Get a handle.
    String path = fd_.path_;
    if (isDirectory() != IFSReturnCodeRep.SUCCESS) {
      path = IFSFile.getParent(fd_.path_);
    }
    byte[] pathname = fd_.getConverter().stringToByteArray(path);
    ds = null;
    try
    {
      // Issue a Create Working Directory Handle request.
      IFSCreateDirHandleReq req = new IFSCreateDirHandleReq(pathname, fd_.preferredServerCCSID_);
      ds = (ClientAccessDataStream) fd_.server_.sendAndReceive(req);
    }
    catch(ConnectionDroppedException e)
    {
      Trace.log(Trace.ERROR, "Byte stream server connection lost.");
      fd_.connectionDropped(e);
    }
    catch(InterruptedException e)
    {
      Trace.log(Trace.ERROR, "Interrupted");
      throw new InterruptedIOException(e.getMessage());
    }

    // Verify the reply.
    rc = 0;
    int dirHandle = 0;
    if (ds instanceof IFSCreateDirHandleRep)
    {
      dirHandle = ((IFSCreateDirHandleRep) ds).getHandle();
    }
    else if (ds instanceof IFSReturnCodeRep)
    {
      rc = ((IFSReturnCodeRep) ds).getReturnCode();
      if (rc != IFSReturnCodeRep.SUCCESS)
      {
        Trace.log(Trace.ERROR, "IFSReturnCodeRep return code = ", rc);
      }
      throw new ExtendedIOException(fd_.path_, rc);
    }
    else
    {
      // Unknown data stream.
      Trace.log(Trace.ERROR, "Unknown reply data stream ",
                ds.getReqRepID());
      throw new
        InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                               InternalErrorException.DATA_STREAM_UNKNOWN);
    }


    // Query the actual total available space in the file system.
    ds = null;
    try
    {
      // Issue a query space request.
      IFSQuerySpaceReq req = new IFSQuerySpaceReq(dirHandle);
      ds = (ClientAccessDataStream) fd_.server_.sendAndReceive(req);
    }
    catch(ConnectionDroppedException e)
    {
      Trace.log(Trace.ERROR, "Byte stream server connection lost.");
      fd_.connectionDropped(e);
    }
    catch(InterruptedException e)
    {
      Trace.log(Trace.ERROR, "Interrupted");
      throw new InterruptedIOException(e.getMessage());
    }

    // Verify the reply.
    rc = 0;
    if (ds instanceof IFSQuerySpaceRep)
    {
      freeSpaceInFileSystem = ((IFSQuerySpaceRep) ds).getFreeSpace();
    }
    else if (ds instanceof IFSReturnCodeRep)
    {
      rc = ((IFSReturnCodeRep) ds).getReturnCode();
      if (rc != IFSReturnCodeRep.SUCCESS)
      {
        Trace.log(Trace.ERROR, "IFSReturnCodeRep return code = ", rc);
      }
      throw new ExtendedIOException(fd_.path_, rc);
    }
    else
    {
      // Unknown data stream.
      Trace.log(Trace.ERROR, "Unknown reply data stream ",
                ds.getReqRepID());
      throw new
        InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                               InternalErrorException.DATA_STREAM_UNKNOWN);
    }

    return Math.min(freeSpaceForUser, freeSpaceInFileSystem);
  }


  /**
   Returns the name of the user profile that is the owner of the file.
   Returns "" if called against a directory.
   @exception ExtendedIOException if file does not exist.
   **/
  public String getOwnerName()
    throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException
  {
    // Design note: This method demonstrates how to get attributes that are returned in the OA1* structure (as opposed to the OA2).
    String ownerName = null;

    fd_.connect();
    // The 'owner name' field is in the OA1 structure; the flag is in the first Flags() field.
    try
    {
      IFSListAttrsRep reply = fd_.listObjAttrs1(IFSObjAttrs1.OWNER_NAME_FLAG, 0);
      if (reply != null) {
        ownerName = reply.getOwnerName(fd_.system_.getCcsid());
      }
      else {
        if (Trace.traceOn_) Trace.log(Trace.WARNING, "Return code from getOwnerName: " + fd_.errorRC_);
        if (fd_.errorRC_ == IFSReturnCodeRep.FILE_NOT_FOUND ||
            fd_.errorRC_ == IFSReturnCodeRep.PATH_NOT_FOUND)
        {
          throw new ExtendedIOException(fd_.path_, ExtendedIOException.PATH_NOT_FOUND);
        }
      }
    }
    catch (ExtendedIOException e) {
      if (e.getReturnCode() == ExtendedIOException.DIR_ENTRY_EXISTS) {
        if (Trace.traceOn_) Trace.log(Trace.WARNING, "Unable to determine owner of directory.", e);
      }
      else throw e;
    }

    return (ownerName == null ? "" : ownerName);
  }


  // Design note: The following is an alternative implementation of getOwnerName(), using the Qp0lGetAttr API.

//  /**
//   Returns the name of the user profile that is the owner of the file.
//   Returns null if *NOUSRPRF or if error.
//   **/
//  private String getOwnerName(AS400 system)
//    throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException, ObjectDoesNotExistException
//  {
//    String ownerName = null;
//    int pathnameCCSID = fd_.converter_.getCcsid();
//    ProgramParameter[] parms = new ProgramParameter[7];
//
//    //
//    // Parameter 0 (input, reference): The "path name" structure.
//
//    ByteArrayOutputStream baos = new ByteArrayOutputStream(600);
//    byte[] zeros = { (byte)0, (byte)0, (byte)0, (byte)0, (byte)0,
//                     (byte)0, (byte)0, (byte)0, (byte)0, (byte)0 };
//    // BINARY(4): CCSID of pathname and path delimiter
//    baos.write(BinaryConverter.intToByteArray(pathnameCCSID),0,4);
//    // CHAR(2): Country or region ID.  X'0000' == Use the current job country or region ID.
//    baos.write(zeros,0,2);
//    // CHAR(3): Language ID.  X'000000' == Use the current job language ID.
//    baos.write(zeros,0,3);
//    // CHAR(3): Reserved.  Must be set to hexadecimal zeros.
//    baos.write(zeros,0,3);
//    // BINARY(4): Path type indicator.  0 == The path name is a character string, and the path name delimiter character is 1 character long.
//    baos.write(zeros,0,4);
//    // BINARY(4): Length of path name (in bytes).
//    byte[] pathnameBytes = fd_.getPathnameAsBytes();
//    baos.write(BinaryConverter.intToByteArray(pathnameBytes.length),0,4);
//    // CHAR(2): Path name delimiter character.
//    baos.write(fd_.converter_.stringToByteArray("/"),0,2);
//    // CHAR(10): Reserved.  Must be set to hexadecimal zeros.
//    baos.write(zeros,0,10);
//    // CHAR(*): Path name.
//    baos.write(pathnameBytes, 0, pathnameBytes.length);
//
//    parms[0] = new ProgramParameter(baos.toByteArray());
//    setPassByReference(parms[0]);
//
//    //
//    // Parameter 1 (input, reference): The attribute identifiers array.
//
//    baos.reset();
//    // BINARY(4): Number of requested attributes.
//    baos.write(BinaryConverter.intToByteArray(1),0,4);
//    // BINARY(4): Attribute identifier.  11 == QP0L_ATTR_AUTH, public/private authorities
//    baos.write(BinaryConverter.intToByteArray(11),0,4);
//    parms[1] = new ProgramParameter(baos.toByteArray());
//    setPassByReference(parms[1]);
//
//    //
//    // Parameter 2 (input, reference): Buffer for the returned attribute values.
//    parms[2] = new ProgramParameter(64);
//    setPassByReference(parms[2]);
//
//    //
//    // Parameter 3 (input): Buffer size provided.
//    parms[3] = new ProgramParameter(BinaryConverter.intToByteArray(64));
//
//    //
//    // Parameter 4 (output, reference): Buffer size needed.
//    parms[4] = new ProgramParameter(4);
//    setPassByReference(parms[4]);
//
//    //
//    // Parameter 5 (output, reference): Number of bytes returned.
//    parms[5] = new ProgramParameter(4);
//    setPassByReference(parms[5]);
//
//    // Parameter 6 (input): Follow symlink.  0 == Do not follow symlink; 1 == follow symlink
//    boolean followSymlink = false;  // this should probably be a parameter on the method
//    int follow = (followSymlink ? 1 : 0);
//    parms[6] = new ProgramParameter(BinaryConverter.intToByteArray(follow));
//
//    ServiceProgramCall spc = new ServiceProgramCall(system, "/QSYS.LIB/QP0LLIB2.SRVPGM", "Qp0lGetAttr", ServiceProgramCall.RETURN_INTEGER, parms);
//    pc.setThreadsafe(true);  // should check that the 'threadsafe' property isn't set
//
//    if (!spc.run()) {
//      throw new AS400Exception(spc.getMessageList());
//    }
//
//    // Check the returned byte counts.
//    int bufSizeNeeded = BinaryConverter.byteArrayToInt(parms[4].getOutputData(), 0);
//    int numBytesReturned = BinaryConverter.byteArrayToInt(parms[5].getOutputData(), 0);
//
//    // The 'Object Owner' field is the CHAR(10) at offset 16 in the output data.
//    byte[] outputData = parms[2].getOutputData();
//
//    int ccsid = fd_.system_.getCcsid();  // system CCSID (usually EBCDIC)
//    ConvTable conv = ConvTable.getTable(ccsid, null);
//    ownerName = conv.byteArrayToString(outputData, 16, 10).trim();
//    if (ownerName.equals("*NOUSRPRF")) ownerName = null;
//
//    return ownerName;
//  }


  // @B7a
  /**
   Returns the file's owner's "user ID" number.
   Returns -1 if error.
   **/
  public long getOwnerUID()
    throws IOException, AS400SecurityException        // @C0c
  {
    fd_.connect();
    IFSListAttrsRep reply = fd_.listObjAttrs2(); // the "owner UID" field is in the OA2 structure
    if (reply != null)
    {
      return reply.getOwnerUID();
    }
    else return -1L;        // @C0c
  }


  // @B5a
  // Returns zero-length string if the file has no subtype.
  public String getSubtype()
    throws IOException, AS400SecurityException
  {
    String subtype = "";

    // Ensure that we are connected to the server.
    fd_.connect();

    // Convert the path name to the server CCSID.
    byte[] pathname = fd_.converter_.stringToByteArray(fd_.path_);

    // Send the List Attributes request.
    byte[] extendedAttrName = fd_.converter_.stringToByteArray(".TYPE");
    IFSListAttrsReq req = new IFSListAttrsReq(pathname, fd_.preferredServerCCSID_,
                              IFSListAttrsReq.NO_AUTHORITY_REQUIRED, NO_MAX_GET_COUNT,
                              null, false, extendedAttrName, false, fd_.patternMatching_);  // @C3c
    Vector replys = fd_.listAttributes(req);

    // Verify that we got at least one reply.
    if (replys == null) {
      if (Trace.traceOn_) Trace.log(Trace.WARNING, "Received null from listAttributes(req).");
    }
    else if (replys.size() == 0) {
      if (Trace.traceOn_) Trace.log(Trace.WARNING, "Received no replies from listAttributes(req).");
    }
    else
    {
      if (replys.size() > 1) {
        if (Trace.traceOn_) Trace.log(Trace.WARNING, "Received multiple replies from listAttributes(req) (" +
                  replys.size() + ")");
      }
      IFSListAttrsRep reply = (IFSListAttrsRep)replys.elementAt(0);
      byte[] subtypeAsBytes = reply.getExtendedAttributeValue(/*fd_.serverDatastreamLevel_*/);
      if (subtypeAsBytes != null)
      {
        // Note: The EA value field is always returned in EBCDIC (ccsid=37).
        subtype = (new CharConverter(37)).byteArrayToString(subtypeAsBytes).trim();
      }
    }
    return subtype;
  }


  /**
   Determines if the integrated file system object represented by this object is a directory.
  **/
  public int isDirectory()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    int returnCode = IFSReturnCodeRep.FILE_NOT_FOUND;

    if (attributesReply_ == null)
    {
      try
      {
        attributesReply_ = getAttributeSetFromServer(fd_.path_);
      }
      catch (AS400SecurityException e)
      {
        returnCode = IFSReturnCodeRep.ACCESS_DENIED_TO_DIR_ENTRY;
      }
    }

    //@B1D Moved this code to determineIsDirectory().
    //Determine if the file attributes indicate a directory.
    //if (reply != null)
    //{
    //  boolean answer = false;
    //  switch(reply.getObjectType())
    //  {
    //    case IFSListAttrsRep.DIRECTORY:
    //    case IFSListAttrsRep.FILE:
    //      answer = ((reply.getFixedAttributes() &
    //                 IFSListAttrsRep.FA_DIRECTORY) != 0);
    //      break;
    //    case IFSListAttrsRep.AS400_OBJECT:
    //      // Server libraries and database files look like directories.
    //      answer = (path_.endsWith(".LIB") || path_.endsWith(".FILE") ||
    //                path_.endsWith(".LIB" + IFSFile.separator) ||
    //                path_.endsWith(".FILE" + IFSFile.separator));
    //      break;
    //    default:
    //      answer = false;
    //  }
    //@B1D Deleted during move for rework below.
    //  if (answer == true)
    //  {
    //   returnCode = IFSReturnCodeRep.SUCCESS;
    //  }
    //}

    //@B1A Added code to call determineIsDirectory().
    if (attributesReply_ != null)
    {
       if (determineIsDirectory(attributesReply_))
          returnCode = IFSReturnCodeRep.SUCCESS;
    }

    return returnCode;
  }


  /**
   Determines if the integrated file system object represented by this object is a "normal" file.<br>
   A file is "normal" if it is not a directory or a container of other objects.
   **/
  public int isFile()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    int returnCode = IFSReturnCodeRep.FILE_NOT_FOUND;

    if (attributesReply_ == null)
    {
      try
      {
          attributesReply_ = getAttributeSetFromServer(fd_.path_);
      }
      catch (AS400SecurityException e)
      {
        returnCode = IFSReturnCodeRep.ACCESS_DENIED_TO_DIR_ENTRY;
      }
    }

    //@B1D Moved this code to determineIsFile().
    //Determine if the file attributes indicate a directory.
    //if (reply != null)
    //{
    //  boolean answer = false;
    //  switch(reply.getObjectType())
    //  {
    //    case IFSListAttrsRep.DIRECTORY:
    //    case IFSListAttrsRep.FILE:
    //      answer = ((reply.getFixedAttributes() &
    //                 IFSListAttrsRep.FA_DIRECTORY) == 0);
    //      break;
    //    case IFSListAttrsRep.AS400_OBJECT:
    //      // Server libraries and database files look like directories.
    //      answer = !(path_.endsWith(".LIB") || path_.endsWith(".FILE") ||
    //                 path_.endsWith(".LIB" + IFSFile.separator) ||
    //                 path_.endsWith(".FILE" + IFSFile.separator));
    //      break;
    //    default:
    //      answer = false;
    //  }
    //@B1D Deleted this during move for rework below.
    //  if (answer == true)
    //  {
    //    returnCode = IFSReturnCodeRep.SUCCESS;
    //  }
    //}

    //@B1A Added code to call determineIsFile().
    if (attributesReply_ != null)
    {
       if (determineIsFile(attributesReply_))
          returnCode = IFSReturnCodeRep.SUCCESS;
    }

    return returnCode;
  }

  /**
   Determines if the integrated file system object represented by this
   object has its hidden attribute set.
  **/
   // @D1 - new method because of changes to java.io.file in Java 2.

  public boolean isHidden()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    boolean result = false;

    if (attributesReply_ == null)
    {
        // Attempt to get the attributes of this object.
        attributesReply_ = getAttributeSetFromServer(fd_.path_);
    }

    // Determine if the file attributes indicate hidden.
    if (attributesReply_ != null)
    {
      result = (attributesReply_.getFixedAttributes() & IFSListAttrsRep.FA_HIDDEN) != 0;
    }

    return result;
  }

  /**
   Determines if the integrated file system object represented by this
   object has its hidden attribute set.
  **/
  // @D1 - new method because of changes to java.io.file in Java 2.

  public boolean isReadOnly()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    boolean result = false;

    if (attributesReply_ == null)
    {
        // Attempt to get the attributes of this object.
        attributesReply_ = getAttributeSetFromServer(fd_.path_);
    }

    // Determine if the file attributes indicate hidden.
    if (attributesReply_ != null)
    {
      result = (attributesReply_.getFixedAttributes() & IFSListAttrsRep.FA_READONLY) != 0;
    }

    return result;
  }


  /**
   Determines if the integrated file system object represented by this object is a symbolic link.
   **/
  public boolean isSymbolicLink()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    if (Trace.traceOn_ && fd_.getSystemVRM() < 0x00050300) {
      Trace.log(Trace.WARNING, "Server is V5R2 or lower, so isSymbolicLink() will always report false.");
      return false;
    }

    /* Temporary workaround, until better File Server support is in place.
     if (attributesReply_ != null)
     {
     System.out.println("DEBUG IFSFileImplRemote.isSymbolicLink(): attributesReply_ != null");
     result = attributesReply_.isSymbolicLink(fd_.serverDatastreamLevel_);
     }
     else
     */
    if (!determinedIsSymbolicLink_)
    {
      // Note: As of V5R3, we can't get accurate symbolic link info by querying the attrs of a specific file.
      // Instead, we must query the contents of the parent directory.
      int pathLen = fd_.path_.length();
      if (pathLen <= 1) {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Path length is less than 2, so assuming not a symbolic link: " + fd_.path_);
        isSymbolicLink_ = false;
        determinedIsSymbolicLink_ = true;
      }
      else
      {
        // Do a wildcard search.
        StringBuffer wildCardPatternBuf = new StringBuffer(fd_.path_);
        wildCardPatternBuf.setCharAt(pathLen-1, '*');
        String wildCardPattern = wildCardPatternBuf.toString();
        String dirPath = wildCardPattern.substring(0,1+wildCardPattern.lastIndexOf('/'));

        byte[] pathBytes = fd_.converter_.stringToByteArray(wildCardPattern);
        IFSCachedAttributes[] attrList = listDirectoryDetails(wildCardPattern, dirPath, NO_MAX_GET_COUNT, pathBytes, IS_RESTART_NAME, !SORT_LIST);

        IFSCachedAttributes attrs = null;
        String filename = fd_.path_.substring(1+(fd_.path_.lastIndexOf('/')));
        for (int i=0; attrs == null && i<attrList.length; i++)
        {
          // Note: No need to compare full pathnames, since we know the directory.
          if (attrList[i].name_.equals(filename)) {
            attrs = attrList[i];
          }
        }
        if (attrs != null)
        {
          isSymbolicLink_ = attrs.isSymbolicLink_;
          determinedIsSymbolicLink_ = true;
        }
        else
        {
          if (Trace.traceOn_) Trace.log(Trace.ERROR, "Received zero matches from listDirectoryDetails() against parent of " + wildCardPattern.toString());
          isSymbolicLink_ = false;
          determinedIsSymbolicLink_ = true;
        }
      }
    }

    return isSymbolicLink_;
  }




  /**
   @D3a lastAccessed0 is a new method
   Determines the time that the integrated file system object represented by this object was last accessed.
   **/
  public long lastAccessed()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    long accessDate = 0L;


    // Attempt to list the attributes of the specified file.
    // Note: Do not use cached attributes, since they may be out of date.
    IFSListAttrsRep attrs = getAttributeSetFromServer(fd_.path_);
    if (attrs != null)
    {
      attributesReply_ = attrs;
      accessDate = attrs.getAccessDate();
    }

    return accessDate;
  }


  /**
   Determines the time that the integrated file system object represented by this object was last modified.
   **/
  public long lastModified()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    long modificationDate = 0L;


    // Attempt to list the attributes of the specified file.
    // Note: Do not use cached attributes, since they may be out of date.
    IFSListAttrsRep attrs = getAttributeSetFromServer(fd_.path_);
    if (attrs != null)
    {
      attributesReply_ = attrs;
      modificationDate = attrs.getModificationDate();
    }

    return modificationDate;
  }


  /**
   Determines the length of the integrated file system object represented by this object.
   **/
  public long length()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    long size = 0L;

    if (fd_.getSystemVRM() >> 8 != 0x00000502)  // system is other than V5R2   @C1c
    {
      // Attempt to list the attributes of the specified file.
      // Note: Do not use cached attributes, since they may be out of date.
      IFSListAttrsRep attrs = getAttributeSetFromServer(fd_.path_);

      if (attrs != null)
      {
        attributesReply_ = attrs;
        size = attrs.getSize(fd_.serverDatastreamLevel_);
      }
    }
    else  // the system is V5R2 (and therefore, datastream level is 3)
    {
      // Convert the path name to the server CCSID.
      byte[] pathname = fd_.converter_.stringToByteArray(fd_.path_);

      // Send the List Attributes request.  Indicate that we want the "8-byte file size".
      IFSListAttrsReq req = new IFSListAttrsReq(pathname, fd_.preferredServerCCSID_,
                                                IFSListAttrsReq.NO_AUTHORITY_REQUIRED, NO_MAX_GET_COUNT,
                                                null, true, null, true, fd_.patternMatching_);  // @C3c
      Vector replys = fd_.listAttributes(req);

      if (replys == null) {
        if (fd_.errorRC_ != 0) {
          throw new ExtendedIOException(fd_.path_, fd_.errorRC_);
        }
        else throw new InternalErrorException(InternalErrorException.UNKNOWN);
      }
      else if (replys.size() == 0) {
        // Assume this simply indicates that the file does not exist.
        if (Trace.traceOn_) {
          Trace.log(Trace.WARNING, "Received no replies from listAttributes(req).");
        }
      }
      else
      {
        if ( replys.size() > 1 &&
             Trace.traceOn_ )
        {
            Trace.log(Trace.WARNING, "Received multiple replies from listAttributes(req) (" +
                      replys.size() + ")");
        }
        IFSListAttrsRep reply = (IFSListAttrsRep)replys.elementAt(0); // use first reply
        size = reply.getSize8Bytes(/*fd_.serverDatastreamLevel_*/);
      }
    }
    return size;
  }



  // Fetch list attributes reply(s) for the specified path.
  private Vector listAttributes(String path, int maxGetCount, byte[] restartNameOrID, boolean isRestartName, boolean sortList)           // @D4C @C3c
    throws IOException, AS400SecurityException
  {
    // Assume connect() has already been done.

    // Convert the pathname to the server CCSID.
    byte[] pathname = fd_.converter_.stringToByteArray(path);

    // Prepare the 'list attributes' request.
    IFSListAttrsReq req = new IFSListAttrsReq(pathname, fd_.preferredServerCCSID_,                              // @D4A
                                              IFSListAttrsReq.NO_AUTHORITY_REQUIRED, maxGetCount,               // @D4A
                                              restartNameOrID,                                       // @D4A @C3c
                                              isRestartName, // @C3a
                                              null, false, fd_.patternMatching_);
    if (sortList) req.setSorted(true);
    return fd_.listAttributes(req);  // Note: This does setFD() on each returned IFSListAttrsRep..
  }


  // @A7A
  // List the files/directories in the specified directory.
  // Returns null if specified file or directory does not exist.
  public String[] listDirectoryContents(String directoryPath)
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    Vector replys = listAttributes(directoryPath, NO_MAX_GET_COUNT, null, NO_RESTART_NAME, sortLists_);
    String[] names = null;

    // Add the name for each file or directory in the specified directory,
    // to the array of names.

    // @A1C
    // Changed the behavior of the list() to conform to that of the JDK1.1.x
    // so that a NULL is returned if and only if the directory or file represented
    // by this IFSFile object doesn't exist.
    //
    // Original code:
    // if (replys != null && replys.size() != 0)
    if (replys != null)                             // @A1C
    {
      names = new String[replys.size()];
      int j = 0;
      for (int i = 0; i < replys.size(); i++)
      {
        IFSListAttrsRep reply = (IFSListAttrsRep) replys.elementAt(i);
        String name = fd_.converter_.byteArrayToString(reply.getName(/*dsl*/));
        if (!(name.equals(".") || name.equals("..")))
        {
          names[j++] = name;
        }
      }

      if (j == 0)
      {
        // @A1C
        //
        // Original code:
        // names = null;
        names = new String[0];      // @A1C
      }
      else if (names.length != j)
      {
        // Copy the names to an array of the exact size.
        String[] newNames = new String[j];
        System.arraycopy(names, 0, newNames, 0, j);
        names = newNames;
      }
    }

    return names;
  }


  // @B1A Added this function to support caching attributes.
  // @C3C Morphed this method by adding a parameter and making it private.
  // List the files/directories details in the specified directory.
  // Returns null if specified file or directory does not exist.
  private IFSCachedAttributes[] listDirectoryDetails(String pathPattern,
                                                     String directoryPath,
                                                    int maxGetCount,            // @D4A
                                                    byte[] restartNameOrID,     // @C3C
                                                    boolean isRestartName,      // @C3A
                                                    boolean sortList)
     throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    IFSCachedAttributes[] fileAttributes = null;

    try
    {
      // Design note: Due to a limitation in the File Server, if we specify a "filename pattern", we cannot get OA1 or OA2 structures in the reply.
      // Only "handle-based" requests can get OA* structures in the reply; and a handls is specific to a single file.
      // This prevents us, for example, from obtaining the "name of file owner" values for an entire list of files at once; rather, we must obtain that attribute one file at a time.
      Vector replys = listAttributes(pathPattern, maxGetCount, restartNameOrID, isRestartName, sortList);

      // Add each file or directory in the specified directory,
      // to the array of files.

      int j = 0;
      if (replys != null)
      {
        fileAttributes = new IFSCachedAttributes[replys.size()];
        int dsl = fd_.serverDatastreamLevel_;
        for (int i = 0; i < replys.size(); i++)
        {
          IFSListAttrsRep reply = (IFSListAttrsRep) replys.elementAt(i);
          String name = fd_.converter_.byteArrayToString(reply.getName(/*dsl*/));
          if (!(name.equals(".") || name.equals("..")))
          {
             // isDirectory and isFile should be different unless the
             // file is an invalid symbolic link (circular or points
             // to a non-existent object).  Such a link cannot
             // be resolved and both determineIsDirectory and
             // determineIsFile will return false.  Regular symbolic links
             // will resolve.  For example, a symbolic link to a file will return
             // true from isFile and false from determineIsDirectory.
             boolean isDirectory = determineIsDirectory(reply);
             boolean isFile = determineIsFile(reply);
             IFSCachedAttributes attributes = new IFSCachedAttributes(reply.getAccessDate(),
                 reply.getCreationDate(), reply.getFixedAttributes(), reply.getModificationDate(),
                 reply.getObjectType(), reply.getSize(dsl), name, directoryPath, isDirectory, isFile, reply.getRestartID(), reply.isSymbolicLink(dsl)); //@B3A @C3C
             fileAttributes[j++] = attributes;
           }
        }
      }//end if

      if (j == 0)
      {
        fileAttributes = new IFSCachedAttributes[0];    //@B3C
      }
      else if (fileAttributes.length != j)
      {
        //Copy the attributes to an array of the exact size.
        IFSCachedAttributes[] newFileAttributes = new IFSCachedAttributes[j];   //@B3C
        System.arraycopy(fileAttributes, 0, newFileAttributes, 0, j);    //@B3C
        fileAttributes = newFileAttributes;
      }
    }
    catch (AS400SecurityException e)
    {
      fileAttributes = null;
      throw e;
    }
    return fileAttributes;
  }


  // @B1A Added this function to support caching attributes.
  // @C3c Moved logic from this method into new private method.
  // List the files/directories details in the specified directory.
  // Returns null if specified file or directory does not exist.
  public IFSCachedAttributes[] listDirectoryDetails(String pathPattern,
                                                    String directoryPath,
                                                    int maxGetCount,            // @D4A
                                                    String restartName)         // @D4A
     throws IOException, AS400SecurityException
  {
    byte[] restartNameBytes = fd_.converter_.stringToByteArray(restartName);                     // @C3M
    return listDirectoryDetails(pathPattern, directoryPath, maxGetCount, restartNameBytes, IS_RESTART_NAME, sortLists_);
  }

  // @C3a
  // List the files/directories details in the specified directory.
  // Returns null if specified file or directory does not exist.
  public IFSCachedAttributes[] listDirectoryDetails(String pathPattern,
                                                    String directoryPath,
                                                    int maxGetCount,
                                                    byte[] restartID)
     throws IOException, AS400SecurityException
  {
    return listDirectoryDetails(pathPattern, directoryPath, maxGetCount, restartID, !IS_RESTART_NAME, sortLists_);
  }



  /**
   Creates an integrated file system directory whose path name is specified by this object.
   **/
  public int mkdir(String directory)
    throws IOException, AS400SecurityException
  {
    // Ensure that the path name is set.
    fd_.connect();

    int returnCode = IFSReturnCodeRep.FILE_NOT_FOUND;

    try
    {
      // Convert the directory name to the server CCSID.
      byte[] pathname = fd_.converter_.stringToByteArray(directory);

      // Send a create directory request.
      IFSCreateDirReq req = new IFSCreateDirReq(pathname,
                                                fd_.preferredServerCCSID_);
      ClientAccessDataStream ds =
        (ClientAccessDataStream) fd_.server_.sendAndReceive(req);

      // Verify the reply.
      if (ds instanceof IFSReturnCodeRep)
      {
        returnCode = ((IFSReturnCodeRep) ds).getReturnCode();
        if (returnCode == IFSReturnCodeRep.ACCESS_DENIED_TO_DIR_ENTRY
        ||  returnCode == IFSReturnCodeRep.ACCESS_DENIED_TO_REQUEST)
        {
          throw new AS400SecurityException(AS400SecurityException.DIRECTORY_ENTRY_ACCESS_DENIED);
        }
      }
      else
      {
        // Unknown data stream.
        Trace.log(Trace.ERROR, "Unknown reply data stream ", ds.data_);
        throw new
          InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                                 InternalErrorException.DATA_STREAM_UNKNOWN);
      }
    }
    catch(ConnectionDroppedException e)
    {
      Trace.log(Trace.ERROR, "Byte stream server connection lost.");
      fd_.connectionDropped(e);
    }
    catch(InterruptedException e)
    {
      Trace.log(Trace.ERROR, "Interrupted");
      throw new InterruptedIOException(e.getMessage());
    }

    return returnCode;
  }


  /**
   Creates an integrated file system directory whose path name is specified by this object. In addition, create all parent directories as necessary.
    **/
  public int mkdirs()
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    // Traverse up the parent directories until the first parent
    // directory that exists is found, saving each parent directory
    // as we go.
    boolean success = false;
    Vector nonexistentDirs = new Vector();
    String directory = fd_.path_;
    int returnCode = IFSReturnCodeRep.FILE_NOT_FOUND;

    returnCode = exists(directory);
    if (returnCode != IFSReturnCodeRep.SUCCESS)
    {
      do
      {
        nonexistentDirs.addElement(directory);
        directory = IFSFile.getParent(directory);
      }
      while ((directory != null) && (exists(directory) != IFSReturnCodeRep.SUCCESS));
    } else
    {
     returnCode = IFSReturnCodeRep.DUPLICATE_DIR_ENTRY_NAME;
    }

    // Create each parent directory in the reverse order that
    // they were saved.
    for (int i = nonexistentDirs.size(); i > 0; i--)
    {
      // Get the name of the next directory to create.
      //try                                                                  //@B1D
      //{                                                                    //@B1D
        directory = (String) nonexistentDirs.elementAt(i - 1);
      //}                                                                    //@B1D
      //catch(Exception e)                                                   //@B1D
      //{                                                                    //@B1D
      //  Trace.log(Trace.ERROR, "Error fetching element from vector.\n" +   //@B1D
      //            "length = " + nonexistentDirs.size() + " index = ",      //@B1D
      //            i - 1);
      //  throw new InternalErrorException(InternalErrorException.UNKNOWN);  //@B1D
      //}

      // Create the next directory.
      returnCode = mkdir(directory);
      if (returnCode != IFSReturnCodeRep.SUCCESS)
      {
        // Failed to create a directory.
        break;
      }
    }

    return returnCode;
  }


  /**
   Renames the integrated file system object specified by this object to have the path name of <i>file</i>.  Wildcards are not permitted in this file name.
   @param file The new file name.
   **/
  public int renameTo(IFSFileImpl file)
    throws IOException, AS400SecurityException
  {
    // Ensure that we are connected to the server.
    fd_.connect();

    // Assume the argument has been validated by the public class.

    // Rename the file.
    boolean success = false;
    ClientAccessDataStream ds = null;
    IFSFileImplRemote otherFile = (IFSFileImplRemote)file;
    try
    {
      // Convert the path names to the server CCSID.
      byte[] oldName = fd_.converter_.stringToByteArray(fd_.path_);
      byte[] newName = fd_.converter_.stringToByteArray(otherFile.getAbsolutePath());

      // Issue a rename request.
      IFSRenameReq req = new IFSRenameReq(oldName, newName,
                                          fd_.preferredServerCCSID_,
                                          false);
      ds = (ClientAccessDataStream) fd_.server_.sendAndReceive(req);
    }
    catch(ConnectionDroppedException e)
    {
      Trace.log(Trace.ERROR, "Byte stream server connection lost.");
      fd_.connectionDropped(e);
    }
    catch(InterruptedException e)
    {
      Trace.log(Trace.ERROR, "Interrupted");
      throw new InterruptedIOException(e.getMessage());
    }

    int returnCode = IFSReturnCodeRep.FILE_NOT_FOUND;
    // Verify the reply.
    if (ds instanceof IFSReturnCodeRep)
    {
      returnCode = ((IFSReturnCodeRep) ds).getReturnCode();
      if (returnCode == IFSReturnCodeRep.SUCCESS)
        success = true;
      else
        if (Trace.traceOn_) Trace.log(Trace.ERROR, "Error renaming file: " +
                    "IFSReturnCodeRep return code = ", returnCode);
    }
    else
    {
      // Unknown data stream.
      Trace.log(Trace.ERROR, "Unknown reply data stream ", ds.data_);
      throw new
        InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                               InternalErrorException.DATA_STREAM_UNKNOWN);
    }

    if (success)
    {
      fd_.path_ = otherFile.getAbsolutePath();

      // Clear any cached attributes.
      attributesReply_ = null;
    }

    return returnCode;
  }


  /**
   Sets the file's "data CCSID" tag.
   **/
  public boolean setCCSID(int ccsid)
    throws IOException, AS400SecurityException
  {
    // To change the file data CCSID, we need to get the file's current attributes (in an OA2 structure), reset the 'CCSID' field, and then send back the modified OA2 struct in a Change Attributes request.

    fd_.connect();
    IFSListAttrsRep reply = fd_.listObjAttrs2();  // get current attributes (OA2 structure)
    if (reply == null) {
      if (fd_.errorRC_ != 0) {
        throw new ExtendedIOException(fd_.path_, fd_.errorRC_);
      }
      else throw new InternalErrorException(InternalErrorException.UNKNOWN);
    }

    boolean success = false;
    IFSObjAttrs2 objAttrs = reply.getObjAttrs2(); // get the OA2* structure

    // Sanity-check the length: If it's an OA2a or OA2b, the length will be 144 bytes.  If it's an OA2c, the length will be 160 bytes.
    if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Length of returned OA2* structure (should be 144 or 160): " + objAttrs.length());

    // Reset the "CCSID of the object" field in the OA2* structure.
    objAttrs.setCCSID(ccsid, fd_.serverDatastreamLevel_);

    // Issue a change attributes request.
    ClientAccessDataStream ds = null;
    try
    {
      // Convert the path name to the server CCSID.
      byte[] pathname = fd_.converter_.stringToByteArray(fd_.path_);

      IFSChangeAttrsReq req = new IFSChangeAttrsReq(pathname,
                                                    fd_.preferredServerCCSID_,
                                                    objAttrs,
                                                    fd_.serverDatastreamLevel_);
      ds = (ClientAccessDataStream) fd_.server_.sendAndReceive(req);
    }
    catch(ConnectionDroppedException e)
    {
      Trace.log(Trace.ERROR, "Byte stream server connection lost.");
      fd_.connectionDropped(e);
    }
    catch(InterruptedException e)
    {
      Trace.log(Trace.ERROR, "Interrupted");
      throw new InterruptedIOException(e.getMessage());
    }

    if (ds instanceof IFSReturnCodeRep)
    {
      int rc = ((IFSReturnCodeRep) ds).getReturnCode();
      if (rc == IFSReturnCodeRep.SUCCESS) {
        success = true;
        fd_.setCCSID(ccsid); // update the cached CCSID value in the fd_
      }
      else {
        if (Trace.traceOn_) Trace.log(Trace.ERROR, "Error setting file data CCSID: " +
                                      "IFSReturnCodeRep return code = ", rc);
      }
    }
    else
    {
      // Unknown data stream.
      Trace.log(Trace.ERROR, "Unknown reply data stream ", ds.data_);
      throw new
        InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                               InternalErrorException.DATA_STREAM_UNKNOWN);
    }

    return success;
  }



  /**
   Changes the fixed attributes (read only, hidden, etc.) of the integrated
   file system object represented by this object to <i>attributes</i>.

   @param attributes The set of attributes to apply to the object.  Note
                     these attributes are not ORed with the existing
                     attributes.  They replace the existing fixed
                     attributes of the file.
   @return true if successful; false otherwise.

   @exception ConnectionDroppedException If the connection is dropped unexpectedly.
   @exception ExtendedIOException If an error occurs while communicating with the server.
   @exception InterruptedIOException If this thread is interrupted.
   @exception ServerStartupException If the server cannot be started.
   @exception UnknownHostException If the server cannot be located.
   **/
   // @D1 - new method because of changes to java.io.file in Java 2.

  public boolean setFixedAttributes(int attributes)
    throws IOException
  {
    // Assume the argument has been validated by the public class.

    // Ensure that we are connected to the server.
    try
    {
      fd_.connect();
    }
    catch (AS400SecurityException e)
    {
      throw new ExtendedIOException(fd_.path_, ExtendedIOException.ACCESS_DENIED);
    }
    // Issue a change attributes request.
    ClientAccessDataStream ds = null;
    try
    {
      // Convert the path name to the server CCSID.
      byte[] pathname = fd_.converter_.stringToByteArray(fd_.path_);

      IFSChangeAttrsReq req = new IFSChangeAttrsReq(pathname,
                                                    fd_.preferredServerCCSID_,
                                                    attributes,
                                                    true,
                                                    fd_.serverDatastreamLevel_);
      ds = (ClientAccessDataStream) fd_.server_.sendAndReceive(req);
    }
    catch(ConnectionDroppedException e)
    {
      Trace.log(Trace.ERROR, "Byte stream server connection lost.");
      fd_.connectionDropped(e);
    }
    catch(InterruptedException e)
    {
      Trace.log(Trace.ERROR, "Interrupted");
      throw new InterruptedIOException(e.getMessage());
    }

    // Verify the reply.
    boolean success = false;
    if (ds instanceof IFSReturnCodeRep)
    {
      int rc = ((IFSReturnCodeRep) ds).getReturnCode();
      if (rc == IFSReturnCodeRep.SUCCESS)
        success = true;
      else
        if (Trace.traceOn_) Trace.log(Trace.ERROR, "Error setting file attributes: " +
                  "IFSReturnCodeRep return code = ", rc);
    }
    else
    {
      // Unknown data stream.
      Trace.log(Trace.ERROR, "Unknown reply data stream ", ds.data_);
      throw new
        InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                               InternalErrorException.DATA_STREAM_UNKNOWN);
    }

    // Clear any cached attributes.
    attributesReply_ = null;

    return success;
  }



  /**
   Alters the hidden attribute of the object.  If <i>attribute</i>
   is true, the bit is turned on.  If <i>attribute</i> is turned off,
   the bit is turned off.

   @param attributes The new state of the hidden attribute.  The hidden
                     attribute is the second bit from the right.

   @return true if successful; false otherwise.

   @exception ConnectionDroppedException If the connection is dropped unexpectedly.
   @exception ExtendedIOException If an error occurs while communicating with the server.
   @exception InterruptedIOException If this thread is interrupted.
   @exception ServerStartupException If the server cannot be started.
   @exception UnknownHostException If the server cannot be located.
   **/
   // @D1 - new method because of changes to java.io.file in Java 2.

  public boolean setHidden(boolean attribute)
    throws IOException
  {
    // Assume the argument has been validated by the public class.

    // Ensure that we are connected to the server.
    try
    {
      fd_.connect();
    }
    catch (AS400SecurityException e)
    {
      throw new ExtendedIOException(fd_.path_, ExtendedIOException.ACCESS_DENIED);
    }

    boolean success = false;
    IFSListAttrsRep attributes = null;

    // Setting the fixed attributes of a file involves
    // replacing the current set of attributes.  The first
    // set is to get the current set.
    try
    {
       attributes = getAttributeSetFromServer(fd_.path_);
    }
    catch (AS400SecurityException e)
    {
      Trace.log(Trace.ERROR, "Failed to get attribute set", e);
      throw new ExtendedIOException(fd_.path_, ExtendedIOException.ACCESS_DENIED);
    }

    if (attributes != null)
    {
       // Now that we have the current set of attributes, figure
       // out if the bit is currently on.
       int currentFixedAttributes = attributes.getFixedAttributes();
       boolean currentHiddenBit = (currentFixedAttributes & 2) != 0;

       // If current does not match what the user wants we need to go
       // to the server to fix it.
       if (currentHiddenBit != attribute)
       {
          int newAttributes;

          // If the user wants hidden on set the bit else the
          // user wants the bit off so clear it.
          if (attribute)
             newAttributes = currentFixedAttributes + 2;
          else
             newAttributes = currentFixedAttributes & 0x7ffffffd;

              // Issue a change attributes request.
          ClientAccessDataStream ds = null;
          try
          {
              // Convert the path name to the server CCSID.
              byte[] pathname = fd_.converter_.stringToByteArray(fd_.path_);

              IFSChangeAttrsReq req = new IFSChangeAttrsReq(pathname,
                                                            fd_.preferredServerCCSID_,
                                                            newAttributes,
                                                            true,
                                                            fd_.serverDatastreamLevel_);
              ds = (ClientAccessDataStream) fd_.server_.sendAndReceive(req);
          }
          catch(ConnectionDroppedException e)
          {
             Trace.log(Trace.ERROR, "Byte stream server connection lost.");
             fd_.connectionDropped(e);
          }
          catch(InterruptedException e)
          {
             Trace.log(Trace.ERROR, "Interrupted");
             throw new InterruptedIOException(e.getMessage());
          }

          if (ds instanceof IFSReturnCodeRep)
          {
             int rc = ((IFSReturnCodeRep) ds).getReturnCode();
             if (rc == IFSReturnCodeRep.SUCCESS)
               success = true;
             else
               if (Trace.traceOn_) Trace.log(Trace.ERROR, "Error setting hidden attribute: " +
                         "IFSReturnCodeRep return code = ", rc);
          }
          else
          {
             // Unknown data stream.
             Trace.log(Trace.ERROR, "Unknown reply data stream ", ds.data_);
             throw new
                 InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                                  InternalErrorException.DATA_STREAM_UNKNOWN);
          }

          // Clear any cached attributes.
          attributesReply_ = null;

       }
       else
          success = true;
    }
    return success;
  }


  // @B8c
  /**
   Changes the last modified time of the integrated file system object represented by this object to <i>time</i>.
   @param time The desired last modification time (measured in milliseconds
   since January 1, 1970 00:00:00 GMT), or -1 to set the last modification time to the current system time.

   @return true if successful; false otherwise.

   @exception ConnectionDroppedException If the connection is dropped unexpectedly.
   @exception ExtendedIOException If an error occurs while communicating with the server.
   @exception InterruptedIOException If this thread is interrupted.
   @exception ServerStartupException If the server cannot be started.
   @exception UnknownHostException If the server cannot be located.
   **/

  public boolean setLastModified(long time)
    throws IOException
  {
    // Assume the argument has been validated by the public class.

    int fileHandle = UNINITIALIZED;
    boolean success = false;

    // Ensure that we are connected to the server.
    try
    {
      fd_.connect();

      if (time == -1)  // @B8a
      {
        // We are setting modification time to "current system time".
        // To do that, we will simply read and write-back the first byte in the file.

        // Open the file for read/write.
        fileHandle = fd_.createFileHandle(IFSOpenReq.WRITE_ACCESS, IFSOpenReq.OPEN_OPTION_FAIL_OPEN);
        if (fileHandle == UNINITIALIZED) return false;
        else fd_.setOpen(true, fileHandle);  // inform the descriptor of the file handle

        byte[] buffer = new byte[1];  // buffer for reading/writing a single byte

        // If we're setting timestamp to "current system time", we'll need to know how big the file is.
        boolean fileIsEmpty = false;  // @B8a
        if (time == -1) fileIsEmpty = (length()==0 ? true : false);  // @B8a

        if (fileIsEmpty)
        {
          // Update last-modification date by writing one byte (the value doesn't matter).
          fd_.writeBytes(buffer, 0, 1, true);

          // Reset the file size to zero.
          success = fd_.setLength(0);
        }
        else // the file is not empty
        {
          // Read the first byte.
          if (1 == fd_.read(buffer, 0, 1))
          {
            // Write back the first byte.
            fd_.setFileOffset(0);
            fd_.writeBytes(buffer, 0, 1, true);
            success = true;
          }
          else
          {
            if (Trace.traceOn_) Trace.log(Trace.ERROR, "Failed to read first byte of file.");
            success = false;
          }
        }
      }
      else  // the caller specified a last-modified time
      {
        // Issue a change attributes request.
        ClientAccessDataStream ds = null;
        try
        {
          // Convert the path name to the server CCSID.
          byte[] pathname = fd_.converter_.stringToByteArray(fd_.path_);

          IFSChangeAttrsReq req = new IFSChangeAttrsReq(pathname,
                                                        fd_.preferredServerCCSID_,
                                                        0, time, 0,
                                                        fd_.serverDatastreamLevel_);
          ds = (ClientAccessDataStream) fd_.server_.sendAndReceive(req);
        }
        catch(ConnectionDroppedException e)
        {
          Trace.log(Trace.ERROR, "Byte stream server connection lost.");
          fd_.connectionDropped(e);
        }
        catch(InterruptedException e)
        {
          Trace.log(Trace.ERROR, "Interrupted");
          throw new InterruptedIOException(e.getMessage());
        }

        // Verify the reply.
        if (ds instanceof IFSReturnCodeRep)
        {
          int rc = ((IFSReturnCodeRep) ds).getReturnCode();
          if (rc == IFSReturnCodeRep.SUCCESS)
            success = true;
          else
            if (Trace.traceOn_) Trace.log(Trace.ERROR, "Error setting last-modified date: " +
                                          "IFSReturnCodeRep return code = ", rc);
        }
        else
        {
          // Unknown data stream.
          Trace.log(Trace.ERROR, "Unknown reply data stream ", ds.data_);
          throw new
            InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                                   InternalErrorException.DATA_STREAM_UNKNOWN);
        }
      }

      // Clear any cached attributes.
      attributesReply_ = null;
    }
    catch (AS400SecurityException e) {
      throw new ExtendedIOException(fd_.path_, ExtendedIOException.ACCESS_DENIED);
    }
    finally {
      if (fileHandle != UNINITIALIZED) fd_.close(fileHandle);
    }

    return success;
  }


  // @B8a
  /**
   Sets the length of the integrated file system object represented by this object.  The file can be made larger or smaller.  If the file is made larger, the contents of the new bytes of the file are undetermined.
   @param length The new length, in bytes.
   @return true if successful; false otherwise.

   @exception ConnectionDroppedException If the connection is dropped unexpectedly.
   @exception ExtendedIOException If an error occurs while communicating with the server.
   @exception InterruptedIOException If this thread is interrupted.
   @exception ServerStartupException If the server cannot be started.
   @exception UnknownHostException If the server cannot be located.
   **/
  public boolean setLength(int length)
    throws IOException, AS400SecurityException
  {
    // Assume the argument has been validated by the public class.

    // Ensure that we are connected to the server.
    try
    {
      fd_.connect();
    }
    catch (AS400SecurityException e)
    {
      throw new ExtendedIOException(fd_.path_, ExtendedIOException.ACCESS_DENIED);
    }

    // Clear any cached attributes.
    attributesReply_ = null;

    return fd_.setLength(length);
  }

  /**
   Sets the file path.
   @param path The absolute file path.
   **/
  public void setPath(String path)
  {
    // Assume the argument has been validated by the public class.

    // Ensure that the path is not altered after the connection is
    // established.
    if (fd_.server_ != null)
    {
      throw new ExtendedIllegalStateException("path",
                               ExtendedIllegalStateException.PROPERTY_NOT_CHANGED);
    }

    // If the specified path doesn't start with the separator character,
    // add one.  All paths are absolute for IFS.
    String newPath;
    if (path.length() == 0 || path.charAt(0) != IFSFile.separatorChar)
    {
      newPath = IFSFile.separator + path;
    }
    else
    {
      newPath = path;
    }

    // Update the path value.
    fd_.path_ = newPath;
  }


  // Sets a ProgramParameter to "pass by reference".
  private static final void setPassByReference(ProgramParameter parm)
  {
    try { parm.setParameterType(ProgramParameter.PASS_BY_REFERENCE); }
    catch (PropertyVetoException pve) {}  // should never happen
  }


  /**
   Sets the pattern-matching behavior used when files are listed by any of the <tt>list()</tt> or <tt>listFiles()</tt> methods.  The default is PATTERN_POSIX.
   @param patternMatching Either {@link IFSFile#PATTERN_POSIX PATTERN_POSIX}, {@link IFSFile#PATTERN_POSIX_ALL PATTERN_POSIX_ALL}, or {@link IFSFile#PATTERN_OS2 PATTERN_OS2}
   **/
  public void setPatternMatching(int patternMatching)
  {
    fd_.patternMatching_ = patternMatching;
  }


  /**
   Alters the read only attribute of the object.  If <i>attribute</i>
   is true, the bit is turned on.  If <i>attribute</i> is turned off,
   the bit is turned off.

   @param attributes The new state of the read only attribute

   @return true if successful; false otherwise.

   @exception ConnectionDroppedException If the connection is dropped unexpectedly.
   @exception ExtendedIOException If an error occurs while communicating with the server.
   @exception InterruptedIOException If this thread is interrupted.
   @exception ServerStartupException If the server cannot be started.
   @exception UnknownHostException If the server cannot be located.
   **/
   // @D1 - new method because of changes to java.io.file in Java 2.

  public boolean setReadOnly(boolean attribute)
    throws IOException
  {
    // Assume the argument has been validated by the public class.

    // Ensure that we are connected to the server.
    try
    {
      fd_.connect();
    }
    catch (AS400SecurityException e)
    {
      throw new ExtendedIOException(fd_.path_, ExtendedIOException.ACCESS_DENIED);
    }

    boolean success = false;
    IFSListAttrsRep attributes = null;

    try
    {
       attributes = getAttributeSetFromServer(fd_.path_);
    }
    catch (AS400SecurityException e)
    {
      Trace.log(Trace.ERROR, "Failed to get attribute set", e);
      throw new ExtendedIOException(fd_.path_, ExtendedIOException.ACCESS_DENIED);
    }

    // Same as setHidden -- setting fixed attributes is a total replacement
    // of the fixed attributes.  So, we have to get the current set, fix up
    // the readonly bit, then put them back.
    if (attributes != null)
    {
       int currentFixedAttributes = attributes.getFixedAttributes();
       boolean currentReadOnlyBit = (currentFixedAttributes & 1) != 0;

       // If the bit is not currently set to what the user wants.
       if (currentReadOnlyBit != attribute)
       {
          int newAttributes;

          // If the user wants readonly on, add to the current set.
          // If the user wants it off, clear the bit.
          if (attribute)
             newAttributes = currentFixedAttributes + 1;
          else
             newAttributes = currentFixedAttributes & 0x7ffffffe;

              // Issue a change attributes request.
          ClientAccessDataStream ds = null;
          try
          {
              // Convert the path name to the server CCSID.
              byte[] pathname = fd_.converter_.stringToByteArray(fd_.path_);

              IFSChangeAttrsReq req = new IFSChangeAttrsReq(pathname,
                                                            fd_.preferredServerCCSID_,
                                                            newAttributes,
                                                            true,
                                                            fd_.serverDatastreamLevel_);
              ds = (ClientAccessDataStream) fd_.server_.sendAndReceive(req);
          }
          catch(ConnectionDroppedException e)
          {
             Trace.log(Trace.ERROR, "Byte stream server connection lost.");
             fd_.connectionDropped(e);
          }
          catch(InterruptedException e)
          {
             Trace.log(Trace.ERROR, "Interrupted");
             throw new InterruptedIOException(e.getMessage());
          }

          if (ds instanceof IFSReturnCodeRep)
          {
             int rc = ((IFSReturnCodeRep) ds).getReturnCode();
             if (rc == IFSReturnCodeRep.SUCCESS)
               success = true;
             else
               if (Trace.traceOn_) Trace.log(Trace.ERROR, "Error setting read-only attribute: " +
                         "IFSReturnCodeRep return code = ", rc);
          }
          else
          {
             // Unknown data stream.
             Trace.log(Trace.ERROR, "Unknown reply data stream ", ds.data_);
             throw new
                 InternalErrorException(Integer.toHexString(ds.getReqRepID()),
                                  InternalErrorException.DATA_STREAM_UNKNOWN);
          }

          // Clear any cached attributes.
          attributesReply_ = null;
       }
       else
          success = true;
    }
    return success;
  }


  /**
   Sets the sorting behavior used when files are listed by any of the <tt>list()</tt> or <tt>listFiles()</tt> methods.  The default is <tt>false</tt> (unsorted).
   @param sort If <tt>true</tt>: Return lists of files in sorted order.
   If <tt>false</tt>: Return lists of files in whatever order the file system provides.

   @exception IOException If an error occurs while communicating with the server.
   @exception AS400SecurityException If a security or authority error occurs.
   **/
  public void setSorted(boolean sort)
  {
    sortLists_ = sort;
  }


  /**
   Sets the system.
   @param system The server object.
   **/
  public void setSystem(AS400Impl system)
  {
    // Assume the argument has been validated by the public class.

    fd_.system_ = (AS400ImplRemote)system;
  }

}
