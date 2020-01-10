SELECT
	a.attname AS NAME,
	pg_type.typname AS type,
	'false' AS autoIncrement,
CASE
	a.attnotnull 
	WHEN 'f' THEN
	'true' ELSE 'false' 
	END AS nullable,
CASE
	
	WHEN p.colname IS NOT NULL THEN
	'true' ELSE 'false' 
	END isPrimaryKey,
	null AS defaultValue,
	col_description ( a.attrelid, a.attnum ) AS COMMENT 
FROM
	pg_class AS c,
	pg_attribute AS a
	INNER JOIN pg_type ON pg_type.oid = a.atttypid
	LEFT JOIN (
	SELECT
		pg_constraint.conname AS pk_name,
		pg_attribute.attname AS colname,
		pg_type.typname AS typename 
	FROM
		pg_constraint
		INNER JOIN pg_class ON pg_constraint.conrelid = pg_class.oid
		INNER JOIN pg_attribute ON pg_attribute.attrelid = pg_class.oid 
		AND pg_attribute.attnum = pg_constraint.conkey [ 1 ]
		INNER JOIN pg_type ON pg_type.oid = pg_attribute.atttypid 
	WHERE
		pg_class.relname = ?
		AND pg_constraint.contype = 'p' 
	) p ON p.colname = a.attname 
WHERE
	c.relname = ?
	AND a.attrelid = c.oid 
AND a.attnum > 0