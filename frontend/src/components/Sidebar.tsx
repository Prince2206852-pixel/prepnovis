"use client";

import { clearAuthSession } from "@/lib/auth";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import {
  BarChart3,
  BookOpen,
  BrainCircuit,
  LayoutDashboard,
  LogOut,
  Menu,
  Settings,
  Target,
  X,
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

type SidebarContentProps = {
  pathname: string;
  onClose: () => void;
  onLogout: () => void;
};

function SidebarContent({
  pathname,
  onClose,
  onLogout,
}: SidebarContentProps) {
  return (
    <>
      <div className="mb-8 flex items-center justify-between px-3">
        <Link href="/dashboard" onClick={onClose}>
          <PrepNovisLogo size={44} />
        </Link>

        <button
          type="button"
          onClick={onClose}
          className="rounded-lg p-2 text-slate-400 transition hover:bg-slate-900 hover:text-white lg:hidden"
          aria-label="Close navigation"
        >
          <X size={22} />
        </button>
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
              onClick={onClose}
              className={`flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition ${
                isActive
                  ? "bg-indigo-500 text-white shadow-lg shadow-indigo-950/20"
                  : "text-slate-400 hover:bg-slate-900 hover:text-white"
              }`}
            >
              <Icon size={19} className="shrink-0" />
              <span>{item.name}</span>
            </Link>
          );
        })}
      </nav>

      <div className="border-t border-slate-800 pt-4">
        <Link
          href="/settings"
          onClick={onClose}
          className="flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium text-slate-400 transition hover:bg-slate-900 hover:text-white"
        >
          <Settings size={19} className="shrink-0" />
          <span>Settings</span>
        </Link>

        <button
          type="button"
          onClick={onLogout}
          className="flex w-full items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium text-slate-400 transition hover:bg-slate-900 hover:text-white"
        >
          <LogOut size={19} className="shrink-0" />
          <span>Logout</span>
        </button>
      </div>
    </>
  );
}

export default function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();

  const [mobileOpen, setMobileOpen] = useState(false);

  useEffect(() => {
    if (mobileOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }

    return () => {
      document.body.style.overflow = "";
    };
  }, [mobileOpen]);

  function handleClose() {
    setMobileOpen(false);
  }

  function handleLogout() {
    clearAuthSession();
    setMobileOpen(false);
    router.push("/login");
  }

  return (
    <>
      {/* MOBILE MENU BUTTON */}
      <button
        type="button"
        onClick={() => setMobileOpen(true)}
        className="fixed left-4 top-4 z-40 flex h-11 w-11 items-center justify-center rounded-xl border border-slate-200 bg-white text-slate-700 shadow-sm lg:hidden"
        aria-label="Open navigation"
      >
        <Menu size={22} />
      </button>

      {/* DESKTOP SIDEBAR */}
      <aside className="fixed left-0 top-0 z-40 hidden h-screen w-64 flex-col bg-slate-950 px-4 py-6 text-white lg:flex">
        <SidebarContent
          pathname={pathname}
          onClose={handleClose}
          onLogout={handleLogout}
        />
      </aside>

      {/* MOBILE OVERLAY */}
      {mobileOpen && (
        <button
          type="button"
          aria-label="Close navigation"
          onClick={handleClose}
          className="fixed inset-0 z-40 bg-slate-950/60 backdrop-blur-sm lg:hidden"
        />
      )}

      {/* MOBILE SIDEBAR */}
      <aside
        className={`fixed left-0 top-0 z-50 flex h-dvh w-[280px] max-w-[85vw] flex-col bg-slate-950 px-4 py-6 text-white shadow-2xl transition-transform duration-300 lg:hidden ${
          mobileOpen ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <SidebarContent
          pathname={pathname}
          onClose={handleClose}
          onLogout={handleLogout}
        />
      </aside>
    </>
  );
}