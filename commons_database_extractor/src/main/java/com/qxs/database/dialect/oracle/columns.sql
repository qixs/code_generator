SELECT USER_TAB_COLUMNS.COLUMN_NAME AS "name",
       DATA_TYPE AS "type",
       'false' AS "autoIncrement",
       CASE NULLABLE WHEN 'Y' THEN 'true' ELSE 'false' END AS "nullable",
       case when cons.COLUMN_NAME is not null then 'true' else 'false' end "isPrimaryKey",
       DATA_DEFAULT AS "defaultValue",
       USER_COL_COMMENTS.COMMENTS AS "comment"
  FROM USER_TAB_COLUMNS
  LEFT JOIN USER_COL_COMMENTS ON USER_TAB_COLUMNS.TABLE_NAME = USER_COL_COMMENTS.TABLE_NAME AND USER_TAB_COLUMNS.COLUMN_NAME = USER_COL_COMMENTS.COLUMN_NAME
	left join (
		select user_cons_columns.* from user_cons_columns 
		left join user_constraints on user_constraints.constraint_type ='P' 
					and user_constraints.constraint_name = user_cons_columns.constraint_name
		where user_cons_columns.position is not null
  ) cons on cons.COLUMN_NAME = USER_TAB_COLUMNS.COLUMN_NAME and cons.TABLE_NAME = USER_TAB_COLUMNS.TABLE_NAME
 WHERE USER_TAB_COLUMNS.TABLE_NAME = ?
 order by USER_TAB_COLUMNS.column_id