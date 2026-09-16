import Link from "next/link";
import { ArrowRight, Sparkles } from "lucide-react";

export default function FinalCTA() {
  return (
    <section className="bg-slate-50 px-6 py-20 sm:py-24 lg:px-8">
      <div className="relative mx-auto max-w-6xl overflow-hidden rounded-[36px] bg-gradient-to-br from-indigo-600 via-violet-600 to-purple-600 px-6 py-16 text-center shadow-2xl shadow-indigo-200 sm:px-12 sm:py-20">
        <div className="pointer-events-none absolute -left-20 -top-20 h-64 w-64 rounded-full bg-white/10 blur-3xl" />
        <div className="pointer-events-none absolute -bottom-24 -right-16 h-72 w-72 rounded-full bg-purple-300/20 blur-3xl" />

        <div className="relative">
          <div className="mx-auto flex w-fit items-center gap-2 rounded-full border border-white/20 bg-white/10 px-4 py-2 text-sm font-semibold text-indigo-50">
            <Sparkles size={15} />
            Your next interview starts with practice
          </div>

          <h2 className="mx-auto mt-6 max-w-3xl text-4xl font-bold tracking-tight text-white sm:text-5xl">
            Your next interview shouldn&apos;t be your first practice session.
          </h2>

          <p className="mx-auto mt-5 max-w-2xl text-base leading-7 text-indigo-100">
            Build your question bank, practice with PrepNovis Mock,
            get evaluated by Novis and keep improving before the
            real interview.
          </p>

          <Link
            href="/register"
            className="mt-9 inline-flex items-center justify-center gap-2 rounded-xl bg-white px-6 py-3.5 text-sm font-bold text-indigo-700 shadow-lg transition hover:-translate-y-0.5 hover:bg-indigo-50"
          >
            Start Practicing Free
            <ArrowRight size={17} />
          </Link>

          <p className="mt-4 text-xs text-indigo-200">
            Create your PrepNovis account and start practicing.
          </p>
        </div>
      </div>
    </section>
  );
}