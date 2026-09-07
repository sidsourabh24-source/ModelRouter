"use client";

import React, { useEffect, useState } from "react";
import { Activity, DollarSign, Clock, Zap, TrendingUp, Sparkles } from "lucide-react";
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  BarChart,
  Bar,
} from "recharts";

interface OverviewData {
  totalRequests: number;
  totalCostUsd: number;
  avgLatencyMs: number;
  cacheHitRatePercent: number;
  estimatedSavingsPercent: number;
}

const mockChartData = [
  { time: "00:00", requests: 120, cost: 0.24, cheap: 60, quality: 40 },
  { time: "04:00", requests: 80, cost: 0.16, cheap: 50, quality: 20 },
  { time: "08:00", requests: 450, cost: 0.88, cheap: 200, quality: 180 },
  { time: "12:00", requests: 890, cost: 1.65, cheap: 400, quality: 350 },
  { time: "16:00", requests: 1120, cost: 2.10, cheap: 550, quality: 420 },
  { time: "20:00", requests: 670, cost: 1.25, cheap: 320, quality: 260 },
];

export default function OverviewPage() {
  const [data, setData] = useState<OverviewData>({
    totalRequests: 3430,
    totalCostUsd: 6.23,
    avgLatencyMs: 64.2,
    cacheHitRatePercent: 28.4,
    estimatedSavingsPercent: 34.8,
  });

  useEffect(() => {
    fetch("http://localhost:8080/api/v1/admin/analytics/overview")
      .then((res) => res.json())
      .then((json) => {
        if (json.totalRequests !== undefined) {
          setData(json);
        }
      })
      .catch(() => {
        // Fallback to initial mock state if backend is offline
      });
  }, []);

  return (
    <div className="space-y-8">
      {/* Header Banner */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">System Overview & Analytics</h2>
          <p className="text-slate-400 text-sm mt-1">
            Real-time AI gateway telemetry, cost optimization metrics, and cache performance.
          </p>
        </div>
        <div className="flex items-center gap-2 bg-indigo-500/10 border border-indigo-500/20 px-4 py-2 rounded-xl text-indigo-400 text-xs font-medium">
          <Sparkles className="w-4 h-4" />
          <span>Auto-Routing Engine Active</span>
        </div>
      </div>

      {/* KPI Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
        <div className="glass-card p-5 rounded-2xl">
          <div className="flex items-center justify-between text-slate-400 mb-3">
            <span className="text-xs font-semibold uppercase tracking-wider">Total Requests</span>
            <Activity className="w-4 h-4 text-indigo-400" />
          </div>
          <div className="text-2xl font-bold text-white">{data.totalRequests.toLocaleString()}</div>
          <div className="text-xs text-emerald-400 mt-2 flex items-center gap-1 font-medium">
            <TrendingUp className="w-3 h-3" />
            <span>+14.2% vs last week</span>
          </div>
        </div>

        <div className="glass-card p-5 rounded-2xl">
          <div className="flex items-center justify-between text-slate-400 mb-3">
            <span className="text-xs font-semibold uppercase tracking-wider">Total Cost</span>
            <DollarSign className="w-4 h-4 text-emerald-400" />
          </div>
          <div className="text-2xl font-bold text-white">${data.totalCostUsd}</div>
          <div className="text-xs text-slate-400 mt-2">Spent across 4 providers</div>
        </div>

        <div className="glass-card p-5 rounded-2xl">
          <div className="flex items-center justify-between text-slate-400 mb-3">
            <span className="text-xs font-semibold uppercase tracking-wider">Est. Savings</span>
            <Sparkles className="w-4 h-4 text-amber-400" />
          </div>
          <div className="text-2xl font-bold text-amber-400">{data.estimatedSavingsPercent}%</div>
          <div className="text-xs text-slate-400 mt-2">Saved vs GPT-4o only</div>
        </div>

        <div className="glass-card p-5 rounded-2xl">
          <div className="flex items-center justify-between text-slate-400 mb-3">
            <span className="text-xs font-semibold uppercase tracking-wider">Avg Latency</span>
            <Clock className="w-4 h-4 text-blue-400" />
          </div>
          <div className="text-2xl font-bold text-white">{data.avgLatencyMs} ms</div>
          <div className="text-xs text-emerald-400 mt-2">Optimal routing speed</div>
        </div>

        <div className="glass-card p-5 rounded-2xl">
          <div className="flex items-center justify-between text-slate-400 mb-3">
            <span className="text-xs font-semibold uppercase tracking-wider">Cache Hit Rate</span>
            <Zap className="w-4 h-4 text-purple-400" />
          </div>
          <div className="text-2xl font-bold text-purple-400">{data.cacheHitRatePercent}%</div>
          <div className="text-xs text-slate-400 mt-2">Redis exact-match hit</div>
        </div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="glass-card p-6 rounded-2xl lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="font-semibold text-white">24h Request Volume & Latency Trend</h3>
              <p className="text-xs text-slate-400">Total inference traffic processed through ModelRouter gateway</p>
            </div>
          </div>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={mockChartData}>
                <defs>
                  <linearGradient id="colorReq" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#6366f1" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#6366f1" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <XAxis dataKey="time" stroke="#475569" fontSize={12} />
                <YAxis stroke="#475569" fontSize={12} />
                <Tooltip
                  contentStyle={{ backgroundColor: "#181e2a", borderColor: "#262f40", borderRadius: "12px" }}
                />
                <Area type="monotone" dataKey="requests" stroke="#6366f1" strokeWidth={2} fillOpacity={1} fill="url(#colorReq)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="glass-card p-6 rounded-2xl space-y-4">
          <div>
            <h3 className="font-semibold text-white">Traffic by Routing Mode</h3>
            <p className="text-xs text-slate-400">Distribution between Cheap, Fast, and Quality requests</p>
          </div>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={mockChartData}>
                <XAxis dataKey="time" stroke="#475569" fontSize={12} />
                <YAxis stroke="#475569" fontSize={12} />
                <Tooltip
                  contentStyle={{ backgroundColor: "#181e2a", borderColor: "#262f40", borderRadius: "12px" }}
                />
                <Bar dataKey="cheap" fill="#10b981" name="Cheap Mode" radius={[4, 4, 0, 0]} />
                <Bar dataKey="quality" fill="#6366f1" name="Quality Mode" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}
