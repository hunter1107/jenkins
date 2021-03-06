/*
 * The MIT License
 * 
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.model;

import hudson.model.Fingerprint.RangeSet;
import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Kohsuke Kawaguchi
 */
public class FingerprintTest {
    
    @Test public void rangeSet() {
        RangeSet rs = new RangeSet();
        assertFalse(rs.includes(0));
        assertFalse(rs.includes(3));
        assertFalse(rs.includes(5));

        rs.add(3);
        assertFalse(rs.includes(2));
        assertTrue(rs.includes(3));
        assertFalse(rs.includes(4));
        assertEquals("[3,4)",rs.toString());

        rs.add(4);
        assertFalse(rs.includes(2));
        assertTrue(rs.includes(3));
        assertTrue(rs.includes(4));
        assertFalse(rs.includes(5));
        assertEquals("[3,5)",rs.toString());

        rs.add(10);
        assertEquals("[3,5),[10,11)",rs.toString());

        rs.add(9);
        assertEquals("[3,5),[9,11)",rs.toString());

        rs.add(6);
        assertEquals("[3,5),[6,7),[9,11)",rs.toString());

        rs.add(5);
        assertEquals("[3,7),[9,11)",rs.toString());
    }

    @Test public void merge() {
        RangeSet x = new RangeSet();
        x.add(1);
        x.add(2);
        x.add(3);
        x.add(5);
        x.add(6);
        assertEquals("[1,4),[5,7)",x.toString());

        RangeSet y = new RangeSet();
        y.add(3);
        y.add(4);
        y.add(5);
        assertEquals("[3,6)",y.toString());

        x.add(y);
        assertEquals("[1,7)",x.toString());
    }

    @Test public void merge2() {
        RangeSet x = new RangeSet();
        x.add(1);
        x.add(2);
        x.add(5);
        x.add(6);
        assertEquals("[1,3),[5,7)",x.toString());

        RangeSet y = new RangeSet();
        y.add(3);
        y.add(4);
        assertEquals("[3,5)",y.toString());

        x.add(y);
        assertEquals("[1,7)",x.toString());
    }

    @Test public void merge3() {
        RangeSet x = new RangeSet();
        x.add(1);
        x.add(5);
        assertEquals("[1,2),[5,6)",x.toString());

        RangeSet y = new RangeSet();
        y.add(3);
        y.add(5);
        y.add(7);
        assertEquals("[3,4),[5,6),[7,8)",y.toString());

        x.add(y);
        assertEquals("[1,2),[3,4),[5,6),[7,8)",x.toString());
    }

    @Test public void deserialize() throws Exception {
        assertEquals("Fingerprint["
                + "original=stapler/org.kohsuke.stapler:stapler-jelly #123,"
                + "hash=069484c9e963cc615c51278327da8eab,"
                + "fileName=org.kohsuke.stapler:stapler-jelly-1.207.jar,"
                + "timestamp=2013-05-21 19:20:03.534 UTC,"
                + "usages={stuff=[304,306),[307,324),[328,330), stuff/test:stuff=[2,67),[72,77),[84,223),[228,229),[232,268)},"
                + "facets=[]]",
                Fingerprint.load(new File(FingerprintTest.class.getResource("fingerprint.xml").toURI())).toString());
    }

}
