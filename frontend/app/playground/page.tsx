"use client";

import React, { useState } from "react";
import { Play, Sparkles, Zap, Shield, CheckCircle2, Clock, DollarSign, Cpu, ArrowRight } from "lucide-react";

interface ChatMessage {
  role: string;
  content: string;
}

interface InferenceResponse {
  requestId: string;
  model: string;
  provider: string;
  content: string;
  cacheHit: boolean;
  usage?: {
    inputTokens: number;
    outputTokens: number;
    estimatedCost: number;
    latencyMs: number;
  };
  routing?: {
    mode: string;
    taskCategory: string;
    complexityScore: number;
    candidateCount: number;
    calculatedScore: number;
    reason: string;
    evaluatedCandidates?: { modelId: string; modelName: string; score: number }[];
  };
}

const samplePrompts = [
  {
    label: "Reasoning Task",
    mode: "BALANCED",
    prompt: "Explain step-by-step why time complexity of merge sort is always O(n log n) even in worst case.",
  },
  {
    label: "Coding Task",
    mode: "QUALITY",
    prompt: "Write a Java function for QuickSort algorithm with recursion and pivot partition.",
  },
  {
    label: "Fast Chat",
    mode: "CHEAP",
    prompt: "Hello ModelRouter! What are 3 habits for high productivity?",
  },
  {
    label: "Creative Writing",
    mode: "BALANCED",
    prompt: "Draft a polite, professional email to request a 2-day project deadline extension.",
  },
];

export default function PlaygroundPage() {
  const [mode, setMode] = useState<string>("BALANCED");
  const [prompt, setPrompt] = useState<string>("Explain step-by-step why time complexity of merge sort is always O(n log n) even in worst case.");
  const [apiKey, setApiKey] = useState<string>("modelrouter-demo-key-123");
  const [loading, setLoading] = useState<boolean>(false);
  const [result, setResult] = useState<InferenceResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleExecute = async () => {
    if (!prompt.trim()) return;

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const res = await fetch("http://localhost:8080/api/v1/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-API-Key": apiKey,
        },
        body: JSON.stringify({
          mode: mode,
          messages: [
            {
              role: "user",
              content: prompt,
            },
          ],
        }),
      });

      if (!res.ok) {
        throw new Error(`Gateway returned HTTP ${res.status}: ${res.statusText}`);
      }

      const data: InferenceResponse = await res.json();
      setResult(data);
    } catch (err: any) {
      setError(err.message || "Failed to communicate with ModelRouter gateway.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Interactive AI Inference Playground</h2>
          <p className="text-slate-400 text-sm mt-1">
            Test live model routing, automated task classification, and fallback execution in real time directly from your browser.
          </p>
        </div>
        <div className="flex items-center gap-2 bg-emerald-500/10 border border-emerald-500/20 px-3 py-1.5 rounded-xl text-emerald-400 text-xs font-semibold">
          <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
          <span>Gateway Live on :8080</span>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Column: Request Form */}
        <div className="lg:col-span-6 space-y-6">
          <div className="glass-card p-6 rounded-2xl space-y-5">
            <h3 className="font-semibold text-white text-sm flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-indigo-400" />
              <span>Routing Configuration</span>
            </h3>

            {/* Mode Selector */}
            <div>
              <label className="text-xs text-slate-400 block mb-2 font-medium">Select Routing Objective Mode</label>
              <div className="grid grid-cols-4 gap-2">
                {["BALANCED", "CHEAP", "FAST", "QUALITY"].map((m) => (
                  <button
                    key={m}
                    type="button"
                    onClick={() => setMode(m)}
                    className={`py-2 px-3 rounded-xl text-xs font-semibold border transition-all ${
                      mode === m
                        ? "bg-indigo-600 text-white border-indigo-500 shadow-md shadow-indigo-600/30"
                        : "bg-surface text-slate-400 border-border hover:text-white"
                    }`}
                  >
                    {m}
                  </button>
                ))}
              </div>
            </div>

            {/* API Key */}
            <div>
              <label className="text-xs text-slate-400 block mb-1 font-medium">Gateway API Key Header (X-API-Key)</label>
              <input
                type="text"
                value={apiKey}
                onChange={(e) => setApiKey(e.target.value)}
                className="w-full bg-surface border border-border rounded-xl px-3 py-2 text-xs font-mono text-slate-300 focus:outline-none focus:border-indigo-500"
              />
            </div>

            {/* Quick Sample Prompts */}
            <div>
              <label className="text-xs text-slate-400 block mb-1.5 font-medium">Quick Demo Prompts</label>
              <div className="flex flex-wrap gap-2">
                {samplePrompts.map((sp) => (
                  <button
                    key={sp.label}
                    type="button"
                    onClick={() => {
                      setMode(sp.mode);
                      setPrompt(sp.prompt);
                    }}
                    className="px-2.5 py-1 rounded-lg bg-surface border border-border hover:border-indigo-500 text-[11px] text-slate-300 hover:text-white transition-all flex items-center gap-1"
                  >
                    <span>{sp.label}</span>
                    <ArrowRight className="w-3 h-3 text-slate-500" />
                  </button>
                ))}
              </div>
            </div>

            {/* User Prompt Textarea */}
            <div>
              <label className="text-xs text-slate-400 block mb-1 font-medium">User Prompt / Instructions</label>
              <textarea
                rows={5}
                value={prompt}
                onChange={(e) => setPrompt(e.target.value)}
                placeholder="Enter prompt to execute via ModelRouter..."
                className="w-full bg-surface border border-border rounded-xl p-3 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500 font-sans"
              />
            </div>

            {/* Submit Button */}
            <button
              onClick={handleExecute}
              disabled={loading || !prompt.trim()}
              className={`w-full py-3 rounded-xl font-semibold text-xs flex items-center justify-center gap-2 transition-all ${
                loading
                  ? "bg-indigo-600/50 text-slate-300 cursor-not-allowed"
                  : "bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/30"
              }`}
            >
              {loading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                  <span>Evaluating Candidates &amp; Routing...</span>
                </>
              ) : (
                <>
                  <Play className="w-4 h-4 fill-white" />
                  <span>Execute Dynamic Inference</span>
                </>
              )}
            </button>
          </div>
        </div>

        {/* Right Column: Live Output & Trace */}
        <div className="lg:col-span-6 space-y-6">
          {error && (
            <div className="p-4 rounded-2xl bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs font-medium">
              ❌ {error}
            </div>
          )}

          {result ? (
            <div className="glass-card p-6 rounded-2xl space-y-6 animate-in fade-in duration-300">
              {/* Top Result Banner */}
              <div className="flex items-center justify-between border-b border-border pb-4">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-base font-bold text-white">{result.model}</span>
                    <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
                      {result.provider}
                    </span>
                  </div>
                  <div className="font-mono text-[11px] text-slate-400 mt-0.5">
                    ID: {result.requestId}
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <span className="flex items-center gap-1 text-xs text-emerald-400 font-semibold bg-emerald-500/10 px-2.5 py-1 rounded-lg border border-emerald-500/20">
                    <CheckCircle2 className="w-3.5 h-3.5" /> Success
                  </span>
                </div>
              </div>

              {/* Metrics Grid */}
              <div className="grid grid-cols-4 gap-2 text-center">
                <div className="p-2.5 rounded-xl bg-surface border border-border">
                  <span className="text-[10px] text-slate-400 block mb-0.5">Task Category</span>
                  <span className="font-bold text-indigo-400 text-xs">{result.routing?.taskCategory || "CHAT"}</span>
                </div>
                <div className="p-2.5 rounded-xl bg-surface border border-border">
                  <span className="text-[10px] text-slate-400 block mb-0.5">Latency</span>
                  <span className="font-mono font-bold text-blue-400 text-xs">{result.usage?.latencyMs || 0} ms</span>
                </div>
                <div className="p-2.5 rounded-xl bg-surface border border-border">
                  <span className="text-[10px] text-slate-400 block mb-0.5">Est. Cost</span>
                  <span className="font-mono font-bold text-emerald-400 text-xs">${result.usage?.estimatedCost || 0}</span>
                </div>
                <div className="p-2.5 rounded-xl bg-surface border border-border">
                  <span className="text-[10px] text-slate-400 block mb-0.5">Score</span>
                  <span className="font-mono font-bold text-amber-400 text-xs">{result.routing?.calculatedScore?.toFixed(3) || "0.95"}</span>
                </div>
              </div>

              {/* Generated AI Content */}
              <div>
                <label className="text-xs text-slate-400 block mb-1.5 font-medium">Model Response Content</label>
                <div className="p-4 rounded-xl bg-slate-900/80 border border-border text-xs text-slate-200 font-mono whitespace-pre-wrap leading-relaxed">
                  {result.content}
                </div>
              </div>

              {/* Explainable Decision Trace */}
              {result.routing && (
                <div className="p-4 rounded-xl bg-indigo-950/20 border border-indigo-500/20 space-y-2">
                  <div className="flex items-center gap-1.5 text-xs font-semibold text-indigo-300">
                    <Shield className="w-4 h-4 text-indigo-400" />
                    <span>Explainable Decision Trace</span>
                  </div>
                  <p className="text-[11px] text-slate-300 leading-relaxed font-sans">
                    {result.routing.reason}
                  </p>
                </div>
              )}
            </div>
          ) : (
            <div className="glass-card p-12 rounded-2xl text-center space-y-3 border-dashed">
              <Cpu className="w-10 h-10 text-slate-600 mx-auto" />
              <h4 className="text-sm font-semibold text-slate-300">Ready for Live AI Inference</h4>
              <p className="text-xs text-slate-500 max-w-sm mx-auto">
                Configure your prompt on the left and click Execute to view real-time model selection, latency, token telemetry, and routing decision traces.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
