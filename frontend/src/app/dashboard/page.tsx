"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import {
  BookOpen,
  BrainCircuit,
  Target,
  Trophy,
} from "lucide-react";

import AuthGuard from "@/components/AuthGuard";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";
import PerformanceChart from "@/features/dashboard/PerformanceChart";
import RecentSessions from "@/features/dashboard/RecentSessions";
import StatCard from "@/features/dashboard/StatCard";
import { AuthUser, getAuthUser } from "@/lib/auth";
import { getAnalyticsDashboard } from "@/services/analyticsService";
import { AnalyticsDashboard } from "@/types/analytics";

export default function DashboardPage() {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [analytics, setAnalytics] =
    useState<AnalyticsDashboard | null>(null);
  const [analyticsLoading, setAnalyticsLoading] = useState(true);
  const [analyticsError, setAnalyticsError] = useState("");

  const router = useRouter();

  useEffect(() => {
    setUser(getAuthUser());

    async function loadAnalytics() {
      try {
        setAnalyticsLoading(true);
        setAnalyticsError("");

        const data = await getAnalyticsDashboard();
        setAnalytics(data);
      } catch (error) {
        setAnalyticsError(
          error instanceof Error
            ? error.message
            : "Unable to load your analytics."
        );
      } finally {
        setAnalyticsLoading(false);
      }
    }

    void loadAnalytics();
  }, []);

  const averageScore = analytics?.averageScore ?? 0;
  const highestScore = analytics?.highestScore ?? 0;

  const savedAverage = analytics?.savedQuestionsAverageScore ?? 0;
  const mockAverage = analytics?.prepNovisMockAverageScore ?? 0;

  const savedProgress = Math.min(
    100,
    Math.max(0, savedAverage * 10)
  );

  const mockProgress = Math.min(
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
            {/* WELCOME */}
            <section className="mb-6 flex flex-col gap-5 sm:mb-8 xl:flex-row xl:items-center xl:justify-between">
              <div className="min-w-0">
                <p className="mb-2 text-sm font-medium text-indigo-600">
                  Welcome back
                </p>

                <h1 className="break-words text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
                  Good morning, {user?.fullName ?? "there"}
                </h1>

                <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
                  Keep practicing consistently and use Novis feedback to improve
                  your interview answers.
                </p>
              </div>

              <div className="grid w-full grid-cols-1 gap-3 sm:grid-cols-2 xl:w-auto">
                <button
                  type="button"
                  onClick={() => router.push("/questions")}
                  className="flex w-full items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-semibold text-slate-700 shadow-sm transition hover:bg-slate-50"
                >
                  <BookOpen size={18} className="shrink-0" />
                  Practice Saved Questions
                </button>

                <button
                  type="button"
                  onClick={() => router.push("/mock")}
                  className="flex w-full items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700"
                >
                  <BrainCircuit size={18} className="shrink-0" />
                  Start PrepNovis Mock
                </button>
              </div>
            </section>

            {/* ANALYTICS ERROR */}
            {analyticsError && (
              <div className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {analyticsError}
              </div>
            )}

            {/* STATS */}
            <section className="mb-6 grid grid-cols-1 gap-4 sm:mb-8 sm:grid-cols-2 sm:gap-5 xl:grid-cols-4">
              <StatCard
                title="Total Sessions"
                value={
                  analyticsLoading
                    ? "..."
                    : analytics?.totalSessions ?? 0
                }
                description="Practice sessions started"
                icon={Target}
              />

              <StatCard
                title="Questions Answered"
                value={
                  analyticsLoading
                    ? "..."
                    : analytics?.totalQuestionsAnswered ?? 0
                }
                description="Across saved and mock sessions"
                icon={BookOpen}
              />

              <StatCard
                title="Average Score"
                value={
                  analyticsLoading
                    ? "..."
                    : averageScore.toFixed(1)
                }
                description="Average Novis evaluation score"
                icon={BrainCircuit}
              />

              <StatCard
                title="Best Score"
                value={
                  analyticsLoading
                    ? "..."
                    : highestScore.toFixed(1)
                }
                description="Your highest evaluation score"
                icon={Trophy}
              />
            </section>

            {/* PERFORMANCE */}
            <section className="mb-6 grid min-w-0 grid-cols-1 gap-5 sm:mb-8 sm:gap-6 xl:grid-cols-[minmax(0,1.6fr)_minmax(0,1fr)]">
              <div className="min-w-0 overflow-hidden">
                <PerformanceChart
                  sessions={analytics?.recentSessions ?? []}
                />
              </div>

              <div className="min-w-0 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6">
                <div className="mb-6">
                  <h2 className="text-base font-semibold text-slate-900">
                    Practice Performance
                  </h2>

                  <p className="mt-1 text-sm text-slate-500">
                    Saved Questions vs PrepNovis Mock
                  </p>
                </div>

                <div className="space-y-6">
                  {/* SAVED */}
                  <div>
                    <div className="mb-2 flex items-center justify-between gap-3">
                      <div className="flex min-w-0 items-center gap-2">
                        <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-emerald-50 text-emerald-600">
                          <BookOpen size={17} />
                        </div>

                        <div className="min-w-0">
                          <p className="text-sm font-semibold text-slate-800">
                            Saved Questions
                          </p>

                          <p className="text-xs text-slate-500">
                            {analyticsLoading
                              ? "Loading..."
                              : `${
                                  analytics?.savedQuestionsAnswered ?? 0
                                } answered`}
                          </p>
                        </div>
                      </div>

                      <span className="shrink-0 text-lg font-bold text-slate-900">
                        {analyticsLoading
                          ? "..."
                          : `${savedAverage.toFixed(1)}/10`}
                      </span>
                    </div>

                    <div className="h-2 overflow-hidden rounded-full bg-slate-100">
                      <div
                        className="h-full rounded-full bg-emerald-500 transition-all duration-500"
                        style={{
                          width: `${savedProgress}%`,
                        }}
                      />
                    </div>
                  </div>

                  {/* MOCK */}
                  <div>
                    <div className="mb-2 flex items-center justify-between gap-3">
                      <div className="flex min-w-0 items-center gap-2">
                        <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-indigo-50 text-indigo-600">
                          <BrainCircuit size={17} />
                        </div>

                        <div className="min-w-0">
                          <p className="text-sm font-semibold text-slate-800">
                            PrepNovis Mock
                          </p>

                          <p className="text-xs text-slate-500">
                            {analyticsLoading
                              ? "Loading..."
                              : `${
                                  analytics
                                    ?.prepNovisMockQuestionsAnswered ?? 0
                                } answered`}
                          </p>
                        </div>
                      </div>

                      <span className="shrink-0 text-lg font-bold text-slate-900">
                        {analyticsLoading
                          ? "..."
                          : `${mockAverage.toFixed(1)}/10`}
                      </span>
                    </div>

                    <div className="h-2 overflow-hidden rounded-full bg-slate-100">
                      <div
                        className="h-full rounded-full bg-indigo-500 transition-all duration-500"
                        style={{
                          width: `${mockProgress}%`,
                        }}
                      />
                    </div>
                  </div>
                </div>

                <div className="mt-7 rounded-xl bg-slate-50 p-4">
                  <p className="text-sm font-semibold text-slate-900">
                    Performance snapshot
                  </p>

                  <p className="mt-1 text-sm leading-6 text-slate-500">
                    {analyticsLoading
                      ? "Loading your practice performance..."
                      : analytics &&
                          analytics.totalQuestionsAnswered > 0
                        ? `You have answered ${analytics.totalQuestionsAnswered} questions with an overall average score of ${averageScore.toFixed(
                            1
                          )}/10.`
                        : "Complete your first practice session to start building your performance history."}
                  </p>
                </div>
              </div>
            </section>

            {/* RECENT + NOVIS */}
            <section className="grid min-w-0 grid-cols-1 gap-5 sm:gap-6 xl:grid-cols-[minmax(0,1.6fr)_minmax(0,1fr)]">
              <div className="min-w-0 overflow-hidden">
                <RecentSessions
                  sessions={analytics?.recentSessions ?? []}
                />
              </div>

              <div className="min-w-0 rounded-2xl bg-slate-950 p-5 text-white shadow-sm sm:p-6">
                <div className="mb-5 flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-500">
                  <BrainCircuit size={23} />
                </div>

                <p className="text-sm font-medium text-indigo-300">
                  Meet Novis
                </p>

                <h2 className="mt-2 text-xl font-bold sm:text-2xl">
                  Your AI interview coach
                </h2>

                <p className="mt-3 text-sm leading-6 text-slate-300">
                  Answer interview questions and Novis will evaluate your
                  response, give you a score, identify strengths, and show where
                  you can improve.
                </p>

                <button
                  type="button"
                  onClick={() => router.push("/practice")}
                  className="mt-6 w-full rounded-xl bg-white px-4 py-3 text-sm font-semibold text-slate-950 transition hover:bg-slate-100"
                >
                  Start practicing
                </button>
              </div>
            </section>
          </main>
        </div>
      </div>
    </AuthGuard>
  );
}