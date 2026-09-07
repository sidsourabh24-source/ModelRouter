"use client";

import React from "react";
import { Activity, ShieldCheck, Zap } from "lucide-react";

export default function Navbar() {
  return (
    <header className="h-16 border-b border-border bg-surface/50 backdrop-blur-md px-6 flex items-center justify-between sticky top-0 z-40">
      <div className="flex items-center gap-3">
        <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-500 flex items-center justify-center shadow-lg shadow-indigo-500/20">
          <Zap className="w-5 h-5 text-white" />
        </div>
        <div>
          <h1 className="font-bold text-lg text-white tracking-tight leading-none">
            ModelRouter <span className="text-xs text-indigo-400 font-normal px-2 py-0.5 rounded-full bg-indigo-500/10 border border-indigo-500/20">v1.0 Gateway</span>
          </h1>
          <p className="text-xs text-slate-400">Adaptive AI Inference & Control Plane</p>
        </div>
      </div>

      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-medium">
          <Activity className="w-3.5 h-3.5 animate-pulse" />
          <span>Gateway Active</span>
        </div>

        <div className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-slate-800 border border-slate-700 text-xs text-slate-300">
          <ShieldCheck className="w-3.5 h-3.5 text-indigo-400" />
          <span>Org: demo-org-001</span>
        </div>
      </div>
    </header>
  );
}
