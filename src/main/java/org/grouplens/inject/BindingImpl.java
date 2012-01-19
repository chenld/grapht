package org.grouplens.inject;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Provider;

import org.grouplens.inject.resolver.ContextChain;
import org.grouplens.inject.spi.InjectSPI;

class BindingImpl<T> implements Binding<T> {
    private final ContextImpl context;
    private final Set<Class<?>> sourceTypes;
    
    private final Set<Class<?>> excludeTypes;
    
    private Class<? extends Annotation> role;
    private CachePolicy policy;
    
    private boolean bindingCompleted;
    
    public BindingImpl(ContextImpl context, Class<T> type, Class<?>... otherTypes) {
        this.context = context;
        sourceTypes = new HashSet<Class<?>>();
        excludeTypes = new HashSet<Class<?>>();
        
        sourceTypes.add(type);
        if (otherTypes != null) {
            for (Class<?> t: otherTypes) {
                sourceTypes.add(t);
            }
        }
        
        policy = CachePolicy.SHARED;
        bindingCompleted = false;
    }
    
    private void validateState() {
        if (bindingCompleted) {
            throw new IllegalStateException("Binding already completed");
        }
    }
    
    @Override
    public Binding<T> withRole(Class<? extends Annotation> role) {
        validateState();
        this.role = role;
        return this;
    }

    @Override
    public Binding<T> exclude(Class<?> exclude) {
        if (exclude == null) {
            throw new NullPointerException("Type cannot be null");
        }
        validateState();
        excludeTypes.add(exclude);
        return this;
    }

    @Override
    public Binding<T> cachePolicy(CachePolicy policy) {
        if (policy == null) {
            throw new NullPointerException("CachePolicy cannot be null");
        }
        validateState();
        this.policy = policy;
        return this;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void to(Class<? extends T> impl) {
        InjectSPI spi = context.getSPI();
        ContextChain chain = context.getContextChain();
        RootContextImpl root = context.getRootContext();
        
        for (Class<?> source: sourceTypes) {
            root.addBindRule(chain, spi.bind(role, (Class) source, impl, 0));
        }
        // TODO create generated bindings based on source, impl,
        // and exclude sets
        // FIXME record selected cache policy somehow
        // REVIEW: Should we get rid of cache policy and say everything is always shared?
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void to(T instance) {
        InjectSPI spi = context.getSPI();
        ContextChain chain = context.getContextChain();
        RootContextImpl root = context.getRootContext();
        
        for (Class<?> source: sourceTypes) {
            root.addBindRule(chain, spi.bind(role, (Class) source, instance, 0));
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void toProvider(Class<? extends Provider<? extends T>> provider) {
        InjectSPI spi = context.getSPI();
        ContextChain chain = context.getContextChain();
        RootContextImpl root = context.getRootContext();
        
        for (Class<?> source: sourceTypes) {
            root.addBindRule(chain, spi.bindProvider(role, (Class) source, provider, 0));
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void toProvider(Provider<? extends T> provider) {
        InjectSPI spi = context.getSPI();
        ContextChain chain = context.getContextChain();
        RootContextImpl root = context.getRootContext();
        
        for (Class<?> source: sourceTypes) {
            root.addBindRule(chain, spi.bindProvider(role, (Class) source, provider, 0));
        }        
    }
}