package com.hackathon.churninsight.service;

import com.hackathon.churninsight.domain.PredictionHistory;

public interface RecommendationService {
    String generateSuggestion(PredictionHistory history);
}
