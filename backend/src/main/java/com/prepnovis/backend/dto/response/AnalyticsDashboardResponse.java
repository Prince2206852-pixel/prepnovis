package com.prepnovis.backend.dto.response;

import java.util.List;

public class AnalyticsDashboardResponse {

    private int totalSessions;
    private int completedSessions;
    private int totalQuestionsAnswered;
    private int savedQuestionsAnswered;
    private int prepNovisMockQuestionsAnswered;

    private Double savedQuestionsAverageScore;
    private Double prepNovisMockAverageScore;
    private Double averageScore;
    private Double highestScore;
    private List<PracticeSessionResultResponse> recentSessions;

    public AnalyticsDashboardResponse() {
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(int completedSessions) {
        this.completedSessions = completedSessions;
    }

    public int getTotalQuestionsAnswered() {
        return totalQuestionsAnswered;
    }

    public void setTotalQuestionsAnswered(int totalQuestionsAnswered) {
        this.totalQuestionsAnswered = totalQuestionsAnswered;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Double highestScore) {
        this.highestScore = highestScore;
    }

    public List<PracticeSessionResultResponse> getRecentSessions() {
        return recentSessions;
    }

    public void setRecentSessions(List<PracticeSessionResultResponse> recentSessions) {
        this.recentSessions = recentSessions;
    }

    public int getSavedQuestionsAnswered() {
    return savedQuestionsAnswered;
}

public void setSavedQuestionsAnswered(int savedQuestionsAnswered) {
    this.savedQuestionsAnswered = savedQuestionsAnswered;
}

public int getPrepNovisMockQuestionsAnswered() {
    return prepNovisMockQuestionsAnswered;
}

public void setPrepNovisMockQuestionsAnswered(int prepNovisMockQuestionsAnswered) {
    this.prepNovisMockQuestionsAnswered = prepNovisMockQuestionsAnswered;
}

public Double getSavedQuestionsAverageScore() {
    return savedQuestionsAverageScore;
}

public void setSavedQuestionsAverageScore(Double savedQuestionsAverageScore) {
    this.savedQuestionsAverageScore = savedQuestionsAverageScore;
}

public Double getPrepNovisMockAverageScore() {
    return prepNovisMockAverageScore;
}

public void setPrepNovisMockAverageScore(Double prepNovisMockAverageScore) {
    this.prepNovisMockAverageScore = prepNovisMockAverageScore;
}
}