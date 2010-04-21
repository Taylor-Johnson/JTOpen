///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: IFSFileWriter.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approvsed by the Open Source Initiative.         
// Copyright (C) 2004-2004 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.IOException;
import java.io.Writer;

/**
Convenience class for writing character files in the integrated file system.
The behavior of this class is comparable to <tt>java.io.FileWriter</tt>.
<br>
IFSFileWriter is meant for writing streams of characters.
For writing streams of raw bytes, use {@link IFSFileOutputStream IFSFileOutputStream}.
If an <tt>OutputStream</tt> is required, use {@link IFSTextFileOutputStream IFSTextFileOutputStream}.
<p>
The following example illustrates the use of IFSFileWriter:
<pre>
import java.io.PrintWriter;
import java.io.BufferedWriter;
// Work with /File1 on the system eniac.
AS400 as400 = new AS400("eniac");
IFSFile file = new IFSFile(system, "/File1");
PrintWriter writer = new PrintWriter(new BufferedWriter(new IFSFileWriter(file)));
// Write a line of text to the file, converting characters.
writer.println(text);
// Close the file.
writer.close();
</pre>
 **/
public class IFSFileWriter extends Writer
{
  private static final String copyright = "Copyright (C) 2004-2004 International Business Machines Corporation and others.";

  static final long serialVersionUID = 4L;


  /**
   Share option that allows read and write access by other users.
   **/
  public final static int SHARE_ALL = IFSFileOutputStream.SHARE_ALL;
  /**
   Share option that does not allow read or write access by other users.
   **/
  public final static int SHARE_NONE = IFSFileOutputStream.SHARE_NONE;
  /**
   Share option that allows only read access by other users.
   **/
  public final static int SHARE_READERS = IFSFileOutputStream.SHARE_READERS;
  /**
   Share option that allows only write access by other users.
   **/
  public final static int SHARE_WRITERS = IFSFileOutputStream.SHARE_WRITERS;


  transient private ConvTableWriter writer_;

  private IFSFileOutputStream outputStream_;


  /**
   Constructs an IFSFileWriter object. 
   Other readers and writers are allowed to access the file.
   The file is opened if it exists; otherwise an exception is thrown.
   The file's CCSID is obtained by referencing the file's "coded character set ID" tag on the server.
   Any data currently in the file will be overwritten.
   Other readers and writers are allowed to access the file.
   @param file The file to be opened for writing.

   @exception AS400SecurityException If a security or authority error occurs.
   @exception IOException If an error occurs while communicating with the server.
   @see IFSFile#createNewFile
   **/
  public IFSFileWriter(IFSFile file)
    throws AS400SecurityException, IOException
  {
    if (file == null) throw new NullPointerException("file");
    int ccsid = file.getCCSID();  // do this before opening stream
    if (ccsid == -1) throwException(file.getPath());
    outputStream_ = new IFSFileOutputStream(file);
    writer_ = new ConvTableWriter(outputStream_, ccsid);
  }

  /**
   Constructs an IFSFileWriter object.
   The file is opened if it exists; otherwise an exception is thrown.
   Any data currently in the file will be overwritten.
   Other readers and writers are allowed to access the file.
   @param file The file to be opened for writing.
   @param ccsid The CCSID to convert the data to when writing to the file.
   The file's "data CCSID" tag on the server is not changed.

   @exception AS400SecurityException If a security or authority error occurs.
   @exception IOException If an error occurs while communicating with the server.
   @see IFSFile#createNewFile
   **/
  public IFSFileWriter(IFSFile file, int ccsid)
    throws AS400SecurityException, IOException
  {
    if (file == null) throw new NullPointerException("file");
    outputStream_ = new IFSFileOutputStream(file);
    writer_ = new ConvTableWriter(outputStream_, ccsid);
  }


  /**
   Constructs an IFSFileWriter object.
   The file is opened if it exists; otherwise an exception is thrown.
   The file's CCSID is obtained by referencing the file's "coded character set ID" tag on the server.
   Other readers and writers are allowed to access the file.
   @param file The file to be opened for writing.
   @param append If true, output is appended to the file; otherwise, any data currently in the file will be overwritten.

   @exception AS400SecurityException If a security or authority error occurs.
   @exception IOException If an error occurs while communicating with the server.
   @see IFSFile#createNewFile
   **/
  public IFSFileWriter(IFSFile file, boolean append)
    throws AS400SecurityException, IOException
  {
    if (file == null) throw new NullPointerException("file");
    int ccsid = file.getCCSID();  // do this before opening stream
    if (ccsid == -1) throwException(file.getPath());
    outputStream_ = new IFSFileOutputStream(file, IFSFileOutputStream.SHARE_ALL, append);
    writer_ = new ConvTableWriter(outputStream_, ccsid);
  }

  /**
   Constructs an IFSFileWriter object.
   The file is opened if it exists; otherwise an exception is thrown.
   The file's CCSID is obtained by referencing the file's "coded character set ID" tag on the server.
   @param file The file to be opened for writing.
   @param append If true, output is appended to the file; otherwise, any data currently in the file will be overwritten.
   @param shareOption Indicates how users can access the file. <ul><li>SHARE_ALL Share access with readers and writers<li>SHARE_NONE Share access with none<li>SHARE_READERS Share access with readers<li>SHARE_WRITERS Share access with writers</ul>

   @exception AS400SecurityException If a security or authority error occurs.
   @exception IOException If an error occurs while communicating with the server.
   @see IFSFile#createNewFile
   **/
  public IFSFileWriter(IFSFile file, boolean append, int shareOption)
    throws AS400SecurityException, IOException
  {
    if (file == null) throw new NullPointerException("file");
    int ccsid = file.getCCSID();  // do this before opening stream
    if (ccsid == -1) throwException(file.getPath());
    outputStream_ = new IFSFileOutputStream(file, shareOption, append);
    writer_ = new ConvTableWriter(outputStream_, ccsid);
  }


  /**
   Constructs an IFSFileWriter object.
   The file is opened if it exists; otherwise an exception may be thrown.
   @param file The file to be opened for writing.
   @param append If true, output is appended to the file; otherwise, any data currently in the file will be overwritten.
   @param shareOption Indicates how users can access the file. <ul><li>SHARE_ALL Share access with readers and writers<li>SHARE_NONE Share access with none<li>SHARE_READERS Share access with readers<li>SHARE_WRITERS Share access with writers</ul>
   @param ccsid The CCSID to convert the data to when writing to the file.
   The file's "data CCSID" tag on the server is not changed.

   @exception AS400SecurityException If a security or authority error occurs.
   @exception IOException If an error occurs while communicating with the server.
   @see IFSFile#createNewFile
   **/
  public IFSFileWriter(IFSFile file, boolean append, int shareOption, int ccsid)
    throws AS400SecurityException, IOException
  {
    if (file == null) throw new NullPointerException("file");
    outputStream_ = new IFSFileOutputStream(file, shareOption, append);
    writer_ = new ConvTableWriter(outputStream_, ccsid);
  }

  /**
   Constructs an IFSFileWriter object. 
   The file is opened if it exists; otherwise an exception is thrown.
   @param fd The file descriptor to be opened for writing.

   @exception AS400SecurityException If a security or authority error occurs.
   @exception IOException If an error occurs while communicating with the server.
   @see IFSFile#createNewFile
   **/
  public IFSFileWriter(IFSFileDescriptor fd)
    throws AS400SecurityException, IOException
  {
    int ccsid = fd.getCCSID();  // do this before opening stream
    if (ccsid == -1) throwException(fd.getPath());
    outputStream_ = new IFSFileOutputStream(fd);
    writer_ = new ConvTableWriter(outputStream_, ccsid);
    // Note: IFSFileDescriptor has a shareOption data member.
  }


  // Method required by interface 'Appendable', new in J2SE 5.0.
  /**
   Appends the specified character to this writer.
   @param c - The 16-bit character to append. 
   @return This IFSFileWriter 
   @exception IOException  If an I/O error occurs
   **/
  public Writer append(char c)
    throws IOException
  {
    write(c);
    return this;
  }


  // Method required by interface 'Appendable', new in J2SE 5.0.
  /**
   Appends the specified character sequence to this writer.
   @param csq  The character sequence to append. If csq is null, then the four characters "null" are appended to this writer. 
   @return This IFSFileWriter 
   @exception IOException  If an I/O error occurs
   **/
  public Writer append(CharSequence csq)
    throws IOException
  {
    write(csq == null ? "null" : csq.toString());
    return this;
  }


  // Method required by interface 'Appendable', new in J2SE 5.0.
  /**
   Appends a subsequence of the specified character sequence to this writer.
   @param csq  The character sequence from which a subsequence will be appended. If csq is null, then characters will be appended as if csq contained the four characters "null".
   @param start  The index of the first character in the subsequence
   @param end The index of the character following the last character in the subsequence 
   @return This IFSFileWriter 
   @exception IndexOutOfBoundsException If start or end are negative, start is greater than end, or end is greater than csq.length()
   @exception IOException  If an I/O error occurs
   **/
  public Writer append(CharSequence csq,
                       int start,
                       int end)
    throws IOException
  {
    write(csq == null ? new String("null").substring(start,Math.min(end,4))
          : csq.subSequence(start, end).toString());
    return this;
  }


  /**
   Closes the stream, flushing it first.  Once a stream has been closed, further write() or flush() invocations will cause an IOException to be thrown.  Closing a previously-closed stream, however, has no effect.
   @exception IOException If an error occurs while communicating with the server.
   **/
  public void close()
    throws IOException
  {
    writer_.close();  // let the Writer close the IFSFileOutputStream
  }


  /**
   Flushes the underlying output stream.
   @exception IOException If an error occurs while communicating with the server.
   **/
  public void flush() throws IOException
  {
    writer_.flush();
  }


  /**
   Returns the CCSID used by this IFSFileWriter.
   @return  The CCSID, or -1 if the CCSID is not known.
   **/
  public int getCCSID()
  {
    return writer_.getCcsid();
  }

  /**
   Returns the encoding used by this IFSFileWriter.
   @return  The encoding, or null if the encoding is not known.
   **/
  public String getEncoding()
  {
    return writer_.getEncoding();
  }


  /**
   Places a lock on the file at the current position for the specified
   number of bytes.
   @param length The number of bytes to lock.
   @return The key for undoing this lock.
 
   @exception IOException If an error occurs while communicating with the server.
   @see #unlockBytes
   **/
  public IFSKey lockBytes(int length)
    throws IOException
  {
    return outputStream_.lock(length);
  }

  /**
   Undoes a lock on the file.
   @param key The key for the lock.

   @exception IOException If an error occurs while communicating with the server.
 
   @see #lockBytes
   **/
  public void unlockBytes(IFSKey key)
    throws IOException
  {
    outputStream_.unlock(key);
  }

  /**
   Writes a single character.
   @param  c  int specifying a character to be written.
   @exception IOException If an error occurs while communicating with the server.
   **/
  public void write(int c) throws IOException
  {
    writer_.write(c);
  }

  /**
   Writes the specified array of characters.
   @param  buffer  The characters to be written.
   @exception IOException If an error occurs while communicating with the server.
   **/
  public void write(char[] buffer) throws IOException
  {
    writer_.write(buffer);
  }

  /**
   Writes a portion of an array of characters.
   @param  buffer  The characters to be written.
   @param  offset  The offset into the array from which to begin extracting characters to write.
   @param  length  The number of characters to write.
   @exception IOException If an error occurs while communicating with the server.
   **/
  public void write(char[] buffer, int offset, int length) throws IOException
  {
    writer_.write(buffer, offset, length);
  }

  /**
   Writes a String.
   @param  data  The String to write.
   @exception IOException If an error occurs while communicating with the server.
   **/
  public void write(String data) throws IOException
  {
    writer_.write(data);
  }

  /**
   Writes a portion of a String.
   @param  data  The String to write.
   @param  offset  The offset into the String from which to begin extracting characters to write.
   @param  length  The number of characters to write.
   @exception IOException If an error occurs while communicating with the server.
   **/
  public void write(String data, int offset, int length) throws IOException
  {
    writer_.write(data, offset, length);
  }


  // Utility method.
  private static final void throwException(String path)
    throws ExtendedIOException
  {
    Trace.log(Trace.ERROR, "File does not exist or is not writable.");
    throw new ExtendedIOException(path, ExtendedIOException.FILE_NOT_FOUND);
  }

}