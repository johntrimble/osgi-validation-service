/**
 * Copyright (C) 2012 John Trimble <trimblej@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.johntrimble.osgi.validation.impl;

import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;

import org.apache.bval.jsr303.ApacheValidationProvider;
import org.apache.bval.jsr303.ConfigurationImpl;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;

@Component(immediate=true)
@Service(Validator.class)
public class ValidatorImpl implements Validator {
  Validator delegate;
  
  @Activate
  public void activate(ComponentContext context, Map<String,Object> properties) {
    ClassLoader threadCl = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(ValidatorImpl.class.getClassLoader());
      ApacheValidationProvider p = new ApacheValidationProvider();
      delegate = new ConfigurationImpl(null, p).buildValidatorFactory().getValidator();
    } finally {
      Thread.currentThread().setContextClassLoader(threadCl);
    }
  }
  
  @Deactivate
  public void deactivate() {
    delegate = null;
  }

  public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
    return delegate.validate(object, groups);
  }

  public <T> Set<ConstraintViolation<T>> validateProperty(T object,
      String propertyName, Class<?>... groups) {
    return delegate.validateProperty(object, propertyName, groups);
  }

  public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType,
      String propertyName, Object value, Class<?>... groups) {
    return delegate.validateValue(beanType, propertyName, value, groups);
  }

  public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
    return delegate.getConstraintsForClass(clazz);
  }

  public <T> T unwrap(Class<T> type) {
    return delegate.unwrap(type);
  }
}
