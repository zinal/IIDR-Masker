/****************************************************************************
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
*****************************************************************************/

import com.datamirror.ts.derivedexpressionmanager.*;
import com.datamirror.ts.target.publication.userexit.*;
/**
 *
 * DEUserExitSample is an implementation of the UserExitIF
 */
public class DESeqno implements DEUserExitIF
{
	boolean firstTime = true;
	long    seqno = 0;
   /**
    * User exit <b>must have</b> constructor with no arguments.
    * Though it would exist by default, it is adduced here for clearness.
    */
   public DESeqno()
   {
	   
   }

   /**
    * Expects arguments to be of "Number" data type
    * (Integer, Long, Double etc.). Sums their integer values
    * and returns the sum incremented by 1.
    *
    * @param   aobjArgsList  Object[]
    * @return    Integer as Object
    */
   public Object invoke(Object[] aobjList) throws UserExitInvalidArgumentException, UserExitInvokeException
   {
	   UserExitEnvironment env = UserExitEnvironment.getEnvironment();
	   
      // First check that there is at least one input argument
      if (firstTime)
      {
    	  seqno = System.currentTimeMillis();
    	  firstTime = false;
      }
      seqno++;

      String result = Long.toString(seqno);

      return result;
   }
}