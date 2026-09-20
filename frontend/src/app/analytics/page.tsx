"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import {
  ArrowRight,
  BarChart3,
  BookOpen,
  BrainCircuit,
  CheckCircle2,
  Clock3,
  Target,
  Trophy,
} from "lucide-react";

import AuthGuard from "@/components/AuthGuard";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";
import PerformanceChart from "@/features/dashboard/PerformanceChart";
import { getAnalyticsDashboard } from "@/services/analyticsService";
import {
  AnalyticsDashboard,
  AnalyticsRecentSession,
} from "@/types/analytics";

function formatDate(date: string | null) {
  if (!date) {
    return "In progress";
  }

  return new Intl.DateTimeFormat("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
  }).format(new Date(date));
}

function getScoreLabel(score: number) {
  if (score >= 9) return "Excellent";
  if (score >= 8) return "Strong";
  if (score >= 7) return "Good";
  if (score >= 5) return "Developing";

  return "Needs improvement";
}

function StatCard({
  title,
  value,
  description,
  icon: Icon,
}: {
  title: string;
  value: string | number;
  description: string;
  icon: typeof Target;
}) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0">
          <p className="text-sm font-medium text-slate-500">{title}</p>

          <p className="mt-3 text-3xl font-bold tracking-tight text-slate-900">
            {value}
          </p>

          <p className="mt-2 text-xs leading-5 text-slate-500">
            {description}
          </p>
        </div>

        <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-indigo-50 text-indigo-600">
          <Icon size={20} />
        </div>
      </div>
    </div>
  );
}

function SessionRow({
  session,
  number,
}: {
  session: AnalyticsRecentSession;
  number: number;
}) {
  const completed = session.status === "COMPLETED";
  const Icon = completed ? CheckCircle2 : Clock3;

  return (
    <Link
      href={`/practice/${session.sessionId}`}
      className="flex flex-col gap-4 py-5 transition hover:bg-slate-50 sm:flex-row sm:items-center sm:justify-between sm:px-3"
    >
      <div className="flex min-w-0 items-center gap-4">
        <div
          className={`flex h-11 w-11 shrink-0 items-center justify-center rounded-xl ${
            completed
              ? "bg-emerald-50 text-emerald-600"
              : "bg-amber-50 text-amber-600"
          }`}
        >
          <Icon size={20} />
        </div>

        <div className="min-w-0">
          <p className="text-sm font-semibold text-slate-900">
            Practice Session {number}
          </p>

          <div className="mt-1 flex flex-wrap items-center gap-2 text-xs text-slate-500">
            <span>
              {session.answeredQuestions}/{session.assignedQuestions} answered
            </span>

            <span>•</span>

            <span>{completed ? "Completed" : "In progress"}</span>

            <span>•</span>

            <span>{formatDate(session.completedAt)}</span>
          </div>
        </div>
      </div>

      <div className="flex items-center justify-between gap-5 sm:justify-end">
        <div className="text-left sm:text-right">
          <p className="text-lg font-bold text-slate-900">
            {session.averageScore.toFixed(1)}
            <span className="text-sm font-medium text-slate-400">/10</span>
          </p>

          <p className="text-xs text-slate-500">
            {session.answeredQuestions > 0
              ? getScoreLabel(session.averageScore)
              : "No answers yet"}
          </p>
        </div>

        <ArrowRight size={18} className="shrink-0 text-slate-400" />
      </div>
    </Link>
  );
}

export default function AnalyticsPage() {
  const [analytics, setAnalytics] =
    useState<AnalyticsDashboard | null>(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadAnalytics() {
      try {
        setLoading(true);
        setError("");

        const data = await getAnalyticsDashboard();

        setAnalytics(data);
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : "Unable to load your analytics."
        );
      } finally {
        setLoading(false);
      }
    }

    void loadAnalytics();
  }, []);

  const averageScore = analytics?.averageScore ?? 0;
  const highestScore = analytics?.highestScore ?? 0;

  const savedAverage =
    analytics?.savedQuestionsAverageScore ?? 0;

  const mockAverage =
    analytics?.prepNovisMockAverageScore ?? 0;

  const savedAnswered =
    analytics?.savedQuestionsAnswered ?? 0;

  const mockAnswered =
    analytics?.prepNovisMockQuestionsAnswered ?? 0;

  const savedWidth = Math.min(
    100,
    Math.max(0, savedAverage * 10)
  );

  const mockWidth = Math.min(
    100,
    Math.max(0, mockAverage * 10)
  );

  return (
    <AuthGuard>
      <div className="min-h-screen overflow-x-hidden bg-slate-50">
        <Sidebar />

        <div className="min-h-screen w-full lg:ml-64 lg:w-[calc(100%-16rem)]">
          <Header />

          <main className="w-full px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
            {/* PAGE HEADER */}
            <section className="mb-8">
              <div className="flex items-center gap-2 text-sm font-medium text-indigo-600">
                <BarChart3 size={17} />
                Performance
              </div>

              <h1 className="mt-2 text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
                Results & Analytics
              </h1>

              <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
                Track your practice activity, Novis evaluation scores, and
                recent interview performance.
              </p>
            </section>

            {/* ERROR */}
            {error && (
              <div className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}

            {/* MAIN STATS */}
            <section className="mb-8 grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
              <StatCard
                title="Average Score"
                value={
                  loading
                    ? "..."
                    : `${averageScore.toFixed(1)}/10`
                }
                description="Across all evaluated answers"
                icon={BrainCircuit}
              />

              <StatCard
                title="Highest Score"
                value={
                  loading
                    ? "..."
                    : `${highestScore.toFixed(1)}/10`
                }
                description="Your best Novis evaluation"
                icon={Trophy}
              />

              <StatCard
                title="Questions Answered"
                value={
                  loading
                    ? "..."
                    : analytics?.totalQuestionsAnswered ?? 0
                }
                description="Saved Questions and PrepNovis Mock"
                icon={BookOpen}
              />

              <StatCard
                title="Completed Sessions"
                value={
                  loading
                    ? "..."
                    : `${analytics?.completedSessions ?? 0}/${
                        analytics?.totalSessions ?? 0
                      }`
                }
                description="Completed out of sessions started"
                icon={Target}
              />
            </section>

            {/* SAVED VS MOCK */}
            <section className="mb-8 grid grid-cols-1 gap-5 lg:grid-cols-2">
              {/* SAVED QUESTIONS */}
              <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex min-w-0 items-center gap-3">
                    <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-emerald-50 text-emerald-600">
                      <BookOpen size={20} />
                    </div>

                    <div className="min-w-0">
                      <h2 className="text-base font-semibold text-slate-900">
                        Saved Questions
                      </h2>

                      <p className="mt-1 text-sm text-slate-500">
                        Your personal question bank
                      </p>
                    </div>
                  </div>

                  <div className="shrink-0 text-right">
                    <p className="text-2xl font-bold text-slate-900">
                      {loading
                        ? "..."
                        : savedAverage.toFixed(1)}
                    </p>

                    <p className="text-xs text-slate-500">
                      out of 10
                    </p>
                  </div>
                </div>

                <div className="mt-6 h-2.5 overflow-hidden rounded-full bg-slate-100">
                  <div
                    className="h-full rounded-full bg-emerald-500 transition-all duration-500"
                    style={{
                      width: `${savedWidth}%`,
                    }}
                  />
                </div>

                <div className="mt-5 flex items-center justify-between gap-4 rounded-xl bg-slate-50 p-4">
                  <div>
                    <p className="text-xs font-medium uppercase tracking-wide text-slate-400">
                      Answered
                    </p>

                    <p className="mt-1 text-xl font-bold text-slate-900">
                      {loading ? "..." : savedAnswered}
                    </p>
                  </div>

                  <Link
                    href="/questions"
                    className="flex items-center gap-1 text-sm font-semibold text-indigo-600 transition hover:text-indigo-700"
                  >
                    Practice
                    <ArrowRight size={16} />
                  </Link>
                </div>
              </div>

              {/* PREPNOVIS MOCK */}
              <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex min-w-0 items-center gap-3">
                    <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-indigo-50 text-indigo-600">
                      <BrainCircuit size={20} />
                    </div>

                    <div className="min-w-0">
                      <h2 className="text-base font-semibold text-slate-900">
                        PrepNovis Mock
                      </h2>

                      <p className="mt-1 text-sm text-slate-500">
                        Fresh AI-generated interview questions
                      </p>
                    </div>
                  </div>

                  <div className="shrink-0 text-right">
                    <p className="text-2xl font-bold text-slate-900">
                      {loading
                        ? "..."
                        : mockAverage.toFixed(1)}
                    </p>

                    <p className="text-xs text-slate-500">
                      out of 10
                    </p>
                  </div>
                </div>

                <div className="mt-6 h-2.5 overflow-hidden rounded-full bg-slate-100">
                  <div
                    className="h-full rounded-full bg-indigo-500 transition-all duration-500"
                    style={{
                      width: `${mockWidth}%`,
                    }}
                  />
                </div>

                <div className="mt-5 flex items-center justify-between gap-4 rounded-xl bg-slate-50 p-4">
                  <div>
                    <p className="text-xs font-medium uppercase tracking-wide text-slate-400">
                      Answered
                    </p>

                    <p className="mt-1 text-xl font-bold text-slate-900">
                      {loading ? "..." : mockAnswered}
                    </p>
                  </div>

                  <Link
                    href="/mock"
                    className="flex items-center gap-1 text-sm font-semibold text-indigo-600 transition hover:text-indigo-700"
                  >
                    Start Mock
                    <ArrowRight size={16} />
                  </Link>
                </div>
              </div>
            </section>

            {/* PERFORMANCE CHART */}
            <section className="mb-8">
              <PerformanceChart
                sessions={analytics?.recentSessions ?? []}
              />
            </section>

            {/* RECENT SESSIONS */}
            <section className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6">
              <div className="mb-2">
                <h2 className="text-base font-semibold text-slate-900">
                  Recent Sessions
                </h2>

                <p className="mt-1 text-sm text-slate-500">
                  Open a recent session to review your answers and Novis
                  feedback.
                </p>
              </div>

              {loading ? (
                <div className="py-12 text-center text-sm text-slate-500">
                  Loading your recent sessions...
                </div>
              ) : !analytics ||
                analytics.recentSessions.length === 0 ? (
                <div className="py-12 text-center">
                  <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-50 text-indigo-600">
                    <Target size={21} />
                  </div>

                  <p className="mt-4 text-sm font-semibold text-slate-700">
                    No results yet
                  </p>

                  <p className="mt-1 text-sm text-slate-500">
                    Complete a practice session and your results will appear
                    here.
                  </p>

                  <Link
                    href="/practice"
                    className="mt-5 inline-flex rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700"
                  >
                    Start practicing
                  </Link>
                </div>
              ) : (
                <div className="divide-y divide-slate-100">
                  {analytics.recentSessions.map(
                    (session, index) => (
                      <SessionRow
                        key={session.sessionId}
                        session={session}
                        number={index + 1}
                      />
                    )
                  )}
                </div>
              )}
            </section>
          </main>
        </div>
      </div>
    </AuthGuard>
  );
}