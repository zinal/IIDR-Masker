package com.ibm.idrcdc.groovy;

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
 * Put the CdcGroovy.class into {cdc-install-dir}/lib.
 * Copy groovy-2.5.10.jar to the same directory.
 * Create file system.cp in instance/INAME/conf directory, with:
 *    lib/groovy-2.5.10.jar
 * 
 * javac CdcGroovy.java -classpath ts.jar:groovy-2.5.10.jar
 * 
 * %USERFUNC("JAVA","CdcGroovy","script-name", COLUMN)
 */
public class CdcGroovy extends ScriptLoader implements DEUserExitIF {

    public static final String VERSION = 
            "CdcGroovy 1.3 2020-04-29";

    public CdcGroovy() {
        super("cdcgroovy.de.path");
        // Trace version and script directory on load
        System.out.println(VERSION + ", scripts path: " + scriptHome.getPath()
            + (scriptHome.isDirectory() ? " (available)" : "(UNAVAILABLE)"));
    }
    
    /**
     * Load and call the Groovy script with the specified arguments.
     * 
     * @param args Object[]
     * @return String as Object
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvalidArgumentException
     * @throws com.datamirror.ts.derivedexpressionmanager.UserExitInvokeException
     */
    @Override
    public Object invoke(Object[] args) 
            throws UserExitInvalidArgumentException, UserExitInvokeException {
        // At least one argument is expected - it is the script name
        if (args.length < 1) {
            throw new UserExitInvalidArgumentException(getClass().getName() 
                    + ": insufficient number of arguments, "
                    + "expects a script name");
        }
        // Check we actually got a script name
        if (!(args[0] instanceof String)) {
            throw new UserExitInvalidArgumentException(getClass().getName() 
                    + ": The script name must be String");
        }
        String scriptName = (String) args[0];
        try {
            // Load script (or locate it in the cache)
            Script script = locateScript(scriptName);
            if (script==null)
                throw new Exception("Script not found: " + scriptName);
            // Call the "invoke" method, passing our arguments
            return script.invokeMethod("invoke", args);
        } catch(Exception ex) {
            // Convert the original exception to a debugging message
            throw new UserExitInvokeException(buildMessage(ex));
        }
    }

}
