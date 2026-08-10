# Struct-IQ MVP — Week 1 Scaffold

This is a working Next.js + Supabase app covering **Screens 1–5** from the
launch plan: sign up, log in, create organization (happens automatically at
signup), create project, project list, project detail placeholder.

Follow these steps in order. Total time if nothing goes wrong: about an hour.

## 1. Create accounts (5 min)

- GitHub: https://github.com (if you don't have one)
- Vercel: https://vercel.com — sign up with your GitHub account
- Supabase: https://supabase.com — sign up with GitHub

## 2. Create your Supabase project (5 min)

1. In Supabase, click **New project**
2. Name it `structiq` (or anything), set a database password (save it
   somewhere), pick a region close to Kenya (Frankfurt or Cape Town if
   available)
3. Wait ~2 minutes for it to provision

## 3. Run the database schema (2 min)

1. In your Supabase project, open **SQL Editor** in the left sidebar
2. Click **New query**
3. Open `supabase/schema.sql` from this project, copy the whole file, paste
   it in, click **Run**
4. You should see "Success. No rows returned" — that means all 6 tables and
   the security policies were created

## 4. Turn off email confirmation for now (1 min)

To let pilot users sign up and start using the app immediately without
waiting for a confirmation email (you'll turn this back on later if you
want it):

1. Supabase project → **Authentication** → **Providers** → **Email**
2. Turn OFF "Confirm email"
3. Save

## 5. Get your API keys (1 min)

1. Supabase project → **Settings** → **API**
2. Copy the **Project URL** and the **anon / public** key — you'll need
   both in the next step

## 6. Set up the project locally (10 min)

You need [Node.js](https://nodejs.org) installed (v18 or later).

```bash
# unzip this project, then inside the folder:
npm install

# copy the env template and fill in your real keys
cp .env.local.example .env.local
```

Open `.env.local` and paste in your Supabase URL and anon key from step 5.

```bash
npm run dev
```

Open http://localhost:3000 — you should see the Struct-IQ landing page.
Try signing up, creating a project, and confirming it shows up on your
dashboard.

## 7. Push to GitHub (5 min)

```bash
git init
git add .
git commit -m "Struct-IQ MVP week 1 scaffold"
```

Create a new empty repo on GitHub, then follow the push instructions GitHub
shows you (`git remote add origin ...`, `git push -u origin main`).

## 8. Deploy to Vercel (5 min)

1. In Vercel, click **Add New → Project**
2. Import the GitHub repo you just pushed
3. Before deploying, expand **Environment Variables** and add the same two
   values from your `.env.local`:
   - `NEXT_PUBLIC_SUPABASE_URL`
   - `NEXT_PUBLIC_SUPABASE_ANON_KEY`
4. Click **Deploy**
5. In ~1 minute you'll get a live URL like `structiq.vercel.app` — this is
   what you send to your first pilot contacts

## What's already done vs. what's next

**Done (Week 1 scope):**
- Sign up / log in (Supabase Auth)
- Organization auto-created on signup
- Create project, project list, project detail page
- Row Level Security — every user only ever sees their own org's data,
  even though the database is shared

**Next (Week 2 — Budget Tracking):**
The `budget_lines` and `expenses` tables already exist in your database
(the schema created them up front). Week 2 is building the frontend
screens against them: add budget line, log expense, budget-vs-actual view.
These slot into `app/projects/[id]/page.tsx` where the placeholder card
currently says "Coming next."

**Week 3 — Daily Reporting:**
Same story — `attendance` and `site_updates` tables are already there
waiting for their screens.

## If something breaks

- **"Missing Supabase env vars" in the browser console** — you forgot to
  create `.env.local`, or forgot to restart `npm run dev` after adding it
  (env vars only load on server start)
- **Signup succeeds but organization isn't created** — almost always means
  "Confirm email" is still ON in Supabase Auth settings (step 4). Turn it
  off, or handle the confirm-then-login flow separately.
- **"row-level security policy" error when creating a project** — you're
  not logged in, or the `schema.sql` didn't run fully. Re-run it from the
  SQL Editor and check for red error text.
