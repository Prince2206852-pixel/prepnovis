import Image from "next/image";

type PrepNovisLogoProps = {
  size?: number;
  showName?: boolean;
};

export default function PrepNovisLogo({
  size = 42,
  showName = true,
}: PrepNovisLogoProps) {
  return (
    <div className="flex items-center gap-3">
      <Image
        src="/images/prepnovis-logo.png"
        alt="PrepNovis logo"
        width={size}
        height={size}
        priority
        className="rounded-xl object-contain"
      />

      {showName && (
        <div>
          <p className="text-xl font-bold tracking-tight text-white">
            PrepNovis
          </p>

          <p className="text-xs text-slate-400">
            Interview smarter
          </p>
        </div>
      )}
    </div>
  );
}