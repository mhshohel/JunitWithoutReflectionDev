package callgraphstat;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;

public class VisitClass extends EmptyVisitor {
	private JavaClass clazz;
	private ConstantPoolGen constants;

	public VisitClass(JavaClass jc) {
		clazz = jc;
		constants = new ConstantPoolGen(clazz.getConstantPool());
	}

	public void visitJavaClass(JavaClass jc) {
		jc.getConstantPool().accept(this);
		Field[] fields = jc.getFields();
	}

	public void visitConstantPool(ConstantPool constantPool) {
		for (int i = 0; i < constantPool.getLength(); i++) {
			Constant constant = constantPool.getConstant(i);
			if (constant == null)
				continue;
			if (constant.getTag() == 7) {
				constantPool.constantToString(constant);
			}
		}
	}

	public void start() {
		visitJavaClass(clazz);
	}
}
