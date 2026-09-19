"use client";

import { useRouter } from "next/navigation";
import {
  ArrowRight,
  BookOpen,
  BrainCircuit,
  Play,
} from "lucide-react";

import AuthGuard from "@/components/AuthGuard";
import Header from "@/components/Header";
import Sidebar from "@/components/Sidebar";

export default function PracticePage() {
  const router = useRouter();

  return (
    <AuthGuard>
      <div className="min-h-screen overflow-x-hidden bg-slate-50">
        <Sidebar />

        <div className="min-h-screen w-full lg:ml-64 lg:w-[calc(100%-16rem)]">
          <Header />

          <main className="w-full px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
            {/* PAGE HEADER */}
            <section className="mb-8">
              <p className="text-sm font-semibold text-indigo-600">
                Practice
              </p>

              <h1 className="mt-2 text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
                Practice Interview Questions
              </h1>

              <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
                Practice saved questions or start a fresh AI-powered
                mock interview with Novis.
              </p>
            </section>

            {/* PRACTICE OPTIONS */}
            <section className="grid gap-5 lg:grid-cols-2">
              {/* SAVED QUESTIONS */}
              <button
                type="button"
                onClick={() => router.push("/questions")}
                className="group w-full rounded-2xl border border-slate-200 bg-white p-5 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-indigo-200 hover:shadow-md sm:p-6"
              >
                <div className="flex items-start justify-between gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-50 text-indigo-600">
                    <BookOpen size={23} />
                  </div>

                  <ArrowRight
                    size={20}
                    className="mt-1 text-slate-300 transition group-hover:translate-x-1 group-hover:text-indigo-600"
                  />
                </div>

                <div className="mt-6">
                  <h2 className="text-lg font-bold text-slate-900">
                    Saved Questions
                  </h2>

                  <p className="mt-2 text-sm leading-6 text-slate-500">
                    Choose questions from your personal question bank,
                    answer them and get feedback from Novis.
                  </p>
                </div>

                <div className="mt-6 inline-flex items-center gap-2 text-sm font-semibold text-indigo-600">
                  <Play size={16} />
                  Start Practice
                </div>
              </button>

              {/* PREPNOVIS MOCK */}
              <button
                type="button"
                onClick={() => router.push("/mock")}
                className="group relative w-full overflow-hidden rounded-2xl bg-slate-950 p-5 text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-lg sm:p-6"
              >
                <div className="absolute -right-12 -top-12 h-40 w-40 rounded-full bg-indigo-500/10" />

                <div className="relative">
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-500 text-white">
                      <BrainCircuit size={23} />
                    </div>

                    <ArrowRight
                      size={20}
                      className="mt-1 text-slate-500 transition group-hover:translate-x-1 group-hover:text-white"
                    />
                  </div>

                  <div className="mt-6">
                    <div className="flex flex-wrap items-center gap-2">
                      <h2 className="text-lg font-bold text-white">
                        PrepNovis Mock
                      </h2>

                      <span className="rounded-full bg-indigo-500/15 px-2.5 py-1 text-[10px] font-bold uppercase tracking-wide text-indigo-300">
                        AI Interview
                      </span>
                    </div>

                    <p className="mt-2 text-sm leading-6 text-slate-400">
                      Configure your interview and let Novis generate
                      fresh questions for a real interview-style
                      practice session.
                    </p>
                  </div>

                  <div className="mt-6 inline-flex items-center gap-2 text-sm font-semibold text-indigo-300">
                    <Play size={16} />
                    Start Mock Interview
                  </div>
                </div>
              </button>
            </section>

            {/* INFO */}
            <section className="mt-6 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
              <div className="flex items-start gap-4">
                <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-violet-50 text-violet-600">
                  <BrainCircuit size={20} />
                </div>

                <div>
                  <h2 className="font-semibold text-slate-900">
                    Practice with Novis
                  </h2>

                  <p className="mt-1 text-sm leading-6 text-slate-500">
                    Submit your interview answer and Novis will
                    evaluate it with a score, feedback, strengths and
                    areas to improve.
                  </p>
                </div>
              </div>
            </section>
          </main>
        </div>
      </div>
    </AuthGuard>
  );
}