grammar DDB;

databasecreation: 'create database ' database_name;

insertquery: 'insert into table ' table_name '('  column_name (',' column_name)* ')';

database_name: MYSTR;
table_name: MYSTR;
column_name: MYSTR;

//mystr:MYSTR;

MYSTR:[a-z]+;

NUMBER: ('0' .. '9') + ('.' ('0' .. '9') +)?
   ;

WS : (' ' | '\t')+ -> channel(HIDDEN);