SELECT 
  name,
  CASE
    type 
    WHEN 'view' 
    THEN ''
    ELSE ''
  END AS comment,
  CASE
    type 
    WHEN 'view' 
    THEN 'true' 
    ELSE 'false' 
  END AS view 
FROM 
  (select tablename as name,'table' as type from pg_tables where schemaname = 'public' 
union all 
select viewname as name,'view' as type from pg_views where schemaname = 'public') a 
WHERE name = ? 