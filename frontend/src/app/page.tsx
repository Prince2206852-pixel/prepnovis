"use client";

import Image from "next/image";
import Link from "next/link";
import { FormEvent, useState } from "react";
import {
  ArrowRight,
  BarChart3,
  Bookmark,
  BrainCircuit,
  Check,
  Code2,
  Mail,
  MessageSquareText,
  Play,
  Send,
  Sparkles,
  Target,
} from "lucide-react";

import PrepNovisLogo from "@/components/PrepNovisLogo";
import { getApiUrl } from "@/lib/api";

const companies = [
  "Google",
  "Microsoft",
  "amazon",
  "Meta",
  "Adobe",
  "accenture",
  "TCS",
  "Infosys",
];

const features = [
  {
    icon: Bookmark,
    title: "Save Your Questions",
    text: "Keep track of questions from your interviews and preparation.",
  },
  {
    icon: BrainCircuit,
    title: "PrepNovis Mock",
    text: "Practice fresh, role-specific questions based on topic and difficulty.",
  },
  {
    icon: MessageSquareText,
    title: "Practice & Get Evaluated",
    text: "Answer like a real interview and get instant Novis feedback and score.",
  },
  {
    icon: BarChart3,
    title: "Track Your Progress",
    text: "See your improvement over time and understand where to focus next.",
  },
];

const howItWorks = [
  {
    number: "01",
    icon: Bookmark,
    title: "Choose Questions",
    text: "Practice from your Saved Questions or start a PrepNovis Mock.",
  },
  {
    number: "02",
    icon: MessageSquareText,
    title: "Answer Like an Interview",
    text: "Explain your answer just like you would in the real interview.",
  },
  {
    number: "03",
    icon: Sparkles,
    title: "Novis Evaluates",
    text: "Get your score, feedback, strengths and areas to improve.",
  },
  {
    number: "04",
    icon: BarChart3,
    title: "Improve & Track",
    text: "Use your results to understand weak areas and keep improving.",
  },
];

const faqs = [
  {
    question: "What is PrepNovis?",
    answer:
      "PrepNovis is an interview practice platform where you can save interview questions, practice them, take PrepNovis Mock sessions, get your answers evaluated by Novis and track your improvement.",
  },
  {
    question: "What is PrepNovis Mock?",
    answer:
      "PrepNovis Mock creates a focused practice session with fresh interview questions based on the topic and difficulty you choose.",
  },
  {
    question: "What does Novis evaluate?",
    answer:
      "Novis analyzes your interview answer and gives you a score, feedback, strengths and clear areas where you can improve.",
  },
  {
    question: "Can I practice my own interview questions?",
    answer:
      "Yes. Saved Questions lets you build your own question bank from real interviews or preparation and practice those questions whenever you want.",
  },
  {
    question: "Is PrepNovis only for Java developers?",
    answer:
      "No. PrepNovis is being designed as an interview practice platform across different roles, topics and technologies. Java and backend preparation are among the initial areas of focus.",
  },
];

export default function Home() {
  const [feedbackName, setFeedbackName] = useState("");
  const [feedbackEmail, setFeedbackEmail] = useState("");
  const [feedbackMessage, setFeedbackMessage] = useState("");
  const [feedbackStatus, setFeedbackStatus] = useState("");
  const [feedbackError, setFeedbackError] = useState("");
  const [isSendingFeedback, setIsSendingFeedback] = useState(false);

  async function handleFeedbackSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setFeedbackStatus("");
    setFeedbackError("");
    setIsSendingFeedback(true);

    try {
      const response = await fetch(getApiUrl("/feedback"), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: feedbackName.trim(),
          email: feedbackEmail.trim(),
          message: feedbackMessage.trim(),
        }),
      });

      if (!response.ok) {
        throw new Error("Unable to send feedback.");
      }

      setFeedbackStatus(
        "Thank you. Your feedback has been sent to the PrepNovis team.",
      );

      setFeedbackName("");
      setFeedbackEmail("");
      setFeedbackMessage("");
    } catch {
  setFeedbackError(
    "We couldn't send your feedback right now. Please try again in a moment.",
  );
}finally {
      setIsSendingFeedback(false);
    }
  }

  return (
    <main className="min-h-screen bg-[#f7f8fc] text-slate-950">
      {/* NAVBAR */}
      <header className="sticky top-0 z-50 border-b border-white/10 bg-[#071226]/95 backdrop-blur-xl">
        <div className="mx-auto flex h-[68px] max-w-[1180px] items-center justify-between px-5">
          <Link href="/" className="shrink-0">
            <PrepNovisLogo size={34} />
          </Link>

          <nav className="hidden items-center gap-7 md:flex">
            <a
              href="#features"
              className="text-[13px] font-medium text-slate-300 transition hover:text-white"
            >
              Features
            </a>

            <a
              href="#how-it-works"
              className="text-[13px] font-medium text-slate-300 transition hover:text-white"
            >
              How it works
            </a>

            <a
              href="#novis"
              className="text-[13px] font-medium text-slate-300 transition hover:text-white"
            >
              Novis
            </a>

            <a
              href="#faq"
              className="text-[13px] font-medium text-slate-300 transition hover:text-white"
            >
              FAQ
            </a>

            <a
              href="#feedback"
              className="text-[13px] font-medium text-slate-300 transition hover:text-white"
            >
              Feedback
            </a>
          </nav>

          <div className="flex items-center gap-3">
            <Link
              href="/login"
              className="hidden rounded-lg border border-white/15 px-4 py-2 text-[13px] font-semibold text-white transition hover:bg-white/5 sm:block"
            >
              Sign in
            </Link>

            <Link
              href="/register"
              className="rounded-lg bg-gradient-to-r from-indigo-500 to-violet-500 px-4 py-2 text-[13px] font-semibold text-white shadow-lg shadow-indigo-950/30 transition hover:brightness-110"
            >
              Start free
            </Link>
          </div>
        </div>
      </header>

      {/* HERO */}
      <section className="relative overflow-hidden bg-[#071226] text-white">
        <div className="pointer-events-none absolute left-[38%] top-20 h-[380px] w-[380px] rounded-full bg-blue-600/15 blur-[100px]" />
        <div className="pointer-events-none absolute right-[-80px] top-[-40px] h-[400px] w-[400px] rounded-full bg-violet-600/15 blur-[110px]" />

        <div className="relative mx-auto grid max-w-[1180px] gap-10 px-5 pb-10 pt-14 lg:grid-cols-[0.9fr_1.1fr] lg:items-center lg:pb-12 lg:pt-16">
          <div className="max-w-[520px]">
            <div className="inline-flex items-center gap-2 rounded-full border border-indigo-400/20 bg-indigo-400/10 px-3 py-1.5 text-[11px] font-semibold text-indigo-200">
              <Sparkles size={13} />
              Interview practice with Novis
            </div>

            <h1 className="mt-5 text-[42px] font-bold leading-[1.08] tracking-[-0.035em] sm:text-[50px] lg:text-[54px]">
              Don&apos;t just prepare.

              <span className="mt-1 block bg-gradient-to-r from-violet-400 via-purple-400 to-blue-400 bg-clip-text text-transparent">
                Practice like it&apos;s the
                <br />
                real interview.
              </span>
            </h1>

            <p className="mt-5 max-w-[490px] text-[14px] leading-6 text-slate-400">
              Save your own questions, practice with PrepNovis Mock,
              answer like a real interview, and let Novis give you a
              score, feedback and a clear path to improve.
            </p>

            <div className="mt-7 flex flex-wrap gap-3">
              <Link
                href="/register"
                className="inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-indigo-500 to-violet-500 px-5 py-3 text-[13px] font-semibold text-white shadow-lg shadow-indigo-950/40"
              >
                Start Practicing Free
                <ArrowRight size={15} />
              </Link>

              <a
                href="#how-it-works"
                className="inline-flex items-center gap-2 rounded-xl border border-white/15 bg-white/[0.03] px-5 py-3 text-[13px] font-semibold text-white"
              >
                <Play size={14} />
                See how it works
              </a>
            </div>
          </div>

          {/* PRODUCT PREVIEW */}
          <div className="relative mx-auto w-full max-w-[560px]">
            <div className="absolute inset-10 rounded-full bg-indigo-500/20 blur-[70px]" />

            <div className="relative rotate-[-1deg] rounded-[22px] border border-indigo-400/25 bg-[#101c34]/95 p-4 shadow-2xl shadow-black/30">
              <div className="mb-3 flex items-center justify-between">
                <span className="rounded-full bg-indigo-500/10 px-2.5 py-1 text-[10px] font-semibold text-indigo-300">
                  Java • Intermediate
                </span>

                <span className="text-[10px] text-slate-500">
                  3 / 8
                </span>
              </div>

              <div className="rounded-[16px] border border-white/10 bg-[#071226] p-4">
                <div className="flex items-center gap-2 text-[10px] font-medium text-indigo-300">
                  <Code2 size={13} />
                  Interview Question
                </div>

                <h3 className="mt-2 max-w-[430px] text-[15px] font-semibold leading-5 text-white">
                  Explain the difference between HashMap and
                  ConcurrentHashMap. When would you use each?
                </h3>

                <div className="mt-4 rounded-xl border border-white/10 bg-[#101b31] p-3">
                  <p className="text-[9px] uppercase tracking-wider text-slate-500">
                    Your Answer
                  </p>

                  <p className="mt-1.5 text-[12px] leading-5 text-slate-300">
                    HashMap is not thread-safe, whereas
                    ConcurrentHashMap is designed for concurrent
                    access...
                  </p>
                </div>
              </div>

              <div
                id="novis"
                className="mt-3 grid gap-3 sm:grid-cols-[0.65fr_1fr]"
              >
                <div className="rounded-[16px] border border-indigo-400/20 bg-gradient-to-br from-indigo-500/15 to-purple-500/10 p-4">
                  <div className="flex items-center gap-2">
                    <Sparkles
                      size={15}
                      className="text-violet-300"
                    />

                    <span className="text-[11px] font-semibold text-white">
                      Novis Evaluation
                    </span>
                  </div>

                  <p className="mt-3 text-[28px] font-bold text-emerald-400">
                    8.6
                    <span className="text-[13px] text-emerald-300">
                      {" "}
                      / 10
                    </span>
                  </p>

                  <p className="mt-1 text-[10px] leading-4 text-slate-400">
                    Strong answer with a clear explanation.
                  </p>
                </div>

                <div className="grid gap-2">
                  <div className="rounded-xl border border-emerald-400/15 bg-emerald-400/[0.06] p-3">
                    <p className="flex items-center gap-1.5 text-[10px] font-semibold text-emerald-400">
                      <Check size={12} />
                      Strengths
                    </p>

                    <p className="mt-1 text-[10px] text-slate-400">
                      Clear explanation and correct concepts
                    </p>
                  </div>

                  <div className="rounded-xl border border-rose-400/15 bg-rose-400/[0.05] p-3">
                    <p className="flex items-center gap-1.5 text-[10px] font-semibold text-rose-300">
                      <Target size={12} />
                      Improvements
                    </p>

                    <p className="mt-1 text-[10px] text-slate-400">
                      Add a practical use case
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* COMPANY STRIP */}
        <div className="relative mx-auto max-w-[1180px] border-t border-white/10 px-5 py-6">
          <p className="text-center text-[10px] font-medium text-slate-500">
            Practice for interviews at companies like
          </p>

          <div className="mt-4 flex flex-wrap items-center justify-center gap-x-8 gap-y-3 sm:gap-x-10">
            {companies.map((company) => (
              <span
                key={company}
                className="text-[14px] font-semibold tracking-tight text-slate-300/80"
              >
                {company}
              </span>
            ))}
          </div>

          <p className="mt-4 text-center text-[8px] text-slate-600">
            Company names and trademarks belong to their respective
            owners. PrepNovis is not affiliated with or endorsed by
            the companies shown.
          </p>
        </div>
      </section>

      {/* FEATURES */}
      <section
        id="features"
        className="bg-gradient-to-b from-[#f8faff] to-[#f3f5fb] py-16"
      >
        <div className="mx-auto max-w-[1180px] px-5">
          <div className="text-center">
            <h2 className="text-[30px] font-bold tracking-[-0.025em] text-[#0b1730] sm:text-[34px]">
              Everything you need to crack{" "}
              <span className="bg-gradient-to-r from-indigo-500 to-violet-500 bg-clip-text text-transparent">
                your next interview
              </span>
            </h2>

            <p className="mx-auto mt-3 max-w-[650px] text-[13px] leading-5 text-slate-500">
              PrepNovis combines your interview experience with
              structured practice and Novis evaluation to help you
              prepare better, faster and smarter.
            </p>
          </div>

          <div className="mt-10 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {features.map((feature, index) => {
              const Icon = feature.icon;

              return (
                <div
                  key={feature.title}
                  className="rounded-2xl border border-slate-200/80 bg-white p-5 shadow-[0_8px_30px_rgba(15,23,42,0.04)] transition hover:-translate-y-1 hover:shadow-lg"
                >
                  <div
                    className={`flex h-10 w-10 items-center justify-center rounded-xl ${
                      index === 0
                        ? "bg-violet-100 text-violet-600"
                        : index === 1
                          ? "bg-blue-100 text-blue-600"
                          : index === 2
                            ? "bg-indigo-100 text-indigo-600"
                            : "bg-amber-100 text-amber-600"
                    }`}
                  >
                    <Icon size={19} />
                  </div>

                  <h3 className="mt-4 text-[15px] font-bold text-[#0b1730]">
                    {feature.title}
                  </h3>

                  <p className="mt-2 text-[12px] leading-5 text-slate-500">
                    {feature.text}
                  </p>
                </div>
              );
            })}
          </div>

          {/* HOW IT WORKS */}
          <div
            id="how-it-works"
            className="mx-auto mt-16 max-w-[1080px]"
          >
            <div className="text-center">
              <p className="text-[10px] font-bold uppercase tracking-[0.2em] text-indigo-500">
                How PrepNovis works
              </p>

              <h2 className="mt-2 text-[28px] font-bold tracking-tight text-[#0b1730]">
                See PrepNovis in action
              </h2>

              <p className="mx-auto mt-2 max-w-[540px] text-[12px] leading-5 text-slate-500">
                Turn interview preparation into a simple practice
                cycle — choose, answer, get evaluated and improve.
              </p>
            </div>

            <div className="relative mt-9">
              <div className="absolute left-[12%] right-[12%] top-[27px] hidden h-px bg-gradient-to-r from-indigo-200 via-violet-300 to-indigo-200 md:block" />

              <div className="relative grid gap-4 md:grid-cols-4">
                {howItWorks.map((step) => {
                  const Icon = step.icon;

                  return (
                    <div
                      key={step.number}
                      className="group relative rounded-2xl border border-slate-200 bg-white p-5 shadow-[0_8px_30px_rgba(15,23,42,0.04)] transition duration-300 hover:-translate-y-1 hover:border-indigo-200 hover:shadow-lg"
                    >
                      <div className="relative z-10 flex items-center justify-between">
                        <div className="flex h-[54px] w-[54px] items-center justify-center rounded-2xl bg-gradient-to-br from-indigo-50 to-violet-100 text-indigo-600 ring-4 ring-[#f5f7fc]">
                          <Icon size={21} />
                        </div>

                        <span className="text-[11px] font-bold tracking-wider text-indigo-400">
                          {step.number}
                        </span>
                      </div>

                      <h3 className="mt-5 text-[14px] font-bold text-[#0b1730]">
                        {step.title}
                      </h3>

                      <p className="mt-2 text-[11px] leading-[18px] text-slate-500">
                        {step.text}
                      </p>
                    </div>
                  );
                })}
              </div>
            </div>

            <div className="mt-7 flex flex-wrap items-center justify-center gap-2 text-[10px] font-semibold text-slate-500">
              <span className="rounded-full border border-slate-200 bg-white px-3 py-1.5">
                Save / Choose
              </span>

              <ArrowRight size={12} className="text-indigo-400" />

              <span className="rounded-full border border-slate-200 bg-white px-3 py-1.5">
                Practice
              </span>

              <ArrowRight size={12} className="text-indigo-400" />

              <span className="rounded-full border border-indigo-100 bg-indigo-50 px-3 py-1.5 text-indigo-600">
                Novis Evaluation
              </span>

              <ArrowRight size={12} className="text-indigo-400" />

              <span className="rounded-full border border-slate-200 bg-white px-3 py-1.5">
                Improve
              </span>
            </div>
          </div>

          {/* FOUNDER + CTA */}
          <div className="mx-auto mt-14 grid max-w-[1050px] gap-4 lg:grid-cols-[1.05fr_0.95fr]">
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="text-3xl leading-none text-indigo-400">
                “
              </div>

              <p className="mt-1 text-[14px] font-medium leading-6 text-[#17233d]">
                Knowing the concepts and performing well in an
                interview are two different things. PrepNovis is being
                built to bridge that gap — helping you practice,
                understand where you&apos;re weak, and improve before
                the real interview.
              </p>

              <div className="mt-5 flex items-center gap-3">
                <div className="relative h-10 w-10 overflow-hidden rounded-full border border-indigo-200">
                  <Image
                    src="/images/prince-kumar.png"
                    alt="Prince Kumar, Founder and CEO of PrepNovis"
                    fill
                    sizes="40px"
                    className="object-cover"
                  />
                </div>

                <div>
                  <p className="text-[12px] font-bold text-[#0b1730]">
                    Prince Kumar
                  </p>

                  <p className="text-[10px] text-slate-500">
                    Founder &amp; CEO, PrepNovis
                  </p>
                </div>
              </div>
            </div>

            <div className="relative overflow-hidden rounded-2xl border border-indigo-100 bg-gradient-to-br from-white via-indigo-50 to-violet-100 p-6 text-center shadow-sm">
              <div className="absolute -right-10 -top-10 h-32 w-32 rounded-full bg-violet-300/30 blur-3xl" />

              <div className="relative">
                <Sparkles
                  size={20}
                  className="mx-auto text-indigo-500"
                />

                <h3 className="mx-auto mt-3 max-w-[300px] text-[20px] font-bold leading-6 text-[#0b1730]">
                  Ready to start your interview preparation journey?
                </h3>

                <p className="mx-auto mt-2 max-w-[320px] text-[11px] leading-4 text-slate-500">
                  Practice before the real interview and keep improving
                  with Novis.
                </p>

                <Link
                  href="/register"
                  className="mt-5 inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-indigo-500 to-violet-500 px-5 py-2.5 text-[12px] font-semibold text-white shadow-md"
                >
                  Start Practicing Free
                  <ArrowRight size={14} />
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ */}
<section
  id="faq"
  className="relative overflow-hidden bg-gradient-to-b from-[#f3f5fb] via-[#f8f9fd] to-white py-16"
>
  <div className="pointer-events-none absolute -left-24 top-10 h-64 w-64 rounded-full bg-indigo-200/25 blur-[90px]" />
  <div className="pointer-events-none absolute -right-24 bottom-0 h-72 w-72 rounded-full bg-violet-200/25 blur-[100px]" />

  <div className="relative mx-auto grid max-w-[1080px] gap-8 px-5 lg:grid-cols-[0.72fr_1.28fr]">
    {/* Left */}
    <div className="relative overflow-hidden rounded-[24px] bg-[#071226] p-7 text-white shadow-xl shadow-slate-200/50">
      <div className="absolute -right-16 -top-16 h-48 w-48 rounded-full bg-indigo-500/20 blur-3xl" />

      <div className="relative">
        <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-indigo-500/15 text-indigo-300">
          <MessageSquareText size={20} />
        </div>

        <p className="mt-7 text-[10px] font-bold uppercase tracking-[0.2em] text-indigo-300">
          Frequently asked questions
        </p>

        <h2 className="mt-3 max-w-[300px] text-[27px] font-bold leading-[1.15] tracking-tight">
          Everything you may want to know before you start.
        </h2>

        <p className="mt-4 max-w-[320px] text-[12px] leading-5 text-slate-400">
          Learn how PrepNovis Mock, Saved Questions, Novis evaluation
          and interview practice work together.
        </p>

        <div className="mt-8 rounded-2xl border border-white/10 bg-white/[0.04] p-4">
          <p className="text-[11px] font-semibold text-white">
            Still have a question?
          </p>

          <p className="mt-1 text-[10px] leading-4 text-slate-400">
            Send your suggestion or question directly to the PrepNovis team.
          </p>

          <a
            href="#feedback"
            className="mt-4 inline-flex items-center gap-2 rounded-lg bg-indigo-500 px-4 py-2 text-[11px] font-semibold text-white transition hover:bg-indigo-400"
          >
            Share feedback
            <ArrowRight size={13} />
          </a>
        </div>
      </div>
    </div>

    {/* Right */}
    <div className="space-y-3">
      {faqs.map((faq, index) => (
        <details
          key={faq.question}
          className="group overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-[0_8px_24px_rgba(15,23,42,0.035)] transition hover:border-indigo-200 hover:shadow-md"
        >
          <summary className="cursor-pointer list-none px-5 py-5">
            <div className="flex items-center gap-4">
              <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-indigo-50 text-[10px] font-bold text-indigo-600">
                0{index + 1}
              </div>

              <span className="flex-1 text-[13px] font-bold text-[#0b1730]">
                {faq.question}
              </span>

              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-slate-50 text-indigo-500 transition group-open:rotate-45 group-open:bg-indigo-50">
                <span className="text-lg font-light leading-none">
                  +
                </span>
              </div>
            </div>
          </summary>

          <div className="border-t border-slate-100 bg-gradient-to-r from-indigo-50/40 to-transparent px-5 py-4">
            <p className="pl-[52px] pr-5 text-[11px] leading-5 text-slate-500">
              {faq.answer}
            </p>
          </div>
        </details>
      ))}
    </div>
  </div>
</section>
      {/* FEEDBACK */}
      <section
        id="feedback"
        className="bg-gradient-to-b from-[#f7f8fc] to-[#eef1fa] py-16"
      >
        <div className="mx-auto grid max-w-[1050px] overflow-hidden rounded-[24px] border border-slate-200 bg-white shadow-[0_18px_60px_rgba(15,23,42,0.07)] lg:grid-cols-[0.8fr_1.2fr]">
          <div className="relative overflow-hidden bg-[#071226] p-7 text-white sm:p-9">
            <div className="absolute -left-16 -top-16 h-44 w-44 rounded-full bg-indigo-600/20 blur-3xl" />

            <div className="relative">
              <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-indigo-500/15 text-indigo-300">
                <Mail size={20} />
              </div>

              <p className="mt-7 text-[10px] font-bold uppercase tracking-[0.2em] text-indigo-300">
                Help us improve
              </p>

              <h2 className="mt-2 text-[25px] font-bold leading-tight">
                Share your feedback with PrepNovis
              </h2>

              <p className="mt-4 text-[12px] leading-5 text-slate-400">
                Found something we can improve? Have an idea for
                PrepNovis? Send it directly to the team.
              </p>

            <p className="mt-7 text-[11px] text-slate-500">
  Your feedback goes directly to the PrepNovis team.
</p>
            </div>
          </div>

          <form
            onSubmit={handleFeedbackSubmit}
            className="space-y-4 p-7 sm:p-9"
          >
            <div className="grid gap-4 sm:grid-cols-2">
              <div>
                <label
                  htmlFor="feedbackName"
                  className="mb-1.5 block text-[11px] font-semibold text-slate-700"
                >
                  Your name
                </label>

                <input
                  id="feedbackName"
                  type="text"
                  value={feedbackName}
                  onChange={(event) =>
                    setFeedbackName(event.target.value)
                  }
                  required
                  placeholder="Enter your name"
                  className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-[12px] outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                />
              </div>

              <div>
                <label
                  htmlFor="feedbackEmail"
                  className="mb-1.5 block text-[11px] font-semibold text-slate-700"
                >
                  Email (for reply)
                </label>

                <input
                  id="feedbackEmail"
                  type="email"
                  value={feedbackEmail}
                  onChange={(event) =>
                    setFeedbackEmail(event.target.value)
                  }
                  required
                  placeholder="you@example.com"
                  className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-[12px] outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
                />
              </div>
            </div>

            <div>
              <label
                htmlFor="feedbackMessage"
                className="mb-1.5 block text-[11px] font-semibold text-slate-700"
              >
                Feedback
              </label>

              <textarea
                id="feedbackMessage"
                value={feedbackMessage}
                onChange={(event) =>
                  setFeedbackMessage(event.target.value)
                }
                required
                maxLength={2000}
                rows={5}
                placeholder="Tell us what you think..."
                className="w-full resize-none rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-[12px] leading-5 outline-none transition focus:border-indigo-400 focus:ring-4 focus:ring-indigo-100"
              />

              <p className="mt-1 text-right text-[9px] text-slate-400">
                {feedbackMessage.length}/2000
              </p>
            </div>

            {feedbackStatus && (
              <div className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-[11px] text-emerald-700">
                {feedbackStatus}
              </div>
            )}

            {feedbackError && (
              <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-[11px] text-red-700">
                {feedbackError}
              </div>
            )}

            <button
              type="submit"
              disabled={isSendingFeedback}
              className="inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-indigo-500 to-violet-500 px-5 py-3 text-[12px] font-semibold text-white shadow-md transition hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-60"
            >
              <Send size={14} />

              {isSendingFeedback
                ? "Sending..."
                : "Send Feedback"}
            </button>
          </form>
        </div>
      </section>

      {/* FOOTER */}
      <footer className="bg-[#071226] text-slate-400">
        <div className="mx-auto max-w-[1180px] px-5">
          <div className="grid gap-10 py-11 sm:grid-cols-2 lg:grid-cols-[1.5fr_0.7fr_0.7fr_0.7fr]">
            <div>
              <PrepNovisLogo size={34} />

              <p className="mt-4 max-w-[310px] text-[11px] leading-5 text-slate-400">
                Practice real interview questions, improve your
                answers with Novis and track your progress before the
                real interview.
              </p>

              <p className="mt-4 text-[11px] font-medium text-slate-300">
                Practice. Improve. Get interview-ready.
              </p>
            </div>

            <div>
              <p className="text-[11px] font-bold uppercase tracking-[0.15em] text-white">
                Product
              </p>

              <div className="mt-4 flex flex-col gap-3 text-[11px]">
                <a
                  href="#features"
                  className="transition hover:text-white"
                >
                  Features
                </a>

                <a
                  href="#how-it-works"
                  className="transition hover:text-white"
                >
                  How it works
                </a>

                <a
                  href="#novis"
                  className="transition hover:text-white"
                >
                  Novis
                </a>
              </div>
            </div>

            <div>
              <p className="text-[11px] font-bold uppercase tracking-[0.15em] text-white">
                Support
              </p>

              <div className="mt-4 flex flex-col gap-3 text-[11px]">
                <a
                  href="#faq"
                  className="transition hover:text-white"
                >
                  FAQ
                </a>

                <a
                  href="#feedback"
                  className="transition hover:text-white"
                >
                  Feedback
                </a>

              <a href="#feedback">
                  Contact
                </a>
              </div>
            </div>

            <div>
              <p className="text-[11px] font-bold uppercase tracking-[0.15em] text-white">
                Account
              </p>

              <div className="mt-4 flex flex-col gap-3 text-[11px]">
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

          <div className="flex flex-col gap-2 border-t border-white/10 py-5 text-[9px] text-slate-600 sm:flex-row sm:items-center sm:justify-between">
            <p>
              © {new Date().getFullYear()} PrepNovis. All rights
              reserved.
            </p>

            <p>
              Built for people who want to practice before the real
              interview.
            </p>
          </div>
        </div>
      </footer>
    </main>
  );
}