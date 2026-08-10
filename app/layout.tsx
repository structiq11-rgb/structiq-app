import "./globals.css";

export const metadata = {
  title: "Struct-IQ",
  description: "Daily site reporting and budget tracking for contractors",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
