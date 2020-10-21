
/** **************************************************************************
 ** Licensed Materials - Property of IBM
 ** IBM InfoSphere Change Data Capture
 ** 5724-U70
 **
 ** (c) Copyright IBM Corp. 2011 All rights reserved.
 **
 ** The following sample of source code ("Sample") is owned by International
 ** Business Machines Corporation or one of its subsidiaries ("IBM") and is
 ** copyrighted and licensed, not sold. You may use, copy, modify, and
 ** distribute the Sample in any form without payment to IBM.
 **
 ** The Sample code is provided to you on an "AS IS" basis, without warranty of
 ** any kind. IBM HEREBY EXPRESSLY DISCLAIMS ALL WARRANTIES, EITHER EXPRESS OR
 ** IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. Some jurisdictions do
 ** not allow for the exclusion or limitation of implied warranties, so the above
 ** limitations or exclusions may not apply to you. IBM shall not be liable for
 ** any damages you suffer as a result of using, copying, modifying or
 ** distributing the Sample, even if IBM has been advised of the possibility of
 ** such damages.
 **************************************************************************** */

import com.datamirror.ts.derivedexpressionmanager.*;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;

/**
 * Convert binary values to strings for IBM CDC.
 * This code is provided "as is", without warranty of any kind.
 *
 * Put the CdcBin2Str.class into {cdc-install-dir}/lib
 *
 * javac CdcBin2Str.java -classpath ts.jar
 *
 * %USERFUNC("JAVA","CdcBin2Str", uuid)        -- hex conversion
 * %USERFUNC("JAVA","CdcBin2Str", uuid, 0)     -- hex conversion
 * %USERFUNC("JAVA","CdcBin2Str", uuid, 1)     -- base64 conversion
 */
public class CdcBin2Str implements DEUserExitIF {

    public static final String VERSION =
            "CdcBin2Str 1.2 2020-10-21";

    /**
     * Perform the conversion
     *
     * @param args Object[]
     * @return String as Object
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvalidArgumentException
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvokeException
     */
    @Override
    public Object invoke(Object[] args)
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        // 1 argument expected - the value to be converted
        if (args.length < 1) {
            throw new UserExitInvalidArgumentException(getClass().getName()
                    + ": insufficient number of arguments, "
                    + "expects a value to be converted");
        }
        // Handle null input values
        final Object src = args[0];
        if ( src == null )
            return null;
        // Algorithm type
        boolean useBase64 = false;
        if (args.length > 1) {
            final Object flag = args[1];
            if (flag instanceof Number) {
                Number n = (Number) flag;
                if (n.intValue() != 0)
                    useBase64 = true;
            } else if (flag instanceof String) {
                String s = flag.toString().trim();
                if (s.length() > 0) {
                    switch(s.charAt(0)) {
                        case '1': case 'y': case 'Y': case 't': case 'T':
                            useBase64 = true;
                            break;
                    }
                }
            }
        }
        byte[] value;
        if (src instanceof byte[]) {
            value = (byte[]) src;
        } else {
            value = src.toString().getBytes(StandardCharsets.UTF_8);
        }
        if (useBase64) {
            // More compact base64 encoding
            return DatatypeConverter.printBase64Binary(value);
        } else {
            // HEX encoding
            return printHex(value);
        }
    }

   private static final char[] DIGITS =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    protected static String printHex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xf0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0f & data[i]];
        }
        return new String(out);
    }

}
