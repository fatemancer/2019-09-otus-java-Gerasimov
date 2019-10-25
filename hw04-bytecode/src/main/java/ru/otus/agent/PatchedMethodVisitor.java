package ru.otus.agent;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class PatchedMethodVisitor extends AdviceAdapter {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";

    private boolean hasLoggingAnnotation;

    protected PatchedMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if ("Lru/otus/annotation/MagicBytecodeLogging;".equals(descriptor)) {
            hasLoggingAnnotation = true;
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    protected void onMethodEnter() {
        if (hasLoggingAnnotation) {

            Handle handle = new Handle(
                    H_INVOKESTATIC,
                    Type.getInternalName(java.lang.invoke.StringConcatFactory.class),
                    "makeConcatWithConstants",
                    MethodType.methodType(
                            CallSite.class,
                            MethodHandles.Lookup.class,
                            String.class,
                            MethodType.class,
                            String.class,
                            Object[].class
                    ).toMethodDescriptorString(),
                    false);

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitInvokeDynamicInsn(
                    "makeConcatWithConstants",
                    "(III)Ljava/lang/String;",
                    handle,
                    ANSI_RED + "Bytecode logger says: Method invoked, arguments: [\u0001, \u0001, \u0001] " + ANSI_RESET);
            mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/io/PrintStream",
                    "println",
                    "(Ljava/lang/String;)V",
                    false);
            super.onMethodEnter();
        }
    }
}