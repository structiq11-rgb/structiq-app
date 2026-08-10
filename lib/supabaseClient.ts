import { createClient } from "@supabase/supabase-js";

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL as string;
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY as string;

if (!supabaseUrl || !supabaseAnonKey) {
  // This will show up loudly in the browser console if .env.local isn't set
  console.warn(
    "Missing Supabase env vars. Copy .env.local.example to .env.local and fill in your project keys."
  );
}

export const supabase = createClient(supabaseUrl, supabaseAnonKey);
