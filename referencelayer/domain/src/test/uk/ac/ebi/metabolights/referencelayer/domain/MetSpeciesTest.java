package uk.ac.ebi.metabolights.referencelayer.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: conesa
 * Date: 25/04/2013
 * Time: 15:21
 */
public class MetSpeciesTest {
    @Test
    public void testEquals() throws Exception {

        Database db1 = new Database();
        db1.setName("db1");

        Species sp1 = new Species();
        sp1.setSpecies("sp1");



        MetSpecies msp1 = new MetSpecies(sp1,db1);
        assertTrue("Equals to itself expected to be true", msp1.equals(msp1));



        Database db2 = new Database();
        db1.setName("db2");

        Species sp2 = new Species();
        sp1.setSpecies("sp2");


        MetSpecies msp2 = new MetSpecies(sp2,db2);

        assertFalse("Equals expected to be false", msp1.equals(msp2));

        msp2 = new MetSpecies(sp1,db1);
        assertTrue("Equals expected to be true", msp1.equals(msp2));


        msp2 = new MetSpecies(null,null);
        assertFalse("Equals expected to be false", msp1.equals(msp2));

        msp1 = new MetSpecies(null,null);
        assertTrue("Equals expected to be true", msp1.equals(msp2));


    }
}
