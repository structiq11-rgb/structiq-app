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

type BudgetLine = {
  id: string;
  category: string;
  description: string | null;
  budgeted_amount: number;
};

type Expense = {
  id: string;
  budget_line_id: string;
  amount: number;
  note: string | null;
  expense_date: string;
};

const CATEGORIES = ["Labour", "Materials", "Equipment", "Other"];

export default function ProjectDetail() {
  const params = useParams();
  const router = useRouter();
  const id = params.id as string;

  const [project, setProject] = useState<Project | null>(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);

  const [budgetLines, setBudgetLines] = useState<BudgetLine[]>([]);
  const [expenses, setExpenses] = useState<Expense[]>([]);

  // add budget line form
  const [category, setCategory] = useState(CATEGORIES[0]);
  const [description, setDescription] = useState("");
  const [budgetedAmount, setBudgetedAmount] = useState("");
  const [savingLine, setSavingLine] = useState(false);
  const [lineError, setLineError] = useState("");

  // log expense form
  const [expenseLineId, setExpenseLineId] = useState("");
  const [expenseAmount, setExpenseAmount] = useState("");
  const [expenseNote, setExpenseNote] = useState("");
  const [savingExpense, setSavingExpense] = useState(false);
  const [expenseError, setExpenseError] = useState("");

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
    await loadBudget();
    setLoading(false);
  }

  async function loadBudget() {
    const { data: lines } = await supabase
      .from("budget_lines")
      .select("id, category, description, budgeted_amount")
      .eq("project_id", id)
      .order("created_at", { ascending: true });

    setBudgetLines(lines || []);

    const lineIds = (lines || []).map((l) => l.id);
    if (lineIds.length === 0) {
      setExpenses([]);
      return;
    }

    const { data: expenseRows } = await supabase
      .from("expenses")
      .select("id, budget_line_id, amount, note, expense_date")
      .in("budget_line_id", lineIds)
      .order("expense_date", { ascending: false });

    setExpenses(expenseRows || []);

    if (!expenseLineId && lines && lines.length > 0) {
      setExpenseLineId(lines[0].id);
    }
  }

  function spentFor(lineId: string) {
    return expenses
      .filter((e) => e.budget_line_id === lineId)
      .reduce((sum, e) => sum + Number(e.amount), 0);
  }

  async function handleAddBudgetLine(e: React.FormEvent) {
    e.preventDefault();
    setLineError("");

    const amount = Number(budgetedAmount);
    if (!amount || amount <= 0) {
      setLineError("Enter a budget amount greater than 0.");
      return;
    }

    setSavingLine(true);
    const { error } = await supabase.from("budget_lines").insert({
      project_id: id,
      category,
      description: description || null,
      budgeted_amount: amount,
    });
    setSavingLine(false);

    if (error) {
      setLineError(error.message);
      return;
    }

    setDescription("");
    setBudgetedAmount("");
    await loadBudget();
  }

  async function handleAddExpense(e: React.FormEvent) {
    e.preventDefault();
    setExpenseError("");

    if (!expenseLineId) {
      setExpenseError("Add a budget line first, then you can log expenses against it.");
      return;
    }
    const amount = Number(expenseAmount);
    if (!amount || amount <= 0) {
      setExpenseError("Enter an expense amount greater than 0.");
      return;
    }

    setSavingExpense(true);
    const { error } = await supabase.from("expenses").insert({
      budget_line_id: expenseLineId,
      amount,
      note: expenseNote || null,
    });
    setSavingExpense(false);

    if (error) {
      setExpenseError(error.message);
      return;
    }

    setExpenseAmount("");
    setExpenseNote("");
    await loadBudget();
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

  const totalBudgeted = budgetLines.reduce((s, l) => s + Number(l.budgeted_amount), 0);
  const totalSpent = budgetLines.reduce((s, l) => s + spentFor(l.id), 0);

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
        <p style={{ marginTop: 4, color: "#444" }}>{project.site_address || "Not set"}</p>
        <strong>Timeline</strong>
        <p style={{ marginTop: 4, color: "#444" }}>
          {project.start_date || "?"} — {project.end_date || "?"}
        </p>
      </div>

      <h2>Budget overview</h2>
      <div className="card">
        <div style={{ display: "flex", justifyContent: "space-between", fontSize: 14 }}>
          <span>Total budgeted</span>
          <strong>KES {totalBudgeted.toLocaleString()}</strong>
        </div>
        <div style={{ display: "flex", justifyContent: "space-between", fontSize: 14, marginTop: 4 }}>
          <span>Total spent</span>
          <strong>KES {totalSpent.toLocaleString()}</strong>
        </div>
      </div>

      {budgetLines.length === 0 && (
        <p className="sub">No budget lines yet — add your first one below.</p>
      )}

      {budgetLines.map((line) => {
        const spent = spentFor(line.id);
        const pct = line.budgeted_amount > 0 ? (spent / line.budgeted_amount) * 100 : 0;
        const color = pct >= 100 ? "#9B2226" : pct >= 80 ? "#E87722" : "#1A7A4A";

        return (
          <div key={line.id} className="card">
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <strong>{line.category}</strong>
              <span style={{ fontSize: 13, color: "#666" }}>
                {pct >= 80 && pct < 100 && "⚠ Near limit"}
                {pct >= 100 && "⚠ Over budget"}
              </span>
            </div>
            {line.description && (
              <p style={{ fontSize: 13, color: "#666", margin: "2px 0 8px" }}>{line.description}</p>
            )}
            <div style={{ fontSize: 13, color: "#444", marginBottom: 4 }}>
              KES {spent.toLocaleString()} spent of {line.budgeted_amount.toLocaleString()} budgeted
              {" · "}
              {pct.toFixed(0)}%
            </div>
            <div style={{ background: "#eee", borderRadius: 6, height: 8, overflow: "hidden" }}>
              <div
                style={{
                  width: `${Math.min(pct, 100)}%`,
                  background: color,
                  height: "100%",
                }}
              />
            </div>
          </div>
        );
      })}

      <h2>Add a budget line</h2>
      <div className="card">
        <form onSubmit={handleAddBudgetLine}>
          <label>Category</label>
          <select value={category} onChange={(e) => setCategory(e.target.value)}>
            {CATEGORIES.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>

          <label>Description (optional)</label>
          <input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="e.g. Cement and blocks" />

          <label>Budgeted amount (KES)</label>
          <input
            type="number"
            value={budgetedAmount}
            onChange={(e) => setBudgetedAmount(e.target.value)}
            placeholder="e.g. 250000"
          />

          {lineError && <p className="error">{lineError}</p>}

          <button type="submit" disabled={savingLine}>
            {savingLine ? "Saving..." : "Add budget line"}
          </button>
        </form>
      </div>

      <h2>Log an expense</h2>
      <div className="card">
        {budgetLines.length === 0 ? (
          <p className="sub" style={{ marginBottom: 0 }}>
            Add a budget line above first — expenses log against a specific category.
          </p>
        ) : (
          <form onSubmit={handleAddExpense}>
            <label>Budget line</label>
            <select value={expenseLineId} onChange={(e) => setExpenseLineId(e.target.value)}>
              {budgetLines.map((l) => (
                <option key={l.id} value={l.id}>
                  {l.category}{l.description ? ` — ${l.description}` : ""}
                </option>
              ))}
            </select>

            <label>Amount (KES)</label>
            <input
              type="number"
              value={expenseAmount}
              onChange={(e) => setExpenseAmount(e.target.value)}
              placeholder="e.g. 15000"
            />

            <label>Note (optional)</label>
            <input value={expenseNote} onChange={(e) => setExpenseNote(e.target.value)} placeholder="e.g. Delivery receipt #4521" />

            {expenseError && <p className="error">{expenseError}</p>}

            <button type="submit" disabled={savingExpense}>
              {savingExpense ? "Saving..." : "Log expense"}
            </button>
          </form>
        )}
      </div>

      {expenses.length > 0 && (
        <>
          <h2>Recent expenses</h2>
          {expenses.slice(0, 10).map((exp) => {
            const line = budgetLines.find((l) => l.id === exp.budget_line_id);
            return (
              <div key={exp.id} className="card" style={{ padding: 12 }}>
                <div style={{ display: "flex", justifyContent: "space-between", fontSize: 14 }}>
                  <span>{line?.category || "Unknown category"}</span>
                  <strong>KES {Number(exp.amount).toLocaleString()}</strong>
                </div>
                <div style={{ fontSize: 12, color: "#666", marginTop: 2 }}>
                  {exp.expense_date}
                  {exp.note && ` · ${exp.note}`}
                </div>
              </div>
            );
          })}
        </>
      )}

      <div className="card" style={{ background: "#F0F0EC", borderStyle: "dashed", marginTop: 20 }}>
        <strong>Coming next (Week 3 build)</strong>
        <p style={{ marginTop: 4, color: "#666", fontSize: 14 }}>
          Daily attendance and site updates will plug in below this section —
          the <code>attendance</code> and <code>site_updates</code> tables
          already exist in your database.
        </p>
      </div>
    </div>
  );
}
