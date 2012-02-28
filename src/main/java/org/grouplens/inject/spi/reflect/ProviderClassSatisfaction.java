/*
 * LensKit, an open source recommender systems toolkit.
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
package org.grouplens.inject.spi.reflect;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Provider;

import org.grouplens.inject.spi.Desire;

import com.google.common.base.Function;

import org.grouplens.inject.types.TypeAssignment;
import org.grouplens.inject.types.Types;

/**
 * ProviderClassSatisfaction is a satisfaction implementation that satisfies a
 * type given a {@link Provider} class capable of providing that type.
 * 
 * @author Michael Ludwig <mludwig@cs.umn.edu>
 */
public class ProviderClassSatisfaction extends ReflectionSatisfaction {
    private final Class<? extends Provider<?>> providerType;
    private final TypeAssignment assignment;

    /**
     * Create a ProviderClassSatisfaction that wraps a given provider type.
     * TypeAssignment is an assignment of TypeVariables so that the class has no
     * more free variables.
     * 
     * @param providerType The provider class type
     * @throws NullPointerException if providerType or assignment is null
     * @throws IllegalArgumentException if the class is not a Provider, or is
     *             not instantiable
     */
    public ProviderClassSatisfaction(Class<? extends Provider<?>> providerType, TypeAssignment assignment) {
        if (providerType == null) {
            throw new NullPointerException("Provider class cannot be null");
        }
        if (assignment == null) {
            throw new NullPointerException("TypeAssignment cannot be null");
        }
        if (!Provider.class.isAssignableFrom(providerType)) {
            throw new IllegalArgumentException("Class type is not a Provider implementation");
        }
        if (!Types.isInstantiable(providerType)) {
            throw new IllegalArgumentException("Provider class cannot be instntiated");
        }
        
        this.providerType = providerType;
        this.assignment = assignment;
    }
    
    /**
     * @return The Provider class that provides instances satisfying this
     *         satisfaction
     */
    public Class<? extends Provider<?>> getProviderType() {
        return providerType;
    }
    
    @Override
    public List<? extends Desire> getDependencies() {
        return ReflectionDesire.getDesires(providerType, assignment);
    }

    @Override
    public Type getType() {
        return assignment.apply(Types.getProvidedType(providerType));
    }

    @Override
    public Class<?> getErasedType() {
        return Types.erase(getType());
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Provider<?> makeProvider(Function<? super Desire, ? extends Provider<?>> dependencies) {
        // we have to use the raw type because we don't have enough information,
        // but we can assume correctly that it will build a provider
        Provider<Provider<?>> providerBuilder = new InjectionProviderImpl(providerType, getDependencies(), dependencies);
        return providerBuilder.get();
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProviderClassSatisfaction)) {
            return false;
        }
        ProviderClassSatisfaction p = (ProviderClassSatisfaction) o;
        return p.providerType.equals(providerType) && p.assignment.equals(assignment);
    }
    
    @Override
    public int hashCode() {
        return providerType.hashCode() ^ assignment.hashCode();
    }
    
    @Override
    public String toString() {
        return "ProviderClassSatisfaction(" + getType() + ", " + providerType + ")";
    }
}
