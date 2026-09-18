import { apiRequest } from "@/lib/api";

import type {
  PracticeSessionDetail,
  PracticeSessionQuestion,
  PracticeSessionResponse,
  PracticeSessionResult,
  StartPracticeSessionRequest,
  SubmitPracticeAnswerRequest,
} from "@/types/practice";

export async function startPracticeSession(
  request: StartPracticeSessionRequest,
): Promise<PracticeSessionResponse> {
  return apiRequest<PracticeSessionResponse>(
    "/practice-sessions/start",
    {
      method: "POST",
      body: JSON.stringify(request),
    },
  );
}

export async function getPracticeSession(
  sessionId: string,
): Promise<PracticeSessionDetail> {
  return apiRequest<PracticeSessionDetail>(
    `/practice-sessions/${sessionId}`,
  );
}

export async function submitPracticeAnswer(
  sessionId: string,
  sessionQuestionId: string,
  request: SubmitPracticeAnswerRequest,
): Promise<PracticeSessionQuestion> {
  return apiRequest<PracticeSessionQuestion>(
    `/practice-sessions/${sessionId}/questions/${sessionQuestionId}/answer`,
    {
      method: "POST",
      body: JSON.stringify(request),
    },
  );
}

export async function completePracticeSession(
  sessionId: string,
): Promise<PracticeSessionResult> {
  return apiRequest<PracticeSessionResult>(
    `/practice-sessions/${sessionId}/complete`,
    {
      method: "POST",
    },
  );
}

export async function getPracticeSessionResult(
  sessionId: string,
): Promise<PracticeSessionResult> {
  return apiRequest<PracticeSessionResult>(
    `/practice-sessions/${sessionId}/result`,
  );
}