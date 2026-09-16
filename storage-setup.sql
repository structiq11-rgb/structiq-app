-- Run this in Supabase SQL Editor AFTER creating the storage bucket
-- (bucket creation itself is a UI step, done separately — see instructions)

-- Allow any logged-in user to upload files into the "site-photos" bucket
create policy "Authenticated users can upload site photos"
on storage.objects for insert
to authenticated
with check (bucket_id = 'site-photos');

-- Allow anyone to view/download files in the "site-photos" bucket
-- (this is what lets the photo display in the app without extra auth)
create policy "Public can view site photos"
on storage.objects for select
using (bucket_id = 'site-photos');
