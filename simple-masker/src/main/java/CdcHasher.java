/** **************************************************************************
 ** Licensed Materials - Property of IBM
 ** IBM InfoSphere Change Data Capture
 ** 5724-U70
 **
 ** (c) Copyright IBM Corp. 2020 All rights reserved.
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
import java.security.MessageDigest;
import java.util.Objects;
import javax.xml.bind.DatatypeConverter;

/**
 * Hash computation for IBM CDC.
 * This code is provided "as is", without warranty of any kind.
 *
 * Put the CdcHasher.class into {cdc-install-dir}/lib
 *
 * javac CdcHasher.java -classpath ts.jar
 *
 * %USERFUNC("JAVA","CdcHasher", "SHA-1", PAN)      -- SHA-1 in hex
 * %USERFUNC("JAVA","CdcHasher", "SHA-1", PAN, 0)   -- SHA-1 in hex
 * %USERFUNC("JAVA","CdcHasher", "SHA-1", PAN, 1)   -- SHA-1 in base64
 */
public class CdcHasher implements DEUserExitIF {

    public static final String VERSION =
            "CdcHasher 1.2 2020-10-22";

    private String mdType = null;
    private MessageDigest mdVal = null;

    /**
     * Calculates the hash
     *
     * @param args Object[]
     * @return String as Object
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvalidArgumentException
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvokeException
     */
    @Override
    public Object invoke(Object[] args)
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        // 2 Arguments expected - algorithm type and value to hash
        if (args.length < 2) {
            throw new UserExitInvalidArgumentException(getClass().getName()
                    + ": insufficient number of arguments, "
                    + "expects a hash type and a String value to hash");
        }
        // Handle null input values
        if ( args[1] == null )
            return null;
        // Algorithm type
        if (!(args[0] instanceof String)) {
            throw new UserExitInvalidArgumentException(getClass().getName()
                    + ": The algorithm type must be a String");
        }
        final String algo = (String) args[0];
        // Value to hash
        byte[] value;
        final Object src = args[1];
        if (src==null) {
            value = new byte[0];
        } else if (src instanceof byte[]) {
            value = (byte[]) src;
        } else {
            value = src.toString().getBytes(StandardCharsets.UTF_8);
        }
        // Output type
        final boolean useBase64 = (args.length > 2) && parseFlag(args[2]);
        // Compute hash and built the output
        try {
            if (mdVal==null || !Objects.equals(algo, mdType)) {
                mdType = algo;
                mdVal = MessageDigest.getInstance(mdType);
            } else {
                mdVal.reset();
            }
            final byte[] hashValue = mdVal.digest(value);
            if (useBase64) {
                // More compact base64 encoding
                return DatatypeConverter.printBase64Binary(hashValue);
            } else {
                // Hex encoding
                return printHex(hashValue);
            }
        } catch(Exception ex) {
            throw new UserExitInvokeException(getClass().getName()
                + ": call failed - " + ex.toString());
        }
    }

    private static final char[] DIGITS
            = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

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

    protected static boolean parseFlag(Object flag) {
        if (flag instanceof Number) {
            Number n = (Number) flag;
            if (n.intValue() != 0)
                return true;
        } else if (flag instanceof String) {
            String s = flag.toString().trim();
            if (s.length() > 0) {
                switch (s.charAt(0)) {
                    case '1': case 'y': case 'Y': case 't': case 'T':
                        return true;
                }
            }
        }
        return false;
    }

}
