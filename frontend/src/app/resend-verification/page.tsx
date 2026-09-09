"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import {
  ArrowLeft,
  CheckCircle2,
  Mail,
  Send,
} from "lucide-react";

import PrepNovisLogo from "@/components/PrepNovisLogo";
import {
  ApiError,
  resendVerificationEmail,
} from "@/services/authService";

export default function ResendVerificationPage() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setError("");
    setSuccess(false);
    setIsSubmitting(true);

    try {
      await resendVerificationEmail(email.trim());

      setSuccess(true);
    } catch (error) {
      if (error instanceof ApiError) {
        setError(error.message);
      } else {
        setError(
          "Unable to resend the verification email. Please try again.",
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="flex min-h-screen bg-slate-50">
      <section className="hidden w-1/2 flex-col justify-between bg-slate-950 p-12 text-white lg:flex">
        <div>
          <PrepNovisLogo size={48} />
        </div>

        <div className="max-w-lg">
          <p className="mb-4 text-sm font-semibold uppercase tracking-[0.2em] text-indigo-400">
            Almost there
          </p>

          <h1 className="text-5xl font-bold leading-tight tracking-tight">
            Verify your account.
            <br />
            Start practicing.
            <br />
            Keep improving.
          </h1>

          <p className="mt-6 max-w-md text-base leading-7 text-slate-400">
            We&apos;ll send you a fresh verification link
            so you can activate your PrepNovis account and
            continue your interview preparation.
          </p>
        </div>

        <p className="text-sm text-slate-500">
          PrepNovis • Your interview preparation workspace
        </p>
      </section>

      <section className="flex w-full items-center justify-center px-6 py-12 lg:w-1/2">
        <div className="w-full max-w-md">
          <div className="mb-8 lg:hidden">
            <PrepNovisLogo size={46} />
          </div>

          <div className="mb-8">
            <p className="mb-2 text-sm font-semibold text-indigo-600">
              Email verification
            </p>

            <h2 className="text-3xl font-bold tracking-tight text-slate-900">
              Resend verification email
            </h2>

            <p className="mt-2 text-sm leading-6 text-slate-500">
              Enter the email address you used to create
              your PrepNovis account.
            </p>
          </div>

          {error && (
            <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          {success ? (
            <div>
              <div className="rounded-2xl border border-emerald-200 bg-emerald-50 p-6 text-center">
                <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-full bg-white">
                  <CheckCircle2
                    size={28}
                    className="text-emerald-600"
                  />
                </div>

                <h3 className="mt-4 text-lg font-bold text-slate-900">
                  Check your email
                </h3>

                <p className="mt-2 text-sm leading-6 text-slate-600">
                  If an unverified PrepNovis account exists
                  for{" "}
                  <span className="font-semibold text-slate-800">
                    {email.trim()}
                  </span>
                  , a new verification link has been sent.
                </p>
              </div>

              <button
                type="button"
                onClick={() => {
                  setSuccess(false);
                  setError("");
                }}
                className="mt-4 flex w-full items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
              >
                <Send size={17} />
                Send again
              </button>

              <Link
                href="/login"
                className="mt-3 flex w-full items-center justify-center gap-2 text-sm font-semibold text-indigo-600 hover:text-indigo-700"
              >
                <ArrowLeft size={16} />
                Back to sign in
              </Link>
            </div>
          ) : (
            <form
              onSubmit={handleSubmit}
              className="space-y-5"
            >
              <div>
                <label
                  htmlFor="email"
                  className="mb-2 block text-sm font-medium text-slate-700"
                >
                  Email address
                </label>

                <div className="relative">
                  <Mail
                    size={18}
                    className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400"
                  />

                  <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(event) =>
                      setEmail(event.target.value)
                    }
                    placeholder="you@example.com"
                    autoComplete="email"
                    required
                    className="w-full rounded-xl border border-slate-200 bg-white py-3 pl-11 pr-4 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={isSubmitting}
                className="flex w-full items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
              >
                <Send size={17} />

                {isSubmitting
                  ? "Sending..."
                  : "Send verification email"}
              </button>

              <Link
                href="/login"
                className="flex w-full items-center justify-center gap-2 text-sm font-semibold text-slate-500 transition hover:text-slate-700"
              >
                <ArrowLeft size={16} />
                Back to sign in
              </Link>
            </form>
          )}
        </div>
      </section>
    </main>
  );
}