///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  ConvTable737.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2004 International Business Machines Corporation and
// others.  All rights reserved.
//
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable737 extends ConvTableAsciiMap {
    private static final String copyright = "Copyright (C) 1997-2004 International Business Machines Corporation and others.";

    private static final String toUnicode_ =
            "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000B\f\r\u000E\u000F" +
                    "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001C\u001B\u007F\u001D\u001E\u001F" +
                    "\u0020\u0021\"\u0023\u0024\u0025\u0026\'\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F" +
                    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F" +
                    "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F" +
                    "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F" +
                    "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F" +
                    "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u001A" +
                    "\u0391\u0392\u0393\u0394\u0395\u0396\u0397\u0398\u0399\u039A\u039B\u039C\u039D\u039E\u039F\u03A0" +
                    "\u03A1\u03A3\u03A4\u03A5\u03A6\u03A7\u03A8\u03A9\u03B1\u03B2\u03B3\u03B4\u03B5\u03B6\u03B7\u03B8" +
                    "\u03B9\u03BA\u03BB\u03BC\u03BD\u03BE\u03BF\u03C0\u03C1\u03C3\u03C2\u03C4\u03C5\u03C6\u03C7\u03C8" +
                    "\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255D\u255C\u255B\u2510" +
                    "\u2514\u2534\u252C\u251C\u2500\u253C\u255E\u255F\u255A\u2554\u2569\u2566\u2560\u2550\u256C\u2567" +
                    "\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256B\u256A\u2518\u250C\u2588\u2584\u258C\u2590\u2580" +
                    "\u03C9\u03AC\u03AD\u03AE\u03CA\u03AF\u03CC\u03CD\u03CB\u03CE\u0386\u0388\u0389\u038A\u038C\u038E" +
                    "\u038F\u00B1\u2265\u2264\u03AA\u03AB\u00F7\u2248\u00B0\u2219\u00B7\u221A\u207F\u00B2\u25A0\u00A0";


    private static final String fromUnicode_ =
            "\u0001\u0203\u0405\u0607\u0809\u0A0B\u0C0D\u0E0F\u1011\u1213\u1415\u1617\u1819\u7F1B\u1A1D\u1E1F" +
                    "\u2021\u2223\u2425\u2627\u2829\u2A2B\u2C2D\u2E2F\u3031\u3233\u3435\u3637\u3839\u3A3B\u3C3D\u3E3F" +
                    "\u4041\u4243\u4445\u4647\u4849\u4A4B\u4C4D\u4E4F\u5051\u5253\u5455\u5657\u5859\u5A5B\u5C5D\u5E5F" +
                    "\u6061\u6263\u6465\u6667\u6869\u6A6B\u6C6D\u6E6F\u7071\u7273\u7475\u7677\u7879\u7A7B\u7C7D\u7E1C" +
                    "\uFFFF\u0010\u7F7F\uFF7F\u0000\u0003\u7F7F\u7F15\u7F7F\u7F7F\uF8F1\uFD7F\u7F7F\u14FA\uFFFF\u001F" +
                    "\u7F7F\u7FF6\uFFFF\u0147\u7F7F\uEA7F\uEBEC\uED7F\uEE7F\uEFF0\u7F80\u8182\u8384\u8586\u8788\u898A" +
                    "\u8B8C\u8D8E\u8F90\u7F91\u9293\u9495\u9697\uF4F5\uE1E2\uE3E5\u7F98\u999A\u9B9C\u9D9E\u9FA0\uA1A2" +
                    "\uA3A4\uA5A6\uA7A8\uAAA9\uABAC\uADAE\uAFE0\uE4E8\uE6E7\uE97F\u7F7F\u7F7F\u7FAD\uFFFF\u0E26\u7F7F" +
                    "\u077F\uFFFF\f\u7F7F\u137F\uFFFF\u0020\u7F7F\u7FFC\uFFFF\u0088\u7F7F\u1B18\u1A19\u1D12\uFFFF" +
                    "\t\u7F7F\u177F\uFFFF\u0037\u7F7F\u7FF9\uFB7F\u7F7F\u7F1C\uFFFF\u0014\u7F7F\uF77F\uFFFF\r" +
                    "\u7F7F\uF3F2\uFFFF\u014D\u7F7F\uC47F\uB37F\uFFFF\u0004\u7F7F\uDA7F\u7F7F\uBF7F\u7F7F\uC07F\u7F7F" +
                    "\uD97F\u7F7F\uC37F\u7F7F\u7F7F\u7F7F\uB47F\u7F7F\u7F7F\u7F7F\uC27F\u7F7F\u7F7F\u7F7F\uC17F\u7F7F" +
                    "\u7F7F\u7F7F\uC57F\uFFFF\t\u7F7F\uCDBA\uD5D6\uC9B8\uB7BB\uD4D3\uC8BE\uBDBC\uC6C7\uCCB5\uB6B9" +
                    "\uD1D2\uCBCF\uD0CA\uD8D7\uCE7F\uFFFF\t\u7F7F\uDF7F\u7F7F\uDC7F\u7F7F\uDB7F\u7F7F\uDD7F\u7F7F" +
                    "\uDEB0\uB1B2\uFFFF\u0006\u7F7F\uFE7F\uFFFF\u0005\u7F7F\u167F\u7F7F\u7F7F\u1E7F\u7F7F\u7F7F\u7F7F" +
                    "\u107F\u1F7F\u7F7F\u7F7F\u7F7F\u117F\u0000\u0004\u7F7F\u7F09\u7F7F\u7F7F\u7F7F\u080A\uFFFF\u0030" +
                    "\u7F7F\u0102\u0F7F\u7F7F\u0C7F\u0B7F\uFFFF\u000E\u7F7F\u067F\u7F05\u7F03\u047F\u7F7F\u0D0E\uFFFF" +
                    "\u6C4A\u7F7F\u7F21\u2223\u2425\u2627\u2829\u2A2B\u2C2D\u2E2F\u3031\u3233\u3435\u3637\u3839\u3A3B" +
                    "\u3C3D\u3E3F\u4041\u4243\u4445\u4647\u4849\u4A4B\u4C4D\u4E4F\u5051\u5253\u5455\u5657\u5859\u5A5B" +
                    "\u5C5D\u5E5F\u6061\u6263\u6465\u6667\u6869\u6A6B\u6C6D\u6E6F\u7071\u7273\u7475\u7677\u7879\u7A7B" +
                    "\u7C7D\u7E7F\uFFFF\u0044\u7F7F\uB31B\u181A\u19FE\u097F\uFFFF\b\u7F7F";


    ConvTable737() {
        super(737, toUnicode_.toCharArray(), fromUnicode_.toCharArray());
    }
}
