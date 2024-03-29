///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: AS400JDBCClobOutputStream.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2003 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;


import java.sql.SQLException;

final class AS400JDBCClobOutputStream extends AS400JDBCOutputStream {
    static final String copyright2 = "Copyright (C) 1997-2003 International Business Machines Corporation and others.";

    private AS400JDBCClob clob_;
    private ConvTable converter_;

    // Currently, the only thing that uses this is AS400JDBCClob.toAsciiStream(),
    // so the converter is always 819.
    AS400JDBCClobOutputStream(AS400JDBCClob clob, long position, ConvTable converter) {
        super(position);
        clob_ = clob;
        converter_ = converter;
    }

    int doWrite(long position, byte data) throws SQLException {
        return doWrite(position, new byte[]{data}, 0, 1);
    }

    int doWrite(long position, byte[] data, int offset, int length) throws SQLException {
        String s = converter_.byteArrayToString(data, offset, length);
        clob_.setString(position, s);
        return length;
    }
}



