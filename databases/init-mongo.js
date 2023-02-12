db = db.getSiblingDB('user_service_db');
db.createUser(
  {
    user: 'user',
    pwd: 'password',
    roles: [{ role: 'readWrite', db: 'user_service_db' }],
  },
);


db = db.getSiblingDB('product_service_db');
db.createUser(
  {
    user: 'user',
    pwd: 'password',
    roles: [{ role: 'readWrite', db: 'product_service_db' }],
  },
);


db = db.getSiblingDB('inventory_service_db');
db.createUser(
  {
    user: 'user',
    pwd: 'password',
    roles: [{ role: 'readWrite', db: 'inventory_service_db' }],
  },
);