///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: SQLData.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;



/**
The SQLData interface represents native SQL data.  A specific
implementation of this interface will implement a specific
type of SQL data.

<p>The implementation's constructor should not necessarily
initialize the data.  That is done via the set() methods.
**/
interface SQLData
extends Cloneable
{



/**
Returns a clone of the SQLData object.  Use this sparingly
so that we minimize the number of copies.

@return     The clone.
**/
    public abstract Object clone ();



//---------------------------------------------------------//
//                                                         //
// CONVERSION TO AND FROM RAW BYTES                        //
//                                                         //
//---------------------------------------------------------//



/**
Loads the contents of the data from raw bytes, as returned
in a reply from the server.

@param  rawBytes    raw bytes from the server.
@param  offset      offset.
@param  converter   the converter.

@exception  SQLException    If the raw bytes are not in
                            the expected format.
**/
    public abstract void convertFromRawBytes (byte[] rawBytes,
                                              int offset,
                                              ConverterImplRemote converter)
        throws SQLException;



/**
Converts the contents of the data in raw bytes, as needed
in a request to the server.

@param  rawBytes         the raw bytes for the server.
@param  offset           the offset into the byte array.
@param  ccsidConverter   the converter.
**/
    public abstract void convertToRawBytes (byte[] rawBytes,
                                            int offset,
                                            ConverterImplRemote ccsidConverter)
        throws SQLException;



//---------------------------------------------------------//
//                                                         //
// SET METHODS                                             //
//                                                         //
// The set methods initialize the data in a uniform way    //
// across all types.  If a specific initialization is      //
// needed based on a Java type, then add other flavors     //
// of set() methods.                                       //
//                                                         //
//---------------------------------------------------------//



/**
Sets the contents of the data based on a Java object.
This performs all conversions described in Table 6
of the JDBC specification.

@param  object      a Java object.
@param  calendar    The calendar.
@param  scale       The scale.

@exception  SQLException    If the Java object is not an
                            appropriate type.
**/
    public abstract void set (Object object, 
                              Calendar calendar,
                              int scale)
        throws SQLException;



//---------------------------------------------------------//
//                                                         //
// DESCRIPTION OF SQL TYPE                                 //
//                                                         //
// These methods describe information about the actual     //
// type of data.                                           //
//                                                         //
/*---------------------------------------------------------*/



/**
Returns the parameters used in creating the
type.

@return     the parameters, separated by commas,
            or null if none.
**/
    public abstract String getCreateParameters ();



/**
Returns the display size.  This is defined in Appendix
D of the ODBC 2.0 Programmer's Reference.

@return                 the display size (in characters).

@exception  SQLException    If the index is invalid
                            or an error occurs.
**/
    public abstract int getDisplaySize ();



/**
Returns the prefix used to quote a literal.

@return     the prefix, or null if none.
**/
    public abstract String getLiteralPrefix ();



/**
Returns the suffix used to quote a literal.

@return     the suffix, or null if none.
**/
    public abstract String getLiteralSuffix ();



/**
Returns the localized version of the name of the
data type.

@return     the name, or null.
**/
	public abstract String getLocalName ();



/**
Returns the maximum precision of the type. This is
defined in Appendix D of the ODBC 2.0 Programmer's
Reference.

@return     the maximum precision.
**/
    public abstract int getMaximumPrecision ();



/**
Returns the maximum scale of the type.  This is
defined in Appendix D of the ODBC 2.0 Programmer's
Reference.

@return     the maximum scale.
**/
    public abstract int getMaximumScale ();



/**
Returns the minimum scale of the type.  This is
defined in Appendix D of the ODBC 2.0 Programmer's
Reference.

@return     the minimum scale.
**/
    public abstract int getMinimumScale ();



/**
Returns the native AS/400 identifier for the type.

@return     the native type.
**/
    public abstract int getNativeType ();



/**
Returns the precision of the type. This is
defined in Appendix D of the ODBC 2.0 Programmer's
Reference.

@return     the precision.
**/
    public abstract int getPrecision ();



/**
Returns the radix for the type.

@return     the radix.
**/
    public abstract int getRadix ();



/**
Returns the scale of the type. This is
defined in Appendix D of the ODBC 2.0 Programmer's
Reference.

@return     the scale.
**/
    public abstract int getScale ();



/**
Returns the type constant associated with the type.

@return     SQL type code defined in java.sql.Types.
**/
	public abstract int getType ();



/**
Returns the name of the data type.

@return     the name.
**/
	public abstract String getTypeName ();



// @E1D /**
// @E1D Indicates whether the type is graphic data.  Note
// @E1D that this should only return true if isText()
// @E1D also returns true.
// @E1D 
// @E1D @return     true or false
// @E1D **/
// @E1D     public abstract boolean isGraphic ();



/**
Indicates whether the type is signed.

@return     true or false
**/
    public abstract boolean isSigned ();



/**
Indicates whether the type is text.  This also
indicates that the associated data needs to be
converted.

@return     true or false
**/
    public abstract boolean isText ();



//---------------------------------------------------------//
//                                                         //
// CONVERSIONS TO JAVA TYPES                               //
//                                                         //
// These methods convert the data to a specific Java       //
// type.  These conversions should be provided per         //
// section 7, table 1 ("Use of ResultSet.getXxx methods    //
// to retrieve common SQL data types") of the JDBC 1.10    //
// specification.  If a conversion is not required or is   //
// not possible given the data, then the method should     //
// throw an exception.                                     //
//                                                         //
/*---------------------------------------------------------*/



/**
Returns the actual size of this piece of data in bytes.

@return the actual size of this piece of data in bytes.
**/
    public abstract int getActualSize ();



/**
Returns the number of bytes truncated by the last conversion
of this piece of data.

@return the number of bytes truncated by the last conversion
**/
    public abstract int getTruncated ();



/**
Converts the data to a stream of ASCII characters.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract InputStream	toAsciiStream ()
	    throws SQLException;



/**
Converts the data to a Java BigDecimal object.

@param      scale   scale, or -1 to use full scale.
@return             the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract BigDecimal toBigDecimal (int scale)
	    throws SQLException;



/**
Converts the data to a stream of uninterpreted bytes.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract InputStream	toBinaryStream ()
	    throws SQLException;



/**
Converts the data to a java.sql.Blob object.

@return             the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract Blob toBlob ()
	    throws SQLException;



/**
Converts the data to a Java boolean.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract boolean toBoolean ()
	    throws SQLException;



/**
Converts the data to a Java byte.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract byte toByte ()
	    throws SQLException;



/**
Converts the data to a Java byte array containing
uninterpreted bytes.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract byte[] toBytes ()
	    throws SQLException;



/**
Converts the data to a java.io.Reader object.

@return             the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract Reader toCharacterStream ()
	    throws SQLException;



/**
Converts the data to a java.sql.Clob object.

@return             the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract Clob toClob ()
	    throws SQLException;



/**
Converts the data to a java.sql.Date object.

@param  calendar    The calendar.
@return             the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract Date toDate (Calendar calendar)
	    throws SQLException;



/**
Converts the data to a Java double.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
   	public abstract double toDouble ()
       	throws SQLException;



/**
Converts the data to a Java float.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract float toFloat ()
	    throws SQLException;



/**
Converts the data to a Java int.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract int	toInt ()
    	throws SQLException;



/**
Converts the data to a Java long.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract long toLong ()
	    throws SQLException;



/**
Converts the data to a Java object.  The actual type
of the Java object is dictated per section 8,
table 2 ("Standard mapping from SQL types to Java types")
of the JDBC 1.10 specification

@return             the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract Object toObject ();



/**
Converts the data to a Java short.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract short toShort ()
    	throws SQLException;



/**
Converts the data to a Java String object.  This
conversion must be provided by the implementation.

@return             the result of the conversion.
**/
	public abstract String toString ();



/**
Converts the data to a java.sql.Time object.

@param  calendar    The calendar.
@return             the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract Time toTime (Calendar calendar)
	    throws SQLException;



/**
Converts the data to a java.sql.Timestamp object.

@param  calendar    The calendar.
@return             the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract Timestamp toTimestamp (Calendar calendar)
    	throws SQLException;



/**
Converts the data to a stream of Unicdoe characters.

@return     the result of the conversion.

@exception  SQLException    If the conversion is not
                            required or not possible.
**/
	public abstract InputStream	toUnicodeStream ()
	    throws SQLException;

}