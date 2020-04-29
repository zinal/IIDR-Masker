package com.ibm.idrcdc.groovy;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author zinal
 */
public class ScriptLoader {
    
    protected final File scriptHome;
    private final Random random = new Random();
    private final Map<String, ScriptInfo> scripts = new HashMap<>();
    private GroovyShell groovyShell = null;
    
    public ScriptLoader(String envVar) {
        if (envVar==null)
            envVar = "cdcgroovy.de.path";
        String scriptHomeVal = System.getProperty(envVar);
        if (scriptHomeVal==null || scriptHomeVal.trim().length()==0) {
            scriptHome = new File(System.getProperty("user.home"), "cdcgroovy");
        } else {
            scriptHome = new File(scriptHomeVal);
        }
    }

    public File getScriptHome() {
        return scriptHome;
    }
    
    /**
     * Look up for the script with the specified name,
     * compile it if necessary (if it has not yet been loaded),
     * and return the compiled implementation.
     * 
     * This method synchronizes at two points: at accessing cached scripts
     * and at re-loading the unknown or obsolete scripts.
     * This may not be necessary, but I was not able to find anywhere
     * if the invoke() method may or may not be called concurrently.
     * 
     * @param name Script name
     * @return Compiled script, or null if script has not been found
     * @throws Exception 
     */
    public Script locateScript(String name) throws Exception {
        final long curTv = System.currentTimeMillis();
        ScriptInfo info;
        // Synchronize on script cache
        synchronized(scripts) {
            info = scripts.get(name);
            if (info != null)
                info = new ScriptInfo(info);
            // Ensure we have a Groovy shell
            if (groovyShell == null)
                groovyShell = new GroovyShell();
        }
        if (info!=null) {
            // We have a (copy of) cached entry, and if it is not
            // the time to check it, just return what we have
            if ( curTv - info.mark < 5000L )
                return info.script;
        }
        // Checking the timestamp of the file
        final File f = generateScriptFilename(name);
        if ( !f.isFile() || !f.canRead() ) {
            // TODO: print warning about missing file.
            // Still returning the already-cached version, if we have one
            return (info==null) ? null : info.script;
        }
        final long fileStamp = f.lastModified();
        if ( (info != null) && (info.stamp == fileStamp) ) {
            // It is the very same script.
            // We need to schedule the next check.
            synchronized(scripts) {
                ScriptInfo temp = scripts.get(name);
                if (temp==null) {
                    // This should never happen unless someone have evicted
                    // the entry from the cache by debugger or other means.
                    // Anyway, we can survive that.
                    temp = new ScriptInfo(info.script, info.stamp, info.mark);
                    scripts.put(name, temp);
                }
                // Next check in 4-5 seconds.
                // Random check time is better to avoid sudden stulls
                // each 5 seconds due to multiple scripts being re-loaded.
                temp.mark = curTv + random.nextInt(1000);
            }
            // Again we return the already cached version.
            return info.script;
        }
        // At this point we checked that the file exists,
        // and it should be either re-loaded, or loaded for the first time.
        // We also have a Groovy shell object available.
        synchronized(groovyShell) {
            // We disallow parallel loading of scripts.
            info = new ScriptInfo(groovyShell.parse(f), fileStamp, curTv);
        }
        // We need to put the newly loaded entry to the cache.
        synchronized(scripts) {
            scripts.put(name, info);
        }
        return info.script;
    }
    
    /**
     * Build the full script filename from the script name.
     * @param name Script name
     * @return Filename (may not exist, or may be unavailable).
     */
    public File generateScriptFilename(String name) {
        return new File(scriptHome, name + ".groovy");
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
    
    private static final class ScriptInfo {
        final Script script;
        final long stamp;
        final boolean duplicate;
        long mark;
        
        ScriptInfo(Script script, long stamp, long mark) {
            this.script = script;
            this.stamp = stamp;
            this.duplicate = false;
            this.mark = System.currentTimeMillis();
        }
        
        ScriptInfo(ScriptInfo si) {
            this.script = si.script;
            this.stamp = si.stamp;
            this.duplicate = true;
            this.mark = si.mark;
        }
    }

}
