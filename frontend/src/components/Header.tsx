"use client";

import { Bell } from "lucide-react";

import { getAuthUser } from "@/lib/auth";

export default function Header() {
  const user = getAuthUser();

  const initials = user?.fullName
    ? user.fullName
        .split(" ")
        .filter(Boolean)
        .map((part) => part[0])
        .join("")
        .slice(0, 2)
        .toUpperCase()
    : "U";

  return (
    <header className="flex min-h-16 items-center justify-between border-b border-slate-200 bg-white px-4 py-3 pl-20 sm:px-6 sm:pl-20 lg:h-20 lg:px-8 lg:pl-8">
      <div className="min-w-0">
        <h2 className="truncate text-base font-semibold text-slate-900 sm:text-lg">
          Interview Preparation
        </h2>

        <p className="hidden text-sm text-slate-500 sm:block">
          Practice. Improve. Get interview-ready.
        </p>
      </div>

      <div className="flex shrink-0 items-center gap-2 sm:gap-3 lg:gap-4">
        <button
          type="button"
          aria-label="Notifications"
          className="relative flex h-10 w-10 shrink-0 items-center justify-center rounded-xl border border-slate-200 text-slate-600 transition hover:bg-slate-50"
        >
          <Bell size={18} />

          <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-indigo-500" />
        </button>

        <div className="flex items-center gap-2 sm:border-l sm:border-slate-200 sm:pl-3 lg:gap-3 lg:pl-4">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-indigo-100 text-sm font-semibold text-indigo-700">
            {initials}
          </div>

          <div className="hidden md:block">
            <p className="max-w-36 truncate text-sm font-semibold text-slate-900">
              {user?.fullName ?? "PrepNovis User"}
            </p>

            <p className="text-xs text-slate-500">
              {user?.role ?? "USER"}
            </p>
          </div>
        </div>
      </div>
    </header>
  );
}