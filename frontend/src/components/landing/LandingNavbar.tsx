"use client";

import Link from "next/link";
import { useState } from "react";
import { Menu, X } from "lucide-react";

import PrepNovisLogo from "@/components/PrepNovisLogo";

export default function LandingNavbar() {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 border-b border-white/10 bg-slate-950/90 backdrop-blur-xl">
      <div className="mx-auto flex h-20 max-w-7xl items-center justify-between px-6 lg:px-8">
        <Link href="/" aria-label="PrepNovis home">
          <PrepNovisLogo size={42} />
        </Link>

        <nav className="hidden items-center gap-9 md:flex">
          <Link
            href="#features"
            className="text-sm font-medium text-slate-300 transition hover:text-white"
          >
            Features
          </Link>

          <Link
            href="#how-it-works"
            className="text-sm font-medium text-slate-300 transition hover:text-white"
          >
            How it works
          </Link>

          <Link
            href="#novis"
            className="text-sm font-medium text-slate-300 transition hover:text-white"
          >
            Novis
          </Link>
        </nav>

        <div className="hidden items-center gap-3 md:flex">
          <Link
            href="/login"
            className="rounded-xl px-5 py-2.5 text-sm font-semibold text-slate-200 transition hover:bg-white/5 hover:text-white"
          >
            Sign in
          </Link>

          <Link
            href="/register"
            className="rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white shadow-lg shadow-indigo-950/30 transition hover:bg-indigo-500"
          >
            Start Practicing Free
          </Link>
        </div>

        <button
          type="button"
          onClick={() => setMenuOpen((current) => !current)}
          aria-label="Toggle navigation"
          aria-expanded={menuOpen}
          className="rounded-lg p-2 text-slate-200 transition hover:bg-white/10 md:hidden"
        >
          {menuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      {menuOpen && (
        <div className="border-t border-white/10 bg-slate-950 px-6 py-5 md:hidden">
          <nav className="flex flex-col gap-1">
            <Link
              href="#features"
              onClick={() => setMenuOpen(false)}
              className="rounded-lg px-3 py-3 text-sm font-medium text-slate-300 hover:bg-white/5 hover:text-white"
            >
              Features
            </Link>

            <Link
              href="#how-it-works"
              onClick={() => setMenuOpen(false)}
              className="rounded-lg px-3 py-3 text-sm font-medium text-slate-300 hover:bg-white/5 hover:text-white"
            >
              How it works
            </Link>

            <Link
              href="#novis"
              onClick={() => setMenuOpen(false)}
              className="rounded-lg px-3 py-3 text-sm font-medium text-slate-300 hover:bg-white/5 hover:text-white"
            >
              Novis
            </Link>

            <div className="my-3 h-px bg-white/10" />

            <Link
              href="/login"
              className="rounded-lg px-3 py-3 text-sm font-semibold text-slate-200"
            >
              Sign in
            </Link>

            <Link
              href="/register"
              className="mt-2 rounded-xl bg-indigo-600 px-4 py-3 text-center text-sm font-semibold text-white"
            >
              Start Practicing Free
            </Link>
          </nav>
        </div>
      )}
    </header>
  );
}