/*
 * Grapht, an open source dependency injector.
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
package org.grouplens.grapht;

import java.lang.annotation.Annotation;

import javax.inject.Provider;
import javax.inject.Qualifier;

/**
 * Binding is part of the fluent API used for configuring an {@link Injector}.
 * It represents a binding action from one type to another type.
 * 
 * @author Michael Ludwig <mludwig@cs.umn.edu>
 * @param <T> The source type
 */
public interface Binding<T> {
    /**
     * <p>
     * Specify that the binding created created is the last binding to apply to
     * the desired type. It is permissible to have two bindings as
     * <code>A -&gt; B -&gt; C</code>. If the binding from A to B is a final
     * binding, the binding from B to C will not be followed.
     * <p>
     * Bindings to instances and {@link Provider Providers} are automatically
     * final bindings.
     * 
     * @return This Binding
     */
    Binding<T> finalBinding();

    /**
     * <p>
     * Configure the binding to match the given {@link Qualifier} annotation.
     * The given annotation type must be annotated with {@link Qualifier}. The
     * created binding will match injection points only if the qualifier is
     * applied to the injection point, unless the annotation inherits from the
     * default qualifier.
     * <p>
     * This will override any previous name or qualifier annotation.
     * 
     * @param qualifier The Qualifier that must match
     * @return This Binding
     * @throws NullPointerException if qualifier is null
     */
    Binding<T> withQualifier(Class<? extends Annotation> qualifier);
    
    /**
     * <p>
     * Configure the binding to match injection points that have been annotated
     * with the exact annotation instance.
     * <p>
     * This will override any previous name or qualifier annotation.
     * 
     * @param annot The annotation instance to match
     * @return This Binding
     * @throws NullPointerException if annot is null
     */
    Binding<T> withQualifier(Annotation annot);
    
    /**
     * <p>
     * Configure the binding to only match injection points that have no
     * qualifier. By default, the binding matches any injection point with the
     * given type, whether or not its been qualified. A qualified binding to the
     * same type will still be preferred first when resolving a qualified
     * injection point.
     * 
     * @return This binding
     */
    Binding<T> unqualified();

    /**
     * Exclude the provided type from being matched when examining injection
     * points. Bindings can generate multiple {@link BindRule BindRules} for
     * super and sub types. Excluded classes for a binding will not have
     * BindRules generated for them.
     * 
     * @param exclude The type to exclude from automated rule generation
     * @return This Binding
     */
    Binding<T> exclude(Class<?> exclude);

    /**
     * <p>
     * Complete this binding by specifying a subtype that will satisfy the
     * desired type. The implementation does not have to be instantiable; if
     * it's not then additional bindings must be configured to bind to reach an
     * instantiable type. It is recommended for types to be instantiable.
     * <p>
     * The given type may have its own dependencies that will have to be
     * satisfied by other bindings.
     * 
     * @param impl The implementation type
     */
    void to(Class<? extends T> impl);

    /**
     * Complete this binding by specifying an instance to use. This instance
     * will be used to satisfy matched injection points.
     * 
     * @param instance The instance to use
     */
    void to(T instance);

    /**
     * Complete this binding by specifying a Provider class to be instantiated
     * and used to create instances of type T. The Provider class may have its
     * own dependencies that will be resolved by the injector.
     * 
     * @param provider The provider type that will satisfy this binding
     */
    void toProvider(Class<? extends Provider<? extends T>> provider);

    /**
     * Complete this binding by specifying a Provider instance that will be used
     * to create instances of type T to satisfy this binding.
     * 
     * @param provider The provider instance
     */
    void toProvider(Provider<? extends T> provider);
}
