package com.chenyi;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.impl.FileManager;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.apache.xmlbeans.impl.xb.ltgfmt.Code;
import org.junit.Assert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: chenyi.zsq
 * @Date: 2018/11/2
 */
public class GenarateAction extends AnAction implements ActionListener {

    JPanel contentPane;
    JButton buttonOK;
    JEditorPane serviceEdit;
    JLabel serviceLabel;
    JDialog dialog;
    AnActionEvent event;
    JCheckBox needModuleBox;

    /**
     * 对话框事件
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String serviceName = serviceEdit.getText();
        boolean isNeedModule = needModuleBox.isSelected();
        System.out.println("service:"+serviceName+" need:"+isNeedModule);
        try {
            PsiClass psiClass = createSPIClass(event,serviceName,isNeedModule);
            createSPIImplClass(event, psiClass,isNeedModule);
            dialog.setVisible(false);
        } catch (Exception e1) {
            System.out.println(e1);
        }
    }

    /**
     * 标题栏事件
     * @param event
     */
    @Override
    public void actionPerformed(AnActionEvent event) {
        System.out.println("started");
        this.event = event;
        showDialog();
    }

    public PsiClass createSPIClass(AnActionEvent event,String serviceName,boolean isNeedModule) {
        System.out.println("addSPIClass");
        Project project = event.getData(LangDataKeys.PROJECT);
        VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);
        if (!serviceName.matches(".*\\.java")) {
            serviceName = serviceName + ".java";
        }
        PsiFile serviceFile = CodeUtil.getServiceInterface(project,serviceName);
        Assert.assertNotNull("searchFile_is_null:" + serviceName, serviceFile);
        VirtualFile packageFile = virtualFile.getParent();
        String name = serviceName.substring(0, serviceName.length() - 5) + "SPI.java";
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        String interfaceName = name.substring(0, name.length() - 5);
        PsiClass sourceClass = elementFactory.createInterface(interfaceName);
        WriteCommandAction.runWriteCommandAction(project,()->{
            VirtualFile classFile = packageFile.findChild(name);
            if (classFile == null) {
                PsiFile initFile = PsiFileFactory.getInstance(project).createFileFromText(
                    name, JavaFileType.INSTANCE, "");
                // 加到package下
                PsiManager.getInstance(project).findDirectory(packageFile).add(initFile);
                classFile = packageFile.findChild(name);
            } else {
                return;
            }
            PsiFile psiFile = PsiManager.getInstance(project).findFile(classFile);
            CodeUtil.getSPIMethods(serviceFile,project,psiFile,sourceClass,isNeedModule);
            psiFile.add(sourceClass);
        });
        return sourceClass;
    }


    private void createSPIImplClass(AnActionEvent event, PsiClass psiClass,boolean isNeedModule) {
        System.out.println("addSPIImplClass");
        Project project = event.getData(LangDataKeys.PROJECT);
        VirtualFile virtualFile = event.getData(LangDataKeys.VIRTUAL_FILE);

        String serviceName = "AgentOrderService.java";
        PsiFile serviceFile = CodeUtil.getServiceInterface(project, serviceName);
        Assert.assertNotNull("searchFile_is_null:" + serviceName, serviceFile);
        VirtualFile packageFile = virtualFile.getParent();
        String name = serviceName.substring(0, serviceName.length() - 5) + "SPIImpl.java";

        WriteCommandAction.runWriteCommandAction(project, () -> {
            VirtualFile implPackageFile = packageFile.findChild("impl");
            if (implPackageFile == null) {
                try {
                    implPackageFile = packageFile.createChildDirectory(null, "impl");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            VirtualFile classFile = implPackageFile.findChild(name);
            if (classFile == null) {
                PsiFile initFile = PsiFileFactory.getInstance(project).createFileFromText(
                    name, JavaFileType.INSTANCE, "");
                // 加到package下
                PsiManager.getInstance(project).findDirectory(implPackageFile).add(initFile);
                classFile = implPackageFile.findChild(name);
            } else {
                return;
            }
            PsiFile psiFile = PsiManager.getInstance(project).findFile(classFile);
            psiFile.add(CodeUtil.getSPIImplMethods(serviceFile, project, psiFile, psiClass,isNeedModule));
        });
    }

    public void showDialog(){

        dialog = new JDialog();
        dialog.setSize(600, 100);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        contentPane = new JPanel();
        serviceLabel = new JLabel("生成SPI的Service类名:");
        serviceEdit = new JEditorPane();
        serviceEdit.setSize(300,100);
        needModuleBox = new JCheckBox("返回Module");
        needModuleBox.addActionListener(e -> {
            String command = e.getActionCommand();
        });
        buttonOK = new JButton("OK");
        buttonOK.addActionListener(this);

        contentPane.add(serviceLabel,BorderLayout.CENTER);
        contentPane.add(serviceEdit,BorderLayout.CENTER);
        contentPane.add(needModuleBox);
        contentPane.add(buttonOK,BorderLayout.CENTER);

        dialog.add(contentPane,BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        GenarateAction genarateAction = new GenarateAction();
        genarateAction.showDialog();
    }
}

