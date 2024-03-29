///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  ConvTable5233.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2016 International Business Machines Corporation and
// others.  All rights reserved.
//
// Generated Fri Apr 08 14:08:13 CDT 2016 from sq730
// Using Open Source Software, JTOpen 9.0, codebase 5770-SS1 V7R3M0.00 built=20160408 @R1
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable5233 extends ConvTableSingleMap {
    private static final String copyright = "Copyright (C) 1997-2016 International Business Machines Corporation and others.";
    private static final String toUnicode_ =
            "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F\u0097\u008D\u008E\u000B\f\r\u000E\u000F" +
                    "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +
                    "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +
                    "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +
                    "\u0020\u00A0\u0901\u0902\u0903\u0905\u0906\u0907\u0908\u0909\u090A\u002E\u003C\u0028\u002B\u007C" +
                    "\u0026\u090B\u090C\u090D\u090E\u090F\u0910\u0911\u0912\u0913\u0021\u0024\u002A\u0029\u003B\u005E" +
                    "\u002D\u002F\u0914\u0915\u0916\u0917\u0918\u0919\u091A\u091B\u091C\u002C\u0025\u005F\u003E\u003F" +
                    "\u091D\u091E\u091F\u0920\u0921\u0922\u0923\u0924\u0925\u0060\u003A\u0023\u0040\'\u003D\"" +
                    "\u0926\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u0927\u0928\u092A\u092B\u092C\u092D" +
                    "\u092E\u006A\u006B\u006C\u006D\u006E\u006F\u0070\u0071\u0072\u092F\u0930\u0932\u0933\u0935\u0936" +
                    "\u200C\u007E\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u0937\u0938\u0939\u005B\u093C\u093D" +
                    "\u093E\u093F\u0940\u0941\u0942\u0943\u0944\u0945\u0946\u0947\u0948\u0949\u094A\u005D\u094B\u094C" +
                    "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u094D\u0950\u0951\u0952\u20B9\u001A" +
                    "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050\u0051\u0052\u0960\u0961\u0962\u0963\u0964\u0965" +
                    "\\\u200D\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u0966\u0967\u0968\u0969\u096A\u096B" +
                    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u096C\u096D\u096E\u096F\u0970\u009F";
    private static final String fromUnicode_ =
            "\u0001\u0203\u372D\u2E2F\u1605\u250B\u0C0D\u0E0F\u1011\u1213\u3C3D\u3226\u1819\u3F27\u1C1D\u1E1F" +
                    "\u405A\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F" +
                    "\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE9AD\uE0BD\u5F6D" +
                    "\u7981\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u4FD0\uA107" +
                    "\u2021\u2223\u2415\u0617\u2829\u2A2B\u2C09\u0A1B\u3031\u1A33\u3435\u3608\u3839\u3A3B\u0414\u3EFF" +
                    "\u413F\uFFFF\u042F\u3F3F\u3F42\u4344\u3F45\u4647\u4849\u4A51\u5253\u5455\u5657\u5859\u6263\u6465" +
                    "\u6667\u6869\u6A70\u7172\u7374\u7576\u7778\u808A\u8B3F\u8C8D\u8E8F\u909A\u9B3F\u9C9D\u3F9E\u9FAA" +
                    "\uABAC\u3F3F\uAEAF\uB0B1\uB2B3\uB4B5\uB6B7\uB8B9\uBABB\uBCBE\uBFCA\u3F3F\uCBCC\uCD3F\uFFFF\u0006" +
                    "\u3F3F\uDADB\uDCDD\uDEDF\uEAEB\uECED\uEEEF\uFAFB\uFCFD\uFE3F\uFFFF\u0B4D\u3F3F\uA0E1\uFFFF\u0055" +
                    "\u3F3F\u3FCE\uFFFF\u6F23\u3F3F\u3F5A\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5" +
                    "\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4" +
                    "\uE5E6\uE7E8\uE9AD\uE0BD\u5F6D\u7981\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4" +
                    "\uA5A6\uA7A8\uA9C0\u4FD0\uA13F\uFFFF\u0050\u3F3F";
    private static char[] toUnicodeArray_;
    private static char[] fromUnicodeArray_;

    static {
        toUnicodeArray_ = toUnicode_.toCharArray();
        fromUnicodeArray_ = fromUnicode_.toCharArray();
    }

    ConvTable5233() {
        super(5233, toUnicodeArray_, fromUnicodeArray_);
    }


    ConvTable5233(int ccsid) {
        super(ccsid, toUnicodeArray_, fromUnicodeArray_);
    }
}
