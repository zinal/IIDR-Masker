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

import java.math.BigDecimal;
import com.datamirror.ts.derivedexpressionmanager.*;

/**
 * Computes the integer division remainder in IBM CDC.
 * This code is provided "as is", without warranty of any kind.
 *
 * Put the CdcMod.class into {cdc-install-dir}/lib
 *
 * javac CdcMod.java -classpath ts.jar
 *
 * %USERFUNC("JAVA","CdcMod", COLNAME, 10)
 */
public class CdcMod implements DEUserExitIF {

    public static final String VERSION =
            "CdcMod 1.0 2020-12-29.A";

    /**
     * Computes the integer division remainder
     *
     * @param args Object[]
     * @return String as Object
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvalidArgumentException
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvokeException
     */
    @Override
    public Object invoke(Object[] args)
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        if (args.length != 2) {
            throw new UserExitInvalidArgumentException(getClass().getName()
                    + ": expects two arguments on input");
        }
        // Handle null input values
        if ( args[1] == null )
            return null;
        // Perform the conversion
        try {
            final Number val1 = toNumber(args[0]);
            final Number val2 = toNumber(args[1]);
            return val1.longValue() % val2.longValue();
        } catch(Exception ex) {
            throw new UserExitInvokeException(getClass().getName()
                + ": call failed - " + ex.toString());
        }
    }
    
    private static Number toNumber(Object o) {
        if (o==null)
            return null;
        if (o instanceof Number) {
            return (Number) o;
        } else {
            return new BigDecimal(o.toString());
        }
    }
}
