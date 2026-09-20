import Link from "next/link";
import {
  ArrowRight,
  CheckCircle2,
  Clock3,
  Target,
} from "lucide-react";

import { AnalyticsRecentSession } from "@/types/analytics";

interface RecentSessionsProps {
  sessions: AnalyticsRecentSession[];
}

function formatSessionDate(date: string | null) {
  if (!date) {
    return "In progress";
  }

  return new Intl.DateTimeFormat("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
  }).format(new Date(date));
}

export default function RecentSessions({
  sessions,
}: RecentSessionsProps) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6">
      <div className="mb-5 flex items-center justify-between gap-4">
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
          className="flex shrink-0 items-center gap-1 text-sm font-medium text-indigo-600 transition hover:text-indigo-700"
        >
          View all
          <ArrowRight size={16} />
        </Link>
      </div>

      {sessions.length === 0 ? (
        <div className="rounded-xl bg-slate-50 px-5 py-10 text-center">
          <div className="mx-auto flex h-11 w-11 items-center justify-center rounded-xl bg-indigo-50 text-indigo-600">
            <Target size={20} />
          </div>

          <p className="mt-4 text-sm font-semibold text-slate-700">
            No practice sessions yet
          </p>

          <p className="mt-1 text-sm text-slate-500">
            Start practicing and your recent sessions will appear here.
          </p>

          <Link
            href="/practice"
            className="mt-4 inline-flex items-center justify-center rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700"
          >
            Start practicing
          </Link>
        </div>
      ) : (
        <div className="divide-y divide-slate-100">
          {sessions.map((session, index) => {
            const isCompleted = session.status === "COMPLETED";
            const Icon = isCompleted ? CheckCircle2 : Clock3;

            return (
              <Link
                key={session.sessionId}
                href={`/practice/${session.sessionId}`}
                className="flex items-center justify-between gap-4 py-4 transition first:pt-0 last:pb-0 hover:bg-slate-50 sm:px-2"
              >
                <div className="flex min-w-0 items-center gap-4">
                  <div
                    className={`flex h-11 w-11 shrink-0 items-center justify-center rounded-xl ${
                      isCompleted
                        ? "bg-emerald-50 text-emerald-600"
                        : "bg-amber-50 text-amber-600"
                    }`}
                  >
                    <Icon size={20} />
                  </div>

                  <div className="min-w-0">
                    <p className="truncate text-sm font-semibold text-slate-900">
                      Practice Session {index + 1}
                    </p>

                    <div className="mt-1 flex flex-wrap items-center gap-2 text-xs text-slate-500">
                      <span>
                        {session.answeredQuestions}/{session.assignedQuestions}{" "}
                        answered
                      </span>

                      <span>•</span>

                      <span>
                        {isCompleted ? "Completed" : "In progress"}
                      </span>

                      <span>•</span>

                      <span>{formatSessionDate(session.completedAt)}</span>
                    </div>
                  </div>
                </div>

                <div className="shrink-0 text-right">
                  <p className="text-lg font-bold text-slate-900">
                    {session.averageScore.toFixed(1)}
                  </p>

                  <p className="text-xs text-slate-500">out of 10</p>
                </div>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}