"use client";

import React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { LayoutDashboard, Cpu, Sliders, ListTree, Play, Settings } from "lucide-react";

const navItems = [
  { name: "Overview", href: "/", icon: LayoutDashboard },
  { name: "Live Playground", href: "/playground", icon: Play },
  { name: "Models & Providers", href: "/models", icon: Cpu },
  { name: "Routing Policies", href: "/policies", icon: Sliders },
  { name: "Request Explorer", href: "/requests", icon: ListTree },
];

export default function Sidebar() {
  const pathname = usePathname();

  return (
    <aside className="w-64 border-r border-border bg-surface/30 min-h-[calc(100vh-4rem)] p-4 flex flex-col justify-between">
      <nav className="space-y-1.5">
        <div className="px-3 py-2 text-xs font-semibold text-slate-400 uppercase tracking-wider">
          Control Plane
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200 ${
                isActive
                  ? "bg-indigo-600/15 text-indigo-400 border border-indigo-500/30 shadow-sm shadow-indigo-500/10"
                  : "text-slate-400 hover:text-slate-200 hover:bg-slate-800/50"
              }`}
            >
              <Icon className={`w-4 h-4 ${isActive ? "text-indigo-400" : "text-slate-400"}`} />
              <span>{item.name}</span>
            </Link>
          );
        })}
      </nav>

      <div className="p-3 rounded-xl bg-card border border-border">
        <div className="flex items-center gap-2 text-xs font-medium text-slate-300">
          <Settings className="w-4 h-4 text-indigo-400" />
          <span>System Status</span>
        </div>
        <p className="text-[11px] text-slate-400 mt-1">
          PostgreSQL & Redis connected. Routing overhead &lt; 8ms.
        </p>
      </div>
    </aside>
  );
}
