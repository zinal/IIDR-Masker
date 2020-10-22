package iidr.samples.tests;

import com.datamirror.ts.derivedexpressionmanager.DEUserExitIF;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
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
        final String algo = "SHA-1";
        final MessageDigest md = MessageDigest.getInstance(algo);
        DEUserExitIF x = (DEUserExitIF) Class.forName("CdcHasher").newInstance();
        for ( int i=0; i<1000; ++i) {
            byte[] input = ("служилГаврилаПрограммистом#" + String.valueOf(i))
                    .getBytes(StandardCharsets.UTF_8);
            Object output = x.invoke(new Object[] {algo, input});
            Assert.assertNotNull(output);
            Assert.assertEquals(output.getClass(), String.class);
            byte[] digest = md.digest(input);
            Assert.assertEquals(output.toString().toLowerCase(),
                    DatatypeConverter.printHexBinary(digest).toLowerCase());
        }
    }

    @Test
    public void testTwo() throws Exception {
        final String algo = "SHA-1";
        final MessageDigest md = MessageDigest.getInstance(algo);
        DEUserExitIF x = (DEUserExitIF) Class.forName("CdcHasher").newInstance();
        for ( int i=0; i<1000; ++i) {
            byte[] input = ("софтверГаврилаСочинял#" + String.valueOf(i))
                    .getBytes(StandardCharsets.UTF_8);
            Object output = x.invoke(new Object[] {algo, input, 1});
            Assert.assertNotNull(output);
            Assert.assertEquals(output.getClass(), String.class);
            byte[] digest = md.digest(input);
            Assert.assertEquals(output.toString(),
                    DatatypeConverter.printBase64Binary(digest));
        }
    }

}
