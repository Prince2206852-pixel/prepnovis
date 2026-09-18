import type {
  DifficultyLevel,
  QuestionType,
} from "@/types/question";

export type QuestionSource =
  | "SAVED"
  | "PREPNOVIS_MOCK";

export type PracticeSessionStatus =
  | "IN_PROGRESS"
  | "COMPLETED";

export interface StartPracticeSessionRequest {
  category: string;
  topic: string;
  difficultyLevel: DifficultyLevel;
  questionType: QuestionType;
  totalQuestions: number;
  questionSource: QuestionSource;
  questionId?: string;
}

export interface SubmitPracticeAnswerRequest {
  answer: string;
  
}

export interface PracticeSessionResponse {
  id: string;
  category: string;
  topic: string;
  difficultyLevel: DifficultyLevel;
  questionType: QuestionType;
  totalQuestions: number;
  assignedQuestions: number;
  status: PracticeSessionStatus;
  createdAt: string;
}

export interface PracticeSessionQuestion {
  id: string;
  questionId: string | null;
  questionText: string;
  category: string;
  topic: string;
  questionType: string;
  difficultyLevel: string;
  answered: boolean;
  userAnswer: string | null;
  score: number | null;
  feedback: string | null;
  strengths: string | null;
  improvements: string | null;
}

export interface PracticeSessionDetail {
  id: string;
  category: string;
  topic: string;
  difficultyLevel: DifficultyLevel;
  questionType: QuestionType;
  totalQuestions: number;
  status: PracticeSessionStatus;
  createdAt: string;
  questions: PracticeSessionQuestion[];
}

export interface PracticeSessionResult {
  sessionId: string;
  totalQuestions: number;
  assignedQuestions: number;
  answeredQuestions: number;
  unansweredQuestions: number;
  averageScore: number | null;
  status: PracticeSessionStatus;
  completedAt: string | null;
}