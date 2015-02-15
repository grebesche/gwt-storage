/*
 * Copyright 2015 Xi CHEN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seanchenxi.gwt.storage.shared;

import com.google.gwt.core.shared.GwtIncompatible;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

import com.seanchenxi.gwt.storage.server.ServerStorageSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;

/**
 * A utility class for easy serialization/deserialization actions
 *
 * This a server side implementation.
 * The Storage Serialization Policy File (StorageSerializerPolicy.gwt.rpc) is mandatory for server side serialization.
 * This file will be generated by GWT compilation process and placed in folder <GWT_MODULE_NAME>.
 * In multi module case, multi policy files will be generated separately with <b>the same name</b> (StorageSerializerPolicy.gwt.rpc)
 * But in different module folder.
 *
 * In Server side, before calling StorageUtils#serialize(...), the concerned GWT module's Storage Serialization file
 * should be already loaded by {@link com.seanchenxi.gwt.storage.shared.StorageUtils.PolicyLoader}.
 *
 * You don't need to call to load the policy file every time before server side serialization.
 * You can do it only one time at your servlet initialization phase.
 *
 * There is a super-src version of this class which will by used for client side.
 *
 * Created by: Xi
 */
public class StorageUtils {

  private static final class StorageUtilsHolder {

    private static final ServerStorageSerializer SERIALIZER;
    private static final Map<String, SerializationPolicy> SERIALIZATION_POLICY_CACHE;

    static {
      SERIALIZER = new ServerStorageSerializer();
      SERIALIZATION_POLICY_CACHE = Collections.synchronizedMap(new HashMap<String, SerializationPolicy>());
    }
  }

  @GwtIncompatible
  public static final class PolicyLoader {

    public static void load(ServletContext context, String... moduleNames) {
      if (moduleNames != null) {
        for (String moduleName : moduleNames) {
          load(context, moduleName);
        }
      }
    }

    public static void load(ServletContext context, String moduleName) {
      load(context, moduleName, false);
    }

    public static void load(ServletContext context, String moduleName, boolean isDefault) {
      // To avoid reload the same policy file
      SerializationPolicy serializationPolicy, defaultSerializationPolicy = null;
      synchronized (StorageUtils.StorageUtilsHolder.SERIALIZATION_POLICY_CACHE) {
        serializationPolicy = StorageUtilsHolder.SERIALIZATION_POLICY_CACHE.get(moduleName);
        if (isDefault && serializationPolicy != null) {
          defaultSerializationPolicy = StorageUtilsHolder.SERIALIZATION_POLICY_CACHE.get(DEFAULT_POLICY_MODULE);
        }
      }
      if (defaultSerializationPolicy != null && defaultSerializationPolicy == serializationPolicy) {
        return;
      }

      // Get policy file path, and then load it into cache
      String contextRelativePath = "/" + moduleName + "/" + StorageUtils.SERIALIZATION_POLICY_NAME;
      String serializationPolicyFilePath = SerializationPolicyLoader.getSerializationPolicyFileName(contextRelativePath);
      InputStream is = context.getResourceAsStream(serializationPolicyFilePath);
      try {
        if (is != null) {
          try {
            load(is, moduleName, isDefault);
          } catch (ParseException e) {
            context.log("ERROR: Failed to parse the policy file '" + serializationPolicyFilePath + "'", e);
          } catch (IOException e) {
            context.log("ERROR: Could not read the policy file '" + serializationPolicyFilePath + "'", e);
          }
        } else {
          String message = "ERROR: The serialization policy file '" + serializationPolicyFilePath
              + "' was not found; did you forget to include it in this deployment?";
          context.log(message);
        }
      } finally {
        if (is != null) {
          try {
            is.close();
          } catch (IOException e) {
            // Ignore this error
          }
        }
      }
    }

    public static void load(InputStream is, String moduleName, boolean isDefault) throws IOException, ParseException {
      SerializationPolicy serializationPolicy = SerializationPolicyLoader.loadFromStream(is, null);
      synchronized (StorageUtils.StorageUtilsHolder.SERIALIZATION_POLICY_CACHE) {
        boolean shouldDefault = StorageUtils.StorageUtilsHolder.SERIALIZATION_POLICY_CACHE.isEmpty();
        StorageUtils.StorageUtilsHolder.SERIALIZATION_POLICY_CACHE.put(moduleName, serializationPolicy);
        if (isDefault || shouldDefault) {
          StorageUtils.StorageUtilsHolder.SERIALIZATION_POLICY_CACHE.put(DEFAULT_POLICY_MODULE, serializationPolicy);
        }
      }
    }
  }

  public static final String SERIALIZATION_POLICY_NAME = "StorageSerializerPolicy";
  private static final String DEFAULT_POLICY_MODULE = "DEFAULT_POLICY_MODULE";

  public static String serialize(String str) throws SerializationException {
    return serialize(str, DEFAULT_POLICY_MODULE);
  }

  public static String serialize(boolean bool) throws SerializationException {
    return serialize(boolean.class, bool);
  }

  public static String serialize(byte value) throws SerializationException {
    return serialize(byte.class, value);
  }

  public static String serialize(char value) throws SerializationException {
    return serialize(char.class, value);
  }

  public static String serialize(double value) throws SerializationException {
    return serialize(double.class, value);
  }

  public static String serialize(float value) throws SerializationException {
    return serialize(float.class, value);
  }

  public static String serialize(int value) throws SerializationException {
    return serialize(int.class, value);
  }

  public static String serialize(long value) throws SerializationException {
    return serialize(long.class, value);
  }

  public static String serialize(short value) throws SerializationException {
    return serialize(short.class, value);
  }

  public static String serialize(Object value) throws SerializationException {
    return serialize(Object.class, value);
  }

  public static String serialize(String str, String moduleName) throws SerializationException {
    return serialize(String.class, str, moduleName);
  }

  public static String serialize(boolean bool, String moduleName) throws SerializationException {
    return serialize(boolean.class, bool, moduleName);
  }

  public static String serialize(byte value, String moduleName) throws SerializationException {
    return serialize(byte.class, value, moduleName);
  }

  public static String serialize(char value, String moduleName) throws SerializationException {
    return serialize(char.class, value, moduleName);
  }

  public static String serialize(double value, String moduleName) throws SerializationException {
    return serialize(double.class, value, moduleName);
  }

  public static String serialize(float value, String moduleName) throws SerializationException {
    return serialize(float.class, value, moduleName);
  }

  public static String serialize(int value, String moduleName) throws SerializationException {
    return serialize(int.class, value, moduleName);
  }

  public static String serialize(long value, String moduleName) throws SerializationException {
    return serialize(long.class, value, moduleName);
  }

  public static String serialize(short value, String moduleName) throws SerializationException {
    return serialize(short.class, value, moduleName);
  }

  public static String serialize(Object value, String moduleName) throws SerializationException {
    return serialize(Object.class, value, moduleName);
  }

  public static <T> T deserialize(Class<? super T> clazz, String serializedString) throws SerializationException {
    return deserialize(clazz, serializedString, DEFAULT_POLICY_MODULE);
  }

  public static <T> T deserialize(Class<? super T> clazz, String serializedString, String moduleName) throws SerializationException {
    if(clazz == null || serializedString == null){
      return null;
    }
    SerializationPolicy policy;
    synchronized (StorageUtilsHolder.SERIALIZATION_POLICY_CACHE) {
      String _mn = (moduleName != null && !moduleName.trim().isEmpty()) ? moduleName.trim() : DEFAULT_POLICY_MODULE;
      policy = StorageUtilsHolder.SERIALIZATION_POLICY_CACHE.get(_mn);
    }
    if (policy == null) policy = RPC.getDefaultSerializationPolicy();
    return StorageUtilsHolder.SERIALIZER.deserialize(clazz, serializedString, policy);
  }

  public static <T> String serialize(Class<? super T> clazz, T instance) throws SerializationException {
    return serialize(clazz, instance, DEFAULT_POLICY_MODULE);
  }

  public static <T> String serialize(Class<? super T> clazz, T instance, String moduleName) throws SerializationException {
    if(clazz == null || instance == null){
      return null;
    }
    SerializationPolicy policy;
    synchronized (StorageUtilsHolder.SERIALIZATION_POLICY_CACHE) {
      String _mn = (moduleName != null && !moduleName.trim().isEmpty()) ? moduleName.trim() : DEFAULT_POLICY_MODULE;
      policy = StorageUtilsHolder.SERIALIZATION_POLICY_CACHE.get(_mn);
    }
    if (policy == null) policy = RPC.getDefaultSerializationPolicy();
    return serialize(clazz, instance, policy);
  }

  private static <T> String serialize(Class<? super T> clazz, T instance, SerializationPolicy policy) throws SerializationException {
    return StorageUtilsHolder.SERIALIZER.serialize(clazz, instance, policy);
  }

  private StorageUtils() {

  }
}
