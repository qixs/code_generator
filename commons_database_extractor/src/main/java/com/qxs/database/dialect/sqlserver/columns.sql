select sys.columns.name,
       systypes.name AS type,
       CASE sys.columns.is_identity
         WHEN 1 THEN
          'true'
         ELSE
          'false'
       END AS autoIncrement,
       CASE sys.columns.is_nullable
         WHEN 1 THEN
          'true'
         ELSE
          'false'
       END AS nullable,
       
       CASE
       
         WHEN EXISTS
          (SELECT 1
                 FROM sysobjects
                WHERE xtype = 'PK'
                  AND name IN
                      (SELECT name
                         FROM sysindexes
                        WHERE indid IN
                              (SELECT indid
                                 FROM sysindexkeys
                                WHERE id = sys.columns.object_id
                                  AND colid = sys.columns.column_id))) THEN
          'true'
         ELSE
          'false'
       end
       
       AS isPrimaryKey,
       syscomments.text AS defaultValue,
       cast(sys.extended_properties.value as varchar(1000)) comment 
  from sys.sysobjects
 inner join sys.columns
    on sysobjects.id = sys.columns.object_id
 inner join sys.systypes
    on systypes.xusertype = columns.user_type_id
  left join syscomments
    on sys.columns.default_object_id = syscomments.id
  left join sys.extended_properties
    on sys.sysobjects.id = sys.extended_properties.major_id
   and minor_id = sys.columns.column_id
 where sysobjects.type in ('U', 'V')
   and sys.sysobjects.name != 'sysdiagrams'
   and sys.sysobjects.name = ?
