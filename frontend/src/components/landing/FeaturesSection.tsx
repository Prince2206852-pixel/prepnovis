import {
  BarChart3,
  BookmarkPlus,
  BrainCircuit,
  MessageSquareText,
  Sparkles,
  Target,
} from "lucide-react";

const features = [
  {
    icon: BookmarkPlus,
    label: "Build your own question bank",
    title: "Saved Questions",
    description:
      "Save interview questions you collect from real interviews or preparation and keep them organized for future practice.",
    points: [
      "Save questions by topic",
      "Build your personal question bank",
      "Practice them whenever you want",
    ],
  },
  {
    icon: BrainCircuit,
    label: "Practice beyond your saved questions",
    title: "PrepNovis Mock",
    description:
      "Choose your topic and difficulty, then start a focused mock session with fresh interview questions.",
    points: [
      "Topic-based mock sessions",
      "Multiple difficulty levels",
      "Fresh questions for every practice session",
    ],
  },
  {
    icon: MessageSquareText,
    label: "Meet Novis",
    title: "Practice & Get Evaluated",
    description:
      "Answer interview questions and let Novis analyze your response so you know what you explained well and what needs improvement.",
    points: [
      "Answer evaluation",
      "Score and detailed feedback",
      "Strengths and improvement areas",
    ],
  },
  {
    icon: BarChart3,
    label: "Turn practice into progress",
    title: "Track Your Progress",
    description:
      "Understand how your interview preparation is improving over time instead of practicing without knowing where you stand.",
    points: [
      "Practice activity",
      "Performance insights",
      "Saved vs PrepNovis Mock progress",
    ],
  },
];

export default function FeaturesSection() {
  return (
    <section
      id="features"
      className="relative overflow-hidden bg-slate-50 py-24 sm:py-28"
    >
      <div className="pointer-events-none absolute left-1/2 top-0 h-72 w-72 -translate-x-1/2 rounded-full bg-indigo-200/30 blur-[110px]" />

      <div className="relative mx-auto max-w-7xl px-6 lg:px-8">
        <div className="mx-auto max-w-3xl text-center">
          <div className="mx-auto flex w-fit items-center gap-2 rounded-full border border-indigo-100 bg-indigo-50 px-4 py-2 text-sm font-semibold text-indigo-600">
            <Sparkles size={15} />
            Built for interview practice
          </div>

          <h2 className="mt-6 text-4xl font-bold tracking-tight text-slate-950 sm:text-5xl">
            Everything you need to
            <span className="block text-indigo-600">
              crack your next interview.
            </span>
          </h2>

          <p className="mx-auto mt-5 max-w-2xl text-base leading-7 text-slate-600 sm:text-lg">
            PrepNovis brings your questions, mock practice, Novis
            evaluation and progress tracking into one structured
            interview preparation workspace.
          </p>
        </div>

        <div className="mt-16 grid gap-6 lg:grid-cols-2">
          {features.map((feature, index) => {
            const Icon = feature.icon;

            return (
              <article
                key={feature.title}
                className="group relative overflow-hidden rounded-3xl border border-slate-200 bg-white p-7 shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-xl sm:p-9"
              >
                <div className="absolute right-5 top-3 text-7xl font-bold text-slate-50">
                  0{index + 1}
                </div>

                <div className="relative">
                  <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-indigo-50 text-indigo-600 transition group-hover:bg-indigo-600 group-hover:text-white">
                    <Icon size={23} />
                  </div>

                  <p className="mt-7 text-xs font-semibold uppercase tracking-[0.16em] text-indigo-600">
                    {feature.label}
                  </p>

                  <h3 className="mt-2 text-2xl font-bold tracking-tight text-slate-950">
                    {feature.title}
                  </h3>

                  <p className="mt-4 max-w-xl text-sm leading-7 text-slate-600">
                    {feature.description}
                  </p>

                  <div className="mt-7 space-y-3">
                    {feature.points.map((point) => (
                      <div
                        key={point}
                        className="flex items-start gap-3 text-sm text-slate-600"
                      >
                        <div className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-indigo-50">
                          <Target
                            size={11}
                            className="text-indigo-600"
                          />
                        </div>

                        <span>{point}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </article>
            );
          })}
        </div>
      </div>
    </section>
  );
}