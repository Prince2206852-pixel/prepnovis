"use client";

import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

import { AnalyticsRecentSession } from "@/types/analytics";

interface PerformanceChartProps {
  sessions: AnalyticsRecentSession[];
}

export default function PerformanceChart({
  sessions,
}: PerformanceChartProps) {
  const performanceData = [...sessions]
    .reverse()
    .map((session, index) => ({
      session: `S${index + 1}`,
      score: session.averageScore ?? 0,
    }));

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6">
      <div className="mb-6">
        <h3 className="text-base font-semibold text-slate-900">
          Recent Session Performance
        </h3>

        <p className="mt-1 text-sm text-slate-500">
          Your average score across your latest practice sessions
        </p>
      </div>

      {performanceData.length === 0 ? (
        <div className="flex h-72 items-center justify-center rounded-xl bg-slate-50 px-6 text-center">
          <div>
            <p className="text-sm font-semibold text-slate-700">
              No performance data yet
            </p>

            <p className="mt-1 text-sm text-slate-500">
              Complete a practice session to start seeing your progress.
            </p>
          </div>
        </div>
      ) : (
        <div className="h-72 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              data={performanceData}
              margin={{
                top: 5,
                right: 10,
                left: -20,
                bottom: 0,
              }}
            >
              <CartesianGrid
                strokeDasharray="3 3"
                vertical={false}
                stroke="#e2e8f0"
              />

              <XAxis
                dataKey="session"
                axisLine={false}
                tickLine={false}
                tick={{ fill: "#64748b", fontSize: 12 }}
              />

              <YAxis
                domain={[0, 10]}
                axisLine={false}
                tickLine={false}
                tick={{ fill: "#64748b", fontSize: 12 }}
              />

              <Tooltip
                formatter={(value) => [
                  `${Number(value).toFixed(1)}/10`,
                  "Score",
                ]}
                contentStyle={{
                  borderRadius: "12px",
                  border: "1px solid #e2e8f0",
                  boxShadow: "0 4px 12px rgba(15, 23, 42, 0.08)",
                }}
              />

              <Line
                type="monotone"
                dataKey="score"
                stroke="#6366f1"
                strokeWidth={3}
                dot={{
                  r: 4,
                  fill: "#6366f1",
                  strokeWidth: 0,
                }}
                activeDot={{
                  r: 6,
                }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  );
}