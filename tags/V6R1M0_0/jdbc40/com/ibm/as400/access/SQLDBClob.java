///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: SQLDBClob.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2006 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

final class SQLDBClob implements SQLData
{
    private static final String copyright = "Copyright (C) 1997-2006 International Business Machines Corporation and others.";

    private int                     length_;                    // Length of string, in characters.     @E3C
    private int                     maxLength_;                 // Max length of field, in bytes.       @E3C
    private SQLConversionSettings   settings_;
    private int                     truncated_;
    private String                  value_;
    private Object savedObject_; // This is our byte[] or InputStream or whatever that we save to convert to bytes until we really need to.

    // Note: maxLength is in bytes not counting 2 for LL.
    //
    SQLDBClob(int maxLength, SQLConversionSettings settings)
    {
        length_         = 0;
        maxLength_      = maxLength;
        settings_       = settings;
        truncated_      = 0;
        value_          = "";
    }

    public Object clone()
    {
        return new SQLDBClob(maxLength_, settings_);
    }

    //---------------------------------------------------------//
    //                                                         //
    // CONVERSION TO AND FROM RAW BYTES                        //
    //                                                         //
    //---------------------------------------------------------//

    public void convertFromRawBytes(byte[] rawBytes, int offset, ConvTable ccsidConverter)
    throws SQLException
    {
        length_ = BinaryConverter.byteArrayToInt(rawBytes, offset);

        int bidiStringType = settings_.getBidiStringType();

        // if bidiStringType is not set by user, use ccsid to get value
        if(bidiStringType == -1) bidiStringType = ccsidConverter.bidiStringType_;
        
        BidiConversionProperties bidiConversionProperties = new BidiConversionProperties(bidiStringType);  //@KBA
        bidiConversionProperties.setBidiImplicitReordering(settings_.getBidiImplicitReordering());         //@KBA
        bidiConversionProperties.setBidiNumericOrderingRoundTrip(settings_.getBidiNumericOrdering());      //@KBA

        // If the field is DBCLOB, length_ contains the number
        // of characters in the string, while the converter is expecting
        // the number of bytes. Thus, we need to multiply length_ by 2.
        value_ = ccsidConverter.byteArrayToString(rawBytes, offset + 4, length_*2, bidiConversionProperties);   //@KBA changed to use bidiConversionProperties instead of bidiStringType
        savedObject_ = null;
    }

    public void convertToRawBytes(byte[] rawBytes, int offset, ConvTable ccsidConverter)
    throws SQLException
    {
        if(savedObject_ != null) doConversion();

        BinaryConverter.intToByteArray(length_, rawBytes, offset);
        try
        {
            int bidiStringType = settings_.getBidiStringType();
            if(bidiStringType == -1) bidiStringType = ccsidConverter.bidiStringType_;
            
            BidiConversionProperties bidiConversionProperties = new BidiConversionProperties(bidiStringType);  //@KBA
            bidiConversionProperties.setBidiImplicitReordering(settings_.getBidiImplicitReordering());         //@KBA
            bidiConversionProperties.setBidiNumericOrderingRoundTrip(settings_.getBidiNumericOrdering());      //@KBA

            ccsidConverter.stringToByteArray(value_, rawBytes, offset + 4, maxLength_, bidiConversionProperties);   //@KBC changed to bidiConversionProperties instead of bidiStringType
        }
        catch(Exception e)
        {
            JDError.throwSQLException(this, JDError.EXC_INTERNAL, e);
        }
    }

    //---------------------------------------------------------//
    //                                                         //
    // SET METHODS                                             //
    //                                                         //
    //---------------------------------------------------------//

    public void set(Object object, Calendar calendar, int scale)
    throws SQLException
    {
        // If it's a String we check for data truncation.
        if(object instanceof String)
        {
            String s = (String)object;
            truncated_ = (s.length() > maxLength_ ? s.length()-maxLength_ : 0);
        }
        //@PDD jdbc40 (JDUtilities.JDBCLevel_ >= 20 incorrect logic, but n/a now
        else if(!(object instanceof Clob) && //@PDC NClob extends Clob
                !(object instanceof Reader) && //@PDC jdbc40
                !(object instanceof SQLXML)) //@PDC jdbc40
        {
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        }

        savedObject_ = object;
        if(scale != -1) length_ = scale;
    }

    private void doConversion() throws SQLException
    {
        try
        {
            Object object = savedObject_;
            if(savedObject_ instanceof String)
            {
                value_ = (String)object;
            }
            else if(object instanceof Reader)
            {
                if(length_ >= 0)
                {
                    try
                    {
                        int blockSize = length_ < AS400JDBCPreparedStatement.LOB_BLOCK_SIZE ? length_ : AS400JDBCPreparedStatement.LOB_BLOCK_SIZE;
                        Reader stream = (Reader)object;
                        StringBuffer buf = new StringBuffer();
                        char[] charBuffer = new char[blockSize];
                        int totalCharsRead = 0;
                        int charsRead = stream.read(charBuffer, 0, blockSize);
                        while(charsRead > -1 && totalCharsRead < length_)
                        {
                            buf.append(charBuffer, 0, charsRead);
                            totalCharsRead += charsRead;
                            int charsRemaining = length_ - totalCharsRead;
                            if(charsRemaining < blockSize)
                            {
                                blockSize = charsRemaining;
                            }
                            charsRead = stream.read(charBuffer, 0, blockSize);
                        }
                        value_ = buf.toString();
                    }
                    catch(IOException ie)
                    {
                        JDError.throwSQLException(this, JDError.EXC_INTERNAL, ie);
                    }
                }
                else
                {
                    JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
                }
            }
            else if(JDUtilities.JDBCLevel_ >= 20 && object instanceof Clob)
            {
                Clob clob = (Clob)object;
                value_ = clob.getSubString(1, (int)clob.length());
            }
            else if(object instanceof SQLXML)  //@PDA jdbc40 
            {
                SQLXML xml = (SQLXML)object;
                value_ = xml.getString();
            }
            else
            {
                JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
            }

            // Truncate if necessary.
            int valueLength = value_.length();
            if(valueLength > maxLength_)
            {
                value_ = value_.substring(0, maxLength_);
                truncated_ = valueLength - maxLength_;
            }
            else
            {
                truncated_ = 0;
            }

            length_ = value_.length();
        }
        finally
        {
            savedObject_ = null;
        }
    }

    //---------------------------------------------------------//
    //                                                         //
    // DESCRIPTION OF SQL TYPE                                 //
    //                                                         //
    //---------------------------------------------------------//

    public int getSQLType()
    {
        return SQLData.DBCLOB;
    }

    public String getCreateParameters()
    {
        return AS400JDBCDriver.getResource("MAXLENGTH"); 
    }

    public int getDisplaySize()
    {
        return(maxLength_ / 2);
    }


    public String getJavaClassName()
    {
        return "com.ibm.as400.access.AS400JDBCClob";
    }

    public String getLiteralPrefix()
    {
        return null;
    }

    public String getLiteralSuffix()
    {
        return null;
    }

    public String getLocalName()
    {
        return "DBCLOB"; 
    }

    public int getMaximumPrecision()
    {
        return 1073741822; // the DB2 SQL reference says this should be 1073741823 but we return 1 less to allow for NOT NULL columns
    }

    public int getMaximumScale()
    {
        return 0;
    }

    public int getMinimumScale()
    {
        return 0;
    }

    public int getNativeType()
    {
        return 412;
    }

    public int getPrecision()
    {
        return maxLength_;
    }

    public int getRadix()
    {
        return 0;
    }

    public int getScale()
    {
        return 0;
    }

    public int getType()
    {
        return java.sql.Types.CLOB;
    }

    public String getTypeName()
    {
        return "DBCLOB";
    }

    public boolean isSigned()
    {
        return false;
    }

    public boolean isText()
    {
        return true;
    }

    public int getActualSize()
    {
        return value_.length();
    }

    public int getTruncated()
    {
        return truncated_;
    }

    //---------------------------------------------------------//
    //                                                         //
    // CONVERSIONS TO JAVA TYPES                               //
    //                                                         //
    //---------------------------------------------------------//

    public InputStream getAsciiStream()
    throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        try
        {
            return new ByteArrayInputStream(ConvTable.getTable(819, null).stringToByteArray(value_));
        }
        catch(UnsupportedEncodingException e)
        {
            JDError.throwSQLException(this, JDError.EXC_INTERNAL, e);
            return null;
        }
    }

    public BigDecimal getBigDecimal(int scale)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public InputStream getBinaryStream()
    throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        return new HexReaderInputStream(new StringReader(value_));
    }

    public Blob getBlob()
    throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        try
        {
            return new AS400JDBCBlob(BinaryConverter.stringToBytes(value_), maxLength_);
        }
        catch(NumberFormatException nfe)
        {
            // this DBClob contains non-hex characters
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, nfe);
            return null;
        }
    }

    public boolean getBoolean()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return false;
    }

    public byte getByte()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public byte[] getBytes()
    throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        try
        {
            return BinaryConverter.stringToBytes(value_);
        }
        catch(NumberFormatException nfe)
        {
            // this DBClob contains non-hex characters
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, nfe);
            return null;
        }
    }

    public Reader getCharacterStream()
    throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        return new StringReader(value_);
    }

    public Clob getClob()
    throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        return new AS400JDBCClob(value_, maxLength_);
    }

    public Date getDate(Calendar calendar)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public double getDouble()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public float getFloat()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public int getInt()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public long getLong()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public Object getObject()
    throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        return new AS400JDBCClob(value_, maxLength_);
    }

    public short getShort()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public String getString()
    throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        return value_;     
    }

    public Time getTime(Calendar calendar)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public Timestamp getTimestamp(Calendar calendar)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public InputStream getUnicodeStream()
    throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        try
        {
            return new ReaderInputStream(new StringReader(value_), 13488);
        }
        catch(UnsupportedEncodingException e)
        {
            JDError.throwSQLException(this, JDError.EXC_INTERNAL, e);
            return null;
        }
    }
    

    //@pda jdbc40
    public Reader getNCharacterStream() throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        return new StringReader(value_);
    }
    
    //@pda jdbc40
    public NClob getNClob() throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        return new AS400JDBCNClob(value_, maxLength_);
    }

    //@pda jdbc40
    public String getNString() throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        return value_;     
    }

    //@pda jdbc40
    public RowId getRowId() throws SQLException
    {
        /*
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        try
        {
            return new AS400JDBCRowId(BinaryConverter.stringToBytes(value_));
        }
        catch(NumberFormatException nfe)
        {
            // this Clob contains non-hex characters
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, nfe);
            return null;
        }*/
        //decided this is of no use
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    //@pda jdbc40
    public SQLXML getSQLXML() throws SQLException
    {
        if(savedObject_ != null) doConversion();
        truncated_ = 0;
        return new AS400JDBCSQLXML(value_.toCharArray());     
    }
}