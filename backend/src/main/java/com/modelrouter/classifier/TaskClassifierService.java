package com.modelrouter.classifier;

import com.modelrouter.routing.InferenceRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class TaskClassifierService {

    private static final List<String> CODE_KEYWORDS = List.of(
            "function", "class", "def ", "public ", "private ", "import ", "const ", "let ", "var ",
            "return", "sql", "select ", "insert ", "update ", "delete ", "refactor", "debug", "bug",
            "algorithm", "api", "json", "html", "css", "git", "java", "python", "typescript", "react"
    );

    private static final List<String> REASONING_KEYWORDS = List.of(
            "calculate", "prove", "evaluate", "solve", "why", "explain step-by-step", "derivation",
            "logic", "math", "equation", "proof", "analyze", "hypothesis", "deduce", "compare and contrast"
    );

    private static final List<String> WRITING_KEYWORDS = List.of(
            "essay", "draft", "write a story", "summarize", "paraphrase", "rewrite", "blog post",
            "article", "poem", "script", "email", "cover letter", "translate", "proofread"
    );

    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```[a-zA-Z]*\\n[\\s\\S]*?\\n```");

    public TaskClassificationResult classify(InferenceRequest request) {
        if (request == null || request.getMessages() == null || request.getMessages().isEmpty()) {
            return TaskClassificationResult.builder()
                    .category(TaskClassificationResult.TaskCategory.CHAT)
                    .complexityScore(0.1)
                    .estimatedPromptTokens(0)
                    .recommendedCapability("chat")
                    .detectedKeywords(Collections.emptyList())
                    .build();
        }

        StringBuilder combinedTextBuilder = new StringBuilder();
        for (InferenceRequest.ChatMessage msg : request.getMessages()) {
            if (msg.getContent() != null) {
                combinedTextBuilder.append(msg.getContent()).append("\n");
            }
        }
        String combinedText = combinedTextBuilder.toString().toLowerCase();

        // 1. Calculate Token Count Heuristic (~4 chars / token)
        int totalChars = combinedText.length();
        int estimatedTokens = Math.max(1, totalChars / 4);

        // 2. Keyword Matching & Scoring
        List<String> matchedKeywords = new ArrayList<>();
        int codeScore = countMatches(combinedText, CODE_KEYWORDS, matchedKeywords);
        int reasoningScore = countMatches(combinedText, REASONING_KEYWORDS, matchedKeywords);
        int writingScore = countMatches(combinedText, WRITING_KEYWORDS, matchedKeywords);

        // Check explicit code block presence
        if (CODE_BLOCK_PATTERN.matcher(combinedTextBuilder.toString()).find()) {
            codeScore += 5;
            matchedKeywords.add("```code_block```");
        }

        // 3. Determine Dominant Category
        TaskClassificationResult.TaskCategory category = TaskClassificationResult.TaskCategory.CHAT;
        String recommendedCapability = "chat";

        if (codeScore >= reasoningScore && codeScore >= writingScore && codeScore > 0) {
            category = TaskClassificationResult.TaskCategory.CODE;
            recommendedCapability = "code";
        } else if (reasoningScore >= writingScore && reasoningScore > 0) {
            category = TaskClassificationResult.TaskCategory.REASONING;
            recommendedCapability = "reasoning";
        } else if (writingScore > 0) {
            category = TaskClassificationResult.TaskCategory.WRITING;
            recommendedCapability = "writing";
        }

        // 4. Calculate Complexity Score (0.0 to 1.0)
        double tokenFactor = Math.min(1.0, estimatedTokens / 4000.0) * 0.4;
        double matchFactor = Math.min(1.0, (codeScore + reasoningScore + writingScore) / 10.0) * 0.4;
        double structureFactor = (combinedText.contains("?") || combinedText.contains("!")) ? 0.2 : 0.1;
        
        double complexityScore = Math.min(1.0, Math.max(0.1, tokenFactor + matchFactor + structureFactor));
        complexityScore = Math.round(complexityScore * 100.0) / 100.0;

        return TaskClassificationResult.builder()
                .category(category)
                .complexityScore(complexityScore)
                .estimatedPromptTokens(estimatedTokens)
                .recommendedCapability(recommendedCapability)
                .detectedKeywords(matchedKeywords)
                .build();
    }

    private int countMatches(String text, List<String> keywords, List<String> matchedList) {
        int count = 0;
        for (String kw : keywords) {
            if (text.contains(kw)) {
                count++;
                if (!matchedList.contains(kw)) {
                    matchedList.add(kw);
                }
            }
        }
        return count;
    }
}
