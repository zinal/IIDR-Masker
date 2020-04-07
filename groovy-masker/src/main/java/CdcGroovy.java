import com.datamirror.ts.derivedexpressionmanager.*;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Groovy dynamic scripting for IBM CDC.
 * This code is provided "as is", without warranty of any kind.
 * 
 * Put the CdcGroovy.class into {cdc-install-dir}/lib
 * 
 * javac CdcGroovy.java -classpath ts.jar
 * 
 * %USERFUNC("JAVA","CdcGroovy","script-name", COLUMN)
 */
public class CdcGroovy implements DEUserExitIF {

    public static final String VERSION = 
            "CdcGroovy 1.0 2020-04-07";

    private final File scriptHome = 
            new File(System.getProperty("user.home"), "cdcgroovy");
    // Ugly data structure to avoid the need to have multiple class files
    private final Map<String, Object[]> scripts = new HashMap<>();
    private GroovyShell groovyShell = null;

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
        if (args.length < 1) {
            throw new UserExitInvalidArgumentException(getClass().getName() 
                    + ": insufficient number of arguments, "
                    + "expects a script name");
        }
        // Algorithm type
        if (!(args[0] instanceof String)) {
            throw new UserExitInvalidArgumentException(getClass().getName() 
                    + ": The script name must be String");
        }
        String scriptName = (String) args[0];
        try {
            Script script = locateScript(scriptName);
            if (script==null)
                throw new Exception("Script not found: " + scriptName);
            return script.invokeMethod("invoke", args);
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
            throw new UserExitInvokeException(buildMessage(ex));
        }
    }
    
    private Script locateScript(String name) throws Exception {
        synchronized(scripts) {
            File f = null;
            Object[] info = scripts.get(name);
            if (info != null) {
                final long curTv = System.currentTimeMillis();
                if ( curTv - getInfoMark(info) < 2000L )
                    return getInfoScript(info);
                updateInfoMark(info, curTv);
                f = locateScriptFile(name);
                if (f.canRead()) {
                    long stamp = f.lastModified();
                    if (stamp == getInfoStamp(info)) {
                        return getInfoScript(info);
                    }
                } else {
                    // TODO: print warning about missing file
                    return getInfoScript(info);
                }
            }
            if (f==null)
                f = locateScriptFile(name);
            if (f.canRead()) {
                info = loadInfo(f);
                scripts.put(name, info);
                return getInfoScript(info);
            } else {
                return null;
            }
        }
    }
    
    private File locateScriptFile(String name) {
        return new File(scriptHome, name + ".groovy");
    }
    
    private Object[] loadInfo(File f) throws Exception {
        if (groovyShell == null)
            groovyShell = new GroovyShell();
        long stamp = f.lastModified();
        Script script = groovyShell.parse(f);
        return new Object[] { script, stamp, System.currentTimeMillis() };
    }
    
    private static Script getInfoScript(Object[] info) {
        return (Script) info[0];
    }
    
    private static long getInfoStamp(Object[] info) {
        return (Long) info[1];
    }
    
    private static long getInfoMark(Object[] info) {
        return (Long) info[2];
    }

    private static void updateInfoMark(Object[] info, long mark) {
        info[2] = mark;
    }

    private String buildMessage(Throwable ex) {
        final StringBuilder sb = new StringBuilder();
        while (ex != null) {
            sb.append(ex.getClass().getName());
            if (ex.getStackTrace()!=null && ex.getStackTrace().length > 0) {
                sb.append(" at ");
                StackTraceElement ste = ex.getStackTrace()[0];
                sb.append(ste.getFileName()).append(":")
                        .append(ste.getLineNumber());
                sb.append(", method ").append(ste.getClassName())
                        .append("/").append(ste.getMethodName());
            }
            sb.append(" -> ").append(ex.getMessage());
            if (ex.getCause()!=null)
                sb.append(" *** | ");
            ex = ex.getCause();
        }
        return sb.toString();
    }

}
