///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: SQLVargraphic.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2003 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

final class SQLVargraphic
implements SQLData
{
    private static final String copyright = "Copyright (C) 1997-2003 International Business Machines Corporation and others.";

    // Private data.
    private SQLConversionSettings   settings_;
    private int                     length_;
    private int                     maxLength_;
    private int                     truncated_;
    private String                  value_;

    // Note: maxLength is in bytes not counting 2 for LL.
    //
    SQLVargraphic(int maxLength, SQLConversionSettings settings)
    {
        settings_       = settings;
        length_         = 0;
        maxLength_      = maxLength;
        truncated_      = 0;
        value_          = "";
    }

    public Object clone()
    {
        return new SQLVargraphic(maxLength_, settings_);
    }

    // Added method trim() to trim the string.
    public void trim()
    {
        value_ = value_.trim();
    }

    //---------------------------------------------------------//
    //                                                         //
    // CONVERSION TO AND FROM RAW BYTES                        //
    //                                                         //
    //---------------------------------------------------------//

    public void convertFromRawBytes(byte[] rawBytes, int offset, ConvTable ccsidConverter)
    throws SQLException
    {
        length_ = BinaryConverter.byteArrayToUnsignedShort(rawBytes, offset);

        int bidiStringType = settings_.getBidiStringType();
        // if bidiStringType is not set by user, use ccsid to get value
        if(bidiStringType == -1)
            bidiStringType = ccsidConverter.bidiStringType_;

        // If the field is VARGRAPHIC, length_ contains the number
        // of characters in the string, while the converter is expecting
        // the number of bytes. Thus, we need to multiply length_ by 2.
        value_ = ccsidConverter.byteArrayToString(rawBytes, offset+2, length_*2, bidiStringType);
    }

    public void convertToRawBytes(byte[] rawBytes, int offset, ConvTable ccsidConverter)
    throws SQLException
    {
        try
        {
            int bidiStringType = settings_.getBidiStringType();
            // if bidiStringType is not set by user, use ccsid to get value
            if(bidiStringType == -1)
                bidiStringType = ccsidConverter.bidiStringType_;

            // The length in the first 2 bytes is actually the length in characters.
            byte[] temp = ccsidConverter.stringToByteArray(value_, bidiStringType);
            BinaryConverter.unsignedShortToByteArray(temp.length/2, rawBytes, offset);

            if(temp.length > maxLength_)
            {
                maxLength_ = temp.length;
                JDError.throwSQLException(this, JDError.EXC_INTERNAL);
            }
            System.arraycopy(temp, 0, rawBytes, offset+2, temp.length);

            // The buffer we are filling with data is big enough to hold the entire field.
            // For varchar fields the actual data is often smaller than the field width.
            // That means whatever is in the buffer from the previous send is sent to the
            // server.  The data stream includes actual data length so the old bytes are not 
            // written to the database, but the junk left over may decrease the effectiveness 
            // of compression.  The following code will write hex 0s to the buffer when
            // actual length is less that field length.  Note the 0s are written only if 
            // the field length is pretty big.  The data stream code (DBBaseRequestDS)
            // does not compress anything smaller than 1K.
            if((maxLength_ > 256) && (maxLength_ - temp.length > 16))
            {
                int stopHere = offset + 2 + maxLength_;
                for(int i=offset + 2 + temp.length; i<stopHere; i++)
                    rawBytes[i] = 0x00;
            }
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
        String value = null;

        if(object instanceof String)
            value = (String)object;

        else if(object instanceof Number)
            value = object.toString();

        else if(object instanceof Boolean)
            value = object.toString();

        else if(object instanceof Time)
            value = SQLTime.timeToString((Time)object, settings_, calendar);

        else if(object instanceof Timestamp)
            value = SQLTimestamp.timestampToString((Timestamp)object, calendar);

        else if(object instanceof java.util.Date)
            value = SQLDate.dateToString((java.util.Date)object, settings_, calendar);

        else if(JDUtilities.JDBCLevel_ >= 20 && object instanceof Clob)
        {
            Clob clob = (Clob)object;
            value = clob.getSubString(1, (int)clob.length());
        }

        if(value == null)
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);

        value_ = value;

        // Truncate if necessary.
        int valueLength = value_.length();

        int truncLimit = maxLength_ / 2;

        if(valueLength > truncLimit)
        {
            value_ = value_.substring(0, truncLimit);
            truncated_ = valueLength - truncLimit;
        }
        else
            truncated_ = 0;

        length_ = value_.length();
    }

    //---------------------------------------------------------//
    //                                                         //
    // DESCRIPTION OF SQL TYPE                                 //
    //                                                         //
    //---------------------------------------------------------//

    public int getSQLType()
    {
        return SQLData.VARGRAPHIC;
    }

    public String getCreateParameters()
    {
        return AS400JDBCDriver.getResource("MAXLENGTH");
    }

    public int getDisplaySize()
    {
        return maxLength_ / 2;
    }

    // JDBC 3.0
    public String getJavaClassName()
    {
        return "java.lang.String";   
    }

    public String getLiteralPrefix()
    {
        return "\'";
    }

    public String getLiteralSuffix()
    {
        return "\'";
    }

    public String getLocalName()
    {
        return "VARGRAPHIC";
    }

    public int getMaximumPrecision()
    {
        return 16369;
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
        return 464;
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
        return java.sql.Types.VARCHAR;
    }

    public String getTypeName()
    {
        return "VARGRAPHIC";
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
        truncated_ = 0;
        try
        {
            return new ByteArrayInputStream(ConvTable.getTable(819, null).stringToByteArray(getString()));
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
        truncated_ = 0;
        try
        {
            BigDecimal bigDecimal = new BigDecimal(SQLDataFactory.convertScientificNotation(getString())); // @F3C
            if(scale >= 0)
            {
                if(scale >= bigDecimal.scale())
                {
                    truncated_ = 0;
                    return bigDecimal.setScale(scale);
                }
                else
                {
                    truncated_ = bigDecimal.scale() - scale;
                    return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
                }
            }
            else
                return bigDecimal;
        }
        catch(NumberFormatException e)
        {
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, e);
            return null;
        }
    }

    public InputStream getBinaryStream()
    throws SQLException
    {
        truncated_ = 0;
        return new HexReaderInputStream(new StringReader(getString()));
    }

    public Blob getBlob()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            byte[] bytes = BinaryConverter.stringToBytes(getString());
            return new AS400JDBCBlob(bytes, bytes.length);
        }
        catch(NumberFormatException nfe)
        {
            // this field contains non-hex characters
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, nfe);
            return null;
        }
    }

    public boolean getBoolean()
    throws SQLException
    {
        truncated_ = 0;

        // If value equals "true" or "false", then return the
        // corresponding boolean, otherwise an empty string is
        // false, a non-empty string is true.
        String trimmedValue = getString().trim();        
        return((trimmedValue.length() > 0) 
               && (! trimmedValue.equalsIgnoreCase("false"))
               && (! trimmedValue.equals("0")));
    }

    public byte getByte()
    throws SQLException
    {
        truncated_ = 0;

        try
        {
            return(new Double(getString().trim())).byteValue();
        }
        catch(NumberFormatException e)
        {
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, e);
            return -1;
        }
    }

    public byte[] getBytes()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            return BinaryConverter.stringToBytes(getString());
        }
        catch(NumberFormatException nfe)
        {
            // this field contains non-hex characters
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, nfe);
            return null;
        }
    }

    public Reader getCharacterStream()
    throws SQLException
    {
        truncated_ = 0;
        // This is written in terms of getString(), since it will
        // handle truncating to the max field size if needed.
        return new StringReader(getString());
    }

    public Clob getClob()
    throws SQLException
    {
        truncated_ = 0;
        // This is written in terms of getString(), since it will
        // handle truncating to the max field size if needed.
        String string = getString();
        return new AS400JDBCClob(string, string.length());
    }

    public Date getDate(Calendar calendar)
    throws SQLException
    {
        truncated_ = 0;
        return SQLDate.stringToDate(getString(), settings_, calendar);
    }

    public double getDouble()
    throws SQLException
    {
        truncated_ = 0;

        try
        {
            return(new Double(getString().trim())).doubleValue();
        }
        catch(NumberFormatException e)
        {
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, e);
            return -1;
        }
    }

    public float getFloat()
    throws SQLException
    {
        truncated_ = 0;

        try
        {
            return(new Double(getString().trim())).floatValue();
        }
        catch(NumberFormatException e)
        {
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, e);
            return -1;
        }
    }

    public int getInt()
    throws SQLException
    {
        truncated_ = 0;

        try
        {
            return(new Double(getString().trim())).intValue();
        }
        catch(NumberFormatException e)
        {
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, e);
            return -1;
        }
    }

    public long getLong()
    throws SQLException
    {
        truncated_ = 0;

        try
        {
            return(new Double(getString().trim())).longValue();
        }
        catch(NumberFormatException e)
        {
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, e);
            return -1;
        }
    }

    public Object getObject()
    throws SQLException
    {
        truncated_ = 0;
        // This is written in terms of getString(), since it will
        // handle truncating to the max field size if needed.
        return getString();
    }

    public short getShort()
    throws SQLException
    {
        truncated_ = 0;

        try
        {
            return(new Double(getString().trim())).shortValue();
        }
        catch(NumberFormatException e)
        {
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, e);
            return -1;
        }
    }

    public String getString()
    throws SQLException
    {
        truncated_ = 0;
        // Truncate to the max field size if needed.
        // Do not signal a DataTruncation per the spec. @B1A
        int maxFieldSize = settings_.getMaxFieldSize();
        if((value_.length() > maxFieldSize) && (maxFieldSize > 0))
        {
            // @B1D truncated_ = value_.length() - maxFieldSize;
            return value_.substring(0, maxFieldSize);
        }
        else
        {
            // @B1D truncated_ = 0;
            return value_;
        }
    }

    public Time getTime(Calendar calendar)
    throws SQLException
    {
        truncated_ = 0;
        return SQLTime.stringToTime(getString(), settings_, calendar);
    }

    public Timestamp getTimestamp(Calendar calendar)
    throws SQLException
    {
        truncated_ = 0;
        return SQLTimestamp.stringToTimestamp(getString(), calendar);
    }

    public InputStream getUnicodeStream()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            return new ReaderInputStream(new StringReader(getString()), 13488);
        }
        catch(UnsupportedEncodingException e)
        {
            JDError.throwSQLException(this, JDError.EXC_INTERNAL, e);
            return null;
        }
    }
}

