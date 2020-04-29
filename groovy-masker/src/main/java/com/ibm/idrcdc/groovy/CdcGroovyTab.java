package com.ibm.idrcdc.groovy;

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
public class CdcGroovyTab extends ScriptLoader implements UserExitIF {

    public static final String VERSION = 
            "CdcGroovyTab 0.1 2020-04-29";

    public CdcGroovyTab() {
        super("cdcgroovy.exit.path");
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

}
