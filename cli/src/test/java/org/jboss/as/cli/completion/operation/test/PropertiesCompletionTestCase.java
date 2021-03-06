/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.cli.completion.operation.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.as.cli.completion.mock.MockCommandContext;
import org.jboss.as.cli.completion.mock.MockNode;
import org.jboss.as.cli.completion.mock.MockOperation;
import org.jboss.as.cli.completion.mock.MockOperationCandidatesProvider;
import org.jboss.as.cli.operation.OperationRequestCompleter;
import org.junit.Test;

/**
 *
 * @author Alexey Loubyansky
 */
public class PropertiesCompletionTestCase {

    private MockCommandContext ctx;
    private OperationRequestCompleter completer;

    public PropertiesCompletionTestCase() {

        MockNode root = new MockNode("root");

        MockOperation op = new MockOperation("operation-no-properties");
        root.addOperation(op);

        op = new MockOperation("operation-properties-one-two-three");
        op.setPropertyNames(Arrays.asList("one", "two", "three"));
        root.addOperation(op);

        ctx = new MockCommandContext();
        ctx.setOperationCandidatesProvider(new MockOperationCandidatesProvider(root));
        completer = new OperationRequestCompleter(ctx);
    }

    @Test
    public void testNoProperties() {

        List<String> candidates = fetchCandidates(":operation-no-properties(");
        //assertEquals(Arrays.asList(), candidates);
        assertEquals(Arrays.asList(")"), candidates);
    }

    @Test
    public void testAllCandidates() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(");
        assertEquals(Arrays.asList("one", "three", "two"), candidates);
    }

    @Test
    public void testTCandidates() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(t");
        assertEquals(Arrays.asList("three", "two"), candidates);
    }

    @Test
    public void testTwCandidates() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(tw");
        //assertEquals(Arrays.asList("two"), candidates);
        assertEquals(Arrays.asList("two="), candidates);
    }

    @Test
    public void testTwoCandidates() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(two");
        //assertEquals(Arrays.asList("two"), candidates);
        assertEquals(Arrays.asList("two="), candidates);
    }

    @Test
    public void testNoMatch() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(twoo");
        assertEquals(Arrays.asList(), candidates);
    }

    @Test
    public void testTwoWithValueCandidates() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(two=2");
        assertEquals(Arrays.asList(), candidates);
    }

    @Test
    public void testAfterTwoCandidates() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(two=2,");
        assertEquals(Arrays.asList("one", "three"), candidates);
    }

    @Test
    public void testOneAfterTwoCandidates() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(two=2,o");
        //assertEquals(Arrays.asList("one"), candidates);
        assertEquals(Arrays.asList("one="), candidates);
    }

    @Test
    public void testNoMatchAfterTwo() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(two=2,oo");
        assertEquals(Arrays.asList(), candidates);
    }

    @Test
    public void testThreeAfterOneAndTwo() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(two=2,one=1,");
        //assertEquals(Arrays.asList("three"), candidates);
        assertEquals(Arrays.asList("three="), candidates);
    }

    @Test
    public void testAllSpecified() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(two=2,one=1,three=3");
        //assertEquals(Arrays.asList(), candidates);
        assertEquals(Arrays.asList(")"), candidates);
    }

    @Test
    public void testAllSpecifiedWithSeparator() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(two=2,one=1,three=3,");
        assertEquals(Arrays.asList(), candidates);
    }

    @Test
    public void testAllSpecifiedAndAnotherPropName() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(two=2,one=1,three=3,four");
        assertEquals(Arrays.asList(), candidates);
    }

    @Test
    public void testAllSpecifiedAndAnotherProp() {

        List<String> candidates = fetchCandidates(":operation-properties-one-two-three(two=2,one=1,three=3,four=4");
        assertEquals(Arrays.asList(")"), candidates);
    }

    protected List<String> fetchCandidates(String buffer) {
        ArrayList<String> candidates = new ArrayList<String>();
        completer.complete(buffer, 0, candidates);
        return candidates;
    }
}
