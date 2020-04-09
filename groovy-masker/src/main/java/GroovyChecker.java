/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zinal
 */
public class GroovyChecker {
    
    public static void main(String[] args) {
        try {
            CdcGroovy cg = new CdcGroovy();
            for ( int i=0; i<30; ++i) {
                Object v = cg.invoke(new Object[] { "demo1", "secretValue#" + i });
                System.out.println("Response: " + v);
                Thread.sleep(1000L);
            }
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    
}
