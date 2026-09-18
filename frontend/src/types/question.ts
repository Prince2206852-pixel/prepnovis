export type DifficultyLevel = "EASY" | "MEDIUM" | "HARD";

export type QuestionType =
  | "TECHNICAL"
  | "CODING"
  | "SYSTEM_DESIGN"
  | "BEHAVIORAL"
  | "MCQ"
  | "OTHER";

export type Question = {
  id: string;
  questionText: string;
  answer: string | null;
  category: string;
  topic: string;
  questionType: QuestionType;
  difficultyLevel: DifficultyLevel;
  tags: string | null;
  createdAt: string;
  updatedAt: string;
};

export type QuestionRequest = {
  questionText: string;
  answer?: string;
  category: string;
  topic: string;
  questionType: QuestionType;
  difficultyLevel: DifficultyLevel;
  tags?: string;
};

export type QuestionFilters = {
  page?: number;
  size?: number;
  category?: string;
  topic?: string;
  difficultyLevel?: DifficultyLevel;
  questionType?: QuestionType;
};

export type PageResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};