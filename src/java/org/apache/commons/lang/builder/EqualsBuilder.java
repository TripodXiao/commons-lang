/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.lang.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
/**
 * <p><code>Equals</code> generation routines.</p>
 *
 * <p> This class provides methods to build a good equals method for any
 * class. It follows rules laid out in
 * <a href="http://java.sun.com/docs/books/effective/index.html">Effective Java</a>
 * , by Joshua Bloch. In particular the rule for comparing <code>doubles</code>,
 * <code>floats</code>, and arrays can be tricky. Also, making sure that
 * <code>equals()</code> and <code>hashCode()</code> are consistent can be
 * difficult.</p>
 *
 * <p>Two Object that compare as equals must generate the same hash code.
 * But two Objects with the same hash code do not have to be equal.</p>
 *
 * <p>All relevant fields should be included in the calculation of equals.
 * Derived fields may be ignored. In particular, any field used in
 * generating a hash code must be used in the equals method, and vice
 * versa.</p>
 *
 * <p>Typical use for the code is as follows:</p>
 * <pre>
 * public boolean equals(Object o) {
 *   if ( !(o instanceof MyClass) ) {
 *    return false;
 *   }
 *  MyClass rhs = (MyClass) o;
 *  return new EqualsBuilder()
 *                 .appendSuper(super.equals(o))
 *                 .append(field1, rhs.field1)
 *                 .append(field2, rhs.field2)
 *                 .append(field3, rhs.field3)
 *                 .isEquals();
 *  }
 * </pre>
 *
 * <p> Alternatively, there is a method that uses reflection to determine
 * the fields to test. Because these fields are usually private, the method,
 * <code>reflectionEquals</code>, uses <code>Field.setAccessible</code> to
 * change the visibility of the fields. This will fail under a security
 * manager, unless the appropriate permissions are set up correctly. It is
 * also slower than testing explicitly.</p>
 *
 * <p> A typical invocation for this method would look like:</p>
 * <pre>
 * public boolean equals(Object o) {
 *   return EqualsBuilder.reflectionEquals(this, obj);
 * }
 * </pre>
 * 
 * @author <a href="mailto:steve.downey@netfolio.com">Steve Downey</a>
 * @author <a href="mailto:scolebourne@joda.org">Stephen Colebourne</a>
 * @version $Id: EqualsBuilder.java,v 1.7 2002/12/08 21:10:42 scolebourne Exp $
 */
public class EqualsBuilder {
    /**
     * If the fields tested are equals.
     */
    private boolean isEquals;

    /**
     * <p>Constructor for EqualsBuilder.</p>
     *
     * <p>Starts off assuming that equals is <code>true</code>.</p>
     * @see java.lang.Object#Object()
     */
    public EqualsBuilder() {
        super();
        isEquals = true;
    }

    //-------------------------------------------------------------------------
    
    /**
     * <p>This method uses reflection to determine if the two Object are equal.</p>
     *
     * <p>It uses <code>Field.setAccessible</code> to gain access to private
     * fields. This means that it will throw a security exception if run under
     * a security manger, if the permissions are not set up correctly. It is also
     * not as efficient as testing explicitly.</p>
     *
     * <p>Transient members will be not be tested, as they are likely derived
     * fields, and not part of the value of the Object.</p>
     *
     * <p>Static fields will not be tested.</p>
     * 
     * @param lhs  Left Hand Side
     * @param rhs  Right Hand Side
     * @return <code>true</code> if the two Objects have tested equals.
     */
    public static boolean reflectionEquals(Object lhs, Object rhs) {
        return reflectionEquals(lhs, rhs, false);
    }

    /**
     * <p>This method uses reflection to determine if the two Object are equal.</p>
     *
     * <p>It uses <code>Field.setAccessible</code> to gain access to private
     * fields. This means that it will throw a security exception if run under
     * a security manger, if the permissions are not set up correctly. It is also
     * not as efficient as testing explicitly.</p>
     *
     * <p>If the TestTransients parameter is set to <code>true</code>, transient
     * members will be tested, otherwise they are ignored, as they are likely
     * derived fields, and not part of the value of the Object.</p>
     *
     * <p>Static fields will not be tested.</p>
     * 
     * @param lhs  Left Hand Side
     * @param rhs  Right Hand Side
     * @param testTransients  whether to include transient fields
     * @return <code>true</code> if the two Objects have tested equals.
     */
    public static boolean reflectionEquals(Object lhs, Object rhs, 
            boolean testTransients) {
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        Class c1 = lhs.getClass();
        if (!c1.isInstance(rhs)) {
            return false;
        }
        Field[] fields = c1.getDeclaredFields();
        Field.setAccessible(fields, true);
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        for (int i = 0; i < fields.length && equalsBuilder.isEquals; ++i) {
            Field f = fields[i];
            if (testTransients || !Modifier.isTransient(f.getModifiers())) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    try {
                        equalsBuilder.append(f.get(lhs), f.get(rhs));
                    } catch (IllegalAccessException e) {
                        //this can't happen. Would get a Security exception instead
                        //throw a runtime exception in case the impossible happens.
                        throw new InternalError("Unexpected IllegalAccessException");
                    }
                }
            }
        }
        return equalsBuilder.isEquals();
    }

    //-------------------------------------------------------------------------
    
    /**
     * <p>Adds the result of super.equals() to this builder.</p>
     *
     * @param superEquals  the result of calling <code>super.equals()</code>
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder appendSuper(boolean superEquals) {
        if (isEquals == false) {
            return this;
        }
        isEquals = superEquals;
        return this;
    }
    
    //-------------------------------------------------------------------------
    
    /**
     * <p>Test if two <code>Object</code>s are equal using their
     * <code>equals</code> method.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(Object lhs, Object rhs) {
        if (isEquals == false) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }
        Class lhsClass = lhs.getClass();
        if (!lhsClass.isArray()) {
            //the simple case, not an array, just test the element 
            isEquals = lhs.equals(rhs);
        } else {
            //'Switch' on type of array, to dispatch to the correct handler
            // This handles multi dimensional arrays
            if (lhs instanceof long[]) {
                append((long[]) lhs, (long[]) rhs);
            } else if (lhs instanceof int[]) {
                append((int[]) lhs, (int[]) rhs);
            } else if (lhs instanceof short[]) {
                append((short[]) lhs, (short[]) rhs);
            } else if (lhs instanceof char[]) {
                append((char[]) lhs, (char[]) rhs);
            } else if (lhs instanceof byte[]) {
                append((byte[]) lhs, (byte[]) rhs);
            } else if (lhs instanceof double[]) {
                append((double[]) lhs, (double[]) rhs);
            } else if (lhs instanceof float[]) {
                append((float[]) lhs, (float[]) rhs);
            } else if (lhs instanceof boolean[]) {
                append((boolean[]) lhs, (boolean[]) rhs);
            } else {
                // Not an array of primitives
                append((Object[]) lhs, (Object[]) rhs);
            }
        }
        return this;
    }

    /**
     * <p>Test if two <code>long</code>s are equal using ==.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(long lhs, long rhs) {
        if (isEquals == false) {
            return this;
        }
        isEquals = (lhs == rhs);
        return this;
    }

    /**
     * <p>Test if two <code>int</code>s are equal using ==.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(int lhs, int rhs) {
        if (isEquals == false) {
            return this;
        }
        isEquals = (lhs == rhs);
        return this;
    }

    /**
     * <p>Test if two <code>short</code>s are equal using ==.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(short lhs, short rhs) {
        if (isEquals == false) {
            return this;
        }
        isEquals = (lhs == rhs);
        return this;
    }

    /**
     * <p>Test if two <code>char</code>s are equal using ==.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(char lhs, char rhs) {
        if (isEquals == false) {
            return this;
        }
        isEquals = (lhs == rhs);
        return this;
    }

    /**
     * <p>Test if two <code>byte</code>s are equal using ==.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(byte lhs, byte rhs) {
        if (isEquals == false) {
            return this;
        }
        isEquals = (lhs == rhs);
        return this;
    }

    /**
     * <p>Test if two <code>double</code>s are equal by testing that the
     * pattern of bits returned by <code>doubleToLong</code> are equal.</p>
     *
     * <p>This handles NaNs, Infinties, and <code>-0.0</code>.</p>
     *
     * <p>It is compatible with the hash code generated by
     * <code>HashCodeBuilder</code>.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(double lhs, double rhs) {
        if (isEquals == false) {
            return this;
        }
        return append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
    }

    /**
     * <p>Test if two <code>float</code>s are equal byt testing that the
     * pattern of bits returned by doubleToLong are equal.</p>
     *
     * <p>This handles NaNs, Infinties, and <code>-0.0</code>.</p>
     *
     * <p>It is compatible with the hash code generated by
     * <code>HashCodeBuilder</code>.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(float lhs, float rhs) {
        if (isEquals == false) {
            return this;
        }
        return append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
    }

    /**
     * <p>Test if two <code>booleans</code>s are equal using ==.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
      */
    public EqualsBuilder append(boolean lhs, boolean rhs) {
        if (isEquals == false) {
            return this;
        }
        isEquals = (lhs == rhs);
        return this;
    }

    /**
     * <p>Performs a deep comparison of two Object arrays.</p>
     *
     * <p>This also will be called for the top level of
     * multi-dimensional, ragged, and multi-typed arrays.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(Object[] lhs, Object[] rhs) {
        if (isEquals == false) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            Class lhsClass = lhs[i].getClass();
            if (!lhsClass.isInstance(rhs[i])) {
                isEquals = false; //If the types don't match, not equal
                break;
            }
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>long</code> Length and all values
     * are compared.</p>
     *
     * <p>The method {@link #append(long, long)} is used.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(long[] lhs, long[] rhs) {
        if (isEquals == false) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>int</code> Length and all values
     * are compared.</p>
     *
     * <p>The method {@link #append(int, int)} is used.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(int[] lhs, int[] rhs) {
        if (isEquals == false) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>short</code> Length and all values
     * are compared.</p>
     *
     * <p>The method {@link #append(short, short)} is used.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(short[] lhs, short[] rhs) {
        if (isEquals == false) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>char</code> Length and all values
     * are compared.</p>
     *
     * <p>The method {@link #append(char, char)} is used.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(char[] lhs, char[] rhs) {
        if (isEquals == false) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>byte</code> Length and all values
     * are compared.</p>
     *
     * <p>The method {@link #append(byte, byte)} is used.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(byte[] lhs, byte[] rhs) {
        if (isEquals == false) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>double</code> Length and all values
     * are compared.</p>
     *
     * <p>The method {@link #append(double, double)} is used.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(double[] lhs, double[] rhs) {
        if (isEquals == false) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>float</code> Length and all values
     * are compared.</p>
     *
     * <p>The method {@link #append(float, float)} is used.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(float[] lhs, float[] rhs) {
        if (isEquals == false) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Deep comparison of array of <code>boolean</code> Length and all values
     * are compared.</p>
     *
     * <p>The method {@link #append(boolean, boolean)} is used.</p>
     *
     * @param lhs - Left Hand Side
     * @param rhs - Right Hand Side
     * @return EqualsBuilder - used to chain calls.
     */
    public EqualsBuilder append(boolean[] lhs, boolean[] rhs) {
        if (isEquals == false) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && isEquals; ++i) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    /**
     * <p>Return <code>true</code> if the fields that have been checked
     * are all equal.</p>
     *
     * @return boolean
     */
    public boolean isEquals() {
        return isEquals;
    }

}
