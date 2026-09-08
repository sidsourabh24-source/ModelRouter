"use client";

import React, { useState } from "react";
import { Cpu, CheckCircle2, AlertTriangle, XCircle, ToggleLeft, ToggleRight, Edit3 } from "lucide-react";

interface ModelItem {
  id: string;
  name: string;
  provider: string;
  capabilities: string[];
  contextLimit: number;
  inputPricePer1k: number;
  outputPricePer1k: number;
  qualityScore: number;
  latencyScore: number;
  status: "ACTIVE" | "INACTIVE";
  health: "HEALTHY" | "DEGRADED" | "UNHEALTHY";
}

const initialModels: ModelItem[] = [
  {
    id: "model-gpt-4o",
    name: "gpt-4o",
    provider: "OpenAI",
    capabilities: ["chat", "code", "reasoning", "vision"],
    contextLimit: 128000,
    inputPricePer1k: 0.0025,
    outputPricePer1k: 0.0100,
    qualityScore: 0.96,
    latencyScore: 0.85,
    status: "ACTIVE",
    health: "HEALTHY",
  },
  {
    id: "model-claude-3-5-sonnet",
    name: "claude-3-5-sonnet",
    provider: "Anthropic",
    capabilities: ["chat", "code", "writing", "reasoning"],
    contextLimit: 200000,
    inputPricePer1k: 0.0030,
    outputPricePer1k: 0.0150,
    qualityScore: 0.98,
    latencyScore: 0.80,
    status: "ACTIVE",
    health: "HEALTHY",
  },
  {
    id: "model-mock-cheap",
    name: "mock-cheap-v1",
    provider: "Mock Provider",
    capabilities: ["chat", "code"],
    contextLimit: 32000,
    inputPricePer1k: 0.0001,
    outputPricePer1k: 0.0002,
    qualityScore: 0.65,
    latencyScore: 0.95,
    status: "ACTIVE",
    health: "HEALTHY",
  },
  {
    id: "model-gemini-1-5-flash",
    name: "gemini-1.5-flash",
    provider: "Google Gemini",
    capabilities: ["chat", "code", "fast"],
    contextLimit: 1000000,
    inputPricePer1k: 0.00035,
    outputPricePer1k: 0.00105,
    qualityScore: 0.88,
    latencyScore: 0.92,
    status: "ACTIVE",
    health: "HEALTHY",
  },
  {
    id: "model-deepseek-coder",
    name: "deepseek-coder-v2",
    provider: "DeepSeek",
    capabilities: ["code", "math"],
    contextLimit: 64000,
    inputPricePer1k: 0.00014,
    outputPricePer1k: 0.00028,
    qualityScore: 0.92,
    latencyScore: 0.88,
    status: "ACTIVE",
    health: "DEGRADED",
  },
];

export default function ModelsPage() {
  const [models, setModels] = useState<ModelItem[]>(initialModels);
  const [editingModel, setEditingModel] = useState<ModelItem | null>(null);

  const toggleStatus = (id: string) => {
    setModels((prev) =>
      prev.map((m) => (m.id === id ? { ...m, status: m.status === "ACTIVE" ? "INACTIVE" : "ACTIVE" } : m))
    );
  };

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Models & Provider Management</h2>
          <p className="text-slate-400 text-sm mt-1">
            Manage provider connections, model context boundaries, token pricing, and live health status.
          </p>
        </div>
      </div>

      <div className="glass-card rounded-2xl overflow-hidden">
        <div className="p-6 border-b border-border flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Cpu className="w-5 h-5 text-indigo-400" />
            <h3 className="font-semibold text-white">Registered Candidate Models ({models.length})</h3>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-surface/50 text-slate-400 uppercase text-[11px] font-semibold tracking-wider border-b border-border">
              <tr>
                <th className="px-6 py-4">Model & Provider</th>
                <th className="px-6 py-4">Capabilities</th>
                <th className="px-6 py-4">Context Limit</th>
                <th className="px-6 py-4">Input / Output Price (1k)</th>
                <th className="px-6 py-4">Quality / Latency Score</th>
                <th className="px-6 py-4">Circuit Health</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border/50 text-slate-300">
              {models.map((m) => (
                <tr key={m.id} className="hover:bg-slate-800/30 transition-colors">
                  <td className="px-6 py-4">
                    <div className="font-semibold text-white">{m.name}</div>
                    <div className="text-xs text-indigo-400">{m.provider}</div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex flex-wrap gap-1">
                      {m.capabilities.map((cap) => (
                        <span key={cap} className="px-2 py-0.5 rounded-md bg-slate-800 text-slate-300 text-[11px] border border-slate-700">
                          {cap}
                        </span>
                      ))}
                    </div>
                  </td>
                  <td className="px-6 py-4 text-slate-200 font-mono text-xs">
                    {(m.contextLimit / 1000).toFixed(0)}k tokens
                  </td>
                  <td className="px-6 py-4 font-mono text-xs text-emerald-400">
                    ${m.inputPricePer1k} / ${m.outputPricePer1k}
                  </td>
                  <td className="px-6 py-4 text-xs">
                    <div className="flex items-center gap-2">
                      <span className="text-indigo-400 font-medium">Q: {(m.qualityScore * 100).toFixed(0)}%</span>
                      <span className="text-blue-400 font-medium">L: {(m.latencyScore * 100).toFixed(0)}%</span>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    {m.health === "HEALTHY" && (
                      <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                        <CheckCircle2 className="w-3.5 h-3.5" /> Healthy
                      </span>
                    )}
                    {m.health === "DEGRADED" && (
                      <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-amber-500/10 text-amber-400 border border-amber-500/20">
                        <AlertTriangle className="w-3.5 h-3.5" /> Degraded
                      </span>
                    )}
                    {m.health === "UNHEALTHY" && (
                      <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-rose-500/10 text-rose-400 border border-rose-500/20">
                        <XCircle className="w-3.5 h-3.5" /> Unhealthy
                      </span>
                    )}
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex items-center justify-end gap-3">
                      <button
                        onClick={() => setEditingModel(m)}
                        className="text-slate-400 hover:text-white transition-colors"
                        title="Edit Pricing"
                      >
                        <Edit3 className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => toggleStatus(m.id)}
                        className="text-indigo-400 hover:text-indigo-300 transition-colors"
                      >
                        {m.status === "ACTIVE" ? (
                          <ToggleRight className="w-6 h-6 text-emerald-400" />
                        ) : (
                          <ToggleLeft className="w-6 h-6 text-slate-600" />
                        )}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Price Edit Modal */}
      {editingModel && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="glass-card max-w-md w-full p-6 rounded-2xl space-y-4">
            <h3 className="text-lg font-bold text-white">Edit Pricing for {editingModel.name}</h3>
            <div className="space-y-3">
              <div>
                <label className="text-xs text-slate-400 block mb-1">Input Price per 1k Tokens ($)</label>
                <input
                  type="number"
                  step="0.0001"
                  defaultValue={editingModel.inputPricePer1k}
                  className="w-full bg-surface border border-border rounded-xl p-2.5 text-sm text-white focus:outline-none focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="text-xs text-slate-400 block mb-1">Output Price per 1k Tokens ($)</label>
                <input
                  type="number"
                  step="0.0001"
                  defaultValue={editingModel.outputPricePer1k}
                  className="w-full bg-surface border border-border rounded-xl p-2.5 text-sm text-white focus:outline-none focus:border-indigo-500"
                />
              </div>
            </div>
            <div className="flex justify-end gap-3 pt-2">
              <button
                onClick={() => setEditingModel(null)}
                className="px-4 py-2 rounded-xl text-xs font-medium text-slate-400 hover:text-white"
              >
                Cancel
              </button>
              <button
                onClick={() => setEditingModel(null)}
                className="px-4 py-2 rounded-xl text-xs font-medium bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-500/20"
              >
                Save Changes
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
