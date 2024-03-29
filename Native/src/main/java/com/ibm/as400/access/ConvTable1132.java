///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  ConvTable1132.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2004 International Business Machines Corporation and
// others.  All rights reserved.
//
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable1132 extends ConvTableSingleMap {
    private static final String copyright = "Copyright (C) 1997-2004 International Business Machines Corporation and others.";

    private static final String toUnicode_ =
            "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F\u0097\u008D\u008E\u000B\f\r\u000E\u000F" +
                    "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +
                    "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +
                    "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +
                    "\u0020\u00A0\u0E81\u0E82\u0E84\u0E87\u0E88\u0EAA\u0E8A\u005B\u00A2\u002E\u003C\u0028\u002B\u007C" +
                    "\u0026\u001A\u0E8D\u0E94\u0E95\u0E96\u0E97\u0E99\u0E9A\u005D\u0021\u0024\u002A\u0029\u003B\u00AC" +
                    "\u002D\u002F\u0E9B\u0E9C\u0E9D\u0E9E\u0E9F\u0EA1\u0EA2\u005E\u00A6\u002C\u0025\u005F\u003E\u003F" +
                    "\u006B\u001A\u0EA3\u0EA5\u0EA7\u0EAB\u0EAD\u0EAE\u001A\u0060\u003A\u0023\u0040\'\u003D\"" +
                    "\u001A\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u001A\u001A\u0EAF\u0EB0\u0EB2\u0EB3" +
                    "\u001A\u006A\u006B\u006C\u006D\u006E\u006F\u0070\u0071\u0072\u0EB4\u0EB5\u0EB6\u0EB7\u0EB8\u0EB9" +
                    "\u001A\u007E\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u0EBC\u0EB1\u0EBB\u0EBD\u001A\u001A" +
                    "\u0ED0\u0ED1\u0ED2\u0ED3\u0ED4\u0ED5\u0ED6\u0ED7\u0ED8\u0ED9\u001A\u0EC0\u0EC1\u0EC2\u0EC3\u0EC4" +
                    "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u001A\u0EC8\u0EC9\u0ECA\u0ECB\u0ECC" +
                    "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050\u0051\u0052\u0ECD\u0EC6\u001A\u0EDC\u0EDD\u001A" +
                    "\\\u001A\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u001A\u001A\u001A\u001A\u001A\u001A" +
                    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u001A\u001A\u001A\u001A\u001A\u009F";


    private static final String fromUnicode_ =
            "\u0001\u0203\u372D\u2E2F\u1605\u250B\u0C0D\u0E0F\u1011\u1213\u3C3D\u3226\u1819\u3F27\u1C1D\u1E1F" +
                    "\u405A\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F" +
                    "\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE949\uE059\u696D" +
                    "\u7981\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u4FD0\uA107" +
                    "\u2021\u2223\u2415\u0617\u2829\u2A2B\u2C09\u0A1B\u3031\u1A33\u3435\u3608\u3839\u3A3B\u0414\u3EFF" +
                    "\u413F\u4A3F\u3F3F\u6A3F\u3F3F\u3F3F\u5F3F\uFFFF\u06E9\u3F3F\u3F42\u433F\u443F\u3F45\u463F\u483F" +
                    "\u3F52\u3F3F\u3F3F\u3F3F\u5354\u5556\u3F57\u5862\u6364\u6566\u3F67\u6872\u3F73\u3F74\u3F3F\u4775" +
                    "\u3F76\u778C\u8DAB\u8E8F\u9A9B\u9C9D\u9E9F\u3FAC\uAAAD\u3F3F\uBBBC\uBDBE\uBF3F\uDB3F\uCBCC\uCDCE" +
                    "\uCFDA\u3F3F\uB0B1\uB2B3\uB4B5\uB6B7\uB8B9\u3F3F\uDDDE\uFFFF\u7811\u3F3F\u3F5A\u7F7B\u5B6C\u507D" +
                    "\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F\u7CC1\uC2C3\uC4C5\uC6C7" +
                    "\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE949\uE059\u696D\u7981\u8283\u8485\u8687" +
                    "\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u4FD0\uA13F\uFFFF\u0050\u3F3F";


    ConvTable1132() {
        super(1132, toUnicode_.toCharArray(), fromUnicode_.toCharArray());

        super.fromUnicode_[0x20ad] = super.fromUnicode_[0x006b];  /* Kip Sign to k */

    }
}
