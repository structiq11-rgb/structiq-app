"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabaseClient";

export default function NewProject() {
  const router = useRouter();
  const [name, setName] = useState("");
  const [clientName, setClientName] = useState("");
  const [siteAddress, setSiteAddress] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    setLoading(true);

    const { data: sessionData } = await supabase.auth.getSession();
    if (!sessionData.session) {
      router.replace("/login");
      return;
    }
    const userId = sessionData.session.user.id;

    const { data: org } = await supabase
      .from("organizations")
      .select("id")
      .eq("owner_id", userId)
      .single();

    if (!org) {
      setError("No organization found for this account.");
      setLoading(false);
      return;
    }

    const { data: project, error: insertError } = await supabase
      .from("projects")
      .insert({
        org_id: org.id,
        name,
        client_name: clientName || null,
        site_address: siteAddress || null,
        start_date: startDate || null,
        end_date: endDate || null,
      })
      .select()
      .single();

    setLoading(false);

    if (insertError || !project) {
      setError(insertError?.message || "Could not create project.");
      return;
    }

    router.replace(`/projects/${project.id}`);
  }

  return (
    <div className="container" style={{ paddingTop: 40 }}>
      <h1>New project</h1>
      <p className="sub">The basics — you can add budget and daily updates once it's created.</p>
      <form onSubmit={handleCreate}>
        <label>Project name</label>
        <input value={name} onChange={(e) => setName(e.target.value)} required />

        <label>Client name</label>
        <input value={clientName} onChange={(e) => setClientName(e.target.value)} />

        <label>Site address</label>
        <input value={siteAddress} onChange={(e) => setSiteAddress(e.target.value)} />

        <label>Start date</label>
        <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />

        <label>End date</label>
        <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />

        {error && <p className="error">{error}</p>}

        <button type="submit" disabled={loading}>
          {loading ? "Creating..." : "Create project"}
        </button>
        <a href="/dashboard"><button className="secondary" type="button">Cancel</button></a>
      </form>
    </div>
  );
}
