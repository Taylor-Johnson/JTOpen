///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  ConvTable1166.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2016 International Business Machines Corporation and
// others.  All rights reserved.
//
// Generated Fri Apr 08 13:58:31 CDT 2016 from sq730
// Using Open Source Software, JTOpen 9.0, codebase 5770-SS1 V7R3M0.00 built=20160408 @R1
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable1166 extends ConvTableSingleMap {
    private static final String copyright = "Copyright (C) 1997-2016 International Business Machines Corporation and others.";
    private static final String toUnicode_ =
            "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F\u0097\u008D\u008E\u000B\f\r\u000E\u000F" +
                    "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +
                    "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +
                    "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +
                    "\u0020\u00A0\u04D9\u0493\u0451\u0454\u0455\u0456\u049B\u0458\u005B\u002E\u003C\u0028\u002B\u0021" +
                    "\u0026\u04A3\u04E9\u04B1\u04AF\u045E\u04BB\u042A\u2116\u04D8\u005D\u0024\u002A\u0029\u003B\u005E" +
                    "\u002D\u002F\u0492\u0401\u0404\u0405\u0406\u049A\u0408\u04A2\u007C\u002C\u0025\u005F\u003E\u003F" +
                    "\u04E8\u04B0\u04AE\u00AD\u040E\u04BA\u044E\u0430\u0431\u0060\u003A\u0023\u0040\'\u003D\"" +
                    "\u0446\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u0434\u0435\u0444\u0433\u0445\u0438" +
                    "\u0439\u006A\u006B\u006C\u006D\u006E\u006F\u0070\u0071\u0072\u043A\u043B\u043C\u043D\u043E\u043F" +
                    "\u044F\u007E\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u0440\u0441\u0442\u0443\u0436\u0432" +
                    "\u044C\u044B\u0437\u0448\u044D\u0449\u0447\u044A\u042E\u0410\u0411\u0426\u0414\u0415\u0424\u0413" +
                    "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u0425\u0418\u0419\u041A\u041B\u041C" +
                    "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050\u0051\u0052\u041D\u041E\u041F\u042F\u0420\u0421" +
                    "\\\u20AC\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u0422\u0423\u0416\u0412\u042C\u042B" +
                    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u0417\u0428\u042D\u0429\u0427\u009F";
    private static final String fromUnicode_ =
            "\u0001\u0203\u372D\u2E2F\u1605\u250B\u0C0D\u0E0F\u1011\u1213\u3C3D\u3226\u1819\u3F27\u1C1D\u1E1F" +
                    "\u404F\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F" +
                    "\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE94A\uE05A\u5F6D" +
                    "\u7981\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u6AD0\uA107" +
                    "\u2021\u2223\u2415\u0617\u2829\u2A2B\u2C09\u0A1B\u3031\u1A33\u3435\u3608\u3839\u3A3B\u0414\u3EFF" +
                    "\u413F\uFFFF\u0005\u3F3F\u3F73\uFFFF\u01A9\u3F3F\u3F63\u3F3F\u6465\u663F\u683F\u3F3F\u3F3F\u743F" +
                    "\uB9BA\uEDBF\uBCBD\uECFA\uCBCC\uCDCE\uCFDA\uDBDC\uDEDF\uEAEB\uBECA\uBBFE\uFBFD\u57EF\uEEFC\uB8DD" +
                    "\u7778\uAF8D\u8A8B\uAEB2\u8F90\u9A9B\u9C9D\u9E9F\uAAAB\uACAD\u8C8E\u80B6\uB3B5\uB7B1\uB0B4\u76A0" +
                    "\u3F44\u3F3F\u4546\u473F\u493F\u3F3F\u3F3F\u553F\uFFFF\u0019\u3F3F\u6243\u3F3F\u3F3F\u3F3F\u6748" +
                    "\u3F3F\u3F3F\u3F3F\u6951\uFFFF\u0005\u3F3F\u7254\u7153\uFFFF\u0004\u3F3F\u7556\uFFFF\u000E\u3F3F" +
                    "\u5942\uFFFF\u0007\u3F3F\u7052\uFFFF\u0DE1\u3F3F\uE13F\uFFFF\u0034\u3F3F\u583F\uFFFF\u6EF4\u3F3F" +
                    "\u0001\u0203\u0405\u0607\u0809\u0A0B\u0C0D\u0E0F\u1011\u1213\u1415\u1617\u1819\u1A1B\u1C1D\u1E1F" +
                    "\u2021\u2223\u2425\u2627\u2829\u2A2B\u2C2D\u2E2F\u3031\u3233\u3435\u3637\u3839\u3A3B\u3C3D\u3E3F" +
                    "\u4041\u4243\u4445\u4647\u4849\uBA4B\u4C4D\u4E5A\u5051\u5253\u5455\u5657\u5859\uBB5B\u5C5D\u5EB0" +
                    "\u6061\u6263\u6465\u6667\u6869\u4F6B\u6C6D\u6E6F\u7071\u72CA\u7475\u7677\u7879\u7A7B\u7C7D\u7E7F" +
                    "\u8081\u8283\u8485\u8687\u8889\u8A8B\u8C8D\u8E8F\u9091\u9293\u9495\u9697\u9899\u9A9B\u9C9D\u9EE1" +
                    "\uA0A1\uA2A3\uA4A5\uA6A7\uA8A9\uAAAB\uACAD\uAEAF\u5FB1\uB2B3\uB4B5\uB6B7\uB8B9\u4A6A\uBCBD\uBEBF" +
                    "\uC0C1\uC2C3\uC4C5\uC6C7\uC8C9\u73CB\uCCCD\uCECF\uD0D1\uD2D3\uD4D5\uD6D7\uD8D9\uDADB\uDCDD\uDEDF" +
                    "\uE09F\uE2E3\uE4E5\uE6E7\uE8E9\uEAEB\uECED\uEEEF\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\uFAFB\uFCFD\uFEFF";
    private static char[] toUnicodeArray_;
    private static char[] fromUnicodeArray_;

    static {
        toUnicodeArray_ = toUnicode_.toCharArray();
        fromUnicodeArray_ = fromUnicode_.toCharArray();
    }

    ConvTable1166() {
        super(1166, toUnicodeArray_, fromUnicodeArray_);
    }


    ConvTable1166(int ccsid) {
        super(ccsid, toUnicodeArray_, fromUnicodeArray_);
    }
}
