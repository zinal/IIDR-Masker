import com.datamirror.ts.derivedexpressionmanager.*;
import java.nio.charset.Charset;
import javax.xml.bind.DatatypeConverter;

/**
 * Convert binary values to strings for IBM CDC.
 * This code is provided "as is", without warranty of any kind.
 * 
 * Put the CdcBin2Str.class into {cdc-install-dir}/lib
 * 
 * javac CdcBin2Str.java -classpath ts.jar
 * 
 * %USERFUNC("JAVA","CdcBin2Str", uuid)        -- hex conversion
 * %USERFUNC("JAVA","CdcBin2Str", uuid, 0)     -- hex conversion
 * %USERFUNC("JAVA","CdcBin2Str", uuid, 1)     -- base64 conversion
 */
public class CdcBin2Str implements DEUserExitIF {

    public static final String VERSION = 
            "CdcBin2Str 1.0 2020-03-06";

    private static final Charset CS = Charset.forName("UTF-8");

    /**
     * Perform the conversion
     * 
     * @param args Object[]
     * @return String as Object
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvalidArgumentException
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvokeException
     */
    @Override
    public Object invoke(Object[] args) 
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        // 1 argument expected - the value to be converted
        if (args.length < 1) {
            throw new UserExitInvalidArgumentException(getClass().getName() 
                    + ": insufficient number of arguments, "
                    + "expects a value to be converted");
        }
        // Handle null input values
        final Object src = args[0];
        if ( src == null )
            return null;
        // Algorithm type
        boolean useBase64 = false;
        if (args.length > 1) {
            final Object flag = args[1];
            if (flag instanceof Number) {
                Number n = (Number) flag;
                if (n.intValue() != 0)
                    useBase64 = true;
            } else if (flag instanceof String) {
                String s = flag.toString().trim();
                if (s.length() > 0) {
                    switch(s.charAt(0)) {
                        case '1': case 'y': case 'Y': case 't': case 'T':
                            useBase64 = true;
                            break;
                    }
                }
            }
        }
        byte[] value;
        if (src instanceof byte[]) {
            value = (byte[]) src;
        } else {
            value = src.toString().getBytes(CS);
        }
        if (useBase64) {
            // More compact base64 encoding
            return DatatypeConverter.printBase64Binary(value);
        } else {
            // HEX encoding
            final StringBuilder sb = new StringBuilder();
            for (byte b : value) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        }
    }
}
