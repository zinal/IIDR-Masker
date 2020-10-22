package iidr.samples.tests;

import com.datamirror.ts.derivedexpressionmanager.DEUserExitIF;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;
import org.junit.Assert;
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
        for ( int i=0; i<1000; ++i) {
            byte[] input = ("служилГаврилаПрограммистом#" + String.valueOf(i))
                    .getBytes(StandardCharsets.UTF_8);
            Object output = x.invoke(new Object[] {input});
            Assert.assertNotNull(output);
            Assert.assertEquals(output.getClass(), String.class);
            Assert.assertEquals(output.toString().length(), 2 * input.length);
            Assert.assertEquals(output.toString().toLowerCase(),
                    DatatypeConverter.printHexBinary(input).toLowerCase());
        }
    }

}
