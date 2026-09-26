import { apiRequest } from "@/lib/api";

import type {
  PageResponse,
  Question,
  QuestionFilters,
  QuestionRequest,
} from "@/types/question";

export async function getQuestions(
  filters: QuestionFilters = {},
): Promise<PageResponse<Question>> {
  const params = new URLSearchParams();

  params.set("page", String(filters.page ?? 0));
  params.set("size", String(filters.size ?? 10));

  if (filters.search?.trim()) {
    params.set("search", filters.search.trim());
  }

  if (filters.category?.trim()) {
    params.set("category", filters.category.trim());
  }

  if (filters.topic?.trim()) {
    params.set("topic", filters.topic.trim());
  }

  if (filters.difficultyLevel) {
    params.set("difficultyLevel", filters.difficultyLevel);
  }

  if (filters.questionType) {
    params.set("questionType", filters.questionType);
  }

  return apiRequest<PageResponse<Question>>(
    `/questions?${params.toString()}`,
  );
}

export async function getQuestionById(
  id: string,
): Promise<Question> {
  return apiRequest<Question>(`/questions/${id}`);
}

export async function createQuestion(
  request: QuestionRequest,
): Promise<Question> {
  return apiRequest<Question>("/questions", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

export async function updateQuestion(
  id: string,
  request: QuestionRequest,
): Promise<Question> {
  return apiRequest<Question>(`/questions/${id}`, {
    method: "PUT",
    body: JSON.stringify(request),
  });
}

export async function deleteQuestion(
  id: string,
): Promise<void> {
  await apiRequest<void>(`/questions/${id}`, {
    method: "DELETE",
  });
}