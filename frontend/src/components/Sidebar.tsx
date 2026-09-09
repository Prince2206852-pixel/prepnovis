"use client";


import { clearAuthSession } from "@/lib/auth";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import {
  BarChart3,
  BookOpen,
  BrainCircuit,
  LayoutDashboard,
  LogOut,
  Settings,
  Target,
} from "lucide-react";

import PrepNovisLogo from "@/components/PrepNovisLogo";

const navigationItems = [
  {
    name: "Dashboard",
    href: "/dashboard",
    icon: LayoutDashboard,
  },
  {
    name: "Saved Questions",
    href: "/questions",
    icon: BookOpen,
  },
  {
    name: "Practice",
    href: "/practice",
    icon: Target,
  },
  {
    name: "PrepNovis Mock",
    href: "/mock",
    icon: BrainCircuit,
  },
  {
    name: "Results & Analytics",
    href: "/analytics",
    icon: BarChart3,
  },
];

export default function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();

  function handleLogout() {
  clearAuthSession();
  router.push("/login");
}

  return (
    <aside className="fixed left-0 top-0 flex h-screen w-64 flex-col bg-slate-950 px-4 py-6 text-white">
      <div className="mb-10 px-3">
        <Link href="/dashboard">
          <PrepNovisLogo size={44} />
        </Link>
      </div>

      <nav className="flex flex-1 flex-col gap-2">
        {navigationItems.map((item) => {
          const Icon = item.icon;

          const isActive =
            pathname === item.href ||
            (item.href !== "/dashboard" &&
              pathname.startsWith(`${item.href}/`));

          return (
            <Link
              key={item.name}
              href={item.href}
              className={`flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition ${
                isActive
                  ? "bg-indigo-500 text-white shadow-lg shadow-indigo-950/20"
                  : "text-slate-400 hover:bg-slate-900 hover:text-white"
              }`}
            >
              <Icon size={19} />
              <span>{item.name}</span>
            </Link>
          );
        })}
      </nav>

      <div className="border-t border-slate-800 pt-4">
        <Link
          href="/settings"
          className="flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium text-slate-400 transition hover:bg-slate-900 hover:text-white"
        >
          <Settings size={19} />
          Settings
        </Link>

        <button
             type="button"
             onClick={handleLogout}
            className="flex w-full items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium text-slate-400 transition hover:bg-slate-900 hover:text-white"
        >
           <LogOut size={19} />
            Logout
        </button>
      </div>
    </aside>
  );
}