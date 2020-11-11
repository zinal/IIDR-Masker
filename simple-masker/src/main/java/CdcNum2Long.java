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
import java.math.BigDecimal;

/**
 * Converting the numbers to 64-bit integers in IBM CDC.
 * This code is provided "as is", without warranty of any kind.
 *
 * Put the CdcNum2Long.class into {cdc-install-dir}/lib
 *
 * javac CdcNum2Long.java -classpath ts.jar
 *
 * %USERFUNC("JAVA","CdcNum2Long", COLNAME)
 */
public class CdcNum2Long implements DEUserExitIF {

    public static final String VERSION =
            "CdcNum2Long 1.1 2020-04-21.A";

    /**
     * Converts the input valus to 64-bit integers
     *
     * @param args Object[]
     * @return String as Object
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvalidArgumentException
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvokeException
     */
    @Override
    public Object invoke(Object[] args)
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        if (args.length != 1) {
            throw new UserExitInvalidArgumentException(getClass().getName()
                    + ": expects a single argument on input");
        }
        // Handle null input values
        if ( args[1] == null )
            return null;
        // Perform the conversion
        try {
            final Number val;
            if (args[0] instanceof Number) {
                val = (Number) args[0];
            } else {
                val = new BigDecimal(args[0].toString());
            }
            return val.longValue();
        } catch(Exception ex) {
            throw new UserExitInvokeException(getClass().getName()
                + ": call failed - " + ex.toString());
        }

    }
}
