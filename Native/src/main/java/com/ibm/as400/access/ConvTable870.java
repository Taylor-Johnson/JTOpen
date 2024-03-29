///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename:  ConvTable870.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2004 International Business Machines Corporation and
// others.  All rights reserved.
//
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable870 extends ConvTableSingleMap {
    private static final String copyright = "Copyright (C) 1997-2004 International Business Machines Corporation and others.";

    private static final String toUnicode_ =
            "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F\u0097\u008D\u008E\u000B\f\r\u000E\u000F" +
                    "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +
                    "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +
                    "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +
                    "\u0020\u00A0\u00E2\u00E4\u0163\u00E1\u0103\u010D\u00E7\u0107\u005B\u002E\u003C\u0028\u002B\u0021" +
                    "\u0026\u00E9\u0119\u00EB\u016F\u00ED\u00EE\u013E\u013A\u00DF\u005D\u0024\u002A\u0029\u003B\u005E" +
                    "\u002D\u002F\u00C2\u00C4\u02DD\u00C1\u0102\u010C\u00C7\u0106\u007C\u002C\u0025\u005F\u003E\u003F" +
                    "\u02C7\u00C9\u0118\u00CB\u016E\u00CD\u00CE\u013D\u0139\u0060\u003A\u0023\u0040\'\u003D\"" +
                    "\u02D8\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u015B\u0148\u0111\u00FD\u0159\u015F" +
                    "\u00B0\u006A\u006B\u006C\u006D\u006E\u006F\u0070\u0071\u0072\u0142\u0144\u0161\u00B8\u02DB\u00A4" +
                    "\u0105\u007E\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u015A\u0147\u0110\u00DD\u0158\u015E" +
                    "\u02D9\u0104\u017C\u0162\u017B\u00A7\u017E\u017A\u017D\u0179\u0141\u0143\u0160\u00A8\u00B4\u00D7" +
                    "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u00AD\u00F4\u00F6\u0155\u00F3\u0151" +
                    "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050\u0051\u0052\u011A\u0171\u00FC\u0165\u00FA\u011B" +
                    "\\\u00F7\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u010F\u00D4\u00D6\u0154\u00D3\u0150" +
                    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u010E\u0170\u00DC\u0164\u00DA\u009F";


    private static final String fromUnicode_ =
            "\u0001\u0203\u372D\u2E2F\u1605\u250B\u0C0D\u0E0F\u1011\u1213\u3C3D\u3226\u1819\u3F27\u1C1D\u1E1F" +
                    "\u404F\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F" +
                    "\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE94A\uE05A\u5F6D" +
                    "\u7981\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u6AD0\uA107" +
                    "\u2021\u2223\u2415\u0617\u2829\u2A2B\u2C09\u0A1B\u3031\u1A33\u3435\u3608\u3839\u3A3B\u0414\u3EFF" +
                    "\u413F\u3F3F\u9F3F\u3FB5\uBD3F\u3F3F\u3FCA\u3F3F\u903F\u3F3F\uBE3F\u3F3F\u9D3F\u3F3F\u3F3F\u3F3F" +
                    "\u3F65\u623F\u633F\u3F68\u3F71\u3F73\u3F75\u763F\u3F3F\u3FEE\uEB3F\uECBF\u3F3F\uFE3F\uFCAD\u3F59" +
                    "\u3F45\u423F\u433F\u3F48\u3F51\u3F53\u3F55\u563F\u3F3F\u3FCE\uCB3F\uCCE1\u3F3F\uDE3F\uDC8D\u3F3F" +
                    "\u3F3F\u6646\uB1A0\u6949\u3F3F\u3F3F\u6747\uFAEA\uAC8C\u3F3F\u3F3F\u3F3F\u7252\uDADF\uFFFF\u000E" +
                    "\u3F3F\u3F78\u583F\u3F77\u573F\u3FBA\u9ABB\u9B3F\u3FAB\u8B3F\u3F3F\u3F3F\u3F3F\uEFCF\u3F3F\uEDCD" +
                    "\u3F3F\uAE8E\uAA8A\u3F3F\uAF8F\uBC9C\uB344\uFDDD\uFFFF\u0004\u3F3F\u7454\uFBDB\u3F3F\u3F3F\u3F3F" +
                    "\u3FB9\uB7B4\uB2B8\uB63F\uFFFF\u00A3\u3F3F\u3F70\uFFFF\b\u3F3F\u80B0\u3F9E\u3F64\uFFFF\u7E91" +
                    "\u3F3F";


    ConvTable870() {
        super(870, toUnicode_.toCharArray(), fromUnicode_.toCharArray());

        // Fix up mappings to match system behavior @V5A
        super.fromUnicode_[0x00d0] = super.fromUnicode_[0x0110];  /* capital letter eth to capital D with stroke   */

    }
}
