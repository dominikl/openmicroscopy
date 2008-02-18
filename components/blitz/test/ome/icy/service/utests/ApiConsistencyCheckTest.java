/*
 *   $Id$
 *
 *   Copyright 2008 Glencoe Software, Inc. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */
package ome.icy.service.utests;

import junit.framework.TestCase;

import org.testng.annotations.Test;

/**
 * @author Josh Moore, josh at glencoesoftware.com
 * @since 3.0-Beta2
 */
public class ApiConsistencyCheckTest extends TestCase {

    @Test
    public void testCreateContextAndHopeItDoesntExplode() {
        new ome.system.OmeroContext(
                "classpath:ome/services/blitz-servantDefinitions.xml");
    }

}