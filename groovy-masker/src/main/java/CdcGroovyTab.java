import com.datamirror.ts.target.publication.userexit.*;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Groovy dynamic scripting for IBM CDC.
 * This code is provided "as is", without warranty of any kind.
 * 
 * Put the CdcGroovyTab.class into {cdc-install-dir}/lib.
 * Copy groovy-2.5.10.jar to the same directory.
 * Create file system.cp in instance/INAME/conf directory, with:
 *    lib/groovy-2.5.10.jar
 * 
 * javac CdcGroovyTab.java -classpath ts.jar:groovy-2.5.10.jar
 * 
 */
public class CdcGroovyTab implements UserExitIF {

    public static final String VERSION = 
            "CdcGroovyTab 0.1 2020-04-28";

    private final File scriptHome;
    // Ugly data structure to avoid the need to have multiple class files
    private final Map<String, Object[]> scripts = new HashMap<>();
    private GroovyShell groovyShell = null;
    
    public CdcGroovyTab() {
        String scriptHomeVal = System.getProperty("cdcgroovy.de.path");
        if (scriptHomeVal==null || scriptHomeVal.trim().length()==0) {
            scriptHome = new File(System.getProperty("user.home"), "cdcgroovy");
        } else {
            scriptHome = new File(scriptHomeVal);
        }
        // Trace version and script directory on load
        System.out.println(VERSION + ", scripts path: " + scriptHome.getPath()
            + (scriptHome.isDirectory() ? " (available)" : "(UNAVAILABLE)"));
    }

    @Override
    public void init(ReplicationEventPublisherIF repif) throws UserExitException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean processReplicationEvent(ReplicationEventIF reif) throws UserExitException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void finish() throws UserExitException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Look up for the script with the specified name,
     * compile it if necessary (if it has not yet been loaded),
     * and return the compiled implementation.
     * @param name Script name
     * @return Compiled script, or null if script has not been found
     * @throws Exception 
     */
    private Script locateScript(String name) throws Exception {
        // This may not be necessary, but I was not able to find anywhere
        // if the invoke() method may or may not be called concurrently.
        synchronized(scripts) {
            File f = null;
            // Look up for already loaded script
            Object[] info = scripts.get(name);
            if (info != null) {
                // In case we have one, let's check whether its timestamp
                // has been recently checked for changes.
                final long curTv = System.currentTimeMillis();
                if ( curTv - getInfoMark(info) < 2000L )
                    return getInfoScript(info);
                // We now need to check whether the script has changed
                updateInfoMark(info, curTv);
                f = generateScriptFilename(name);
                if (f.canRead()) {
                    // Compare the timestamp
                    long stamp = f.lastModified();
                    if (stamp == getInfoStamp(info)) {
                        // Same timestamp as we have seen during load.
                        // We can return the already-cached version.
                        return getInfoScript(info);
                    }
                } else {
                    // TODO: print warning about missing file.
                    // Still returning the already-cached version.
                    return getInfoScript(info);
                }
            }
            if (f==null)
                f = generateScriptFilename(name);
            if (f.canRead()) {
                // Load and cache the script
                info = loadInfo(f);
                scripts.put(name, info);
                return getInfoScript(info);
            } else {
                // No script available
                return null;
            }
        }
    }
    
    /**
     * Build the full script filename from the script name.
     * @param name Script name
     * @return Filename (may not exist, or may be unavailable).
     */
    private File generateScriptFilename(String name) {
        return new File(scriptHome, name + ".groovy");
    }
    
    /**
     * Load the script from the specified file.
     * @param f File
     * @return Compiled scriot
     * @throws Exception If no file or bad script
     */
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

    /**
     * Collect the exception chain as a string to support debugging.
     * @param ex Exception which has been caught.
     * @return Generated exception chain description.
     */
    public static String buildMessage(Throwable ex) {
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
