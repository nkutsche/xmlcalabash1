/*
 * ProcessMatchingNodes.java
 *
 * Copyright 2008 Mark Logic Corporation.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.xmlcalabash.util;

import net.sf.saxon.om.AttributeMap;
import net.sf.saxon.s9api.XdmNode;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: Apr 13, 2008
 * Time: 3:24:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ProcessMatchingNodes {
    public boolean processStartDocument(XdmNode node);
    public void processEndDocument(XdmNode node);
    public boolean processStartElement(XdmNode node, AttributeMap attributes);
    public AttributeMap processAttributes(XdmNode node, AttributeMap matchingAttributes, AttributeMap nonMatchingAttributes);
    public void processEndElement(XdmNode node);
    public void processText(XdmNode node);
    public void processComment(XdmNode node);
    public void processPI(XdmNode node);
}
