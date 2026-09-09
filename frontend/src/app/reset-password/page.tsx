"use client";

import {
  FormEvent,
  Suspense,
  useState,
} from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import {
  CheckCircle2,
  Eye,
  EyeOff,
  LockKeyhole,
} from "lucide-react";

import PrepNovisLogo from "@/components/PrepNovisLogo";
import {
  ApiError,
  resetPassword,
} from "@/services/authService";

function ResetPasswordContent() {
  const searchParams = useSearchParams();

  const token = searchParams.get("token") ?? "";

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] =
    useState("");

  const [showPassword, setShowPassword] =
    useState(false);

  const [showConfirmPassword, setShowConfirmPassword] =
    useState(false);

  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] =
    useState(false);

  const [isSuccess, setIsSuccess] =
    useState(false);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setError("");

    if (!token) {
      setError(
        "This password reset link is invalid or incomplete.",
      );
      return;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setIsSubmitting(true);

    try {
      await resetPassword(
        token,
        password,
      );

      setIsSuccess(true);
    } catch (error) {
      if (error instanceof ApiError) {
        setError(error.message);
      } else {
        setError(
          "Unable to reset your password. Please try again.",
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
            Create a new
            <br />
            secure password.
          </h1>

          <p className="mt-6 max-w-md text-base leading-7 text-slate-400">
            Update your password and get back to
            practicing, improving, and preparing for
            your next interview.
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

          {!isSuccess ? (
            <>
              <div className="mb-8">
                <p className="mb-2 text-sm font-semibold text-indigo-600">
                  Account recovery
                </p>

                <h2 className="text-3xl font-bold tracking-tight text-slate-900">
                  Reset your password
                </h2>

                <p className="mt-2 text-sm leading-6 text-slate-500">
                  Choose a new password for your
                  PrepNovis account.
                </p>
              </div>

              {error && (
                <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                  {error}
                </div>
              )}

              {!token ? (
                <>
                  <div className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-4 text-sm leading-6 text-amber-800">
                    This reset link does not contain a
                    valid token. Request a new password
                    reset link to continue.
                  </div>

                  <Link
                    href="/forgot-password"
                    className="mt-6 flex w-full items-center justify-center rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700"
                  >
                    Request new reset link
                  </Link>
                </>
              ) : (
                <form
                  onSubmit={handleSubmit}
                  className="space-y-5"
                >
                  <div>
                    <label
                      htmlFor="password"
                      className="mb-2 block text-sm font-medium text-slate-700"
                    >
                      New password
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
                          setPassword(
                            event.target.value,
                          )
                        }
                        placeholder="Enter new password"
                        autoComplete="new-password"
                        required
                        minLength={8}
                        maxLength={20}
                        disabled={isSubmitting}
                        className="w-full rounded-xl border border-slate-200 bg-white py-3 pl-11 pr-12 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:opacity-60"
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

                  <div>
                    <label
                      htmlFor="confirmPassword"
                      className="mb-2 block text-sm font-medium text-slate-700"
                    >
                      Confirm new password
                    </label>

                    <div className="relative">
                      <LockKeyhole
                        size={18}
                        className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400"
                      />

                      <input
                        id="confirmPassword"
                        type={
                          showConfirmPassword
                            ? "text"
                            : "password"
                        }
                        value={confirmPassword}
                        onChange={(event) =>
                          setConfirmPassword(
                            event.target.value,
                          )
                        }
                        placeholder="Confirm new password"
                        autoComplete="new-password"
                        required
                        minLength={8}
                        maxLength={20}
                        disabled={isSubmitting}
                        className="w-full rounded-xl border border-slate-200 bg-white py-3 pl-11 pr-12 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:opacity-60"
                      />

                      <button
                        type="button"
                        onClick={() =>
                          setShowConfirmPassword(
                            (current) => !current,
                          )
                        }
                        aria-label={
                          showConfirmPassword
                            ? "Hide password"
                            : "Show password"
                        }
                        className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 transition hover:text-slate-700"
                      >
                        {showConfirmPassword ? (
                          <EyeOff size={18} />
                        ) : (
                          <Eye size={18} />
                        )}
                      </button>
                    </div>
                  </div>

                  <div className="rounded-xl bg-slate-100 px-4 py-3">
                    <p className="text-xs leading-5 text-slate-600">
                      Password must be 8–20 characters
                      and contain at least one uppercase
                      letter, one lowercase letter, one
                      number, and one special character.
                    </p>
                  </div>

                  <button
                    type="submit"
                    disabled={isSubmitting}
                    className="flex w-full items-center justify-center rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {isSubmitting
                      ? "Resetting password..."
                      : "Reset password"}
                  </button>
                </form>
              )}

              <p className="mt-7 text-center text-sm text-slate-500">
                Remember your password?{" "}
                <Link
                  href="/login"
                  className="font-semibold text-indigo-600 hover:text-indigo-700"
                >
                  Sign in
                </Link>
              </p>
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
                Password updated
              </p>

              <h2 className="text-3xl font-bold tracking-tight text-slate-900">
                Password reset successful
              </h2>

              <p className="mt-3 text-sm leading-6 text-slate-500">
                Your PrepNovis password has been
                changed successfully. You can now sign
                in using your new password.
              </p>

              <Link
                href="/login"
                className="mt-7 flex w-full items-center justify-center rounded-xl bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700"
              >
                Sign in with new password
              </Link>
            </div>
          )}
        </div>
      </section>
    </main>
  );
}

export default function ResetPasswordPage() {
  return (
    <Suspense
      fallback={
        <main className="flex min-h-screen items-center justify-center bg-slate-50">
          <p className="text-sm text-slate-500">
            Loading...
          </p>
        </main>
      }
    >
      <ResetPasswordContent />
    </Suspense>
  );
}