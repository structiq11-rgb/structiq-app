"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabaseClient";

type Project = {
  id: string;
  name: string;
  client_name: string | null;
  status: string;
  start_date: string | null;
};

export default function Dashboard() {
  const router = useRouter();
  const [orgName, setOrgName] = useState("");
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    load();
  }, []);

  async function load() {
    const { data: sessionData } = await supabase.auth.getSession();
    if (!sessionData.session) {
      router.replace("/login");
      return;
    }
    const userId = sessionData.session.user.id;

    const { data: org } = await supabase
      .from("organizations")
      .select("id, name")
      .eq("owner_id", userId)
      .single();

    if (!org) {
      setLoading(false);
      return;
    }
    setOrgName(org.name);

    const { data: projectRows } = await supabase
      .from("projects")
      .select("id, name, client_name, status, start_date")
      .eq("org_id", org.id)
      .order("created_at", { ascending: false });

    setProjects(projectRows || []);
    setLoading(false);
  }

  async function handleLogout() {
    await supabase.auth.signOut();
    router.replace("/login");
  }

  if (loading) {
    return (
      <div className="container" style={{ paddingTop: 60 }}>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="wide-container">
      <div className="top-bar">
        <div>
          <h1>{orgName || "Your workspace"}</h1>
          <p className="sub" style={{ marginBottom: 0 }}>
            {projects.length} project{projects.length === 1 ? "" : "s"}
          </p>
        </div>
        <button className="secondary" onClick={handleLogout}>
          Log out
        </button>
      </div>

      <a href="/projects/new"><button>+ New project</button></a>

      <h2>Your projects</h2>

      {projects.length === 0 && (
        <p className="sub">No projects yet. Create your first one above.</p>
      )}

      {projects.map((p) => (
        <a key={p.id} href={`/projects/${p.id}`} style={{ display: "block" }}>
          <div className="card">
            <strong>{p.name}</strong>
            <div style={{ fontSize: 13, color: "#666", marginTop: 4 }}>
              {p.client_name && <span>Client: {p.client_name} · </span>}
              Status: {p.status}
              {p.start_date && <span> · Started {p.start_date}</span>}
            </div>
          </div>
        </a>
      ))}
    </div>
  );
}
