///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: AS400Text.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2001 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.UnsupportedEncodingException;
import java.io.IOException;

/**
   The AS400Text class provides character set conversion between Java String objects and
   AS/400 native code pages.
   <P>Note that in the past few releases, several constructors were deprecated because
   they did not accept an AS400 system object as an argument. Due to recent changes in 
   the behavior of the character conversion routines, this system object is no longer
   necessary, except when the AS400Text object is to be passed as a parameter on a
   Toolbox Proxy connection. Since this case is extremely rare, it is more beneficial
   not to have the constructors issue deprecation warnings.
   @see com.ibm.as400.access.CharConverter
**/
public class AS400Text implements AS400DataType
{
  private static final String copyright = "Copyright (C) 1997-2001 International Business Machines Corporation and others.";



    static final long serialVersionUID = 4L;

  private int length_;
  private int ccsid_ = 65535;
  transient private String encoding_ = null; //@D2A
  private AS400 system_;
  //@F0D transient Converter table_;
  transient ConverterImpl tableImpl_;
  private static final String defaultValue = "";
  private byte[] padding_ = null; //@E3A

  /**
    Constructs an AS400Text object.
    It uses the most likely CCSID based on the default locale.
    @param  length  The byte length of the AS/400 text.  It must be greater than or equal to zero.
  **/
  public AS400Text(int length)
  {
    if(length < 0)
    {
      throw new ExtendedIllegalArgumentException("length (" + String.valueOf(length) + ")", ExtendedIllegalArgumentException.LENGTH_NOT_VALID);
    }
    length_ = length;
//@D2D      setTable();
  }


  /**
    Constructs an AS400Text object.
    @param  length  The byte length of the AS/400 text.  It must be greater than or equal to zero.
    @param  ccsid  The CCSID of the AS/400 text.  It must refer to a valid and available CCSID.  The value 65535 will cause the data type to use the most likely CCSID based on the default locale.
  **/
  public AS400Text(int length, int ccsid)
  {
    if(length < 0)
    {
      throw new ExtendedIllegalArgumentException("length (" + String.valueOf(length) + ")", ExtendedIllegalArgumentException.LENGTH_NOT_VALID);
    }
    if(ccsid < 0) //@D2A
    {
      //@D2A
      throw new ExtendedIllegalArgumentException("ccsid (" + String.valueOf(ccsid) + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID); //@D2A
    } //@D2A
    length_ = length;
    ccsid_ = ccsid;
//@D2D      setTable();
  }


  /**
    Constructs AS400Text object.
    @param  length  The byte length of the AS/400 text.  It must be greater than or equal to zero.
    @param  encoding  The name of a character encoding.  It must be a valid and available encoding.
  **/
  public AS400Text(int length, String encoding)
  {
    if(length < 0)
    {
      throw new ExtendedIllegalArgumentException("length (" + String.valueOf(length) + ")", ExtendedIllegalArgumentException.LENGTH_NOT_VALID);
    }
    if(encoding == null)
    {
      throw new NullPointerException("encoding");
    }
    length_ = length;
    encoding_ = encoding; //@D2A

//@D2M      try
//@D2M      {
//@D2M        table_ = new Converter(encoding); //@B5C
//@D2M        ccsid_ = table_.getCcsid();
//@D2M        tableImpl_ = table_.impl; //@D0A
//@D2M      }
//@D2M      catch (UnsupportedEncodingException e)
//@D2M      {
//@D2M        throw new ExtendedIllegalArgumentException("encoding (" + encoding + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID);
//@D2M      }

  }


  /**
    Constructs an AS400Text object. The ccsid used for conversion will be the ccsid of the <i>system</i> object.
    @param  length  The byte length of the AS/400 text.
                    It must be greater than or equal to zero.
    @param  system  The AS/400 system from which the conversion table may be downloaded.
   */
  public AS400Text(int length, AS400 system)
  {
    this(length, 65535, system); //@D2C - passing a 65535 will cause setTable() to do a system.getCcsid() at conversion time.
  }


  /**
    Constructs an AS400Text object.
    @param  length  The byte length of the AS/400 text.
                    It must be greater than or equal to zero.
    @param  ccsid  The CCSID of the AS/400 text.
                   It must refer to a valid and available CCSID.
                   The value 65535 will cause the data type to use
                   the most likely CCSID based on the default locale.
    @param  system  The AS/400 system from which the conversion table may be downloaded.
   */
  public AS400Text(int length, int ccsid, AS400 system)
  {
    if(length < 0)
    {
      throw new ExtendedIllegalArgumentException("length (" + String.valueOf(length) + ")", ExtendedIllegalArgumentException.LENGTH_NOT_VALID);
    }
    if(ccsid < 0) //@D2A
    {
      //@D2A
      throw new ExtendedIllegalArgumentException("ccsid (" + String.valueOf(ccsid) + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID); //@D2A
    } //@D2A
    if(system == null)
    {
      throw new NullPointerException("system");
    }
    length_ = length;
    ccsid_ = ccsid;
    system_ = system;
//@D2D      setTable();
  }


  //@B5A - package scope constructor for use on the proxy server
  // Note that this constructor is only used in AS400FileRecordDescriptionImplRemote.
  // It is expected that the client code (AS400FileRecordDescription)
  // will call fillInConverter() on each AS400Text object returned.
  /**
    Constructs an AS400Text object.
    @param  length  The byte length of the AS/400 text.
                    It must be greater than or equal to zero.
    @param  ccsid  The CCSID of the AS/400 text.
                   It must refer to a valid and available CCSID.
                   The value 65535 will cause the data type to use
                   the most likely CCSID based on the default locale.
    @param  system  The AS/400 system from which the conversion table may be downloaded.
   */
  AS400Text(int length, int ccsid, AS400Impl system)
  {
    if(length < 0)
    {
      throw new ExtendedIllegalArgumentException("length (" + String.valueOf(length) + ")", ExtendedIllegalArgumentException.LENGTH_NOT_VALID);
    }
    if(ccsid < 0) //@D2A
    {
      //@D2A
      throw new ExtendedIllegalArgumentException("ccsid (" + String.valueOf(ccsid) + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID); //@D2A
    } //@D2A
    if(system == null)
    {
      throw new NullPointerException("system");
    }
    length_ = length;
    ccsid_ = ccsid;
    // Notice that we have not filled in the Converter object. We can't do that
    // because we don't know if this object will in the end be used on the
    // public side (Converter) or on the server side (ConverterImpl).
    //@D2 - We also can't do that yet since the Converter ctor will connect to the system.
  }


  /**
    Creates a new AS400Text object that is identical to the current instance.
    @return  The new object.
   **/
  public Object clone()
  {
    try
    {
      return super.clone();  // Object.clone does not throw exception
    }
    catch(CloneNotSupportedException e)
    {
      Trace.log(Trace.ERROR, "Unexpected cloning error", e);
      throw new InternalErrorException(InternalErrorException.UNKNOWN);
    }
  }


  /**
    Returns the byte length of the data type.
    @return  The number of bytes in the AS/400 representation of the data type.
   **/
  public int getByteLength()
  {
    return length_;
  }


  /**
    Returns the CCSID of the data type.
    @return  The CCSID.
   **/
  public int getCcsid()
  {
    if(ccsid_ == 65535) setTable(); //@D2A
    return ccsid_; //@D2A
  }


  //@B5A
  /**
    Returns the ConverterImpl object so other classes don't
    need to create a new Converter if they already have an
    AS400Text object.
  **/
  ConverterImpl getConverter() //@F0C
  {
    setTable(); //@D2A @F0C
    return tableImpl_; //@F0C
  }


  /**
    Returns a Java object representing the default value of the data type.
    @return  The String object representing an empty string ("").
   **/
  public Object getDefaultValue()
  {
    return new String(defaultValue);
  }


  /**
    Returns the encoding of the data type.
    @return  The encoding of the data type.
   **/
  public String getEncoding()
  {
    if(encoding_ == null) setTable(); //@D2A
//@D2D      setTable(); // Make sure the table is set
//@D2D      if (tableImpl_ != null) return tableImpl_.getEncoding();
//@D2D      return table_.getEncoding();
    return encoding_; //@D2A
  }


  //@B5A
  /**
    This method is used in conjunction with the constructor
    that takes an AS400Impl. It is used to fully instantiate
    the member data of this AS400Text object once it has been
    serialized and received on the client from the proxy server.
    We do it this way because we can't create a normal AS400Text
    object on the proxy server and expect it to be valid on
    the proxy client because its internal Converter object
    would not be proxified correctly.
  **/
  // When an AS400Text object is serialized from the proxy server over to
  // the client, the client code must set the converter using this method.
  void setConverter(AS400 system)
  {
    system_ = system;
    setTable();
  }


  //@D0A
  // When an AS400Text object is serialized from the client over to
  // the proxy server, the server code must set the converter using
  // this method. Note that we cannot refer directly to the ConverterImplRemote
  // class here, so it is left up to the server code to create that and pass it
  // in to this method.
  void setConverter(ConverterImpl converter)
  {
    tableImpl_ = converter;
    ccsid_ = tableImpl_.getCcsid(); // Just in case this object ever goes back to the client
  }


  //@D0A
  // private method to initialize the Converter table and its impl
  private void setTable()
  {
    if (tableImpl_ == null) //@F0C
    {
      if (Trace.traceOn_) //@D2A
      {
        Trace.log(Trace.CONVERSION, "AS400Text object initializing with "+encoding_+", "+ccsid_+", "+system_); //@D2A
      }
      if(encoding_ != null) //@D2A
      {
        //@D2A
        try                                         //@D2M
        {
          //@D2M
          Converter table = new Converter(encoding_); //@B5C //@D2M I realize this is deprecated, but we have no choice if the user specified an encoding. @F0C
          ccsid_ = table.getCcsid();               //@D2M
          tableImpl_ = table.impl; //@D0A          //@D2M
        }                                           //@D2M
        catch(UnsupportedEncodingException e)      //@D2M
        {
          //@D2M
          throw new ExtendedIllegalArgumentException("encoding (" + encoding_ + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID); //@D2M
        }                                           //@D2M
      } //@D2A
      else //@D2A
      {
        //@D2A
        try
        {
          if(system_ == null)
          {
            Converter table; //@F0C
            if(ccsid_ == 65535)
            {
              table = new Converter(); // I realize this is deprecated, but the user never specified a ccsid or a system object. @F0C
              ccsid_ = table.getCcsid();
            }
            else
            {
              table = new Converter(ccsid_); // I realize this is deprecated, but the user never specified a system object.
            }
            tableImpl_ = table.impl;
          }
          else
          {
            if(ccsid_ == 65535)
            {
              ccsid_ = system_.getCcsid();
            }
            Converter table = new Converter(ccsid_, system_); //@F0C
            tableImpl_ = table.impl;
          }
          encoding_ = tableImpl_.getEncoding(); //@D2A @F0C
        }
        catch(UnsupportedEncodingException e)
        {
          throw new ExtendedIllegalArgumentException("ccsid (" + String.valueOf(ccsid_) + ")", ExtendedIllegalArgumentException.PARAMETER_VALUE_NOT_VALID);
        }
      } //@D2A
      if (Trace.traceOn_) //@D2A
      {
        Trace.log(Trace.CONVERSION, "AS400Text object initialized to "+encoding_+", "+ccsid_+", "+system_+", "+tableImpl_); //@D2A
      }
    }
  }


  /**
    Converts the specified Java object to AS/400 format.
    @param  javaValue  The object corresponding to the data type.  It must be an instance of String, and the converted text length must be less than or equal to the byte length of this data type.  If the provided string is not long enough to fill the return array, the remaining bytes will be padded with space bytes (EBCDIC 0x40, ASCII 0x20, or Unicode 0x0020).
    @return  The AS/400 representation of the data type.
   **/
  public byte[] toBytes(Object javaValue)
  {
    byte[] as400Value = new byte[length_];
    toBytes(javaValue, as400Value, 0);                              //$E0C   $E2C
    return as400Value;
  }


  /**
    Converts the specified Java object into AS/400 format in the specified byte array.
    @param  javaValue  The object corresponding to the data type. It must be an instance of String, and the converted text length must be less than or equal to the byte length of this data type.  If the provided string is not long enough to fill the return array, the remaining bytes will be padded with space bytes (EBCDIC 0x40, ASCII 0x20, or Unicode 0x0020).
    @param  as400Value  The array to receive the data type in AS/400 format.  There must be enough space to hold the AS/400 value.
    @return  The number of bytes in the AS/400 representation of the data type.
   **/
  public int toBytes(Object javaValue, byte[] as400Value)
  {
    return toBytes(javaValue, as400Value, 0);                       //$E0C   $E2C
  }


  /**
    Converts the specified Java object into AS/400 format in the specified byte array.
    @param  javaValue  The object corresponding to the data type.  It must be an instance of String, and the converted text length must be less than or equal to the byte length of this data type.  If the provided string is not long enough to fill the return array, the remaining bytes will be padded with space bytes (EBCDIC 0x40, ASCII 0x20, or Unicode 0x0020).
    @param  as400Value  The array to receive the data type in AS/400 format.  There must be enough space to hold the AS/400 value.
    @param  offset  The offset into the byte array for the start of the AS/400 value.  It must be greater than or equal to zero.
    @return  The number of bytes in the AS/400 representation of the data type.
   **/
  public int toBytes(Object javaValue, byte[] as400Value, int offset)
  {
    if(AS400BidiTransform.isBidiCcsid(getCcsid()))                         //$E2A
      return toBytes(javaValue, as400Value, offset, AS400BidiTransform.getStringType((char)getCcsid()));   //$E2A
    else
      return toBytes(javaValue, as400Value, offset, BidiStringType.DEFAULT);    //$E0C   $E2C
  }


  /**
    Converts the specified Java object into AS/400 format in the specified byte array.
    @param  javaValue  The object corresponding to the data type.  It must be an instance of String, and the converted text length must be less than or equal to the byte length of this data type.  If the provided string is not long enough to fill the return array, the remaining bytes will be padded with space bytes (EBCDIC 0x40, ASCII 0x20, or Unicode 0x0020).
    @param  as400Value  The array to receive the data type in AS/400 format.  There must be enough space to hold the AS/400 value.
    @param  offset  The offset into the byte array for the start of the AS/400 value.  It must be greater than or equal to zero.
    @param type The bidi string type, as defined by the CDRA (Character
                Data Representataion Architecture). See <a href="BidiStringType.html">
                BidiStringType</a> for more information and valid values.
    @return  The number of bytes in the AS/400 representation of the data type.
    @see com.ibm.as400.access.BidiStringType
   **/
  public int toBytes(Object javaValue, byte[] as400Value, int offset, int type)     //$E0A
  {
    // Check here to avoid sending bad data to Converter and ConvTable
    if(javaValue == null)
    {
      throw new NullPointerException("javaValue");
    }

    byte[] eValue = null;
    setTable(); // Make sure the table is set
      eValue = tableImpl_.stringToByteArray((String)javaValue, type);    //$E0C

    // Check that converted data fits within data type
    if(eValue.length > length_)
    {
      throw new ExtendedIllegalArgumentException("javaValue (" + javaValue.toString() + ")", ExtendedIllegalArgumentException.LENGTH_NOT_VALID);
    }
    System.arraycopy(eValue, 0, as400Value, offset, eValue.length);  // Let this line throw ArrayIndexException

//@E0A - pad with spaces
    // Note that this may sort of kludge the byte array in cases where the allocated size isn't
    // an even number for double-byte ccsids.
    // e.g. new AS400Text(11, 13488) and wrote "ABCDE" which would take up 5*2=10 bytes, so
    // we would pad the 11th byte with a double-byte space (0x00 0x20), so only the 0x00 would
    // get written. Not much we can do about it though.

    // Build padding string
    int index = offset+eValue.length;
    if(index < as400Value.length && index < offset+length_)
    {
      if(padding_ == null)
      {
        // Convert padding string using appropriate ccsid
          padding_ = tableImpl_.stringToByteArray("\u0020"); // the single-byte space
          // Either 0020 or 3000 must translate to a valid space character, no matter the codepage.
          switch(padding_.length)
          {
            case 0: // char wasn't in table
              padding_ = tableImpl_.stringToByteArray("\u3000");
              break;
            case 1: // char may be a single-byte substitution character
              if(padding_[0] == 0x3F || padding_[0] == 0x7F || padding_[0] == 0x1A)
              {
                padding_ = tableImpl_.stringToByteArray("\u3000");
              }
              break;
            case 2: // char may be a double-byte substitution character
              int s = (0xFFFF & BinaryConverter.byteArrayToShort(padding_, 0));
              if(s == 0xFEFE || s == 0xFFFD || s == 0x003F || s == 0x007F || s == 0x001A)
              {
                padding_ = tableImpl_.stringToByteArray("\u3000");
              }
              break;
            default:
            if (Trace.traceOn_)
            {
                Trace.log(Trace.WARNING, "AS400Text.toBytes(): Padding character not found for 0x0020 or 0x3000 under ccsid "+tableImpl_.getCcsid(), padding_, 0, padding_.length);
                Trace.log(Trace.WARNING, "Using 0x40 as default padding character.");
              }
              padding_ = new byte[] { 0x40};
          }
        }
      // Copy padding bytes into destination as many times as necessary
      // Could've used a StringBuffer and a System.arraycopy, but this is faster...
      int max = (offset+length_) < as400Value.length ? (offset+length_) : as400Value.length;
      for(int i=0; i<max-index; ++i)
      {
        as400Value[i+index] = padding_[i % padding_.length];
      }
    }

    // Copy padding bytes into destination as many times as necessary
    // Could've used a StringBuffer and a System.arraycopy, but this is faster...
        int max = (offset+length_) < as400Value.length ? (offset+length_) : as400Value.length;
    for(int i=0; i<max-index; ++i)
    {
      as400Value[i+index] = padding_[i % padding_.length];
    }
    if (Trace.traceOn_)
    {
      Trace.log(Trace.CONVERSION, "AS400Text.toBytes(): Converted javaValue ("+javaValue+") to:", as400Value, offset, length_);
    }

    return length_;
  }


  /**
    Converts the specified AS/400 data type to a Java object.
    @param  as400Value  The array containing the data type in AS/400 format.  The entire data type must be represented.
    @return  The String object corresponding to the data type.
   **/
  public Object toObject(byte[] as400Value)
  {
    // Check here to avoid sending bad data to Converter and ConvTable
    if(as400Value == null)
    {
      throw new NullPointerException("as400Value");
    }
    setTable(); // Make sure the table is set
      return tableImpl_.byteArrayToString(as400Value, 0, length_); //@D0A  $E0C   $E2C
    }


  /**
    Converts the specified AS/400 data type to a Java object.
    @param  as400Value  The array containing the data type in AS/400 format.  The entire data type must be represented.
    @param  offset  The offset into the byte array for the start of the AS/400 value. It must be greater than or equal to zero.
    @return  The String object corresponding to the data type.
   **/
  public Object toObject(byte[] as400Value, int offset)
  {
    // Check here to avoid sending bad data to Converter and ConvTable
    if(as400Value == null)
    {
      throw new NullPointerException("as400Value");
    }
    setTable(); // Make sure the table is set
      return tableImpl_.byteArrayToString(as400Value, offset, length_); //@D0A  $E0C  $E2C
    }

  /**
    Converts the specified AS/400 data type to a Java object.
    @param  as400Value  The array containing the data type in AS/400 format.  The entire data type must be represented.
    @param  offset  The offset into the byte array for the start of the AS/400 value. It must be greater than or equal to zero.
    @param type The bidi string type, as defined by the CDRA (Character
                Data Representataion Architecture). See <a href="BidiStringType.html">
                BidiStringType</a> for more information and valid values.
    @return  The String object corresponding to the data type.
    @see com.ibm.as400.access.BidiStringType
   **/
  public Object toObject(byte[] as400Value, int offset, int type)           //$E0A
  {
    // Check here to avoid sending bad data to Converter and ConvTable
    if(as400Value == null)
    {
      throw new NullPointerException("as400Value");
    }
    setTable(); // Make sure the table is set
      return tableImpl_.byteArrayToString(as400Value, offset, length_, type); //@D0A
    }
}
