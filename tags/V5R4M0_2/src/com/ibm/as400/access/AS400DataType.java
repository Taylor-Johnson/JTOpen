///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  AS400DataType.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2004 International Business Machines Corporation and
// others.  All rights reserved.
//
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.Serializable;

/**
 *  The AS400DataType interface provides an interface for conversions between Java objects and byte arrays representing server data types.
 **/
public interface AS400DataType extends Cloneable, Serializable
{



    static final long serialVersionUID = 4L;


    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400Array AS400Array} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_ARRAY = 0;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400Bin2 AS400Bin2} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_BIN2 = 1;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400Bin4 AS400Bin4} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_BIN4 = 2;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400Bin8 AS400Bin8} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_BIN8 = 3;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400ByteArray AS400ByteArray} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_BYTE_ARRAY = 4;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400Float4 AS400Float4} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_FLOAT4 = 5;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400Float8 AS400Float8} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_FLOAT8 = 6;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400PackedDecimal AS400PackedDecimal} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_PACKED = 7;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400Structure AS400Structure} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_STRUCTURE = 8;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400Text AS400Text} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_TEXT = 9;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400UnsignedBin2 AS400UnsignedBin2} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_UBIN2 = 10;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400UnsignedBin4 AS400UnsignedBin4} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_UBIN4 = 11;
    /**
     * Constant representing the instance of this class is an {@link com.ibm.as400.access.AS400ZonedDecimal AS400ZonedDecimal} object.
     * @see #getInstanceType
    **/
    public static final int TYPE_ZONED = 12;

    /**
     * Creates a new AS400DataType object that is identical to the current instance.
     * @return The new object.
     **/
    public abstract Object clone();  // Implementers must provide a clone() method that is public and does not throw CloneNotSupported Exception

    /**
     * Returns the byte length of the data type.
     * @return The number of bytes in the server representation of the data type.
     **/
    public abstract int getByteLength();

    /**
     * Returns a Java object representing the default value of the data type.
     * @return The Object of the corresponding data type.
     **/
    public abstract Object getDefaultValue();

    
    /**
     * Returns an integer constant representing the type of class that implements
     * this interface. This is typically faster than using the instanceof operator, and may prove useful
     * where code needs a primitive type for ease of calculation.
     * Possible values for standard com.ibm.as400.access classes that implement this
     * interface are provided as constants in this class. Note that any implementing class provided
     * by a third party is not guaranteed to correctly return one of the pre-defined constants.
     * @return The type of object implementing this interface.
    **/
    public abstract int getInstanceType();


    /**
     * Converts the specified Java object to server format.
     * @param javaValue The object corresponding to the data type.  It must be an instance of the correct type.
     * @return The server representation of the data type.
     **/
    public abstract byte[] toBytes(Object javaValue);

    /**
     * Converts the specified Java object into server format in the specified byte array.
     * @param javaValue The object corresponding to the data type.  It must be an instance of the correct type.
     * @param as400Value The array to receive the data type in server format.  There must be enough space to hold the server value.
     * @return The number of bytes in the server representation of the data type.
     **/
    public abstract int toBytes(Object javaValue, byte[] as400Value);

    /**
     * Converts the specified Java object into server format in the specified byte array.
     * @param javaValue The object corresponding to the data type. It must be an instance of the correct type.
     * @param as400Value The array to receive the data type in server format.  There must be enough space to hold the server value.
     * @param offset The offset into the byte array for the start of the server value.  It must be greater than or equal to zero.
     * @return The number of bytes in the server representation of the data type.
     **/
    public abstract int toBytes(Object javaValue, byte[] as400Value, int offset);

    /**
     * Converts the specified server data type to a Java object.
     * @param as400Value The array containing the data type in server format.  The entire data type must be represented.
     * @return The object corresponding to the data type.
     **/
    public abstract Object toObject(byte[] as400Value);

    /**
     * Converts the specified server data type to a Java object.
     * @param as400Value The array containing the data type in server format.  The entire data type must be represented.
     * @param offset The offset into the byte array for the start of the server value.  It must be greater than or equal to zero.
     * @return The object corresponding to the data type.
     **/
    public abstract Object toObject(byte[] as400Value, int offset);
}