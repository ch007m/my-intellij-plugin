package dev.snowdrop.intellijplugin;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class AddLicenseAction extends AnAction {

    // The full Apache 2.0 license header text.
    private static final String APACHE_LICENSE_TEXT =
        "/*\n" +
            " * Copyright (c) 2025 Your Name or Company\n" +
            " *\n" +
            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            " * you may not use this file except in compliance with the License.\n" +
            " * You may obtain a copy of the License at\n" +
            " *\n" +
            " * [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)\n" +
            " *\n" +
            " * Unless required by applicable law or agreed to in writing, software\n" +
            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            " * See the License for the specific language governing permissions and\n" +
            " * limitations under the License.\n" +
            " */\n\n";

    /**
     * This method is called by IntelliJ to determine if the action should be
     * visible and enabled. We'll enable it only if there's an open project
     * and the current file is a Java file.
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        boolean isVisible = project != null &&
            psiFile != null &&
            "JAVA".equalsIgnoreCase(psiFile.getLanguage().getID());

        e.getPresentation().setEnabledAndVisible(isVisible);
    }

    /**
     * This method is called when the user clicks the menu item.
     * It contains the core logic to add the license.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Get the current project and editor to access the document
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null) {
            return;
        }

        Document document = editor.getDocument();

        // Check if the license already exists to avoid duplicates
        if (document.getText().contains("Licensed under the Apache License")) {
            // Optionally, show a notification to the user
            System.out.println("License header already exists.");
            return;
        }

        // All modifications to documents must be wrapped in a WriteCommandAction
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // Insert the license text at the very beginning of the file (offset 0)
            document.insertString(0, APACHE_LICENSE_TEXT);
        });
    }

    /**
     * Specifies that the update method of this action should be called on a
     * background thread (BGT) instead of the Event Dispatch Thread (EDT).
     * This is required to avoid UI freezes when accessing potentially slow
     * data like PSI files.
     *
     * @return ActionUpdateThread.BGT to run on a background thread.
     */
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}