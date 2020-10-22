package iidr.samples.tests;

import com.datamirror.ts.derivedexpressionmanager.DEUserExitIF;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

/**
 *
 * @author zinal
 */
public class CdcBin2StrTest {

    public CdcBin2StrTest() {
    }

    @Test
    public void testOne() throws Exception {
        DEUserExitIF x = (DEUserExitIF) Class.forName("CdcBin2Str").newInstance();
        long tvStart = System.currentTimeMillis();
        for ( int i=0; i<1000; ++i) {
            byte[] input = ("служилГаврилаПрограммистом#" + String.valueOf(i))
                    .getBytes(StandardCharsets.UTF_8);
            x.invoke(new Object[] {input});
        }
        long tvFinish = System.currentTimeMillis();
        System.out.println("CdcBin2Str time: " + (tvFinish - tvStart));
    }

}
