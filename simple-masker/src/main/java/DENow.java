
import com.datamirror.ts.derivedexpressionmanager.*;

/**
 * Returns current timestamp.
 * %USERFUNC("JAVA","DENow",null)
 */
public class DENow implements DEUserExitIF {

    public static final String VERSION =
        "DENow 1.0 2020-09-21";

    @Override
    public Object invoke(Object[] aobjList)
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }
}
