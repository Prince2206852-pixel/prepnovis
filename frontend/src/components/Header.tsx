"use client";

import { Bell, Search } from "lucide-react";

export default function Header() {
  return (
    <header className="flex h-20 items-center justify-between border-b border-slate-200 bg-white px-8">
      <div>
        <h2 className="text-lg font-semibold text-slate-900">
          Interview Preparation
        </h2>
        <p className="text-sm text-slate-500">
          Practice. Improve. Get interview-ready.
        </p>
      </div>

      <div className="flex items-center gap-4">
        <div className="hidden items-center gap-2 rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 lg:flex">
          <Search size={18} className="text-slate-400" />

          <input
            type="text"
            placeholder="Search..."
            className="w-48 bg-transparent text-sm text-slate-700 outline-none placeholder:text-slate-400"
          />
        </div>

        <button
          type="button"
          aria-label="Notifications"
          className="relative flex h-10 w-10 items-center justify-center rounded-xl border border-slate-200 text-slate-600 transition hover:bg-slate-50"
        >
          <Bell size={19} />

          <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-indigo-500" />
        </button>

        <div className="flex items-center gap-3 border-l border-slate-200 pl-4">
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-indigo-100 font-semibold text-indigo-700">
            P
          </div>

          <div className="hidden sm:block">
            <p className="text-sm font-semibold text-slate-900">Prince</p>
            <p className="text-xs text-slate-500">Java Developer</p>
          </div>
        </div>
      </div>
    </header>
  );
}