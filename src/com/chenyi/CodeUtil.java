package com.chenyi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: chenyi.zsq
 * @Date: 2018/11/2
 */
public class CodeUtil {

    public static PsiClass getSPIMethods(PsiFile serviceFile, Project project, PsiFile psiFile, PsiClass sourceClass,
                                         boolean isNeedModule) {

        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        for (PsiElement child : serviceFile.getChildren()) {
            if (child instanceof PsiClass) {
                PsiClass psiClass = (PsiClass)child;
                for (PsiMethod method : psiClass.getAllMethods()) {
                    if (method instanceof PsiMethodImpl) {
                        String methodContent = getSpiMethod(method, isNeedModule);
                        PsiMethod spiMethod = elementFactory.createMethodFromText(methodContent, psiFile);
                        sourceClass.add(spiMethod);
                    }
                }
            } else if (child instanceof PsiImportList) {
                psiFile.add(child);
            }
        }
        return sourceClass;
    }

    public static PsiClass getSPIImplMethods(PsiFile serviceFile, Project project, PsiFile psiFile,
                                              PsiClass spiClass,boolean isNeedModule) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        String className = psiFile.getName().substring(0, psiFile.getName().length() - 5);
        PsiClass sourceClass = elementFactory.createClass(className);
        sourceClass.setName(className);
        PsiField psiField = elementFactory.createFieldFromText(CodeUtil.getFieldContent(serviceFile.getName()),psiFile);
        sourceClass.add(psiField);
        for (PsiElement child : serviceFile.getChildren()) {
            if (child instanceof PsiClass) {
                PsiClass psiClass = (PsiClass)child;
                for (PsiMethod method : psiClass.getAllMethods()) {
                    if (method instanceof PsiMethodImpl) {
                        PsiMethod targetMethod = elementFactory.createMethodFromText(
                            CodeUtil.getMethodContent(method,serviceFile.getName(),isNeedModule), child);
                        sourceClass.add(targetMethod);
                    }
                }
            } else if (child instanceof PsiImportList) {
                PsiImportList importList = (PsiImportList)child;
                PsiImportStatement importStatement = elementFactory.createImportStatement(spiClass);
                importList.add(importStatement);
                psiFile.add(importList);
            }
        }
        return sourceClass;
    }


    public static String getClassContent(String className) {
        StringBuilder strb = new StringBuilder();
        String spiName = className.substring(0, className.length() - 4);
        strb.append("@Service(\"").append(spiName).append("\")\n")
            .append("public class ").append(className).append(" implements ")
            .append(spiName).append("{\n}");
        String result = strb.toString();
        return result;
    }

    /**
     * 拼接SPI方法
     * @param method 方法
     * @param isNeedModule 返回值是否去掉第一层泛型嵌套
     * @return
     */
    public static String getSpiMethod(PsiMethod method, boolean isNeedModule) {
        StringBuilder strb = new StringBuilder();
        String returnType = method.getReturnType().getPresentableText();
        strb.append(isNeedModule == true ? getResultModule(returnType) : returnType)
            .append(" ")
            .append(method.getName()).append("(");
        PsiParameter[] psiParameters = method.getParameterList().getParameters();
        for (int i = 0; i < psiParameters.length; i++) {
            PsiParameter parameter = psiParameters[i];
            strb.append(parameter.getType().getPresentableText()).append(" ").append(parameter.getName());
            if (i != psiParameters.length - 1) {
                strb.append(", ");
            }
        }
        strb.append(");");
        return strb.toString();
    }

    /**
     * 拼接SPI实现 方法
     * @param method 方法
     * @param serviceName 服务类名
     * @param isNeedModule 返回值是否去掉第一层泛型嵌套
     * @return
     */
    public static String getMethodContent(PsiMethod method, String serviceName,boolean isNeedModule) {
        StringBuilder strb = new StringBuilder();
        String fieldName =serviceName.toLowerCase().substring(0, 1) + serviceName.substring(1, serviceName.length() - 5);
        String returnType = method.getReturnType().getPresentableText();
        strb.append("@Override\n")
            .append("public ")
            .append(isNeedModule == true ? getResultModule(returnType) : returnType)
            .append(" ")
            .append(method.getName()).append("(");
        PsiParameter[] psiParameters = method.getParameterList().getParameters();
        for (int i = 0; i < psiParameters.length; i++) {
            PsiParameter parameter = method.getParameterList().getParameters()[i];
            strb.append(parameter.getType().getPresentableText()).append(" ").append(parameter.getName());
            if (i != psiParameters.length - 1) {
                strb.append(", ");
            }
        }
        strb.append("){\n");
        // 格式化调用代码
        strb.append("return ").append(fieldName).append(".");
        strb.append(method.getName())
            .append("(");
        for (int i = 0; i < psiParameters.length; i++) {
            PsiParameter parameter = method.getParameterList().getParameters()[i];
            strb.append(parameter.getName());
            if (i != psiParameters.length - 1) {
                strb.append(", ");
            }
        }
        if (isNeedModule) {
            strb.append(").getModule(");
        }
        strb.append(");\n");
        strb.append("}");
        return strb.toString();
    }

    // 去掉第一层泛型
    private static String getResultModule(String typeText) {
        return typeText.replaceAll("\\w+\\<(.+)\\>", "$1");
    }


    public static PsiFile getServiceInterface(Project project, String name) {
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, name, GlobalSearchScope.projectScope(project));
        if (psiFiles.length > 0) {
            return psiFiles[0];
        }
        return null;
    }

    /**
     * 获取 字段 内容
     * @param serviceName 服务类名
     * @return
     */
    public static String getFieldContent(String serviceName) {
        StringBuilder strb = new StringBuilder();
        String name = serviceName.toLowerCase().substring(0, 1) + serviceName.substring(1, serviceName.length() - 5);
        strb.append("@Resource\n")
            .append("private ")
            .append(serviceName, 0, serviceName.length() - 5)
            .append(" ")
            .append(name)
            .append(";");
        return strb.toString();
    }


    public static String getFilePackageName(VirtualFile dir) {
        if(!dir.isDirectory()) {
            // 非目录的取所在文件夹路径
            dir = dir.getParent();
        }
        String path = dir.getPath().replace("/", ".");
        String preText = "src.main.java";
        int preIndex = path.indexOf(preText) + preText.length() + 1;
        path = path.substring(preIndex);
        return path;
    }

}
