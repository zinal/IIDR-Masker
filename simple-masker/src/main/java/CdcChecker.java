/**
 * Hash checker mini-test.
 * This code is provided "as is", without warranty of any kind.
 * @author zinal
 */
public class CdcChecker {
    
    private static final String VAL = "AbCdEfGhIjKlMnOpQrStUvWxYz";
    
    public static void main(String[] args) {
        try {
            final Object[] xargs = new Object[3];
            xargs[0] = "SHA-1";
            xargs[1] = VAL;
            xargs[2] = "P@$$w0rd";
            String prevval = null;
            int cycler = 0;
            while (true) {
                for (int i=0; i<10000000; ++i) {
                    xargs[1] = VAL + "#" + String.valueOf(cycler)
                            + "#" + String.valueOf(i);
                    String retval = new CdcHasher().invoke(xargs).toString();
                    if (prevval!=null) {
                        if (prevval.length() != retval.length()) {
                            System.out.println("LEN mismatch: " + 
                                    prevval + " -> " + retval);
                        } else if (prevval.equals(retval)) {
                            System.out.println("VAL eq: " + 
                                    prevval + " -> " + retval);
                        }
                    }
                    prevval = retval;
                }
                System.out.println("Next #" + cycler + " -> " + prevval
                    + " @" + String.valueOf(prevval.length()));
                ++cycler;
            }
        } catch(Exception ex) {
            ex.printStackTrace(System.out);
            System.exit(1);
        }
    }
    
}
