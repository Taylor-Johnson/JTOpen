///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: SQLResultSetRowData.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.util.servlet;

import com.ibm.as400.access.ExtendedIllegalArgumentException;
import com.ibm.as400.access.ExtendedIllegalStateException;
import com.ibm.as400.access.Trace;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
  The SQLResultSetRowData class represents an SQL ResultSet as a list of data.
  The ResultSet object is generated by an executed SQL statement.

  <P>An SQLResultSetRowData object maintains a position in the list that points to its
  current row of data.  The initial position in the list is set before the
  first row.  The <i>next</i> method moves to the next row in the list.

  <P>The <i>getString</i> method is used to retrieve the column value for
  the current row indexed by the column number.  Columns are numbered
  starting from 0.

  <P>The number, types, and properties of the list's columns are provided
  by the <A href="SQLResultSetMetaData.html">SQLResultSetMetaData</A>
  object returned by the <i>getMetaData</i> method.

  <P>The following example creates an SQLResultSetRowData object and initializes it to an
  SQL ResultSet.
  <BLOCKQUOTE><PRE>
  <P>       // Register and get a connection to the database.
  DriverManager.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
  Connection connection = DriverManager.getConnection("jdbc:as400://mySystem");
  <P>       // Execute an SQL statement and get the result set.
  Statement statement = connection.createStatement();
  statement.execute("select * from qiws.qcustcdt");
  ResultSet resultSet = statement.getResultSet();
  <P>       // Create the SQLResultSetRowData object and initialize to the result set.
  SQLResultSetRowData rowData = new SQLResultSetRowData(resultSet);
  </PRE></BLOCKQUOTE>
**/
public class SQLResultSetRowData extends RowData
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

  private static final String CHK_POSITION = "Attempting to check the list position";
  private static final String SET_POSITION = "Attempting to set the list position";

  /**
   * The java.sql.ResultSet object associated with this RowData.
  **/
  protected transient ResultSet resultSet_;        // The SQL result set. @B6C

  private SQLResultSetMetaData metadata_;        // The metadata.
  //@B6D private int numColumns_;                  // The number of columns.

  /**
  *  Constructs a default SQLResultSetRowData object.
  **/
  public SQLResultSetRowData()
  {
    super();
  }

  /**
  *  Constructs a SQLResultSetRowData object with the specified <i>resultSet</i>.
  *  The remaining rows are read from the ResultSet starting at the current
  *  cursor position.  The ResultSet is left in an open state with the cursor
  *  positioned after the last row.  The ResultSet can be closed using the
  *  close method.
  *  @param resultSet The SQL result set.  An empty result set is invalid.
  *  @exception RowDataException If the cursor state of the result set is invalid or a rowdata error occurs.
  *  @see #close
  **/
  public SQLResultSetRowData(ResultSet resultSet) throws RowDataException
  {
    this();

    try
    {
      setResultSet(resultSet);
    }
    catch (PropertyVetoException e)
    { /* Will never happen. */
    }
  }

  /**
  *  Closes the SQL result set.
  *  @exception RowDataException If a row data error occurs.
  **/
  public void close() throws RowDataException
  {
    // Validate the result set.
    if (resultSet_ == null)
    {
      Trace.log(Trace.ERROR, "Attempting to close the result set before setting the result set.");
      throw new ExtendedIllegalStateException("resultSet", ExtendedIllegalStateException.PROPERTY_NOT_SET);
    }

    try
    {
      resultSet_.close();
    }
    catch (SQLException e)
    {
      Trace.log(Trace.INFORMATION, "Rethrowing SQLException.");
      throw new RowDataException(e);
    }
  }

  /**
  *  Returns the metadata.
  *  @return The metadata.
  *  @exception RowDataException If a row data error occurs.
  **/
  public RowMetaData getMetaData() throws RowDataException
  {
    // Validate the result set.
//@B4D      validateResultSet("Attempting to get the metadata");

    try
    {
        if (resultSet_ != null & metadata_ == null)                                                 //@D5C
        metadata_ = new SQLResultSetMetaData(resultSet_.getMetaData());

      return metadata_;
    }
    catch (SQLException e)
    {
      throw new RowDataException(e);
    }
  }

  /**
  *  Returns the SQL result set.
  *  Null is returned if the object was serialized.  The result set data is cached
  *  during serialization.
  *  @return The SQL result set.
  **/
  public ResultSet getResultSet()
  {
    return resultSet_;
  }


  /**
  *  Deserializes the object and initializes the transient data.
  **/
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();

    resultSet_ = null;
  }

    
  /**
  *  Sets the SQL result set.  The remaining rows are read from
  *  the ResultSet starting at the current cursor position.  The
  *  ResultSet is left in an open state with the cursor positioned
  *  after the last row.  The ResultSet can be closed using the
  *  close method.
  *  If a result set already exists, then setting the result set
  *  will remove all rows from the list before reading in the
  *  new data.
  *  @param resultSet The SQL result set.  An empty result set is invalid.
  *  @exception RowDataException If the cursor state of the result set is invalid or a rowdata error occurs.
  *  @exception PropertyVetoException If a change is vetoed.
  *  @see #close
  **/
  public void setResultSet(ResultSet resultSet) throws RowDataException, PropertyVetoException
  {
    if (resultSet == null)
      throw new NullPointerException("resultSet");

    ResultSet old = resultSet_;
    if (vetos_ != null) vetos_.fireVetoableChange("resultSet", old, resultSet); //@CRS

    resultSet_ = resultSet;

    // Remove all existing rows and rows properties.
    rows_ = new Vector();
    rowProperties_ = new Vector();

    try
    {
      // Initialize the properties vector for each row.
      final int numColumns = resultSet.getMetaData().getColumnCount(); //@B6C

      Object[] row;

      // Process the result set.
      while (resultSet.next())
      {

        row = new Object[numColumns]; //@B6C
        for (int column=0; column < numColumns; column++) //@B6C
          row[column] = resultSet.getString(column+1); //@B5C


        // add the row and properties.
        rows_.addElement(row);
        rowProperties_.addElement(new Vector[numColumns]); //@B6C
      }
      // Verify that a row was added to the list.
      if (changes_ != null) changes_.firePropertyChange("resultSet", old, resultSet); //@CRS
      // Set the starting position in the list.
      beforeFirst();
    }
    catch (SQLException e)
    {
      Trace.log(Trace.INFORMATION, "Rethrowing SQLException.");
      throw new RowDataException(e);
    }
  }
}
