///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: AS400JDBCConnectionPoolBeanInfo.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2001 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.awt.*;
import java.beans.*;


/**
 * The AS400JDBCConnectionPoolBeanInfo class provides bean information
 * for the AS400JDBCConnectionPool class.
 **/
public class AS400JDBCConnectionPoolBeanInfo extends SimpleBeanInfo {
    static final String copyright = "Copyright (C) 1997-2001 International Business Machines Corporation and others.";


    // Class this bean info represents.
    private final static Class beanClass = AS400JDBCConnectionPool.class;

    // Handles loading the appropriate resource bundle
    // private static ResourceBundleLoader loader_;

    private static EventSetDescriptor[] events_;
    private static PropertyDescriptor[] properties_;

    static {
        try {
            EventSetDescriptor changed = new EventSetDescriptor(beanClass,
                    "propertyChange",
                    java.beans.PropertyChangeListener.class,
                    "propertyChange");
            changed.setDisplayName(ResourceBundleLoader.getText("EVT_NAME_PROPERTY_CHANGE"));
            changed.setShortDescription(ResourceBundleLoader.getText("EVT_DESC_PROPERTY_CHANGE"));

            EventSetDescriptor[] events = {changed};

            events_ = events;

            PropertyDescriptor dataSource = new PropertyDescriptor("dataSource", beanClass,
                    "getDataSource", "setDataSource");
            dataSource.setBound(true);
            dataSource.setConstrained(false);
            dataSource.setDisplayName(ResourceBundleLoader.getText("PROP_NAME_CP_DATA_SOURCE"));
            dataSource.setShortDescription(ResourceBundleLoader.getText("PROP_DESC_CP_DATA_SOURCE"));

            properties_ = new PropertyDescriptor[]{dataSource};
        } catch (Exception e) {
            throw new Error(e.toString());
        }
    }

    /**
     * Returns additional bean information from the ConnectionPool superclass.
     *
     * @return The bean information.
     **/
    public BeanInfo[] getAdditionalBeanInfo() {
        return new BeanInfo[]{new ConnectionPoolBeanInfo()};
    }

    /**
     * Returns the bean descriptor.
     *
     * @return The bean descriptor.
     **/
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(beanClass);
    }


    /**
     * Returns the index of the default event.
     *
     * @return The index to the default event.
     **/
    public int getDefaultEventIndex() {
        return 0;
    }

    /**
     * Returns the index of the default property.
     *
     * @return The index to the default property.
     **/
    public int getDefaultPropertyIndex() {
        return 0;
    }

    /**
     * Returns the descriptors for all events.
     *
     * @return The descriptors for all events.
     **/
    public EventSetDescriptor[] getEventSetDescriptors() {
        return events_;
    }

    /**
     * Returns an image for the icon.
     *
     * @param icon The icon size and color.
     * @return The image.
     **/
    public Image getIcon(int icon) {
        Image image = null;
        switch (icon) {
            case BeanInfo.ICON_MONO_16x16:
            case BeanInfo.ICON_COLOR_16x16:
                image = loadImage("AS400JDBCConnectionPool16.gif");
                break;
            case BeanInfo.ICON_MONO_32x32:
            case BeanInfo.ICON_COLOR_32x32:
                image = loadImage("AS400JDBCConnectionPool32.gif");
                break;
        }
        return image;
    }

    /**
     * Returns the descriptors for all properties.
     *
     * @return The descriptors for all properties.
     **/
    public PropertyDescriptor[] getPropertyDescriptors() {
        return properties_;
    }
}
