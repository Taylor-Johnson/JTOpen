///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  ConvTable1154.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2004 International Business Machines Corporation and
// others.  All rights reserved.
//
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable1154 extends ConvTableSingleMap {
    private static final String copyright = "Copyright (C) 1997-2004 International Business Machines Corporation and others.";

    private static final String toUnicode_ =
            "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F\u0097\u008D\u008E\u000B\f\r\u000E\u000F" +
                    "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +
                    "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +
                    "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +
                    "\u0020\u00A0\u0452\u0453\u0451\u0454\u0455\u0456\u0457\u0458\u005B\u002E\u003C\u0028\u002B\u0021" +
                    "\u0026\u0459\u045A\u045B\u045C\u045E\u045F\u042A\u2116\u0402\u005D\u0024\u002A\u0029\u003B\u005E" +
                    "\u002D\u002F\u0403\u0401\u0404\u0405\u0406\u0407\u0408\u0409\u007C\u002C\u0025\u005F\u003E\u003F" +
                    "\u040A\u040B\u040C\u00AD\u040E\u040F\u044E\u0430\u0431\u0060\u003A\u0023\u0040\'\u003D\"" +
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
                    "\u413F\uFFFF\u0005\u3F3F\u3F73\uFFFF\u01A9\u3F3F\u3F63\u5962\u6465\u6667\u6869\u7071\u723F\u7475" +
                    "\uB9BA\uEDBF\uBCBD\uECFA\uCBCC\uCDCE\uCFDA\uDBDC\uDEDF\uEAEB\uBECA\uBBFE\uFBFD\u57EF\uEEFC\uB8DD" +
                    "\u7778\uAF8D\u8A8B\uAEB2\u8F90\u9A9B\u9C9D\u9E9F\uAAAB\uACAD\u8C8E\u80B6\uB3B5\uB7B1\uB0B4\u76A0" +
                    "\u3F44\u4243\u4546\u4748\u4951\u5253\u543F\u5556\uFFFF\u0E26\u3F3F\uE13F\uFFFF\u0034\u3F3F\u583F" +
                    "\uFFFF\u6EF4\u3F3F\u3F4F\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9" +
                    "\u7A5E\u4C7E\u6E6F\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8" +
                    "\uE94A\uE05A\u5F6D\u7981\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8" +
                    "\uA9C0\u6AD0\uA13F\uFFFF\u0050\u3F3F";


    ConvTable1154() {
        super(1154, toUnicode_.toCharArray(), fromUnicode_.toCharArray());
    }
}
