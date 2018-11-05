package com.chenyi;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

/**
 * @author: chenyi.zsq
 * @Date: 2018/11/2
 */
public class HelloAction{
    //@Override
    //public void actionPerformed(AnActionEvent event) {
    //    System.out.println("started");
    //    Project project = event.getData(LangDataKeys.PROJECT);
    //    VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);
    //    addSPIClass(project,virtualFile);
    //}
    //
    //
    //private void addSPIClass(Project project, VirtualFile virtualFile) {
    //    System.out.println("addSPIClass");
    //
    //    WriteCommandAction.runWriteCommandAction(project,()->{
    //        VirtualFile packageFile = virtualFile.getParent();
    //        String packagePath = getFilePackageName(packageFile);
    //        String name = "TimeTravelerSPI.java";
    //        VirtualFile classFile = packageFile.findChild(name);
    //        if (classFile == null) {
    //            PsiFile initFile = PsiFileFactory.getInstance(project).createFileFromText(
    //                name, JavaFileType.INSTANCE, "");
    //            // 加到package下
    //            PsiManager.getInstance(project).findDirectory(packageFile).add(initFile);
    //            classFile = packageFile.findChild(name);
    //        }
    //        PsiFile psiFile = PsiManager.getInstance(project).findFile(classFile);
    //
    //        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
    //        String className = "TimeTravelerSPI";
    //        PsiClass sourceClass = elementFactory.createInterface(className);
    //        PsiMethod method = elementFactory.createMethodFromText(getClassContent(),psiFile);
    //        sourceClass.add(method);
    //        psiFile.add(sourceClass);
    //
    //    });
    //}
    //
    //private String getClassContent() {
    //    StringBuilder strb = new StringBuilder();
    //    strb.append("@Deprecated\n")
    //        .append("public ").append("Long ").append("testOne").append("();");
    //    return strb.toString();
    //}
    //
    //@Override
    //public void update(AnActionEvent event) {
    //    super.update(event);
    //    System.out.println("update");
    //    Project project = event.getData(LangDataKeys.PROJECT);
    //    VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);
    //    addSPIClass(project, virtualFile);
    //}
    //
    //public static String getFilePackageName(VirtualFile dir) {
    //    if(!dir.isDirectory()) {
    //        // 非目录的取所在文件夹路径
    //        dir = dir.getParent();
    //    }
    //    String path = dir.getPath().replace("/", ".");
    //    String preText = "src.main.java";
    //    int preIndex = path.indexOf(preText) + preText.length() + 1;
    //    path = path.substring(preIndex);
    //    return path;
    //}
}
