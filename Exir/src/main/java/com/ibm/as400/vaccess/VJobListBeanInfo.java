///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: VJobListBeanInfo.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.vaccess;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


/**
The VJobListBeanInfo class provides bean information
for the VJobList class.

@see VJobList
@deprecated Use Java Swing instead, along with the classes in package <tt>com.ibm.as400.access</tt>
**/
public class VJobListBeanInfo
extends SimpleBeanInfo
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";




    // Private data.
    private final static Class              beanClass_      = VJobList.class;
    private static EventSetDescriptor[]     events_;
    private static PropertyDescriptor[]     properties_;



/**
Static initializer.
**/
    static
    {
        try {

            // Events.
            EventSetDescriptor error = new EventSetDescriptor (beanClass_,
                "error", ErrorListener.class, "errorOccurred");
            error.setDisplayName (ResourceLoader.getText ("EVT_NAME_ERROR"));
            error.setShortDescription (ResourceLoader.getText ("EVT_DESC_ERROR"));

            String[] vobjectMethods = { "objectChanged", "objectCreated", "objectDeleted" };
            EventSetDescriptor vobject = new EventSetDescriptor (beanClass_,
                "vobject", VObjectListener.class, vobjectMethods,
                "addVObjectListener", "removeVObjectListener");
            vobject.setDisplayName (ResourceLoader.getText ("EVT_NAME_VOBJECT"));
            vobject.setShortDescription (ResourceLoader.getText ("EVT_DESC_VOBJECT"));

            String[] workingMethods = { "startWorking", "stopWorking" };
            EventSetDescriptor working = new EventSetDescriptor (beanClass_,
                "working", WorkingListener.class, workingMethods,
                "addWorkingListener", "removeWorkingListener");
            working.setDisplayName (ResourceLoader.getText ("EVT_NAME_WORKING"));
            working.setShortDescription (ResourceLoader.getText ("EVT_DESC_WORKING"));

            events_ = new EventSetDescriptor[] { error, vobject, working };

            // Properties.
            PropertyDescriptor name = new PropertyDescriptor ("name", beanClass_);
            name.setBound (true);
            name.setConstrained (true);
            name.setDisplayName (ResourceLoader.getText ("PROP_NAME_NAME"));
            name.setShortDescription (ResourceLoader.getText ("PROP_DESC_NAME"));

            PropertyDescriptor number = new PropertyDescriptor ("number", beanClass_);
            number.setBound (true);
            number.setConstrained (true);
            number.setDisplayName (ResourceLoader.getText ("PROP_NAME_NUMBER"));
            number.setShortDescription (ResourceLoader.getText ("PROP_DESC_NUMBER"));

            PropertyDescriptor system = new PropertyDescriptor ("system", beanClass_);
            system.setBound (true);
            system.setConstrained (true);
            system.setDisplayName (ResourceLoader.getText ("PROP_NAME_SYSTEM"));
            system.setShortDescription (ResourceLoader.getText ("PROP_DESC_SYSTEM"));

            PropertyDescriptor user = new PropertyDescriptor ("user", beanClass_);
            user.setBound (true);
            user.setConstrained (true);
            user.setDisplayName (ResourceLoader.getText ("PROP_NAME_USER"));
            user.setShortDescription (ResourceLoader.getText ("PROP_DESC_USER"));

            properties_ = new PropertyDescriptor[] { name, number,
                system, user };
        }
        catch (Exception e) {
            throw new Error (e.toString ());
        }
    }



/**
Returns the bean descriptor.

@return The bean descriptor.
**/
    public BeanDescriptor getBeanDescriptor ()
    {
        return new BeanDescriptor (beanClass_);
    }



/**
Returns the index of the default event.

@return The index of the default event.
**/
    public int getDefaultEventIndex ()
    {
        return 0; // ErrorEvent.
    }



/**
Returns the index of the default property.

@return The index of the default property.
**/
    public int getDefaultPropertyIndex ()
    {
        return 2; // system.
    }



/**
Returns the descriptors for all events.

@return The descriptors for all events.
**/
    public EventSetDescriptor[] getEventSetDescriptors ()
    {
        return events_;
    }



/**
Returns an image for the icon.

@param icon    The icon size and color.
@return        The image.
**/
    public Image getIcon (int icon)
    {
        Image image = null;
        switch (icon) {
            case BeanInfo.ICON_MONO_16x16:
            case BeanInfo.ICON_COLOR_16x16:
                image = loadImage ("VJobList16.gif");
                break;
            case BeanInfo.ICON_MONO_32x32:
            case BeanInfo.ICON_COLOR_32x32:
                image = loadImage ("VJobList32.gif");
                break;
        }
        return image;
    }



/**
Returns the descriptors for all properties.

@return The descriptors for all properties.
**/
    public PropertyDescriptor[] getPropertyDescriptors ()
    {
        return properties_;
    }



}


