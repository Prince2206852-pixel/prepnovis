import Image from "next/image";
import { Quote, Sparkles } from "lucide-react";

export default function FounderNote() {
  return (
    <section className="relative overflow-hidden bg-white py-24 sm:py-28">
      <div className="pointer-events-none absolute left-1/2 top-1/2 h-80 w-80 -translate-x-1/2 -translate-y-1/2 rounded-full bg-indigo-100/60 blur-[120px]" />

      <div className="relative mx-auto max-w-5xl px-6 lg:px-8">
        <div className="overflow-hidden rounded-[32px] border border-slate-200 bg-slate-950 shadow-2xl shadow-slate-200/60">
          <div className="grid lg:grid-cols-[0.35fr_1fr]">
            
            {/* Left side */}
            <div className="flex flex-col justify-between border-b border-white/10 bg-gradient-to-br from-indigo-600/20 to-purple-600/10 p-8 lg:border-b-0 lg:border-r lg:p-10">
              <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-indigo-500/15 text-indigo-300">
                <Quote size={27} />
              </div>

              <div className="mt-12 lg:mt-24">
                <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-[0.18em] text-indigo-300">
                  <Sparkles size={14} />
                  Founder&apos;s Note
                </div>

                <p className="mt-3 text-sm text-slate-500">
                  Why PrepNovis exists
                </p>
              </div>
            </div>

            {/* Quote */}
            <div className="p-8 sm:p-10 lg:p-12">
              <blockquote className="text-2xl font-medium leading-relaxed tracking-tight text-white sm:text-3xl">
                &ldquo;Knowing the concepts and performing well in an
                interview are two different things. PrepNovis is being
                built to bridge that gap — by helping you practice
                repeatedly, understand where you&apos;re weak, and
                improve before the real interview.&rdquo;
              </blockquote>

              {/* Founder */}
              <div className="mt-10 flex items-center gap-4 border-t border-white/10 pt-7">
                <div className="relative h-14 w-14 shrink-0 overflow-hidden rounded-full border-2 border-indigo-400/40">
                  <Image
                    src="/images/prince-kumar.png"
                    alt="Prince Kumar, Founder and CEO of PrepNovis"
                    fill
                    sizes="56px"
                    className="object-cover"
                  />
                </div>

                <div>
                  <p className="font-semibold text-white">
                    Prince Kumar
                  </p>

                  <p className="mt-0.5 text-sm text-slate-400">
                    Founder &amp; CEO, PrepNovis
                  </p>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </section>
  );
}