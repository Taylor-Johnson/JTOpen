///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: SQLVarbinary.java
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
import java.math.BigDecimal;
import java.sql.*;
/* ifdef JDBC40
import java.sql.NClob;
import java.sql.RowId;
endif */
/* ifdef JDBC40
import java.sql.SQLXML;
endif */
import java.util.Calendar;

final class SQLVarbinary
        extends SQLDataBase {
    static final String copyright = "Copyright (C) 1997-2003 International Business Machines Corporation and others.";

    // Private data.
    private static final byte[] default_ = new byte[0];

    private int length_;
    private int maxLength_;
    private byte[] value_;

    SQLVarbinary(int maxLength, SQLConversionSettings settings) {
        super(settings);
        length_ = 0;
        maxLength_ = maxLength;
        value_ = default_;
    }

    public Object clone() {
        return new SQLVarbinary(maxLength_, settings_);
    }

    //---------------------------------------------------------//
    //                                                         //
    // CONVERSION TO AND FROM RAW BYTES                        //
    //                                                         //
    //---------------------------------------------------------//

    public void convertFromRawBytes(byte[] rawBytes, int offset, ConvTable ccsidConverter, boolean ignoreConversionErrors)
            throws SQLException {
        length_ = BinaryConverter.byteArrayToUnsignedShort(rawBytes, offset);
        AS400ByteArray typeConverter = new AS400ByteArray(length_);
        value_ = (byte[]) typeConverter.toObject(rawBytes, offset + 2);
    }

    public void convertToRawBytes(byte[] rawBytes, int offset, ConvTable ccsidConverter)
            throws SQLException {
        AS400ByteArray typeConverter = new AS400ByteArray(length_);
        BinaryConverter.unsignedShortToByteArray(length_, rawBytes, offset);
        typeConverter.toBytes(value_, rawBytes, offset + 2);
    }

    //---------------------------------------------------------//
    //                                                         //
    // SET METHODS                                             //
    //                                                         //
    //---------------------------------------------------------//

    public void set(Object object, Calendar calendar, int scale)
            throws SQLException {
        if (object instanceof String) {
            try {
                value_ = BinaryConverter.stringToBytes((String) object);
            } catch (NumberFormatException nfe) {
                // the String contains non-hex characters
                JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, nfe);
            }
        } else if (object instanceof byte[])
            value_ = (byte[]) object;

        else if (object instanceof InputStream) {
            //value_ = JDUtilities.streamToBytes((InputStream)object, scale);
            value_ = getBytesFromInputStream((InputStream) object, scale, this);
        } else if (object instanceof Reader) {
            // value_ = BinaryConverter.stringToBytes(JDUtilities.readerToString((Reader)object, scale));
            value_ = getBytesFromReader((Reader) object, scale, this);
        } else if (object instanceof Blob)
            value_ = ((Blob) object).getBytes(1, (int) ((Blob) object).length());

        else if (object instanceof Clob) {
            try {
                value_ = BinaryConverter.stringToBytes(((Clob) object).getSubString(1, (int) ((Clob) object).length()));
            } catch (NumberFormatException nfe) {
                // the Clob contains non-hex characters
                JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, nfe);
            }
        } else {
            if (JDTrace.isTraceOn()) {
                if (object == null) {
                    JDTrace.logInformation(this, "Unable to assign null object");
                } else {
                    JDTrace.logInformation(this, "Unable to assign object(" + object + ") of class(" + object.getClass().toString() + ")");
                }
            }
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        }

        // Truncate if necessary.
        int valueLength = value_.length;
        if (valueLength > maxLength_) {
            byte[] newValue = new byte[maxLength_];
            System.arraycopy(value_, 0, newValue, 0, maxLength_);
            value_ = newValue;
            truncated_ = valueLength - maxLength_;
            outOfBounds_ = false;
        } else
            truncated_ = 0;
        outOfBounds_ = false;

        length_ = value_.length;
    }

    //---------------------------------------------------------//
    //                                                         //
    // DESCRIPTION OF SQL TYPE                                 //
    //                                                         //
    //---------------------------------------------------------//

    public int getSQLType() {
        return SQLData.VARBINARY;
    }

    public String getCreateParameters() {
        return AS400JDBCDriver.getResource("MAXLENGTH", null);
    }

    public int getDisplaySize() {
        return maxLength_;
    }

    public String getJavaClassName() {
        return "[B";
    }

    public String getLiteralPrefix() {
        return "VARBINARY(X\'";     //@5WXVJX changed from x'
    }

    public String getLiteralSuffix() {
        return "\')";       //@5WXVJX changed from '
    }

    public String getLocalName() {
        return "VARBINARY";
    }

    public int getMaximumPrecision() {
        return 32739;
    }

    public int getMaximumScale() {
        return 0;
    }

    public int getMinimumScale() {
        return 0;
    }

    public int getNativeType() {
        return 908;
    }

    public int getPrecision() {
        return maxLength_;
    }

    public int getRadix() {
        return 0;
    }

    public int getScale() {
        return 0;
    }

    public int getType() {
        return java.sql.Types.VARBINARY;
    }

    public String getTypeName() {
        return "VARBINARY";
    }

    public boolean isSigned() {
        return false;
    }

    public boolean isText() {
        return true;
    }

    public int getActualSize() {
        return value_.length;
    }

    public int getTruncated() {
        return truncated_;
    }

    public boolean getOutOfBounds() {
        return outOfBounds_;
    }

    //---------------------------------------------------------//
    //                                                         //
    // CONVERSIONS TO JAVA TYPES                               //
    //                                                         //
    //---------------------------------------------------------//


    public BigDecimal getBigDecimal(int scale)
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public InputStream getBinaryStream()
            throws SQLException {
        truncated_ = 0;
        outOfBounds_ = false;
        // This is written in terms of getBytes(), since it will
        // handle truncating to the max field size if needed.
        return new ByteArrayInputStream(getBytes());
    }

    public Blob getBlob()
            throws SQLException {
        truncated_ = 0;
        outOfBounds_ = false;
        // This is written in terms of getBytes(), since it will
        // handle truncating to the max field size if needed.
        byte[] bytes = getBytes();
        return new AS400JDBCBlob(bytes, bytes.length);
    }

    public boolean getBoolean()
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return false;
    }

    public byte getByte()
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return -1;
    }

    public byte[] getBytes() {
        truncated_ = 0;
        outOfBounds_ = false;
        // Truncate to the max field size if needed.
        // Do not signal a DataTruncation per the spec.
        int maxFieldSize = settings_.getMaxFieldSize();
        if ((value_.length > maxFieldSize) && (maxFieldSize > 0)) {
            byte[] truncatedValue = new byte[maxFieldSize];
            System.arraycopy(value_, 0, truncatedValue, 0, maxFieldSize);
            return truncatedValue;
        } else {
            return value_;
        }
    }

    public Reader getCharacterStream()
            throws SQLException {
        truncated_ = 0;
        outOfBounds_ = false;
        // This is written in terms of getBytes(), since it will
        // handle truncating to the max field size if needed.
        return new StringReader(BinaryConverter.bytesToHexString(getBytes()));
    }

    public Clob getClob()
            throws SQLException {
        truncated_ = 0;
        outOfBounds_ = false;
        // This is written in terms of getString(), since it will
        // handle truncating to the max field size if needed.
        String string = BinaryConverter.bytesToHexString(getBytes());
        return new AS400JDBCClob(string, string.length());
    }

    public Date getDate(Calendar calendar)
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public double getDouble()
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return -1;
    }

    public float getFloat()
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return -1;
    }

    public int getInt()
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return -1;
    }

    public long getLong()
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return -1;
    }

    public Object getObject()
            throws SQLException {
        truncated_ = 0;
        outOfBounds_ = false;
        // This is written in terms of getBytes(), since it will
        // handle truncating to the max field size if needed.
        return getBytes();
    }

    public short getShort()
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return -1;
    }

    public String getString()
            throws SQLException {
        truncated_ = 0;
        outOfBounds_ = false;
        // This is written in terms of getBytes(), since it will
        // handle truncating to the max field size if needed.
        return BinaryConverter.bytesToHexString(getBytes());
    }

    public Time getTime(Calendar calendar)
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public Timestamp getTimestamp(Calendar calendar)
            throws SQLException {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }


    //@PDA jdbc40
    public String getNString() throws SQLException {
        truncated_ = 0;
        outOfBounds_ = false;
        // This is written in terms of getBytes(), since it will
        // handle truncating to the max field size if needed.
        return BinaryConverter.bytesToHexString(getBytes());
    }


    //@PDA jdbc40
    public RowId getRowId() throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    //@PDA jdbc40
    public SQLXML getSQLXML() throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }


    public void saveValue() {
        savedValue_ = value_;
    }
}

