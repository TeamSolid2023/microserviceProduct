# team_solid
Repo to main the record of all the products avaiables in Mercadone. Clients are allowed to see the product list, search by name or category and obtain their details. It's able to provide the product stock.

## Endpoints
```
GET /products/getAll: retrieves a list of all products.
DELETE /products/{id}: deletes the product with the specified ID.
POST /products/newProduct: creates a new product based on the provided data.
GET /products/{id}: retrieves the product with the specified ID.
GET /products/name/{name}: retrieves the product with the specified name.
POST /products/JSON_load: updates products from a JSON file.
PUT /products/{id}: updates the product with the specified ID with the provided data.
```
