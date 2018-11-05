package com.chenyi;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

/**
 * @author: chenyi.zsq
 * @Date: 2018/11/2
 */
public class HelloAction extends AnAction{

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Messages.showMessageDialog("HelloPlugin action","PluginTitle",null);
    }
}
