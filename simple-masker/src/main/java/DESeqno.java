
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

/**
 *
 * DEUserExitSample is an implementation of the UserExitIF
 */
public class DESeqno implements DEUserExitIF {

    public static final String VERSION =
        "DESeqno 1.0 2020-09-21";

    private long seqno = 0;

    /**
     * Does not require any arguments.
     * Returns the incremental counter.
     *
     * @param aobjList Object[]
     * @return Integer as Object
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvalidArgumentException
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvokeException
     */
    @Override
    public Object invoke(Object[] aobjList)
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        if (seqno < 1L) {
            // First time call - initialize the counter
            seqno = System.currentTimeMillis();
        }
        return Long.toString(++seqno);
    }
}
