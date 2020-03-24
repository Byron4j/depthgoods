 Save This PageHome » openjdk-7 » sun » misc » [javadoc | source]
    1   /*
    2    * Copyright (c) 2000, 2009, Oracle and/or its affiliates. All rights reserved.
    3    * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
    4    *
    5    * This code is free software; you can redistribute it and/or modify it
    6    * under the terms of the GNU General Public License version 2 only, as
    7    * published by the Free Software Foundation.  Oracle designates this
    8    * particular file as subject to the "Classpath" exception as provided
    9    * by Oracle in the LICENSE file that accompanied this code.
   10    *
   11    * This code is distributed in the hope that it will be useful, but WITHOUT
   12    * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
   13    * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
   14    * version 2 for more details (a copy is included in the LICENSE file that
   15    * accompanied this code).
   16    *
   17    * You should have received a copy of the GNU General Public License version
   18    * 2 along with this work; if not, write to the Free Software Foundation,
   19    * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
   20    *
   21    * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
   22    * or visit www.oracle.com if you need additional information or have any
   23    * questions.
   24    */
   25   
   26   package sun.misc;
   27   
   28   import java.security;
   29   import java.lang.reflect;
   30   
   31   
   32   /**
   33    * A collection of methods for performing low-level, unsafe operations.
   34    * Although the class and all methods are public, use of this class is
   35    * limited because only trusted code can obtain instances of it.
   36    *
   37    * @author John R. Rose
   38    * @see #getUnsafe
   39    */
   40   
   41   public final class Unsafe {
   42   
   43       private static native void registerNatives();
   44       static {
   45           registerNatives();
   46           sun.reflect.Reflection.registerMethodsToFilter(Unsafe.class, "getUnsafe");
   47       }
   48   
   49       private Unsafe() {}
   50   
   51       private static final Unsafe theUnsafe = new Unsafe();
   52   
   53       /**
   54        * Provides the caller with the capability of performing unsafe
   55        * operations.
   56        *
   57        * <p> The returned <code>Unsafe</code> object should be carefully guarded
   58        * by the caller, since it can be used to read and write data at arbitrary
   59        * memory addresses.  It must never be passed to untrusted code.
   60        *
   61        * <p> Most methods in this class are very low-level, and correspond to a
   62        * small number of hardware instructions (on typical machines).  Compilers
   63        * are encouraged to optimize these methods accordingly.
   64        *
   65        * <p> Here is a suggested idiom for using unsafe operations:
   66        *
   67        * <blockquote><pre>
   68        * class MyTrustedClass {
   69        *   private static final Unsafe unsafe = Unsafe.getUnsafe();
   70        *   ...
   71        *   private long myCountAddress = ...;
   72        *   public int getCount() { return unsafe.getByte(myCountAddress); }
   73        * }
   74        * </pre></blockquote>
   75        *
   76        * (It may assist compilers to make the local variable be
   77        * <code>final</code>.)
   78        *
   79        * @exception  SecurityException  if a security manager exists and its
   80        *             <code>checkPropertiesAccess</code> method doesn't allow
   81        *             access to the system properties.
   82        */
   83       public static Unsafe getUnsafe() {
   84           Class cc = sun.reflect.Reflection.getCallerClass(2);
   85           if (cc.getClassLoader() != null)
   86               throw new SecurityException("Unsafe");
   87           return theUnsafe;
   88       }
   89   
   90       /// peek and poke operations
   91       /// (compilers should optimize these to memory ops)
   92   
   93       // These work on object fields in the Java heap.
   94       // They will not work on elements of packed arrays.
   95   
   96       /**
   97        * Fetches a value from a given Java variable.
   98        * More specifically, fetches a field or array element within the given
   99        * object <code>o</code> at the given offset, or (if <code>o</code> is
  100        * null) from the memory address whose numerical value is the given
  101        * offset.
  102        * <p>
  103        * The results are undefined unless one of the following cases is true:
  104        * <ul>
  105        * <li>The offset was obtained from {@link #objectFieldOffset} on
  106        * the {@link java.lang.reflect.Field} of some Java field and the object
  107        * referred to by <code>o</code> is of a class compatible with that
  108        * field's class.
  109        *
  110        * <li>The offset and object reference <code>o</code> (either null or
  111        * non-null) were both obtained via {@link #staticFieldOffset}
  112        * and {@link #staticFieldBase} (respectively) from the
  113        * reflective {@link Field} representation of some Java field.
  114        *
  115        * <li>The object referred to by <code>o</code> is an array, and the offset
  116        * is an integer of the form <code>B+N*S</code>, where <code>N</code> is
  117        * a valid index into the array, and <code>B</code> and <code>S</code> are
  118        * the values obtained by {@link #arrayBaseOffset} and {@link
  119        * #arrayIndexScale} (respectively) from the array's class.  The value
  120        * referred to is the <code>N</code><em>th</em> element of the array.
  121        *
  122        * </ul>
  123        * <p>
  124        * If one of the above cases is true, the call references a specific Java
  125        * variable (field or array element).  However, the results are undefined
  126        * if that variable is not in fact of the type returned by this method.
  127        * <p>
  128        * This method refers to a variable by means of two parameters, and so
  129        * it provides (in effect) a <em>double-register</em> addressing mode
  130        * for Java variables.  When the object reference is null, this method
  131        * uses its offset as an absolute address.  This is similar in operation
  132        * to methods such as {@link #getInt(long)}, which provide (in effect) a
  133        * <em>single-register</em> addressing mode for non-Java variables.
  134        * However, because Java variables may have a different layout in memory
  135        * from non-Java variables, programmers should not assume that these
  136        * two addressing modes are ever equivalent.  Also, programmers should
  137        * remember that offsets from the double-register addressing mode cannot
  138        * be portably confused with longs used in the single-register addressing
  139        * mode.
  140        *
  141        * @param o Java heap object in which the variable resides, if any, else
  142        *        null
  143        * @param offset indication of where the variable resides in a Java heap
  144        *        object, if any, else a memory address locating the variable
  145        *        statically
  146        * @return the value fetched from the indicated Java variable
  147        * @throws RuntimeException No defined exceptions are thrown, not even
  148        *         {@link NullPointerException}
  149        */
  150       public native int getInt(Object o, long offset);
  151   
  152       /**
  153        * Stores a value into a given Java variable.
  154        * <p>
  155        * The first two parameters are interpreted exactly as with
  156        * {@link #getInt(Object, long)} to refer to a specific
  157        * Java variable (field or array element).  The given value
  158        * is stored into that variable.
  159        * <p>
  160        * The variable must be of the same type as the method
  161        * parameter <code>x</code>.
  162        *
  163        * @param o Java heap object in which the variable resides, if any, else
  164        *        null
  165        * @param offset indication of where the variable resides in a Java heap
  166        *        object, if any, else a memory address locating the variable
  167        *        statically
  168        * @param x the value to store into the indicated Java variable
  169        * @throws RuntimeException No defined exceptions are thrown, not even
  170        *         {@link NullPointerException}
  171        */
  172       public native void putInt(Object o, long offset, int x);
  173   
  174       /**
  175        * Fetches a reference value from a given Java variable.
  176        * @see #getInt(Object, long)
  177        */
  178       public native Object getObject(Object o, long offset);
  179   
  180       /**
  181        * Stores a reference value into a given Java variable.
  182        * <p>
  183        * Unless the reference <code>x</code> being stored is either null
  184        * or matches the field type, the results are undefined.
  185        * If the reference <code>o</code> is non-null, car marks or
  186        * other store barriers for that object (if the VM requires them)
  187        * are updated.
  188        * @see #putInt(Object, int, int)
  189        */
  190       public native void putObject(Object o, long offset, Object x);
  191   
  192       /** @see #getInt(Object, long) */
  193       public native boolean getBoolean(Object o, long offset);
  194       /** @see #putInt(Object, int, int) */
  195       public native void    putBoolean(Object o, long offset, boolean x);
  196       /** @see #getInt(Object, long) */
  197       public native byte    getByte(Object o, long offset);
  198       /** @see #putInt(Object, int, int) */
  199       public native void    putByte(Object o, long offset, byte x);
  200       /** @see #getInt(Object, long) */
  201       public native short   getShort(Object o, long offset);
  202       /** @see #putInt(Object, int, int) */
  203       public native void    putShort(Object o, long offset, short x);
  204       /** @see #getInt(Object, long) */
  205       public native char    getChar(Object o, long offset);
  206       /** @see #putInt(Object, int, int) */
  207       public native void    putChar(Object o, long offset, char x);
  208       /** @see #getInt(Object, long) */
  209       public native long    getLong(Object o, long offset);
  210       /** @see #putInt(Object, int, int) */
  211       public native void    putLong(Object o, long offset, long x);
  212       /** @see #getInt(Object, long) */
  213       public native float   getFloat(Object o, long offset);
  214       /** @see #putInt(Object, int, int) */
  215       public native void    putFloat(Object o, long offset, float x);
  216       /** @see #getInt(Object, long) */
  217       public native double  getDouble(Object o, long offset);
  218       /** @see #putInt(Object, int, int) */
  219       public native void    putDouble(Object o, long offset, double x);
  220   
  221       /**
  222        * This method, like all others with 32-bit offsets, was native
  223        * in a previous release but is now a wrapper which simply casts
  224        * the offset to a long value.  It provides backward compatibility
  225        * with bytecodes compiled against 1.4.
  226        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  227        * See {@link #staticFieldOffset}.
  228        */
  229       @Deprecated
  230       public int getInt(Object o, int offset) {
  231           return getInt(o, (long)offset);
  232       }
  233   
  234       /**
  235        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  236        * See {@link #staticFieldOffset}.
  237        */
  238       @Deprecated
  239       public void putInt(Object o, int offset, int x) {
  240           putInt(o, (long)offset, x);
  241       }
  242   
  243       /**
  244        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  245        * See {@link #staticFieldOffset}.
  246        */
  247       @Deprecated
  248       public Object getObject(Object o, int offset) {
  249           return getObject(o, (long)offset);
  250       }
  251   
  252       /**
  253        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  254        * See {@link #staticFieldOffset}.
  255        */
  256       @Deprecated
  257       public void putObject(Object o, int offset, Object x) {
  258           putObject(o, (long)offset, x);
  259       }
  260   
  261       /**
  262        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  263        * See {@link #staticFieldOffset}.
  264        */
  265       @Deprecated
  266       public boolean getBoolean(Object o, int offset) {
  267           return getBoolean(o, (long)offset);
  268       }
  269   
  270       /**
  271        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  272        * See {@link #staticFieldOffset}.
  273        */
  274       @Deprecated
  275       public void putBoolean(Object o, int offset, boolean x) {
  276           putBoolean(o, (long)offset, x);
  277       }
  278   
  279       /**
  280        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  281        * See {@link #staticFieldOffset}.
  282        */
  283       @Deprecated
  284       public byte getByte(Object o, int offset) {
  285           return getByte(o, (long)offset);
  286       }
  287   
  288       /**
  289        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  290        * See {@link #staticFieldOffset}.
  291        */
  292       @Deprecated
  293       public void putByte(Object o, int offset, byte x) {
  294           putByte(o, (long)offset, x);
  295       }
  296   
  297       /**
  298        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  299        * See {@link #staticFieldOffset}.
  300        */
  301       @Deprecated
  302       public short getShort(Object o, int offset) {
  303           return getShort(o, (long)offset);
  304       }
  305   
  306       /**
  307        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  308        * See {@link #staticFieldOffset}.
  309        */
  310       @Deprecated
  311       public void putShort(Object o, int offset, short x) {
  312           putShort(o, (long)offset, x);
  313       }
  314   
  315       /**
  316        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  317        * See {@link #staticFieldOffset}.
  318        */
  319       @Deprecated
  320       public char getChar(Object o, int offset) {
  321           return getChar(o, (long)offset);
  322       }
  323   
  324       /**
  325        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  326        * See {@link #staticFieldOffset}.
  327        */
  328       @Deprecated
  329       public void putChar(Object o, int offset, char x) {
  330           putChar(o, (long)offset, x);
  331       }
  332   
  333       /**
  334        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  335        * See {@link #staticFieldOffset}.
  336        */
  337       @Deprecated
  338       public long getLong(Object o, int offset) {
  339           return getLong(o, (long)offset);
  340       }
  341   
  342       /**
  343        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  344        * See {@link #staticFieldOffset}.
  345        */
  346       @Deprecated
  347       public void putLong(Object o, int offset, long x) {
  348           putLong(o, (long)offset, x);
  349       }
  350   
  351       /**
  352        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  353        * See {@link #staticFieldOffset}.
  354        */
  355       @Deprecated
  356       public float getFloat(Object o, int offset) {
  357           return getFloat(o, (long)offset);
  358       }
  359   
  360       /**
  361        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  362        * See {@link #staticFieldOffset}.
  363        */
  364       @Deprecated
  365       public void putFloat(Object o, int offset, float x) {
  366           putFloat(o, (long)offset, x);
  367       }
  368   
  369       /**
  370        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  371        * See {@link #staticFieldOffset}.
  372        */
  373       @Deprecated
  374       public double getDouble(Object o, int offset) {
  375           return getDouble(o, (long)offset);
  376       }
  377   
  378       /**
  379        * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
  380        * See {@link #staticFieldOffset}.
  381        */
  382       @Deprecated
  383       public void putDouble(Object o, int offset, double x) {
  384           putDouble(o, (long)offset, x);
  385       }
  386   
  387       // These work on values in the C heap.
  388   
  389       /**
  390        * Fetches a value from a given memory address.  If the address is zero, or
  391        * does not point into a block obtained from {@link #allocateMemory}, the
  392        * results are undefined.
  393        *
  394        * @see #allocateMemory
  395        */
  396       public native byte    getByte(long address);
  397   
  398       /**
  399        * Stores a value into a given memory address.  If the address is zero, or
  400        * does not point into a block obtained from {@link #allocateMemory}, the
  401        * results are undefined.
  402        *
  403        * @see #getByte(long)
  404        */
  405       public native void    putByte(long address, byte x);
  406   
  407       /** @see #getByte(long) */
  408       public native short   getShort(long address);
  409       /** @see #putByte(long, byte) */
  410       public native void    putShort(long address, short x);
  411       /** @see #getByte(long) */
  412       public native char    getChar(long address);
  413       /** @see #putByte(long, byte) */
  414       public native void    putChar(long address, char x);
  415       /** @see #getByte(long) */
  416       public native int     getInt(long address);
  417       /** @see #putByte(long, byte) */
  418       public native void    putInt(long address, int x);
  419       /** @see #getByte(long) */
  420       public native long    getLong(long address);
  421       /** @see #putByte(long, byte) */
  422       public native void    putLong(long address, long x);
  423       /** @see #getByte(long) */
  424       public native float   getFloat(long address);
  425       /** @see #putByte(long, byte) */
  426       public native void    putFloat(long address, float x);
  427       /** @see #getByte(long) */
  428       public native double  getDouble(long address);
  429       /** @see #putByte(long, byte) */
  430       public native void    putDouble(long address, double x);
  431   
  432       /**
  433        * Fetches a native pointer from a given memory address.  If the address is
  434        * zero, or does not point into a block obtained from {@link
  435        * #allocateMemory}, the results are undefined.
  436        *
  437        * <p> If the native pointer is less than 64 bits wide, it is extended as
  438        * an unsigned number to a Java long.  The pointer may be indexed by any
  439        * given byte offset, simply by adding that offset (as a simple integer) to
  440        * the long representing the pointer.  The number of bytes actually read
  441        * from the target address maybe determined by consulting {@link
  442        * #addressSize}.
  443        *
  444        * @see #allocateMemory
  445        */
  446       public native long getAddress(long address);
  447   
  448       /**
  449        * Stores a native pointer into a given memory address.  If the address is
  450        * zero, or does not point into a block obtained from {@link
  451        * #allocateMemory}, the results are undefined.
  452        *
  453        * <p> The number of bytes actually written at the target address maybe
  454        * determined by consulting {@link #addressSize}.
  455        *
  456        * @see #getAddress(long)
  457        */
  458       public native void putAddress(long address, long x);
  459   
  460       /// wrappers for malloc, realloc, free:
  461   
  462       /**
  463        * Allocates a new block of native memory, of the given size in bytes.  The
  464        * contents of the memory are uninitialized; they will generally be
  465        * garbage.  The resulting native pointer will never be zero, and will be
  466        * aligned for all value types.  Dispose of this memory by calling {@link
  467        * #freeMemory}, or resize it with {@link #reallocateMemory}.
  468        *
  469        * @throws IllegalArgumentException if the size is negative or too large
  470        *         for the native size_t type
  471        *
  472        * @throws OutOfMemoryError if the allocation is refused by the system
  473        *
  474        * @see #getByte(long)
  475        * @see #putByte(long, byte)
  476        */
  477       public native long allocateMemory(long bytes);
  478   
  479       /**
  480        * Resizes a new block of native memory, to the given size in bytes.  The
  481        * contents of the new block past the size of the old block are
  482        * uninitialized; they will generally be garbage.  The resulting native
  483        * pointer will be zero if and only if the requested size is zero.  The
  484        * resulting native pointer will be aligned for all value types.  Dispose
  485        * of this memory by calling {@link #freeMemory}, or resize it with {@link
  486        * #reallocateMemory}.  The address passed to this method may be null, in
  487        * which case an allocation will be performed.
  488        *
  489        * @throws IllegalArgumentException if the size is negative or too large
  490        *         for the native size_t type
  491        *
  492        * @throws OutOfMemoryError if the allocation is refused by the system
  493        *
  494        * @see #allocateMemory
  495        */
  496       public native long reallocateMemory(long address, long bytes);
  497   
  498       /**
  499        * Sets all bytes in a given block of memory to a fixed value
  500        * (usually zero).
  501        *
  502        * <p>This method determines a block's base address by means of two parameters,
  503        * and so it provides (in effect) a <em>double-register</em> addressing mode,
  504        * as discussed in {@link #getInt(Object,long)}.  When the object reference is null,
  505        * the offset supplies an absolute base address.
  506        *
  507        * <p>The stores are in coherent (atomic) units of a size determined
  508        * by the address and length parameters.  If the effective address and
  509        * length are all even modulo 8, the stores take place in 'long' units.
  510        * If the effective address and length are (resp.) even modulo 4 or 2,
  511        * the stores take place in units of 'int' or 'short'.
  512        *
  513        * @since 1.7
  514        */
  515       public native void setMemory(Object o, long offset, long bytes, byte value);
  516   
  517       /**
  518        * Sets all bytes in a given block of memory to a fixed value
  519        * (usually zero).  This provides a <em>single-register</em> addressing mode,
  520        * as discussed in {@link #getInt(Object,long)}.
  521        *
  522        * <p>Equivalent to <code>setMemory(null, address, bytes, value)</code>.
  523        */
  524       public void setMemory(long address, long bytes, byte value) {
  525           setMemory(null, address, bytes, value);
  526       }
  527   
  528       /**
  529        * Sets all bytes in a given block of memory to a copy of another
  530        * block.
  531        *
  532        * <p>This method determines each block's base address by means of two parameters,
  533        * and so it provides (in effect) a <em>double-register</em> addressing mode,
  534        * as discussed in {@link #getInt(Object,long)}.  When the object reference is null,
  535        * the offset supplies an absolute base address.
  536        *
  537        * <p>The transfers are in coherent (atomic) units of a size determined
  538        * by the address and length parameters.  If the effective addresses and
  539        * length are all even modulo 8, the transfer takes place in 'long' units.
  540        * If the effective addresses and length are (resp.) even modulo 4 or 2,
  541        * the transfer takes place in units of 'int' or 'short'.
  542        *
  543        * @since 1.7
  544        */
  545       public native void copyMemory(Object srcBase, long srcOffset,
  546                                     Object destBase, long destOffset,
  547                                     long bytes);
  548       /**
  549        * Sets all bytes in a given block of memory to a copy of another
  550        * block.  This provides a <em>single-register</em> addressing mode,
  551        * as discussed in {@link #getInt(Object,long)}.
  552        *
  553        * Equivalent to <code>copyMemory(null, srcAddress, null, destAddress, bytes)</code>.
  554        */
  555       public void copyMemory(long srcAddress, long destAddress, long bytes) {
  556           copyMemory(null, srcAddress, null, destAddress, bytes);
  557       }
  558   
  559       /**
  560        * Disposes of a block of native memory, as obtained from {@link
  561        * #allocateMemory} or {@link #reallocateMemory}.  The address passed to
  562        * this method may be null, in which case no action is taken.
  563        *
  564        * @see #allocateMemory
  565        */
  566       public native void freeMemory(long address);
  567   
  568       /// random queries
  569   
  570       /**
  571        * This constant differs from all results that will ever be returned from
  572        * {@link #staticFieldOffset}, {@link #objectFieldOffset},
  573        * or {@link #arrayBaseOffset}.
  574        */
  575       public static final int INVALID_FIELD_OFFSET   = -1;
  576   
  577       /**
  578        * Returns the offset of a field, truncated to 32 bits.
  579        * This method is implemented as follows:
  580        * <blockquote><pre>
  581        * public int fieldOffset(Field f) {
  582        *     if (Modifier.isStatic(f.getModifiers()))
  583        *         return (int) staticFieldOffset(f);
  584        *     else
  585        *         return (int) objectFieldOffset(f);
  586        * }
  587        * </pre></blockquote>
  588        * @deprecated As of 1.4.1, use {@link #staticFieldOffset} for static
  589        * fields and {@link #objectFieldOffset} for non-static fields.
  590        */
  591       @Deprecated
  592       public int fieldOffset(Field f) {
  593           if (Modifier.isStatic(f.getModifiers()))
  594               return (int) staticFieldOffset(f);
  595           else
  596               return (int) objectFieldOffset(f);
  597       }
  598   
  599       /**
  600        * Returns the base address for accessing some static field
  601        * in the given class.  This method is implemented as follows:
  602        * <blockquote><pre>
  603        * public Object staticFieldBase(Class c) {
  604        *     Field[] fields = c.getDeclaredFields();
  605        *     for (int i = 0; i < fields.length; i++) {
  606        *         if (Modifier.isStatic(fields[i].getModifiers())) {
  607        *             return staticFieldBase(fields[i]);
  608        *         }
  609        *     }
  610        *     return null;
  611        * }
  612        * </pre></blockquote>
  613        * @deprecated As of 1.4.1, use {@link #staticFieldBase(Field)}
  614        * to obtain the base pertaining to a specific {@link Field}.
  615        * This method works only for JVMs which store all statics
  616        * for a given class in one place.
  617        */
  618       @Deprecated
  619       public Object staticFieldBase(Class c) {
  620           Field[] fields = c.getDeclaredFields();
  621           for (int i = 0; i < fields.length; i++) {
  622               if (Modifier.isStatic(fields[i].getModifiers())) {
  623                   return staticFieldBase(fields[i]);
  624               }
  625           }
  626           return null;
  627       }
  628   
  629       /**
  630        * Report the location of a given field in the storage allocation of its
  631        * class.  Do not expect to perform any sort of arithmetic on this offset;
  632        * it is just a cookie which is passed to the unsafe heap memory accessors.
  633        *
  634        * <p>Any given field will always have the same offset and base, and no
  635        * two distinct fields of the same class will ever have the same offset
  636        * and base.
  637        *
  638        * <p>As of 1.4.1, offsets for fields are represented as long values,
  639        * although the Sun JVM does not use the most significant 32 bits.
  640        * However, JVM implementations which store static fields at absolute
  641        * addresses can use long offsets and null base pointers to express
  642        * the field locations in a form usable by {@link #getInt(Object,long)}.
  643        * Therefore, code which will be ported to such JVMs on 64-bit platforms
  644        * must preserve all bits of static field offsets.
  645        * @see #getInt(Object, long)
  646        */
  647       public native long staticFieldOffset(Field f);
  648   
  649       /**
  650        * Report the location of a given static field, in conjunction with {@link
  651        * #staticFieldBase}.
  652        * <p>Do not expect to perform any sort of arithmetic on this offset;
  653        * it is just a cookie which is passed to the unsafe heap memory accessors.
  654        *
  655        * <p>Any given field will always have the same offset, and no two distinct
  656        * fields of the same class will ever have the same offset.
  657        *
  658        * <p>As of 1.4.1, offsets for fields are represented as long values,
  659        * although the Sun JVM does not use the most significant 32 bits.
  660        * It is hard to imagine a JVM technology which needs more than
  661        * a few bits to encode an offset within a non-array object,
  662        * However, for consistency with other methods in this class,
  663        * this method reports its result as a long value.
  664        * @see #getInt(Object, long)
  665        */
  666       public native long objectFieldOffset(Field f);
  667   
  668       /**
  669        * Report the location of a given static field, in conjunction with {@link
  670        * #staticFieldOffset}.
  671        * <p>Fetch the base "Object", if any, with which static fields of the
  672        * given class can be accessed via methods like {@link #getInt(Object,
  673        * long)}.  This value may be null.  This value may refer to an object
  674        * which is a "cookie", not guaranteed to be a real Object, and it should
  675        * not be used in any way except as argument to the get and put routines in
  676        * this class.
  677        */
  678       public native Object staticFieldBase(Field f);
  679   
  680       /**
  681        * Ensure the given class has been initialized. This is often
  682        * needed in conjunction with obtaining the static field base of a
  683        * class.
  684        */
  685       public native void ensureClassInitialized(Class c);
  686   
  687       /**
  688        * Report the offset of the first element in the storage allocation of a
  689        * given array class.  If {@link #arrayIndexScale} returns a non-zero value
  690        * for the same class, you may use that scale factor, together with this
  691        * base offset, to form new offsets to access elements of arrays of the
  692        * given class.
  693        *
  694        * @see #getInt(Object, long)
  695        * @see #putInt(Object, long, int)
  696        */
  697       public native int arrayBaseOffset(Class arrayClass);
  698   
  699       /** The value of {@code arrayBaseOffset(boolean[].class)} */
  700       public static final int ARRAY_BOOLEAN_BASE_OFFSET
  701               = theUnsafe.arrayBaseOffset(boolean[].class);
  702   
  703       /** The value of {@code arrayBaseOffset(byte[].class)} */
  704       public static final int ARRAY_BYTE_BASE_OFFSET
  705               = theUnsafe.arrayBaseOffset(byte[].class);
  706   
  707       /** The value of {@code arrayBaseOffset(short[].class)} */
  708       public static final int ARRAY_SHORT_BASE_OFFSET
  709               = theUnsafe.arrayBaseOffset(short[].class);
  710   
  711       /** The value of {@code arrayBaseOffset(char[].class)} */
  712       public static final int ARRAY_CHAR_BASE_OFFSET
  713               = theUnsafe.arrayBaseOffset(char[].class);
  714   
  715       /** The value of {@code arrayBaseOffset(int[].class)} */
  716       public static final int ARRAY_INT_BASE_OFFSET
  717               = theUnsafe.arrayBaseOffset(int[].class);
  718   
  719       /** The value of {@code arrayBaseOffset(long[].class)} */
  720       public static final int ARRAY_LONG_BASE_OFFSET
  721               = theUnsafe.arrayBaseOffset(long[].class);
  722   
  723       /** The value of {@code arrayBaseOffset(float[].class)} */
  724       public static final int ARRAY_FLOAT_BASE_OFFSET
  725               = theUnsafe.arrayBaseOffset(float[].class);
  726   
  727       /** The value of {@code arrayBaseOffset(double[].class)} */
  728       public static final int ARRAY_DOUBLE_BASE_OFFSET
  729               = theUnsafe.arrayBaseOffset(double[].class);
  730   
  731       /** The value of {@code arrayBaseOffset(Object[].class)} */
  732       public static final int ARRAY_OBJECT_BASE_OFFSET
  733               = theUnsafe.arrayBaseOffset(Object[].class);
  734   
  735       /**
  736        * Report the scale factor for addressing elements in the storage
  737        * allocation of a given array class.  However, arrays of "narrow" types
  738        * will generally not work properly with accessors like {@link
  739        * #getByte(Object, int)}, so the scale factor for such classes is reported
  740        * as zero.
  741        *
  742        * @see #arrayBaseOffset
  743        * @see #getInt(Object, long)
  744        * @see #putInt(Object, long, int)
  745        */
  746       public native int arrayIndexScale(Class arrayClass);
  747   
  748       /** The value of {@code arrayIndexScale(boolean[].class)} */
  749       public static final int ARRAY_BOOLEAN_INDEX_SCALE
  750               = theUnsafe.arrayIndexScale(boolean[].class);
  751   
  752       /** The value of {@code arrayIndexScale(byte[].class)} */
  753       public static final int ARRAY_BYTE_INDEX_SCALE
  754               = theUnsafe.arrayIndexScale(byte[].class);
  755   
  756       /** The value of {@code arrayIndexScale(short[].class)} */
  757       public static final int ARRAY_SHORT_INDEX_SCALE
  758               = theUnsafe.arrayIndexScale(short[].class);
  759   
  760       /** The value of {@code arrayIndexScale(char[].class)} */
  761       public static final int ARRAY_CHAR_INDEX_SCALE
  762               = theUnsafe.arrayIndexScale(char[].class);
  763   
  764       /** The value of {@code arrayIndexScale(int[].class)} */
  765       public static final int ARRAY_INT_INDEX_SCALE
  766               = theUnsafe.arrayIndexScale(int[].class);
  767   
  768       /** The value of {@code arrayIndexScale(long[].class)} */
  769       public static final int ARRAY_LONG_INDEX_SCALE
  770               = theUnsafe.arrayIndexScale(long[].class);
  771   
  772       /** The value of {@code arrayIndexScale(float[].class)} */
  773       public static final int ARRAY_FLOAT_INDEX_SCALE
  774               = theUnsafe.arrayIndexScale(float[].class);
  775   
  776       /** The value of {@code arrayIndexScale(double[].class)} */
  777       public static final int ARRAY_DOUBLE_INDEX_SCALE
  778               = theUnsafe.arrayIndexScale(double[].class);
  779   
  780       /** The value of {@code arrayIndexScale(Object[].class)} */
  781       public static final int ARRAY_OBJECT_INDEX_SCALE
  782               = theUnsafe.arrayIndexScale(Object[].class);
  783   
  784       /**
  785        * Report the size in bytes of a native pointer, as stored via {@link
  786        * #putAddress}.  This value will be either 4 or 8.  Note that the sizes of
  787        * other primitive types (as stored in native memory blocks) is determined
  788        * fully by their information content.
  789        */
  790       public native int addressSize();
  791   
  792       /** The value of {@code addressSize()} */
  793       public static final int ADDRESS_SIZE = theUnsafe.addressSize();
  794   
  795       /**
  796        * Report the size in bytes of a native memory page (whatever that is).
  797        * This value will always be a power of two.
  798        */
  799       public native int pageSize();
  800   
  801   
  802       /// random trusted operations from JNI:
  803   
  804       /**
  805        * Tell the VM to define a class, without security checks.  By default, the
  806        * class loader and protection domain come from the caller's class.
  807        */
  808       public native Class defineClass(String name, byte[] b, int off, int len,
  809                                       ClassLoader loader,
  810                                       ProtectionDomain protectionDomain);
  811   
  812       public native Class defineClass(String name, byte[] b, int off, int len);
  813   
  814       /**
  815        * Define a class but do not make it known to the class loader or system dictionary.
  816        * <p>
  817        * For each CP entry, the corresponding CP patch must either be null or have
  818        * the a format that matches its tag:
  819        * <ul>
  820        * <li>Integer, Long, Float, Double: the corresponding wrapper object type from java.lang
  821        * <li>Utf8: a string (must have suitable syntax if used as signature or name)
  822        * <li>Class: any java.lang.Class object
  823        * <li>String: any object (not just a java.lang.String)
  824        * <li>InterfaceMethodRef: (NYI) a method handle to invoke on that call site's arguments
  825        * </ul>
  826        * @params hostClass context for linkage, access control, protection domain, and class loader
  827        * @params data      bytes of a class file
  828        * @params cpPatches where non-null entries exist, they replace corresponding CP entries in data
  829        */
  830       public native Class defineAnonymousClass(Class hostClass, byte[] data, Object[] cpPatches);
  831   
  832   
  833       /** Allocate an instance but do not run any constructor.
  834           Initializes the class if it has not yet been. */
  835       public native Object allocateInstance(Class cls)
  836           throws InstantiationException;
  837   
  838       /** Lock the object.  It must get unlocked via {@link #monitorExit}. */
  839       public native void monitorEnter(Object o);
  840   
  841       /**
  842        * Unlock the object.  It must have been locked via {@link
  843        * #monitorEnter}.
  844        */
  845       public native void monitorExit(Object o);
  846   
  847       /**
  848        * Tries to lock the object.  Returns true or false to indicate
  849        * whether the lock succeeded.  If it did, the object must be
  850        * unlocked via {@link #monitorExit}.
  851        */
  852       public native boolean tryMonitorEnter(Object o);
  853   
  854       /** Throw the exception without telling the verifier. */
  855       public native void throwException(Throwable ee);
  856   
  857   
  858       /**
  859        * Atomically update Java variable to <tt>x</tt> if it is currently
  860        * holding <tt>expected</tt>.
  861        * @return <tt>true</tt> if successful
  862        */
  863       public final native boolean compareAndSwapObject(Object o, long offset,
  864                                                        Object expected,
  865                                                        Object x);
  866   
  867       /**
  868        * Atomically update Java variable to <tt>x</tt> if it is currently
  869        * holding <tt>expected</tt>.
  870        * @return <tt>true</tt> if successful
  871        */
  872       public final native boolean compareAndSwapInt(Object o, long offset,
  873                                                     int expected,
  874                                                     int x);
  875   
  876       /**
  877        * Atomically update Java variable to <tt>x</tt> if it is currently
  878        * holding <tt>expected</tt>.
  879        * @return <tt>true</tt> if successful
  880        */
  881       public final native boolean compareAndSwapLong(Object o, long offset,
  882                                                      long expected,
  883                                                      long x);
  884   
  885       /**
  886        * Fetches a reference value from a given Java variable, with volatile
  887        * load semantics. Otherwise identical to {@link #getObject(Object, long)}
  888        */
  889       public native Object getObjectVolatile(Object o, long offset);
  890   
  891       /**
  892        * Stores a reference value into a given Java variable, with
  893        * volatile store semantics. Otherwise identical to {@link #putObject(Object, long, Object)}
  894        */
  895       public native void    putObjectVolatile(Object o, long offset, Object x);
  896   
  897       /** Volatile version of {@link #getInt(Object, long)}  */
  898       public native int     getIntVolatile(Object o, long offset);
  899   
  900       /** Volatile version of {@link #putInt(Object, long, int)}  */
  901       public native void    putIntVolatile(Object o, long offset, int x);
  902   
  903       /** Volatile version of {@link #getBoolean(Object, long)}  */
  904       public native boolean getBooleanVolatile(Object o, long offset);
  905   
  906       /** Volatile version of {@link #putBoolean(Object, long, boolean)}  */
  907       public native void    putBooleanVolatile(Object o, long offset, boolean x);
  908   
  909       /** Volatile version of {@link #getByte(Object, long)}  */
  910       public native byte    getByteVolatile(Object o, long offset);
  911   
  912       /** Volatile version of {@link #putByte(Object, long, byte)}  */
  913       public native void    putByteVolatile(Object o, long offset, byte x);
  914   
  915       /** Volatile version of {@link #getShort(Object, long)}  */
  916       public native short   getShortVolatile(Object o, long offset);
  917   
  918       /** Volatile version of {@link #putShort(Object, long, short)}  */
  919       public native void    putShortVolatile(Object o, long offset, short x);
  920   
  921       /** Volatile version of {@link #getChar(Object, long)}  */
  922       public native char    getCharVolatile(Object o, long offset);
  923   
  924       /** Volatile version of {@link #putChar(Object, long, char)}  */
  925       public native void    putCharVolatile(Object o, long offset, char x);
  926   
  927       /** Volatile version of {@link #getLong(Object, long)}  */
  928       public native long    getLongVolatile(Object o, long offset);
  929   
  930       /** Volatile version of {@link #putLong(Object, long, long)}  */
  931       public native void    putLongVolatile(Object o, long offset, long x);
  932   
  933       /** Volatile version of {@link #getFloat(Object, long)}  */
  934       public native float   getFloatVolatile(Object o, long offset);
  935   
  936       /** Volatile version of {@link #putFloat(Object, long, float)}  */
  937       public native void    putFloatVolatile(Object o, long offset, float x);
  938   
  939       /** Volatile version of {@link #getDouble(Object, long)}  */
  940       public native double  getDoubleVolatile(Object o, long offset);
  941   
  942       /** Volatile version of {@link #putDouble(Object, long, double)}  */
  943       public native void    putDoubleVolatile(Object o, long offset, double x);
  944   
  945       /**
  946        * Version of {@link #putObjectVolatile(Object, long, Object)}
  947        * that does not guarantee immediate visibility of the store to
  948        * other threads. This method is generally only useful if the
  949        * underlying field is a Java volatile (or if an array cell, one
  950        * that is otherwise only accessed using volatile accesses).
  951        */
  952       public native void    putOrderedObject(Object o, long offset, Object x);
  953   
  954       /** Ordered/Lazy version of {@link #putIntVolatile(Object, long, int)}  */
  955       public native void    putOrderedInt(Object o, long offset, int x);
  956   
  957       /** Ordered/Lazy version of {@link #putLongVolatile(Object, long, long)} */
  958       public native void    putOrderedLong(Object o, long offset, long x);
  959   
  960       /**
  961        * Unblock the given thread blocked on <tt>park</tt>, or, if it is
  962        * not blocked, cause the subsequent call to <tt>park</tt> not to
  963        * block.  Note: this operation is "unsafe" solely because the
  964        * caller must somehow ensure that the thread has not been
  965        * destroyed. Nothing special is usually required to ensure this
  966        * when called from Java (in which there will ordinarily be a live
  967        * reference to the thread) but this is not nearly-automatically
  968        * so when calling from native code.
  969        * @param thread the thread to unpark.
  970        *
  971        */
  972       public native void unpark(Object thread);
  973   
  974       /**
  975        * Block current thread, returning when a balancing
  976        * <tt>unpark</tt> occurs, or a balancing <tt>unpark</tt> has
  977        * already occurred, or the thread is interrupted, or, if not
  978        * absolute and time is not zero, the given time nanoseconds have
  979        * elapsed, or if absolute, the given deadline in milliseconds
  980        * since Epoch has passed, or spuriously (i.e., returning for no
  981        * "reason"). Note: This operation is in the Unsafe class only
  982        * because <tt>unpark</tt> is, so it would be strange to place it
  983        * elsewhere.
  984        */
  985       public native void park(boolean isAbsolute, long time);
  986   
  987       /**
  988        * Gets the load average in the system run queue assigned
  989        * to the available processors averaged over various periods of time.
  990        * This method retrieves the given <tt>nelem</tt> samples and
  991        * assigns to the elements of the given <tt>loadavg</tt> array.
  992        * The system imposes a maximum of 3 samples, representing
  993        * averages over the last 1,  5,  and  15 minutes, respectively.
  994        *
  995        * @params loadavg an array of double of size nelems
  996        * @params nelems the number of samples to be retrieved and
  997        *         must be 1 to 3.
  998        *
  999        * @return the number of samples actually retrieved; or -1
 1000        *         if the load average is unobtainable.
 1001        */
 1002       public native int getLoadAverage(double[] loadavg, int nelems);
 1003   }

 Save This PageHome » openjdk-7 » sun » misc » [javadoc | source]