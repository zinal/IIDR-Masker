package iidr.samples.tests;

import com.datamirror.ts.derivedexpressionmanager.DEUserExitIF;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author zinal
 */
public class CdcHasherTest {

    public CdcHasherTest() {
    }

    @Test
    public void testOne() throws Exception {
        final String algo = "SHA-256";
        final MessageDigest md = MessageDigest.getInstance(algo);
        DEUserExitIF x = (DEUserExitIF) Class.forName("CdcHasher").newInstance();
        long tvStart = System.currentTimeMillis();
        for ( int i=0; i<1000; ++i) {
            byte[] input = ("софтверГаврилаСочинял#" + String.valueOf(i))
                    .getBytes(StandardCharsets.UTF_8);
            Object output = x.invoke(new Object[] {algo, input, 0});
            Assert.assertNotNull(output);
            Assert.assertEquals(output.getClass(), algo.getClass());
        }
        long tvFinish = System.currentTimeMillis();
        System.out.println("CdcHasher time: " + (tvFinish - tvStart));
    }

}
