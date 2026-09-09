import { clearAuthSession, getAccessToken } from "@/lib/auth";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api/v1";

type ApiRequestOptions = RequestInit & {
  authenticated?: boolean;
};

/**
 * Builds a complete PrepNovis backend API URL.
 *
 * Existing services such as authService.ts use this function.
 */
export function getApiUrl(endpoint: string): string {
  const normalizedEndpoint = endpoint.startsWith("/")
    ? endpoint
    : `/${endpoint}`;

  return `${API_BASE_URL}${normalizedEndpoint}`;
}

/**
 * Central helper for calling PrepNovis APIs.
 * Protected requests automatically receive the logged-in user's JWT.
 */
export async function apiRequest<T>(
  endpoint: string,
  options: ApiRequestOptions = {}
): Promise<T> {
  const {
    authenticated = true,
    headers,
    ...requestOptions
  } = options;

  const requestHeaders = new Headers(headers);

  if (requestOptions.body && !requestHeaders.has("Content-Type")) {
    requestHeaders.set("Content-Type", "application/json");
  }

  if (authenticated) {
    const token = getAccessToken();

    if (!token) {
      clearAuthSession();

      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }

      throw new Error("Authentication is required.");
    }

    requestHeaders.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(getApiUrl(endpoint), {
    ...requestOptions,
    headers: requestHeaders,
  });

  if (response.status === 401) {
    clearAuthSession();

    if (typeof window !== "undefined") {
      window.location.href = "/login";
    }

    throw new Error("Your session is invalid or has expired.");
  }

  if (!response.ok) {
    let message = "Something went wrong. Please try again.";

    try {
      const errorBody = await response.json();

      if (
        errorBody &&
        typeof errorBody.message === "string" &&
        errorBody.message.trim()
      ) {
        message = errorBody.message;
      }
    } catch {
      // Keep the default message if the backend response is not JSON.
    }

    throw new Error(message);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const contentType = response.headers.get("content-type");

  if (!contentType?.includes("application/json")) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}