export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  id: string;
  fullName: string;
  email: string;
  role: string;
  accessToken: string;
};

export type RegisterUserRequest = {
  fullName: string;
  email: string;
  password: string;
};

export type RegisterUserResponse = {
  id: string;
  fullName: string;
  email: string;
  role: string;
  message: string;
};

export type ApiErrorResponse = {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  validationErrors?: Record<string, string>;
  path?: string;
};