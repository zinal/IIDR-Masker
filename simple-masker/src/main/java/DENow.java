
import com.datamirror.ts.derivedexpressionmanager.*;

/**
 * Returns current timestamp.
 * %USERFUNC("JAVA","DENow",null)
 */
public class DENow implements DEUserExitIF {

    public DENow() {

    }

    @Override
    public Object invoke(Object[] aobjList)
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }
}
