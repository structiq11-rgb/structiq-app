"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabaseClient";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    supabase.auth.getSession().then(({ data }) => {
      if (data.session) router.replace("/dashboard");
    });
  }, [router]);

  return (
    <div className="container" style={{ paddingTop: 80, textAlign: "center" }}>
      <h1>Struct-IQ</h1>
      <p className="sub">Daily site reporting and budget tracking, built for Kenyan contractors.</p>
      <a href="/signup"><button>Get started</button></a>
      <a href="/login"><button className="secondary">Log in</button></a>
    </div>
  );
}
