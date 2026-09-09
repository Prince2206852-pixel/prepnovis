"use client";

import {
  FormEvent,
  useEffect,
  useRef,
  useState,
} from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  CheckCircle2,
  Eye,
  EyeOff,
  LockKeyhole,
  Mail,
  Send,
  UserRound,
} from "lucide-react";

import PrepNovisLogo from "@/components/PrepNovisLogo";
import {
  isAuthenticated,
  saveAuthSession,
} from "@/lib/auth";
import { apiRequest } from "@/lib/api";
import {
  ApiError,
  registerUser,
  resendVerificationEmail,
} from "@/services/authService";
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
            callback: (
              response: GoogleCredentialResponse
            ) => void;
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
            }
          ) => void;
        };
      };
    };
  }
}

export default function RegisterPage() {
  const router = useRouter();

  useEffect(() => {
    if (isAuthenticated()) {
      router.replace("/dashboard");
    }
  }, [router]);
  const googleButtonRef = useRef<HTMLDivElement>(null);

  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] =
    useState(false);

  const [error, setError] = useState("");
  const [validationErrors, setValidationErrors] =
    useState<Record<string, string>>({});
  const [isSubmitting, setIsSubmitting] =
    useState(false);

  const [
    isGoogleSubmitting,
    setIsGoogleSubmitting,
  ] = useState(false);

  const [
    registrationComplete,
    setRegistrationComplete,
  ] = useState(false);

  const [
    isResending,
    setIsResending,
  ] = useState(false);

  const [
    resendMessage,
    setResendMessage,
  ] = useState("");

  useEffect(() => {
    const googleClientId =
      process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;

    if (!googleClientId) {
      console.error(
        "NEXT_PUBLIC_GOOGLE_CLIENT_ID is not configured."
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
          response: GoogleCredentialResponse
        ) => {
          setError("");
          setValidationErrors({});
          setIsGoogleSubmitting(true);

          try {
            const loginResponse =
              await apiRequest<LoginResponse>(
                "/auth/google",
                {
                  method: "POST",
                  authenticated: false,
                  body: JSON.stringify({
                    credential:
                      response.credential,
                  }),
                }
              );

            saveAuthSession(loginResponse);

            router.push("/dashboard");
          } catch (error) {
            if (error instanceof Error) {
              setError(error.message);
            } else {
              setError(
                "Unable to continue with Google. Please try again."
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
        }
      );
    }

    if (window.google) {
      initializeGoogleSignIn();
      return;
    }

    const existingScript =
      document.querySelector<HTMLScriptElement>(
        'script[src="https://accounts.google.com/gsi/client"]'
      );

    if (existingScript) {
      existingScript.addEventListener(
        "load",
        initializeGoogleSignIn
      );

      return () => {
        existingScript.removeEventListener(
          "load",
          initializeGoogleSignIn
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
      initializeGoogleSignIn
    );

    document.head.appendChild(script);

    return () => {
      script.removeEventListener(
        "load",
        initializeGoogleSignIn
      );
    };
  }, [router]);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>
  ) {
    event.preventDefault();

    setError("");
    setValidationErrors({});
    setResendMessage("");
    setIsSubmitting(true);

    try {
      await registerUser({
        fullName: fullName.trim(),
        email: email.trim(),
        password,
      });

      setRegistrationComplete(true);
    } catch (error) {
      if (error instanceof ApiError) {
        setError(error.message);

        if (error.validationErrors) {
          setValidationErrors(
            error.validationErrors
          );
        }
      } else {
        setError(
          "Unable to create account. Please try again."
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleResendVerification() {
    setError("");
    setResendMessage("");
    setIsResending(true);

    try {
      await resendVerificationEmail(
        email.trim()
      );

      setResendMessage(
        "If your account is still unverified, a new verification email has been sent."
      );
    } catch (error) {
      if (error instanceof ApiError) {
        setError(error.message);
      } else {
        setError(
          "Unable to resend the verification email. Please try again."
        );
      }
    } finally {
      setIsResending(false);
    }
  }

  if (registrationComplete) {
    return (
      <main className="flex min-h-screen bg-slate-50">
        <section className="hidden w-1/2 flex-col justify-between bg-slate-950 p-12 text-white lg:flex">
          <PrepNovisLogo size={48} />

          <div className="max-w-lg">
            <p className="mb-4 text-sm font-semibold uppercase tracking-[0.2em] text-indigo-400">
              Account created
            </p>

            <h1 className="text-5xl font-bold leading-tight tracking-tight">
              Verify your email.
              <br />
              Start practicing.
              <br />
              Keep improving.
            </h1>

            <p className="mt-6 max-w-md text-base leading-7 text-slate-400">
              Your PrepNovis account has been created.
              Verify your email address to activate sign in
              and continue your interview preparation.
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

            {error && (
              <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}

            <div className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
              <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-emerald-50">
                <CheckCircle2
                  size={32}
                  className="text-emerald-600"
                />
              </div>

              <p className="mt-6 text-sm font-semibold text-emerald-600">
                Account created successfully
              </p>

              <h2 className="mt-2 text-2xl font-bold tracking-tight text-slate-900">
                Check your email
              </h2>

              <p className="mt-3 text-sm leading-6 text-slate-500">
                We sent a verification link to
              </p>

              <p className="mt-1 break-all text-sm font-semibold text-slate-900">
                {email.trim()}
              </p>

              <p className="mt-4 text-sm leading-6 text-slate-500">
                Open the email and click the verification
                link before signing in to PrepNovis.
              </p>

              {resendMessage && (
                <div className="mt-5 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm leading-6 text-emerald-700">
                  {resendMessage}
                </div>
              )}

              <button
                type="button"
                onClick={handleResendVerification}
                disabled={isResending}
                className="mt-7 flex w-full items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
              >
                <Send size={17} />

                {isResending
                  ? "Sending..."
                  : "Resend verification email"}
              </button>

              <Link
                href="/login"
                className="mt-3 flex w-full items-center justify-center rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700"
              >
                Go to sign in
              </Link>
            </div>
          </div>
        </section>
      </main>
    );
  }

  return (
    <main className="flex min-h-screen bg-slate-50">
      <section className="hidden w-1/2 flex-col justify-between bg-slate-950 p-12 text-white lg:flex">
        <PrepNovisLogo size={48} />

        <div className="max-w-lg">
          <p className="mb-4 text-sm font-semibold uppercase tracking-[0.2em] text-indigo-400">
            Start improving
          </p>

          <h1 className="text-5xl font-bold leading-tight tracking-tight">
            Build your question bank.
            <br />
            Practice consistently.
            <br />
            Improve with Novis.
          </h1>

          <p className="mt-6 max-w-md text-base leading-7 text-slate-400">
            Save interview questions, practice with
            PrepNovis Mock, and use Novis feedback to
            identify strengths and improve weak areas.
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
              Create your account
            </p>

            <h2 className="text-3xl font-bold tracking-tight text-slate-900">
              Join PrepNovis
            </h2>

            <p className="mt-2 text-sm leading-6 text-slate-500">
              Start building a structured interview
              preparation routine.
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
                Creating your account with Google...
              </p>
            )}
          </div>

          <div className="mb-6 flex items-center gap-4">
            <div className="h-px flex-1 bg-slate-200" />

            <span className="text-xs font-medium uppercase tracking-wider text-slate-400">
              or sign up with email
            </span>

            <div className="h-px flex-1 bg-slate-200" />
          </div>

          <form
            onSubmit={handleSubmit}
            className="space-y-5"
          >
            <div>
              <label
                htmlFor="fullName"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Full name
              </label>

              <div className="relative">
                <UserRound
                  size={18}
                  className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400"
                />

                <input
                  id="fullName"
                  type="text"
                  value={fullName}
                  onChange={(event) =>
                    setFullName(event.target.value)
                  }
                  placeholder="Enter your full name"
                  autoComplete="name"
                  required
                  className="w-full rounded-xl border border-slate-200 bg-white py-3 pl-11 pr-4 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100"
                />
              </div>

              {validationErrors.fullName && (
                <p className="mt-2 text-xs text-red-600">
                  {validationErrors.fullName}
                </p>
              )}
            </div>

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

              {validationErrors.email && (
                <p className="mt-2 text-xs text-red-600">
                  {validationErrors.email}
                </p>
              )}
            </div>

            <div>
              <label
                htmlFor="password"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Password
              </label>

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
                  placeholder="Create a strong password"
                  autoComplete="new-password"
                  required
                  minLength={8}
                  maxLength={20}
                  className="w-full rounded-xl border border-slate-200 bg-white py-3 pl-11 pr-12 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100"
                />

                <button
                  type="button"
                  onClick={() =>
                    setShowPassword(
                      (current) => !current
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

              <p className="mt-2 text-xs leading-5 text-slate-500">
                8–20 characters with at least one uppercase
                letter, one lowercase letter, one number,
                and one special character.
              </p>

              {validationErrors.password && (
                <p className="mt-2 text-xs text-red-600">
                  {validationErrors.password}
                </p>
              )}
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
                ? "Creating account..."
                : "Create account"}
            </button>
          </form>

          <p className="mt-7 text-center text-sm text-slate-500">
            Already have an account?{" "}
            <Link
              href="/login"
              className="font-semibold text-indigo-600 hover:text-indigo-700"
            >
              Sign in
            </Link>
          </p>
        </div>
      </section>
    </main>
  );
}