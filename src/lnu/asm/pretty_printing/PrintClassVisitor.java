/**
 * 
 */
package lnu.asm.pretty_printing;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.util.TraceSignatureVisitor;

/**
 * @author jonasl
 *
 */
public class PrintClassVisitor extends ClassVisitor {
	private StringBuilder buf = new StringBuilder();

	public PrintClassVisitor() {
		super(Opcodes.ASM4);
	}

	public PrintClassVisitor(ClassVisitor arg1) {
		super(Opcodes.ASM4, arg1);
	}
	
    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for internal
     * type names in bytecode notation.
     */
    public static final int INTERNAL_NAME = 0;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for field
     * descriptors, formatted in bytecode notation
     */
    public static final int FIELD_DESCRIPTOR = 1;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for field
     * signatures, formatted in bytecode notation
     */
    public static final int FIELD_SIGNATURE = 2;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for method
     * descriptors, formatted in bytecode notation
     */
    public static final int METHOD_DESCRIPTOR = 3;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for method
     * signatures, formatted in bytecode notation
     */
    public static final int METHOD_SIGNATURE = 4;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for class
     * signatures, formatted in bytecode notation
     */
    public static final int CLASS_SIGNATURE = 5;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for field or
     * method return value signatures, formatted in default Java notation
     * (non-bytecode)
     */
    public static final int TYPE_DECLARATION = 6;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for class
     * signatures, formatted in default Java notation (non-bytecode)
     */
    public static final int CLASS_DECLARATION = 7;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for method
     * parameter signatures, formatted in default Java notation (non-bytecode)
     */
    public static final int PARAMETERS_DECLARATION = 8;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for handle
     * descriptors, formatted in bytecode notation
     */
    public static final int HANDLE_DESCRIPTOR = 9;

    /**
     * Tab for class members.
     */
    protected String tab = "  ";

    /**
     * Tab for bytecode instructions.
     */
    protected String tab2 = "    ";

    /**
     * Tab for table and lookup switch instructions.
     */
    protected String tab3 = "      ";

    /**
     * Tab for labels.
     */
    protected String ltab = "   ";

    /**
     * The label names. This map associate String values to Label keys.
     */
    protected Map<Label, String> labelNames;

    
	
	
    /**
     * Visits the header of the class.
     * 
     * @param version
     *            the class version.
     * @param access
     *            the class's access flags (see {@link Opcodes}). This parameter
     *            also indicates if the class is deprecated.
     * @param name
     *            the internal name of the class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param signature
     *            the signature of this class. May be <tt>null</tt> if the class
     *            is not a generic one, and does not extend or implement generic
     *            classes or interfaces.
     * @param superName
     *            the internal of name of the super class (see
     *            {@link Type#getInternalName() getInternalName}). For
     *            interfaces, the super class is {@link Object}. May be
     *            <tt>null</tt>, but only for the {@link Object} class.
     * @param interfaces
     *            the internal names of the class's interfaces (see
     *            {@link Type#getInternalName() getInternalName}). May be
     *            <tt>null</tt>.
     */
	@Override
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        int major = version & 0xFFFF;
        int minor = version >>> 16;
        buf.setLength(0);
        buf.append("// class version ").append(major).append('.').append(minor)
                .append(" (").append(version).append(")\n");
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            buf.append("// DEPRECATED\n");
        }
        buf.append("// access flags 0x")
                .append(Integer.toHexString(access).toUpperCase()).append('\n');

        appendDescriptor(CLASS_SIGNATURE, signature);
        if (signature != null) {
            TraceSignatureVisitor sv = new TraceSignatureVisitor(access);
            SignatureReader r = new SignatureReader(signature);
            r.accept(sv);
            buf.append("// declaration: ").append(name)
                    .append(sv.getDeclaration()).append('\n');
        }

        appendAccess(access & ~Opcodes.ACC_SUPER);
        if ((access & Opcodes.ACC_ANNOTATION) != 0) {
            buf.append("@interface ");
        } else if ((access & Opcodes.ACC_INTERFACE) != 0) {
            buf.append("interface ");
        } else if ((access & Opcodes.ACC_ENUM) == 0) {
            buf.append("class ");
        }
        appendDescriptor(INTERNAL_NAME, name);

        if (superName != null && !"java/lang/Object".equals(superName)) {
            buf.append(" extends ");
            appendDescriptor(INTERNAL_NAME, superName);
            buf.append(' ');
        }
        if (interfaces != null && interfaces.length > 0) {
            buf.append(" implements ");
            for (int i = 0; i < interfaces.length; ++i) {
                appendDescriptor(INTERNAL_NAME, interfaces[i]);
                buf.append(' ');
            }
        }
        buf.append(" {\n");
        System.out.print(buf.toString());
		
		
		
//        System.out.println("Visiting class: "+name);
//        System.out.println("Class Major Version: "+version);
//        System.out.println("Super class: "+superName);
//        super.visit(version, access, name, signature, superName, interfaces);
    }

    /**
     * Visits the source of the class.
     * 
     * @param source
     *            the name of the source file from which the class was compiled.
     *            May be <tt>null</tt>.
     * @param debug
     *            additional debug information to compute the correspondance
     *            between source and compiled elements of the class. May be
     *            <tt>null</tt>.
     */
	@Override
    public void visitSource(String file, String debug) {
        buf.setLength(0);
        if (file != null) {
            buf.append(tab).append("// compiled from: ").append(file)
                    .append('\n');
        }
        if (debug != null) {
            buf.append(tab).append("// debug info: ").append(debug)
                    .append('\n');
        }
        if (buf.length() > 0) {
        	System.out.print(buf.toString());
        }
		
		
//        System.out.println("Source: "+source);
//        super.visitSource(source, debug);
    }

    /**
     * Visits the enclosing class of the class. This method must be called only
     * if the class has an enclosing class.
     * 
     * @param owner
     *            internal name of the enclosing class of the class.
     * @param name
     *            the name of the method that contains the class, or
     *            <tt>null</tt> if the class is not enclosed in a method of its
     *            enclosing class.
     * @param desc
     *            the descriptor of the method that contains the class, or
     *            <tt>null</tt> if the class is not enclosed in a method of its
     *            enclosing class.
     */
	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		buf.setLength(0);
		buf.append(tab).append("OUTERCLASS ");
		appendDescriptor(INTERNAL_NAME, owner);
		buf.append(' ');
		if (name != null) {
			buf.append(name).append(' ');
		}
		appendDescriptor(METHOD_DESCRIPTOR, desc);
		System.out.println(buf.toString());
		//        System.out.println("Outer class: "+owner);
		//        super.visitOuterClass(owner, name, desc);
	}

    /**
     * Visits an annotation of the class.
     * 
     * @param desc
     *            the class descriptor of the annotation class.
     * @param visible
     *            <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <tt>null</tt> if
     *         this visitor is not interested in visiting this annotation.
     */
	@Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return new PrintAnnotationVisitor();
//        System.out.println("Annotation: "+desc);
//        return super.visitAnnotation(desc, visible);
    }

    /**
     * Visits a non standard attribute of the class.
     * 
     * @param attr
     *            an attribute.
     */
	@Override
    public void visitAttribute(Attribute attr) {
        System.out.println("Class Attribute: "+attr.type);
        super.visitAttribute(attr);
    }

    /**
     * Visits information about an inner class. This inner class is not
     * necessarily a member of the class being visited.
     * 
     * @param name
     *            the internal name of an inner class (see
     *            {@link Type#getInternalName() getInternalName}).
     * @param outerName
     *            the internal name of the class to which the inner class
     *            belongs (see {@link Type#getInternalName() getInternalName}).
     *            May be <tt>null</tt> for not member classes.
     * @param innerName
     *            the (simple) name of the inner class inside its enclosing
     *            class. May be <tt>null</tt> for anonymous inner classes.
     * @param access
     *            the access flags of the inner class as originally declared in
     *            the enclosing class.
     */
	@Override
	public void visitInnerClass(String name, String outerName,
			String innerName, int access) {
		buf.setLength(0);
		buf.append(tab).append("// access flags 0x");
		buf.append(
				Integer.toHexString(access & ~Opcodes.ACC_SUPER).toUpperCase())
				.append('\n');
		buf.append(tab);
		appendAccess(access);
		buf.append("INNERCLASS ");
		appendDescriptor(INTERNAL_NAME, name);
		buf.append(' ');
		appendDescriptor(INTERNAL_NAME, outerName);
		buf.append(' ');
		appendDescriptor(INTERNAL_NAME, innerName);
		System.out.println(buf.toString());
		//        System.out.println("Inner Class: "+ innerName+" defined in "+outerName);
		//        super.visitInnerClass(name, outerName, innerName, access);
	}

    /**
     * Visits a field of the class.
     * 
     * @param access
     *            the field's access flags (see {@link Opcodes}). This parameter
     *            also indicates if the field is synthetic and/or deprecated.
     * @param name
     *            the field's name.
     * @param desc
     *            the field's descriptor (see {@link Type Type}).
     * @param signature
     *            the field's signature. May be <tt>null</tt> if the field's
     *            type does not use generic types.
     * @param value
     *            the field's initial value. This parameter, which may be
     *            <tt>null</tt> if the field does not have an initial value,
     *            must be an {@link Integer}, a {@link Float}, a {@link Long}, a
     *            {@link Double} or a {@link String} (for <tt>int</tt>,
     *            <tt>float</tt>, <tt>long</tt> or <tt>String</tt> fields
     *            respectively). <i>This parameter is only used for static
     *            fields</i>. Its value is ignored for non static fields, which
     *            must be initialized through bytecode instructions in
     *            constructors or methods.
     * @return a visitor to visit field annotations and attributes, or
     *         <tt>null</tt> if this class visitor is not interested in visiting
     *         these annotations and attributes.
     */
	@Override
    public FieldVisitor visitField(int access, String name, String desc,
            String signature, Object value) {
        buf.setLength(0);
        buf.append('\n');
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            buf.append(tab).append("// DEPRECATED\n");
        }
        buf.append(tab).append("// access flags 0x")
                .append(Integer.toHexString(access).toUpperCase()).append('\n');
        if (signature != null) {
            buf.append(tab);
            appendDescriptor(FIELD_SIGNATURE, signature);

            TraceSignatureVisitor sv = new TraceSignatureVisitor(0);
            SignatureReader r = new SignatureReader(signature);
            r.acceptType(sv);
            buf.append(tab).append("// declaration: ")
                    .append(sv.getDeclaration()).append('\n');
        }

        buf.append(tab);
        appendAccess(access);

        appendDescriptor(FIELD_DESCRIPTOR, desc);
        buf.append(' ').append(name);
        if (value != null) {
            buf.append(" = ");
            if (value instanceof String) {
                buf.append('\"').append(value).append('\"');
            } else {
                buf.append(value);
            }
        }

        System.out.println(buf.toString());
		
        //System.out.println("Field: "+name+" "+desc+" value:"+value);
        return super.visitField(access, name, desc, signature, value);
    }

    /**
     * Visits a method of the class. This method <i>must</i> return a new
     * {@link MethodVisitor} instance (or <tt>null</tt>) each time it is called,
     * i.e., it should not return a previously returned visitor.
     * 
     * @param access
     *            the method's access flags (see {@link Opcodes}). This
     *            parameter also indicates if the method is synthetic and/or
     *            deprecated.
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     * @param signature
     *            the method's signature. May be <tt>null</tt> if the method
     *            parameters, return type and exceptions do not use generic
     *            types.
     * @param exceptions
     *            the internal names of the method's exception classes (see
     *            {@link Type#getInternalName() getInternalName}). May be
     *            <tt>null</tt>.
     * @return an object to visit the byte code of the method, or <tt>null</tt>
     *         if this class visitor is not interested in visiting the code of
     *         this method.
     */
	@Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) {
        buf.setLength(0);
        buf.append('\n');
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            buf.append(tab).append("// DEPRECATED\n");
        }
        buf.append(tab).append("// access flags 0x")
                .append(Integer.toHexString(access).toUpperCase()).append('\n');

        if (signature != null) {
            buf.append(tab);
            appendDescriptor(METHOD_SIGNATURE, signature);

            TraceSignatureVisitor v = new TraceSignatureVisitor(0);
            SignatureReader r = new SignatureReader(signature);
            r.accept(v);
            String genericDecl = v.getDeclaration();
            String genericReturn = v.getReturnType();
            String genericExceptions = v.getExceptions();

            buf.append(tab).append("// declaration: ").append(genericReturn)
                    .append(' ').append(name).append(genericDecl);
            if (genericExceptions != null) {
                buf.append(" throws ").append(genericExceptions);
            }
            buf.append('\n');
        }

        buf.append(tab);
        appendAccess(access);
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            buf.append("native ");
        }
        if ((access & Opcodes.ACC_VARARGS) != 0) {
            buf.append("varargs ");
        }
        if ((access & Opcodes.ACC_BRIDGE) != 0) {
            buf.append("bridge ");
        }

        buf.append(name);
        appendDescriptor(METHOD_DESCRIPTOR, desc);
        if (exceptions != null && exceptions.length > 0) {
            buf.append(" throws ");
            for (int i = 0; i < exceptions.length; ++i) {
                appendDescriptor(INTERNAL_NAME, exceptions[i]);
                buf.append(' ');
            }
        }

        System.out.println(buf.toString());
        //System.out.println("Method: "+name+" "+desc);
        return new PrintMethodVisitor();
        // return cv.visitMethod(access, name, desc, signature, exceptions);
    }

    /**
     * Visits the end of the class. This method, which is the last one to be
     * called, is used to inform the visitor that all the fields and methods of
     * the class have been visited.
     */
	@Override
    public void visitEnd() {
        System.out.println("} // Class ends here\n");
        super.visitEnd();
    }
	
	
	
    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------



    /**
     * Appends an internal name, a type descriptor or a type signature to
     * {@link #buf buf}.
     * 
     * @param type
     *            indicates if desc is an internal name, a field descriptor, a
     *            method descriptor, a class signature, ...
     * @param desc
     *            an internal name, type descriptor, or type signature. May be
     *            <tt>null</tt>.
     */
    protected void appendDescriptor(final int type, final String desc) {
        if (type == CLASS_SIGNATURE || type == FIELD_SIGNATURE
                || type == METHOD_SIGNATURE) {
            if (desc != null) {
                buf.append("// signature ").append(desc).append('\n');
            }
        } else {
            buf.append(desc);
        }
    }

    /**
     * Appends the name of the given label to {@link #buf buf}. Creates a new
     * label name if the given label does not yet have one.
     * 
     * @param l
     *            a label.
     */
    protected void appendLabel(final Label l) {
        if (labelNames == null) {
            labelNames = new HashMap<Label, String>();
        }
        String name = labelNames.get(l);
        if (name == null) {
            name = "L" + labelNames.size();
            labelNames.put(l, name);
        }
        buf.append(name);
    }

    /**
     * Appends the information about the given handle to {@link #buf buf}.
     * 
     * @param h
     *            a handle, non null.
     */
    protected void appendHandle(final Handle h) {
        buf.append('\n').append(tab3);
        int tag = h.getTag();
        buf.append("// handle kind 0x").append(Integer.toHexString(tag))
                .append(" : ");
        switch (tag) {
        case Opcodes.H_GETFIELD:
            buf.append("GETFIELD");
            break;
        case Opcodes.H_GETSTATIC:
            buf.append("GETSTATIC");
            break;
        case Opcodes.H_PUTFIELD:
            buf.append("PUTFIELD");
            break;
        case Opcodes.H_PUTSTATIC:
            buf.append("PUTSTATIC");
            break;
        case Opcodes.H_INVOKEINTERFACE:
            buf.append("INVOKEINTERFACE");
            break;
        case Opcodes.H_INVOKESPECIAL:
            buf.append("INVOKESPECIAL");
            break;
        case Opcodes.H_INVOKESTATIC:
            buf.append("INVOKESTATIC");
            break;
        case Opcodes.H_INVOKEVIRTUAL:
            buf.append("INVOKEVIRTUAL");
            break;
        case Opcodes.H_NEWINVOKESPECIAL:
            buf.append("NEWINVOKESPECIAL");
            break;
        }
        buf.append('\n');
        buf.append(tab3);
        appendDescriptor(INTERNAL_NAME, h.getOwner());
        buf.append('.');
        buf.append(h.getName());
        buf.append('(');
        appendDescriptor(HANDLE_DESCRIPTOR, h.getDesc());
        buf.append(')').append('\n');
    }

    /**
     * Appends a string representation of the given access modifiers to
     * {@link #buf buf}.
     * 
     * @param access
     *            some access modifiers.
     */
    private void appendAccess(final int access) {
        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            buf.append("public ");
        }
        if ((access & Opcodes.ACC_PRIVATE) != 0) {
            buf.append("private ");
        }
        if ((access & Opcodes.ACC_PROTECTED) != 0) {
            buf.append("protected ");
        }
        if ((access & Opcodes.ACC_FINAL) != 0) {
            buf.append("final ");
        }
        if ((access & Opcodes.ACC_STATIC) != 0) {
            buf.append("static ");
        }
        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) {
            buf.append("synchronized ");
        }
        if ((access & Opcodes.ACC_VOLATILE) != 0) {
            buf.append("volatile ");
        }
        if ((access & Opcodes.ACC_TRANSIENT) != 0) {
            buf.append("transient ");
        }
        if ((access & Opcodes.ACC_ABSTRACT) != 0) {
            buf.append("abstract ");
        }
        if ((access & Opcodes.ACC_STRICT) != 0) {
            buf.append("strictfp ");
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            buf.append("synthetic ");
        }
        if ((access & Opcodes.ACC_ENUM) != 0) {
            buf.append("enum ");
        }
    }




//	
//	/**
//     * Called when a class is visited. This is the method called first
//     */
//    @Override
//    public void visit(int version, int access, String name,
//            String signature, String superName, String[] interfaces) {
//    }
//    
//    /**
//     * Invoked only when the class being visited is an inner class
//     */
//    @Override
//    public void visitOuterClass(String owner, String name, String desc) {
//        System.out.println("Outer class: "+owner);
//        super.visitOuterClass(owner, name, desc);
//    }
//
//    /**
//     *Invoked when a class level annotation is encountered
//     */
//    @Override
//    public AnnotationVisitor visitAnnotation(String desc,
//            boolean visible) {
//        System.out.println("Annotation: "+desc);
//        return super.visitAnnotation(desc, visible);
//    }
//
//    /**
//     * When a class attribute is encountered 
//     */
//    @Override
//    public void visitAttribute(Attribute attr) {
//        System.out.println("Class Attribute: "+attr.type);
//        super.visitAttribute(attr);
//    }
//
//    /**
//     *When an inner class is encountered 
//     */
//    @Override
//    public void visitInnerClass(String name, String outerName,
//            String innerName, int access) {
//        System.out.println("Inner Class: "+ innerName+" defined in "+outerName);
//        super.visitInnerClass(name, outerName, innerName, access);
//    }
//    
//    /**
//     * When a field is encountered
//     */
//    @Override
//    public FieldVisitor visitField(int access, String name,
//            String desc, String signature, Object value) {
//        System.out.println("Field: "+name+" "+desc+" value:"+value);
//        return super.visitField(access, name, desc, signature, value);
//    }
//
//    
//    @Override
//    public void visitEnd() {
//    }
//
//    /**
//     * When a method is encountered
//     */
//    @Override
//    public MethodVisitor visitMethod(int access, String name,
//            String desc, String signature, String[] exceptions) {
//        //return super.visitMethod(access, name, desc, signature, exceptions);
//    }
//
//    /**
//     * When the optional source is encountered
//     */
//    @Override
//    public void visitSource(String source, String debug) {
//    }	

}
