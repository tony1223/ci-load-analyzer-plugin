package org.tonyq.ciloadanalyzer;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
// 移除 PHP 特定的 import
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * CodeIgniter Load Analyzer - 主要 Action
 * 
 * 這個 Action 會：
 * 1. 分析當前 PHP 檔案中的 $this->load 調用
 * 2. 生成對應的 @property 註解
 * 3. 更新檔案內容
 */
public class GeneratePropertiesAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // 在背景執行緒中執行更新
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 取得當前的專案、編輯器和檔案
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        // 檢查必要條件
        if (project == null || editor == null || psiFile == null) {
            Messages.showErrorDialog("無法取得當前檔案資訊", "錯誤");
            return;
        }

        // 確認是 PHP 檔案 (通過副檔名檢查)
        String fileName = psiFile.getName();
        if (!fileName.toLowerCase().endsWith(".php")) {
            Messages.showInfoMessage("此功能僅支援 PHP 檔案", "提示");
            return;
        }

        // 取得檔案內容
        Document document = editor.getDocument();
        String content = document.getText();

        try {
            // 分析 $this->load 調用
            CodeIgniterLoadAnalyzer analyzer = new CodeIgniterLoadAnalyzer();
            List<LoadInfo> loads = analyzer.analyzeContent(content);

            if (loads.isEmpty()) {
                Messages.showInfoMessage("未找到任何 $this->load 調用", "結果");
                return;
            }

            // 生成 PHPDoc 屬性
            String properties = analyzer.generatePhpDocProperties(loads);

            // 更新檔案內容
            WriteCommandAction.runWriteCommandAction(project, () -> {
                String newContent = analyzer.addPhpDocToContent(content, properties);
                document.setText(newContent);
                PsiDocumentManager.getInstance(project).commitDocument(document);
            });

            // 顯示成功訊息
//            String message = String.format("成功生成 %d 個屬性註解：\n\n%s", 
//                loads.size(), 
//                loads.stream()
//                    .map(load -> String.format("• %s -> $%s", load.getPath(), load.getPropertyName()))
//                    .reduce((a, b) -> a + "\n" + b)
//                    .orElse("無")
//            );
//            
//            Messages.showInfoMessage(message, "CodeIgniter Properties 已生成");

        } catch (Exception ex) {
            Messages.showErrorDialog("處理檔案時發生錯誤：" + ex.getMessage(), "錯誤");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 檢查是否為 PHP 檔案（簡化檢測，只用副檔名）
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        boolean isPhpFile = false;

        if (psiFile != null) {
            String fileName = psiFile.getName();
            isPhpFile = fileName.toLowerCase().endsWith(".php");
        }

        // 設定 Action 的可見性和啟用狀態
        e.getPresentation().setEnabledAndVisible(isPhpFile);
    }
}