"use client";

import { Suspense, useEffect, useRef, useState } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import {
  AlertCircle,
  CheckCircle2,
  LoaderCircle,
  MailCheck,
} from "lucide-react";

import PrepNovisLogo from "@/components/PrepNovisLogo";
import {
  ApiError,
  verifyEmail,
} from "@/services/authService";

type VerificationStatus =
  | "verifying"
  | "success"
  | "error";

function VerifyEmailContent() {
  const searchParams = useSearchParams();

  const [status, setStatus] =
    useState<VerificationStatus>("verifying");

  const [message, setMessage] = useState(
    "We're verifying your email address.",
  );

  const verificationStarted = useRef(false);

  useEffect(() => {
    if (verificationStarted.current) {
      return;
    }

    verificationStarted.current = true;

    const token = searchParams.get("token");

    if (!token) {
      setStatus("error");
      setMessage(
        "This verification link is invalid. Please request a new verification email.",
      );
      return;
    }

    async function verify() {
      try {
        await verifyEmail(token!);

        setStatus("success");
        setMessage(
          "Your email has been verified successfully. You can now sign in to PrepNovis.",
        );
      } catch (error) {
        setStatus("error");

        if (error instanceof ApiError) {
          setMessage(error.message);
        } else {
          setMessage(
            "We couldn't verify your email. Please request a new verification link.",
          );
        }
      }
    }

    verify();
  }, [searchParams]);

  return (
    <main className="flex min-h-screen bg-slate-50">
      <section className="hidden w-1/2 flex-col justify-between bg-slate-950 p-12 text-white lg:flex">
        <div>
          <PrepNovisLogo size={48} />
        </div>

        <div className="max-w-lg">
          <p className="mb-4 text-sm font-semibold uppercase tracking-[0.2em] text-indigo-400">
            One last step
          </p>

          <h1 className="text-5xl font-bold leading-tight tracking-tight">
            Verify your email.
            <br />
            Start practicing.
            <br />
            Keep improving.
          </h1>

          <p className="mt-6 max-w-md text-base leading-7 text-slate-400">
            Secure your PrepNovis account and continue
            building your interview confidence through
            structured practice and feedback from Novis.
          </p>
        </div>

        <p className="text-sm text-slate-500">
          PrepNovis • Your interview preparation workspace
        </p>
      </section>

      <section className="flex w-full items-center justify-center px-6 py-12 lg:w-1/2">
        <div className="w-full max-w-md">
          <div className="mb-10 lg:hidden">
            <PrepNovisLogo size={46} />
          </div>

          <div className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm">
            {status === "verifying" && (
              <div className="text-center">
                <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-indigo-50">
                  <LoaderCircle
                    size={30}
                    className="animate-spin text-indigo-600"
                  />
                </div>

                <p className="mt-6 text-sm font-semibold text-indigo-600">
                  Email verification
                </p>

                <h2 className="mt-2 text-2xl font-bold tracking-tight text-slate-900">
                  Verifying your email...
                </h2>

                <p className="mt-3 text-sm leading-6 text-slate-500">
                  {message}
                </p>
              </div>
            )}

            {status === "success" && (
              <div className="text-center">
                <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-emerald-50">
                  <CheckCircle2
                    size={32}
                    className="text-emerald-600"
                  />
                </div>

                <p className="mt-6 text-sm font-semibold text-emerald-600">
                  Verification complete
                </p>

                <h2 className="mt-2 text-2xl font-bold tracking-tight text-slate-900">
                  Email verified
                </h2>

                <p className="mt-3 text-sm leading-6 text-slate-500">
                  {message}
                </p>

                <Link
                  href="/login"
                  className="mt-7 flex w-full items-center justify-center rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700"
                >
                  Sign in to PrepNovis
                </Link>
              </div>
            )}

            {status === "error" && (
              <div className="text-center">
                <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-red-50">
                  <AlertCircle
                    size={32}
                    className="text-red-600"
                  />
                </div>

                <p className="mt-6 text-sm font-semibold text-red-600">
                  Verification failed
                </p>

                <h2 className="mt-2 text-2xl font-bold tracking-tight text-slate-900">
                  We couldn't verify this link
                </h2>

                <p className="mt-3 text-sm leading-6 text-slate-500">
                  {message}
                </p>

                <div className="mt-7 space-y-3">
                  <Link
                    href="/resend-verification"
                    className="flex w-full items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700"
                  >
                    <MailCheck size={18} />
                    Resend verification email
                  </Link>

                  <Link
                    href="/login"
                    className="flex w-full items-center justify-center rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
                  >
                    Back to sign in
                  </Link>
                </div>
              </div>
            )}
          </div>

          <p className="mt-6 text-center text-xs leading-5 text-slate-400">
            You can close this page after your email has
            been verified.
          </p>
        </div>
      </section>
    </main>
  );
}

export default function VerifyEmailPage() {
  return (
    <Suspense
      fallback={
        <main className="flex min-h-screen items-center justify-center bg-slate-50">
          <LoaderCircle
            size={32}
            className="animate-spin text-indigo-600"
          />
        </main>
      }
    >
      <VerifyEmailContent />
    </Suspense>
  );
}