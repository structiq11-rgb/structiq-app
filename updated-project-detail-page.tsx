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

type AttendanceRow = {
  id: string;
  worker_name: string;
  status: string;
  attendance_date: string;
};

type SiteUpdate = {
  id: string;
  note: string;
  update_date: string;
  created_at: string;
  photo_url: string | null;
};

const CATEGORIES = ["Labour", "Materials", "Equipment", "Other"];
const ATTENDANCE_STATUSES = ["Present", "Absent", "Late"];
const PHOTO_BUCKET = "site-photos";

function todayStr() {
  return new Date().toISOString().slice(0, 10);
}

export default function ProjectDetail() {
  const params = useParams();
  const router = useRouter();
  const id = params.id as string;

  const [project, setProject] = useState<Project | null>(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);

  // budget state
  const [budgetLines, setBudgetLines] = useState<BudgetLine[]>([]);
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [category, setCategory] = useState(CATEGORIES[0]);
  const [description, setDescription] = useState("");
  const [budgetedAmount, setBudgetedAmount] = useState("");
  const [savingLine, setSavingLine] = useState(false);
  const [lineError, setLineError] = useState("");

  // editing a budget line
  const [editingLineId, setEditingLineId] = useState<string | null>(null);
  const [editCategory, setEditCategory] = useState("");
  const [editDescription, setEditDescription] = useState("");
  const [editBudgetedAmount, setEditBudgetedAmount] = useState("");
  const [editLineError, setEditLineError] = useState("");

  const [expenseLineId, setExpenseLineId] = useState("");
  const [expenseAmount, setExpenseAmount] = useState("");
  const [expenseNote, setExpenseNote] = useState("");
  const [savingExpense, setSavingExpense] = useState(false);
  const [expenseError, setExpenseError] = useState("");

  // editing an expense
  const [editingExpenseId, setEditingExpenseId] = useState<string | null>(null);
  const [editExpenseAmount, setEditExpenseAmount] = useState("");
  const [editExpenseNote, setEditExpenseNote] = useState("");
  const [editExpenseError, setEditExpenseError] = useState("");

  // attendance state
  const [attendanceToday, setAttendanceToday] = useState<AttendanceRow[]>([]);
  const [workerName, setWorkerName] = useState("");
  const [workerStatus, setWorkerStatus] = useState(ATTENDANCE_STATUSES[0]);
  const [savingAttendance, setSavingAttendance] = useState(false);
  const [attendanceError, setAttendanceError] = useState("");

  // site update state
  const [siteUpdates, setSiteUpdates] = useState<SiteUpdate[]>([]);
  const [updateNote, setUpdateNote] = useState("");
  const [updatePhoto, setUpdatePhoto] = useState<File | null>(null);
  const [savingUpdate, setSavingUpdate] = useState(false);
  const [updateError, setUpdateError] = useState("");

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
    await loadAttendance();
    await loadSiteUpdates();
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

  async function loadAttendance() {
    const { data } = await supabase
      .from("attendance")
      .select("id, worker_name, status, attendance_date")
      .eq("project_id", id)
      .eq("attendance_date", todayStr())
      .order("created_at", { ascending: true });

    setAttendanceToday(data || []);
  }

  async function loadSiteUpdates() {
    const { data } = await supabase
      .from("site_updates")
      .select("id, note, update_date, created_at, photo_url")
      .eq("project_id", id)
      .order("created_at", { ascending: false })
      .limit(10);

    setSiteUpdates(data || []);
  }

  function spentFor(lineId: string) {
    return expenses
      .filter((e) => e.budget_line_id === lineId)
      .reduce((sum, e) => sum + Number(e.amount), 0);
  }

  // ---------- BUDGET LINES ----------

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

  function startEditLine(line: BudgetLine) {
    setEditingLineId(line.id);
    setEditCategory(line.category);
    setEditDescription(line.description || "");
    setEditBudgetedAmount(String(line.budgeted_amount));
    setEditLineError("");
  }

  function cancelEditLine() {
    setEditingLineId(null);
    setEditLineError("");
  }

  async function saveEditLine(lineId: string) {
    setEditLineError("");
    const amount = Number(editBudgetedAmount);
    if (!amount || amount <= 0) {
      setEditLineError("Enter a budget amount greater than 0.");
      return;
    }

    const { error } = await supabase
      .from("budget_lines")
      .update({
        category: editCategory,
        description: editDescription || null,
        budgeted_amount: amount,
      })
      .eq("id", lineId);

    if (error) {
      setEditLineError(error.message);
      return;
    }

    setEditingLineId(null);
    await loadBudget();
  }

  async function deleteLine(lineId: string) {
    const confirmed = window.confirm(
      "Delete this budget line? Any expenses logged under it will be deleted too."
    );
    if (!confirmed) return;

    // expenses reference budget_line_id, so clear those first
    await supabase.from("expenses").delete().eq("budget_line_id", lineId);
    await supabase.from("budget_lines").delete().eq("id", lineId);
    await loadBudget();
  }

  // ---------- EXPENSES ----------

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

  function startEditExpense(exp: Expense) {
    setEditingExpenseId(exp.id);
    setEditExpenseAmount(String(exp.amount));
    setEditExpenseNote(exp.note || "");
    setEditExpenseError("");
  }

  function cancelEditExpense() {
    setEditingExpenseId(null);
    setEditExpenseError("");
  }

  async function saveEditExpense(expenseId: string) {
    setEditExpenseError("");
    const amount = Number(editExpenseAmount);
    if (!amount || amount <= 0) {
      setEditExpenseError("Enter an amount greater than 0.");
      return;
    }

    const { error } = await supabase
      .from("expenses")
      .update({ amount, note: editExpenseNote || null })
      .eq("id", expenseId);

    if (error) {
      setEditExpenseError(error.message);
      return;
    }

    setEditingExpenseId(null);
    await loadBudget();
  }

  async function deleteExpense(expenseId: string) {
    const confirmed = window.confirm("Delete this expense?");
    if (!confirmed) return;
    await supabase.from("expenses").delete().eq("id", expenseId);
    await loadBudget();
  }

  // ---------- ATTENDANCE ----------

  async function handleAddAttendance(e: React.FormEvent) {
    e.preventDefault();
    setAttendanceError("");

    if (!workerName.trim()) {
      setAttendanceError("Enter a worker name.");
      return;
    }

    setSavingAttendance(true);
    const { error } = await supabase.from("attendance").insert({
      project_id: id,
      worker_name: workerName.trim(),
      status: workerStatus,
      attendance_date: todayStr(),
    });
    setSavingAttendance(false);

    if (error) {
      setAttendanceError(error.message);
      return;
    }

    setWorkerName("");
    setWorkerStatus(ATTENDANCE_STATUSES[0]);
    await loadAttendance();
  }

  async function changeAttendanceStatus(attendanceId: string, newStatus: string) {
    await supabase.from("attendance").update({ status: newStatus }).eq("id", attendanceId);
    await loadAttendance();
  }

  async function deleteAttendance(attendanceId: string) {
    const confirmed = window.confirm("Remove this attendance entry?");
    if (!confirmed) return;
    await supabase.from("attendance").delete().eq("id", attendanceId);
    await loadAttendance();
  }

  // ---------- SITE UPDATES ----------

  async function handleAddSiteUpdate(e: React.FormEvent) {
    e.preventDefault();
    setUpdateError("");

    if (!updateNote.trim()) {
      setUpdateError("Write a short update before posting.");
      return;
    }

    setSavingUpdate(true);

    let photoUrl: string | null = null;

    if (updatePhoto) {
      const fileExt = updatePhoto.name.split(".").pop();
      const filePath = `${id}/${Date.now()}.${fileExt}`;

      const { error: uploadError } = await supabase.storage
        .from(PHOTO_BUCKET)
        .upload(filePath, updatePhoto);

      if (uploadError) {
        setSavingUpdate(false);
        setUpdateError(
          "Photo upload failed: " +
            uploadError.message +
            ". Make sure the 'site-photos' storage bucket exists and is set to Public in Supabase."
        );
        return;
      }

      const { data: urlData } = supabase.storage.from(PHOTO_BUCKET).getPublicUrl(filePath);
      photoUrl = urlData.publicUrl;
    }

    const { error } = await supabase.from("site_updates").insert({
      project_id: id,
      note: updateNote.trim(),
      update_date: todayStr(),
      photo_url: photoUrl,
    });
    setSavingUpdate(false);

    if (error) {
      setUpdateError(error.message);
      return;
    }

    setUpdateNote("");
    setUpdatePhoto(null);
    await loadSiteUpdates();
  }

  async function deleteSiteUpdate(updateId: string) {
    const confirmed = window.confirm("Delete this update?");
    if (!confirmed) return;
    await supabase.from("site_updates").delete().eq("id", updateId);
    await loadSiteUpdates();
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

  const presentCount = attendanceToday.filter((a) => a.status === "Present").length;
  const absentCount = attendanceToday.filter((a) => a.status === "Absent").length;
  const lateCount = attendanceToday.filter((a) => a.status === "Late").length;

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

      <h2>Today at a glance</h2>
      <div className="card" style={{ display: "flex", justifyContent: "space-between" }}>
        <div style={{ textAlign: "center" }}>
          <div style={{ fontSize: 20, fontWeight: 700, color: "#1A7A4A" }}>{presentCount}</div>
          <div style={{ fontSize: 12, color: "#666" }}>Present</div>
        </div>
        <div style={{ textAlign: "center" }}>
          <div style={{ fontSize: 20, fontWeight: 700, color: "#9B2226" }}>{absentCount}</div>
          <div style={{ fontSize: 12, color: "#666" }}>Absent</div>
        </div>
        <div style={{ textAlign: "center" }}>
          <div style={{ fontSize: 20, fontWeight: 700, color: "#E87722" }}>{lateCount}</div>
          <div style={{ fontSize: 12, color: "#666" }}>Late</div>
        </div>
        <div style={{ textAlign: "center" }}>
          <div style={{ fontSize: 20, fontWeight: 700, color: "#0D1F3C" }}>
            KES {totalSpent.toLocaleString()}
          </div>
          <div style={{ fontSize: 12, color: "#666" }}>Spent so far</div>
        </div>
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
        const isEditing = editingLineId === line.id;

        if (isEditing) {
          return (
            <div key={line.id} className="card">
              <label>Category</label>
              <select value={editCategory} onChange={(e) => setEditCategory(e.target.value)}>
                {CATEGORIES.map((c) => (
                  <option key={c} value={c}>{c}</option>
                ))}
              </select>
              <label>Description</label>
              <input value={editDescription} onChange={(e) => setEditDescription(e.target.value)} />
              <label>Budgeted amount (KES)</label>
              <input
                type="number"
                value={editBudgetedAmount}
                onChange={(e) => setEditBudgetedAmount(e.target.value)}
              />
              {editLineError && <p className="error">{editLineError}</p>}
              <div style={{ display: "flex", gap: 8, marginTop: 12 }}>
                <button style={{ marginTop: 0 }} onClick={() => saveEditLine(line.id)}>
                  Save
                </button>
                <button
                  type="button"
                  className="secondary"
                  style={{ marginTop: 0 }}
                  onClick={cancelEditLine}
                >
                  Cancel
                </button>
              </div>
            </div>
          );
        }

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
            <div style={{ display: "flex", gap: 8, marginTop: 10 }}>
              <button
                type="button"
                className="secondary"
                style={{ marginTop: 0, fontSize: 12, padding: "6px 10px" }}
                onClick={() => startEditLine(line)}
              >
                Edit
              </button>
              <button
                type="button"
                className="secondary"
                style={{ marginTop: 0, fontSize: 12, padding: "6px 10px", color: "#9B2226", borderColor: "#9B2226" }}
                onClick={() => deleteLine(line.id)}
              >
                Delete
              </button>
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
            const isEditing = editingExpenseId === exp.id;

            if (isEditing) {
              return (
                <div key={exp.id} className="card" style={{ padding: 12 }}>
                  <label>Amount (KES)</label>
                  <input
                    type="number"
                    value={editExpenseAmount}
                    onChange={(e) => setEditExpenseAmount(e.target.value)}
                  />
                  <label>Note</label>
                  <input value={editExpenseNote} onChange={(e) => setEditExpenseNote(e.target.value)} />
                  {editExpenseError && <p className="error">{editExpenseError}</p>}
                  <div style={{ display: "flex", gap: 8, marginTop: 12 }}>
                    <button style={{ marginTop: 0 }} onClick={() => saveEditExpense(exp.id)}>
                      Save
                    </button>
                    <button
                      type="button"
                      className="secondary"
                      style={{ marginTop: 0 }}
                      onClick={cancelEditExpense}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              );
            }

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
                <div style={{ display: "flex", gap: 8, marginTop: 8 }}>
                  <button
                    type="button"
                    className="secondary"
                    style={{ marginTop: 0, fontSize: 12, padding: "5px 9px" }}
                    onClick={() => startEditExpense(exp)}
                  >
                    Edit
                  </button>
                  <button
                    type="button"
                    className="secondary"
                    style={{ marginTop: 0, fontSize: 12, padding: "5px 9px", color: "#9B2226", borderColor: "#9B2226" }}
                    onClick={() => deleteExpense(exp.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            );
          })}
        </>
      )}

      <h2>Today's attendance — {todayStr()}</h2>
      <div className="card">
        <form onSubmit={handleAddAttendance}>
          <label>Worker name</label>
          <input value={workerName} onChange={(e) => setWorkerName(e.target.value)} placeholder="e.g. John Mwangi" />

          <label>Status</label>
          <select value={workerStatus} onChange={(e) => setWorkerStatus(e.target.value)}>
            {ATTENDANCE_STATUSES.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>

          {attendanceError && <p className="error">{attendanceError}</p>}

          <button type="submit" disabled={savingAttendance}>
            {savingAttendance ? "Saving..." : "Add worker"}
          </button>
        </form>
      </div>

      {attendanceToday.length === 0 ? (
        <p className="sub">No one marked yet today.</p>
      ) : (
        attendanceToday.map((a) => {
          const color =
            a.status === "Present" ? "#1A7A4A" : a.status === "Late" ? "#E87722" : "#9B2226";
          return (
            <div key={a.id} className="card" style={{ padding: 12 }}>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <span>{a.worker_name}</span>
                <select
                  value={a.status}
                  onChange={(e) => changeAttendanceStatus(a.id, e.target.value)}
                  style={{ width: "auto", padding: "4px 8px", fontSize: 13, color, fontWeight: 600 }}
                >
                  {ATTENDANCE_STATUSES.map((s) => (
                    <option key={s} value={s}>{s}</option>
                  ))}
                </select>
              </div>
              <button
                type="button"
                className="secondary"
                style={{ marginTop: 8, fontSize: 12, padding: "5px 9px", color: "#9B2226", borderColor: "#9B2226", width: "auto" }}
                onClick={() => deleteAttendance(a.id)}
              >
                Remove
              </button>
            </div>
          );
        })
      )}

      <h2>Daily site update</h2>
      <div className="card">
        <form onSubmit={handleAddSiteUpdate}>
          <label>What happened on site today?</label>
          <textarea
            value={updateNote}
            onChange={(e) => setUpdateNote(e.target.value)}
            rows={4}
            placeholder="e.g. Foundation pour completed on block A, delayed 2 hours due to rain."
            style={{
              width: "100%",
              padding: "10px 12px",
              border: "1px solid #ddd",
              borderRadius: 8,
              fontSize: 15,
              fontFamily: "inherit",
              resize: "vertical",
            }}
          />

          <label>Photo (optional)</label>
          <input
            type="file"
            accept="image/*"
            capture="environment"
            onChange={(e) => setUpdatePhoto(e.target.files?.[0] || null)}
          />

          {updateError && <p className="error">{updateError}</p>}

          <button type="submit" disabled={savingUpdate}>
            {savingUpdate ? "Posting..." : "Post update"}
          </button>
        </form>
      </div>

      {siteUpdates.length > 0 && (
        <>
          <h2>Recent updates</h2>
          {siteUpdates.map((u) => (
            <div key={u.id} className="card">
              <div style={{ fontSize: 12, color: "#666", marginBottom: 4 }}>{u.update_date}</div>
              <p style={{ margin: 0, fontSize: 14 }}>{u.note}</p>
              {u.photo_url && (
                <img
                  src={u.photo_url}
                  alt="Site photo"
                  style={{ width: "100%", borderRadius: 8, marginTop: 8, display: "block" }}
                />
              )}
              <button
                type="button"
                className="secondary"
                style={{ marginTop: 8, fontSize: 12, padding: "5px 9px", color: "#9B2226", borderColor: "#9B2226", width: "auto" }}
                onClick={() => deleteSiteUpdate(u.id)}
              >
                Delete
              </button>
            </div>
          ))}
        </>
      )}
    </div>
  );
}
