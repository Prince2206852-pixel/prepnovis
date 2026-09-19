"use client";

import { use, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import {
  ArrowLeft,
  ArrowRight,
  BrainCircuit,
  Check,
  CheckCircle2,
  ChevronLeft,
  CircleAlert,
  Lightbulb,
  Loader2,
  RefreshCw,
  Send,
  Sparkles,
  Target,
  Trophy,
} from "lucide-react";

import AuthGuard from "@/components/AuthGuard";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";

import {
  completePracticeSession,
  getPracticeSession,
  getPracticeSessionResult,
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

function splitEvaluationPoints(
  value: string | null | undefined,
): string[] {
  if (!value) {
    return [];
  }

  return value
    .replace(/\r\n/g, "\n")
    .split(/\s*\|\s*|\n+/)
    .map((item) =>
      item
        .trim()
        .replace(/^[-•*]\s*/, "")
        .replace(/^\d+[.)]\s*/, "")
        .replace(/^\*\*(.*?)\*\*$/, "$1")
        .trim(),
    )
    .filter((item) => item.length > 0);
}

function getScoreLabel(score: number | null) {
  if (score === null) {
    return "";
  }

  if (score >= 9) {
    return "Excellent";
  }

  if (score >= 8) {
    return "Strong";
  }

  if (score >= 7) {
    return "Good";
  }

  if (score >= 5) {
    return "Developing";
  }

  return "Needs improvement";
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

  const [currentQuestionIndex, setCurrentQuestionIndex] =
    useState(0);

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

      if (response.status === "COMPLETED") {
        const sessionResult =
          await getPracticeSessionResult(sessionId);

        setResult(sessionResult);
        setCurrentQuestionIndex(0);
      } else {
        setResult(null);

        const firstUnansweredIndex =
          response.questions.findIndex(
            (question) => !question.answered,
          );

        setCurrentQuestionIndex(
          firstUnansweredIndex >= 0
            ? firstUnansweredIndex
            : 0,
        );
      }
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
    if (submittingQuestionId !== null) {
      return;
    }

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
        [question.id]:
          response.userAnswer ?? answer,
      }));
    } catch (error) {
      setAnswerErrors((current) => ({
        ...current,
        [question.id]:
          error instanceof Error
            ? error.message
            : "Novis could not evaluate your answer. Please try again.",
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

      window.scrollTo({
        top: document.body.scrollHeight,
        behavior: "smooth",
      });
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

  function handlePreviousQuestion() {
    if (currentQuestionIndex <= 0) {
      return;
    }

    setCurrentQuestionIndex(
      (current) => current - 1,
    );

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }

  function handleNextQuestion() {
    if (
      !session ||
      currentQuestionIndex >=
        session.questions.length - 1
    ) {
      return;
    }

    setCurrentQuestionIndex(
      (current) => current + 1,
    );

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }

  const answeredQuestions =
    session?.questions.filter(
      (question) => question.answered,
    ).length ?? 0;

  const allAnswered =
    session !== null &&
    session.questions.length > 0 &&
    answeredQuestions === session.questions.length;

  const currentQuestion =
    session?.questions[currentQuestionIndex] ?? null;

  const isFirstQuestion =
    currentQuestionIndex === 0;

  const isLastQuestion =
    session !== null &&
    currentQuestionIndex ===
      session.questions.length - 1;

  const isMockSession =
    session?.questionSource === "PREPNOVIS_MOCK";

  const isEvaluatingCurrentQuestion =
    currentQuestion !== null &&
    submittingQuestionId === currentQuestion.id;

  const currentStrengths = splitEvaluationPoints(
    currentQuestion?.strengths,
  );

  const currentImprovements = splitEvaluationPoints(
    currentQuestion?.improvements,
  );

  const scoreLabel = getScoreLabel(
    currentQuestion?.score ?? null,
  );

  function handleBack() {
    router.push(isMockSession ? "/mock" : "/questions");
  }

  return (
    <AuthGuard>
      <div className="min-h-screen overflow-x-hidden bg-slate-50">
        <Sidebar />

        <div className="min-h-screen w-full lg:ml-64 lg:w-[calc(100%-16rem)]">
          <Header />

          <main className="w-full px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
            <button
              type="button"
              onClick={handleBack}
              className="mb-6 inline-flex items-center gap-2 text-sm font-semibold text-slate-500 transition hover:text-slate-900"
            >
              <ArrowLeft size={17} />

              {isMockSession
                ? "PrepNovis Mock"
                : "Saved Questions"}
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
            ) : session && currentQuestion ? (
              <>
                {/* SESSION HEADER */}
                <section className="mb-6">
                  <div className="flex flex-wrap items-center gap-3">
                    <p className="text-sm font-medium text-indigo-600">
                      {isMockSession
                        ? "PrepNovis Mock Interview"
                        : "Practice Session"}
                    </p>

                    {isMockSession && (
                      <span className="inline-flex items-center gap-1 rounded-full bg-violet-50 px-2.5 py-1 text-xs font-semibold text-violet-700">
                        <Sparkles size={13} />
                        Novis AI
                      </span>
                    )}

                    {session.status === "COMPLETED" && (
                      <span className="inline-flex items-center gap-1 rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-semibold text-emerald-700">
                        <CheckCircle2 size={13} />
                        Completed
                      </span>
                    )}
                  </div>

                  <h1 className="mt-2 text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
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

                  {session.questions.length > 1 && (
                    <div className="mt-4 flex flex-wrap gap-2">
                      {session.questions.map(
                        (question, index) => (
                          <button
                            key={question.id}
                            type="button"
                            disabled={
                              submittingQuestionId !== null
                            }
                            onClick={() =>
                              setCurrentQuestionIndex(
                                index,
                              )
                            }
                            className={`flex h-8 min-w-8 items-center justify-center rounded-lg px-2 text-xs font-bold transition ${
                              index ===
                              currentQuestionIndex
                                ? "bg-indigo-600 text-white"
                                : question.answered
                                  ? "bg-emerald-100 text-emerald-700"
                                  : "bg-slate-100 text-slate-500 hover:bg-slate-200"
                            } disabled:cursor-not-allowed disabled:opacity-60`}
                          >
                            {index + 1}
                          </button>
                        ),
                      )}
                    </div>
                  )}
                </section>

                {pageError && (
                  <div className="mb-6 rounded-xl border border-red-100 bg-red-50 px-4 py-3 text-sm text-red-600">
                    {pageError}
                  </div>
                )}

                {/* CURRENT QUESTION */}
                <article className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
                  <div className="border-b border-slate-100 p-4 sm:p-6">
                    <div className="flex items-start justify-between gap-4">
                      <div className="min-w-0">
                        <p className="text-xs font-semibold uppercase tracking-wide text-indigo-600">
                          Question{" "}
                          {currentQuestionIndex + 1} of{" "}
                          {session.questions.length}
                        </p>

                        <h2 className="mt-2 break-words text-lg font-bold leading-7 text-slate-900">
                          {currentQuestion.questionText}
                        </h2>
                      </div>

                      {currentQuestion.answered && (
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
                        answers[currentQuestion.id] ?? ""
                      }
                      disabled={
                        currentQuestion.answered ||
                        session.status === "COMPLETED" ||
                        isEvaluatingCurrentQuestion
                      }
                      onChange={(event) =>
                        setAnswers((current) => ({
                          ...current,
                          [currentQuestion.id]:
                            event.target.value,
                        }))
                      }
                      maxLength={10000}
                      rows={7}
                      placeholder="Write your interview answer here..."
                      className="w-full resize-y rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm leading-6 text-slate-800 outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:bg-slate-100"
                    />

                    {/* EVALUATION ERROR */}
                    {answerErrors[
                      currentQuestion.id
                    ] && (
                      <div className="mt-4 rounded-xl border border-red-100 bg-red-50 p-4">
                        <div className="flex items-start gap-3">
                          <CircleAlert
                            size={19}
                            className="mt-0.5 shrink-0 text-red-600"
                          />

                          <div>
                            <p className="text-sm font-bold text-red-700">
                              Novis could not evaluate this answer
                            </p>

                            <p className="mt-1 break-words text-sm leading-6 text-red-600">
                              {
                                answerErrors[
                                  currentQuestion.id
                                ]
                              }
                            </p>

                            <p className="mt-2 text-xs text-red-500">
                              Your answer is still here. You
                              can try the evaluation again.
                            </p>
                          </div>
                        </div>

                        {!currentQuestion.answered &&
                          session.status !==
                            "COMPLETED" && (
                            <button
                              type="button"
                              disabled={
                                submittingQuestionId !== null
                              }
                              onClick={() =>
                                handleSubmitAnswer(
                                  currentQuestion,
                                )
                              }
                              className="mt-4 inline-flex items-center justify-center gap-2 rounded-lg bg-red-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-60"
                            >
                              <RefreshCw size={16} />
                              Retry Evaluation
                            </button>
                          )}
                      </div>
                    )}

                    {/* SUBMIT */}
                    {!currentQuestion.answered &&
                      session.status !== "COMPLETED" &&
                      !isEvaluatingCurrentQuestion &&
                      !answerErrors[
                        currentQuestion.id
                      ] && (
                        <button
                          type="button"
                          disabled={
                            submittingQuestionId !== null
                          }
                          onClick={() =>
                            handleSubmitAnswer(
                              currentQuestion,
                            )
                          }
                          className="mt-4 inline-flex w-full items-center justify-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60 sm:w-auto"
                        >
                          <Send size={17} />
                          Submit to Novis
                        </button>
                      )}

                    {/* NOVIS ANALYZING */}
                    {isEvaluatingCurrentQuestion && (
                      <div className="mt-6 overflow-hidden rounded-2xl border border-indigo-100 bg-gradient-to-br from-indigo-50 to-violet-50">
                        <div className="p-5 sm:p-6">
                          <div className="flex items-center gap-4">
                            <div className="relative flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl bg-indigo-600 text-white">
                              <BrainCircuit size={23} />

                              <span className="absolute -right-1 -top-1 flex h-4 w-4">
                                <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-indigo-400 opacity-75" />
                                <span className="relative inline-flex h-4 w-4 rounded-full bg-indigo-500" />
                              </span>
                            </div>

                            <div>
                              <p className="font-bold text-slate-900">
                                Novis is evaluating your answer
                              </p>

                              <p className="mt-1 text-sm text-slate-500">
                                Analyzing your response,
                                strengths and areas to improve...
                              </p>
                            </div>
                          </div>

                          <div className="mt-5 flex items-center gap-2 text-xs font-semibold text-indigo-600">
                            <Loader2
                              size={15}
                              className="animate-spin"
                            />
                            Preparing your interview feedback
                          </div>
                        </div>
                      </div>
                    )}

                    {/* NOVIS EVALUATION */}
                    {currentQuestion.answered && (
                      <div className="mt-6 overflow-hidden rounded-2xl border border-indigo-100 bg-white shadow-sm">
                        <div className="border-b border-indigo-100 bg-gradient-to-r from-indigo-50 to-violet-50 p-4 sm:p-5">
                          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                            <div className="flex items-center gap-3">
                              <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-indigo-600 text-white">
                                <BrainCircuit size={21} />
                              </div>

                              <div>
                                <div className="flex flex-wrap items-center gap-2">
                                  <p className="text-sm font-bold text-slate-900">
                                    Novis Evaluation
                                  </p>

                                  <span className="inline-flex items-center gap-1 rounded-full bg-white px-2 py-1 text-[10px] font-bold uppercase tracking-wide text-indigo-600 shadow-sm">
                                    <Sparkles size={10} />
                                    AI Feedback
                                  </span>
                                </div>

                                <p className="mt-1 text-xs text-slate-500">
                                  Interview feedback based on
                                  your answer
                                </p>
                              </div>
                            </div>

                            {currentQuestion.score !==
                              null && (
                              <div className="flex items-center gap-3">
                                {scoreLabel && (
                                  <span className="rounded-full bg-white px-3 py-1.5 text-xs font-bold text-slate-700 shadow-sm">
                                    {scoreLabel}
                                  </span>
                                )}

                                <div className="flex items-baseline gap-1">
                                  <span className="text-3xl font-bold text-indigo-600">
                                    {
                                      currentQuestion.score
                                    }
                                  </span>

                                  <span className="text-sm font-medium text-slate-500">
                                    / 10
                                  </span>
                                </div>
                              </div>
                            )}
                          </div>
                        </div>

                        <div className="p-4 sm:p-5">
                          {currentQuestion.feedback && (
                            <div>
                              <div className="flex items-center gap-2">
                                <BrainCircuit
                                  size={16}
                                  className="text-indigo-600"
                                />

                                <p className="text-xs font-bold uppercase tracking-wide text-slate-500">
                                  Novis Feedback
                                </p>
                              </div>

                              <p className="mt-3 whitespace-pre-wrap text-sm leading-6 text-slate-700">
                                {
                                  currentQuestion.feedback
                                }
                              </p>
                            </div>
                          )}

                          {(currentStrengths.length > 0 ||
                            currentImprovements.length >
                              0) && (
                            <div className="mt-5 grid gap-4 lg:grid-cols-2">
                              {currentStrengths.length >
                                0 && (
                                <div className="rounded-xl border border-emerald-100 bg-emerald-50 p-4">
                                  <div className="flex items-center gap-2">
                                    <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-100 text-emerald-700">
                                      <Target size={16} />
                                    </div>

                                    <p className="text-sm font-bold text-emerald-800">
                                      Strengths
                                    </p>
                                  </div>

                                  <div className="mt-4 space-y-3">
                                    {currentStrengths.map(
                                      (strength, index) => (
                                        <div
                                          key={`${strength}-${index}`}
                                          className="flex items-start gap-2.5"
                                        >
                                          <div className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-emerald-100 text-emerald-700">
                                            <Check size={12} />
                                          </div>

                                          <p className="text-sm leading-5 text-emerald-900">
                                            {strength}
                                          </p>
                                        </div>
                                      ),
                                    )}
                                  </div>
                                </div>
                              )}

                              {currentImprovements.length >
                                0 && (
                                <div className="rounded-xl border border-amber-100 bg-amber-50 p-4">
                                  <div className="flex items-center gap-2">
                                    <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-amber-100 text-amber-700">
                                      <Lightbulb size={16} />
                                    </div>

                                    <p className="text-sm font-bold text-amber-800">
                                      Improvements
                                    </p>
                                  </div>

                                  <div className="mt-4 space-y-3">
                                    {currentImprovements.map(
                                      (
                                        improvement,
                                        index,
                                      ) => (
                                        <div
                                          key={`${improvement}-${index}`}
                                          className="flex items-start gap-2.5"
                                        >
                                          <div className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-amber-100 text-amber-700">
                                            <ArrowRight
                                              size={12}
                                            />
                                          </div>

                                          <p className="text-sm leading-5 text-amber-900">
                                            {improvement}
                                          </p>
                                        </div>
                                      ),
                                    )}
                                  </div>
                                </div>
                              )}
                            </div>
                          )}

                          {/* EVALUATION CTA */}
                          {session.status !==
                            "COMPLETED" && (
                            <div className="mt-5 border-t border-slate-100 pt-5">
                              {!isLastQuestion ? (
                                <button
                                  type="button"
                                  onClick={
                                    handleNextQuestion
                                  }
                                  className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 sm:w-auto"
                                >
                                  Continue to Question{" "}
                                  {currentQuestionIndex + 2}
                                  <ArrowRight size={17} />
                                </button>
                              ) : allAnswered ? (
                                <button
                                  type="button"
                                  disabled={completing}
                                  onClick={
                                    handleCompleteSession
                                  }
                                  className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-slate-950 px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60 sm:w-auto"
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
                                      <Trophy
                                        size={17}
                                      />
                                      Complete Session
                                    </>
                                  )}
                                </button>
                              ) : null}
                            </div>
                          )}
                        </div>
                      </div>
                    )}
                  </div>
                </article>

                {/* QUESTION NAVIGATION */}
                {session.questions.length > 1 && (
                  <section className="mt-5 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                    <button
                      type="button"
                      disabled={
                        isFirstQuestion ||
                        submittingQuestionId !== null
                      }
                      onClick={handlePreviousQuestion}
                      className="inline-flex w-full items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-5 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40 sm:w-auto"
                    >
                      <ChevronLeft size={17} />
                      Previous
                    </button>

                    <p className="text-center text-sm font-medium text-slate-500">
                      Question{" "}
                      {currentQuestionIndex + 1} of{" "}
                      {session.questions.length}
                    </p>

                    <button
                      type="button"
                      disabled={
                        isLastQuestion ||
                        submittingQuestionId !== null
                      }
                      onClick={handleNextQuestion}
                      className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-slate-950 px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-40 sm:w-auto"
                    >
                      Next
                      <ArrowRight size={17} />
                    </button>
                  </section>
                )}

                {/* COMPLETE SESSION */}
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
                            : `${answeredQuestions} of ${session.questions.length} questions answered.`}
                        </p>
                      </div>

                      <button
                        type="button"
                        disabled={
                          !allAnswered ||
                          completing ||
                          submittingQuestionId !== null
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

                {/* FINAL RESULT */}
                {result && (
                  <section className="mt-6 rounded-2xl bg-slate-950 p-5 text-white shadow-sm sm:p-6">
                    <div className="flex flex-col gap-6 sm:flex-row sm:items-start sm:justify-between">
                      <div>
                        <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-500">
                          <Trophy size={23} />
                        </div>

                        <p className="mt-5 text-sm font-medium text-indigo-300">
                          {isMockSession
                            ? "Mock interview complete"
                            : "Practice complete"}
                        </p>

                        <h2 className="mt-1 text-2xl font-bold">
                          Session Result
                        </h2>
                      </div>

                      {result.averageScore !== null && (
                        <div className="rounded-2xl bg-indigo-500 px-6 py-4">
                          <p className="text-xs font-medium text-indigo-100">
                            Average Score
                          </p>

                          <div className="mt-1 flex items-baseline gap-1">
                            <span className="text-3xl font-bold">
                              {result.averageScore}
                            </span>

                            <span className="text-sm text-indigo-100">
                              / 10
                            </span>
                          </div>
                        </div>
                      )}
                    </div>

                    <div className="mt-6 grid grid-cols-2 gap-3 sm:grid-cols-3">
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

                      <div className="col-span-2 rounded-xl bg-white/10 p-4 sm:col-span-1">
                        <p className="text-xs text-slate-400">
                          Unanswered
                        </p>

                        <p className="mt-1 text-xl font-bold">
                          {result.unansweredQuestions}
                        </p>
                      </div>
                    </div>

                    <div className="mt-6 flex flex-col gap-3 sm:flex-row">
                      <button
                        type="button"
                        onClick={() =>
                          router.push(
                            isMockSession
                              ? "/mock"
                              : "/questions",
                          )
                        }
                        className="w-full rounded-xl bg-white px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-slate-100 sm:w-auto"
                      >
                        {isMockSession
                          ? "Start Another Mock"
                          : "Practice Another Question"}
                      </button>

                      <button
                        type="button"
                        onClick={() =>
                          router.push("/dashboard")
                        }
                        className="w-full rounded-xl border border-white/20 px-5 py-3 text-sm font-semibold text-white transition hover:bg-white/10 sm:w-auto"
                      >
                        Back to Dashboard
                      </button>
                    </div>
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