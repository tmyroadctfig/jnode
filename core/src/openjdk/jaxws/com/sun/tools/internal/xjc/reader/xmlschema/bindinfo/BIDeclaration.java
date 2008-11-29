/*
 * Copyright 2005-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import java.util.Collection;

import javax.xml.namespace.QName;

import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;

import org.xml.sax.Locator;

/**
 * Base interface for all binding customization declarations.
 * 
 * <p>
 * Because of the setParent method, one customization declaration
 * can be attached to one component alone.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface BIDeclaration {
    
    /**
     * Sets the parent BindInfo object of this declaration.
     * A declaration object can use this pointer to access
     * context information, such as other customizations.
     *
     * <p>
     * This method can be only called from {@link BindInfo},
     * and only once. This is a good opportunity to do some
     * follow-up initialization after JAXB unmarshalling
     * populated {@link BIDeclaration}.
     */
    void setParent( BindInfo parent );
    
    /**
     * Gets the name of this binding declaration,
     * which is the same as the tag name of the binding element.
     */
    QName getName();
    
    /**
     * Gets the source location where this declaration was written.
     * For declarations that are generated by XJC itself,
     * this method returns null.
     */
    Locator getLocation();
    
    /**
     * Marks this declaration to be acknowledged -- either actually
     * used or the existence is admitted (for example when
     * a property customization is given at the point of definition.)
     * 
     * <p>
     * Declarations that are not acknowledged will be considered
     * as an error.
     */
    void markAsAcknowledged();
    
    /**
     * Checks if this declaration was acknowledged.
     */
    boolean isAcknowledged();

    /**
     * Called when the parent {@link BindInfo} got its owner set.
     *
     * This is when declarations are connected to {@link BGMBuilder} and
     * its sibling components.
     */
    void onSetOwner();

    /**
     * Gets child {@link BIDeclaration}s if any.
     *
     * @return
     *      can be empty but always non-null. read-only.
     */
    Collection<BIDeclaration> getChildren();
}
