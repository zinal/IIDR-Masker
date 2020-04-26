import com.datamirror.ts.derivedexpressionmanager.*;
import java.math.BigDecimal;

/**
 * Converting the numbers to 64-bit integers in IBM CDC.
 * This code is provided "as is", without warranty of any kind.
 * 
 * Put the CdcNum2Long.class into {cdc-install-dir}/lib
 * 
 * javac CdcNum2Long.java -classpath ts.jar
 * 
 * %USERFUNC("JAVA","CdcNum2Long", COLNAME)
 */
public class CdcNum2Long implements DEUserExitIF {

    public static final String VERSION = 
            "CdcNum2Long 1.1 2020-04-21";

    /**
     * Converts the input valus to 64-bit integers
     * 
     * @param args Object[]
     * @return String as Object
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvalidArgumentException
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvokeException
     */
    @Override
    public Object invoke(Object[] args) 
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        if (args.length != 1) {
            throw new UserExitInvalidArgumentException(getClass().getName() 
                    + ": expects a single argument on input");
        }
        // Handle null input values
        if ( args[1] == null )
            return null;
        // Perform the conversion
        try {
            final Number val;
            if (args[0] instanceof Number) {
                val = (Number) args[0];
            } else {
                val = new BigDecimal(args[0].toString());
            }
            return val.longValue();
        } catch(Exception ex) {
            throw new UserExitInvokeException(getClass().getName()
                + ": call failed - " + ex.toString());
        }

    }
}
