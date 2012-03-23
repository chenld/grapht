/*
 * LensKit, an open source recommender systems toolkit.
 * Copyright 2010-2012 Regents of the University of Minnesota and contributors
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.grouplens.inject.spi;

import java.lang.reflect.AnnotatedElement;

import javax.annotation.Nullable;

/**
 * <p>
 * Qualifier represents a scoping or limiting of a type binding. Roles can be
 * inherited and extend other qualifiers. Implementations are responsible for
 * implementing these details.
 * <p>
 * Qualifier extends AnnotatedElement because qualifiers are collection of
 * annotations in some form.
 * 
 * @author Michael Ludwig <mludwig@cs.umn.edu>
 */
public interface Qualifier extends AnnotatedElement {
    /**
     * <p>
     * Get the parent Qualifier of this qualifier. If the Qualifier does not
     * inherit from the another qualifier, or if it inherits from the
     * default/null qualifier null must be returned.
     * <p>
     * Use {@link #inheritsDefault()} to distinguish between the two cases for
     * when null is returned.
     * 
     * @return The parent qualifier
     */
    public @Nullable Qualifier getParent();
    
    /**
     * <p>
     * Return true if this qualifier inherits from the default/null/blank
     * qualifier instead of another qualifier. This can be useful if you want a
     * qualified binding to fall back to an unqualified binding.
     * <p>
     * This should return false if {@link #getParent()} returns a non-null
     * qualifier. It will return true when the parent is null, but the default
     * is inherited. If the parent is null and this returns false, then this
     * qualifier does not inherit from any qualifier.
     * 
     * @return True if the default qualifier is inherited from
     */
    public boolean inheritsDefault();
}