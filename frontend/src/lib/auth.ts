import type { LoginResponse } from "@/types/auth";

const ACCESS_TOKEN_KEY = "prepnovis_access_token";
const USER_KEY = "prepnovis_user";

export type AuthUser = {
  id: string;
  fullName: string;
  email: string;
  role: string;
};

function isBrowser(): boolean {
  return typeof window !== "undefined";
}

export function saveAuthSession(response: LoginResponse): void {
  if (!isBrowser()) {
    return;
  }

  const user: AuthUser = {
    id: response.id,
    fullName: response.fullName,
    email: response.email,
    role: response.role,
  };

  sessionStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken);
  sessionStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function getAccessToken(): string | null {
  if (!isBrowser()) {
    return null;
  }

  return sessionStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getAuthUser(): AuthUser | null {
  if (!isBrowser()) {
    return null;
  }

  const storedUser = sessionStorage.getItem(USER_KEY);

  if (!storedUser) {
    return null;
  }

  try {
    return JSON.parse(storedUser) as AuthUser;
  } catch {
    clearAuthSession();
    return null;
  }
}

export function isAuthenticated(): boolean {
  return Boolean(getAccessToken());
}

export function clearAuthSession(): void {
  if (!isBrowser()) {
    return;
  }

  sessionStorage.removeItem(ACCESS_TOKEN_KEY);
  sessionStorage.removeItem(USER_KEY);
}