///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: FileFormInputBeanInfo.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.util.html;


import java.awt.*;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;

/**
 * The FileFormInputBeanInfo class provides
 * bean information for the FileFormInput class.
 **/
public class FileFormInputBeanInfo extends SimpleBeanInfo {
    private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";


    // Class this bean info represents.
    private final static Class beanClass = FileFormInput.class;


    /**
     * Returns the BeanInfo for the superclass of this bean.  Since
     * FileFormInput is a subclass of FormInput, this method
     * will return a FormInputBeanInfo object.
     *
     * @return BeanInfo[] containing this bean's superclass BeanInfo
     **/
    public BeanInfo[] getAdditionalBeanInfo() {
        return new BeanInfo[]{new FormInputBeanInfo()};
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
                image = loadImage("FileFormInput16.gif");
                break;
            case BeanInfo.ICON_MONO_32x32:
            case BeanInfo.ICON_COLOR_32x32:
                image = loadImage("FileFormInput32.gif");
                break;
        }
        return image;
    }

}
