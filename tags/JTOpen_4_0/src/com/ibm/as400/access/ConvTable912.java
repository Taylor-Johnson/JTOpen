///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: ConvTable912.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable912 extends ConvTableAsciiMap
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

  private static final String toUnicode_ = 
    "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000B\f\r\u000E\u000F" +
    "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" +
    "\u0020\u0021\"\u0023\u0024\u0025\u0026\'\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F" +
    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F" +
    "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F" +
    "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F" +
    "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F" +
    "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u007F" +
    "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F" +
    "\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F" +
    "\u00A0\u0104\u02D8\u0141\u00A4\u013D\u015A\u00A7\u00A8\u0160\u015E\u0164\u0179\u00AD\u017D\u017B" +
    "\u00B0\u0105\u02DB\u0142\u00B4\u013E\u015B\u02C7\u00B8\u0161\u015F\u0165\u017A\u02DD\u017E\u017C" +
    "\u0154\u00C1\u00C2\u0102\u00C4\u0139\u0106\u00C7\u010C\u00C9\u0118\u00CB\u011A\u00CD\u00CE\u010E" +
    "\u0110\u0143\u0147\u00D3\u00D4\u0150\u00D6\u00D7\u0158\u016E\u00DA\u0170\u00DC\u00DD\u0162\u00DF" +
    "\u0155\u00E1\u00E2\u0103\u00E4\u013A\u0107\u00E7\u010D\u00E9\u0119\u00EB\u011B\u00ED\u00EE\u010F" +
    "\u0111\u0144\u0148\u00F3\u00F4\u0151\u00F6\u00F7\u0159\u016F\u00FA\u0171\u00FC\u00FD\u0163\u02D9";


  private static final String fromUnicode_ = 
    "\u0001\u0203\u0405\u0607\u0809\u0A0B\u0C0D\u0E0F\u1011\u1213\u1415\u1617\u1819\u1A1B\u1C1D\u1E1F" +
    "\u2021\u2223\u2425\u2627\u2829\u2A2B\u2C2D\u2E2F\u3031\u3233\u3435\u3637\u3839\u3A3B\u3C3D\u3E3F" +
    "\u4041\u4243\u4445\u4647\u4849\u4A4B\u4C4D\u4E4F\u5051\u5253\u5455\u5657\u5859\u5A5B\u5C5D\u5E5F" +
    "\u6061\u6263\u6465\u6667\u6869\u6A6B\u6C6D\u6E6F\u7071\u7273\u7475\u7677\u7879\u7A7B\u7C7D\u7E7F" +
    "\u8081\u8283\u8485\u8687\u8889\u8A8B\u8C8D\u8E8F\u9091\u9293\u9495\u9697\u9899\u9A9B\u9C9D\u9E9F" +
    "\uA01A\u1A1A\uA41A\u1AA7\uA81A\u1A1A\u1AAD\u1A1A\uB01A\u1A1A\uB41A\u1A1A\uB81A\u1A1A\u1A1A\u1A1A" +
    "\u1AC1\uC21A\uC41A\u1AC7\u1AC9\u1ACB\u1ACD\uCE1A\u1A1A\u1AD3\uD41A\uD6D7\u1A1A\uDA1A\uDCDD\u1ADF" +
    "\u1AE1\uE21A\uE41A\u1AE7\u1AE9\u1AEB\u1AED\uEE1A\u1A1A\u1AF3\uF41A\uF6F7\u1A1A\uFA1A\uFCFD\u1A1A" +
    "\u1A1A\uC3E3\uA1B1\uC6E6\u1A1A\u1A1A\uC8E8\uCFEF\uD0F0\u1A1A\u1A1A\u1A1A\uCAEA\uCCEC\uFFFF\u000E" +
    "\u1A1A\u1AC5\uE51A\u1AA5\uB51A\u1AA3\uB3D1\uF11A\u1AD2\uF21A\u1A1A\u1A1A\u1A1A\uD5F5\u1A1A\uC0E0" +
    "\u1A1A\uD8F8\uA6B6\u1A1A\uAABA\uA9B9\uDEFE\uABBB\uFFFF\u0004\u1A1A\uD9F9\uDBFB\u1A1A\u1A1A\u1A1A" +
    "\u1AAC\uBCAF\uBFAE\uBE1A\uFFFF\u00A3\u1A1A\u1AB7\uFFFF\b\u1A1A\uA2FF\u1AB2\u1ABD\uFFFF\u7E91" +
    "\u1A1A";


  ConvTable912()
  {
    super(912, toUnicode_.toCharArray(), fromUnicode_.toCharArray());
  }
}
