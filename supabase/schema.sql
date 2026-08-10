-- Struct-IQ MVP schema
-- Run this whole file once in Supabase: Dashboard -> SQL Editor -> New Query -> paste -> Run
-- Includes Week 1 tables (organizations, projects) AND Week 2-3 tables
-- (budget_lines, expenses, attendance, site_updates) so you only touch the
-- database once. The frontend for the later tables comes in Week 2-3.

-- 1. ORGANIZATIONS ---------------------------------------------------------
create table if not exists organizations (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid references auth.users not null,
  name text not null,
  county text,
  created_at timestamptz default now()
);

-- 2. PROJECTS ---------------------------------------------------------------
create table if not exists projects (
  id uuid primary key default gen_random_uuid(),
  org_id uuid references organizations not null,
  name text not null,
  client_name text,
  site_address text,
  start_date date,
  end_date date,
  status text default 'active',
  created_at timestamptz default now()
);

-- 3. BUDGET LINES (Week 2) ---------------------------------------------------
create table if not exists budget_lines (
  id uuid primary key default gen_random_uuid(),
  project_id uuid references projects not null,
  category text not null,        -- Labour / Materials / Equipment / Other
  description text,
  budgeted_amount numeric not null default 0,
  created_at timestamptz default now()
);

-- 4. EXPENSES (Week 2) --------------------------------------------------------
create table if not exists expenses (
  id uuid primary key default gen_random_uuid(),
  budget_line_id uuid references budget_lines not null,
  amount numeric not null,
  note text,
  expense_date date default current_date,
  receipt_photo_url text,
  created_at timestamptz default now()
);

-- 5. ATTENDANCE (Week 3) -------------------------------------------------------
create table if not exists attendance (
  id uuid primary key default gen_random_uuid(),
  project_id uuid references projects not null,
  worker_name text not null,
  status text not null,          -- present / absent / late
  attendance_date date default current_date,
  created_at timestamptz default now()
);

-- 6. SITE UPDATES (Week 3) -----------------------------------------------------
create table if not exists site_updates (
  id uuid primary key default gen_random_uuid(),
  project_id uuid references projects not null,
  note text,
  photo_url text,
  update_date date default current_date,
  created_at timestamptz default now()
);

-- ROW LEVEL SECURITY ---------------------------------------------------------
-- Locks every table down so a user can only ever see/edit data that belongs
-- to an organization they own. This is what makes it safe to expose the
-- Supabase anon key in the frontend.

alter table organizations enable row level security;
alter table projects enable row level security;
alter table budget_lines enable row level security;
alter table expenses enable row level security;
alter table attendance enable row level security;
alter table site_updates enable row level security;

-- organizations: owner can do everything with their own org row
create policy "org_owner_all" on organizations
  for all using (owner_id = auth.uid())
  with check (owner_id = auth.uid());

-- projects: allowed if the project's org is owned by the current user
create policy "projects_owner_all" on projects
  for all using (
    exists (select 1 from organizations o where o.id = org_id and o.owner_id = auth.uid())
  )
  with check (
    exists (select 1 from organizations o where o.id = org_id and o.owner_id = auth.uid())
  );

-- budget_lines: allowed if the parent project's org is owned by current user
create policy "budget_lines_owner_all" on budget_lines
  for all using (
    exists (
      select 1 from projects p
      join organizations o on o.id = p.org_id
      where p.id = project_id and o.owner_id = auth.uid()
    )
  )
  with check (
    exists (
      select 1 from projects p
      join organizations o on o.id = p.org_id
      where p.id = project_id and o.owner_id = auth.uid()
    )
  );

-- expenses: allowed if the parent budget_line -> project -> org is owned by current user
create policy "expenses_owner_all" on expenses
  for all using (
    exists (
      select 1 from budget_lines b
      join projects p on p.id = b.project_id
      join organizations o on o.id = p.org_id
      where b.id = budget_line_id and o.owner_id = auth.uid()
    )
  )
  with check (
    exists (
      select 1 from budget_lines b
      join projects p on p.id = b.project_id
      join organizations o on o.id = p.org_id
      where b.id = budget_line_id and o.owner_id = auth.uid()
    )
  );

-- attendance: allowed if parent project's org is owned by current user
create policy "attendance_owner_all" on attendance
  for all using (
    exists (
      select 1 from projects p
      join organizations o on o.id = p.org_id
      where p.id = project_id and o.owner_id = auth.uid()
    )
  )
  with check (
    exists (
      select 1 from projects p
      join organizations o on o.id = p.org_id
      where p.id = project_id and o.owner_id = auth.uid()
    )
  );

-- site_updates: allowed if parent project's org is owned by current user
create policy "site_updates_owner_all" on site_updates
  for all using (
    exists (
      select 1 from projects p
      join organizations o on o.id = p.org_id
      where p.id = project_id and o.owner_id = auth.uid()
    )
  )
  with check (
    exists (
      select 1 from projects p
      join organizations o on o.id = p.org_id
      where p.id = project_id and o.owner_id = auth.uid()
    )
  );
