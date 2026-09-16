import Link from "next/link";

import PrepNovisLogo from "@/components/PrepNovisLogo";

export default function LandingFooter() {
  return (
    <footer className="border-t border-white/10 bg-slate-950 text-slate-400">
      <div className="mx-auto max-w-7xl px-6 py-12 lg:px-8">
        <div className="grid gap-10 md:grid-cols-[1.5fr_1fr_1fr]">
          <div>
            <PrepNovisLogo size={40} />

            <p className="mt-5 max-w-sm text-sm leading-6">
              Save questions. Practice interviews. Get evaluated by
              Novis. Understand your weak areas and keep improving.
            </p>
          </div>

          <div>
            <p className="text-sm font-semibold text-white">
              Product
            </p>

            <div className="mt-4 flex flex-col gap-3 text-sm">
              <Link
                href="#features"
                className="transition hover:text-white"
              >
                Features
              </Link>

              <Link
                href="#how-it-works"
                className="transition hover:text-white"
              >
                How it works
              </Link>

              <Link
                href="/register"
                className="transition hover:text-white"
              >
                Start Practicing
              </Link>
            </div>
          </div>

          <div>
            <p className="text-sm font-semibold text-white">
              Account
            </p>

            <div className="mt-4 flex flex-col gap-3 text-sm">
              <Link
                href="/login"
                className="transition hover:text-white"
              >
                Sign in
              </Link>

              <Link
                href="/register"
                className="transition hover:text-white"
              >
                Create account
              </Link>
            </div>
          </div>
        </div>

        <div className="mt-12 flex flex-col gap-3 border-t border-white/10 pt-7 text-xs text-slate-500 sm:flex-row sm:items-center sm:justify-between">
          <p>
            © {new Date().getFullYear()} PrepNovis. All rights reserved.
          </p>

          <p>
            Practice. Improve. Get interview-ready.
          </p>
        </div>
      </div>
    </footer>
  );
}