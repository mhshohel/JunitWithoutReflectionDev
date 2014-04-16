/**
 * 
 */
package lnu.asm.pretty_printing;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.Textifier;

/**
 * @author jonasl
 *
 */
public class PrintAnnotationVisitor extends AnnotationVisitor {
	private StringBuilder buf = new StringBuilder();
	
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
	
	private int valueNumber = 0;

	/**
	 * @param arg0
	 */
	public PrintAnnotationVisitor() {
		super(Opcodes.ASM4);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PrintAnnotationVisitor(AnnotationVisitor arg1) {
		super(Opcodes.ASM4, arg1);
	}
	
	
    @Override
    public void visit(final String name, final Object value) {
        buf.setLength(0);
        appendComa(valueNumber++);

        if (name != null) {
            buf.append(name).append('=');
        }

        if (value instanceof String) {
            visitString((String) value);
        } else if (value instanceof Type) {
            visitType((Type) value);
        } else if (value instanceof Byte) {
            visitByte(((Byte) value).byteValue());
        } else if (value instanceof Boolean) {
            visitBoolean(((Boolean) value).booleanValue());
        } else if (value instanceof Short) {
            visitShort(((Short) value).shortValue());
        } else if (value instanceof Character) {
            visitChar(((Character) value).charValue());
        } else if (value instanceof Integer) {
            visitInt(((Integer) value).intValue());
        } else if (value instanceof Float) {
            visitFloat(((Float) value).floatValue());
        } else if (value instanceof Long) {
            visitLong(((Long) value).longValue());
        } else if (value instanceof Double) {
            visitDouble(((Double) value).doubleValue());
        } else if (value.getClass().isArray()) {
            buf.append('{');
            if (value instanceof byte[]) {
                byte[] v = (byte[]) value;
                for (int i = 0; i < v.length; i++) {
                    appendComa(i);
                    visitByte(v[i]);
                }
            } else if (value instanceof boolean[]) {
                boolean[] v = (boolean[]) value;
                for (int i = 0; i < v.length; i++) {
                    appendComa(i);
                    visitBoolean(v[i]);
                }
            } else if (value instanceof short[]) {
                short[] v = (short[]) value;
                for (int i = 0; i < v.length; i++) {
                    appendComa(i);
                    visitShort(v[i]);
                }
            } else if (value instanceof char[]) {
                char[] v = (char[]) value;
                for (int i = 0; i < v.length; i++) {
                    appendComa(i);
                    visitChar(v[i]);
                }
            } else if (value instanceof int[]) {
                int[] v = (int[]) value;
                for (int i = 0; i < v.length; i++) {
                    appendComa(i);
                    visitInt(v[i]);
                }
            } else if (value instanceof long[]) {
                long[] v = (long[]) value;
                for (int i = 0; i < v.length; i++) {
                    appendComa(i);
                    visitLong(v[i]);
                }
            } else if (value instanceof float[]) {
                float[] v = (float[]) value;
                for (int i = 0; i < v.length; i++) {
                    appendComa(i);
                    visitFloat(v[i]);
                }
            } else if (value instanceof double[]) {
                double[] v = (double[]) value;
                for (int i = 0; i < v.length; i++) {
                    appendComa(i);
                    visitDouble(v[i]);
                }
            }
            buf.append('}');
        }

        System.out.println(buf.toString());
    }

    private void visitInt(final int value) {
        buf.append(value);
    }

    private void visitLong(final long value) {
        buf.append(value).append('L');
    }

    private void visitFloat(final float value) {
        buf.append(value).append('F');
    }

    private void visitDouble(final double value) {
        buf.append(value).append('D');
    }

    private void visitChar(final char value) {
        buf.append("(char)").append((int) value);
    }

    private void visitShort(final short value) {
        buf.append("(short)").append(value);
    }

    private void visitByte(final byte value) {
        buf.append("(byte)").append(value);
    }

    private void visitBoolean(final boolean value) {
        buf.append(value);
    }

    private void visitString(final String value) {
        appendString(value);
    }

    private void visitType(final Type value) {
        buf.append(value.getClassName()).append(".class");
    }

    @Override
    public void visitEnum(final String name, final String desc,
            final String value) {
        buf.setLength(0);
        appendComa(valueNumber++);
        if (name != null) {
            buf.append(name).append('=');
        }
        appendDescriptor(FIELD_DESCRIPTOR, desc);
        buf.append('.').append(value);
        System.out.println(buf.toString());
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String desc) {
        buf.setLength(0);
        appendComa(valueNumber++);
        if (name != null) {
            buf.append(name).append('=');
        }
        buf.append('@');
        appendDescriptor(FIELD_DESCRIPTOR, desc);
        buf.append('(');
        
        /* My handling of code below */
        buf.append(" Problem in PrintAnnotationVisitor.visitAnnotation");
        buf.append(")");        
        System.out.println(buf.toString());
        return super.visitAnnotation(name,desc);
        
        
//        text.add(buf.toString());
//        Textifier t = createTextifier();
//        text.add(t.getText());
//        text.add(")");
//        return t;
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        buf.setLength(0);
        appendComa(valueNumber++);
        if (name != null) {
            buf.append(name).append('=');
        }
        buf.append('{');
        
        /* My handling of code below */
        buf.append(" Problem in PrintAnnotationVisitor.visitArray");
        buf.append("}");        
        System.out.println(buf.toString());
        return super.visitArray(name);
        
        
//        text.add(buf.toString());
//        Textifier t = createTextifier();
//        text.add(t.getText());
//        text.add("}");
//        return t;
    }

    @Override
    public void visitEnd() {
    	super.visitEnd();
    }

    
	
    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    private void appendComa(final int i) {
        if (i != 0) {
            buf.append(", ");
        }
    }
    

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
     * Appends a quoted string to a given buffer.
     * 
     * @param buf
     *            the buffer where the string must be added.
     * @param s
     *            the string to be added.
     */
    public void appendString(String s) {
        buf.append('\"');
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\n') {
                buf.append("\\n");
            } else if (c == '\r') {
                buf.append("\\r");
            } else if (c == '\\') {
                buf.append("\\\\");
            } else if (c == '"') {
                buf.append("\\\"");
            } else if (c < 0x20 || c > 0x7f) {
                buf.append("\\u");
                if (c < 0x10) {
                    buf.append("000");
                } else if (c < 0x100) {
                    buf.append("00");
                } else if (c < 0x1000) {
                    buf.append('0');
                }
                buf.append(Integer.toString(c, 16));
            } else {
                buf.append(c);
            }
        }
        buf.append('\"');
    }


}
