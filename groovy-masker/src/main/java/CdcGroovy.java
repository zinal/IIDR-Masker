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

    private final Map<String, Info> scripts = new HashMap<>();
    private final String userHome = System.getProperty("user.home");
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
            Info info = scripts.get(name);
            if (info != null) {
                final long curTv = System.currentTimeMillis();
                if ( curTv - info.tv < 5000L )
                    return info.script;
                f = locateScriptFile(name);
                if (f.canRead()) {
                    long stamp = f.lastModified();
                    if (stamp == info.stamp)
                        return info.script;
                } else {
                    // TODO: print warning about missing file
                    return info.script;
                }
            }
            if (f==null)
                f = locateScriptFile(name);
            if (f.canRead()) {
                info = loadInfo(f);
                scripts.put(name, info);
                return info.script;
            } else {
                return null;
            }
        }
    }
    
    private File locateScriptFile(String name) {
        return new File(new File(userHome, "cdcgroovy"), name + ".groovy");
    }
    
    private Info loadInfo(File f) throws Exception {
        if (groovyShell == null)
            groovyShell = new GroovyShell();
        long stamp = f.lastModified();
        Script script = groovyShell.parse(f);
        return new Info(script, stamp);
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
    
    private static final class Info {
        final Script script;
        final long stamp;
        long tv;
        
        Info(Script script, long stamp) {
            this.script = script;
            this.stamp = stamp;
            this.tv = System.currentTimeMillis();
        }
    }
}
