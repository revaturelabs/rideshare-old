#!/bin/sh
npm i
npm run build
mvn clean package
export RIDESHARE_KEYSTORE_NAME=dev-keystore.p12
export RIDESHARE_KEYSTORE_PASSWORD=vUP2p2zXvr7gEGFZTlYD
export RIDESHARE_DATABASE_URL=jdbc:oracle:thin:@localhost:1521:xe
export RIDESHARE_DATABASE_USERNAME=examples
export RIDESHARE_DATABASE_PASSWORD=p4ssw0rd
export RIDESHARE_SLACK_ID=184219023015.209820937091
export RIDESHARE_SLACK_SECRET=f69b998afcc9b1043adfa2ffdab49308
export RIDESHARE_SLACK_VERIFICATION=xER6r1Zrr0nxUBdSz7Fyq5UU
export RIDESHARE_SLACK_TEAM=T5E6F0P0F
export RIDESHARE_JWT_SECRET=RichieIsObsessedWithChickens!
java -jar target/rideshare.jar