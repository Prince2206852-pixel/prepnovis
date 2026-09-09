"use client";

import { FormEvent, useEffect, useRef, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Eye, EyeOff, LockKeyhole, Mail } from "lucide-react";

import PrepNovisLogo from "@/components/PrepNovisLogo";
import {
  isAuthenticated,
  saveAuthSession,
} from "@/lib/auth";
import { apiRequest } from "@/lib/api";
import { ApiError, loginUser } from "@/services/authService";
import type { LoginResponse } from "@/types/auth";

type GoogleCredentialResponse = {
  credential: string;
};

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: {
            client_id: string;
            callback: (response: GoogleCredentialResponse) => void;
          }) => void;

          renderButton: (
            parent: HTMLElement,
            options: {
              theme: string;
              size: string;
              type: string;
              shape: string;
              text: string;
              width: number;
            },
          ) => void;
        };
      };
    };
  }
}

export default function LoginPage() {
  const router = useRouter();

  useEffect(() => {
  if (isAuthenticated()) {
    router.replace("/dashboard");
  }
}, [router]);

  const googleButtonRef = useRef<HTMLDivElement>(null);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isGoogleSubmitting, setIsGoogleSubmitting] = useState(false);

  useEffect(() => {
    const googleClientId =
      process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;

    if (!googleClientId) {
      console.error(
        "NEXT_PUBLIC_GOOGLE_CLIENT_ID is not configured.",
      );
      return;
    }

    function initializeGoogleSignIn() {
      if (
        !window.google ||
        !googleButtonRef.current
      ) {
        return;
      }

      window.google.accounts.id.initialize({
        client_id: googleClientId!,
        callback: async (
          response: GoogleCredentialResponse,
        ) => {
          setError("");
          setIsGoogleSubmitting(true);

          try {
            const loginResponse =
              await apiRequest<LoginResponse>(
                "/auth/google",
                {
                  method: "POST",
                  authenticated: false,
                  body: JSON.stringify({
                    credential: response.credential,
                  }),
                },
              );

            saveAuthSession(loginResponse);

            router.push("/dashboard");
          } catch (error) {
            if (error instanceof Error) {
              setError(error.message);
            } else {
              setError(
                "Unable to sign in with Google. Please try again.",
              );
            }
          } finally {
            setIsGoogleSubmitting(false);
          }
        },
      });

      googleButtonRef.current.innerHTML = "";

      window.google.accounts.id.renderButton(
        googleButtonRef.current,
        {
          theme: "outline",
          size: "large",
          type: "standard",
          shape: "rectangular",
          text: "continue_with",
          width: 400,
        },
      );
    }

    if (window.google) {
      initializeGoogleSignIn();
      return;
    }

    const existingScript =
      document.querySelector<HTMLScriptElement>(
        'script[src="https://accounts.google.com/gsi/client"]',
      );

    if (existingScript) {
      existingScript.addEventListener(
        "load",
        initializeGoogleSignIn,
      );

      return () => {
        existingScript.removeEventListener(
          "load",
          initializeGoogleSignIn,
        );
      };
    }

    const script = document.createElement("script");

    script.src =
      "https://accounts.google.com/gsi/client";

    script.async = true;
    script.defer = true;

    script.addEventListener(
      "load",
      initializeGoogleSignIn,
    );

    document.head.appendChild(script);

    return () => {
      script.removeEventListener(
        "load",
        initializeGoogleSignIn,
      );
    };
  }, [router]);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setError("");
    setIsSubmitting(true);

    try {
      const response = await loginUser({
        email: email.trim(),
        password,
      });

      saveAuthSession(response);

      router.push("/dashboard");
    } catch (error) {
      if (error instanceof ApiError) {
        setError(error.message);
      } else {
        setError(
          "Unable to login. Please try again.",
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
            Prepare with purpose
          </p>

          <h1 className="text-5xl font-bold leading-tight tracking-tight">
            Practice better.
            <br />
            Answer better.
            <br />
            Interview better.
          </h1>

          <p className="mt-6 max-w-md text-base leading-7 text-slate-400">
            Build confidence through structured practice,
            AI evaluation, and actionable feedback from
            Novis.
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
              Welcome back
            </p>

            <h2 className="text-3xl font-bold tracking-tight text-slate-900">
              Sign in to PrepNovis
            </h2>

            <p className="mt-2 text-sm leading-6 text-slate-500">
              Continue your interview preparation and track
              your progress.
            </p>
          </div>

          {error && (
            <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          <div className="mb-6">
            <div
              ref={googleButtonRef}
              className="flex min-h-[44px] w-full justify-center"
            />

            {isGoogleSubmitting && (
              <p className="mt-2 text-center text-sm text-slate-500">
                Signing in with Google...
              </p>
            )}
          </div>

          <div className="mb-6 flex items-center gap-4">
            <div className="h-px flex-1 bg-slate-200" />

            <span className="text-xs font-medium uppercase tracking-wider text-slate-400">
              or continue with email
            </span>

            <div className="h-px flex-1 bg-slate-200" />
          </div>

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

            <div>
              <div className="mb-2 flex items-center justify-between">
                <label
                  htmlFor="password"
                  className="text-sm font-medium text-slate-700"
                >
                  Password
                </label>

                <Link
                  href="/forgot-password"
                  className="text-xs font-semibold text-indigo-600 transition hover:text-indigo-700"
                >
                  Forgot password?
                </Link>
              </div>

              <div className="relative">
                <LockKeyhole
                  size={18}
                  className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400"
                />

                <input
                  id="password"
                  type={
                    showPassword
                      ? "text"
                      : "password"
                  }
                  value={password}
                  onChange={(event) =>
                    setPassword(event.target.value)
                  }
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  required
                  className="w-full rounded-xl border border-slate-200 bg-white py-3 pl-11 pr-12 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100"
                />

                <button
                  type="button"
                  onClick={() =>
                    setShowPassword(
                      (current) => !current,
                    )
                  }
                  aria-label={
                    showPassword
                      ? "Hide password"
                      : "Show password"
                  }
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 transition hover:text-slate-700"
                >
                  {showPassword ? (
                    <EyeOff size={18} />
                  ) : (
                    <Eye size={18} />
                  )}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={
                isSubmitting ||
                isGoogleSubmitting
              }
              className="flex w-full items-center justify-center rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isSubmitting
                ? "Signing in..."
                : "Sign in"}
            </button>
          </form>

          <p className="mt-7 text-center text-sm text-slate-500">
            New to PrepNovis?{" "}
            <Link
              href="/register"
              className="font-semibold text-indigo-600 hover:text-indigo-700"
            >
              Create an account
            </Link>
          </p>
        </div>
      </section>
    </main>
  );
}