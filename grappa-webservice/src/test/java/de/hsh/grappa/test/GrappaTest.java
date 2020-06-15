package de.hsh.grappa.test;

import de.hsh.grappa.application.GrappaServlet;
import org.junit.After;
import org.junit.Before;

public class GrappaTest {
    private GrappaServlet grappaServlet;

    @Before
    public void setUp() {
        grappaServlet = new GrappaServlet();
        grappaServlet.contextInitialized(null);
    }

    @After
    public void tearDown() {
        grappaServlet.contextDestroyed(null);
    }
}
