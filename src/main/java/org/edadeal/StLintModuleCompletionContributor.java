package org.edadeal;

// https://github.com/rainerosion/RainGitEmoji/blob/06adb7cfe0b72fd382acf0bc55f4b70bc860855f/src/cn/rainss/emoji/RainsGitEmojiMain.java
// https://github.com/ant-druha/AppleScript-IDEA/blob/85c5197c07c005c590e249318fea9e69b2755131/src/main/java/com/intellij/plugin/applescript/lang/ide/completion/CommandCompletionContributor.java
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class StLintModuleCompletionContributor extends CompletionContributor {
    private static final Logger log = Logger.getInstance(StLintModuleCompletionContributor.class);

    public StLintModuleCompletionContributor() {
        extend(CompletionType.BASIC,
                psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(
                            @NotNull CompletionParameters parameters,
                             @NotNull ProcessingContext context,
                            @NotNull CompletionResultSet resultSet
                    ) {
                        if (!TypeCheck.isStylusFile(Objects.requireNonNull(parameters.getOriginalFile().getVirtualFile().getCanonicalPath()))) {
                            return;
                        }

                        int offset = parameters.getEditor().getCaretModel().getOffset();

                        Document document = parameters.getEditor().getDocument();

                        final int lineStartOffset = document.getLineStartOffset(document.getLineNumber(offset));

                        String text = document.getText(TextRange.create(lineStartOffset, offset));

                        log.info("Autocomplete: " + offset + "--" + lineStartOffset + "---" + text);

                        Collection<Suggest> suggests = TypeCheck.autoCompletes(
                                parameters.getOriginalFile(),
                                offset,
                                lineStartOffset,
                                text
                        );

                        for (final Suggest suggest: suggests) {
                            resultSet.addElement(LookupElementBuilder.create(suggest.title));
                        }
                    }
                }
        );
    }
}