import Link from "next/link";
import {
  ArrowRight,
  CheckCircle2,
  Clock3,
  Code2,
  Sparkles,
  Target,
} from "lucide-react";

export default function HeroSection() {
  return (
    <section className="relative overflow-hidden bg-slate-950">
      {/* Background effects */}
      <div className="pointer-events-none absolute inset-0">
        <div className="absolute -left-40 top-20 h-96 w-96 rounded-full bg-indigo-600/20 blur-[120px]" />
        <div className="absolute right-0 top-10 h-[460px] w-[460px] rounded-full bg-purple-600/15 blur-[130px]" />
      </div>

      <div className="relative mx-auto grid min-h-[720px] max-w-7xl items-center gap-16 px-6 py-20 lg:grid-cols-[0.95fr_1.05fr] lg:px-8 lg:py-24">
        {/* Left */}
        <div className="max-w-2xl">
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-indigo-400/20 bg-indigo-500/10 px-4 py-2 text-sm font-medium text-indigo-300">
            <Sparkles size={16} />
            Practice smarter with Novis
          </div>

          <h1 className="text-5xl font-bold leading-[1.05] tracking-tight text-white sm:text-6xl lg:text-7xl">
            Don&apos;t just prepare.
            <span className="mt-2 block bg-gradient-to-r from-indigo-400 via-violet-400 to-purple-400 bg-clip-text text-transparent">
              Practice like it&apos;s
              <br />
              the real interview.
            </span>
          </h1>

          <p className="mt-7 max-w-xl text-lg leading-8 text-slate-400">
            Save interview questions, practice with PrepNovis Mock,
            get your answers evaluated by Novis, and understand
            exactly where you need to improve.
          </p>

          <div className="mt-9 flex flex-col gap-3 sm:flex-row">
            <Link
              href="/register"
              className="inline-flex items-center justify-center gap-2 rounded-xl bg-indigo-600 px-6 py-3.5 text-sm font-semibold text-white shadow-xl shadow-indigo-950/40 transition hover:bg-indigo-500"
            >
              Start Practicing Free
              <ArrowRight size={17} />
            </Link>

            <a
              href="#how-it-works"
              className="inline-flex items-center justify-center rounded-xl border border-white/10 bg-white/5 px-6 py-3.5 text-sm font-semibold text-slate-200 transition hover:bg-white/10 hover:text-white"
            >
              See how it works
            </a>
          </div>

          <div className="mt-9 flex flex-wrap gap-x-6 gap-y-3 text-sm text-slate-400">
            <span className="flex items-center gap-2">
              <CheckCircle2 size={16} className="text-emerald-400" />
              Save real interview questions
            </span>

            <span className="flex items-center gap-2">
              <CheckCircle2 size={16} className="text-emerald-400" />
              Practice anytime
            </span>

            <span className="flex items-center gap-2">
              <CheckCircle2 size={16} className="text-emerald-400" />
              Improve with Novis
            </span>
          </div>
        </div>

        {/* Right product preview */}
        <div className="relative mx-auto w-full max-w-xl">
          <div className="absolute -inset-8 rounded-[40px] bg-indigo-600/10 blur-3xl" />

          <div className="relative overflow-hidden rounded-[28px] border border-white/10 bg-slate-900 shadow-2xl shadow-black/40">
            {/* Window bar */}
            <div className="flex items-center justify-between border-b border-white/10 bg-slate-900/90 px-5 py-4">
              <div className="flex gap-2">
                <span className="h-2.5 w-2.5 rounded-full bg-slate-600" />
                <span className="h-2.5 w-2.5 rounded-full bg-slate-600" />
                <span className="h-2.5 w-2.5 rounded-full bg-slate-600" />
              </div>

              <span className="text-xs font-medium text-slate-500">
                PrepNovis Practice
              </span>
            </div>

            <div className="p-5 sm:p-7">
              <div className="mb-5 flex items-center justify-between">
                <div>
                  <p className="text-xs font-semibold uppercase tracking-[0.18em] text-indigo-400">
                    PrepNovis Mock
                  </p>

                  <h3 className="mt-1 text-lg font-semibold text-white">
                    Java Backend Interview
                  </h3>
                </div>

                <div className="flex items-center gap-1.5 rounded-lg border border-white/10 bg-white/5 px-3 py-2 text-xs text-slate-300">
                  <Clock3 size={14} />
                  Question 3 of 8
                </div>
              </div>

              {/* Question */}
              <div className="rounded-2xl border border-white/10 bg-slate-950/70 p-5">
                <div className="mb-3 flex items-center gap-2 text-xs font-medium text-indigo-300">
                  <Code2 size={15} />
                  Java • Microservices
                </div>

                <p className="text-sm font-medium leading-6 text-slate-200">
                  How would you handle a downstream microservice
                  failure without affecting the complete request flow?
                </p>

                <div className="mt-5 rounded-xl border border-white/10 bg-slate-900 px-4 py-3">
                  <p className="text-xs leading-5 text-slate-500">
                    Your answer...
                  </p>

                  <p className="mt-2 text-sm leading-6 text-slate-300">
                    I would use a circuit breaker with a fallback...
                  </p>
                </div>
              </div>

              {/* Evaluation */}
              <div
                id="novis"
                className="mt-4 rounded-2xl border border-indigo-400/20 bg-gradient-to-br from-indigo-500/10 to-purple-500/10 p-5"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-indigo-500/15 text-indigo-300">
                      <Sparkles size={18} />
                    </div>

                    <div>
                      <p className="text-sm font-semibold text-white">
                        Novis Evaluation
                      </p>
                      <p className="text-xs text-slate-500">
                        Answer analysis
                      </p>
                    </div>
                  </div>

                  <div className="text-right">
                    <p className="text-2xl font-bold text-white">
                      8.5
                    </p>
                    <p className="text-[11px] text-slate-500">
                      out of 10
                    </p>
                  </div>
                </div>

                <div className="mt-5 grid gap-3 sm:grid-cols-2">
                  <div className="rounded-xl border border-emerald-400/10 bg-emerald-400/5 p-3">
                    <p className="flex items-center gap-1.5 text-xs font-semibold text-emerald-400">
                      <CheckCircle2 size={14} />
                      Strength
                    </p>

                    <p className="mt-2 text-xs leading-5 text-slate-400">
                      Clear explanation with a practical approach.
                    </p>
                  </div>

                  <div className="rounded-xl border border-amber-400/10 bg-amber-400/5 p-3">
                    <p className="flex items-center gap-1.5 text-xs font-semibold text-amber-300">
                      <Target size={14} />
                      Improve
                    </p>

                    <p className="mt-2 text-xs leading-5 text-slate-400">
                      Explain retry and fallback handling in more detail.
                    </p>
                  </div>
                </div>
              </div>

              <div className="mt-5 flex justify-end">
                <div className="rounded-lg bg-indigo-600 px-4 py-2 text-xs font-semibold text-white">
                  Next Question →
                </div>
              </div>
            </div>
          </div>

          {/* Floating score */}
          <div className="absolute -bottom-7 -left-5 hidden rounded-2xl border border-white/10 bg-slate-900/95 p-4 shadow-xl backdrop-blur md:block">
            <p className="text-xs text-slate-500">
              Practice progress
            </p>

            <div className="mt-2 flex items-end gap-2">
              <span className="text-2xl font-bold text-white">
                82%
              </span>
              <span className="pb-1 text-xs font-medium text-emerald-400">
                Improving
              </span>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}