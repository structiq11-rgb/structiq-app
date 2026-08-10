"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabaseClient";

export default function Signup() {
  const router = useRouter();
  const [orgName, setOrgName] = useState("");
  const [county, setCounty] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSignup(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    setLoading(true);

    const { data: signUpData, error: signUpError } = await supabase.auth.signUp({
      email,
      password,
    });

    if (signUpError || !signUpData.user) {
      setError(signUpError?.message || "Could not create account.");
      setLoading(false);
      return;
    }

    // Create the organization row owned by the new user.
    // If Supabase email confirmation is ON, there is no active session yet,
    // so this insert will fail RLS until they confirm + log in. For a fast
    // pilot launch, turn OFF "Confirm email" in Supabase Auth settings so
    // signup logs the user in immediately.
    const { error: orgError } = await supabase.from("organizations").insert({
      owner_id: signUpData.user.id,
      name: orgName,
      county,
    });

    if (orgError) {
      setError(
        "Account created, but organization setup failed: " +
          orgError.message +
          ". If you have email confirmation on in Supabase, confirm your email and log in, then try again."
      );
      setLoading(false);
      return;
    }

    setLoading(false);
    router.replace("/dashboard");
  }

  return (
    <div className="container" style={{ paddingTop: 60 }}>
      <h1>Create your account</h1>
      <p className="sub">Set up your company workspace in under a minute.</p>
      <form onSubmit={handleSignup}>
        <label>Company / organization name</label>
        <input value={orgName} onChange={(e) => setOrgName(e.target.value)} required />

        <label>County</label>
        <input value={county} onChange={(e) => setCounty(e.target.value)} placeholder="e.g. Nairobi" />

        <label>Email</label>
        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />

        <label>Password</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          minLength={6}
          required
        />

        {error && <p className="error">{error}</p>}

        <button type="submit" disabled={loading}>
          {loading ? "Creating account..." : "Create account"}
        </button>
      </form>
      <p style={{ marginTop: 16, fontSize: 13, textAlign: "center" }}>
        Already have an account? <a href="/login">Log in</a>
      </p>
    </div>
  );
}
