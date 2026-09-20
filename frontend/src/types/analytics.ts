export type AnalyticsSessionStatus = "IN_PROGRESS" | "COMPLETED";

export interface AnalyticsRecentSession {
  sessionId: string;
  totalQuestions: number;
  assignedQuestions: number;
  answeredQuestions: number;
  unansweredQuestions: number;
  averageScore: number;
  status: AnalyticsSessionStatus;
  completedAt: string | null;
}

export interface AnalyticsDashboard {
  totalSessions: number;
  completedSessions: number;

  totalQuestionsAnswered: number;
  savedQuestionsAnswered: number;
  prepNovisMockQuestionsAnswered: number;

  averageScore: number;
  highestScore: number;

  savedQuestionsAverageScore: number;
  prepNovisMockAverageScore: number;

  recentSessions: AnalyticsRecentSession[];
}