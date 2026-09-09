import { getApiUrl } from "@/lib/api";
import type {
  ApiErrorResponse,
  LoginRequest,
  LoginResponse,
  RegisterUserRequest,
  RegisterUserResponse,
} from "@/types/auth";

export class ApiError extends Error {
  status: number;
  validationErrors?: Record<string, string>;

  constructor(
    message: string,
    status: number,
    validationErrors?: Record<string, string>,
  ) {
    super(message);

    this.name = "ApiError";
    this.status = status;
    this.validationErrors = validationErrors;
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (response.ok) {
    const contentType = response.headers.get("content-type");

    if (contentType?.includes("application/json")) {
      return response.json() as Promise<T>;
    }

    return (await response.text()) as T;
  }

  let errorResponse: ApiErrorResponse | null = null;

  try {
    errorResponse = (await response.json()) as ApiErrorResponse;
  } catch {
    // The backend returned a response that was not JSON.
  }

  throw new ApiError(
    errorResponse?.message ?? "Something went wrong. Please try again.",
    response.status,
    errorResponse?.validationErrors,
  );
}

export async function loginUser(
  request: LoginRequest,
): Promise<LoginResponse> {
  const response = await fetch(getApiUrl("/auth/login"), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });

  return handleResponse<LoginResponse>(response);
}

export async function registerUser(
  request: RegisterUserRequest,
): Promise<RegisterUserResponse> {
  const response = await fetch(getApiUrl("/auth/register"), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });

  return handleResponse<RegisterUserResponse>(response);
}

export async function verifyEmail(
  token: string,
): Promise<string> {
  const response = await fetch(
    getApiUrl("/auth/verify-email"),
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        token,
      }),
    },
  );

  return handleResponse<string>(response);
}

export async function resendVerificationEmail(
  email: string,
): Promise<string> {
  const response = await fetch(
    getApiUrl("/auth/resend-verification"),
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email,
      }),
    },
  );

  return handleResponse<string>(response);
}

export async function forgotPassword(
  email: string,
): Promise<string> {
  const response = await fetch(
    getApiUrl("/auth/forgot-password"),
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email,
      }),
    },
  );

  return handleResponse<string>(response);
}

export async function resetPassword(
  token: string,
  password: string,
): Promise<string> {
  const response = await fetch(
    getApiUrl("/auth/reset-password"),
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        token,
        password,
      }),
    },
  );

  return handleResponse<string>(response);
}