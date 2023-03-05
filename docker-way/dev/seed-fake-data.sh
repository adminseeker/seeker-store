#!/usr/bin/env bash

###user-service###

curl -X POST -H "Content-Type: application/json" -d '{"name":"user1","email":"user1@gmail.com","password":"password","phone":"1234567890","role": "buyer"}' http://localhost:8081/api/v1/users
curl -X POST -H "Content-Type: application/json" -d '{"name":"user2","email":"user2@gmail.com","password":"password","phone":"1234567890","role": "seller"}' http://localhost:8081/api/v1/users
curl -X POST -H "Content-Type: application/json" -d '{"name":"user3","email":"user3@gmail.com","password":"password","phone":"1234567890","role": "seller"}' http://localhost:8081/api/v1/users
curl -X POST -H "Content-Type: application/json" -d '{"name":"user4","email":"user4@gmail.com","password":"password","phone":"1234567890","role": "buyer"}' http://localhost:8081/api/v1/users

curl -X POST -H "Content-Type: application/json" -d '{"nickname":"office","phone":"1234567890","country":"india","state":"karnataka","city":"bengaluru","street":"blah blah","postalCode":"560049"}' http://localhost:8081/api/v1/users/1/address
curl -X POST -H "Content-Type: application/json" -d '{"nickname":"home","phone":"1234567890","country":"india","state":"karnataka","city":"bengaluru","street":"blah","postalCode":"560024"}' http://localhost:8081/api/v1/users/1/address
curl -X POST -H "Content-Type: application/json" -d '{"nickname":"home","phone":"1234567890","country":"india","state":"karnataka","city":"bengaluru","street":"blah","postalCode":"560024"}' http://localhost:8081/api/v1/users/4/address

###product-service###

curl -X POST -H "Content-Type: application/json" -d '{"name":"ipad air","description":"brand new ipad air","skucode":"IPADAIR","price": 1299.99,"sellerId":2}' http://localhost:8082/api/v1/products
curl -X POST -H "Content-Type: application/json" -d '{"name":"iphone 13","description":"brand new iphone 13","skucode":"IPHN13","price": 899.99,"sellerId":2}' http://localhost:8082/api/v1/products
curl -X POST -H "Content-Type: application/json" -d '{"name":"iphone 14","description":"brand new iphone 14","skucode":"IPHN14","price": 999.99,"sellerId":2}' http://localhost:8082/api/v1/products
curl -X POST -H "Content-Type: application/json" -d '{"name":"Nike Air Max","description":"brand new Nike Air Max","skucode":"NIKEAMAX","price": 399.99,"sellerId":2}' http://localhost:8082/api/v1/products
curl -X POST -H "Content-Type: application/json" -d '{"name":"Nike Sneakers","description":"brand new Nike Sneakers","skucode":"NIKESNK","price": 299.99,"sellerId":2}' http://localhost:8082/api/v1/products

curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"NIKEAMAXR36","type":"shoe","size":"36","color":"RED","price":"399.99"}}' http://localhost:8082/api/v1/products/4/variant
curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"NIKESNKB36","type":"shoe","size":"36","color":"BLACK","price":"299.99"}}' http://localhost:8082/api/v1/products/5/variant
curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"IPHN13R128","type":"phone","size":"128GB","color":"RED","price":"899.99"}}' http://localhost:8082/api/v1/products/2/variant
curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"IPHN13B256","type":"phone","size":"256GB","color":"BLACK","price":"999.99"}}' http://localhost:8082/api/v1/products/2/variant
curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"IPHN14R128","type":"phone","size":"128GB","color":"RED","price":"999.99"}}' http://localhost:8082/api/v1/products/3/variant
curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"IPHN14B256","type":"phone","size":"256GB","color":"BLACK","price":"1199.99"}}' http://localhost:8082/api/v1/products/3/variant

###inventory-service###

curl -X POST -H "Content-Type: application/json" -d '{"skucode":"IPADAIR","quantity":30,"sellerId":2}' http://localhost:8083/api/v1/inventory
curl -X POST -H "Content-Type: application/json" -d '{"skucode":"IPHN13","quantity":30,"sellerId":2}' http://localhost:8083/api/v1/inventory
curl -X POST -H "Content-Type: application/json" -d '{"skucode":"IPHN14","quantity":30,"sellerId":2}' http://localhost:8083/api/v1/inventory
curl -X POST -H "Content-Type: application/json" -d '{"skucode":"NIKEAMAX","quantity":30,"sellerId":2}' http://localhost:8083/api/v1/inventory
curl -X POST -H "Content-Type: application/json" -d '{"skucode":"NIKESNK","quantity":30,"sellerId":2}' http://localhost:8083/api/v1/inventory


curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"IPHN13R128","quantity":15}}' http://localhost:8083/api/v1/inventory/2/variant
curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"IPHN13B256","quantity":15}}' http://localhost:8083/api/v1/inventory/2/variant
curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"IPHN14R128","quantity":15}}' http://localhost:8083/api/v1/inventory/3/variant
curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"IPHN14B256","quantity":15}}' http://localhost:8083/api/v1/inventory/3/variant
curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"NIKEAMAXR36","quantity":30}}' http://localhost:8083/api/v1/inventory/4/variant
curl -X POST -H "Content-Type: application/json" -d '{"userId":2,"variant":{"variantSkucode":"NIKESNKB36","quantity":30}}' http://localhost:8083/api/v1/inventory/5/variant

###cart-service###

curl -X POST -H "Content-Type: application/json" -d '{"userId":1,"item":{"productId":2,"variantId":3,"quantity":2}}' http://localhost:8084/api/v1/cart
curl -X POST -H "Content-Type: application/json" -d '{"userId":1,"item":{"productId":5,"variantId":2,"quantity":2}}' http://localhost:8084/api/v1/cart
curl -X POST -H "Content-Type: application/json" -d '{"userId":1,"item":{"productId":1,"quantity":1}}' http://localhost:8084/api/v1/cart

curl -X POST -H "Content-Type: application/json" -d '{"userId":4,"item":{"productId":3,"variantId":6,"quantity":2}}' http://localhost:8084/api/v1/cart
curl -X POST -H "Content-Type: application/json" -d '{"userId":4,"item":{"productId":4,"variantId":1,"quantity":1}}' http://localhost:8084/api/v1/cart

###order-service###

curl -X POST -H "Content-Type: application/json" -d '{"userId":1,"addressId":1}' http://localhost:8085/api/v1/orders
curl -X POST -H "Content-Type: application/json" -d '{"userId":4,"addressId":3}' http://localhost:8085/api/v1/orders