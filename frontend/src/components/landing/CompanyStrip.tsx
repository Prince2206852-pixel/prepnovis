const companies = [
  "Google",
  "Amazon",
  "Microsoft",
  "Meta",
  "Adobe",
  "Accenture",
  "Deloitte",
  "TCS",
  "Infosys",
];

export default function CompanyStrip() {
  return (
    <section className="border-y border-slate-200 bg-white">
      <div className="mx-auto max-w-7xl px-6 py-14 lg:px-8">
        <div className="text-center">
          <p className="text-sm font-semibold uppercase tracking-[0.18em] text-indigo-600">
            Prepare for your next opportunity
          </p>

          <h2 className="mt-3 text-xl font-semibold text-slate-900 sm:text-2xl">
            Practice for interviews at companies like
          </h2>
        </div>

        <div className="mt-10 flex flex-wrap items-center justify-center gap-x-10 gap-y-7 lg:gap-x-14">
          {companies.map((company) => (
            <div
              key={company}
              className="text-lg font-bold tracking-tight text-slate-400 transition duration-300 hover:text-slate-900 sm:text-xl"
            >
              {company}
            </div>
          ))}
        </div>

        <p className="mx-auto mt-9 max-w-2xl text-center text-sm leading-6 text-slate-500">
          Whatever company you&apos;re targeting, PrepNovis helps
          you practice before the real interview.
        </p>

        <p className="mt-3 text-center text-[11px] leading-5 text-slate-400">
          Company names and trademarks belong to their respective
          owners. PrepNovis is not affiliated with or endorsed by
          the companies shown.
        </p>
      </div>
    </section>
  );
}