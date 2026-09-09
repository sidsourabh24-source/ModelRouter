"use client";

import React, { useState } from "react";
import { ListTree, Search, Eye, CheckCircle2, Zap, Shield } from "lucide-react";

interface RequestLog {
  id: string;
  requestId: string;
  mode: string;
  taskCategory: string;
  complexityScore: number;
  selectedModel: string;
  provider: string;
  latencyMs: number;
  inputTokens: number;
  outputTokens: number;
  estimatedCostUsd: number;
  cacheHit: boolean;
  status: "SUCCESS" | "FAILED";
  reason: string;
  evaluatedCandidates: { name: string; score: number }[];
}

const mockRequestLogs: RequestLog[] = [
  {
    id: "req-9801",
    requestId: "req-f9a8-1234",
    mode: "BALANCED",
    taskCategory: "CODE",
    complexityScore: 0.78,
    selectedModel: "claude-3-5-sonnet",
    provider: "Anthropic",
    latencyMs: 142,
    inputTokens: 320,
    outputTokens: 450,
    estimatedCostUsd: 0.0077,
    cacheHit: false,
    status: "SUCCESS",
    reason: "Selected model 'claude-3-5-sonnet' for mode 'BALANCED' and task 'CODE' (Complexity: 0.78) with score 0.942.",
    evaluatedCandidates: [
      { name: "claude-3-5-sonnet", score: 0.942 },
      { name: "gpt-4o", score: 0.915 },
      { name: "mock-cheap-v1", score: 0.520 },
    ],
  },
  {
    id: "req-9802",
    requestId: "req-e4b2-5678",
    mode: "CHEAP",
    taskCategory: "CHAT",
    complexityScore: 0.15,
    selectedModel: "mock-cheap-v1",
    provider: "Mock Provider",
    latencyMs: 38,
    inputTokens: 45,
    outputTokens: 80,
    estimatedCostUsd: 0.0001,
    cacheHit: false,
    status: "SUCCESS",
    reason: "Selected model 'mock-cheap-v1' for mode 'CHEAP' and task 'CHAT' (Complexity: 0.15) with score 0.890.",
    evaluatedCandidates: [
      { name: "mock-cheap-v1", score: 0.890 },
      { name: "gemini-1.5-flash", score: 0.840 },
      { name: "gpt-4o", score: 0.310 },
    ],
  },
  {
    id: "req-9803",
    requestId: "req-a1c9-9012",
    mode: "FAST",
    taskCategory: "WRITING",
    complexityScore: 0.32,
    selectedModel: "gemini-1.5-flash",
    provider: "Google Gemini",
    latencyMs: 4,
    inputTokens: 120,
    outputTokens: 210,
    estimatedCostUsd: 0.0003,
    cacheHit: true,
    status: "SUCCESS",
    reason: "Instant Redis cache hit for exact SHA-256 prompt match.",
    evaluatedCandidates: [{ name: "gemini-1.5-flash", score: 1.0 }],
  },
];

export default function RequestsPage() {
  const [logs] = useState<RequestLog[]>(mockRequestLogs);
  const [selectedLog, setSelectedLog] = useState<RequestLog | null>(null);

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Request Explorer & Decision Tracing</h2>
          <p className="text-slate-400 text-sm mt-1">
            Audit raw inference requests, latency logs, cost telemetry, and step-by-step model selection trace breakdown.
          </p>
        </div>
      </div>

      <div className="glass-card rounded-2xl overflow-hidden">
        <div className="p-6 border-b border-border flex items-center justify-between">
          <div className="flex items-center gap-2">
            <ListTree className="w-5 h-5 text-indigo-400" />
            <h3 className="font-semibold text-white">Recent Gateway Requests</h3>
          </div>
          <div className="relative w-64">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
            <input
              type="text"
              placeholder="Search request ID..."
              className="w-full bg-surface border border-border rounded-xl pl-9 pr-4 py-2 text-xs text-white focus:outline-none focus:border-indigo-500"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface/50 text-slate-400 uppercase text-[11px] font-semibold tracking-wider border-b border-border">
              <tr>
                <th className="px-6 py-4">Request ID & Status</th>
                <th className="px-6 py-4">Mode & Task</th>
                <th className="px-6 py-4">Selected Model</th>
                <th className="px-6 py-4">Tokens (In / Out)</th>
                <th className="px-6 py-4">Latency</th>
                <th className="px-6 py-4">Est. Cost</th>
                <th className="px-6 py-4 text-right">Inspect Trace</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border/50 text-slate-300">
              {logs.map((log) => (
                <tr key={log.id} className="hover:bg-slate-800/30 transition-colors">
                  <td className="px-6 py-4">
                    <div className="font-mono text-xs text-white font-semibold flex items-center gap-2">
                      {log.requestId}
                      {log.cacheHit && (
                        <span className="px-1.5 py-0.5 rounded text-[10px] bg-purple-500/20 text-purple-300 border border-purple-500/30 flex items-center gap-0.5">
                          <Zap className="w-2.5 h-2.5" /> Cache
                        </span>
                      )}
                    </div>
                    <div className="text-[11px] text-emerald-400 flex items-center gap-1 mt-0.5">
                      <CheckCircle2 className="w-3 h-3" /> {log.status}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="px-2 py-0.5 rounded-md bg-indigo-500/10 text-indigo-300 text-xs font-semibold border border-indigo-500/20 mr-2">
                      {log.mode}
                    </span>
                    <span className="px-2 py-0.5 rounded-md bg-slate-800 text-slate-300 text-xs font-medium border border-slate-700">
                      {log.taskCategory}
                    </span>
                  </td>
                  <td className="px-6 py-4 font-semibold text-white">{log.selectedModel}</td>
                  <td className="px-6 py-4 font-mono text-xs text-slate-300">
                    {log.inputTokens} / {log.outputTokens}
                  </td>
                  <td className="px-6 py-4 text-xs font-mono text-blue-400">{log.latencyMs} ms</td>
                  <td className="px-6 py-4 text-xs font-mono text-emerald-400">${log.estimatedCostUsd}</td>
                  <td className="px-6 py-4 text-right">
                    <button
                      onClick={() => setSelectedLog(log)}
                      className="px-3 py-1.5 rounded-lg bg-indigo-600/20 hover:bg-indigo-600/30 text-indigo-400 text-xs font-medium border border-indigo-500/30 flex items-center gap-1 ml-auto"
                    >
                      <Eye className="w-3.5 h-3.5" /> Inspect
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Decision Trace Inspector Modal */}
      {selectedLog && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="glass-card max-w-xl w-full p-6 rounded-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-border pb-4">
              <div className="flex items-center gap-2">
                <Shield className="w-5 h-5 text-indigo-400" />
                <h3 className="font-bold text-white">Decision Trace Inspector</h3>
              </div>
              <button
                onClick={() => setSelectedLog(null)}
                className="text-slate-400 hover:text-white text-xs font-bold px-2 py-1 bg-surface rounded-lg"
              >
                Close ✕
              </button>
            </div>

            <div className="space-y-4 text-xs">
              <div className="p-3 rounded-xl bg-surface/80 border border-border font-mono text-slate-300">
                <span className="text-slate-500 font-semibold block mb-1">DECISION REASON LOG:</span>
                {selectedLog.reason}
              </div>

              <div>
                <h4 className="font-semibold text-slate-300 mb-2">Evaluated Candidate Model Scores:</h4>
                <div className="space-y-2">
                  {selectedLog.evaluatedCandidates.map((c) => (
                    <div key={c.name} className="flex items-center justify-between p-2.5 rounded-xl bg-card border border-border">
                      <span className="font-semibold text-white">{c.name}</span>
                      <span className="font-mono text-indigo-400 font-bold">Score: {(c.score * 100).toFixed(1)} / 100</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
