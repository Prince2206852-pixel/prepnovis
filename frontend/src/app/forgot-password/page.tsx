"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { ArrowLeft, CheckCircle2, Mail } from "lucide-react";

import PrepNovisLogo from "@/components/PrepNovisLogo";
import {
  ApiError,
  forgotPassword,
} from "@/services/authService";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [submittedEmail, setSubmittedEmail] =
    useState("");

  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] =
    useState("");

  const [isSubmitting, setIsSubmitting] =
    useState(false);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setError("");
    setSuccessMessage("");
    setIsSubmitting(true);

    const normalizedEmail =
      email.trim().toLowerCase();

    try {
      const response =
        await forgotPassword(normalizedEmail);

      setSubmittedEmail(normalizedEmail);
      setSuccessMessage(response);
    } catch (error) {
      if (error instanceof ApiError) {
        setError(error.message);
      } else {
        setError(
          "Unable to send the reset link. Please try again.",
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
            Secure account recovery
          </p>

          <h1 className="text-5xl font-bold leading-tight tracking-tight">
            Get back to
            <br />
            your preparation.
          </h1>

          <p className="mt-6 max-w-md text-base leading-7 text-slate-400">
            Reset your password securely and continue
            practicing, improving, and tracking your
            interview progress.
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

          {!successMessage ? (
            <>
              <div className="mb-8">
                <p className="mb-2 text-sm font-semibold text-indigo-600">
                  Account recovery
                </p>

                <h2 className="text-3xl font-bold tracking-tight text-slate-900">
                  Forgot your password?
                </h2>

                <p className="mt-2 text-sm leading-6 text-slate-500">
                  Enter the email address associated
                  with your PrepNovis account and
                  we&apos;ll send you a password reset
                  link.
                </p>
              </div>

              {error && (
                <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                  {error}
                </div>
              )}

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
                      disabled={isSubmitting}
                      className="w-full rounded-xl border border-slate-200 bg-white py-3 pl-11 pr-4 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:opacity-60"
                    />
                  </div>
                </div>

                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="flex w-full items-center justify-center rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {isSubmitting
                    ? "Sending reset link..."
                    : "Send reset link"}
                </button>
              </form>

              <div className="mt-7 text-center">
                <Link
                  href="/login"
                  className="inline-flex items-center gap-2 text-sm font-semibold text-indigo-600 transition hover:text-indigo-700"
                >
                  <ArrowLeft size={16} />
                  Back to sign in
                </Link>
              </div>
            </>
          ) : (
            <div>
              <div className="mb-6 flex h-14 w-14 items-center justify-center rounded-full bg-green-100">
                <CheckCircle2
                  size={28}
                  className="text-green-600"
                />
              </div>

              <p className="mb-2 text-sm font-semibold text-indigo-600">
                Check your email
              </p>

              <h2 className="text-3xl font-bold tracking-tight text-slate-900">
                Reset link requested
              </h2>

              <p className="mt-3 text-sm leading-6 text-slate-500">
                If an eligible PrepNovis account exists
                for{" "}
                <span className="font-medium text-slate-700">
                  {submittedEmail}
                </span>
                , a password reset link has been sent.
              </p>

              <p className="mt-3 text-sm leading-6 text-slate-500">
                The reset link expires in 30 minutes.
              </p>

              <button
                type="button"
                onClick={() => {
                  setSuccessMessage("");
                  setError("");
                }}
                className="mt-7 flex w-full items-center justify-center rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-100"
              >
                Send another link
              </button>

              <div className="mt-6 text-center">
                <Link
                  href="/login"
                  className="inline-flex items-center gap-2 text-sm font-semibold text-indigo-600 transition hover:text-indigo-700"
                >
                  <ArrowLeft size={16} />
                  Back to sign in
                </Link>
              </div>
            </div>
          )}
        </div>
      </section>
    </main>
  );
}