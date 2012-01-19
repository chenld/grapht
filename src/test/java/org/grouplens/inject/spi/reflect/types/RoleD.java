package org.grouplens.inject.spi.reflect.types;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.grouplens.inject.annotation.DefaultType;
import org.grouplens.inject.annotation.InheritsDefaultRole;
import org.grouplens.inject.annotation.Role;

@Role
@DefaultType(TypeB.class)
@InheritsDefaultRole
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleD { }