SELECT 
  TABLE_NAME AS `name`,
  CASE
    TABLE_TYPE 
    WHEN 'VIEW' 
    THEN ''
    ELSE TABLE_COMMENT
  END AS `comment`,
  CASE
    TABLE_TYPE 
    WHEN 'VIEW' 
    THEN 'true' 
    ELSE 'false' 
  END AS `view`
FROM
  information_schema.TABLES 
WHERE TABLE_SCHEMA = ?
AND TABLE_NAME = ?