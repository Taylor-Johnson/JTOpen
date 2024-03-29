///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  ConvTable1156.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2004 International Business Machines Corporation and
// others.  All rights reserved.
//
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable1156 extends ConvTableSingleMap {
    private static final String copyright = "Copyright (C) 1997-2004 International Business Machines Corporation and others.";

    private static final String toUnicode_ =
            "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F\u0097\u008D\u008E\u000B\f\r\u000E\u000F" +
                    "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +
                    "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +
                    "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +
                    "\u0020\u00A0\u0161\u00E4\u0105\u012F\u016B\u00E5\u0113\u017E\u00A2\u002E\u003C\u0028\u002B\u007C" +
                    "\u0026\u00E9\u0119\u0117\u010D\u0173\u201E\u201C\u0123\u00DF\u0021\u0024\u002A\u0029\u003B\u00AC" +
                    "\u002D\u002F\u0160\u00C4\u0104\u012E\u016A\u00C5\u0112\u017D\u00A6\u002C\u0025\u005F\u003E\u003F" +
                    "\u00F8\u00C9\u0118\u0116\u010C\u0172\u012A\u013B\u0122\u0060\u003A\u0023\u0040\'\u003D\"" +
                    "\u00D8\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u00AB\u00BB\u0101\u017C\u0144\u00B1" +
                    "\u00B0\u006A\u006B\u006C\u006D\u006E\u006F\u0070\u0071\u0072\u0156\u0157\u00E6\u0137\u00C6\u20AC" +
                    "\u00B5\u007E\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u201D\u017A\u0100\u017B\u0143\u00AE" +
                    "\u005E\u00A3\u012B\u00B7\u00A9\u00A7\u00B6\u00BC\u00BD\u00BE\u005B\u005D\u0179\u0136\u013C\u00D7" +
                    "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u00AD\u014D\u00F6\u0146\u00F3\u00F5" +
                    "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050\u0051\u0052\u00B9\u0107\u00FC\u0142\u015B\u2019" +
                    "\\\u00F7\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u00B2\u014C\u00D6\u0145\u00D3\u00D5" +
                    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u00B3\u0106\u00DC\u0141\u015A\u009F";


    private static final String fromUnicode_ =
            "\u0001\u0203\u372D\u2E2F\u1605\u250B\u0C0D\u0E0F\u1011\u1213\u3C3D\u3226\u1819\u3F27\u1C1D\u1E1F" +
                    "\u405A\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F" +
                    "\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE9BA\uE0BB\uB06D" +
                    "\u7981\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u4FD0\uA107" +
                    "\u2021\u2223\u2415\u0617\u2829\u2A2B\u2C09\u0A1B\u3031\u1A33\u3435\u3608\u3839\u3A3B\u0414\u3EFF" +
                    "\u413F\u4AB1\u3F3F\u6AB5\u3FB4\u3F8A\u5FCA\uAF3F\u908F\uEAFA\u3FA0\uB6B3\u3FDA\u3F8B\uB7B8\uB93F" +
                    "\u3F3F\u3F3F\u6367\u9E3F\u0000\u0003\u3F71\u3F3F\u3F3F\uEEEF\uECBF\u803F\u3F3F\uFC3F\u3F59\u3F3F" +
                    "\u3F3F\u4347\u9C3F\u0000\u0003\u3F51\u3F3F\u3F3F\uCECF\uCCE1\u703F\u3F3F\uDC3F\u3F3F\uAC8C\u3F3F" +
                    "\u6444\uFBDB\u3F3F\u3F3F\u7454\u3F3F\u3F3F\u6848\u3F3F\u7353\u7252\uFFFF\u0004\u3F3F\u7858\u3F3F" +
                    "\u3F3F\u3F3F\u76B2\u3F3F\u6545\u3F3F\u3F3F\u3F3F\uBD9D\u3F3F\u3F77\uBE3F\u3F3F\u3FFD\uDDAE\u8EED" +
                    "\uCD3F\u3F3F\u3F3F\uEBCB\uFFFF\u0004\u3F3F\u9A9B\u3F3F\uFEDE\u3F3F\u3F3F\u6242\uFFFF\u0004\u3F3F" +
                    "\u6646\u3F3F\u3F3F\u3F3F\u7555\u3F3F\u3F3F\u3FBC\uABAD\u8D69\u493F\uFFFF\u0F4C\u3F3F\u3FDF\u3F3F" +
                    "\u57AA\u563F\uFFFF\u0046\u3F3F\u9F3F\uFFFF\u6F29\u3F3F\u3F5A\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60" +
                    "\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4" +
                    "\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE9BA\uE0BB\uB06D\u7981\u8283\u8485\u8687\u8889\u9192\u9394" +
                    "\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u4FD0\uA13F\uFFFF\u0050\u3F3F";


    ConvTable1156() {
        super(1156, toUnicode_.toCharArray(), fromUnicode_.toCharArray());
    }
}
