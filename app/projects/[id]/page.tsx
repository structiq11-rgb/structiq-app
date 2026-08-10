"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { supabase } from "@/lib/supabaseClient";

type Project = {
  id: string;
  name: string;
  client_name: string | null;
  site_address: string | null;
  start_date: string | null;
  end_date: string | null;
  status: string;
};

export default function ProjectDetail() {
  const params = useParams();
  const router = useRouter();
  const id = params.id as string;
  const [project, setProject] = useState<Project | null>(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);

  useEffect(() => {
    load();
  }, [id]);

  async function load() {
    const { data: sessionData } = await supabase.auth.getSession();
    if (!sessionData.session) {
      router.replace("/login");
      return;
    }

    const { data, error } = await supabase
      .from("projects")
      .select("id, name, client_name, site_address, start_date, end_date, status")
      .eq("id", id)
      .single();

    if (error || !data) {
      setNotFound(true);
      setLoading(false);
      return;
    }

    setProject(data);
    setLoading(false);
  }

  if (loading) {
    return (
      <div className="container" style={{ paddingTop: 60 }}>
        <p>Loading...</p>
      </div>
    );
  }

  if (notFound || !project) {
    return (
      <div className="container" style={{ paddingTop: 60 }}>
        <p>Project not found, or you don't have access to it.</p>
        <a href="/dashboard"><button className="secondary">Back to dashboard</button></a>
      </div>
    );
  }

  return (
    <div className="wide-container">
      <div className="top-bar">
        <div>
          <h1>{project.name}</h1>
          <p className="sub" style={{ marginBottom: 0 }}>
            {project.client_name && <span>Client: {project.client_name} · </span>}
            Status: {project.status}
          </p>
        </div>
        <a href="/dashboard"><button className="secondary">Back</button></a>
      </div>

      <div className="card">
        <strong>Site address</strong>
        <p style={{ marginTop: 4, color: "#444" }}>
          {project.site_address || "Not set"}
        </p>
        <strong>Timeline</strong>
        <p style={{ marginTop: 4, color: "#444" }}>
          {project.start_date || "?"} — {project.end_date || "?"}
        </p>
      </div>

      <div className="card" style={{ background: "#F0F0EC", borderStyle: "dashed" }}>
        <strong>Coming next (Week 2 build)</strong>
        <p style={{ marginTop: 4, color: "#666", fontSize: 14 }}>
          Budget setup, expense logging, and the budget-vs-actual view will
          plug into this page here — the <code>budget_lines</code> and{" "}
          <code>expenses</code> tables already exist in your database from the
          schema you ran, so Week 2 is pure frontend work against this same
          project record.
        </p>
      </div>
    </div>
  );
}
