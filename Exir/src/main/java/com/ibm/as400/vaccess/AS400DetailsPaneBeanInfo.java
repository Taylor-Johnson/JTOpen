///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: AS400DetailsPaneBeanInfo.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.vaccess;

import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.beans.*;



/**
The AS400DetailsPaneBeanInfo class provides bean information
for the AS400DetailsPane class.

@see AS400DetailsPane
@deprecated Use Java Swing instead, along with the classes in package <tt>com.ibm.as400.access</tt>
**/


public class AS400DetailsPaneBeanInfo
extends SimpleBeanInfo
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";




    // Private data.
    private final static Class              beanClass_      = AS400DetailsPane.class;
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

            EventSetDescriptor listSelection = new EventSetDescriptor (beanClass_,
                "listSelection", ListSelectionListener.class, "valueChanged");
            listSelection.setDisplayName (ResourceLoader.getText ("EVT_NAME_LIST_SELECTION"));
            listSelection.setShortDescription (ResourceLoader.getText ("EVT_DESC_LIST_SELECTION"));

            EventSetDescriptor propertyChange = new EventSetDescriptor (beanClass_,
                "propertyChange", PropertyChangeListener.class, "propertyChange");
            propertyChange.setDisplayName (ResourceLoader.getText ("EVT_NAME_PROPERTY_CHANGE"));
            propertyChange.setShortDescription (ResourceLoader.getText ("EVT_DESC_PROPERTY_CHANGE"));

            EventSetDescriptor vetoableChange = new EventSetDescriptor (beanClass_,
            		"propertyChange", VetoableChangeListener.class, "vetoableChange");
            vetoableChange.setDisplayName (ResourceLoader.getText ("EVT_NAME_PROPERTY_VETO"));
            vetoableChange.setShortDescription (ResourceLoader.getText ("EVT_DESC_PROPERTY_VETO"));

            events_ = new EventSetDescriptor[] { error, listSelection, propertyChange,
                vetoableChange };

            // Properties.
            PropertyDescriptor allowActions = new PropertyDescriptor ("allowActions", beanClass_);
            allowActions.setBound (false);
            allowActions.setConstrained (false);
            allowActions.setDisplayName (ResourceLoader.getText ("PROP_NAME_ALLOW_ACTIONS"));
            allowActions.setShortDescription (ResourceLoader.getText ("PROP_DESC_ALLOW_ACTIONS"));

            PropertyDescriptor columnModel = new PropertyDescriptor ("columnModel", beanClass_,
                "getColumnModel", null);
            columnModel.setBound (false);
            columnModel.setConstrained (false);
            columnModel.setDisplayName (ResourceLoader.getText ("PROP_NAME_COLUMN_MODEL"));
            columnModel.setShortDescription (ResourceLoader.getText ("PROP_DESC_COLUMN_MODEL"));

            PropertyDescriptor confirm = new PropertyDescriptor ("confirm", beanClass_);
            confirm.setBound (false);
            confirm.setConstrained (false);
            confirm.setDisplayName (ResourceLoader.getText ("PROP_NAME_CONFIRM"));
            confirm.setShortDescription (ResourceLoader.getText ("PROP_DESC_CONFIRM"));

            PropertyDescriptor model = new PropertyDescriptor ("model", beanClass_,
                "getModel", null);
            model.setBound (false);
            model.setConstrained (false);
            model.setDisplayName (ResourceLoader.getText ("PROP_NAME_MODEL"));
            model.setShortDescription (ResourceLoader.getText ("PROP_DESC_MODEL"));

            PropertyDescriptor root = new PropertyDescriptor ("root", beanClass_);
            root.setBound (true);
            root.setConstrained (true);
            root.setDisplayName (ResourceLoader.getText ("PROP_NAME_ROOT"));
            root.setShortDescription (ResourceLoader.getText ("PROP_DESC_ROOT"));

            PropertyDescriptor selectionModel = new PropertyDescriptor ("selectionModel", beanClass_);
            selectionModel.setBound (false);
            selectionModel.setConstrained (false);
            selectionModel.setDisplayName (ResourceLoader.getText ("PROP_NAME_SELECTION_MODEL"));
            selectionModel.setShortDescription (ResourceLoader.getText ("PROP_DESC_SELECTION_MODEL"));

            properties_ = new PropertyDescriptor[] { allowActions, columnModel,
                confirm, model, root, selectionModel };
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
        return 4; // root.
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
                image = loadImage ("AS400DetailsPane16.gif");
                break;
            case BeanInfo.ICON_MONO_32x32:
            case BeanInfo.ICON_COLOR_32x32:
                image = loadImage ("AS400DetailsPane32.gif");
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


