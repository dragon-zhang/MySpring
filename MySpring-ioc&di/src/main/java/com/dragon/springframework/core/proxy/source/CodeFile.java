package com.dragon.springframework.core.proxy.source;

import com.dragon.springframework.core.proxy.ProxyHelper;
import com.dragon.springframework.core.proxy.cglib.FastClass;
import com.dragon.springframework.core.proxy.cglib.MethodInterceptor;
import com.dragon.springframework.core.proxy.cglib.MethodProxy;
import com.dragon.springframework.core.proxy.jdk.InvocationHandler;
import com.dragon.springframework.core.proxy.jdk.Proxy;
import com.dragon.springframework.core.proxy.loader.ProxyClassLoader;

import javax.tools.SimpleJavaFileObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 存储java源代码
 *
 * @author SuccessZhang
 * @date 2020/05/13
 */
public class CodeFile extends SimpleJavaFileObject {

    private static final Kind JAVA_SOURCE_FILE = Kind.SOURCE;

    private String src;

    public CodeFile(Class<?>[] interfaces) throws URISyntaxException {
        this(new URI(ProxyHelper.PROXY_CLASS_PREFIX + ProxyHelper.getProxyClassCount() + JAVA_SOURCE_FILE.extension), JAVA_SOURCE_FILE, interfaces);
    }

    private CodeFile(URI uri, Kind kind, Class<?>[] interfaces) {
        super(uri, kind);
        this.src = generateSrc(interfaces);
    }

    /**
     * jdk
     *
     * @see CodeFile#generateDefinitionMethodMemberVariables(List, List, StringBuilder, int)
     * @see CodeFile#generationMethodDefinition(StringBuilder, Method, String, String)
     * @see CodeFile#generateReturn(StringBuilder, String)
     */
    private String generateSrc(Class<?>[] interfaces) {
        //利用反射生成java源代码
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(ProxyClassLoader.class.getPackage().getName()).append(";\n");
        sb.append("import ").append(InvocationHandler.class.getName()).append(";\n");
        sb.append("import java.lang.reflect.*;\n");

        sb.append("public class $Proxy").append(ProxyHelper.getProxyClassCount()).append(" extends ").append(Proxy.class.getName()).append(" implements ");
        for (Class<?> i : interfaces) {
            sb.append(handleInnerClass(i.getName()));
            sb.append(",");
        }
        deleteRedundantChar(sb, "");
        sb.append(" {\n");
        sb.append("public $Proxy").append(ProxyHelper.getProxyClassCount()).append("(InvocationHandler h){\nsuper(h);\n}\n");

        int methodIndex = 0;
        List<Method> methods = new ArrayList<>();
        for (Class<?> i : interfaces) {
            methods.addAll(Arrays.asList(i.getMethods()));
        }
        generateDefinitionMethodMemberVariables(new ArrayList<>(), methods, sb, methodIndex);

        methodIndex = 0;
        for (Method method : methods) {
            String modifiers = Modifier.toString(method.getModifiers());
            String returnTypeName = method.getReturnType().getName();
            generationMethodDefinition(sb, method, modifiers, returnTypeName);

            sb.append("Object $result = null;\n");
            sb.append("try{\n");
            sb.append("$result = super.h.invoke(this,m").append(methodIndex++)
                    .append(",new Object[]{");
            for (Parameter parameter : method.getParameters()) {
                sb.append(parameter.getName());
                sb.append(",");
            }
            deleteRedundantChar(sb, "{");
            sb.append("});\n");
            sb.append("}catch (Throwable t) {\nt.printStackTrace();\n}\n");
            generateReturn(sb, returnTypeName);
            sb.append("}\n");
        }

        sb.append("}\n");
        System.out.println(sb.toString());
        return sb.toString();
    }

    public CodeFile(Class<?> type) throws URISyntaxException {
        this(new URI(ProxyHelper.PROXY_CLASS_PREFIX + ProxyHelper.getProxyClassCount() + JAVA_SOURCE_FILE.extension), JAVA_SOURCE_FILE, type);
    }

    /**
     * @see CodeFile#generateSrc(Class)
     */
    private CodeFile(URI uri, Kind kind, Class<?> type) {
        super(uri, kind);
        this.src = generateSrc(type);
    }

    /**
     * cglib
     *
     * @see CodeFile#implementsFastClass(Class, List, Method[], StringBuilder)
     */
    private String generateSrc(Class<?> type) {
        List<String> names = new ArrayList<>();
        names.add("equals");
        names.add("toString");
        Method[] methods = type.getMethods();
        //利用反射生成java源代码
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(ProxyClassLoader.class.getPackage()
                .getName()).append(";\n");
        sb.append("import ").append(FastClass.class.getName()).append(";\n");
        sb.append("import ").append(MethodInterceptor.class.getName()).append(";\n");
        sb.append("import ").append(MethodProxy.class.getName()).append(";\n");
        sb.append("import java.lang.reflect.*;\n");

        sb.append("public class $Proxy").append(ProxyHelper.getProxyClassCount())
                .append(" extends ").append(handleInnerClass(type.getName())).append(" implements ")
                .append(FastClass.class.getSimpleName()).append(" {\n");
        sb.append("private MethodInterceptor h;\n");
        sb.append("public $Proxy").append(ProxyHelper.getProxyClassCount())
                .append("(MethodInterceptor h){\nthis.h=h;\n}\n");

        int methodIndex = 0;
        generateDefinitionMethodMemberVariables(names,
                new ArrayList<>(Arrays.asList(methods)), sb, methodIndex);

        methodIndex = 0;
        for (Method method : methods) {
            String modifiers = Modifier.toString(method.getModifiers());
            String returnTypeName = method.getReturnType().getName();
            if (!names.contains(method.getName()) && !modifiers.contains("final")
                    && !modifiers.contains("native")) {
                generationMethodDefinition(sb, method, modifiers, returnTypeName);

                sb.append("Object $result = null;\n");
                sb.append("try{\n");
                sb.append("$result = this.h.intercept(this,m").append(methodIndex)
                        .append(",new Object[]{");
                for (Parameter parameter : method.getParameters()) {
                    sb.append(parameter.getName());
                    sb.append(",");
                }
                deleteRedundantChar(sb, "{");
                sb.append("},new MethodProxy(this,m").append(methodIndex++)
                        .append("));\n");

                sb.append("}catch (Throwable t) {\nt.printStackTrace();\n}\n");
                generateReturn(sb, returnTypeName);
                sb.append("}\n");
            }
        }

        implementsFastClass(type, names, methods, sb);

        sb.append("}\n");
        System.out.println(sb.toString());
        return sb.toString();
    }

    private String handleInnerClass(String className) {
        return className.replaceAll("\\$", ".");
    }

    private void generationMethodDefinition(StringBuilder sb, Method method, String modifiers, String returnTypeName) {
        sb.append("@Override\n");
        sb.append(modifiers.replace(" abstract", "")).append(" ");
        sb.append(returnTypeName).append(" ");
        sb.append(method.getName()).append("(");
        for (Parameter parameter : method.getParameters()) {
            Class<?> paramType = parameter.getType();
            String paramTypeName = paramType.getName();
            sb.append(paramTypeName).append(" ").append(parameter.getName());
            sb.append(",");
        }
        deleteRedundantChar(sb, "(");
        sb.append(") {\n");
    }

    private void generateDefinitionMethodMemberVariables(List<String> names, List<Method> methods, StringBuilder sb, int methodIndex) {
        //静态地获取方法
        for (Method method : methods) {
            String modifiers = Modifier.toString(method.getModifiers());
            if (!names.contains(method.getName()) && !modifiers.contains("final") &&
                    !modifiers.contains("native")) {
                sb.append("private static Method m").append(methodIndex++).append(";\n");
            }
        }
        methodIndex = 0;
        sb.append("static {\n");
        sb.append("try {\n");
        for (Method method : methods) {
            String modifiers = Modifier.toString(method.getModifiers());
            if (!names.contains(method.getName()) && !modifiers.contains("final") &&
                    !modifiers.contains("native")) {
                sb.append("m").append(methodIndex++).append(" = ")
                        .append(handleInnerClass(method.getDeclaringClass().getName()))
                        .append(".class").append(".getMethod(\"")
                        .append(method.getName()).append("\",");
                for (Parameter parameter : method.getParameters()) {
                    sb.append(parameter.getType().getName()).append(".class");
                    sb.append(",");
                }
                deleteRedundantChar(sb, "(");
                sb.append(");\n");
            }
        }
        sb.append("} catch (NoSuchMethodException e) {\n");
        sb.append("e.printStackTrace();\n");
        sb.append("}\n");
        sb.append("}\n");
    }

    private void generateReturn(StringBuilder sb, String returnTypeName) {
        if (!"java.lang.Void".equals(returnTypeName) && !"void".equals(returnTypeName)) {
            sb.append("return (").append(returnTypeName).append(")$result;\n");
        }
    }

    /**
     * 实现{@link FastClass}接口。
     *
     * @see FastClass#getIndexAndCache(Method)
     * @see CodeFile#getHashCode(String, Class[])
     */
    private void implementsFastClass(Class<?> type, List<String> names, Method[] methods, StringBuilder sb) {
        //在成员变量加载完成后初始化index并缓存
        sb.append("{\n");
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String modifiers = Modifier.toString(method.getModifiers());
            if (!names.contains(method.getName()) && !modifiers.contains("final")
                    && !modifiers.contains("native")) {
                sb.append("getIndexAndCache(m").append(i).append(");\n");
            }
        }
        sb.append("}\n");

        //实现getIndex
        sb.append("@Override\n");
        sb.append("public int getIndex(String name, Class[] parameterTypes) {\n");
        sb.append("StringBuilder sb = new StringBuilder(name);\n");
        sb.append("for (Class parameterType : parameterTypes) {\n");
        sb.append("sb.append(parameterType.getName());\n");
        sb.append("}\n");
        sb.append("switch (sb.toString().hashCode()) {\n");
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String modifiers = Modifier.toString(method.getModifiers());
            if (!names.contains(method.getName()) && !modifiers.contains("final")
                    && !modifiers.contains("native")) {
                sb.append("case ").append(getHashCode(method.getName(),
                        method.getParameterTypes())).append(":return ")
                        .append(i).append(";\n");
            }
        }
        sb.append("default:break;\n");
        sb.append("}\n");
        sb.append("return -1;\n");
        sb.append("}\n");

        //实现invoke
        sb.append("@Override\n");
        sb.append("public Object invoke(int index, Object obj, Object[] args) throws");
        sb.append(" InvocationTargetException {\n");
        sb.append(handleInnerClass(type.getName())).append(" object = (")
                .append(handleInnerClass(type.getName())).append(") obj;\n");
        sb.append("switch (index) {\n");
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String modifiers = Modifier.toString(method.getModifiers());
            if (!names.contains(method.getName()) && !modifiers.contains("final")
                    && !modifiers.contains("native")) {
                sb.append("case ").append(i).append(":");
                String returnTypeName = method.getReturnType().getName();
                if (!"java.lang.Void".equals(returnTypeName)
                        && !"void".equals(returnTypeName)) {
                    sb.append("return ");
                }
                sb.append("object.").append(method.getName()).append("(");
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (int j = 0; j < parameterTypes.length; j++) {
                    sb.append("(").append(parameterTypes[j].getName())
                            .append(")args[").append(j).append("]").append(",");
                }
                deleteRedundantChar(sb, "(");
                sb.append(");\n");
                if ("java.lang.Void".equals(returnTypeName) ||
                        "void".equals(returnTypeName)) {
                    sb.append("break;\n");
                }
            }
        }
        sb.append("default:break;\n");
        sb.append("}\n");
        sb.append("return null;\n");
        sb.append("}\n");
    }

    private int getHashCode(String name, Class[] parameterTypes) {
        StringBuilder sb = new StringBuilder(name);
        for (Class parameterType : parameterTypes) {
            sb.append(parameterType.getName());
        }
        return sb.toString().hashCode();
    }

    private void deleteRedundantChar(StringBuilder sb, String str) {
        //这里可能会多出1个","
        if (sb.length() - 1 != sb.lastIndexOf(str)) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return this.src;
    }
}