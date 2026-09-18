"use client";

import { BookOpen, BrainCircuit, Play } from "lucide-react";
import { useRouter } from "next/navigation";

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
            <section className="mb-8">
              <p className="mb-2 text-sm font-medium text-indigo-600">
                Practice
              </p>

              <h1 className="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
                Practice Interview Questions
              </h1>

              <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
                Practice your saved questions or start a PrepNovis Mock
                interview.
              </p>
            </section>

            <section className="grid gap-5 lg:grid-cols-2">
              {/* SAVED QUESTIONS */}
              <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-50 text-indigo-600">
                  <BookOpen size={23} />
                </div>

                <h2 className="mt-5 text-xl font-bold text-slate-900">
                  Practice Saved Questions
                </h2>

                <p className="mt-2 text-sm leading-6 text-slate-500">
                  Choose a question from your personal question bank and
                  practice your answer.
                </p>

                <button
                  type="button"
                  onClick={() => router.push("/questions")}
                  className="mt-6 inline-flex w-full items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 sm:w-auto"
                >
                  <Play size={17} />
                  Choose Question
                </button>
              </div>

              {/* PREPNOVIS MOCK */}
              <div className="rounded-2xl bg-slate-950 p-5 text-white shadow-sm sm:p-6">
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-500">
                  <BrainCircuit size={23} />
                </div>

                <p className="mt-5 text-sm font-medium text-indigo-300">
                  PrepNovis Mock
                </p>

                <h2 className="mt-1 text-xl font-bold">
                  Practice Fresh Questions
                </h2>

                <p className="mt-2 text-sm leading-6 text-slate-300">
                  Start a mock interview with fresh questions and get Novis
                  evaluation on your answers.
                </p>

                <button
                  type="button"
                  onClick={() => router.push("/mock")}
                  className="mt-6 inline-flex w-full items-center justify-center gap-2 rounded-xl bg-white px-4 py-3 text-sm font-semibold text-slate-950 transition hover:bg-slate-100 sm:w-auto"
                >
                  <BrainCircuit size={17} />
                  Start PrepNovis Mock
                </button>
              </div>
            </section>
          </main>
        </div>
      </div>
    </AuthGuard>
  );
}