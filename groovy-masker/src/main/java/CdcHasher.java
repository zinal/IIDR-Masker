import com.datamirror.ts.derivedexpressionmanager.*;
import java.security.MessageDigest;
import java.nio.charset.Charset;
import javax.xml.bind.DatatypeConverter;

/**
 * Hash computation for CDC.
 * 
 * Put the CdcHasher.class into {cdc-install-dir}/lib
 * 
 * javac CdcHasher.java -classpath ts.jar
 * 
 * %USERFUNC("JAVA","CdcHasher", "SHA-1", PAN, "passw0rd")
 */
public class CdcHasher implements DEUserExitIF {

    public static final String VERSION = 
            "CdcHasher 1.1 2020-03-06";

    private static final Charset CS = Charset.forName("UTF-8");

    /**
     * Calculates the hash
     * 
     * @param args Object[]
     * @return String as Object
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvalidArgumentException
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvokeException
     */
    @Override
    public Object invoke(Object[] args) 
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        // 2 Arguments expected - algorithm type and value to hash
        if (args.length < 2) {
            throw new UserExitInvalidArgumentException(getClass().getName() 
                    + ": insufficient number of arguments, "
                    + "expects a hash type and a String value to hash");
        }
        // Handle null input values
        if ( args[1] == null )
            return null;
        // Algorithm type
        if (!(args[0] instanceof String)) {
            throw new UserExitInvalidArgumentException(getClass().getName() 
                    + ": The algorithm type must be String");
        }
        String algo = (String) args[0];
        // Value to hash
        byte[] value;
        final Object src = args[1];
        if (src==null) {
            value = new byte[0];
        } else if (src instanceof byte[]) {
            value = (byte[]) src;
        } else {
            value = src.toString().getBytes(CS);
        }
        // Salting key, combined from additional arguments
        String key = null;
        if (args.length > 2) {
            if (args.length==3) {
                if (args[2]!=null)
                    key = args[2].toString();
            } else {
                final StringBuilder tmp = new StringBuilder();
                for (int i=2; i<args.length; ++i) {
                    if (args[i]!=null)
                        tmp.append(args[i].toString());
                }
                key = tmp.toString();
            }
            if (key!=null && key.length()==0)
                key = null;
        }
        
        try {
            final MessageDigest md = MessageDigest.getInstance(algo);
            if (key!=null)
                md.update(key.getBytes(CS));
            final byte[] hashValue = md.digest(value);
            /* 
            // HEX encoding
            final StringBuilder sb = new StringBuilder();
            for (byte b : hashValue) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
            */
            // More compact base64 encoding
            return DatatypeConverter.printBase64Binary(hashValue);
        } catch(Exception ex) {
            throw new UserExitInvokeException(getClass().getName()
                + ": call failed - " + ex.toString());
        }
    }
}
