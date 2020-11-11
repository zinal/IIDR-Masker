/** **************************************************************************
 ** Licensed Materials - Property of IBM
 ** IBM InfoSphere Change Data Capture
 ** 5724-U70
 **
 ** (c) Copyright IBM Corp. 2020 All rights reserved.
 **
 ** The following sample of source code ("Sample") is owned by International
 ** Business Machines Corporation or one of its subsidiaries ("IBM") and is
 ** copyrighted and licensed, not sold. You may use, copy, modify, and
 ** distribute the Sample in any form without payment to IBM.
 **
 ** The Sample code is provided to you on an "AS IS" basis, without warranty of
 ** any kind. IBM HEREBY EXPRESSLY DISCLAIMS ALL WARRANTIES, EITHER EXPRESS OR
 ** IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. Some jurisdictions do
 ** not allow for the exclusion or limitation of implied warranties, so the above
 ** limitations or exclusions may not apply to you. IBM shall not be liable for
 ** any damages you suffer as a result of using, copying, modifying or
 ** distributing the Sample, even if IBM has been advised of the possibility of
 ** such damages.
 **************************************************************************** */

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.sql.Timestamp;
import java.util.Arrays;
import java.io.PrintWriter;
import com.datamirror.ts.target.publication.userexit.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Logger for DDL operations.
 * Requires support for rule-based replication
 * (currently available for Oracle Database and IBM Db2 sources).
 */
public class CdcDdlLogger implements SubscriptionUserExitIF {

    public final String DUMMY_SQL = "SELECT 'A' FROM DUAL";
    public final String PROP_OUT_DIR = "output.dir";

    private File outputDir;
    private String sourceSystemId;
    private MessageDigest digest;

    public void init(Properties props) throws Exception {
        String val = props.getProperty(PROP_OUT_DIR);
        if (val != null && val.trim().length() > 0) {
            this.outputDir = new File(val);
        } else {
            String userDir = System.getProperty("user.dir");
            String dirName = "CdcDdlLogger.out";
            this.outputDir = new File(new File(userDir), dirName);
        }
        if (! outputDir.isDirectory() ) {
            outputDir.mkdirs();
        }
        if ( ! outputDir.isDirectory()
                || ! outputDir.canRead()
                || ! outputDir.canWrite() )
            throw new Exception("Cannot access output directory [" + outputDir + "]");
        digest = MessageDigest.getInstance("SHA-256");
    }

    @Override
    public void init(SubscriptionEventPublisherIF sep) throws UserExitException {
        this.sourceSystemId = sep.getSourceSystemID();
        sep.unsubscribeEvent(SubscriptionEventTypes.ALL_EVENTS);
        sep.subscribeEvent(SubscriptionEventTypes.BEFORE_DDL_EVENT);
        String parameter = sep.getParameter();
        if (parameter==null || parameter.trim().length() == 0) {
            String userDir = System.getProperty("user.dir");
            String configFile = "CdcDdlLogger.xml";
            parameter = new File(new File(userDir), configFile).getAbsolutePath();
        }
        final Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(parameter)) {
            props.loadFromXML(fis);
        } catch(Exception ex) {
            throw new UserExitException("Failed to read config file [" + parameter + "]: "
                    + buildMessage(ex));
        }
        try {
            init(props);
        } catch(Exception ex) {
            throw new UserExitException("Init failed: " + buildMessage(ex));
        }
    }

    @Override
    public boolean processSubscriptionEvent(SubscriptionEventIF event) throws UserExitException {
        String tableName = event.getDdlTableName();
        String schemaName = event.getDdlTableSchema();
        Timestamp timestamp = event.getCommitTimeStamp();
        if (timestamp == null)
            timestamp = new Timestamp(System.currentTimeMillis());
        String[] ddlStatements = event.getDdlStatements();
        if (ddlStatements != null) {
            for (String ds : ddlStatements) {
                if (ds!=null && ds.trim().length() > 0)
                    processStatement(timestamp, tableName, schemaName, ds);
            }
            String[] newStatements = new String[ddlStatements.length];
            Arrays.fill(newStatements, 0, newStatements.length, DUMMY_SQL);
            event.setDdlStatement(newStatements);
        }
        return false;
    }

    private void processStatement(Timestamp timestamp,
            String tableName, String schemaName, String ddl) throws UserExitException {
        String nameBase = "ddl-" + Long.toHexString(timestamp.getTime())
                + "-" + schemaName + "." + tableName + "-";
        File f;
        int counter = 0;
        while (true) {
            f = new File(outputDir, nameBase + Integer.toHexString(counter) + ".txt");
            if (!f.exists())
                break;
            ++counter;
        }

        byte[] ddlBytes = ddl.getBytes(StandardCharsets.UTF_8);
        String ddlDigest = printHex(digest.digest(ddlBytes));

        try (PrintWriter pw = new PrintWriter(f, "UTF-8")) {
            pw.println("TYPE: DDL-EVENT");
            pw.format("SOURCE: %s", sourceSystemId); pw.println();
            pw.format("SCHEMA: %s", schemaName); pw.println();
            pw.format("TABLE: %s", tableName); pw.println();
            pw.format("TIMESTAMP: %s", timestamp); pw.println();
            pw.format("MILLIS: %s", timestamp.getTime()); pw.println();
            pw.format("DIGEST: %s", ddlDigest); pw.println();
            pw.format("SIZE: %s", ddlBytes.length); pw.println();
            pw.print(ddl);
            pw.flush();
        } catch(Exception ex) {
            throw new UserExitException("Cannot dump DDL to file: " + buildMessage(ex));
        }
    }

    @Override
    public void finish() {

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

    private static final char[] DIGITS
            = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Fast method to convert byte array to its hex representation.
     * @param data Input byte array
     * @return String with hex-formatted data
     */
    public static String printHex(final byte[] data) {
        if (data==null)
            return null;
        if (data.length==0)
            return "";
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xf0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0f & data[i]];
        }
        return new String(out);
    }

}
