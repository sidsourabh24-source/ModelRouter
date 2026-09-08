"use client";

import React, { useState } from "react";
import { Sliders, Sparkles, RefreshCw, Zap, DollarSign, Clock, ShieldCheck } from "lucide-react";

interface ModePreset {
  mode: string;
  label: string;
  description: string;
  wQuality: number;
  wLatency: number;
  wCost: number;
  wReliability: number;
  wCapability: number;
}

const presets: ModePreset[] = [
  {
    mode: "CHEAP",
    label: "Cost Optimized (CHEAP)",
    description: "Prioritizes lowest token cost per request. Ideal for high-volume background tasks.",
    wQuality: 10,
    wLatency: 10,
    wCost: 70,
    wReliability: 5,
    wCapability: 5,
  },
  {
    mode: "FAST",
    label: "Latency Optimized (FAST)",
    description: "Prioritizes fastest time-to-first-token. Ideal for interactive real-time user chat.",
    wQuality: 10,
    wLatency: 70,
    wCost: 10,
    wReliability: 5,
    wCapability: 5,
  },
  {
    mode: "QUALITY",
    label: "Maximum Quality (QUALITY)",
    description: "Prioritizes highest benchmark reasoning models (GPT-4o, Claude 3.5 Sonnet).",
    wQuality: 70,
    wLatency: 10,
    wCost: 10,
    wReliability: 5,
    wCapability: 5,
  },
  {
    mode: "BALANCED",
    label: "Default Balanced (BALANCED)",
    description: "Harmonized trade-off between quality, speed, cost, and reliability.",
    wQuality: 35,
    wLatency: 20,
    wCost: 20,
    wReliability: 20,
    wCapability: 5,
  },
];

export default function PoliciesPage() {
  const [selectedMode, setSelectedMode] = useState<string>("BALANCED");
  const [weights, setWeights] = useState<ModePreset>(presets[3]);

  const selectPreset = (preset: ModePreset) => {
    setSelectedMode(preset.mode);
    setWeights(preset);
  };

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Routing Policies & Scoring Weights</h2>
          <p className="text-slate-400 text-sm mt-1">
            Configure multi-objective scoring weights used by ModelRouter to select candidate models per request.
          </p>
        </div>
      </div>

      {/* Preset Mode Selection */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {presets.map((p) => (
          <div
            key={p.mode}
            onClick={() => selectPreset(p)}
            className={`cursor-pointer glass-card p-5 rounded-2xl transition-all duration-200 ${
              selectedMode === p.mode ? "border-indigo-500 ring-1 ring-indigo-500/50 bg-indigo-500/10" : ""
            }`}
          >
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-bold text-white uppercase tracking-wider">{p.mode}</span>
              {selectedMode === p.mode && <Sparkles className="w-4 h-4 text-indigo-400" />}
            </div>
            <h4 className="text-sm font-semibold text-slate-200">{p.label}</h4>
            <p className="text-xs text-slate-400 mt-2 line-clamp-2">{p.description}</p>
          </div>
        ))}
      </div>

      {/* Interactive Weight Sliders */}
      <div className="glass-card p-8 rounded-2xl space-y-6">
        <div className="flex items-center justify-between border-b border-border pb-4">
          <div className="flex items-center gap-2">
            <Sliders className="w-5 h-5 text-indigo-400" />
            <h3 className="font-semibold text-white">Live Multi-Objective Weight Sliders</h3>
          </div>
          <button
            onClick={() => selectPreset(presets.find((p) => p.mode === selectedMode) || presets[3])}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-surface border border-border text-xs text-slate-300 hover:text-white"
          >
            <RefreshCw className="w-3.5 h-3.5" /> Reset Weights
          </button>
        </div>

        <div className="space-y-5">
          <div>
            <div className="flex justify-between text-xs font-semibold mb-2">
              <span className="text-indigo-400 flex items-center gap-1">
                <Sparkles className="w-3.5 h-3.5" /> Quality Score Weight ({weights.wQuality}%)
              </span>
              <span className="text-slate-400">wQuality</span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              value={weights.wQuality}
              onChange={(e) => setWeights({ ...weights, wQuality: Number(e.target.value) })}
              className="w-full accent-indigo-500 bg-surface h-2 rounded-lg cursor-pointer"
            />
          </div>

          <div>
            <div className="flex justify-between text-xs font-semibold mb-2">
              <span className="text-blue-400 flex items-center gap-1">
                <Clock className="w-3.5 h-3.5" /> Latency Speed Weight ({weights.wLatency}%)
              </span>
              <span className="text-slate-400">wLatency</span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              value={weights.wLatency}
              onChange={(e) => setWeights({ ...weights, wLatency: Number(e.target.value) })}
              className="w-full accent-blue-500 bg-surface h-2 rounded-lg cursor-pointer"
            />
          </div>

          <div>
            <div className="flex justify-between text-xs font-semibold mb-2">
              <span className="text-emerald-400 flex items-center gap-1">
                <DollarSign className="w-3.5 h-3.5" /> Token Cost Weight ({weights.wCost}%)
              </span>
              <span className="text-slate-400">wCost</span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              value={weights.wCost}
              onChange={(e) => setWeights({ ...weights, wCost: Number(e.target.value) })}
              className="w-full accent-emerald-500 bg-surface h-2 rounded-lg cursor-pointer"
            />
          </div>

          <div>
            <div className="flex justify-between text-xs font-semibold mb-2">
              <span className="text-purple-400 flex items-center gap-1">
                <ShieldCheck className="w-3.5 h-3.5" /> Reliability Weight ({weights.wReliability}%)
              </span>
              <span className="text-slate-400">wReliability</span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              value={weights.wReliability}
              onChange={(e) => setWeights({ ...weights, wReliability: Number(e.target.value) })}
              className="w-full accent-purple-500 bg-surface h-2 rounded-lg cursor-pointer"
            />
          </div>
        </div>

        <div className="pt-4 flex justify-end">
          <button className="px-6 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-medium text-xs shadow-lg shadow-indigo-500/20 transition-all">
            Save Policy Configuration
          </button>
        </div>
      </div>
    </div>
  );
}
