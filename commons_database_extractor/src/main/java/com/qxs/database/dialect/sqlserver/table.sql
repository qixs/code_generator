select sysobjects.name,cast(sys.extended_properties.value as varchar(1000)) comment, 
  CASE sysobjects.type WHEN 'V' THEN 'true' ELSE 'false' END AS [view] 
from sys.sysobjects 
left join sys.extended_properties on sysobjects.id = sys.extended_properties.major_id and minor_id = 0 
where type in ('U','V') and sysobjects.name != 'sysdiagrams' 
and sysobjects.name = ?