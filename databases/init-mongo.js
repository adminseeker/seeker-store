db = db.getSiblingDB('cart_service_db');
db.createUser(
  {
    user: 'user',
    pwd: 'password',
    roles: [{ role: 'readWrite', db: 'cart_service_db' }],
  },
);


db = db.getSiblingDB('order_service_db');
db.createUser(
  {
    user: 'user',
    pwd: 'password',
    roles: [{ role: 'readWrite', db: 'order_service_db' }],
  },
);

