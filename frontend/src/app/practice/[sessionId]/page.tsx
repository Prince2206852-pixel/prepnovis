"use client";

import { use, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import {
  ArrowLeft,
  BrainCircuit,
  CheckCircle2,
  Loader2,
  Send,
  Trophy,
} from "lucide-react";

import AuthGuard from "@/components/AuthGuard";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";

import {
  completePracticeSession,
  getPracticeSession,
  submitPracticeAnswer,
} from "@/services/practiceService";

import type {
  PracticeSessionDetail,
  PracticeSessionQuestion,
  PracticeSessionResult,
} from "@/types/practice";

interface PracticeSessionPageProps {
  params: Promise<{
    sessionId: string;
  }>;
}

export default function PracticeSessionPage({
  params,
}: PracticeSessionPageProps) {
  const { sessionId } = use(params);
  const router = useRouter();

  const [session, setSession] =
    useState<PracticeSessionDetail | null>(null);

  const [answers, setAnswers] = useState<
    Record<string, string>
  >({});

  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState("");

  const [submittingQuestionId, setSubmittingQuestionId] =
    useState<string | null>(null);

  const [answerErrors, setAnswerErrors] = useState<
    Record<string, string>
  >({});

  const [completing, setCompleting] = useState(false);
  const [result, setResult] =
    useState<PracticeSessionResult | null>(null);

  async function loadSession() {
    setLoading(true);
    setPageError("");

    try {
      const response =
        await getPracticeSession(sessionId);

      setSession(response);

      const initialAnswers: Record<string, string> = {};

      response.questions.forEach((question) => {
        initialAnswers[question.id] =
          question.userAnswer ?? "";
      });

      setAnswers(initialAnswers);
    } catch (error) {
      setPageError(
        error instanceof Error
          ? error.message
          : "Unable to load this practice session.",
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadSession();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sessionId]);

  function updateQuestionInSession(
    updatedQuestion: PracticeSessionQuestion,
  ) {
    setSession((current) => {
      if (!current) {
        return current;
      }

      return {
        ...current,
        questions: current.questions.map((question) =>
          question.id === updatedQuestion.id
            ? updatedQuestion
            : question,
        ),
      };
    });
  }

  async function handleSubmitAnswer(
    question: PracticeSessionQuestion,
  ) {
    const answer = answers[question.id]?.trim();

    setAnswerErrors((current) => ({
      ...current,
      [question.id]: "",
    }));

    if (!answer) {
      setAnswerErrors((current) => ({
        ...current,
        [question.id]: "Please enter your answer.",
      }));

      return;
    }

    setSubmittingQuestionId(question.id);

    try {
      const response = await submitPracticeAnswer(
        sessionId,
        question.id,
        {
          answer,
        },
      );

      updateQuestionInSession(response);

      setAnswers((current) => ({
        ...current,
        [question.id]: response.userAnswer ?? answer,
      }));
    } catch (error) {
      setAnswerErrors((current) => ({
        ...current,
        [question.id]:
          error instanceof Error
            ? error.message
            : "Unable to evaluate your answer.",
      }));
    } finally {
      setSubmittingQuestionId(null);
    }
  }

  async function handleCompleteSession() {
    if (!session) {
      return;
    }

    setCompleting(true);
    setPageError("");

    try {
      const response =
        await completePracticeSession(sessionId);

      setResult(response);

      setSession((current) =>
        current
          ? {
              ...current,
              status: "COMPLETED",
            }
          : current,
      );
    } catch (error) {
      setPageError(
        error instanceof Error
          ? error.message
          : "Unable to complete this session.",
      );
    } finally {
      setCompleting(false);
    }
  }

  const answeredQuestions =
    session?.questions.filter(
      (question) => question.answered,
    ).length ?? 0;

  const allAnswered =
    session !== null &&
    session.questions.length > 0 &&
    answeredQuestions === session.questions.length;

  return (
    <AuthGuard>
      <div className="min-h-screen overflow-x-hidden bg-slate-50">
        <Sidebar />

        <div className="min-h-screen w-full lg:ml-64 lg:w-[calc(100%-16rem)]">
          <Header />

          <main className="w-full px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
            {/* BACK */}
            <button
              type="button"
              onClick={() => router.push("/questions")}
              className="mb-6 inline-flex items-center gap-2 text-sm font-semibold text-slate-500 transition hover:text-slate-900"
            >
              <ArrowLeft size={17} />
              Saved Questions
            </button>

            {loading ? (
              <div className="flex min-h-[400px] flex-col items-center justify-center">
                <Loader2
                  size={30}
                  className="animate-spin text-indigo-600"
                />

                <p className="mt-4 text-sm text-slate-500">
                  Loading practice session...
                </p>
              </div>
            ) : pageError && !session ? (
              <div className="rounded-2xl border border-red-100 bg-red-50 p-6">
                <p className="font-semibold text-red-700">
                  Unable to open practice session
                </p>

                <p className="mt-2 text-sm text-red-600">
                  {pageError}
                </p>

                <button
                  type="button"
                  onClick={loadSession}
                  className="mt-5 rounded-xl bg-red-600 px-4 py-2.5 text-sm font-semibold text-white"
                >
                  Try again
                </button>
              </div>
            ) : session ? (
              <>
                {/* SESSION HEADER */}
                <section className="mb-6">
                  <p className="mb-2 text-sm font-medium text-indigo-600">
                    Practice Session
                  </p>

                  <h1 className="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
                    {session.topic}
                  </h1>

                  <div className="mt-4 flex flex-wrap gap-2">
                    <span className="rounded-full bg-indigo-50 px-3 py-1.5 text-xs font-semibold text-indigo-600">
                      {session.category}
                    </span>

                    <span className="rounded-full bg-amber-50 px-3 py-1.5 text-xs font-semibold text-amber-700">
                      {session.difficultyLevel}
                    </span>

                    <span className="rounded-full bg-emerald-50 px-3 py-1.5 text-xs font-semibold text-emerald-700">
                      {session.questionType.replaceAll(
                        "_",
                        " ",
                      )}
                    </span>
                  </div>
                </section>

                {/* PROGRESS */}
                <section className="mb-6 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-5">
                  <div className="flex items-center justify-between gap-4">
                    <div>
                      <p className="text-sm font-semibold text-slate-900">
                        Session progress
                      </p>

                      <p className="mt-1 text-xs text-slate-500">
                        {answeredQuestions} of{" "}
                        {session.questions.length} answered
                      </p>
                    </div>

                    <span className="text-sm font-bold text-indigo-600">
                      {session.questions.length > 0
                        ? Math.round(
                            (answeredQuestions /
                              session.questions.length) *
                              100,
                          )
                        : 0}
                      %
                    </span>
                  </div>

                  <div className="mt-4 h-2 overflow-hidden rounded-full bg-slate-100">
                    <div
                      className="h-full rounded-full bg-indigo-600 transition-all duration-300"
                      style={{
                        width: `${
                          session.questions.length > 0
                            ? (answeredQuestions /
                                session.questions.length) *
                              100
                            : 0
                        }%`,
                      }}
                    />
                  </div>
                </section>

                {pageError && (
                  <div className="mb-6 rounded-xl border border-red-100 bg-red-50 px-4 py-3 text-sm text-red-600">
                    {pageError}
                  </div>
                )}

                {/* QUESTIONS */}
                <section className="space-y-5">
                  {session.questions.map(
                    (question, index) => (
                      <article
                        key={question.id}
                        className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm"
                      >
                        <div className="border-b border-slate-100 p-4 sm:p-6">
                          <div className="flex items-start justify-between gap-4">
                            <div className="min-w-0">
                              <p className="text-xs font-semibold uppercase tracking-wide text-indigo-600">
                                Question {index + 1}
                              </p>

                              <h2 className="mt-2 break-words text-lg font-bold leading-7 text-slate-900">
                                {question.questionText}
                              </h2>
                            </div>

                            {question.answered && (
                              <CheckCircle2
                                size={22}
                                className="shrink-0 text-emerald-500"
                              />
                            )}
                          </div>
                        </div>

                        <div className="p-4 sm:p-6">
                          <label className="mb-2 block text-sm font-semibold text-slate-700">
                            Your Answer
                          </label>

                          <textarea
                            value={
                              answers[question.id] ?? ""
                            }
                            disabled={
                              question.answered ||
                              session.status ===
                                "COMPLETED"
                            }
                            onChange={(event) =>
                              setAnswers((current) => ({
                                ...current,
                                [question.id]:
                                  event.target.value,
                              }))
                            }
                            maxLength={10000}
                            rows={7}
                            placeholder="Write your interview answer here..."
                            className="w-full resize-y rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm leading-6 text-slate-800 outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-100"
                          />

                          {answerErrors[question.id] && (
                            <p className="mt-2 text-sm text-red-600">
                              {
                                answerErrors[
                                  question.id
                                ]
                              }
                            </p>
                          )}

                          {!question.answered &&
                            session.status !==
                              "COMPLETED" && (
                              <button
                                type="button"
                                disabled={
                                  submittingQuestionId !==
                                  null
                                }
                                onClick={() =>
                                  handleSubmitAnswer(
                                    question,
                                  )
                                }
                                className="mt-4 inline-flex w-full items-center justify-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60 sm:w-auto"
                              >
                                {submittingQuestionId ===
                                question.id ? (
                                  <>
                                    <Loader2
                                      size={17}
                                      className="animate-spin"
                                    />
                                    Novis is evaluating...
                                  </>
                                ) : (
                                  <>
                                    <Send size={17} />
                                    Submit to Novis
                                  </>
                                )}
                              </button>
                            )}

                          {/* NOVIS EVALUATION */}
                          {question.answered && (
                            <div className="mt-6 rounded-2xl border border-indigo-100 bg-indigo-50/50 p-4 sm:p-5">
                              <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                                <div className="flex items-center gap-3">
                                  <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-indigo-600 text-white">
                                    <BrainCircuit
                                      size={20}
                                    />
                                  </div>

                                  <div>
                                    <p className="text-sm font-bold text-slate-900">
                                      Novis Evaluation
                                    </p>

                                    <p className="text-xs text-slate-500">
                                      AI feedback on your
                                      answer
                                    </p>
                                  </div>
                                </div>

                                {question.score !== null && (
                                  <div className="flex items-baseline gap-1">
                                    <span className="text-2xl font-bold text-indigo-600">
                                      {question.score}
                                    </span>

                                    <span className="text-sm text-slate-500">
                                      / 10
                                    </span>
                                  </div>
                                )}
                              </div>

                              {question.feedback && (
                                <div className="mt-5">
                                  <p className="text-xs font-bold uppercase tracking-wide text-slate-500">
                                    Feedback
                                  </p>

                                  <p className="mt-2 whitespace-pre-wrap text-sm leading-6 text-slate-700">
                                    {question.feedback}
                                  </p>
                                </div>
                              )}

                              {question.strengths && (
                                <div className="mt-5 rounded-xl bg-emerald-50 p-4">
                                  <p className="text-sm font-bold text-emerald-700">
                                    Strengths
                                  </p>

                                  <p className="mt-2 whitespace-pre-wrap text-sm leading-6 text-emerald-800">
                                    {question.strengths}
                                  </p>
                                </div>
                              )}

                              {question.improvements && (
                                <div className="mt-3 rounded-xl bg-amber-50 p-4">
                                  <p className="text-sm font-bold text-amber-700">
                                    Improvements
                                  </p>

                                  <p className="mt-2 whitespace-pre-wrap text-sm leading-6 text-amber-800">
                                    {
                                      question.improvements
                                    }
                                  </p>
                                </div>
                              )}
                            </div>
                          )}
                        </div>
                      </article>
                    ),
                  )}
                </section>

                {/* COMPLETE */}
                {session.status !== "COMPLETED" && (
                  <section className="mt-6 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6">
                    <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                      <div>
                        <h2 className="font-semibold text-slate-900">
                          Finish Practice
                        </h2>

                        <p className="mt-1 text-sm text-slate-500">
                          {allAnswered
                            ? "All questions are answered. Complete the session to see your final result."
                            : "Answer all questions before completing the session."}
                        </p>
                      </div>

                      <button
                        type="button"
                        disabled={
                          !allAnswered || completing
                        }
                        onClick={handleCompleteSession}
                        className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-slate-950 px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-40 sm:w-auto"
                      >
                        {completing ? (
                          <>
                            <Loader2
                              size={17}
                              className="animate-spin"
                            />
                            Completing...
                          </>
                        ) : (
                          <>
                            <Trophy size={17} />
                            Complete Session
                          </>
                        )}
                      </button>
                    </div>
                  </section>
                )}

                {/* RESULT */}
                {result && (
                  <section className="mt-6 rounded-2xl bg-slate-950 p-5 text-white shadow-sm sm:p-6">
                    <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-500">
                      <Trophy size={23} />
                    </div>

                    <p className="mt-5 text-sm font-medium text-indigo-300">
                      Practice complete
                    </p>

                    <h2 className="mt-1 text-2xl font-bold">
                      Session Result
                    </h2>

                    <div className="mt-6 grid grid-cols-2 gap-3 sm:grid-cols-4">
                      <div className="rounded-xl bg-white/10 p-4">
                        <p className="text-xs text-slate-400">
                          Questions
                        </p>

                        <p className="mt-1 text-xl font-bold">
                          {result.assignedQuestions}
                        </p>
                      </div>

                      <div className="rounded-xl bg-white/10 p-4">
                        <p className="text-xs text-slate-400">
                          Answered
                        </p>

                        <p className="mt-1 text-xl font-bold">
                          {result.answeredQuestions}
                        </p>
                      </div>

                      <div className="rounded-xl bg-white/10 p-4">
                        <p className="text-xs text-slate-400">
                          Unanswered
                        </p>

                        <p className="mt-1 text-xl font-bold">
                          {result.unansweredQuestions}
                        </p>
                      </div>

                      <div className="rounded-xl bg-indigo-500 p-4">
                        <p className="text-xs text-indigo-100">
                          Average Score
                        </p>

                        <p className="mt-1 text-xl font-bold">
                          {result.averageScore !== null
                            ? `${result.averageScore}/10`
                            : "—"}
                        </p>
                      </div>
                    </div>

                    <button
                      type="button"
                      onClick={() =>
                        router.push("/questions")
                      }
                      className="mt-6 w-full rounded-xl bg-white px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-slate-100 sm:w-auto"
                    >
                      Back to Saved Questions
                    </button>
                  </section>
                )}
              </>
            ) : null}
          </main>
        </div>
      </div>
    </AuthGuard>
  );
}