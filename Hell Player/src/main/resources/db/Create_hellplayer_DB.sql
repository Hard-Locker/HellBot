CREATE USER odmen WITH PASSWORD 'admin';

CREATE DATABASE hellplayer 
  WITH OWNER = odmen
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_US.UTF-8'
  LC_CTYPE = 'en_US.UTF-8'
  TEMPLATE = template0;
  
GRANT ALL PRIVILEGES ON DATABASE hellplayer TO odmen;
