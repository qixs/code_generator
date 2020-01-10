
SELECT 
  COLUMN_NAME AS `name`,
  DATA_TYPE AS `type`, 
  CASE EXTRA WHEN 'auto_increment' THEN 'true' ELSE 'false' END AS `autoIncrement`,
  CASE IS_NULLABLE WHEN 'YES' THEN 'true' ELSE 'false' END AS `nullable`,
  CASE COLUMN_KEY WHEN 'PRI' THEN 'true' ELSE 'false' END AS `isPrimaryKey`,
  COLUMN_DEFAULT AS `defaultValue`,
  COLUMN_COMMENT AS `comment`
FROM
  `information_schema`.`COLUMNS` 
WHERE TABLE_SCHEMA = ?
  AND TABLE_NAME = ?