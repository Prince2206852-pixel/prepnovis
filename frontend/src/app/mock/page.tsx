"use client";

import {
  ArrowLeft,
  BrainCircuit,
  ChevronDown,
  Loader2,
  Play,
  Sparkles,
} from "lucide-react";
import { useRouter } from "next/navigation";
import { FormEvent, useState } from "react";

import AuthGuard from "@/components/AuthGuard";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";
import { startPracticeSession } from "@/services/practiceService";
import type {
  DifficultyLevel,
  QuestionType,
} from "@/types/question";

const difficultyOptions: {
  value: DifficultyLevel;
  label: string;
  description: string;
}[] = [
  {
    value: "EASY",
    label: "Easy",
    description: "Good for basics and concept revision",
  },
  {
    value: "MEDIUM",
    label: "Medium",
    description: "Closer to a regular interview",
  },
  {
    value: "HARD",
    label: "Hard",
    description: "Deep concepts and challenging questions",
  },
];

const questionTypeOptions: {
  value: QuestionType;
  label: string;
}[] = [
  { value: "TECHNICAL", label: "Technical" },
  { value: "CODING", label: "Coding" },
  { value: "SYSTEM_DESIGN", label: "System Design" },
  { value: "BEHAVIORAL", label: "Behavioral" },
  { value: "MCQ", label: "MCQ" },
  { value: "OTHER", label: "Other" },
];

const questionCountOptions = [5, 10];

export default function MockPage() {
  const router = useRouter();

  const [category, setCategory] = useState("");
  const [topic, setTopic] = useState("");

  const [difficultyLevel, setDifficultyLevel] =
    useState<DifficultyLevel>("MEDIUM");

  const [questionType, setQuestionType] =
    useState<QuestionType>("TECHNICAL");

  const [totalQuestions, setTotalQuestions] = useState(5);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleStartMock(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const trimmedCategory = category.trim();
    const trimmedTopic = topic.trim();

    if (!trimmedCategory) {
      setError("Please enter a category.");
      return;
    }

    if (!trimmedTopic) {
      setError("Please enter a topic.");
      return;
    }

    try {
      setLoading(true);
      setError("");

      const session = await startPracticeSession({
        category: trimmedCategory,
        topic: trimmedTopic,
        difficultyLevel,
        questionType,
        totalQuestions,
        questionSource: "PREPNOVIS_MOCK",
      });

      if (!session.id) {
        throw new Error("Practice session was created without a session ID.");
      }

      if (session.assignedQuestions <= 0) {
        throw new Error(
          "Novis could not generate questions for this mock interview.",
        );
      }

      router.push(`/practice/${session.id}`);
    } catch (err) {
      console.error("Failed to start PrepNovis Mock:", err);

      setError(
        err instanceof Error
          ? err.message
          : "Unable to start the mock interview. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthGuard>
      <div className="min-h-screen overflow-x-hidden bg-slate-50">
        <Sidebar />

        <div className="min-h-screen w-full lg:ml-64 lg:w-[calc(100%-16rem)]">
          <Header />

          <main className="w-full px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
            <div className="mx-auto max-w-6xl">
              {/* BACK */}
              <button
                type="button"
                onClick={() => router.push("/practice")}
                disabled={loading}
                className="mb-6 inline-flex items-center gap-2 text-sm font-medium text-slate-500 transition hover:text-slate-900 disabled:cursor-not-allowed disabled:opacity-60"
              >
                <ArrowLeft size={17} />
                Back to Practice
              </button>

              {/* PAGE HEADER */}
              <section className="mb-8">
                <div className="flex items-start gap-4">
                  <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-indigo-600 text-white shadow-sm">
                    <BrainCircuit size={24} />
                  </div>

                  <div>
                    <div className="mb-1 flex items-center gap-2">
                      <p className="text-sm font-semibold text-indigo-600">
                        PrepNovis Mock
                      </p>

                      <Sparkles size={15} className="text-indigo-500" />
                    </div>

                    <h1 className="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
                      Build Your Mock Interview
                    </h1>

                    <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
                      Choose what you want to practice. Novis will generate
                      fresh interview questions based on your selection.
                    </p>
                  </div>
                </div>
              </section>

              <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_320px]">
                {/* CONFIGURATION */}
                <form
                  onSubmit={handleStartMock}
                  className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-7"
                >
                  <div className="mb-7">
                    <h2 className="text-lg font-bold text-slate-900">
                      Interview Configuration
                    </h2>

                    <p className="mt-1 text-sm text-slate-500">
                      Tell Novis what kind of interview you want to practice.
                    </p>
                  </div>

                  <div className="space-y-7">
                    {/* CATEGORY + TOPIC */}
                    <div className="grid gap-5 sm:grid-cols-2">
                      <div>
                        <label
                          htmlFor="category"
                          className="mb-2 block text-sm font-semibold text-slate-700"
                        >
                          Category
                        </label>

                        <input
                          id="category"
                          type="text"
                          value={category}
                          disabled={loading}
                          onChange={(event) => {
                            setCategory(event.target.value);

                            if (error) {
                              setError("");
                            }
                          }}
                          placeholder="e.g. Java"
                          className="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-50 disabled:cursor-not-allowed disabled:bg-slate-50"
                        />

                        <p className="mt-2 text-xs text-slate-400">
                          Example: Java, Spring Boot, AWS
                        </p>
                      </div>

                      <div>
                        <label
                          htmlFor="topic"
                          className="mb-2 block text-sm font-semibold text-slate-700"
                        >
                          Topic
                        </label>

                        <input
                          id="topic"
                          type="text"
                          value={topic}
                          disabled={loading}
                          onChange={(event) => {
                            setTopic(event.target.value);

                            if (error) {
                              setError("");
                            }
                          }}
                          placeholder="e.g. Collections"
                          className="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-50 disabled:cursor-not-allowed disabled:bg-slate-50"
                        />

                        <p className="mt-2 text-xs text-slate-400">
                          Example: Collections, Kafka, Microservices
                        </p>
                      </div>
                    </div>

                    {/* DIFFICULTY */}
                    <div>
                      <p className="mb-3 text-sm font-semibold text-slate-700">
                        Difficulty
                      </p>

                      <div className="grid gap-3 sm:grid-cols-3">
                        {difficultyOptions.map((option) => {
                          const selected =
                            difficultyLevel === option.value;

                          return (
                            <button
                              key={option.value}
                              type="button"
                              disabled={loading}
                              onClick={() =>
                                setDifficultyLevel(option.value)
                              }
                              className={`rounded-xl border p-4 text-left transition disabled:cursor-not-allowed disabled:opacity-60 ${
                                selected
                                  ? "border-indigo-500 bg-indigo-50 ring-1 ring-indigo-500"
                                  : "border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50"
                              }`}
                            >
                              <span
                                className={`block text-sm font-bold ${
                                  selected
                                    ? "text-indigo-700"
                                    : "text-slate-900"
                                }`}
                              >
                                {option.label}
                              </span>

                              <span className="mt-1 block text-xs leading-5 text-slate-500">
                                {option.description}
                              </span>
                            </button>
                          );
                        })}
                      </div>
                    </div>

                    {/* QUESTION TYPE */}
                    <div>
                      <label
                        htmlFor="questionType"
                        className="mb-2 block text-sm font-semibold text-slate-700"
                      >
                        Question Type
                      </label>

                      <div className="relative">
                        <select
                          id="questionType"
                          value={questionType}
                          disabled={loading}
                          onChange={(event) =>
                            setQuestionType(
                              event.target.value as QuestionType,
                            )
                          }
                          className="w-full appearance-none rounded-xl border border-slate-200 bg-white px-4 py-3 pr-11 text-sm font-medium text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-50 disabled:cursor-not-allowed disabled:bg-slate-50"
                        >
                          {questionTypeOptions.map((option) => (
                            <option
                              key={option.value}
                              value={option.value}
                            >
                              {option.label}
                            </option>
                          ))}
                        </select>

                        <ChevronDown
                          size={18}
                          className="pointer-events-none absolute right-4 top-1/2 -translate-y-1/2 text-slate-400"
                        />
                      </div>
                    </div>

                    {/* QUESTION COUNT */}
                    <div>
                      <p className="mb-3 text-sm font-semibold text-slate-700">
                        Number of Questions
                      </p>

                      <div className="grid grid-cols-2 gap-3">
                        {questionCountOptions.map((count) => {
                          const selected = totalQuestions === count;

                          return (
                            <button
                              key={count}
                              type="button"
                              disabled={loading}
                              onClick={() => setTotalQuestions(count)}
                              className={`rounded-xl border px-3 py-3 text-sm font-bold transition disabled:cursor-not-allowed disabled:opacity-60 ${
                                selected
                                  ? "border-indigo-500 bg-indigo-600 text-white"
                                  : "border-slate-200 bg-white text-slate-700 hover:border-slate-300 hover:bg-slate-50"
                              }`}
                            >
                              {count}
                            </button>
                          );
                        })}
                      </div>
                    </div>

                    {/* ERROR */}
                    {error && (
                      <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3">
                        <p className="text-sm font-medium text-red-700">
                          {error}
                        </p>
                      </div>
                    )}

                    {/* START */}
                    <button
                      type="submit"
                      disabled={loading}
                      className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-indigo-600 px-5 py-3.5 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-70"
                    >
                      {loading ? (
                        <>
                          <Loader2
                            size={18}
                            className="animate-spin"
                          />
                          Novis is preparing your interview...
                        </>
                      ) : (
                        <>
                          <Play size={18} />
                          Start Mock Interview
                        </>
                      )}
                    </button>
                  </div>
                </form>

                {/* SUMMARY */}
                <aside className="h-fit rounded-2xl bg-slate-950 p-5 text-white shadow-sm sm:p-6">
                  <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-indigo-500">
                    <BrainCircuit size={21} />
                  </div>

                  <p className="mt-5 text-xs font-semibold uppercase tracking-[0.16em] text-indigo-300">
                    Your Interview
                  </p>

                  <h2 className="mt-2 text-lg font-bold">
                    Mock Summary
                  </h2>

                  <div className="mt-5 space-y-4">
                    <SummaryItem
                      label="Category"
                      value={category.trim() || "Not selected"}
                    />

                    <SummaryItem
                      label="Topic"
                      value={topic.trim() || "Not selected"}
                    />

                    <SummaryItem
                      label="Difficulty"
                      value={formatLabel(difficultyLevel)}
                    />

                    <SummaryItem
                      label="Question Type"
                      value={formatLabel(questionType)}
                    />

                    <SummaryItem
                      label="Questions"
                      value={String(totalQuestions)}
                    />
                  </div>

                  <div className="mt-6 rounded-xl border border-slate-800 bg-slate-900 p-4">
                    <div className="flex items-start gap-3">
                      <Sparkles
                        size={17}
                        className="mt-0.5 shrink-0 text-indigo-400"
                      />

                      <p className="text-xs leading-5 text-slate-300">
                        Novis will generate fresh questions for this
                        interview. They will not be added to your Saved
                        Questions.
                      </p>
                    </div>
                  </div>
                </aside>
              </div>
            </div>
          </main>
        </div>
      </div>
    </AuthGuard>
  );
}

function SummaryItem({
  label,
  value,
}: {
  label: string;
  value: string;
}) {
  return (
    <div className="border-b border-slate-800 pb-3 last:border-b-0 last:pb-0">
      <p className="text-xs text-slate-400">{label}</p>

      <p className="mt-1 break-words text-sm font-semibold text-slate-100">
        {value}
      </p>
    </div>
  );
}

function formatLabel(value: string) {
  return value
    .toLowerCase()
    .split("_")
    .map(
      (word) =>
        word.charAt(0).toUpperCase() + word.slice(1),
    )
    .join(" ");
}