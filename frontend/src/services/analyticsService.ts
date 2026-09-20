import { apiRequest } from "@/lib/api";
import { AnalyticsDashboard } from "@/types/analytics";

export async function getAnalyticsDashboard(): Promise<AnalyticsDashboard> {
  return apiRequest<AnalyticsDashboard>("/analytics/dashboard");
}