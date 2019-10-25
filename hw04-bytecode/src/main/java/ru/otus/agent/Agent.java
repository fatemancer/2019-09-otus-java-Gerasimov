package ru.otus.agent;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Agent {

    private static final int API = Opcodes.ASM7;

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("============================================");
        System.out.println("#Called premain method, injecting logger...#");
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(
                    ClassLoader loader,
                    String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer
            )  {
                if (className.equals("ru/otus/Example")) {
                    return addLogger(classfileBuffer);
                }
                return classfileBuffer;
            }
        });
        System.out.println("#Injection done.                           #");
        System.out.println("============================================");
        System.out.println();
    }

    private static byte[] addLogger(byte[] classfileBuffer) {
        return addMethod(classfileBuffer);
    }

    private static byte[] addMethod(byte[] classfileBuffer) {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(API, cw) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new PatchedMethodVisitor(API, methodVisitor, access, name, descriptor);
            }
        };
        cr.accept(cv, API);
        return cw.toByteArray();
    }
}
