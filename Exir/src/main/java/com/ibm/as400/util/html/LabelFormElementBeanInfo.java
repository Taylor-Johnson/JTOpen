///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: LabelFormElementBeanInfo.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.util.html;


import java.awt.Image;
import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;

/**
 * The LabelFormElementBeanInfo class provides
 * bean information for the LabelFormElement class.
 **/
public class LabelFormElementBeanInfo extends SimpleBeanInfo {
    private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";


    // Class this bean info represents.
    private final static Class beanClass = LabelFormElement.class;

    // Handles loading the appropriate resource bundle
    private static ResourceBundleLoader_h loader_;

    private static EventSetDescriptor[] events_;
    private static PropertyDescriptor[] properties_;


    static {

        try {
            EventSetDescriptor changed = new EventSetDescriptor(beanClass,
                    "propertyChange",
                    java.beans.PropertyChangeListener.class,
                    "propertyChange");
            changed.setDisplayName(loader_.getText("EVT_NAME_PROPERTY_CHANGE"));
            changed.setShortDescription(loader_.getText("EVT_DESC_PROPERTY_CHANGE"));

            EventSetDescriptor veto = new EventSetDescriptor(beanClass,
                    "propertyChange",
                    java.beans.VetoableChangeListener.class,
                    "vetoableChange");
            veto.setDisplayName(loader_.getText("EVT_NAME_PROPERTY_VETO"));
            veto.setShortDescription(loader_.getText("EVT_DESC_PROPERTY_VETO"));

            EventSetDescriptor[] events = {changed, veto};

            events_ = events;

            // ***** PROPERTIES
            PropertyDescriptor label = new PropertyDescriptor("label", beanClass,
                    "getLabel", "setLabel");
            label.setBound(true);
            label.setConstrained(true);
            label.setDisplayName(loader_.getText("PROP_NAME_LABEL"));
            label.setShortDescription(loader_.getText("PROP_LF_DESC_LABEL"));

            properties_ = new PropertyDescriptor[]{label};
        } catch (Exception e) {
            throw new Error(e.toString());
        }
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
                image = loadImage("LabelFormElement16.gif");
                break;
            case BeanInfo.ICON_MONO_32x32:
            case BeanInfo.ICON_COLOR_32x32:
                image = loadImage("LabelFormElement32.gif");
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
