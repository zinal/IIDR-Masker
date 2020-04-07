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
            cg.invoke(new Object[] { "demo1", "arg1", 15 });
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    
}
