///////////////////////////////////////////////////////////////////////////////
//                                                                             
// AS/400 Toolbox for Java - OSS version                                       
//                                                                             
// Filename: PxTable.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
The PxTable class stores all proxy objects in a single
context.  (The context may be a particular connection,
conversation, or session.)

<p>This class takes care of assigning proxy ids.  Px
ids are numbered from 1000 to Long.MAX_VALUE and never
reused.  Since this set only applies to a single connection,
this should not be a problem.
**/
class PxTable
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";




    // Private data.
    private static final boolean DEBUG_                 = false;
    private static int           debugTableCounter_     = 0;
    private static int           debugObjectCounter_    = 0;



    private Hashtable   idToObject_     = new Hashtable ();
    private long        currentId_      = 1000;
    private Hashtable   objectToId_     = new Hashtable ();



    public PxTable()
    {
        if (DEBUG_)
            System.out.println("+Px table count = " + (++debugTableCounter_) + ".");        
    }



/**
Adds an object to the proxy table, while assigning
a unique proxy id.

@param object   The object.
@return         The proxy id.
**/
    public long add (Object object)
    {
        if (DEBUG_) {
            if (objectToId_.containsKey (object))
                System.out.println ("Px table: Object " + object + " added multiple times.");
        }

        long proxyId = currentId_++;
        Object key = toKey (proxyId);
        idToObject_.put (key, object);
        objectToId_.put (object, key);

        if (DEBUG_) {
            System.out.println("Px table: Added " + object + " (" + object.getClass() + ") as #" + proxyId + ".");            
            System.out.println("+Px object count = " + (++debugObjectCounter_) + ".");        
        }

        return proxyId;
    }



/**
Adds an object to the proxy table when the proxy
id is already assigned.

@param proxyId  The proxy id.
@param object   The object.
**/
    public void add (long proxyId, Object object)
    {
        Object key = toKey (proxyId);

        if (DEBUG_) {
            if (objectToId_.containsKey (object))
                System.out.println ("Px table: Object " + object + " added multiple times.");
            if (idToObject_.containsKey (key))
                System.out.println ("Px table: Px id " + proxyId + " added multiple times.");
        }

        idToObject_.put (key, object);
        objectToId_.put (object, key);
        
        if (DEBUG_) {
            System.out.println("Px table: Added " + object + " (" + object.getClass() + ") as #" + proxyId + ".");      
            System.out.println("+Px object count = " + (++debugObjectCounter_) + ".");        
        }
    }



    protected void finalize() throws Throwable
    {
        if (DEBUG_)
            System.out.println("-Px table count = " + (--debugTableCounter_) + ".");
        super.finalize ();
    }



/**
Returns the proxy id for an object.

@param object   The object.
@return         The proxy id, or -1 if not found.
**/
    public long get (Object object)
    {
        if (objectToId_.containsKey (object))
            return ((Long) objectToId_.get (object)).longValue ();
        else
            return -1;
    }



/**
Returns the object associated with a proxy id.

@param proxyId  The proxy id.
@return         The object.
**/
    public Object get (long proxyId)
    {
        Object key = toKey (proxyId);
        if (idToObject_.containsKey (key))
            return idToObject_.get (key);
        else
            return null;
    }



/**
Returns the key associated with the proxy id.
This is for use in the internal hashtables.

@param proxyId  The proxy id.
@return         The key.
**/
    private static Object toKey (long proxyId)
    {
        return new Long (proxyId);
    }



/**
Removes the object associated with the proxy id
from the proxy table.

@param proxyId  The proxy id.
**/
    public void remove (long proxyId)
    {
        Object key = toKey (proxyId);
        if (idToObject_.containsKey (key)) {
            Object object = idToObject_.get (key);
            idToObject_.remove (key);
            objectToId_.remove (object);

            if (DEBUG_) {
                System.out.println("Px table: Removed " + object + " (" + object.getClass() + ") as #" + proxyId + ".");       
                System.out.println("-Px object count = " + (--debugObjectCounter_) + ".");        
            }
        }
    }



/**
Removes the object from the proxy table.

@param object   The object.
**/
    public void remove (Object object)
    {
        if (objectToId_.containsKey (object)) {
            Object key = objectToId_.get (object);
            idToObject_.remove (key);
            objectToId_.remove (object);
            
            if (DEBUG_) {
                System.out.println("Px table: Removed " + object + " (" + object.getClass() + ") as #" + key + ".");       
                System.out.println("-Px object count = " + (--debugObjectCounter_) + ".");        
            }
        }
    }



/**
Removes all objects from the proxy table.
**/
    public void removeAll ()
    {
        if (DEBUG_)
            debugObjectCounter_ -= idToObject_.size();

        // Find any AS400ImplRemote's and disconnect them.  Otherwise,
        // they may sit around and run forever!
        Enumeration enum = objectToId_.keys();
        while(enum.hasMoreElements()) {
            Object object = enum.nextElement();
            if (object instanceof AS400ImplRemote)
                ((AS400ImplRemote)object).disconnectAllServices();            
        }

        idToObject_.clear ();
        objectToId_.clear ();

        if (DEBUG_) {
            System.out.println("Px table: Removed all objects."); 
            System.out.println("--Px object count = " + (debugObjectCounter_) + ".");        
        }
        
    }


}