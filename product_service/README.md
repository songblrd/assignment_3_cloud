# READ ME FOR INVENTORY MANAGMENT APP A2

Profiles:

application-dev.yaml is for development profile
application-prod.yaml is for prod profile

using a local database in dev, using a cloud database in prod

# Running with dev profile

make sure that postgre sql is running in the background
connect to local server of postgresql, and make an inventory_db database
dev profile wants port 5433 and username and password of postgre and password
start the app with .\mvnw.cmd spring-boot:run

# running with prod profile

make sure docker is running
then just do docker compose up --build

# endpoints
Get 
Get {id}
post
put
delete
get /search?=name
get /search?=category
get /info

# example request
{
  "name": "Strawberry Jam",
  "category": "Pantry",
  "price": 4.99,
  "stockQuantity": 20
}

# if restarted the data persists.