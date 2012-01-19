package org.grouplens.inject.spi.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.grouplens.inject.spi.reflect.types.RoleA;
import org.grouplens.inject.spi.reflect.types.RoleB;
import org.grouplens.inject.spi.reflect.types.RoleC;
import org.grouplens.inject.spi.reflect.types.RoleD;
import org.junit.Assert;
import org.junit.Test;

public class InjectionPointTest {
    @Test
    public void testConstructorParameterInjectionPoint() throws Exception {
        // created expected injection points
        Constructor<CtorType> ctor = CtorType.class.getConstructor(Object.class, String.class);
        ConstructorParameterInjectionPoint p1 = new ConstructorParameterInjectionPoint(ctor, 0);
        ConstructorParameterInjectionPoint p2 = new ConstructorParameterInjectionPoint(ctor, 1);
        
        Set<InjectionPoint> expected = new HashSet<InjectionPoint>();
        expected.add(p1);
        expected.add(p2);
        
        // verify that the roles and types are identified properly
        Assert.assertEquals(new AnnotationRole(RoleA.class), p1.getRole());
        Assert.assertEquals(new AnnotationRole(RoleB.class), p2.getRole());
        Assert.assertEquals(Object.class, p1.getType());
        Assert.assertEquals(String.class, p2.getType());
        
        Assert.assertEquals(expected, getInjectionPoints(CtorType.class));
    }
    
    @Test
    public void testSetterMethodInjectionPoint() throws Exception {
        // create expected injection points
        Method m1 = SetterType.class.getMethod("setA", Object.class);
        Method m2 = SetterType.class.getMethod("setB", String.class);
        SetterInjectionPoint p1 = new SetterInjectionPoint(m1);
        SetterInjectionPoint p2 = new SetterInjectionPoint(m2);
        
        Set<InjectionPoint> expected = new HashSet<InjectionPoint>();
        expected.add(p1);
        expected.add(p2);
        
        // verify that the roles and types are identified properly
        Assert.assertEquals(new AnnotationRole(RoleA.class), p1.getRole());
        Assert.assertEquals(new AnnotationRole(RoleB.class), p2.getRole());
        Assert.assertEquals(Object.class, p1.getType());
        Assert.assertEquals(String.class, p2.getType());
        
        Assert.assertEquals(expected, getInjectionPoints(SetterType.class));
    }
    
    @Test
    public void testPrimitiveBoxing() throws Exception {
        Method m1 = PrimitiveType.class.getMethod("setUnboxed", int.class);
        Method m2 = PrimitiveType.class.getMethod("setBoxed", Integer.class);
        
        SetterInjectionPoint p1 = new SetterInjectionPoint(m1);
        SetterInjectionPoint p2 = new SetterInjectionPoint(m2);
        
        // make sure that both injection points are normalized to boxed types
        Assert.assertEquals(Integer.class, p1.getType());
        Assert.assertEquals(Integer.class, p2.getType());
    }
    
    @Test
    public void testGetDesires() throws Exception {
        // create expected injection points
        Constructor<BothTypes> ctor = BothTypes.class.getConstructor(Object.class, String.class);
        ConstructorParameterInjectionPoint p1 = new ConstructorParameterInjectionPoint(ctor, 0);
        ConstructorParameterInjectionPoint p2 = new ConstructorParameterInjectionPoint(ctor, 1);
        Method m1 = BothTypes.class.getMethod("setC", Object.class);
        Method m2 = BothTypes.class.getMethod("setD", String.class);
        SetterInjectionPoint p3 = new SetterInjectionPoint(m1);
        SetterInjectionPoint p4 = new SetterInjectionPoint(m2);
        
        Set<InjectionPoint> expected = new HashSet<InjectionPoint>();
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);
        expected.add(p4);
        
        Assert.assertEquals(expected, getInjectionPoints(BothTypes.class));
    }
    
    private Set<InjectionPoint> getInjectionPoints(Class<?> types) {
        List<ReflectionDesire> desires = Types.getDesires(types);
        Set<InjectionPoint> points = new HashSet<InjectionPoint>();
        for (ReflectionDesire rd: desires) {
            points.add(rd.getInjectionPoint());
        }
        return points;
    }
    
    public static class CtorType {
        @Inject
        public CtorType(@RoleA Object a, @RoleB String b) { }
        
        // other constructor to be ignored
        public CtorType() { }
    }
    
    public static class SetterType {
        @Inject
        public void setA(@RoleA Object a) { }
        
        @Inject
        public void setB(@RoleB String b) { }
        
        // other setter to be ignored
        public void setX(Object c) { }
    }
    
    public static class BothTypes {
        @Inject
        public BothTypes(@RoleA Object a, @RoleB String b) { }
        
        @Inject
        public void setC(@RoleC Object c) { }
        
        @Inject
        public void setD(@RoleD String d) { }
    }
    
    public static class PrimitiveType {
        @Inject
        public void setUnboxed(int a) { }
        
        @Inject
        public void setBoxed(Integer a) { }
    }
}