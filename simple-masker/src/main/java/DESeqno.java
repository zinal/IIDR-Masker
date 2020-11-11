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
import java.util.concurrent.atomic.AtomicLong;

/**
 * %USERFUNC("JAVA","DESeqno",null).
 */
public class DESeqno implements DEUserExitIF {

    public static final String VERSION =
        "DESeqno v1.1 2020-10-13.D mzinal";

    private final static long LOAD_TIME = System.currentTimeMillis();
    private final static AtomicLong COUNTER = new AtomicLong();

    public DESeqno() {
    }

    /**
     * Does not require any arguments.
     * Returns the incremental counter.
     *
     * @param aobjList Object[]
     * @return long converted to String as Object
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvalidArgumentException
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvokeException
     */
    @Override
    public Object invoke(Object[] aobjList)
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        final long seqno = LOAD_TIME + COUNTER.incrementAndGet();
        /*
        System.out.println("DESeqno T" + String.valueOf(Thread.currentThread().getId())
                + " O" + System.identityHashCode(this)
                + " NEXT " + Long.toString(seqno));
        */
        return Long.toString(seqno);
    }

}
