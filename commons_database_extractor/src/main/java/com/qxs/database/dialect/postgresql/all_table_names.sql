select tablename as name from pg_tables where schemaname = 'public' 
union all 
select viewname as name from pg_views where schemaname = 'public' 