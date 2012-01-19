/*
 * LensKit, a reference implementation of recommender algorithms.
 * Copyright 2010-2011 Regents of the University of Minnesota
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

import java.util.Comparator;

/**
 * A possibly-not-concrete type. This represents the type of a dependency; it
 * may or may not be concrete. It can effectively be any type. Desires are
 * iteratively resolved and narrowed until they finally correspond to
 * {@link Satisfaction}s.
 *
 * <p>
 * There are two types of desires: parameter desires and component desires. They
 * are distinguished by the {@link #isParameter()} method. Parameter desires are
 * for primitive or string parameters; component desires are for objects and
 * have more complex resolution rules.
 *
 * @author Michael Ekstrand <ekstrand@cs.umn.edu>
 *
 */
public interface Desire {
    /**
     * Query whether this desire is for a parameter (primitive or string with a
     * parameter annotation).
     *
     * @deprecated Can this be removed and consumed by the Role interface?
     * @return <tt>true</tt> if this desire is for a parameter, <tt>false</tt>
     *         if it is for a component.
     */
    boolean isParameter();

    /**
     * Return the role that was declared with this desire. A desire without a
     * role should return null. No role can also be considered the default role.
     * 
     * @return The role applied to this desire
     */
    Role getRole();

    /**
     * Query whether this desire is instantiable, that is, resolved to a
     * concrete type. If it is instantiable, then it can be converted to a Satisfaction
     * with {@link #getSatisfaction()}.
     *
     * @return <tt>true</tt> if the desire is for a concrete class. The only
     *         further desires or satisfactions that can satisfy it are for subclasses
     *         of the desire type.
     */
    boolean isInstantiable();

    /**
     * Return whether or not this desire is a transient desire. A transient
     * desire is only needed during the instantiation of the satisfaction, but
     * once the instance has been built it is no longer necessary.
     * 
     * @return True if the desire is transient
     */
    boolean isTransient();

    /**
     * Get the satisfaction (concrete type) if this desire is fully resolved.
     *
     * @return The satisfaction for this desire, or <tt>null</tt> if the desire is not a
     *         concrete type.
     */
    Satisfaction getSatisfaction();

    /**
     * <p>
     * Get the default desire for this desire. If a desire or has been annotated
     * with a default implementation, or the role has a default binding, then
     * that is represented by the desire returned. If there is no default
     * desire, then null is returned.
     * <p>
     * The returned desire, if it exists, is equivalent to getting a matching,
     * default bind rule and applying it to this desire.
     * 
     * @return The default bind rule for this desire, if one exists
     */
    Desire getDefaultDesire();

    /**
     * Get a comparator for ordering bind rules.  The resulting comparator will
     * throw {@link IllegalArgumentException} when comparing bind rules from a
     * different implementation or which do not apply to this desire.
     *
     * @return A comparator that compares bind rules which apply to this desire
     *         in increasing order of closeness.
     */
    Comparator<BindRule> ruleComparator();
}