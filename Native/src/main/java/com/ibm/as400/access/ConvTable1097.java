///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  ConvTable1097.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2016 International Business Machines Corporation and
// others.  All rights reserved.
//
// Generated Wed Dec 05 14:16:35 CST 2018 from sq730
// Using Open Source Software, JTOpen 9.7, codebase 5770-SS1 V7R3M0.00 built=20181118 @Y0
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable1097 extends ConvTableSingleMap {
    private static final String copyright = "Copyright (C) 1997-2016 International Business Machines Corporation and others.";
    // toUnicode_ length is 256
    private static final String toUnicode_ =
            "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F\u0097\u008D\u008E\u000B\f\r\u000E\u000F" +
                    "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +
                    "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +
                    "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +
                    "\u0020\u00A0\u060C\u064B\uFE81\uFE82\uF8FA\uFE8D\uFE8E\uF8FB\u00A4\u002E\u003C\u0028\u002B\u007C" +
                    "\u0026\uFE80\uFE83\uFE84\uF8F9\uFE85\uFE8B\uFE8F\uFE91\uFB56\u0021\u0024\u002A\u0029\u003B\u00AC" +
                    "\u002D\u002F\uFB58\uFE95\uFE97\uFE99\uFE9B\uFE9D\uFE9F\uFB7A\u061B\u002C\u0025\u005F\u003E\u003F" +
                    "\uFB7C\uFEA1\uFEA3\uFEA5\uFEA7\uFEA9\uFEAB\uFEAD\uFEAF\u0060\u003A\u0023\u0040\'\u003D\"" +
                    "\uFB8A\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u00AB\u00BB\uFEB1\uFEB3\uFEB5\uFEB7" +
                    "\uFEB9\u006A\u006B\u006C\u006D\u006E\u006F\u0070\u0071\u0072\uFEBB\uFEBD\uFEBF\uFEC1\uFEC3\uFEC5" +
                    "\uFEC7\u007E\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\uFEC9\uFECA\uFECB\uFECC\uFECD\uFECE" +
                    "\uFECF\uFED0\uFED1\uFED3\uFED5\uFED7\uFB8E\uFEDB\uFB92\uFB94\u005B\u005D\uFEDD\uFEDF\uFEE1\u00D7" +
                    "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u00AD\uFEE3\uFEE5\uFEE7\uFEED\uFEE9" +
                    "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050\u0051\u0052\uFEEB\uFEEC\uFBA4\uFBFC\uFBFD\uFBFE" +
                    "\\\u061F\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u0640\u06F0\u06F1\u06F2\u06F3\u06F4" +
                    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u06F5\u06F6\u06F7\u06F8\u06F9\u009F";
    // fromUnicode length = 291
    private static final String fromUnicode_ =
            "\u0001\u0203\u372D\u2E2F\u1605\u250B\u0C0D\u0E0F\u1011\u1213\u3C3D\u3226\u1819\u3F27\u1C1D\u1E1F" +
                    "\u405A\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F" +
                    "\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE9BA\uE0BB\u3F6D" +
                    "\u7981\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u4FD0\uA107" +
                    "\u2021\u2223\u2415\u0617\u2829\u2A2B\u2C09\u0A1B\u3031\u1A33\u3435\u3608\u3839\u3A3B\u0414\u3EFF" +
                    "\u413F\u3F3F\u4A3F\u3F3F\u3F3F\u3F8A\u5FCA\uFFFF\u0006\u3F3F\u3F8B\uFFFF\r\u3F3F\u3FBF\uFFFF" +
                    "\u029A\u3F3F\u423F\uFFFF\u0006\u3F3F\u3F6A\u3F3F\u3FE1\u3F51\u4452\u553F\u3F47\u573F\u6365\u6771" +
                    "\u7375\u7677\u788C\u8E90\u9B3F\u3FAA\uAE3F\u3F3F\u3F3F\uEAB2\uB43F\uBCBE\uCCCF\uCE3F\u3F43\uFFFF" +
                    "\n\u3F3F\uEBEC\uEDEE\uEFFA\uFBFC\uFDFE\u6C6B\u4B5C\uFFFF\u0041\u3F3F\uEBEC\uEDEE\uEFFA\uFBFC" +
                    "\uFDFE\uFFFF\u78FF\u3F3F\u3F54\u4649\uFFFF\u012D\u3F3F\u593F\u623F\uFFFF\u0010\u3F3F\u693F\u703F" +
                    "\uFFFF\u0006\u3F3F\u803F\u3F3F\uB63F\u3F3F\uB83F\uB93F\uFFFF\u0007\u3F3F\uDC3F\uFFFF\u002B\u3F3F" +
                    "\uDDDE\uDF3F\uFFFF\u0140\u3F3F\u5144\u4552\u5355\u553F\u3F3F\u3F56\u5647\u4857\u5758\u583F\u3F63" +
                    "\u6364\u6465\u6566\u6667\u6768\u6871\u7172\u7273\u7374\u7475\u7576\u7677\u7778\u788C\u8C8D\u8D8E" +
                    "\u8E8F\u8F90\u909A\u9A9B\u9B9C\u9C9D\u9D9E\u9E9F\u9FA0\uA0AA\uABAC\uADAE\uAFB0\uB1B2\uB2B3\uB3B4" +
                    "\uB4B5\uB53F\u3FB7\uB7BC\uBCBD\uBDBE\uBECB\uCBCC\uCCCD\uCDCF\uCFDA\uDBCE\uCE3F\uFFFF\b\u3F3F" +
                    "\u3F5A\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F" +
                    "\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE9BA\uE0BB\u3F6D" +
                    "\u7981\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u4FD0\uA13F" +
                    "\uFFFF\u0050\u3F3F";
    private static char[] toUnicodeArray_;
    private static char[] fromUnicodeArray_;

    static {
        toUnicodeArray_ = toUnicode_.toCharArray();
        fromUnicodeArray_ = fromUnicode_.toCharArray();
    }

    ConvTable1097() {
        super(1097, toUnicodeArray_, fromUnicodeArray_);
    }


    ConvTable1097(int ccsid) {
        super(ccsid, toUnicodeArray_, fromUnicodeArray_);
    }
}
