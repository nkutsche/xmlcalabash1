/*
 * Insert.java
 *
 * Copyright 2008 Mark Logic Corporation.
 * Portions Copyright 2007 Sun Microsystems, Inc.
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * https://xproc.dev.java.net/public/CDDL+GPL.html or
 * docs/CDDL+GPL.txt in the distribution. See the License for the
 * specific language governing permissions and limitations under the
 * License. When distributing the software, include this License Header
 * Notice in each file and include the License file at docs/CDDL+GPL.txt.
 */

package com.xmlcalabash.library;

import com.xmlcalabash.core.XMLCalabash;
import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.util.ProcessMatchingNodes;
import com.xmlcalabash.util.ProcessMatch;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.io.WritablePipe;
import net.sf.saxon.om.AttributeMap;
import net.sf.saxon.s9api.*;
import com.xmlcalabash.runtime.XAtomicStep;

/**
 *
 * @author ndw
 */

@XMLCalabash(
        name = "p:insert",
        type = "{http://www.w3.org/ns/xproc}insert")

public class Insert extends DefaultStep implements ProcessMatchingNodes {
    private static final QName _match = new QName("match");
    private static final QName _position = new QName("position");
    private ReadablePipe insertion = null;
    private ReadablePipe source = null;
    private WritablePipe result = null;
    private ProcessMatch matcher = null;
    private String position = null;

    /* Creates a new instance of Insert */
    public Insert(XProcRuntime runtime, XAtomicStep step) {
        super(runtime,step);
    }

    public void setInput(String port, ReadablePipe pipe) {
        if ("source".equals(port)) {
            source = pipe;
        } else {
            insertion = pipe;
        }
    }

    public void setOutput(String port, WritablePipe pipe) {
        result = pipe;
    }

    public void reset() {
        source.resetReader();
        result.resetWriter();
    }

    public void run() throws SaxonApiException {
        super.run();

        position = getOption(_position).getString();

        XdmNode doc = source.read();

        matcher = new ProcessMatch(runtime, this);
        matcher.match(doc, getOption(_match));

        result.write(matcher.getResult());
    }

    public boolean processStartDocument(XdmNode node) {
        throw XProcException.stepError(25);
    }

    public void processEndDocument(XdmNode node) {
        throw XProcException.stepError(25);
    }

    @Override
    public AttributeMap processAttributes(XdmNode node, AttributeMap matchingAttributes, AttributeMap nonMatchingAttributes) {
        throw XProcException.stepError(23);
    }

    @Override
    public boolean processStartElement(XdmNode node, AttributeMap attributes) {
        if ("before".equals(position)) {
            doInsert();
        }

        matcher.addStartElement(node, attributes);

        if ("first-child".equals(position)) {
            doInsert();
        }

        return true;
    }

    public void processEndElement(XdmNode node) {
        if ("last-child".equals(position)) {
            doInsert();
        }

        matcher.addEndElement();

        if ("after".equals(position)) {
            doInsert();
        }
    }

    public void processText(XdmNode node) {
        process(node);
    }

    public void processComment(XdmNode node)  {
        process(node);
    }

    public void processPI(XdmNode node) {
        process(node);
    }

    private void process(XdmNode node) {
        if ("before".equals(position)) {
            doInsert();
        }

        if (node.getNodeKind() == XdmNodeKind.COMMENT) {
            matcher.addComment(node.getStringValue());
        } else if (node.getNodeKind() == XdmNodeKind.PROCESSING_INSTRUCTION) {
            matcher.addPI(node.getNodeName().getLocalName(), node.getStringValue());
        } else if (node.getNodeKind() == XdmNodeKind.TEXT) {
            matcher.addText(node.getStringValue());
        } else {
            throw new IllegalArgumentException("What kind of node was that!?");
        }

        if ("after".equals(position)) {
            doInsert();
        }

        if ("first-child".equals(position) || "last-child".equals(position)) {
            throw XProcException.stepError(25);
        }
    }

    private void doInsert() {
        while (insertion.moreDocuments()) {
            XdmNode doc = insertion.read();
            XdmSequenceIterator<XdmNode> iter = doc.axisIterator(Axis.CHILD);
            while (iter.hasNext()) {
                XdmNode child = iter.next();
                matcher.addSubtree(child);
            }
        }
        insertion.resetReader();
    }
}
