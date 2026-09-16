import {
  BarChart3,
  CheckCircle2,
  ChevronRight,
  MessageSquareText,
  Play,
  Sparkles,
  Target,
} from "lucide-react";

const steps = [
  {
    number: "01",
    title: "Choose what to practice",
    description:
      "Practice from your Saved Questions or start a PrepNovis Mock based on the topic and difficulty you want to prepare for.",
  },
  {
    number: "02",
    title: "Answer like a real interview",
    description:
      "Work through questions one by one and explain your answer just like you would in an actual interview.",
  },
  {
    number: "03",
    title: "Get evaluated by Novis",
    description:
      "Novis evaluates your answer and gives you a score, feedback, strengths and clear areas where you can improve.",
  },
  {
    number: "04",
    title: "Track your progress",
    description:
      "Use your results and analytics to understand your preparation and focus more on the areas that need work.",
  },
];

export default function HowItWorks() {
  return (
    <section
      id="how-it-works"
      className="relative overflow-hidden bg-slate-950 py-24 text-white sm:py-28"
    >
      <div className="pointer-events-none absolute inset-0">
        <div className="absolute -left-32 top-32 h-96 w-96 rounded-full bg-indigo-600/10 blur-[120px]" />
        <div className="absolute -right-32 bottom-0 h-96 w-96 rounded-full bg-purple-600/10 blur-[120px]" />
      </div>

      <div className="relative mx-auto max-w-7xl px-6 lg:px-8">
        <div className="mx-auto max-w-3xl text-center">
          <p className="text-sm font-semibold uppercase tracking-[0.18em] text-indigo-400">
            See PrepNovis in action
          </p>

          <h2 className="mt-4 text-4xl font-bold tracking-tight sm:text-5xl">
            Practice. Get feedback.
            <span className="block bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">
              Improve for the next one.
            </span>
          </h2>

          <p className="mx-auto mt-5 max-w-2xl text-base leading-7 text-slate-400">
            PrepNovis turns interview preparation into a repeatable
            practice cycle instead of just reading questions and
            memorizing answers.
          </p>
        </div>

        <div className="mt-16 grid gap-12 lg:grid-cols-[0.8fr_1.2fr] lg:items-center">
          {/* Steps */}
          <div className="space-y-3">
            {steps.map((step, index) => (
              <div
                key={step.number}
                className="group flex gap-4 rounded-2xl border border-white/10 bg-white/[0.03] p-5 transition hover:border-indigo-400/30 hover:bg-white/[0.06]"
              >
                <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-indigo-500/10 text-xs font-bold text-indigo-300">
                  {step.number}
                </div>

                <div>
                  <h3 className="font-semibold text-white">
                    {step.title}
                  </h3>

                  <p className="mt-2 text-sm leading-6 text-slate-400">
                    {step.description}
                  </p>
                </div>

                {index < steps.length - 1 && (
                  <ChevronRight
                    size={18}
                    className="ml-auto hidden shrink-0 self-center text-slate-700 sm:block"
                  />
                )}
              </div>
            ))}
          </div>

          {/* Product workflow preview */}
          <div className="relative">
            <div className="absolute -inset-10 rounded-full bg-indigo-600/10 blur-3xl" />

            <div className="relative overflow-hidden rounded-[28px] border border-white/10 bg-slate-900 shadow-2xl shadow-black/30">
              <div className="flex items-center justify-between border-b border-white/10 px-5 py-4">
                <div className="flex items-center gap-2">
                  <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-indigo-600">
                    <Play size={14} fill="currentColor" />
                  </div>

                  <div>
                    <p className="text-xs font-semibold text-white">
                      Practice Session
                    </p>
                    <p className="text-[10px] text-slate-500">
                      Java Backend • Medium
                    </p>
                  </div>
                </div>

                <span className="rounded-full bg-indigo-500/10 px-3 py-1 text-[10px] font-semibold text-indigo-300">
                  In progress
                </span>
              </div>

              <div className="p-5 sm:p-7">
                <div className="grid gap-4 sm:grid-cols-3">
                  <div className="rounded-xl border border-white/10 bg-slate-950/60 p-4">
                    <p className="text-xs text-slate-500">
                      Questions
                    </p>
                    <p className="mt-1 text-xl font-bold text-white">
                      8
                    </p>
                  </div>

                  <div className="rounded-xl border border-white/10 bg-slate-950/60 p-4">
                    <p className="text-xs text-slate-500">
                      Answered
                    </p>
                    <p className="mt-1 text-xl font-bold text-white">
                      6
                    </p>
                  </div>

                  <div className="rounded-xl border border-white/10 bg-slate-950/60 p-4">
                    <p className="text-xs text-slate-500">
                      Avg. score
                    </p>
                    <p className="mt-1 text-xl font-bold text-indigo-300">
                      8.2
                    </p>
                  </div>
                </div>

                <div className="mt-5 rounded-2xl border border-white/10 bg-slate-950/60 p-5">
                  <div className="flex items-start justify-between gap-4">
                    <div>
                      <p className="text-[11px] font-semibold uppercase tracking-[0.14em] text-indigo-400">
                        Current question
                      </p>

                      <p className="mt-2 text-sm font-medium leading-6 text-slate-200">
                        How do you make a REST API idempotent when
                        the same request can be received multiple times?
                      </p>
                    </div>

                    <MessageSquareText
                      size={20}
                      className="shrink-0 text-slate-600"
                    />
                  </div>

                  <div className="mt-5 h-2 overflow-hidden rounded-full bg-slate-800">
                    <div className="h-full w-3/4 rounded-full bg-indigo-500" />
                  </div>

                  <p className="mt-2 text-right text-[10px] text-slate-500">
                    6 of 8 completed
                  </p>
                </div>

                <div
                  id="novis-evaluation"
                  className="mt-5 rounded-2xl border border-indigo-400/20 bg-indigo-500/[0.06] p-5"
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-indigo-500/15 text-indigo-300">
                        <Sparkles size={19} />
                      </div>

                      <div>
                        <p className="text-sm font-semibold text-white">
                          Novis Evaluation
                        </p>
                        <p className="text-[11px] text-slate-500">
                          Previous answer
                        </p>
                      </div>
                    </div>

                    <div className="rounded-xl bg-indigo-500/10 px-3 py-2 text-lg font-bold text-indigo-300">
                      8.5
                    </div>
                  </div>

                  <div className="mt-5 grid gap-3 sm:grid-cols-2">
                    <div className="rounded-xl bg-emerald-400/[0.05] p-3">
                      <p className="flex items-center gap-2 text-xs font-semibold text-emerald-400">
                        <CheckCircle2 size={14} />
                        Strengths
                      </p>

                      <p className="mt-2 text-xs leading-5 text-slate-400">
                        Good understanding and clear explanation of
                        the main approach.
                      </p>
                    </div>

                    <div className="rounded-xl bg-amber-400/[0.05] p-3">
                      <p className="flex items-center gap-2 text-xs font-semibold text-amber-300">
                        <Target size={14} />
                        Improve
                      </p>

                      <p className="mt-2 text-xs leading-5 text-slate-400">
                        Add more detail about handling duplicate
                        requests in production.
                      </p>
                    </div>
                  </div>
                </div>

                <div className="mt-5 flex items-center justify-between rounded-xl border border-white/10 px-4 py-3">
                  <div className="flex items-center gap-2 text-xs text-slate-400">
                    <BarChart3 size={15} />
                    Session progress
                  </div>

                  <span className="text-xs font-semibold text-emerald-400">
                    Keep improving ↑
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}