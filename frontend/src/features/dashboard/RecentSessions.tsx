import Link from "next/link";
import { ArrowRight, BookOpen, BrainCircuit } from "lucide-react";

const recentSessions = [
  {
    id: "1",
    type: "PrepNovis Mock",
    topic: "Java & Spring Boot",
    questions: 10,
    score: 8.6,
    date: "Today",
  },
  {
    id: "2",
    type: "Saved Questions",
    topic: "Microservices",
    questions: 5,
    score: 7.8,
    date: "Yesterday",
  },
  {
    id: "3",
    type: "PrepNovis Mock",
    topic: "SQL & Database",
    questions: 8,
    score: 8.1,
    date: "Sep 5",
  },
];

export default function RecentSessions() {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-5 flex items-center justify-between">
        <div>
          <h3 className="text-base font-semibold text-slate-900">
            Recent Practice Sessions
          </h3>

          <p className="mt-1 text-sm text-slate-500">
            Your latest interview practice activity
          </p>
        </div>

        <Link
          href="/analytics"
          className="flex items-center gap-1 text-sm font-medium text-indigo-600 transition hover:text-indigo-700"
        >
          View all
          <ArrowRight size={16} />
        </Link>
      </div>

      <div className="divide-y divide-slate-100">
        {recentSessions.map((session) => {
          const isMock = session.type === "PrepNovis Mock";
          const Icon = isMock ? BrainCircuit : BookOpen;

          return (
            <div
              key={session.id}
              className="flex items-center justify-between gap-4 py-4 first:pt-0 last:pb-0"
            >
              <div className="flex min-w-0 items-center gap-4">
                <div
                  className={`flex h-11 w-11 shrink-0 items-center justify-center rounded-xl ${
                    isMock
                      ? "bg-indigo-50 text-indigo-600"
                      : "bg-emerald-50 text-emerald-600"
                  }`}
                >
                  <Icon size={20} />
                </div>

                <div className="min-w-0">
                  <p className="truncate text-sm font-semibold text-slate-900">
                    {session.topic}
                  </p>

                  <div className="mt-1 flex flex-wrap items-center gap-2 text-xs text-slate-500">
                    <span>{session.type}</span>
                    <span>•</span>
                    <span>{session.questions} questions</span>
                    <span>•</span>
                    <span>{session.date}</span>
                  </div>
                </div>
              </div>

              <div className="shrink-0 text-right">
                <p className="text-lg font-bold text-slate-900">
                  {session.score.toFixed(1)}
                </p>
                <p className="text-xs text-slate-500">out of 10</p>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}