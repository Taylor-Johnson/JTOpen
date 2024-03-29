///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  ConvTable918.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2016 International Business Machines Corporation and
// others.  All rights reserved.
//
// Generated Wed Dec 05 14:15:36 CST 2018 from sq730
// Using Open Source Software, JTOpen 9.7, codebase 5770-SS1 V7R3M0.00 built=20181118 @Y0
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable918 extends ConvTableSingleMap {
    private static final String copyright = "Copyright (C) 1997-2016 International Business Machines Corporation and others.";
    // toUnicode_ length is 256
    private static final String toUnicode_ =
            "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F\u0097\u008D\u008E\u000B\f\r\u000E\u000F" +
                    "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +
                    "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +
                    "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +
                    "\u0020\u00A0\u060C\u061B\u061F\uFE81\uFE8D\uFE8E\uF8FB\uFE8F\u005B\u002E\u003C\u0028\u002B\u0021" +
                    "\u0026\uFE91\uFB56\uFB58\uFE93\uFE95\uFE97\uFB66\uFB68\uFE99\u005D\u0024\u002A\u0029\u003B\u005E" +
                    "\u002D\u002F\uFE9B\uFE9D\uFE9F\uFB7A\uFB7C\uFEA1\uFEA3\uFEA5\u0060\u002C\u0025\u005F\u003E\u003F" +
                    "\u06F0\u06F1\u06F2\u06F3\u06F4\u06F5\u06F6\u06F7\u06F8\u06F9\u003A\u0023\u0040\'\u003D\"" +
                    "\uFEA7\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\uFEA9\uFB88\uFEAB\uFEAD\uFB8C\uFEAF" +
                    "\uFB8A\u006A\u006B\u006C\u006D\u006E\u006F\u0070\u0071\u0072\uFEB1\uFEB3\uFEB5\uFEB7\uFEB9\uFEBB" +
                    "\uFEBD\u007E\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\uFEBF\uFEC3\uFEC7\uFEC9\uFECA\uFECB" +
                    "\uFECC\uFECD\uFECE\uFECF\uFED0\uFED1\uFED3\uFED5\uFED7\uFB8E\uFEDB\u007C\uFB92\uFB94\uFEDD\uFEDF" +
                    "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u00AD\uFEE0\uFEE1\uFEE3\uFB9E\uFEE5" +
                    "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050\u0051\u0052\uFEE7\uFE85\uFEED\uFBA6\uFBA8\uFBA9" +
                    "\\\uFBAA\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\uFE80\uFE89\uFE8A\uFE8B\uFBFC\uFBFD" +
                    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\uFBFE\uFBB0\uFBAE\uFE7C\uFE7D\u009F";
    // fromUnicode length = 300
    private static final String fromUnicode_ =
            "\u0001\u0203\u372D\u2E2F\u1605\u250B\u0C0D\u0E0F\u1011\u1213\u3C3D\u3226\u1819\u3F27\u1C1D\u1E1F" +
                    "\u404F\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F" +
                    "\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE94A\uE05A\u5F6D" +
                    "\u6A81\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\uBBD0\uA107" +
                    "\u2021\u2223\u2415\u0617\u2829\u2A2B\u2C09\u0A1B\u3031\u1A33\u3435\u3608\u3839\u3A3B\u0414\u3EFF" +
                    "\u413F\uFFFF\u0005\u3F3F\u3FCA\uFFFF\u02AF\u3F3F\u423F\uFFFF\u0006\u3F3F\u3F43\u3F3F\u3F44\u3FEA" +
                    "\u453F\uDB3F\u3F46\u4954\u5559\u6367\u698A\u8C8D\u8F9A\u9C9E\uA0AB\uACAD\uB13F\u3F3F\u3F3F\u3FB5" +
                    "\uB73F\uBECC\uCF3F\uDC3F\u3F3F\u3F3F\u3F3F\u3FFD\uFFFF\u0007\u3F3F\u7071\u7273\u7475\u763F\u7879" +
                    "\u6C6B\u4B5C\uFFFF\u0041\u3F3F\u7071\u7273\u7475\u7677\u7879\uFFFF\u7900\u3F3F\u3F48\uFFFF\u012D" +
                    "\u3F3F\u523F\u533F\uFFFF\u0006\u3F3F\u573F\u583F\uFFFF\b\u3F3F\u653F\u663F\uFFFF\u0005\u3F3F" +
                    "\u8B3F\u903F\u8E3F\uB93F\u3F3F\uBC3F\uBD3F\uFFFF\u0004\u3F3F\uCE3F\u3F3F\u3F3F\u3F3F\uDD3F\uDEDF" +
                    "\uE13F\u3F3F\uFC3F\uFB3F\uFFFF\u0025\u3F3F\uEEEF\uFA3F\uFFFF\u013E\u3F3F\uFDFE\u3F3F\uEA45\u3F3F" +
                    "\u3FDB\uDB3F\u3FEB\uECED\uED46\u4749\u4951\u5154\u5455\u5556\u5659\u5962\u6263\u6364\u6467\u6768" +
                    "\u6869\u6980\u808A\u8A8C\u8C8D\u8D8F\u8F9A\u9A9B\u9B9C\u9C9D\u9D9E\u9E9F\u9FA0\uA0AA\uAAAB\uABAB" +
                    "\uABAC\uACAC\uACAD\uAEAF\uB0B1\uB2B3\uB4B5\uB5B6\uB6B7\uB7B8\uB83F\u3FBA\uBABE\uBEBF\uCBCC\uCCCD" +
                    "\uCDCF\uCFDA\uDA3F\u3F3F\u3FDC\uDC3F\uFFFF\b\u3F3F\u3F4F\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60" +
                    "\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4" +
                    "\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE94A\uE05A\u5F6D\u6A81\u8283\u8485\u8687\u8889\u9192\u9394" +
                    "\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\uBBD0\uA13F\uFFFF\u0050\u3F3F";
    private static char[] toUnicodeArray_;
    private static char[] fromUnicodeArray_;

    static {
        toUnicodeArray_ = toUnicode_.toCharArray();
        fromUnicodeArray_ = fromUnicode_.toCharArray();
    }

    ConvTable918() {
        super(918, toUnicodeArray_, fromUnicodeArray_);
    }


    ConvTable918(int ccsid) {
        super(ccsid, toUnicodeArray_, fromUnicodeArray_);
    }
}
