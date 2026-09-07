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

const performanceData = [
  { session: "S1", score: 5.8 },
  { session: "S2", score: 6.4 },
  { session: "S3", score: 6.1 },
  { session: "S4", score: 7.2 },
  { session: "S5", score: 7.6 },
  { session: "S6", score: 8.1 },
  { session: "S7", score: 8.6 },
];

export default function PerformanceChart() {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-6">
        <h3 className="text-base font-semibold text-slate-900">
          Performance Over Time
        </h3>

        <p className="mt-1 text-sm text-slate-500">
          Your average score across recent practice sessions
        </p>
      </div>

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
              formatter={(value) => [`${value}/10`, "Score"]}
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
    </div>
  );
}