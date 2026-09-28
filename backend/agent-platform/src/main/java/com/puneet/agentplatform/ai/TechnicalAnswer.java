package com.puneet.agentplatform.ai;

import java.util.List;

public record TechnicalAnswer(
        String answer,
        List<String> keyPoints,
        List<String> assumptions,
        boolean needsClarification
) {
}
