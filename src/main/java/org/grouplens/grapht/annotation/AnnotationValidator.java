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
package org.grouplens.grapht.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Qualifier;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

/**
 * Annotation processor that checks and validates DI annotations.
 */
public class AnnotationValidator extends AbstractProcessor {
    @Override
    public SourceVersion getSupportedSourceVersion() {
        // support version 6 or 7
        // we can't compile against RELEASE_6 and maintain Java 7 compatibility, but the
        // processor is Java 7-compatible. We have not tested against Java 8, however.
        SourceVersion[] versions = SourceVersion.values();
        SourceVersion v6 = SourceVersion.RELEASE_6;
        if (versions.length > v6.ordinal() + 1) {
            // the runtime supports release 7, let's use it
            return versions[v6.ordinal() + 1];
        } else {
            return v6;
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> atypes = new HashSet<String>();
        atypes.add(Qualifier.class.getName());
        atypes.add(Attribute.class.getName());
        return atypes;
    }

    private void warning(Element e, String fmt, Object... args) {
        Messager log = processingEnv.getMessager();
        String msg = String.format(fmt, args);
        log.printMessage(Diagnostic.Kind.MANDATORY_WARNING, msg, e);
    }

    private void error(Element e, String fmt, Object... args) {
        Messager log = processingEnv.getMessager();
        String msg = String.format(fmt, args);
        log.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        analyzeQualifiers(roundEnv);
        analyzeAttributes(roundEnv);
        return false; // let other processors work too
    }

    private void analyzeQualifiers(RoundEnvironment re) {
        Set<? extends Element> elts = re.getElementsAnnotatedWith(Qualifier.class);
        for (Element elt: elts) {
            if (elt.getAnnotation(Documented.class) == null) {
                warning(elt, "qualifier annotation should be @Documented");
            }
            Retention ret = elt.getAnnotation(Retention.class);
            if (ret == null || !ret.value().equals(RetentionPolicy.RUNTIME)) {
                error(elt, "qualifer annotation must have RUNTIME retention");
            }
        }
    }

    private void analyzeAttributes(RoundEnvironment re) {
        Set<? extends Element> elts = re.getElementsAnnotatedWith(Qualifier.class);
        for (Element elt: elts) {
            if (elt.getAnnotation(Documented.class) == null) {
                warning(elt, "attribute annotation should be @Documented");
            }
            Retention ret = elt.getAnnotation(Retention.class);
            if (ret == null || !ret.value().equals(RetentionPolicy.RUNTIME)) {
                error(elt, "attribute annotation must have RUNTIME retention");
            }
        }
    }
}
