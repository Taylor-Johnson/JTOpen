///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: HTMLTableCaptionBeanInfo.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.util.html;

import java.awt.*;
import java.beans.*;

/**
 * The HTMLTableCaptionBeanInfo class provides
 * bean information for the HTMLTableCaption class.
 **/
public class HTMLTableCaptionBeanInfo extends SimpleBeanInfo {
    private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

    // Class this bean info represents.
    private final static Class beanClass = HTMLTableCaption.class;

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
            PropertyDescriptor alignment = new PropertyDescriptor("alignment", beanClass,
                    "getAlignment", "setAlignment");
            alignment.setBound(true);
            alignment.setConstrained(true);
            alignment.setDisplayName(loader_.getText("PROP_NAME_ALIGNMENT"));
            alignment.setShortDescription(loader_.getText("PROP_HTCAP_DESC_ALIGNMENT"));

            PropertyDescriptor lang = new PropertyDescriptor("lang", beanClass, "getLanguage", "setLanguage");   //$B1A
            lang.setBound(true);                                                                                 //$B1A
            lang.setConstrained(true);                                                                           //$B1A
            lang.setDisplayName(loader_.getText("PROP_NAME_LANGUAGE"));                                          //$B1A
            lang.setShortDescription(loader_.getText("PROP_DESC_LANGUAGE"));                                     //$B1A

            PropertyDescriptor dir = new PropertyDescriptor("dir", beanClass, "getDirection", "setDirection");   //$B1A
            dir.setBound(true);                                                                                  //$B1A
            dir.setConstrained(true);                                                                            //$B1A
            dir.setDisplayName(loader_.getText("PROP_NAME_DIRECTION"));                                          //$B1A
            dir.setShortDescription(loader_.getText("PROP_DESC_DIRECTION"));                                     //$B1A

            PropertyDescriptor useFO = new PropertyDescriptor("useFO", beanClass, "isUseFO", "setUseFO");           //@C1A
            useFO.setBound(true);                                                                                   //@C1A
            useFO.setConstrained(false);                                                                            //@C1A
            useFO.setDisplayName(loader_.getText("PROP_NAME_FORMATTING_OBJECT"));                                   //@C1A
            useFO.setShortDescription(loader_.getText("PROP_DESC_FORMATTING_OBJECT"));                              //@C1A

            properties_ = new PropertyDescriptor[]{alignment, lang, dir, useFO};                                     //$B1C    //@C1C
        } catch (Exception e) {
            throw new Error(e.toString());
        }
    }


    /**
     * Returns the BeanInfo for the superclass of this bean.  Since
     * HTMLTableCaption is a subclass of HTMLTagAttributes, this method
     * will return a HTMLTagAttributesBeanInfo object.
     *
     * @return BeanInfo[] containing this bean's superclass BeanInfo
     **/
    public BeanInfo[] getAdditionalBeanInfo()                            // @Z1A
    {
        return new BeanInfo[]{new HTMLTagAttributesBeanInfo()};
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
                image = loadImage("HTMLTableCaption16.gif");
                break;
            case BeanInfo.ICON_MONO_32x32:
            case BeanInfo.ICON_COLOR_32x32:
                image = loadImage("HTMLTableCaption32.gif");
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
