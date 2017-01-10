package org.lucene46.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;

import org.lucene46.codecs.lucene3x.Lucene3xCodec;
import org.lucene46.codecs.lucene40.Lucene40Codec;
import org.lucene46.codecs.lucene40.Lucene40PostingsFormat;
import org.lucene46.codecs.lucene41.Lucene41Codec;
import org.lucene46.codecs.lucene41.Lucene41PostingsFormat;
import org.lucene46.codecs.lucene42.Lucene42Codec;
import org.lucene46.codecs.lucene42.Lucene42DocValuesFormat;
import org.lucene46.codecs.lucene45.Lucene45Codec;
import org.lucene46.codecs.lucene45.Lucene45DocValuesFormat;
import org.lucene46.codecs.lucene46.Lucene46Codec;

import java.util.ServiceConfigurationError;

/**
 * Helper class for loading named SPIs from classpath (e.g. Codec, PostingsFormat).
 * @lucene.internal
 */
public final class NamedSPILoader<S extends NamedSPILoader.NamedSPI> implements Iterable<S> {

  private volatile Map<String,S> services = Collections.emptyMap();
  private final Class<S> clazz;

  public NamedSPILoader(Class<S> clazz) {
    this(clazz, Thread.currentThread().getContextClassLoader());
  }
  
  public NamedSPILoader(Class<S> clazz, ClassLoader classloader) {
    this.clazz = clazz;
    
    // if clazz' classloader is not a parent of the given one, we scan clazz's classloader, too:
    final ClassLoader clazzClassloader = clazz.getClassLoader();
    
    if (clazzClassloader != null && !SPIClassIterator.isParentClassLoader(clazzClassloader, classloader)) {
    	
      reload(clazzClassloader);
    }
    reload(classloader);
  }
  
  /** 
   * Reloads the internal SPI list from the given {@link ClassLoader}.
   * Changes to the service list are visible after the method ends, all
   * iterators ({@link #iterator()},...) stay consistent. 
   * 
   * <p><b>NOTE:</b> Only new service providers are added, existing ones are
   * never removed or replaced.
   * 
   * <p><em>This method is expensive and should only be called for discovery
   * of new service providers on the given classpath/classloader!</em>
   */
  public synchronized void reload(ClassLoader classloader) {
	  
    final LinkedHashMap<String,S> services = new LinkedHashMap<String,S>(this.services);
    final SPIClassIterator<S> loader = SPIClassIterator.get(clazz, classloader);
    
    
    
    
    if(clazz.getSimpleName().equals("PostingsFormat")) {  
      services.put("Lucene40", (S) new Lucene40PostingsFormat());  
      services.put("Lucene41", (S) new Lucene41PostingsFormat());  
          
        }  
    else if(clazz.getSimpleName().equals("DocValuesFormat")) {  
      services.put("Lucene42", (S) new Lucene42DocValuesFormat());  
      services.put("Lucene45", (S) new Lucene45DocValuesFormat());  
      }  
    else if(clazz.getSimpleName().equals("Codec")) {  
      services.put("Lucene40", (S) new Lucene40Codec());  
      services.put("Lucene41", (S) new Lucene41Codec());  
      services.put("Lucene42", (S) new Lucene42Codec());  
      services.put("Lucene45", (S) new Lucene45Codec()); 
      services.put("Lucene46", (S) new Lucene46Codec());
      services.put("Lucene3x", (S) new Lucene3xCodec());  
    }  
    this.services = Collections.unmodifiableMap(services);  
    
    
    
    
    
//    while (loader.hasNext()) {
//      final Class<? extends S> c = loader.next();
//      System.out.println(c);
//      try {
//    	  System.out.println(c.newInstance());
//        final S service = c.newInstance();
//        final String name = service.getName();
//        
//        // only add the first one for each name, later services will be ignored
//        // this allows to place services before others in classpath to make 
//        // them used instead of others
//        System.out.println(name);
//        if (!services.containsKey(name)) {
//          checkServiceName(name);
//          services.put(name, service);
//          System.out.println(name);
//        }
//      } catch (Exception e) {
//        throw new ServiceConfigurationError("Cannot instantiate SPI class: " + c.getName(), e);
//      }
//    }
    this.services = Collections.unmodifiableMap(services);
  }
  
  /**
   * Validates that a service name meets the requirements of {@link NamedSPI}
   */
  public static void checkServiceName(String name) {
    // based on harmony charset.java
    if (name.length() >= 128) {
      throw new IllegalArgumentException("Illegal service name: '" + name + "' is too long (must be < 128 chars).");
    }
    for (int i = 0, len = name.length(); i < len; i++) {
      char c = name.charAt(i);
      if (!isLetterOrDigit(c)) {
        throw new IllegalArgumentException("Illegal service name: '" + name + "' must be simple ascii alphanumeric.");
      }
    }
  }
  
  /**
   * Checks whether a character is a letter or digit (ascii) which are defined in the spec.
   */
  private static boolean isLetterOrDigit(char c) {
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9');
  }
  
  public S lookup(String name) {
	  
    final S service = services.get(name);
   // System.out.println("111111111111111 :"+name+" with name "+clazz.getName());
    if (service != null) return service;
    throw new IllegalArgumentException("A SPI class of type "+clazz.getName()+" with name '"+name+"' does not exist. "+
     "You need to add the corresponding JAR file supporting this SPI to your classpath."+
     "The current classpath supports the following names: "+availableServices());
  }

  public Set<String> availableServices() {
    return services.keySet();
  }
  
  @Override
  public Iterator<S> iterator() {
    return services.values().iterator();
  }
  
  /**
   * Interface to support {@link NamedSPILoader#lookup(String)} by name.
   * <p>
   * Names must be all ascii alphanumeric, and less than 128 characters in length.
   */
  public static interface NamedSPI {
    String getName();
  }
  
}
